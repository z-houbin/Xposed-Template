package com.example.xposed.library.debug;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;


public class DebugListener {
    private final ArrayList<String> methodNames = new ArrayList<>();

    public DebugListener(String... method) {
        if (method != null) {
            methodNames.addAll(Arrays.asList(method));
        }
    }

    public void onMethodBefore(XC_MethodHook.MethodHookParam method) {

    }

    public void onMethodAfter(XC_MethodHook.MethodHookParam method) {

    }

    public boolean isDebug(XC_MethodHook.MethodHookParam method) {
        boolean match = false;
        if (method != null) {
            String methodName = method.method.getName();
            if (method.method instanceof Constructor) {
                methodName = "init";
            }
            match = methodNames.contains(methodName);
        }
        return match;
    }

    public boolean isDebug(String methodName) {
        boolean match = false;
        if (methodName != null) {
            match = methodNames.contains(methodName);
        }
        return match;
    }
}
