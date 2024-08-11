package com.effectsar.labcv.common.view.item;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.effectsar.labcv.R;
import com.effectsar.labcv.resource.MaterialResource;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder  {

    public LinearLayout llContent;
    public ImageView iv;
    public TextView tvTitle;
    public View vPoint;

    private boolean isFocused = false;
    private boolean isPointOn = false;

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);

        llContent = itemView.findViewById(R.id.ll_content);
        iv = itemView.findViewById(R.id.iv_icon);
        tvTitle = itemView.findViewById(R.id.tv_title);
        vPoint = itemView.findViewById(R.id.v_face_options);
    }

    public void setTitle(String title){
        if (title.isEmpty()) {
            tvTitle.setVisibility(GONE);
        } else {
            tvTitle.setVisibility(VISIBLE);
            tvTitle.setText(title);
        }
    }

    public void setTitleId(int titleId){
        tvTitle.setVisibility(VISIBLE);
        tvTitle.setText(titleId);
    }

    public void setFocused(boolean focused, int position, MaterialResource material) {
        isFocused = focused;
        if (focused) {
            changeToFocused(position,material);
        } else {
            changeToUnfocused(position,material);
        }
    }

    public boolean isFocused() {
        return isFocused;
    }

    public void setPointOn(boolean pointOn){
        isPointOn = pointOn;
        if (pointOn) {
            changeToPointOn();
        } else {
            changeToPointOff();
        }
    }

    public boolean isPointOn() {
        return isPointOn;
    }

    public abstract void setIcon(int iconResource);

    public abstract void changeToFocused(int position, MaterialResource material);

    public abstract void changeToUnfocused(int position, MaterialResource material);

    public abstract void changeToPointOn();

    public abstract void changeToPointOff();

}
