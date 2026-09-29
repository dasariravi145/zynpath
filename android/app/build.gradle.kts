import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinCompose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.zynpath.game"
    compileSdk = 36

    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val keystoreProperties = Properties()
    if (keystorePropertiesFile.exists()) {
        FileInputStream(keystorePropertiesFile).use { keystoreProperties.load(it) }
    }

    val localPropertiesFile = rootProject.file("local.properties")
    val localProperties = Properties()
    if (localPropertiesFile.exists()) {
        FileInputStream(localPropertiesFile).use { localProperties.load(it) }
    }

    fun getSigningProperty(key: String, envKey: String): String? {
        return keystoreProperties.getProperty(key) ?: System.getenv(envKey)
    }

    val uploadStoreFilePath = getSigningProperty("storeFile", "ZYNPATH_UPLOAD_KEYSTORE_PATH")
    val uploadStorePassword = getSigningProperty("storePassword", "ZYNPATH_UPLOAD_KEYSTORE_PASSWORD")
    val uploadKeyAlias = getSigningProperty("keyAlias", "ZYNPATH_UPLOAD_KEY_ALIAS")
    val uploadKeyPassword = getSigningProperty("keyPassword", "ZYNPATH_UPLOAD_KEY_PASSWORD")

    val hasReleaseSigning = !uploadStoreFilePath.isNullOrBlank() &&
            !uploadStorePassword.isNullOrBlank() &&
            !uploadKeyAlias.isNullOrBlank() &&
            !uploadKeyPassword.isNullOrBlank() &&
            file(uploadStoreFilePath).exists()

    val releaseVersionCode = project.findProperty("versionCode")?.toString()?.toIntOrNull()
        ?: System.getenv("ZYNPATH_VERSION_CODE")?.toIntOrNull()
        ?: 1
    val releaseVersionName = project.findProperty("versionName")?.toString()
        ?: System.getenv("ZYNPATH_VERSION_NAME")
        ?: "1.0.0"

    val googleWebClientId = project.findProperty("googleWebClientId")?.toString()
        ?: localProperties.getProperty("googleWebClientId")
        ?: localProperties.getProperty("default_web_client_id")
        ?: System.getenv("GOOGLE_WEB_CLIENT_ID")
        ?: System.getenv("GOOGLE_CLIENT_ID")
        ?: ""

    val debugBackendHost = project.findProperty("zynpath.backend.host")?.toString()
        ?: localProperties.getProperty("zynpath.backend.host")
        ?: System.getenv("ZYNPATH_BACKEND_HOST")
        ?: "192.168.31.164"
    val debugBackendPort = project.findProperty("zynpath.backend.port")?.toString()
        ?: localProperties.getProperty("zynpath.backend.port")
        ?: System.getenv("ZYNPATH_BACKEND_PORT")
        ?: "8080"
    val debugBackendBaseUrl = project.findProperty("zynpath.backend.baseUrl")?.toString()
        ?: localProperties.getProperty("zynpath.backend.baseUrl")
        ?: System.getenv("ZYNPATH_BACKEND_BASE_URL")
        ?: "http://$debugBackendHost:$debugBackendPort/api/v1"
    val debugBackendWsUrl = project.findProperty("zynpath.backend.wsUrl")?.toString()
        ?: localProperties.getProperty("zynpath.backend.wsUrl")
        ?: System.getenv("ZYNPATH_BACKEND_WS_URL")
        ?: "ws://$debugBackendHost:$debugBackendPort/ws/multiplayer"

    defaultConfig {
        applicationId = "com.zynpath.game"
        minSdk = 24
        targetSdk = 36
        versionCode = releaseVersionCode
        versionName = releaseVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        if (googleWebClientId.isNotBlank()) {
            resValue("string", "default_web_client_id", googleWebClientId)
        }
    }



    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(uploadStoreFilePath!!)
                storePassword = uploadStorePassword
                keyAlias = uploadKeyAlias
                keyPassword = uploadKeyPassword
                enableV1Signing = true
                enableV2Signing = true
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("String", "BACKEND_BASE_URL", "\"$debugBackendBaseUrl\"")
            buildConfigField("String", "BACKEND_WS_URL", "\"$debugBackendWsUrl\"")
            buildConfigField("String", "ADMOB_APP_ID", "\"ca-app-pub-3940256099942544~3347511713\"")
            buildConfigField("String", "ADMOB_REWARDED_AD_UNIT_ID", "\"ca-app-pub-3940256099942544/5224354917\"")
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                signingConfig = null
            }
            buildConfigField("String", "BACKEND_BASE_URL", "\"https://api.zynpath.app/api/v1\"")
            buildConfigField("String", "BACKEND_WS_URL", "\"wss://api.zynpath.app/ws/multiplayer\"")
            buildConfigField("String", "ADMOB_APP_ID", "\"ca-app-pub-3940256099942544~3347511713\"")
            buildConfigField("String", "ADMOB_REWARDED_AD_UNIT_ID", "\"\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // DataStore Preferences
    implementation(libs.androidx.datastore.preferences)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Debugging & Tooling
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Networking
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Google Play Billing
    implementation(libs.play.billing)

    // Google Mobile Ads
    implementation(libs.play.services.ads)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Android Credential Manager & Google ID Token
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.okhttp.mockwebserver)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}

