# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# Room Database entities, DAOs, and Database
-keep class com.example.data.local.entity.** { *; }
-keep interface com.example.data.local.dao.** { *; }
-keep class * extends androidx.room.RoomDatabase

# Domain models
-keep class com.example.domain.model.** { *; }

# Glance App Widget
-keep class * extends androidx.glance.appwidget.GlanceAppWidgetReceiver
-keep class * extends androidx.glance.appwidget.GlanceAppWidget
-keep class * implements androidx.glance.appwidget.action.ActionCallback

