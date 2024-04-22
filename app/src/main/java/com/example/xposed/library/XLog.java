package com.example.xposed.library;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.example.xposed.library.utils.Files;
import com.example.xposed.library.utils.SqliteHelper;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("unused")
public class XLog {
    private static boolean enable = false;
    private static String TAG = "@XLog";
    private static File logDir;

    public static void enable(String tag) {
        XLog.enable = true;
        if (!TextUtils.isEmpty(tag)) {
            XLog.TAG = tag;
        }
    }

    public static void setLogDir(File logDir) {
        XLog.logDir = logDir;
    }

    public static String getLog(Object obj) {
        if (obj == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        if (obj instanceof Intent) {
            builder.append(getBundleLog(((Intent) obj).getExtras()));
        } else if (obj instanceof Bundle) {
            builder.append(getBundleLog((Bundle) obj));
        } else if (obj instanceof Cursor) {
            builder.append(getCursorLog((Cursor) obj));
        } else if (obj.getClass().isArray()) {
            builder.append(getArrayLog(obj));
        } else {
            builder.append(obj);
        }
        return builder.toString();
    }

    private static String getArrayLog(Object obj) {
        StringBuilder builder = new StringBuilder();
        if (obj == null) {
            return builder.toString();
        }
        // 判断数组类型
        Class<?> componentType = obj.getClass().getComponentType();
        if (componentType.isPrimitive()) {
            if (componentType == int.class) {
                builder.append(Arrays.toString((int[]) obj));
            } else if (componentType == byte.class) {
                builder.append(Arrays.toString((byte[]) obj));
            } else if (componentType == short.class) {
                builder.append(Arrays.toString((short[]) obj));
            } else if (componentType == long.class) {
                builder.append(Arrays.toString((long[]) obj));
            } else if (componentType == char.class) {
                builder.append(Arrays.toString((char[]) obj));
            } else if (componentType == float.class) {
                builder.append(Arrays.toString((float[]) obj));
            } else if (componentType == double.class) {
                builder.append(Arrays.toString((double[]) obj));
            } else if (componentType == boolean.class) {
                builder.append(Arrays.toString((boolean[]) obj));
            }
        } else if (componentType == byte.class) {
            builder.append(new String((byte[]) obj));
        } else {
            // 对于非原始类型数组，可以直接使用 Arrays.toString() 方法
            System.out.println(Arrays.toString((Object[]) obj));
        }
        return builder.toString();
    }

    private static String getBundleLog(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (String key : bundle.keySet()) {
            Object v = bundle.get(key);
            if (v == null) {
                v = "";
            }
            builder.append(key);
            builder.append(":");
            builder.append(v);
            builder.append("--");
        }
        return builder.toString();
    }

    private static String getCursorLog(Cursor cursor) {
        StringBuilder builder = new StringBuilder();
        try {
            builder.append(SqliteHelper.dumpCursor(cursor));
        } catch (Exception e) {
            XLog.e(e);
        }
        return builder.toString();
    }

    public static void d(String msg) {
        if (enable) {
            msg = TAG + " " + msg;
            ArrayList<String> split = splitLog(msg);
            for (String line : split) {
                Log.d(TAG, line);
            }
        }

        writeFileLog("[DEBUG][" + TAG + "] " + msg);
    }

    public static void d(String format, Object... msg) {
        if (enable) {
            d(format(format, msg));
        }
    }

    public static void w(String msg) {
        if (enable) {
            msg = TAG + " " + msg;
            ArrayList<String> split = splitLog(msg);
            for (String line : split) {
                Log.w(TAG, line);
            }
        }

        writeFileLog("[WARNING][" + TAG + "] " + msg);
    }

    public static void w(String format, String msg) {
        if (enable) {
            msg = TAG + " " + msg;
            msg = format(format, msg);
            ArrayList<String> split = splitLog(msg);
            for (String line : split) {
                Log.w(TAG, line);
            }
        }

        writeFileLog("[WARNING][" + TAG + "] " + msg);
    }

    public static void e(String msg) {
        msg = TAG + " " + msg;
        ArrayList<String> split = splitLog(msg);
        for (String line : split) {
            Log.e(TAG, line);
        }
        writeFileLog("[ERROR][" + TAG + "] " + msg);
    }

    public static void e(Throwable throwable) {
        String msg = throwable.getMessage() + "\r\n" + printStackTrace(throwable.getStackTrace());
        ArrayList<String> split = splitLog(msg);
        for (String line : split) {
            Log.e(TAG, line);
        }
        writeFileLog("[ERROR][" + TAG + "] " + msg);
    }

    public static void e(String format, String msg) {
        msg = TAG + " " + msg;
        msg = format(format, msg);
        ArrayList<String> split = splitLog(msg);
        for (String line : split) {
            Log.e(TAG, line);
        }
        writeFileLog("[ERROR][" + TAG + "] " + msg);
    }

    public static void e(String msg, Throwable throwable) {
        msg = TAG + " " + msg + " " + printStackTrace(throwable.getStackTrace());
        ArrayList<String> split = splitLog(msg);
        for (String line : split) {
            Log.e(TAG, line);
        }
        writeFileLog("[ERROR][" + TAG + "] " + msg);
    }

    public static void e(String format, String msg, Throwable throwable) {
        msg = TAG + " " + msg;
        msg = format(format, msg) + " " + printStackTrace(throwable.getStackTrace());
        ArrayList<String> split = splitLog(msg);
        for (String line : split) {
            Log.e(TAG, line);
        }
        writeFileLog("[ERROR][" + TAG + "] " + msg);
    }

    private static String format(String format, Object... msg) {
        if (!format.contains("%")) {
            return format + " " + Arrays.toString(msg);
        }
        return String.format(Locale.CHINA, format, msg);
    }

    public static void printStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuilder builder = new StringBuilder();
        String lineSeparator = System.getProperty("line.separator");
        for (StackTraceElement trace : stackTrace) {
            if (builder.length() > 0) {
                builder.append(lineSeparator);
            }
            builder.append(java.text.MessageFormat.format("{0}.{1}() {2}", trace.getClassName(), trace.getMethodName(), trace.getLineNumber()));
        }

        XLog.d("StackTrace \r\n" + builder);
    }

    public static String printStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder builder = new StringBuilder();
        String lineSeparator = System.getProperty("line.separator");
        for (StackTraceElement trace : stackTrace) {
            if (builder.length() > 0) {
                builder.append(lineSeparator);
            }
            builder.append(java.text.MessageFormat.format("{0}.{1}() {2}", trace.getClassName(), trace.getMethodName(), trace.getLineNumber()));
        }
        return builder.toString();
    }

    public static void printBytes(String tag, byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        builder.append(":");
        builder.append(bytes.length);
        builder.append(" ");
        for (int i = 0; i < bytes.length && i < 20; i++) {
            builder.append(bytes[i]);
            builder.append(".");
        }
        XLog.d(tag, builder.toString());
    }

    /**
     * 打印对象基础信息
     *
     * @param obj 原始对象
     */
    public static String profile(Object obj, boolean includeMethod) {
        if (obj == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder("\r\n");

        try {
            Class<?> cls = obj.getClass();
            builder.append(cls.getName());
            builder.append("\r\n");

            Field[] fields = cls.getFields();
            for (Field field : fields) {
                // 对于每个属性，获取属性名
                String varName = field.getName();
                try {
                    boolean access = field.isAccessible();
                    if (!access) {
                        field.setAccessible(true);
                    }

                    // 从obj中获取field变量
                    Object o = field.get(obj);
                    builder.append("变量： ").append(varName).append(" = ");
                    builder.append(XLog.getLog(o));
                    builder.append("\r\n");
                    if (!access) {
                        field.setAccessible(false);
                    }
                } catch (Exception ex) {
                    XLog.e(ex);
                }
            }

            if (!includeMethod) {
                return builder.toString();
            }

            // 函数
            Method[] methods = cls.getMethods();
            for (Method method : methods) {
                // 对于每个属性，获取属性名
                // 得到方法的返回值类型的类类型
                builder.append("\r\n");
                Class<?> returnType = method.getReturnType();
                builder.append(returnType.getName());
                builder.append("  ");
                // 得到方法的名称
                builder.append(method.getName());
                builder.append("(");
                // 获取参数类型--->得到的是参数列表的类型的类类型
                Class<?>[] paramTypes = method.getParameterTypes();
                for (Class<?> class1 : paramTypes) {
                    builder.append(class1.getName());
                    builder.append(",");
                }
                builder.append(")");
            }
        } catch (Exception e) {
            XLog.e(e);
        }
        return builder.toString();
    }

    private static void writeFileLog(String text) {
        if (logDir == null) {
            return;
        }

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String monthStr, dayStr;
        if (month < 10) {
            monthStr = "0" + month;
        } else {
            monthStr = String.valueOf(month);
        }
        if (day < 10) {
            dayStr = "0" + day;
        } else {
            dayStr = String.valueOf(day);
        }

        File logFile = new File(logDir, "demo-" + monthStr + "-" + dayStr + ".log");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        text = format.format(new Date()) + " " + text;
        Files.appendFile(logFile, text);
    }

    /**
     * 分割长日志
     *
     * @param message 日志消息
     * @return 多段日志信息
     */
    private static ArrayList<String> splitLog(String message) {
        ArrayList<String> result = new ArrayList<>();
        int maxLogSize = 1000;
        for (int i = 0; i <= message.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = Math.min(end, message.length());
            if (i == 0) {
                result.add(message.substring(start, end));
            } else {
                result.add("\t" + message.substring(start, end));
            }
        }
        if (result.isEmpty()) {
            result.add(message);
        }
        return result;
    }
}
