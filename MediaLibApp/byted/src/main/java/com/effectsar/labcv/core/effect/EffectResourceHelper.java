package com.effectsar.labcv.core.effect;

import android.content.Context;
import android.os.Environment;

import com.effectsar.labcv.core.Config;
import com.effectsar.labcv.core.ResourceHelper;
import com.effectsar.labcv.core.effect.EffectResourceProvider;

import java.io.File;

public class EffectResourceHelper extends ResourceHelper implements EffectResourceProvider {

    public EffectResourceHelper(Context mContext) {
        super(mContext);
    }

}
