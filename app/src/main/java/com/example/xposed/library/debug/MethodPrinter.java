package com.example.xposed.library.debug;

import com.example.xposed.library.XLog;

import java.lang.reflect.Member;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;

public class MethodPrinter implements BasePrinter {
    private XC_MethodHook.MethodHookParam params;

    public MethodPrinter(XC_MethodHook.MethodHookParam params) {
        this.params = params;
    }

    @Override
    public void print(String tag) {
        StringBuilder builder = new StringBuilder(tag);
        builder.append(" ");
        try {
            Member method = params.method;
            builder.append("(").append(method.getName()).append(")");
            Object[] params = this.params.args;
            //参数
            for (int i = 0; i < params.length; i++) {
                builder.append("p").append(i);
                builder.append(":");
                Object p = params[i];
                if (p == null) {
                    builder.append("null");
                } else {
                    builder.append("(");
                    builder.append(p.getClass().getName());
                    builder.append(")");
                    if (p.getClass().isArray()) {
                        try {
                            builder.append(Arrays.toString((Object[]) p));
                        } catch (Exception e) {
                            // e.printStackTrace();
                        }finally {
                            builder.append(p.toString());
                        }
                    } else {
                        builder.append(p.toString());
                    }
                }
                builder.append(",");
            }
            //返回值
            builder.append("-->");
            Object result = this.params.getResult();
            if (result == null) {
                builder.append("null");
            } else {
                builder.append("(");
                builder.append(result.getClass().getName());
                builder.append(")");
                if (result.getClass().isArray()) {
                    builder.append(Arrays.toString((Object[]) result));
                } else {
                    builder.append(result.toString());
                }
            }
        } catch (Exception e) {
            XLog.e(e);
        }

        XLog.d(builder.toString());
    }
}
