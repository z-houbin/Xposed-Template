package com.example.xposed.library.debug;

import com.example.xposed.library.XLog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class Trace {

    /**
     * 仅输出指定类
     *
     * @param cls          类
     * @param debugListener 回调
     */
    public static void traceOn(Class<?> cls, DebugListener debugListener) {
        XLog.d("Trace Class " + cls);
        if (cls == null || debugListener == null) {
            return;
        }
        List<String> methodNames = new ArrayList<>();
        Method[] declaredMethods = cls.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (!methodNames.contains(method.getName()) && debugListener.isDebug(method.getName())) {
                methodNames.add(method.getName());
            }
        }
        declaredMethods = cls.getMethods();
        for (Method method : declaredMethods) {
            if (!methodNames.contains(method.getName()) && debugListener.isDebug(method.getName())) {
                methodNames.add(method.getName());
            }
        }
        DebugMethod debugMethod = new DebugMethod(cls, debugListener);
        for (String name : methodNames) {
            XposedBridge.hookAllMethods(cls, name, debugMethod);
        }
        XposedBridge.hookAllConstructors(cls, debugMethod);
    }

    /**
     * 追踪类函数调用,不输出日志
     *
     * @param cls          类
     * @param debugListener 回调
     */
    public static void traceQuiet(Class<?> cls, DebugListener debugListener) {
        XLog.d("Trace Class " + cls);
        if (cls == null) {
            return;
        }
        List<String> methodNames = new ArrayList<>();
        Method[] declaredMethods = cls.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (!methodNames.contains(method.getName())) {
                methodNames.add(method.getName());
            }
        }
        declaredMethods = cls.getMethods();
        for (Method method : declaredMethods) {
            if (!methodNames.contains(method.getName())) {
                methodNames.add(method.getName());
            }
        }
        DebugMethod debugMethod = new DebugMethod(cls, debugListener);
        debugMethod.setQuiet(true);
        for (String name : methodNames) {
            XposedBridge.hookAllMethods(cls, name, debugMethod);
        }
        XposedBridge.hookAllConstructors(cls, debugMethod);
    }

    /**
     * 追踪类函数调用
     *
     * @param cls          类
     * @param debugListener 函数调用回调
     */
    public static void trace(Class<?> cls, DebugListener debugListener) {
        XLog.d("Trace Class " + cls);
        if (cls == null) {
            return;
        }
        List<String> methodNames = new ArrayList<>();
        Method[] declaredMethods = cls.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (!methodNames.contains(method.getName())) {
                methodNames.add(method.getName());
            }
        }
        declaredMethods = cls.getMethods();
        for (Method method : declaredMethods) {
            if (!methodNames.contains(method.getName())) {
                methodNames.add(method.getName());
            }
        }
        DebugMethod debugMethod = new DebugMethod(cls, debugListener);
        for (String name : methodNames) {
            XposedBridge.hookAllMethods(cls, name, debugMethod);
        }
        XposedBridge.hookAllConstructors(cls, debugMethod);
    }

    private static class DebugMethod extends XC_MethodHook {
        private DebugListener debugListener;
        private Class<?> cls;
        private boolean quiet;

        DebugMethod(Class<?> cls, DebugListener debugListener) {
            this.cls = cls;
            this.debugListener = debugListener;
        }

        void setQuiet(boolean quiet) {
            this.quiet = quiet;
        }

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            if (debugListener != null && debugListener.isDebug(param)) {
                debugListener.onMethodBefore(param);
            }
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            if (debugListener != null && debugListener.isDebug(param)) {
                debugListener.onMethodAfter(param);
            }

            if (quiet) {
                return;
            }

            if (param.thisObject != null) {
                String clsName = param.thisObject.getClass().getName();
                try {
                    new MethodPrinter(param).print(clsName);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            } else {
                //静态
                String clsName = cls.getName();
                String methodName = param.method.getName();
                try {
                    new MethodPrinter(param).print(clsName);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }
    }
}
