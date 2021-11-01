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

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-optimizationpasses 5

# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses

# 这句话能够使我们的项目混淆后产生映射文件
# 包含有类名->混淆后类名的映射关系
-verbose

# 指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify

# 保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses

# 避免混淆泛型
-keepattributes Signature

# 抛出异常时保留代码行号
#-keepattributes SourceFile,LineNumberTable

# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/*,!class/merging/*

# 保留sdk系统自带的一些内容 【例如：-keepattributes *Annotation* 会保留Activity的被@override注释的onCreate、onDestroy方法等】
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

#############################################
#
# Android开发中一些需要保留的公共部分
#
#############################################

# 保留我们使用的四大组件，自定义的Application等等这些类不被混淆
# 因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
#TemplateFragment
-keep public class com.huawei.hms.videoeditor.ui.template.TemplateFragment

# 保留support下的所有类及其内部类
-keep class android.support.** {*;}

# androidx
-keep class androidx.** {*;}
-keep class * extends androidx.recyclerview.widget.RecyclerView$ViewHolder{*;}

# hvi
-keep class com.huawei.videoeditor.** {*;}
-keep class com.huawei.hvi.** {*;}
-keep class com.huawei.hvi.ability.util.** {*;}

# videoedit
-keep class com.huawei.hms.databases.** {*;}

-keep class * implements android.os.Parcelable {
     public static final android.os.Parcelable$Creator *;
 }

# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

# 保留R下面的资源
-keep class **.R$* {*;}

# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留在Activity中的方法参数是view的方法，
# 这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

-ignorewarnings
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
#-keepattributes SourceFile,LineNumberTable
-keep class com.huawei.updatesdk.**{*;}

-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.* { *; }

-keep class com.huawei.agconnect.**{*;}
-dontwarn com.huawei.agconnect.**
-keep class com.hianalytics.android.**{*;}
-keep interface com.huawei.hms.analytics.type.HAEventType{*;}
-keep interface com.huawei.hms.analytics.type.HAParamType{*;}
-keepattributes Exceptions, Signature, InnerClasses, LineNumberTable

-keep,allowobfuscation @interface com.huawei.hms.videoeditor.ui.common.utils.KeepOriginal
-keep @com.huawei.hms.videoeditor.codelab.ui.common.utils.KeepOriginal class * {*;}
-keepclasseswithmembers class * {
    @com.huawei.hms.videoeditor.codelab.ui.common.utils.KeepOriginal <methods>;
}
-keepclasseswithmembers class * {
    @com.huawei.hms.videoeditor.codelab.ui.common.utils.KeepOriginal <fields>;
}
-keepclasseswithmembers class * {
    @com.huawei.hms.videoeditor.codelab.ui.common.utils.KeepOriginal <init>(...);
}

-repackageclasses "com.huawei.hms.videoeditor.ui.p"

#保留SDK内使用到的luaj
-keep class org.luaj.**{*;}