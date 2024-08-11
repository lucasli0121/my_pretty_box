package com.effectsar.labcv.core.algorithm.factory;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.AvaBoostAlgorithmTask;
import com.effectsar.labcv.core.algorithm.BachSkeletonAlgorithmTask;
import com.effectsar.labcv.core.algorithm.C1AlgorithmTask;
import com.effectsar.labcv.core.algorithm.C2AlgorithmTask;
import com.effectsar.labcv.core.algorithm.CarAlgorithmTask;
import com.effectsar.labcv.core.algorithm.ChromaKeyingAlgorithmTask;
import com.effectsar.labcv.core.algorithm.ConcentrateAlgorithmTask;
import com.effectsar.labcv.core.algorithm.DynamicGestureAlgorithmTask;
import com.effectsar.labcv.core.algorithm.FaceAlgorithmTask;
import com.effectsar.labcv.core.algorithm.FaceClusterAlgorithmTask;
import com.effectsar.labcv.core.algorithm.FaceFittingAlgorithmTask;
import com.effectsar.labcv.core.algorithm.FaceVerifyAlgorithmTask;
import com.effectsar.labcv.core.algorithm.GazeEstimationAlgorithmTask;
import com.effectsar.labcv.core.algorithm.HairParserAlgorithmTask;
import com.effectsar.labcv.core.algorithm.HandAlgorithmTask;
import com.effectsar.labcv.core.algorithm.HeadSegAlgorithmTask;
import com.effectsar.labcv.core.algorithm.HumanDistanceAlgorithmTask;
import com.effectsar.labcv.core.algorithm.LicenseCakeAlgorithmTask;
import com.effectsar.labcv.core.algorithm.LightClsAlgorithmTask;
import com.effectsar.labcv.core.algorithm.ObjectTrackingAlgorithmTask;
import com.effectsar.labcv.core.algorithm.PetFaceAlgorithmTask;
import com.effectsar.labcv.core.algorithm.PortraitMattingAlgorithmTask;
import com.effectsar.labcv.core.algorithm.SaliencyMattingAlgorithmTask;
import com.effectsar.labcv.core.algorithm.Skeleton3DAlgorithmTask;
import com.effectsar.labcv.core.algorithm.SkinSegmentationAlgorithmTask;
import com.effectsar.labcv.core.algorithm.SkySegAlgorithmTask;
import com.effectsar.labcv.core.algorithm.SkeletonAlgorithmTask;
import com.effectsar.labcv.core.algorithm.SlamAlgorithmTask;
import com.effectsar.labcv.core.algorithm.StudentIdOcrAlgorithmTask;
import com.effectsar.labcv.core.algorithm.VideoClsAlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.ActionRecognitionAlgorithmTask;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.effectsdk.Skeleton3dDetect;

import java.util.HashMap;
import java.util.Map;

public class AlgorithmTaskFactory {
    private static final Map<AlgorithmTaskKey, AlgorithmTaskGenerator<AlgorithmResourceProvider>> sRegister = new HashMap<>();

    static {
        AlgorithmTaskFactory.register(FaceAlgorithmTask.FACE, new AlgorithmTaskFactory.AlgorithmTaskGenerator<FaceAlgorithmTask.FaceResourceProvider>() {
            @Override
            public AlgorithmTask<FaceAlgorithmTask.FaceResourceProvider, ?> create(Context context, FaceAlgorithmTask.FaceResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new FaceAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(HeadSegAlgorithmTask.HEAD_SEGMENT, new AlgorithmTaskGenerator<HeadSegAlgorithmTask.HeadSegResourceProvider>() {

            @Override
            public AlgorithmTask<HeadSegAlgorithmTask.HeadSegResourceProvider, ?> create(Context context, HeadSegAlgorithmTask.HeadSegResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new HeadSegAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(HairParserAlgorithmTask.HAIR_PARSER, new AlgorithmTaskGenerator<HairParserAlgorithmTask.HairParserResourceProvider>() {
            @Override
            public AlgorithmTask<HairParserAlgorithmTask.HairParserResourceProvider, ?> create(Context context, HairParserAlgorithmTask.HairParserResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new HairParserAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(FaceVerifyAlgorithmTask.FACE_VERIFY, new AlgorithmTaskGenerator<FaceVerifyAlgorithmTask.FaceVerifyResourceProvider>() {
            @Override
            public AlgorithmTask<FaceVerifyAlgorithmTask.FaceVerifyResourceProvider, ?> create(Context context, FaceVerifyAlgorithmTask.FaceVerifyResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new FaceVerifyAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(HandAlgorithmTask.HAND, new AlgorithmTaskGenerator<HandAlgorithmTask.HandResourceProvider>() {
            @Override
            public AlgorithmTask<HandAlgorithmTask.HandResourceProvider, ?> create(Context context, HandAlgorithmTask.HandResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new HandAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(SkeletonAlgorithmTask.SKELETON, new AlgorithmTaskGenerator<SkeletonAlgorithmTask.SkeletonResourceProvider>() {
            @Override
            public AlgorithmTask<SkeletonAlgorithmTask.SkeletonResourceProvider, ?> create(Context context, SkeletonAlgorithmTask.SkeletonResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new SkeletonAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(PetFaceAlgorithmTask.PET_FACE, new AlgorithmTaskGenerator<PetFaceAlgorithmTask.PetFaceResourceProvider>() {
            @Override
            public AlgorithmTask<PetFaceAlgorithmTask.PetFaceResourceProvider, ?> create(Context context, PetFaceAlgorithmTask.PetFaceResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new PetFaceAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(SkySegAlgorithmTask.SKY_SEGMENT, new AlgorithmTaskGenerator<SkySegAlgorithmTask.SkeSegResourceProvider>() {
            @Override
            public AlgorithmTask<SkySegAlgorithmTask.SkeSegResourceProvider, ?> create(Context context, SkySegAlgorithmTask.SkeSegResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new SkySegAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(LightClsAlgorithmTask.LIGHT_CLS, new AlgorithmTaskGenerator<LightClsAlgorithmTask.LightClsResourceProvider>() {
            @Override
            public AlgorithmTask<LightClsAlgorithmTask.LightClsResourceProvider, ?> create(Context context, LightClsAlgorithmTask.LightClsResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new LightClsAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(HumanDistanceAlgorithmTask.HUMAN_DISTANCE, new AlgorithmTaskGenerator<HumanDistanceAlgorithmTask.HumanDistanceResourceProvider>() {
            @Override
            public AlgorithmTask<HumanDistanceAlgorithmTask.HumanDistanceResourceProvider, ?> create(Context context, HumanDistanceAlgorithmTask.HumanDistanceResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new HumanDistanceAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(ConcentrateAlgorithmTask.CONCENTRATION, new AlgorithmTaskGenerator<ConcentrateAlgorithmTask.ConcentrateResourceProvider>() {
            @Override
            public AlgorithmTask<ConcentrateAlgorithmTask.ConcentrateResourceProvider, ?> create(Context context, ConcentrateAlgorithmTask.ConcentrateResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new ConcentrateAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(GazeEstimationAlgorithmTask.GAZE_ESTIMATION, new AlgorithmTaskGenerator<GazeEstimationAlgorithmTask.GazeEstimationResourceProvider>() {
            @Override
            public AlgorithmTask<GazeEstimationAlgorithmTask.GazeEstimationResourceProvider, ?> create(Context context, GazeEstimationAlgorithmTask.GazeEstimationResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new GazeEstimationAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(C1AlgorithmTask.C1, new AlgorithmTaskGenerator<C1AlgorithmTask.C1ResourceProvider>() {
            @Override
            public AlgorithmTask<C1AlgorithmTask.C1ResourceProvider, ?> create(Context context, C1AlgorithmTask.C1ResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new C1AlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(C2AlgorithmTask.C2, new AlgorithmTaskGenerator<C2AlgorithmTask.C2ResourceProvider>() {
            @Override
            public AlgorithmTask<C2AlgorithmTask.C2ResourceProvider, ?> create(Context context, C2AlgorithmTask.C2ResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new C2AlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(VideoClsAlgorithmTask.VIDEO_CLS, new AlgorithmTaskGenerator<VideoClsAlgorithmTask.VideoClsResourceProvider>() {
            @Override
            public AlgorithmTask<VideoClsAlgorithmTask.VideoClsResourceProvider, ?> create(Context context, VideoClsAlgorithmTask.VideoClsResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new VideoClsAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(FaceClusterAlgorithmTask.FACE_CLUSTER, new AlgorithmTaskGenerator<AlgorithmResourceProvider>() {
            @Override
            public AlgorithmTask<AlgorithmResourceProvider, ?> create(Context context, AlgorithmResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new FaceClusterAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(CarAlgorithmTask.CAR_ALGO, new AlgorithmTaskGenerator<CarAlgorithmTask.CarResourceProvider>() {
            @Override
            public AlgorithmTask<CarAlgorithmTask.CarResourceProvider, ?> create(Context context, CarAlgorithmTask.CarResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new CarAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(StudentIdOcrAlgorithmTask.STUDENT_ID_OCR, new AlgorithmTaskGenerator<StudentIdOcrAlgorithmTask.StudentIdOcrResourceProvider>() {
            @Override
            public AlgorithmTask<StudentIdOcrAlgorithmTask.StudentIdOcrResourceProvider, ?> create(Context context, StudentIdOcrAlgorithmTask.StudentIdOcrResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new StudentIdOcrAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(PortraitMattingAlgorithmTask.PORTRAIT_MATTING, new AlgorithmTaskGenerator<PortraitMattingAlgorithmTask.PortraitMattingResourceProvider>() {
            @Override
            public AlgorithmTask<PortraitMattingAlgorithmTask.PortraitMattingResourceProvider, ?> create(Context context, PortraitMattingAlgorithmTask.PortraitMattingResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new PortraitMattingAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(SaliencyMattingAlgorithmTask.SALIENCY_MATTING, new AlgorithmTaskGenerator<SaliencyMattingAlgorithmTask.SaliencyMattingResourceProvider>() {
            @Override
            public AlgorithmTask<SaliencyMattingAlgorithmTask.SaliencyMattingResourceProvider, ?> create(Context context, SaliencyMattingAlgorithmTask.SaliencyMattingResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new SaliencyMattingAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(ActionRecognitionAlgorithmTask.ACTION_RECOGNITION, new AlgorithmTaskGenerator<ActionRecognitionAlgorithmTask.ActionRecognitionResourceProvider>() {
            @Override
            public AlgorithmTask<ActionRecognitionAlgorithmTask.ActionRecognitionResourceProvider, ?> create(Context context, ActionRecognitionAlgorithmTask.ActionRecognitionResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new ActionRecognitionAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(DynamicGestureAlgorithmTask.DYNAMIC_GESTURE, new AlgorithmTaskGenerator<DynamicGestureAlgorithmTask.DynamicGestureResourceProvider>() {
            @Override
            public AlgorithmTask<DynamicGestureAlgorithmTask.DynamicGestureResourceProvider, ?> create(Context context, DynamicGestureAlgorithmTask.DynamicGestureResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new DynamicGestureAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(LicenseCakeAlgorithmTask.LICENSE_CAKE, new AlgorithmTaskGenerator<LicenseCakeAlgorithmTask.LicenseCakeResourceProvider>() {
            @Override
            public AlgorithmTask<LicenseCakeAlgorithmTask.LicenseCakeResourceProvider, ?> create(Context context, LicenseCakeAlgorithmTask.LicenseCakeResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new LicenseCakeAlgorithmTask(context, provider, licenseProvider);
            }
        });

        register(SkinSegmentationAlgorithmTask.SKIN_SEGMENTATION, new AlgorithmTaskGenerator<SkinSegmentationAlgorithmTask.SkinSegmentationResourceProvider>() {
            @Override
            public AlgorithmTask<SkinSegmentationAlgorithmTask.SkinSegmentationResourceProvider, ?> create(Context context, SkinSegmentationAlgorithmTask.SkinSegmentationResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new SkinSegmentationAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(BachSkeletonAlgorithmTask.BACH_SKELETON, new AlgorithmTaskGenerator<BachSkeletonAlgorithmTask.BachSkeletonResourceProvider>() {
            @Override
            public AlgorithmTask<BachSkeletonAlgorithmTask.BachSkeletonResourceProvider, ?> create(Context context, BachSkeletonAlgorithmTask.BachSkeletonResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new BachSkeletonAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(ChromaKeyingAlgorithmTask.CHROMA_KEYING, new AlgorithmTaskGenerator<ChromaKeyingAlgorithmTask.ChromaKeyingResourceProvider>() {
            @Override
            public AlgorithmTask<ChromaKeyingAlgorithmTask.ChromaKeyingResourceProvider, ?> create(Context context, ChromaKeyingAlgorithmTask.ChromaKeyingResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new ChromaKeyingAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(SlamAlgorithmTask.SLAM, new AlgorithmTaskGenerator<SlamAlgorithmTask.SlamResourceProvider>() {
            @Override
            public AlgorithmTask<SlamAlgorithmTask.SlamResourceProvider, ?> create(Context context, SlamAlgorithmTask.SlamResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new SlamAlgorithmTask(context, provider, licenseProvider);
            }
        });

        register(FaceFittingAlgorithmTask.FACE_FITTING, new AlgorithmTaskGenerator<FaceFittingAlgorithmTask.FaceFittingResourceProvider>() {
            @Override
            public AlgorithmTask<FaceFittingAlgorithmTask.FaceFittingResourceProvider, ?> create(Context context, FaceFittingAlgorithmTask.FaceFittingResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new FaceFittingAlgorithmTask(context, provider, licenseProvider);
            }
        });

        register(Skeleton3DAlgorithmTask.SKELETON3D, new AlgorithmTaskGenerator<Skeleton3DAlgorithmTask.Skeleton3DResourceProvider>() {
            @Override
            public AlgorithmTask<Skeleton3DAlgorithmTask.Skeleton3DResourceProvider, ?> create(Context context, Skeleton3DAlgorithmTask.Skeleton3DResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new Skeleton3DAlgorithmTask(context, provider, licenseProvider);
            }
        });

        register(AvaBoostAlgorithmTask.AVABOOST, new AlgorithmTaskGenerator<AvaBoostAlgorithmTask.AvaBoostResourceProvider>() {
            @Override
            public AlgorithmTask<AvaBoostAlgorithmTask.AvaBoostResourceProvider, ?> create(Context context, AvaBoostAlgorithmTask.AvaBoostResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new AvaBoostAlgorithmTask(context, provider, licenseProvider);
            }
        });
        
        register(ObjectTrackingAlgorithmTask.OBJECT_TRACKING, new AlgorithmTaskGenerator<ObjectTrackingAlgorithmTask.ObjectTrackingResourceProvider>() {
            @Override
            public AlgorithmTask<ObjectTrackingAlgorithmTask.ObjectTrackingResourceProvider, ?> create(Context context, ObjectTrackingAlgorithmTask.ObjectTrackingResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new ObjectTrackingAlgorithmTask(context, provider, licenseProvider);
            }
        });
    }

    public static void register(AlgorithmTaskKey key, AlgorithmTaskGenerator generator) {
        sRegister.put(key, generator);
    }

    public static <T extends AlgorithmTask<AlgorithmResourceProvider, ?>> T
        create(AlgorithmTaskKey key, Context context, AlgorithmResourceProvider provider, EffectLicenseProvider licenseProvider) {
        AlgorithmTaskGenerator<AlgorithmResourceProvider> generator = sRegister.get(key);
        if (generator == null) {
            return null;
        }
        return (T) generator.create(context, provider, licenseProvider);
    }

    public interface AlgorithmTaskGenerator<T extends AlgorithmResourceProvider> {
        AlgorithmTask<T, ?> create(Context context, T provider, EffectLicenseProvider licenseProvider);
    }
}
