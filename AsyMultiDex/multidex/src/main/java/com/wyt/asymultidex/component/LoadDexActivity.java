package com.wyt.asymultidex.component;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import com.wyt.asymultidex.R;
import com.wyt.asymultidex.interf.DexCallback;
import com.wyt.asymultidex.utils.DexInstallUtils;


/**
 * Created by wangyt on 2017/4/24.
 * : 加载dex页面
 */

public class LoadDexActivity extends AppCompatActivity implements DexCallback {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_load_dex);
        new Thread(new DexInstall()).start();
    }

    @Override
    public void onBackPressed() {
        Log.d("wyt", "onBackPressed");
    }

    @Override
    public void onDexStart() {
        Log.d("wyt", "onDexStart");
    }

    @Override
    public void onDexComplete() {
        Log.d("wyt", "onDexComplete");
        DexInstallUtils.markInstallFinish(getApplicationContext());
        finish();
        System.exit(0);
    }

    private class DexInstall implements Runnable {

        @Override
        public void run() {
            LoadDexActivity.this.onDexStart();
            MultiDex.install(getApplicationContext());
            LoadDexActivity.this.onDexComplete();
        }
    }
}
