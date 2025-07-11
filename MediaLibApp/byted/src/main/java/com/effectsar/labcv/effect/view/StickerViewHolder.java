package com.effectsar.labcv.effect.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.effectsar.labcv.common.utils.DensityUtils;
import com.effectsar.labcv.R;
import com.effectsar.labcv.effect.view.DownloadView.DownloadState;

public class StickerViewHolder extends RecyclerView.ViewHolder{

    private final int drawableSelected = com.effectsar.labcv.R.drawable.bg_item_focused;
    private final int drawableUnselected = com.effectsar.labcv.R.drawable.bg_item_unselect_selector;

    private final LinearLayout llContent;
    private final RelativeLayout rl;
    private final ImageView iv;
    private final DownloadView dpv;
    private final TextView tv;

    private final int colorOn;
    private final int colorOff;
    private boolean isOn = false;

    public StickerViewHolder(@NonNull View itemView) {
        super(itemView);

        llContent = itemView.findViewById(R.id.ll_content);
        rl = itemView.findViewById(R.id.rl_item);
        iv = itemView.findViewById(R.id.iv_icon);
        dpv = itemView.findViewById(R.id.dpv);
        tv = itemView.findViewById(R.id.tv_title);

        iv.setBackgroundResource(R.drawable.bg_undownloaded_item);
        dpv.bringToFront();

        colorOn = ActivityCompat.getColor(itemView.getContext(), com.effectsar.labcv.R.color.colorWhite);
        colorOff = ActivityCompat.getColor(itemView.getContext(), com.effectsar.labcv.R.color.colorGrey);
    }

    public void change(boolean on) {
        if (on) {
            on();
        } else {
            off();
        }
    }

    public void on() {
        isOn = true;
        tv.setTextColor(colorOn);
        rl.setBackgroundResource(drawableSelected);
    }

    public void off() {
        isOn = false;
        tv.setTextColor(colorOff);
        rl.setBackgroundResource(drawableUnselected);
    }

    public void setIcon(String iconUrl) {
        Glide.with(iv).load(iconUrl).apply(RequestOptions.bitmapTransform(new RoundedCorners((int) DensityUtils.dp2px(itemView.getContext(), 2)))).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                iv.setBackgroundResource(0);
                iv.setImageDrawable(resource);
            }
        });
    }

    public void setIcon(int iconId) {
        Glide.with(iv).load(iconId).apply(RequestOptions.bitmapTransform(new RoundedCorners((int) DensityUtils.dp2px(itemView.getContext(), 2)))).into(iv);
    }

    public void setTitle(String title) {
        if (title.isEmpty()) {
            tv.setVisibility(GONE);
        } else {
            tv.setVisibility(VISIBLE);
            tv.setText(title);
        }
    }

    public void setState(DownloadState status){
        dpv.setState(status);
    }

    public void setProgress(float progress){
        dpv.setProgress(progress);
    }

}
