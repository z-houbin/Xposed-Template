package com.example.xposed.library.proxy;

import android.view.View;

import com.example.xposed.library.XLog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class ProxyHandler {
    private final ClassLoader classLoader;

    public ProxyHandler(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 完全代理对象
     *
     * @param source 代理类
     * @param proxy  回调代理
     */
    public <T> Object proxy(String source, T proxy) {
        try {
            return proxy(classLoader.loadClass(source), proxy);
        } catch (ClassNotFoundException e) {
            XLog.e("proxy failed", e);
            return null;
        }
    }

    /**
     * 完全代理对象
     *
     * @param source 代理类
     * @param proxy  回调代理
     */
    public <T> Object proxy(Class<?> source, T proxy) {
        T proxy0 = proxy;

        return Proxy.newProxyInstance(classLoader, source.getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Method proxyMethod = proxy0.getClass().getMethod(method.getName(), method.getParameterTypes());
                return proxyMethod.invoke(proxy0, args);
            }
        });
    }

    /**
     * 监听函数执行情况,不干预原执行流程,添加 after 监听函数
     *
     * @param source     原始对象
     * @param afterProxy 回调代理
     */
    public <T> Object proxyThen(Object source, T afterProxy) {

        return Proxy.newProxyInstance(classLoader, source.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();

                XLog.d("invoke method", methodName);

                // 执行原始函数
                Object result = method.invoke(source, args);

                // 回调监听
                try {
                    Method proxyMethod = afterProxy.getClass().getMethod(methodName, method.getParameterTypes());
                    proxyMethod.invoke(afterProxy, args);
                } catch (NoSuchMethodException
                        | SecurityException
                        | IllegalAccessException
                        | IllegalArgumentException
                        | InvocationTargetException e) {
                    e.printStackTrace();
                }

                return result;
            }
        });
    }

    private void example() {
        // 代理点击事件
        XposedBridge.hookAllMethods(View.class, "setOnClickListener", new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XLog.d("setOnClickListener", param.thisObject);

                ProxyHandler proxyHandler = new ProxyHandler(classLoader);
                param.args[0] = proxyHandler.<View.OnClickListener>proxyThen(param.args[0], new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        XLog.d("view.click", v);
                    }
                });

                XLog.d("setOnClickListener hook", "end");
            }
        });
    }
}

