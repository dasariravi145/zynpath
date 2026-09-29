# ==============================================================================
# ZYNPATH: NUMBER PATH PUZZLE - PROGUARD & R8 CONFIGURATION
# ==============================================================================

# ------------------------------------------------------------------------------
# 1. General Attributes & De-obfuscation / Crash Diagnostics
# ------------------------------------------------------------------------------
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,Exceptions
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Preserve @Keep annotations across the app
-keep @androidx.annotation.Keep class * { *; }
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}

# ------------------------------------------------------------------------------
# 2. Kotlin & Coroutines Metadata
# ------------------------------------------------------------------------------
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-dontwarn kotlinx.coroutines.**

# ------------------------------------------------------------------------------
# 3. Room Persistence
# ------------------------------------------------------------------------------
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public abstract <methods>;
}
-dontwarn androidx.room.paging.**

# ------------------------------------------------------------------------------
# 4. DataStore Preferences
# ------------------------------------------------------------------------------
-keep class androidx.datastore.** { *; }
-keepclassmembers class * extends androidx.datastore.core.Serializer {
    *** getDefaultValue();
    *** readFrom(java.io.InputStream, kotlin.coroutines.Continuation);
    *** writeTo(java.lang.Object, java.io.OutputStream, kotlin.coroutines.Continuation);
}

# ------------------------------------------------------------------------------
# 5. Dependency Injection (Dagger / Hilt)
# ------------------------------------------------------------------------------
-keep class * extends dagger.hilt.internal.GeneratedComponentManager
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager
-keepclasseswithmembernames class * {
    @javax.inject.Inject <init>(...);
}

# ------------------------------------------------------------------------------
# 6. Networking & WebSocket (OkHttp / Retrofit)
# ------------------------------------------------------------------------------
-keepattributes EnclosingMethod
-keepclassmembers class * extends okhttp3.WebSocketListener {
    public void onOpen(okhttp3.WebSocket, okhttp3.Response);
    public void onMessage(okhttp3.WebSocket, java.lang.String);
    public void onMessage(okhttp3.WebSocket, okio.ByteString);
    public void onClosing(okhttp3.WebSocket, int, java.lang.String);
    public void onClosed(okhttp3.WebSocket, int, java.lang.String);
    public void onFailure(okhttp3.WebSocket, java.lang.Throwable, okhttp3.Response);
}
-dontwarn okhttp3.**
-dontwarn okio.**

# ------------------------------------------------------------------------------
# 7. Google Play Billing
# ------------------------------------------------------------------------------
-keep class com.android.billingclient.** { *; }
-keep interface com.android.billingclient.** { *; }

# ------------------------------------------------------------------------------
# 8. Google Mobile Ads (AdMob)
# ------------------------------------------------------------------------------
-keep public class com.google.android.gms.ads.** {
    public *;
}
-keep public class com.google.ads.** {
    public *;
}
-dontwarn com.google.android.gms.ads.**

# ------------------------------------------------------------------------------
# 9. Jetpack Compose & Material 3
# ------------------------------------------------------------------------------
-dontwarn androidx.compose.**
-keep class androidx.compose.runtime.** { *; }

# ------------------------------------------------------------------------------
# 10. Zynpath Core Domain & Serialization DTOs
# ------------------------------------------------------------------------------
# Keep pure Kotlin domain models that might be serialized/deserialized
-keep class com.zynpath.game.core.puzzle.model.** { *; }
-keep class com.zynpath.game.core.network.model.** { *; }
-keep class com.zynpath.game.feature.**.model.** { *; }
