package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefStudentIdOcrInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.StudentIdOcr;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

public class StudentIdOcrAlgorithmTask extends AlgorithmTask<StudentIdOcrAlgorithmTask.StudentIdOcrResourceProvider, BefStudentIdOcrInfo> {

    public static final AlgorithmTaskKey STUDENT_ID_OCR = AlgorithmTaskKeyFactory.create("student_id_ocr", true);

    private final StudentIdOcr mDetector;
    public StudentIdOcrAlgorithmTask(Context context, StudentIdOcrResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new StudentIdOcr();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        int ret = mDetector.init(mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.STUDENT_ID_OCR));
        if (!checkResult("init student_id_ocr", ret)) return ret;
        ret = mDetector.setModel(EffectsSDKEffectConstants.StudentIdOcrModelType.BEF_STUDENT_ID_OCR_MODEL, mResourceProvider.studentIdOcrModel());
        if (!checkResult("init student_id_ocr", ret)) return ret;
        return ret;
    }

    @Override
    public BefStudentIdOcrInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("student_id_ocr");
        BefStudentIdOcrInfo info = mDetector.detect(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("student_id_ocr");
        return info;
    }

    @Override
    public int destroyTask() {
        mDetector.release();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[0];
    }

    @Override
    public AlgorithmTaskKey key() {
        return null;
    }

    public interface StudentIdOcrResourceProvider extends AlgorithmResourceProvider {
        String studentIdOcrModel();
    }
}
