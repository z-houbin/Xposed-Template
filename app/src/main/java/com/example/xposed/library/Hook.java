package com.example.xposed.library;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.example.xposed.BuildConfig;
import com.example.xposed.library.log.Log2;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public abstract class Hook implements IXposedHookLoadPackage, Application.ActivityLifecycleCallbacks {
    public Activity focusActivity;
    public Application applicationContext;

    protected XC_LoadPackage.LoadPackageParam packageParam;
    protected List<String> targetPackage = new ArrayList<>();
    protected static String DEFAULT_TAG = "com.example.xposed";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        this.packageParam = loadPackageParam;

        Log2.init(DEFAULT_TAG, BuildConfig.DEBUG);

        if (targetPackage.isEmpty()) {
            Log2.d("targetPackage.empty " + loadPackageParam.packageName);
        } else if (targetPackage.contains(loadPackageParam.packageName)) {
            this.onLoadPackage(packageParam);
        }
    }

    protected void onLoadPackage(XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                applicationContext = (Application) param.thisObject;
                applicationContext.registerActivityLifecycleCallbacks(Hook.this);
            }
        });
    }

    public ClassLoader getClassLoader() {
        if (this.packageParam != null) {
            return this.packageParam.classLoader;
        }

        if (focusActivity != null) {
            return focusActivity.getClassLoader();
        }

        return null;
    }

    public Class<?> load(String name) {
        ClassLoader classLoader = getClassLoader();
        if (classLoader != null) {
            try {
                return classLoader.loadClass(name);
            } catch (ClassNotFoundException e) {
                Log2.e(e);
            }
        }
        return null;
    }

    public void toast(final String message) {
        if (focusActivity != null) {
            focusActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(focusActivity, String.valueOf(message), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void toastLong(final String message) {
        if (focusActivity != null) {
            focusActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(focusActivity, String.valueOf(message), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        focusActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
