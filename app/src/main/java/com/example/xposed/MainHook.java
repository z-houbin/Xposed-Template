package com.example.xposed;

import android.app.Activity;

import com.example.xposed.library.Hook;
import com.example.xposed.library.log.Log2;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook extends Hook {
    {
        targetPackage.add("com.tencent.mm");
    }

    @Override
    protected void onLoadPackage(XC_LoadPackage.LoadPackageParam packageParam) {
        super.onLoadPackage(packageParam);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        super.onActivityResumed(activity);
        Log2.d("onActivityResumed@", activity.getLocalClassName());
    }
}
