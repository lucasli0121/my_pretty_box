package com.effectsar.labcv.common.view.item;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.effectsar.labcv.R;
import com.effectsar.labcv.common.utils.DensityUtils;
import com.effectsar.labcv.resource.MaterialResource;

public class SelectOnlineViewHolder extends BaseOnlineViewHolder {

    private final int drawableSelected = R.drawable.bg_item_focused;
    private final int drawableUnselected = R.drawable.bg_item_unselect_selector;

    private final int colorOn;
    private final int colorOff;

    private final RelativeLayout rlItem;

    public SelectOnlineViewHolder(View itemView) {
        super(itemView);

        rlItem = itemView.findViewById(R.id.rl_item);
        colorOn = ActivityCompat.getColor(itemView.getContext(), R.color.colorWhite);
        colorOff = ActivityCompat.getColor(itemView.getContext(), R.color.colorGrey);
    }

    public void setIcon(int iconResource) {
        setIconO(iconResource);
    }

    private void setIconO(Object model){
        iv.setTag(model);
        // fix icon flash when multiple items downloading
        // iv.setImageDrawable(null);
        RequestOptions requestOptions = RequestOptions.bitmapTransform(new RoundedCorners((int) DensityUtils.dp2px(itemView.getContext(), 2)));
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(iv)
                .load(model)
                .apply(requestOptions).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                        Object tag = iv.getTag();
                        if (tag != null && tag == model) iv.setImageDrawable(drawable);
                    }
                });
    }

    @Override
    public void changeToFocused(int position, MaterialResource material) {
        tvTitle.setTextColor(colorOn);
        rlItem.setBackgroundResource(drawableSelected);
    }

    @Override
    public void changeToUnfocused(int position, MaterialResource material) {
        tvTitle.setTextColor(colorOff);
        rlItem.setBackgroundResource(drawableUnselected);
    }

    @Override
    public void setIcon(String iconUrl) {
        setIconO(iconUrl);
    }

    @Override
    public void changeToPointOn() {
        //   {zh} 风格妆中会修改drawable缓存的填充色，此处使用单独的固定颜色的drawable       {en} Style makeup will modify the fill color of the drawable cache, here use a separate fixed color drawable
        vPoint.setBackgroundResource(R.drawable.dot_point_blue);
    }

    @Override
    public void changeToPointOff() {
        vPoint.setBackgroundResource(0);
    }


}
