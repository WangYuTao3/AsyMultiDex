package com.wyt.asymultidex;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.wyt.asymultidex.utils.DexInstallUtils;


/**
 * Created by wangyt on 2017/4/24.
 * : 异步multiDex
 */

public class AsyMultiDex {

    /**
     * 安装dex
     *
     * @param context
     */
    public static void install(Context context) {
        if (AsyMultiDex.isDexInstallProcess(context)) {
            return;
        }
        // vm是否支持multiDex，加载进程中不再加载
        if (!DexInstallUtils.isVMMultiDexCapable()) {
            if (!DexInstallUtils.isMultiDexInstalled(context)) {
                DexInstallUtils.waitForDexInstall(context);
            }
        }
        //再进行一次multiDex(如果进行过dex合并，此步骤会非常快)
        MultiDex.install(context);
    }

    /**
     * 判断是否是安装dex进程
     *
     * @param context
     * @return
     */
    public static boolean isDexInstallProcess(Context context) {
        return DexInstallUtils.isDexInstallProcess(context);
    }
}
