# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in defaultProguardFile('proguard-android-optimize.txt').

# Keep Kotlin reflect and coroutines metadata
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep Room database schemas and entities
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep DataStore
-keep class androidx.datastore.** { *; }

# Keep Hilt generated classes
-keep class * extends dagger.hilt.internal.GeneratedComponentManager
-keep class com.zynpath.game.** { *; }
