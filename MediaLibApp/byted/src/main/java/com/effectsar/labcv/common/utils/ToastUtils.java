package com.effectsar.labcv.common.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.effectsar.labcv.R;
import com.effectsar.labcv.core.util.LogUtils;


public class ToastUtils {
    private static Context mAppContext = null;
    private static Toast mToast;
    private static TextView mTextView;

    public static void init(Context context) {
        mAppContext = context;
        if (mAppContext != null) {
            mToast = new Toast(mAppContext);
            View layout = View.inflate(mAppContext, R.layout.layout_toast, null);
            mTextView = layout.findViewById(R.id.tv_toast);
            mToast.setView(layout);
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER,0,0);
        }
    }


    public static void show(String msg) {
        if (null == mAppContext) {
            LogUtils.d("ToastUtils not inited with Context");
            return;
        }
//        Toast.makeText(mAppContext, msg, Toast.LENGTH_SHORT).show();
        if (mToast == null || mTextView == null) {
            mToast = new Toast(mAppContext);
            View layout = View.inflate(mAppContext, R.layout.layout_toast, null);
            mTextView = layout.findViewById(R.id.tv_toast);
            mToast.setView(layout);
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER,0,0);
        }
        mTextView.setText(msg);
        mToast.show();

    }

    public static Toast makeToast(String msg) {
        if (null == mAppContext) {
            LogUtils.d("ToastUtils not inited with Context");
            return null;
        }
        Toast t = Toast.makeText(mAppContext, msg, Toast.LENGTH_LONG);
        t.show();
        return t;
    }
}
