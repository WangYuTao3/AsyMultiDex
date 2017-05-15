#keep sample

#keep class which need to be added in mainDex
-keep class android.support.multidex.** {*;}
-keep class com.wyt.asymultidex.** {*;}
-keep class com.squareup.leakcanary.** { *; }

#multidex
-keep class android.support.multidex.** {*;}
-keep class cn.wyt.asymultidex.** {*;}
-keep class * implements com.wyt.asymultidex.interf.IMainDex{*;}