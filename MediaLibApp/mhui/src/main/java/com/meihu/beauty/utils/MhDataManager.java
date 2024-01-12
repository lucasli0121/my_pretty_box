package com.meihu.beauty.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.meihu.beauty.bean.MeiYanDataBean;
import com.meihu.beauty.bean.MeiYanValueBean;
import com.meihu.beauty.bean.TieZhiBean;
import com.meihu.beauty.interfaces.CommonCallback;
import com.meihu.beauty.interfaces.IBeautyEffectListener;
import com.meihu.beauty.views.BeautyViewHolder;
import com.meihu.beautylibrary.MHSDK;
import com.meihu.beautylibrary.constant.Constants;
import com.meihu.beautylibrary.manager.MHBeautyManager;
import com.meihu.beautylibrary.utils.UrlUtils;

import java.util.List;

public class MhDataManager {

    private final           String          TAG = MhDataManager.class.getName();
    private volatile static MhDataManager   sInstance;
    private                 MHBeautyManager mMhManager;
    private MeiYanValueBean mInputValue;//美狐sdk上一次使用的数值
    private MeiYanValueBean mUseValue;//当前使用的是美颜美型还是一键美颜
    private MeiYanValueBean mMeiYanValue;//用户当前设置的美颜，美型数值
    private MeiYanValueBean mOneKeyValue;//用户当前设置的一键美颜数值
    private IBeautyEffectListener mMeiYanChangedListener;
    private Context mContext;
    private SPUtil mSPUtil;

    private boolean isShowCapture = true;
    private BeautyViewHolder mBeautyViewHolder;
    private MeiYanDataBean mMeiYanDataBean;

    private int mFilterId;
    private int mHahaName;
    private boolean mHahaTexiao;
    private String mTieZhiName;
    private int  mTieZhiAction;
    private boolean  mTieZhiShow;
    private boolean  mTieZhiIsAction;
    private String  mTieZhiKey;
    private Bitmap mWaterBitmap;
    private int mWaterRes;
    private int mWaterPosition;
    private int mTeXiaoId;

    private boolean mMakeupLipstick;
    private boolean mMakeupEyelash;
    private boolean mMakeupEyeliner;
    private boolean mMakeupEyebrow;
    private boolean mMakeupBlush;
    private boolean mInit;

    private MhDataManager() {

    }

    public boolean getShowCapture(){
        return isShowCapture;
    }

    public static MhDataManager getInstance() {
        if (sInstance == null) {
            synchronized (MhDataManager.class) {
                if (sInstance == null) {
                    sInstance = new MhDataManager();
                }
            }
        }
        return sInstance;
    }

    public Context getContext(){
        return mContext;
    }

    public MHBeautyManager getMHBeautyManager(){
        return mMhManager;
    }

    public void setBeautyViewHolder(BeautyViewHolder beautyViewHolder){
        mBeautyViewHolder = beautyViewHolder;
    }

    public synchronized MhDataManager create(Context context) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        String language = config.locale.getLanguage();
        MHSDK.isEngLish = language.equals("en");

        mContext = context;
        mSPUtil = new SPUtil(context);
        mInputValue = new MeiYanValueBean();
        mUseValue = null;
        mMeiYanValue = null;
        mOneKeyValue = null;
        mInit = false;
        try {
            mMhManager = new MHBeautyManager(context,true);
            mMhManager.setSmooth(0.08f);
            mMhManager.setMinFaceSize(40);
//            mMhManager.setMaxFace(2);
//            mMhManager.setAlwaysTrack(true);
//            mMhManager.setAlwaysTrack(true);
//            mMhManager.setOnFaceDetectListener(new OnFaceDetectListener() {
//                @Override
//                public void OnFaceDetect(boolean hasFace) {
//                    Log.e(TAG, "OnFaceDetect: " + hasFace);
//                }
//            });
        } catch (Exception e) {
            mMhManager = null;
            e.printStackTrace();
        }

        MeiYanValueBean  meiYanValueBean = new MeiYanValueBean();

        String  data =   mSPUtil.getString("meiyan",null);
        if(!TextUtils.isEmpty(data)){
            mMeiYanDataBean = JSON.parseObject(data,MeiYanDataBean.class);
        }

        if (mMeiYanDataBean == null){
            if (!mMhManager.isOnlyOneKey()) {
                meiYanValueBean.setMeiBai(7);
                meiYanValueBean.setMoPi(7);
                meiYanValueBean.setHongRun(5);
                meiYanValueBean.setLiangDu(57);

                meiYanValueBean.setDaYan(30);
                meiYanValueBean.setMeiMao(50);
                meiYanValueBean.setYanJu(50);
                meiYanValueBean.setYanJiao(50);
                meiYanValueBean.setShouLian(30);
                meiYanValueBean.setZuiXing(50);
                meiYanValueBean.setShouBi(50);
                meiYanValueBean.setXiaBa(50);
                meiYanValueBean.setETou(50);
                meiYanValueBean.setChangBi(50);
                meiYanValueBean.setXueLian(0);
                meiYanValueBean.setKaiYanJiao(50);

//                mMhManager.setFilter(MHSDK.FILTER_WEI_MEI);
                //setFilter(MHSDK.FILTER_WEI_MEI);
            }
        }else{
            meiYanValueBean  = mMeiYanDataBean.getMeiYanValueBean();
        }

        setMeiYanValue(meiYanValueBean)
                .useMeiYan().notifyLiangDuChanged();
        setMeiYanValue(meiYanValueBean)
                .useMeiYan().notifyMeiYanChanged();

//        requestActionStickers();

//        mMhManager.setOnStickerActionListener(new OnStickerActionListener() {
//            @Override
//            public void OnStickerAction(String name,int action,boolean show) {
//                Log.e(TAG, "OnStickerAction: " + "name:" + name + ",action:" + action + ",show:"+show);
//                if (mBeautyViewHolder != null && action != 0 && !show){ //动作贴纸
//                    mBeautyViewHolder.hideTip();
//                    mTieZhiShow  = true;
//                }
//            }
//        });

        restoreMeiYanData();

        return this;
    }

    private void saveMeiYanData(){

        if (mMeiYanDataBean == null){
            mMeiYanDataBean = new MeiYanDataBean();
        }

        if (mMeiYanValue !=null){

            MeiYanValueBean meiYanValueBean =  MeiYanValueBean.copy(mMeiYanValue);
            mMeiYanDataBean.setMeiYanValueBean(meiYanValueBean);
            mMeiYanDataBean.setTieZhiName(mTieZhiName);
            mMeiYanDataBean.setTieZhiAction(mTieZhiAction);
            mMeiYanDataBean.setTieZhiShow(mTieZhiShow);
            mMeiYanDataBean.setTieZhiIsAction(mTieZhiIsAction);
            mMeiYanDataBean.setTieZhiKey(mTieZhiKey);
            mMeiYanDataBean.setFilterId(mFilterId);
            mMeiYanDataBean.setTeXiaoId(mTeXiaoId);
            mMeiYanDataBean.setWaterBitmap(mWaterBitmap);
            mMeiYanDataBean.setWaterRes(mWaterRes);
            mMeiYanDataBean.setWaterposition(mWaterPosition);
            mMeiYanDataBean.setHahaName(mHahaName);
            mMeiYanDataBean.setHahaTeXiao(mHahaTexiao);

            mMeiYanDataBean.setMakeupLipstick(mMakeupLipstick);
            mMeiYanDataBean.setMakeupEyelash(mMakeupEyelash);
            mMeiYanDataBean.setMakeupEyeliner(mMakeupEyeliner);
            mMeiYanDataBean.setMakeupEyebrow(mMakeupEyebrow);
            mMeiYanDataBean.setMakeupBlush(mMakeupBlush);

            int[] useFaces = new int[mMhManager.getUseFaces().length];
            for (int i = 0; i < mMhManager.getUseFaces().length ; i++) {
                useFaces[i] = mMhManager.getUseFaces()[i];
            }
            mMeiYanDataBean.setUseFaces(useFaces);
        }

        String data = JSON.toJSONString(mMeiYanDataBean);
        mSPUtil.commitString("meiyan",data);
    }


    private void restoreMeiYanData(){

        if (mMeiYanDataBean != null && mMhManager != null){

            mMhManager.setUseFaces(mMeiYanDataBean.getUseFaces());
            mMhManager.setSticker(mMeiYanDataBean.getTieZhiName(),mMeiYanDataBean.getTieZhiAction(),mMeiYanDataBean.getTieZhiShow(),mMeiYanDataBean.getTieZhiIsAction(),mMeiYanDataBean.getTieZhiKey());
            mMhManager.setFilter(mMeiYanDataBean.getFilterId());
            mMhManager.setSpeciallyEffect(mMeiYanDataBean.getTeXiaoId());

            Bitmap bitmap = null;
            if (mMeiYanDataBean.getWaterRes() != 0) {
                bitmap = BitmapFactory.decodeResource(MhDataManager.getInstance().getContext().getResources(),mMeiYanDataBean.getWaterRes());
            }
            mMhManager.setWaterMark(bitmap,mMeiYanDataBean.getWaterposition());

            mMhManager.setDistortionEffect(mMeiYanDataBean.getHahaName(),mMeiYanDataBean.getHahaTeXiao());

            mMhManager.setMakeup(MHSDK.MAKEUP_LIPSTICK,mMeiYanDataBean.isMakeupLipstick());
            mMhManager.setMakeup(MHSDK.MAKEUP_EYELASH,mMeiYanDataBean.isMakeupEyelash());
            mMhManager.setMakeup(MHSDK.MAKEUP_EYELINER,mMeiYanDataBean.isMakeupEyeliner());
            mMhManager.setMakeup(MHSDK.MAKEUP_EYEBROW,mMeiYanDataBean.isMakeupEyebrow());
            mMhManager.setMakeup(MHSDK.MAKEUP_BLUSH,mMeiYanDataBean.isMakeupBlush());

        }
    }

    private void requestActionStickers(){
        String motionListUrl = UrlUtils.getMotionListUrl();
        MhDataManager.getTieZhiList(motionListUrl, new CommonCallback<String>() {
            @Override
            public void callback(String jsonStr) {
                if (TextUtils.isEmpty(jsonStr)) {
                    return;
                }
                try {
                    JSONObject obj = JSON.parseObject(jsonStr);
                    List<TieZhiBean> list = JSON.parseArray(obj.getString("list"), TieZhiBean.class);
                    if (list != null) {
                        for (TieZhiBean item:list){
                            String name = item.getName();
                            String resource = item.getResource();
                            if(MhDataManager.isTieZhiDownloaded(name)){
                                continue;
                            }else{
                                MhDataManager.downloadTieZhi(name, resource, new CommonCallback<Boolean>() {
                                    @Override
                                    public void callback(Boolean bean) {
                                        if(!bean){
                                            Log.e(TAG, "requestActionStickers: " + "not download");
                                        }
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    private void downloadActionStickers(String stickerName,String resource){
//        downloadTieZhi(stickerName,resource, new CommonCallback<Boolean>() {
//            @Override
//            public void callback(Boolean isSuccess) {
//                if (isSuccess) {
//
//                } else {
//
//                }
//            }
//        });
//    }

    public void setMeiYanChangedListener(IBeautyEffectListener meiYanChangedListener) {
        mMeiYanChangedListener = meiYanChangedListener;
    }

    public synchronized void release() {
        saveMeiYanData();
        OkGo.getInstance().cancelTag(Constants.GET_TIEZHI);
        OkGo.getInstance().cancelTag(Constants.DOWNLOAD_TIEZHI);
        mMeiYanChangedListener = null;
        mInputValue = null;
        mUseValue = null;
        mMeiYanValue = null;
        mOneKeyValue = null;
        if (mMhManager != null) {
            try {
                mMhManager.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        mMhManager = null;

    }

//    public void release() {
//        saveMeiYanData();
//        OkGo.getInstance().cancelTag(ResourceUrl.GET_TIEZHI);
//        OkGo.getInstance().cancelTag(ResourceUrl.DOWNLOAD_TIEZHI);
//        mMeiYanChangedListener = null;
//        mInputValue = null;
//        mUseValue = null;
//        mMeiYanValue = null;
//        mOneKeyValue = null;
//        mMhManager = null;
//    }

    public synchronized void destroy(){
//        mMeiYanDataBean = null;
    }

    public synchronized int render(int texture, int width, int height) {
        if (mMhManager != null) {
            try {
                texture = mMhManager.render12(texture, width, height,2,1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Log.e(TAG, "render: ");
        }
        return texture;
    }

    public synchronized int render(int texture, int width, int height,int faceScale, int textureScale) {
        if (mMhManager != null) {
            try {
                texture = mMhManager.render12(texture, width, height,faceScale,textureScale);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Log.e(TAG, "render: ");
        }
        return texture;
    }

    public void saveBeautyValue() {
        if (mMeiYanValue != null) {
        }
    }

    public MeiYanValueBean getMeiYanValue() {
        return mMeiYanValue;
    }

    public MeiYanValueBean getOneKeyValue() {
        return mOneKeyValue;
    }

    public MhDataManager setMeiYanValue(MeiYanValueBean meiYanValue) {
        mMeiYanValue = meiYanValue;
        return this;
    }

    public MhDataManager setOneKeyValue(MeiYanValueBean oneKeyValue) {
        mOneKeyValue = oneKeyValue;
        return this;
    }


    public MhDataManager useMeiYan() {
        mUseValue = mMeiYanValue;
        return this;
    }

    public MhDataManager useOneKey() {
        mUseValue = mOneKeyValue;
        return this;
    }


    public void setMeiBai(int meiBai) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setMeiBai(meiBai);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    public void setMoPi(int moPi) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setMoPi(moPi);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    public void setHongRun(int hongRun) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setHongRun(hongRun);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    public void setLiangDu(int liangDu) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setLiangDu(liangDu);
        }
        useMeiYan().notifyLiangDuChanged();
    }


    public void setDaYan(int daYan) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setDaYan(daYan);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    public void setMeiMao(int meiMao) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setMeiMao(meiMao);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    public void setYanJu(int yanJu) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setYanJu(yanJu);
        }
        useMeiYan().notifyMeiYanChanged();
    }

    public void setYanJiao(int yanJiao) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setYanJiao(yanJiao);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    public void setShouLian(int shouLian) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setShouLian(shouLian);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    public void setZuiXing(int zuiXing) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setZuiXing(zuiXing);
        }
        useMeiYan().notifyMeiYanChanged();
    }

    public void setShouBi(int shouBi) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setShouBi(shouBi);
        }
        useMeiYan().notifyMeiYanChanged();
    }

    public void setXiaBa(int xiaBa) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setXiaBa(xiaBa);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    public void setETou(int ETou) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setETou(ETou);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    public void setChangBi(int changBi) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setChangBi(changBi);
        }
        useMeiYan().notifyMeiYanChanged();
    }

    public void setXueLian(int xueLian) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setXueLian(xueLian);
        }
        useMeiYan().notifyMeiYanChanged();
    }

    public void setKaiYanJiao(int kaiYanJiao) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setKaiYanJiao(kaiYanJiao);
        }
        useMeiYan().notifyMeiYanChanged();
    }

    /**
     * 哈哈镜
     */
    public void setHaHa(int hahaName,boolean isTeXiao) {
        if (mMhManager != null) {
            mMhManager.setDistortionEffect(hahaName,isTeXiao);
            mHahaName = hahaName;
            mHahaTexiao =  isTeXiao;
        }
    }

    /**
     * 贴纸是否可用
     */
    public boolean isTieZhiEnable() {
        return mMeiYanChangedListener == null || mMeiYanChangedListener.isTieZhiEnable();
    }

    /**
     * 贴纸
     */
    public void setTieZhi(String tieZhiName,String tieZhiKey) {
        if (mMhManager != null) {
            boolean isShow = true;
            boolean isAction = false;
            String key = tieZhiKey;
            mMhManager.setSticker(tieZhiName,MHSDK.TE_XIAO_ACTION_NONE,isShow,isAction,key);
            mTieZhiName = tieZhiName;
            mTieZhiAction = 0;
            mTieZhiShow  = isShow;
            mTieZhiIsAction = isAction;
            mTieZhiKey = key;
        }
    }

    /**
     * 贴纸
     */
    public void setTieZhi(String tieZhiName,int tieZhiAction) {
        if (mMhManager != null) {
            boolean isShow = false;
            boolean isAction = true;
            String key = null;
            mMhManager.setSticker(tieZhiName,tieZhiAction,isShow,isAction,key);
            mTieZhiName = tieZhiName;
            mTieZhiAction = tieZhiAction;
            mTieZhiShow  = isShow;
            mTieZhiIsAction = isAction;
            mTieZhiKey = key;
        }
    }

    /**
     * 水印
     */
    public void setWater(int waterRes, int position) {
        if (mMhManager != null) {
            Bitmap bitmap = null;
            if (waterRes != 0) {
                bitmap = BitmapFactory.decodeResource(MhDataManager.getInstance().getContext().getResources(),waterRes);
            }
            mMhManager.setWaterMark(bitmap,position);
            mWaterBitmap = bitmap;
            mWaterRes = waterRes;
            mWaterPosition = position;
        }
    }


    /**
     * 特效
     */
    public void setTeXiao(int teXiaoId) {
        if (mMhManager != null) {
            mMhManager.setSpeciallyEffect(teXiaoId);
            mTeXiaoId = teXiaoId;
        }
    }



    /**
     * 滤镜
     */
    public void setFilter(int filterId) {
        if (mMhManager != null) {
            mMhManager.setFilter(filterId);
            mFilterId = filterId;
        }
    }

    public void setMakeup(int makeupId,boolean enable) {
        if (mMhManager != null) {

            switch (makeupId){
                case MHSDK.MAKEUP_NONE:
                    mMakeupLipstick = false;
                    mMakeupEyelash = false;
                    mMakeupEyeliner = false;
                    mMakeupEyebrow = false;
                    mMakeupBlush = false;
                    break;
                case MHSDK.MAKEUP_LIPSTICK:
                    mMakeupLipstick = enable;
                    break;
                case MHSDK.MAKEUP_EYELASH:
                    mMakeupEyelash = enable;
                    break;
                case MHSDK.MAKEUP_EYELINER:
                    mMakeupEyeliner = enable;
                    break;
                case MHSDK.MAKEUP_EYEBROW:
                    mMakeupEyebrow = enable;
                    break;
                case MHSDK.MAKEUP_BLUSH:
                    mMakeupBlush = enable;
                    break;
            }

            mMhManager.setMakeup(makeupId,enable);
        }
    }


    private void notifyLiangDuChanged() {
        if (mMhManager == null || mInputValue == null || mUseValue == null) {
            return;
        }
        //亮度
        if (mInputValue.getLiangDu() != mUseValue.getLiangDu()) {
            mInputValue.setLiangDu(mUseValue.getLiangDu());
            mMhManager.setBrightness(mUseValue.getLiangDu());
        }
    }

    public void notifyMeiYanChanged() {
        if (mMhManager == null || mInputValue == null || mUseValue == null) {
            return;
        }
        MeiYanValueBean input = mInputValue;
        MeiYanValueBean use = mUseValue;

        if (mMeiYanChangedListener != null) {
            boolean meiBaiChanged = false;
            boolean moPiChanged = false;
            boolean hongRunChanged = false;
            if (input.getMeiBai() != use.getMeiBai()) {
                input.setMeiBai(use.getMeiBai());
                meiBaiChanged = true;
            }
            if (input.getMoPi() != use.getMoPi()) {
                input.setMoPi(use.getMoPi());
                moPiChanged = true;
            }
            if (input.getHongRun() != use.getHongRun()) {
                input.setHongRun(use.getHongRun());
                hongRunChanged = true;
            }
            mMeiYanChangedListener.onMeiYanChanged(input.getMeiBai(), meiBaiChanged, input.getMoPi(), moPiChanged, input.getHongRun(), hongRunChanged);

        } else {
            //美白
            if (input.getMeiBai() != use.getMeiBai()) {
                input.setMeiBai(use.getMeiBai());
                mMhManager.setSkinWhiting(input.getMeiBai());
            }
            //磨皮
            if (input.getMoPi() != use.getMoPi()) {
                input.setMoPi(use.getMoPi());
                mMhManager.setSkinSmooth(input.getMoPi());

            }
            //红润
            if (input.getHongRun() != use.getHongRun()) {
                input.setHongRun(use.getHongRun());
                mMhManager.setSkinTenderness(input.getHongRun());
            }
        }

        //大眼
        if (input.getDaYan() != use.getDaYan()) {
            input.setDaYan(use.getDaYan());
            mMhManager.setBigEye(input.getDaYan());
        }
        //眉毛
        if (input.getMeiMao() != use.getMeiMao()) {
            input.setMeiMao(use.getMeiMao());
            mMhManager.setEyeBrow(input.getMeiMao());
        }
        //眼距
        if (input.getYanJu() != use.getYanJu()) {
            input.setYanJu(use.getYanJu());
            mMhManager.setEyeLength(input.getYanJu());
        }
        //眼角
        if (input.getYanJiao() != use.getYanJiao()) {
            input.setYanJiao(use.getYanJiao());
            mMhManager.setEyeCorner(input.getYanJiao());
        }
        //瘦脸
        if (input.getShouLian() != use.getShouLian()) {
            input.setShouLian(use.getShouLian());
            int shouLian = (int)(input.getShouLian()*0.5f);
//            int shouLian = (int)(input.getShouLian());
            mMhManager.setFaceLift(shouLian);
        }
        //嘴型
        if (input.getZuiXing() != use.getZuiXing()) {
            input.setZuiXing(use.getZuiXing());
            mMhManager.setMouseLift(input.getZuiXing());
        }
        //瘦鼻
        if (input.getShouBi() != use.getShouBi()) {
            input.setShouBi(use.getShouBi());
            mMhManager.setNoseLift(input.getShouBi());
        }
        //下巴
        if (input.getXiaBa() != use.getXiaBa()) {
            input.setXiaBa(use.getXiaBa());
            mMhManager.setChinLift(input.getXiaBa());
        }
        //额头
        if (input.getETou() != use.getETou()) {
            input.setETou(use.getETou());
            mMhManager.setForeheadLift(input.getETou());
        }
        //长鼻
        if (input.getChangBi() != use.getChangBi()) {
            input.setChangBi(use.getChangBi());
            mMhManager.setLengthenNoseLift(input.getChangBi());
        }
        //削脸
        if (input.getXueLian() != use.getXueLian()) {
            input.setXueLian(use.getXueLian());
            mMhManager.setFaceShave(input.getXueLian());
        }
        //开眼角
        if (input.getKaiYanJiao() != use.getKaiYanJiao()) {
            input.setKaiYanJiao(use.getKaiYanJiao());
            mMhManager.setEyeAlat(input.getKaiYanJiao());
        }
    }



    /**
     * 获取贴纸列表
     */
//    public static void getTieZhiList(int id, final CommonCallback<String> commonCallback) {
//        MHSDK.getTieZhiList(id, new MHSDK.TieZhiListCallback() {
//            @Override
//            public void getTieZhiList(String data) {
//                if (commonCallback != null) {
//                    commonCallback.callback(data);
//                }
//            }
//        });
//    }

    /**
     * 获取贴纸列表
     */
    public static void getTieZhiList(String url, final CommonCallback<String> commonCallback) {
        MHSDK.getTieZhiList(url, new MHSDK.TieZhiListCallback() {
            @Override
            public void getTieZhiList(String data) {
                if (commonCallback != null) {
                    commonCallback.callback(data);
                }
            }
        });
    }

    /**
     * 下载贴纸
     */
    public static void downloadTieZhi(String tieZhiName,String resource, final CommonCallback<Boolean> commonCallback) {
        MHSDK.downloadSticker(tieZhiName,resource, new MHSDK.TieZhiDownloadCallback() {
            @Override
            public void tieZhiDownload(String tieZhiName, boolean success) {
                if (success) {
                    if (commonCallback != null) {
                        commonCallback.callback(true);
                    }
                } else {
                    if (commonCallback != null) {
                        commonCallback.callback(false);
                    }
                }
            }
        });

    }


    /**
     * 贴纸是否下载了
     */
    public static boolean isTieZhiDownloaded(String name) {
        return MHSDK.isTieZhiDownloaded(name);
    }


}
