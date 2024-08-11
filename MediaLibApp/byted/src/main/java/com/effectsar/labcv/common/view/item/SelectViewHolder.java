package com.effectsar.labcv.common.view.item;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.effectsar.labcv.R;
import com.effectsar.labcv.common.utils.DensityUtils;
import com.effectsar.labcv.common.view.MarqueeTextView;
import com.effectsar.labcv.resource.MaterialResource;

public class SelectViewHolder extends BaseViewHolder {

    private final int drawableSelected = R.drawable.bg_item_focused;
    private final int drawableUnselected = R.drawable.bg_item_unselect_selector;

    private final int colorOn;
    private final int colorOff;

    private final LinearLayout llBackground;

    public SelectViewHolder(View itemView) {
        super(itemView);

        llBackground = itemView.findViewById(R.id.ll_select_background);
        colorOn = ActivityCompat.getColor(itemView.getContext(), R.color.colorWhite);
        colorOff = ActivityCompat.getColor(itemView.getContext(), R.color.colorGrey);
    }

    public void setIcon(int iconResource) {
        Glide.with(iv).load(iconResource).apply(RequestOptions.bitmapTransform(new RoundedCorners((int) DensityUtils.dp2px(itemView.getContext(), 2)))).into(iv);
    }

    @Override
    public void changeToFocused(int position, MaterialResource material) {
        tvTitle.setTextColor(colorOn);
        llBackground.setBackgroundResource(drawableSelected);
    }

    @Override
    public void changeToUnfocused(int position, MaterialResource material) {
        tvTitle.setTextColor(colorOff);
        llBackground.setBackgroundResource(drawableUnselected);
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
