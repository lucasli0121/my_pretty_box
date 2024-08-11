package com.effectsar.labcv.core.algorithm;

import android.content.Context;
import android.util.Log;

import com.effectsar.labcv.core.ResourceHelper;
import com.effectsar.labcv.core.util.LogUtils;

import java.io.File;

public class AlgorithmResourceHelper extends ResourceHelper implements FaceAlgorithmTask.FaceResourceProvider,
        HeadSegAlgorithmTask.HeadSegResourceProvider,
        HandAlgorithmTask.HandResourceProvider,
        SkeletonAlgorithmTask.SkeletonResourceProvider,
        PortraitMattingAlgorithmTask.PortraitMattingResourceProvider,
        C1AlgorithmTask.C1ResourceProvider,
        C2AlgorithmTask.C2ResourceProvider,
        CarAlgorithmTask.CarResourceProvider,
        ConcentrateAlgorithmTask.ConcentrateResourceProvider,
        FaceVerifyAlgorithmTask.FaceVerifyResourceProvider,
        GazeEstimationAlgorithmTask.GazeEstimationResourceProvider,
        HumanDistanceAlgorithmTask.HumanDistanceResourceProvider,
        LightClsAlgorithmTask.LightClsResourceProvider,
        PetFaceAlgorithmTask.PetFaceResourceProvider,
        VideoClsAlgorithmTask.VideoClsResourceProvider,
        StudentIdOcrAlgorithmTask.StudentIdOcrResourceProvider,
        SkySegAlgorithmTask.SkeSegResourceProvider,
        ActionRecognitionAlgorithmTask.ActionRecognitionResourceProvider,
        HairParserAlgorithmTask.HairParserResourceProvider,
        DynamicGestureAlgorithmTask.DynamicGestureResourceProvider,
        LicenseCakeAlgorithmTask.LicenseCakeResourceProvider,
        SkinSegmentationAlgorithmTask.SkinSegmentationResourceProvider,
        BachSkeletonAlgorithmTask.BachSkeletonResourceProvider,
        ChromaKeyingAlgorithmTask.ChromaKeyingResourceProvider,
        SlamAlgorithmTask.SlamResourceProvider,
        FaceFittingAlgorithmTask.FaceFittingResourceProvider,
        Skeleton3DAlgorithmTask.Skeleton3DResourceProvider,
        AvaBoostAlgorithmTask.AvaBoostResourceProvider,
        ObjectTrackingAlgorithmTask.ObjectTrackingResourceProvider,
        SaliencyMattingAlgorithmTask.SaliencyMattingResourceProvider
{
    public static final String RESOURCE = "resource";
    public static final String FACE = "ttfacemodel/algo_ggl1pqh_v11.1.model";
    public static final String PETFACE = "petfacemodel/algo_gglihg1pqh_v5.2.model";
    public static final String HAND_DETECT = "handmodel/algo_ggl0pcvlvhg_v11.0.model";
    public static final String HAND_BOX = "handmodel/algo_ggl0pcvl4tml7ha_v12.0.model";
    public static final String HAND_GESTURE = "handmodel/algo_ggl0pcvlahugk7hlgt4_v11.2.model";
    public static final String HAND_KEY_POINT = "handmodel/algo_ggl0pcvlsi_v6.0.model";
    public static final String HAND_SEGMENT = "handmodel/algo_ggl0pcvluha_v2.0.model";
    public static final String FACEEXTA = "ttfacemodel/algo_ggl1pqhlhmg7p_v14.0.model";
    public static final String FACEATTRI = "ttfaceattrmodel/algo_ggl1pqhlpgg754kghlgt4_v7.0.model";
    public static final String FACEVERIFY = "faceverifymodel/algo_ggl1pqhrh7516_v7.0.model";
    public static final String SKELETON = "skeleton_model/algo_gglush9hgtc_v7.0.model";
    public static final String SKELETON_3D = "avatar3d/algo_gglprpgp7bvug5qsh7_v4.0.model";
    public static final String PORTRAITMATTING = "mattingmodel/algo_ggl8pgg5ca_v15.0.model";
    public static final String HEADSEGMENT = "headsegmodel/algo_ggl0hpvuha_v6.0.model";
    public static final String HAIRPARSING = "hairparser/algo_ggl0p57_v11.0.model";
    public static final String LIGHTCLS = "lightcls/algo_ggl95a0gq9u_v1.0.model";
    public static final String HUMANDIST = "human_distance/algo_ggl0k8pcv5ug_v1.0.model";
    public static final String GENERAL_OBJECT_DETECT = "generalobjectmodel/algo_gglahch7p9lt43lvhghqg5tc_v1.0.model";
    public static final String GENERAL_OBJECT_CLS = "generalobjectmodel/algo_gglahch7p9lt43lvhghqg5tclq9u_v1.0.model";
    public static final String GENERAL_OBJECT_TRACK = "generalobjectmodel/algo_gglup8i9h_v1.0.model";
    public static final String C1 = "c1/algo_gglqjlu8p99_v8.0.model";
    public static final String C2 = "c2/";
    public static final String VIDEO_CLS = "videoclsmodel/algo_gglr5vhtT9u_v4.0.model";
    public static final String GAZE_ESTIMAION = "gazeestimationmodel/algo_gglap_h_v3.0.model";

    public static final String CAR_DETECT = "car_damage_detect/algo_gglqp7lvp8pahlvhghqg_v2.0.model";
    public static final String CAR_BRAND_DETECT = "car_damage_detect/algo_gglqp7l9pcv8p7su_v3.0.model";
    public static final String CAR_BRAND_OCR = "car_damage_detect/algo_gglqp7li9pghltq7_v2.0.model";
    public static final String CAR_TRACK = "car_damage_detect/algo_gglqp7lg7pqs_v2.0.model";

    public static final String STUDENT_ID_OCR = "student_id_ocr/algo_gglugkvhcgl5vltq7_v2.0.model";
    public static final String SKY_SEGMENT= "skysegmodel/algo_gglus6uha_v7.0.model";
    public static final String AVATAR_DRIVE_MODEL = "avatar_drive/algo_gglprpgp7lv75rh_v1.0.model";
    public static final String ACTION_RECOGNITION_MODEL = "action_recognition/algo_gglush9hgtcpqglgt4_v7.2.model";
    public static final String ACTION_RECOGNITION_TEMPLATE_OPEN_CLOSE_JUMP = "action_recognition/algo_tihcq9tuh-g8i9.dat";
    public static final String ACTION_RECOGNITION_TEMPLATE_PLANK = "action_recognition/algo_i9pcs-g8i9.dat";
    public static final String ACTION_RECOGNITION_TEMPLATE_PUSH_UP = "action_recognition/algo_iku0ki-g8i9.dat";
    public static final String ACTION_RECOGNITION_TEMPLATE_SIT_UP = "action_recognition/algo_u5gki-g8i9.dat";
    public static final String ACTION_RECOGNITION_TEMPLATE_DEEP_SAUAT = "action_recognition/algo_u2kpg-g8i9.dat";
    public static final String DYNAMIC_GESTURE = "dyngestmodel/";
    public static final String LICENSE_CAKE = "licenseface_detection";
    public static final String SKIN_SEGMENTATION = "skin_seg/";
    public static final String BACH_SKELETON = "bach_skeleton/";
    public static final String CHROMA_KEYING = "chroma_keying/";
    public static final String SLAM_ALGO = "slammodel/algo_ggu9p88tvh9_v5.0.model";
    public static final String SLAM_PARAM = "slammodel/algo_ggu9p8ip7p8.model";
    public static final String FACEFITTING_MODEL = "facefitting/algo_ggl1pqh15gg5cajfde_v2.0.model";
    public static final String AVABOOST_MODEL = "";
    public static final String ACTION_RECOGNITION_TEMPLATE_LUNGE = "action_recognition/algo_9kcah.dat";
    public static final String ACTION_RECOGNITION_TEMPLATE_LUNGE_SQUAT = "action_recognition/algo_9kcahlu2kpg.dat";
    public static final String ACTION_RECOGNITION_TEMPLATE_HIGH_RUN = "action_recognition/algo_05a0l7kc.dat";
    public static final String ACTION_RECOGNITION_TEMPLATE_KNEELING_PUSH_UP = "action_recognition/algo_schh95caliku0ki.dat";
    public static final String ACTION_RECOGNITION_TEMPLATE_HIP_BRIDGE = "action_recognition/algo_05il475vah.dat";
    public static final String OBJECT_TRACKING = "object_tracking/algo_45catlt43hqgS7pqs5ca_v1.0.dat";

    public static final String SALIENCY_MATTING_MODEL = "saliency_matting/";

    public AlgorithmResourceHelper(Context mContext) {
        super(mContext);
    }

    @Override
    public String faceModel() {
        return getModelPath(FACE);
    }

    @Override
    public String gazeEstimationModel() {
        return getModelPath(GAZE_ESTIMAION);
    }

    @Override
    public String faceVerifyModel() {
        return getModelPath(FACEVERIFY);
    }

    @Override
    public String faceExtraModel() {
        return getModelPath(FACEEXTA);
    }

    @Override
    public String faceAttrModel() {
        return getModelPath(FACEATTRI);
    }

    @Override
    public String humanDistanceModel() {
        return getModelPath(HUMANDIST);
    }

    @Override
    public String headSegModel() {
        return getModelPath(HEADSEGMENT);
    }

    @Override
    public String hairParserModel() {
        return getModelPath(HAIRPARSING);
    }

    @Override
    public String studentIdOcrModel() {
        return getModelPath(STUDENT_ID_OCR);
    }

    @Override
    public String c1Model() {
        return getModelPath(C1);
    }

    @Override
    public String c2Model() {
        return getModelPath(C2);
    }

    @Override
    public String carModel() {
        return getModelPath(CAR_DETECT);
    }

    @Override
    public String carBrandModel() {
        return getModelPath(CAR_BRAND_DETECT);
    }

    @Override
    public String brandOcrModel() {
        return getModelPath(CAR_BRAND_OCR);
    }

    @Override
    public String carTrackModel() {
        return getModelPath(CAR_TRACK);
    }

    @Override
    public String handModel() {
        return getModelPath(HAND_DETECT);
    }

    @Override
    public String handBoxModel() {
        return getModelPath(HAND_BOX);
    }

    @Override
    public String handGestureModel() {
        return getModelPath(HAND_GESTURE);
    }

    @Override
    public String handKeyPointModel() {
        return getModelPath(HAND_KEY_POINT);
    }

    @Override
    public String lightClsModel() {
        return getModelPath(LIGHTCLS);
    }

    @Override
    public String petFaceModel() {
        return getModelPath(PETFACE);
    }

    @Override
    public String portraitMattingModel() {
        return getModelPath(PORTRAITMATTING);
    }

    @Override
    public String skeletonModel() {
        return getModelPath(SKELETON);
    }

    @Override
    public String skeleton3DModel() {
        return getModelPath(SKELETON_3D);
    }

    @Override
    public String videoClsModel() {
        return getModelPath(VIDEO_CLS);
    }

    @Override
    public String skySegModel() {
        return getModelPath(SKY_SEGMENT);
    }

    @Override
    public String actionRecognitionModelPath() {
        return getModelPath(ACTION_RECOGNITION_MODEL);
    }

    @Override
    public String templateForActionType(ActionRecognitionAlgorithmTask.ActionType actionType) {
        switch (actionType) {
            case OPEN_CLOSE_JUMP:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_OPEN_CLOSE_JUMP);
            case SIT_UP:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_SIT_UP);
            case DEEP_SQUAT:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_DEEP_SAUAT);
            case PUSH_UP:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_PUSH_UP);
            case PLANK:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_PLANK);
            case LUNGE:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_LUNGE);
            case LUNGE_SQUAT:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_LUNGE_SQUAT);
            case HIGH_RUN:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_HIGH_RUN);
            case KNEELING_PUSH_UP:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_KNEELING_PUSH_UP);
            case HIP_BRIDGE:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_HIP_BRIDGE);
        }
        return null;
    }

    @Override
    public String dynamicGestureModel() {
        return getModelPath(DYNAMIC_GESTURE);
    }

    @Override
    public String licenseCakeModel() {
        return getModelPath(LICENSE_CAKE);
    }

    @Override
    public String skinSegmentationModel() {
        return getModelPath(SKIN_SEGMENTATION);
    }

    @Override
    public String bachSkeletonModel() {
        return getModelPath(BACH_SKELETON);
    }

    @Override
    public String chromaKeyingModel() {
        return getModelPath(CHROMA_KEYING);
    }

    @Override
    public String slamModel() {
        return getModelPath(SLAM_ALGO);
    }

    @Override
    public String slamParam() {
        return getModelPath(SLAM_PARAM);
    }

    @Override
    public String faceFittingModel() {
        return getModelPath(FACEFITTING_MODEL);
    }

    @Override
    public String avaBoostModel() {
        return getModelPath(AVABOOST_MODEL);
    }
    
    @Override
    public String objectTrackingModel() {
        return getModelPath(OBJECT_TRACKING);
    }

    @Override
    public String saliencyModel() {
        return getModelPath(SALIENCY_MATTING_MODEL);
    }
}
