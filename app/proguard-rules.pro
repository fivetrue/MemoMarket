# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/kwonojin/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn java.lang.invoke.*
-dontwarn org.jsoup.*

# RETROFIT 2
-dontwarn retrofit2.**
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
-dontwarn okhttp3.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Lambda
-dontwarn **$$Lambda$*

# For using GSON @Expose annotation
-keepattributes *Annotation*
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# Poi
-keep public class org.apache.poi.** {*;}

-dontwarn org.apache.**
-dontwarn com.opencsv.**
-dontwarn okio.**
-dontwarn android.webkit.**
-dontwarn java.nio.**
-dontwarn com.google.firebase.**

-keep class com.fivetrue.market.memo.model.** { *; }
-keep class android.support.** { *; }
-keep class android.webkit.** { *; }
-keep class org.jsoup.** { *; }