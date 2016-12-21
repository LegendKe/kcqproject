# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\AndroidSdk\Sdk/tools/proguard/proguard-android.txt
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

-optimizationpasses 5          # 指定代码的压缩级别
-dontusemixedcaseclassnames   # 是否使用大小写混合
-dontpreverify           # 混淆时是否做预校验
-verbose                # 混淆时是否记录日志

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法

-keep public class * extends android.app.Activity      # 保持哪些类不被混淆
-keep public class * extends android.app.Application   # 保持哪些类不被混淆
-keep public class * extends android.app.Service       # 保持哪些类不被混淆
-keep public class * extends android.content.BroadcastReceiver  # 保持哪些类不被混淆
-keep public class * extends android.content.ContentProvider    # 保持哪些类不被混淆
-keep public class * extends android.app.backup.BackupAgentHelper # 保持哪些类不被混淆
-keep public class * extends android.preference.Preference        # 保持哪些类不被混淆
-keep public class com.android.vending.licensing.ILicensingService    # 保持哪些类不被混淆

-keepattributes Signature

-keepclassmembers class **.R$* { #不混淆资源类
    public static <fields>;
}

-keepclasseswithmembernames class * {  # 保持 native 方法不被混淆
    native <methods>;
}
-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
    public void *(android.view.View);
}
-keepclassmembers enum * {     # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
    public static final android.os.Parcelable$Creator *;
}

-keep class butterknife.** { *;}
-dontwarn butterknife.**
-keep class **$$ViewBinder { *;}


-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keep class com.huawei.** {*;}
-dontwarn com.huawei.**

-keep class io.rong.** {*;}
-dontwarn io.rong.**
-keep class io.rong.** {*;}
-keep class com.google.gson.** {*;}
-dontwarn com.google.gson.**
-keep class com.google.** {*;}
-dontwarn com.google.**
-keep class com.handmark.** {*;}
-dontwarn com.handmark.**
-keep class com.alipay.** {*;}
-dontwarn com.alipay.**
-keep class com.alipayzhima.** {*;}
-dontwarn com.alipayzhima.**
-keep class com.squareup.** {*;}
-dontwarn com.squareup.**
-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
-keep class okio.Deflater.** {*;}
-dontwarn okio.Deflater.**
-keep class okio.DeflaterSink.** {*;}
-dontwarn okio.DeflaterSink.**
-keep class okio.Okio.** {*;}
-dontwarn okio.Okio.**
-keep class org.codehaus.** {*;}
-dontwarn org.codehaus.**
-keep class okio.** {*;}
-dontwarn okio.**
-keep class android.net.http.** {*;}
-dontwarn android.net.http.**

-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**

-keep class org.apache.** {*;}
-dontwarn org.apache.**

-keep class com.djx.pin.ui.NavigationDrawerFragment {*;}
-dontwarn com.djx.pin.ui.NavigationDrawerFragment

-keep class com.djx.pin.** {*;}
-dontwarn com.djx.pin.**


-keep public class * extends android.support.**
-keep class * implements java.io.Serializable
-keepnames class * implements java.io.Serializable

-keep class com.loopj.** {*;}
-dontwarn com.loopj.**
-keep class pub.devrel.** {*;}
-dontwarn pub.devrel.**
-keep class uk.co.** {*;}
-dontwarn uk.co.**

-keep class com.googlecode.** {*;}
-dontwarn com.googlecode.**
-keep class cn.jpush.** {*;}
-dontwarn cn.jpush.**

-keep class de.greenrobot.event.** {*;}
-keepclassmembers class ** {
    public void onEvent*(**);
    void onEvent*(**);
}



-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keepattributes EnclosingMethod

-keep class com.djx.pin.widget.*
-keep class com.djx.pin.myview.*
-keep class com.djx.pin.slidmenu.*
-dontwarn com.pgyersdk.**
-keep class com.pgyersdk.** { *; }

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class com.android.application.R$*{
public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
