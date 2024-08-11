package com.effectsar.labcv.common.mediapicker.data;

import com.effectsar.labcv.common.mediapicker.entity.Folder;

import java.util.ArrayList;


/**
 * Created by dmcBig on 2017/7/3.
 */

public interface DataCallback {


    void onData(ArrayList<Folder> list);

}
