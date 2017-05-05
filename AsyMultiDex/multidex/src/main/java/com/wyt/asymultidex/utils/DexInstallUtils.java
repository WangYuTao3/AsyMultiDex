package com.wyt.asymultidex.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Process;

import com.wyt.asymultidex.component.LoadDexActivity;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_MULTI_PROCESS;

/**
 * Created by wangyt on 2017/4/24.
 * : 工具类，大多数方法拷贝自谷歌multiDex
 */

public class DexInstallUtils {

    private static final String KEY_DEX2_SHA1 = "dex2-SHA1-Digest";

    /**
     * 是否是安装合并dex的进程；
     *
     * @param context
     * @return
     */
    public static boolean isDexInstallProcess(Context context) {
        return isDexInstallProcess(context, LoadDexActivity.class);
    }

    private static boolean isDexInstallProcess(Context context, Class<? extends Activity> activityClass) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

        String mainProcess = packageInfo.applicationInfo.processName;
        ComponentName component = new ComponentName(context, activityClass);
        ActivityInfo activityInfo;
        try {
            activityInfo = packageManager.getActivityInfo(component, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

        if (activityInfo.processName.equals(mainProcess)) {
            return false;
        } else {
            int myPid = Process.myPid();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.RunningAppProcessInfo myProcess = null;
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();
            if (runningProcesses != null) {
                for (ActivityManager.RunningAppProcessInfo process : runningProcesses) {
                    if (process.pid == myPid) {
                        myProcess = process;
                        break;
                    }
                }
            }
            return myProcess != null && myProcess.processName.equals(activityInfo.processName);
        }
    }

    /**
     * vm是否可以自己合并dex
     *
     * @return
     */
    public static boolean isVMMultiDexCapable() {
        boolean isMultiDexCapable = false;
        String versionString = System.getProperty("java.vm.version");
        if (versionString != null) {
            Matcher matcher = Pattern.compile("(\\d+)\\.(\\d+)(\\.\\d+)?").matcher(versionString);
            if (matcher.matches()) {
                try {
                    int e = Integer.parseInt(matcher.group(1));
                    int minor = Integer.parseInt(matcher.group(2));
                    isMultiDexCapable = e > 2 || e == 2 && minor >= 1;
                } catch (NumberFormatException ignore) {
                }
            }
        }
        return isMultiDexCapable;
    }

    /**
     * MultiDex是否安装完成
     *
     * @param context
     * @return
     */
    public static boolean isMultiDexInstalled(Context context) {
        String flag = get2thDexSHA1(context);
        SharedPreferences sp = context.getSharedPreferences(getPreferencesName(context), MODE_MULTI_PROCESS);
        String saveValue = sp.getString(KEY_DEX2_SHA1, "");
        return flag.equals(saveValue);
    }

    /**
     * block线程,安装dex
     *
     * @param context
     * @return
     */
    public static void waitForDexInstall(Context context) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(context.getPackageName(), LoadDexActivity.class.getName());
        intent.setComponent(componentName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        //最多等待20s
        long waitTime = TimeUnit.SECONDS.toMillis(20);
        long startWait = System.currentTimeMillis();
        //这里阻塞线程
        while (!isMultiDexInstalled(context)) {
            try {
                long nowWait = System.currentTimeMillis() - startWait;
                if (nowWait >= waitTime) {
                    break;
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 标记完成标记文件
     *
     * @param context
     */
    public static void markInstallFinish(Context context) {
        SharedPreferences sp = context.getSharedPreferences(getPreferencesName(context), MODE_MULTI_PROCESS);
        sp.edit().putString(KEY_DEX2_SHA1, get2thDexSHA1(context)).apply();
    }

    private static String get2thDexSHA1(Context context) {
        ApplicationInfo info = context.getApplicationInfo();
        String source = info.sourceDir;
        try {
            JarFile jar = new JarFile(source);
            Manifest mf = jar.getManifest();
            Map<String, Attributes> map = mf.getEntries();
            Attributes a = map.get("classes2.dex");
            return a == null ? "" : a.getValue("SHA1-Digest");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getPreferencesName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        return context.getPackageName() + "." + packageInfo.versionName;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            return pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new PackageInfo();
    }
}
