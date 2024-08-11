package com.effectsar.labcv.core.sport;

import android.content.Context;

import com.effectsar.labcv.core.Config;
import com.effectsar.labcv.core.ResourceHelper;
import com.effectsar.labcv.core.algorithm.ActionRecognitionAlgorithmTask;

import java.io.File;
import java.util.Objects;

public class ActionRecognitionResourceHelper extends ResourceHelper implements ActionRecognitionAlgorithmTask.ActionRecognitionResourceProvider {

    public ActionRecognitionResourceHelper(Context mContext) {
        super(mContext);
    }

    @Override
    public String actionRecognitionModelPath() {
        return null;
    }

    @Override
    public String templateForActionType(ActionRecognitionAlgorithmTask.ActionType actionType) {
        return null;
    }

}
