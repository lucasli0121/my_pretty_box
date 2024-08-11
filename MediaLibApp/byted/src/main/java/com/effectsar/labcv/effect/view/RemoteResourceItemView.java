package com.effectsar.labcv.effect.view;

import android.content.Context;

import androidx.annotation.Nullable;

import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.effectsar.labcv.R;

public class RemoteResourceItemView extends RelativeLayout {

    private final String TAG = "ResourceItemView";

    public ImageView iv_icon;
//    public RemoteResourceDownloadProgressView dpv;
    //    ImageView iv_undownload;

    public RemoteResourceItemView(Context context) {
        super(context);
        iv_icon = findViewById(R.id.iv_icon);
//        dpv = findViewById(R.id.dpv);
//        dpv.setVisibility(View.INVISIBLE);
    }

    public void setProgress(float progress){
//        dpv.setProgress(progress);
    }

}
