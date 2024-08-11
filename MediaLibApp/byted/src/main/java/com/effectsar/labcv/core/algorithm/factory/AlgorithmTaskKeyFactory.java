package com.effectsar.labcv.core.algorithm.factory;

import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;

public class AlgorithmTaskKeyFactory {
    public static AlgorithmTaskKey create(String key, boolean isTask) {
        return new AlgorithmTaskKey(key, isTask);
    }

    public static AlgorithmTaskKey create(String key) {
        return new AlgorithmTaskKey(key, false);
    }
}
