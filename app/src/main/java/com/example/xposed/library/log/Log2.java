package com.example.xposed.library.log;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.xposed.library.utils.SqliteHelper;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.util.Locale;

public class Log2 {
    private static String TAG = "com.example.xposed";

    private static boolean isDebug;

    public static void init(String t, final boolean isDebug) {
        TAG = t + " ";
        Log2.isDebug = isDebug;

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag(TAG)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return isDebug;
            }
        });
    }

    public static void d(String text) {
        if (isDebug) {
            Logger.d(text);
        }
    }

    public static void d(String tag, String text) {
        if (isDebug) {
            Logger.e(" %s : %s", tag, text);
        }
    }

    public static void d(Object cls, String log) {
        if (isDebug) {
            Logger.e(" %s : %s", cls.getClass().getName(), log);
        }
    }

    public static void d(Class<?> cls, String log) {
        if (isDebug) {
            Logger.e(" %s : %s", cls.getName(), log);
        }
    }

    public static void e(Throwable e) {
        d(TAG, Log.getStackTraceString(e));
    }

    public static void e(String tag, Throwable e) {
        d(tag, Log.getStackTraceString(e));
    }

    /**
     * 打印当前堆栈信息
     */
    public static void printStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuilder builder = new StringBuilder();
        String lineSeparator = System.getProperty("line.separator");
        for (StackTraceElement trace : stackTrace) {
            if (builder.length() > 0) {
                builder.append(lineSeparator);
            }
            builder.append(java.text.MessageFormat.format("{0}.{1}() {2}"
                    , trace.getClassName()
                    , trace.getMethodName()
                    , trace.getLineNumber()));
        }
        if (isDebug) {
            Log2.d("StackTrace \r\n" + builder.toString());
        }
    }

    public static void d(Object tag, Object log) {
        if (isDebug) {
            Log2.d(getTag(tag), getLog(log));
        }
    }

    public static String getLog(Object log) {
        StringBuilder builder = new StringBuilder();
        if (log instanceof Bundle) {
            builder.append(getBundleLog((Bundle) log));
        } else if (log instanceof Cursor) {
            builder.append(getCursorLog((Cursor) log));
        } else if (log.getClass().isArray()) {
            builder.append(getArrayLog((Object[]) log));
        } else {
            builder.append(log.toString());
        }
        return builder.toString();
    }

    private static String getArrayLog(Object[] arr) {
        StringBuilder builder = new StringBuilder();
        if (arr != null) {
            for (int i = 0; i < arr.length; i++) {
                builder.append("[");
                builder.append(i);
                builder.append("]");
                builder.append(": ");
                builder.append(arr[i]);
                builder.append("   ");
            }
        }
        return builder.toString();
    }

    private static String getBundleLog(Bundle bundle) {
        StringBuilder builder = new StringBuilder();
        for (String key : bundle.keySet()) {
            Object v = bundle.get(key);
            if (v == null) {
                v = "";
            }
            builder.append(key);
            builder.append(":");
            builder.append(v.toString());
            builder.append("--");
        }
        return builder.toString();
    }

    private static String getCursorLog(Cursor cursor) {
        StringBuilder builder = new StringBuilder();
        try {
            builder.append(SqliteHelper.dumpCursor(cursor));
        } catch (Exception e) {
            Log2.e(e);
        }
        return builder.toString();
    }

    private static String getTag(Object tag) {
        String t = TAG;
        if (tag == null) {
            t += "";
        } else if (tag instanceof String) {
            t += tag.toString();
        } else if (tag instanceof Class) {
            t += ((Class<?>) tag).getName();
        } else {
            t += tag.getClass().getSimpleName();
        }
        return t;
    }

    public static void d(String format, Object... params) {
        d(String.format(Locale.CHINA, format, params));
    }

    public static void json(String data) {
        Logger.json(data);
    }
}
