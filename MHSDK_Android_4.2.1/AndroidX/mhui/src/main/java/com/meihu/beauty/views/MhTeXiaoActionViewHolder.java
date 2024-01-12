package com.meihu.beauty.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meihu.beauty.R;
import com.meihu.beauty.adapter.MhTeXiaoActionAdapter;
import com.meihu.beauty.bean.TeXiaoActionBean;
import com.meihu.beauty.bean.TieZhiBean;
import com.meihu.beauty.interfaces.CommonCallback;
import com.meihu.beauty.interfaces.OnItemClickListener;
import com.meihu.beauty.interfaces.OnTieZhiActionClickListener;
import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beauty.utils.ToastUtil;
import com.meihu.beautylibrary.MHSDK;
import com.meihu.beautylibrary.bean.MHCommonBean;
import com.meihu.beautylibrary.bean.MHConfigConstants;
import com.meihu.beautylibrary.manager.MHBeautyManager;
import com.meihu.beautylibrary.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

public class MhTeXiaoActionViewHolder extends MhTeXiaoChildViewHolder implements OnItemClickListener<TeXiaoActionBean> {

    private final  String  TAG = MhTeXiaoActionViewHolder.class.getName();
    private  MhTeXiaoActionAdapter adapter;

    private volatile int mSelectPos = -1;

    private volatile String mStickerName;

    public MhTeXiaoActionViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init() {

        List<MHCommonBean> beans = new ArrayList<>();

        beans.add(new TeXiaoActionBean(R.string.beauty_mh_texiao_action_no, R.mipmap.ic_texiao_action_no_0, R.mipmap.ic_texiao_action_no_1, "",MHSDK.TE_XIAO_ACTION_NONE,MHConfigConstants.TE_XIAO_DONG_ZUO_YUAN_TU,""));
        beans.add(new TeXiaoActionBean(R.string.beauty_mh_texiao_action_head, R.mipmap.ic_texiao_action_taitou_0, R.mipmap.ic_texiao_action_taitou_1,"",MHSDK.TE_XIAO_ACTION_TAI_TOU,MHConfigConstants.TE_XIAO_DONG_ZUO_TAI_TOU,"face_057"));
        beans.add(new TeXiaoActionBean(R.string.beauty_mh_texiao_action_mouth, R.mipmap.ic_texiao_action_zhangzui_0, R.mipmap.ic_texiao_action_zhangzui_1,"",MHSDK.TE_XIAO_ACTION_ZHANG_ZUI,MHConfigConstants.TE_XIAO_DONG_ZUO_ZHANG_ZUI,"face_053"));
        beans.add(new TeXiaoActionBean(R.string.beauty_mh_texiao_action_eye, R.mipmap.ic_texiao_action_zhayan_0, R.mipmap.ic_texiao_action_zhayan_1,"",MHSDK.TE_XIAO_ACTION_ZHA_YAN,MHConfigConstants.TE_XIAO_DONG_ZUO_ZHA_YAN,"face_056"));

        beans  = MHSDK.getFunctionItems(beans,MHConfigConstants.TE_XIAO,MHConfigConstants.TE_XIAO_DONG_ZUO_FUNCTION);

        List<TeXiaoActionBean> list = new ArrayList<>();
        for (int i = 0; i < beans.size(); i++) {
            TeXiaoActionBean bean = (TeXiaoActionBean)beans.get(i);
            list.add(bean);
        }

        RecyclerView recyclerView = (RecyclerView) mContentView;
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        adapter =  new MhTeXiaoActionAdapter(mContext, list);
        adapter.setOnItemClickListener(this);
        adapter.setOnTieZhiActionClickListener(new OnTieZhiActionClickListener() {
            @Override
            public void OnTieZhiActionClick(int action) {
                if (mOnTieZhiActionClickListener != null){
                    mOnTieZhiActionClickListener.OnTieZhiActionClick(action);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void setItemClick(int postion){
        if (adapter != null){
            adapter.setItemClick(postion);
        }else{
            Log.e(TAG, "setItemClick: ");
        }
    }

    @Override
    public void onItemClick(TeXiaoActionBean bean, int position) {

        if (mSelectPos != position){
            if (mOnTieZhiActionDownloadListener != null){
                mOnTieZhiActionDownloadListener.OnTieZhiActionDownload(1);
            }
            if (mOnTieZhiActionListener != null){
                mOnTieZhiActionListener.OnTieZhiAction(0);
            }
            mSelectPos = position;
        }
        String stickerName = bean.getStickerName();
        mStickerName = stickerName;
        if (position == 0 || MhDataManager.isTieZhiDownloaded(stickerName)){
            setTieZhi(bean,stickerName);
        }else{
            requestSticker(bean);
        }

    }

    private void requestSticker(final TeXiaoActionBean bean){
        if (mOnTieZhiActionDownloadListener != null){
            mOnTieZhiActionDownloadListener.OnTieZhiActionDownload(0);
        }
        String motionListUrl = UrlUtils.getMotionListUrl();
        MhDataManager.getTieZhiList(motionListUrl, new CommonCallback<String>() {
            @Override
            public void callback(String jsonStr) {
                if (TextUtils.isEmpty(jsonStr)) {
                    return;
                }
                try {
                    if (!mStickerName.equals(bean.getStickerName())){
                        return;
                    }
                    JSONObject obj = JSON.parseObject(jsonStr);
                    List<TieZhiBean> list = JSON.parseArray(obj.getString("list"), TieZhiBean.class);
                    if (list != null){
                        for (TieZhiBean item:list){
                            if (bean.getStickerName().equals(item.getName())){
                                String name = item.getName();
                                String resouce = item.getResource();
                                bean.setStickerName(name);
                                bean.setResouce(resouce);
                                downloadSticker(bean);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void downloadSticker(final TeXiaoActionBean bean){
        

        final String stickerName = bean.getStickerName();
        final String resource = bean.getResouce();
        MhDataManager.downloadTieZhi(stickerName,resource, new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean isSuccess) {
                if (!mStickerName.equals(bean.getStickerName())){
                    return;
                }
                if (isSuccess) {
                    setTieZhi(bean,stickerName);
                } else {
                    if (mOnTieZhiActionDownloadListener != null){
                        mOnTieZhiActionDownloadListener.OnTieZhiActionDownload(1);
                    }
                    ToastUtil.show(R.string.beauty_mh_009);
                }
            }
        });
    }

    private void setTieZhi(TeXiaoActionBean bean,String stickerName){
        if(bean == null || TextUtils.isEmpty(bean.getStickerName())){
            enableUseFace(null);
            MhDataManager.getInstance().setTieZhi(null,null);
        }else{
            enableUseFace(stickerName);
            MhDataManager.getInstance().setTieZhi(stickerName,bean.getAction());
        }
        if (mOnTieZhiActionDownloadListener != null){
            mOnTieZhiActionDownloadListener.OnTieZhiActionDownload(1);
        }
        if (mOnTieZhiActionListener != null){
            mOnTieZhiActionListener.OnTieZhiAction(bean.getAction());
        }
    }

    private void enableUseFace(String stickerName){
        int useFace;
        if(TextUtils.isEmpty(stickerName)){
            useFace = 0;
        }else{
            useFace = 1;
        }
        MHBeautyManager mhBeautyManager =  MhDataManager.getInstance().getMHBeautyManager();
        if (mhBeautyManager != null){
            int[]  useFaces = mhBeautyManager.getUseFaces();
            useFaces[4] = useFace;
            mhBeautyManager.setUseFaces(useFaces);
        }
    }

}
