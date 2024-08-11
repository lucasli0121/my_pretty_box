package com.effectsar.labcv.common.view.bubble;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.effectsar.labcv.R;
import com.effectsar.labcv.common.model.BubbleConfig;
import com.effectsar.labcv.common.model.EffectType;
import com.effectsar.labcv.common.utils.DensityUtils;
import com.effectsar.labcv.common.view.SwitchView;
import com.effectsar.labcv.core.util.LogUtils;

/** {zh} 
 * 顶部弹窗管理器
 * Created  on 2021/5/19 2:08 下午
 */
/** {en} 
 * Top pop-up manager
 * Created on 2021/5/19 2:08 pm
 */

public class BubbleWindowManager {
    private final Context mContext;
    private BubblePopupWindow mPopupWindow = null;
    private final View mBubbleView;
    private final BubbleConfig mConfig;
    private String mKey="";

    public Context getContext() {
        return mContext;
    }

    public BubblePopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    public View getBubbleView() {
        return mBubbleView;
    }

    public BubbleConfig getConfig() {
        return mConfig;
    }

    public String getKey() {
        return mKey;
    }

    public BubbleCallback getBubbleCallback() {
        return mBubbleCallback;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    public enum ITEM_TYPE{
        EFFECT_TYPE,PERFORMANCE,BEAUTY,RESOLUTION,PICTURE_MODE
    }


    public  interface BubbleCallback{
        void onBeautyDefaultChanged(boolean on);
        void onResolutionChanged(int width, int height);
        void onPerformanceChanged(boolean on);

        void onPictureModeChanged(boolean on);

    }

    private BubbleCallback mBubbleCallback;

    public BubbleWindowManager(Context context) {
        this.mContext = context;
        mPopupWindow = new BubblePopupWindow(mContext);
        mPopupWindow.setParam(DensityUtils.getScreenWidth((Activity) mContext) - mContext.getResources().getDimensionPixelSize(com.effectsar.labcv.R.dimen.popwindow_margin) * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        mBubbleView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_view, null);
        mPopupWindow.setBubbleView(mBubbleView); //   {zh} 设置气泡内容       {en} Set bubble content  
        mConfig = new BubbleConfig(EffectType.LITE_ASIA,false, new Point(1280,720), true );
    }

    public void hideResolutionOption(View anchor,int...hide_resolution){
        if (null == anchor){
            LogUtils.e("null == anchor");
            return;
        }
        for (int resolution: hide_resolution) {
            switch (resolution) {
                case 480:
                    mBubbleView.findViewById(R.id.rb_480).setVisibility(View.GONE);
                    break;
                case 720:
                    mBubbleView.findViewById(R.id.rb_720).setVisibility(View.GONE);
                    break;
                case 1080:
                    mBubbleView.findViewById(R.id.rb_1080).setVisibility(View.GONE);
                    break;
            }
        }
    }

    public void show( View anchor, BubbleCallback callback, ITEM_TYPE...item_types){
        if (null == anchor){
            LogUtils.e("null == anchor");
            return;
        }
        mBubbleCallback = callback;
        boolean hasResolution = false;
        for (ITEM_TYPE stickerType: item_types){
            switch (stickerType){
                case BEAUTY:
                    mBubbleView.findViewById(R.id.rl_beauty).setVisibility(View.VISIBLE);
                    SwitchView  sw1 = mBubbleView.findViewById(R.id.sw_beauty);
                    sw1.setOpened(mConfig.isEnableBeauty());
                    sw1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sw1.setOpened(!sw1.isOpened());

                        }
                    });
                    sw1.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                        @Override
                        public void toggleToOn(SwitchView view) {
                            if (mBubbleCallback != null){
                                mBubbleCallback.onBeautyDefaultChanged(true);
                            }
                            mConfig.setEnableBeauty(true);
                        }

                        @Override
                        public void toggleToOff(SwitchView view) {
                            if (mBubbleCallback != null){
                                mBubbleCallback.onBeautyDefaultChanged(false);
                            }
                            mConfig.setEnableBeauty(false);

                        }
                    });

                    break;
//                case EFFECT_TYPE:
//                    mBubbleView.findViewById(R.id.rl_effect_type).setVisibility(View.VISIBLE);
//                    RadioGroup rg1 =  mBubbleView.findViewById(R.id.rg_effect);
//                    rg1.check(mConfig.getEffectType() == EffectType.LITE_ASIA ?R.id.rb_live:R.id.rb_camera);
//                    rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                        @Override
//                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
//
//                            if (mBubbleCallback != null){
//                                EffectType stickerType = EffectType.LITE_ASIA;
//                                if (i == R.id.rb_live){
//                                    stickerType = LocaleUtils.isAsia(mContext)?EffectType.LITE_ASIA :EffectType.LITE_NOT_ASIA;
//                                }else if (i == R.id.rb_camera){
//                                    stickerType = LocaleUtils.isAsia(mContext)?EffectType.STANDARD_ASIA :EffectType.STANDARD_NOT_ASIA;
//                                }
//                                mBubbleCallback.onEffectTypeChanged(stickerType);
//                                mConfig.setEffectType(stickerType);
//
//                            }
//
//                        }
//                    });

//                    break;
                case PERFORMANCE:
                    mBubbleView.findViewById(R.id.rl_performance).setVisibility(View.VISIBLE);
                    SwitchView sw2 = mBubbleView.findViewById(R.id.sw_performance);
                    LogUtils.e("mConfig.isPerformance() ="+mConfig.isPerformance());
                    sw2.setOpened(mConfig.isPerformance());
                    sw2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sw2.setOpened(!sw2.isOpened());

                        }
                    });
                    sw2.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                        @Override
                        public void toggleToOn(SwitchView view) {
                            if (mBubbleCallback != null){
                                mBubbleCallback.onPerformanceChanged(true);

                            }
                            mConfig.setPerformance(true);

                        }

                        @Override
                        public void toggleToOff(SwitchView view) {
                            if (mBubbleCallback != null){
                                mBubbleCallback.onPerformanceChanged(false);

                            }
                            mConfig.setPerformance(false);

                        }
                    });

                    break;
                case RESOLUTION:
                    hasResolution = true;
                    mBubbleView.findViewById(R.id.rl_resolution).setVisibility(View.VISIBLE);
                    RadioGroup rg2 =  mBubbleView.findViewById(R.id.rg_resolution);
                    switch(mConfig.getResolution().x){
                        case 1280:
                            rg2.check(R.id.rb_720);
                            break;
                        case 640:
                            rg2.check(R.id.rb_480);
                            break;
                        case 1920:
                            rg2.check(R.id.rb_1080);
                            break;
                    }
//                    rg2.check(mConfig.getResolution().x == 1280?R.id.rb_720:R.id.rb_480);
                    rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                            if (mBubbleCallback != null) {
                                if (i == R.id.rb_720){
                                    mBubbleCallback.onResolutionChanged(1280,720 );
                                    mConfig.setResolution(new Point(1280,720));
                                }else if (i == R.id.rb_480){
                                    mBubbleCallback.onResolutionChanged(640,480 );
                                    mConfig.setResolution(new Point(640,480));
                                }else if (i == R.id.rb_1080){
                                    mBubbleCallback.onResolutionChanged(1920,1080 );
                                    mConfig.setResolution(new Point(1920,1080));
                                }
                            }
//                            mConfig.setResolution(resolution == 720?new Point(1280,720):new Point(640,480));


                        }
                    });
                    break;

                case PICTURE_MODE:
                    mBubbleView.findViewById(R.id.rl_picture_mode).setVisibility(View.VISIBLE);
                    SwitchView sw3 = mBubbleView.findViewById(R.id.sw_picture_mode);
                    LogUtils.e("mConfig.isPictureMode() = " + mConfig.isEnablePictureMode());
                    sw3.setOpened(mConfig.isEnablePictureMode());
                    sw3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sw3.setOpened(!sw3.isOpened());

                        }
                    });
                    sw3.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                        @Override
                        public void toggleToOn(SwitchView view) {
                            mConfig.setEnablePictureMode(true);
                            if (mBubbleCallback != null){
                                mBubbleCallback.onPictureModeChanged(true);
                            }
                        }

                        @Override
                        public void toggleToOff(SwitchView view) {
                            mConfig.setEnablePictureMode(false);
                            if (mBubbleCallback != null){
                                mBubbleCallback.onPictureModeChanged(false);
                            }
                        }
                    });
                    break;

            }

        }
        if (!hasResolution) {
            mBubbleView.findViewById(R.id.rl_resolution).setVisibility(View.GONE);
        }

        int offset = anchor.getLeft()+anchor.getWidth()/2 - mContext.getResources().getDimensionPixelSize(com.effectsar.labcv.R.dimen.popwindow_margin);
        mPopupWindow.show(anchor, Gravity.BOTTOM, offset); //   {zh} 显示弹窗       {en} Display popup  





    }
}
