package com.wyt.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.wyt.asymultidex.AsyMultiDex;

/**
 * Created by wangyt on 2017/5/2.
 * : asy multi dex Application
 */

public class App extends Application {

    /**
     * Set the base context for this ContextWrapper.  All calls will then be
     * delegated to the base context.  Throws
     * IllegalStateException if a base context has already been set.
     *
     * @param base The new base context for this wrapper.
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        AsyMultiDex.install(this);
    }

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
        if (AsyMultiDex.isDexInstallProcess(this)) {
            return;
        }
        Log.e("wyt", "not in dex install process");
        //// TODO: 2017/5/2
    }
}
