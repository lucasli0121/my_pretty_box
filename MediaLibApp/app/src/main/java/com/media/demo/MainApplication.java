package com.media.demo;

import android.app.Activity;
import android.app.Application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainApplication extends Application {

    private static MainApplication INSTANCE;

    private static Activity CURRENT_ACTIVITY;

    public static MainApplication getInstance() {
        return INSTANCE;
    }

    public static void setCurrentActivity(Activity activity) {
        CURRENT_ACTIVITY = activity;
    }

    public static Activity getCurrentActivity() {
        return CURRENT_ACTIVITY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;

//        MHSDK.init(this,"3db63f3b4b73d75062fdb773550b4de2","e614cd56c03a085456b2f4a94c299863");
    }


    /**
     * 错误进行崩溃处理
     **/
    private void initExceptionHandler() {
        final Thread.UncaughtExceptionHandler dueh = Thread.getDefaultUncaughtExceptionHandler();
        /* 处理未捕捉异常 */
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                FileOutputStream fos = null;
                PrintStream ps = null;
                try {
                    File path = INSTANCE.getExternalCacheDir();
                    if (!path.isDirectory()) {
                        path.mkdirs();
                    }
                    fos = new FileOutputStream(path.getAbsolutePath() + File.separator + "crash_log.txt", true);
                    ps = new PrintStream(fos);
                    ps.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).format(new Date(System.currentTimeMillis())));
                    ex.printStackTrace(ps);
                } catch (FileNotFoundException e) {
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                        }
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }
                dueh.uncaughtException(thread, ex);
            }
        });
    }
}