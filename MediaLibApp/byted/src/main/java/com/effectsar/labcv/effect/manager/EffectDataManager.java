package com.effectsar.labcv.effect.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;


import com.effectsar.labcv.common.database.LocalParamHelper;
import com.effectsar.labcv.common.model.EffectType;
import com.effectsar.labcv.R;
import com.effectsar.labcv.effect.model.ColorItem;
import com.effectsar.labcv.effect.model.ComposerNode;
import com.effectsar.labcv.effect.model.EffectButtonItem;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.effectsar.labcv.common.model.EffectType.LITE_ASIA;
import static com.effectsar.labcv.common.model.EffectType.LITE_NOT_ASIA;
import static com.effectsar.labcv.common.model.EffectType.STANDARD_ASIA;
import static com.effectsar.labcv.common.model.EffectType.STANDARD_NOT_ASIA;



/** {zh} 
 * 特效数据管理类，负责管理美颜美妆等功能的数据
 */
/** {en} 
 * Special effects data management class, responsible for managing the data of beauty and makeup functions
 */

public class EffectDataManager {
    public static final  int OFFSET = 16;
    public static final  int MASK = ~0xFFFF;
    public static final  int SUB_OFFSET = 8;
    public static final  int SUB_MASK = ~0xFF;

    //   {zh} 一级菜单       {en} Level 1 menu  
    //The second menu

    public static final int TYPE_CLOSE                  = -1;
    public static final int TYPE_BEAUTY_SUIT            = 1 << OFFSET;
    //   {zh} Beautify face 美颜       {en} Beautify face  
    public static final int TYPE_BEAUTY_FACE            = 2 << OFFSET;
    //   {zh} Beautify reshape 美型       {en} Beautified reshape  
    public static final int TYPE_BEAUTY_RESHAPE         = 3 << OFFSET;
    //   {zh} Beautify body 美体       {en} Beautify the body  
    public static final int TYPE_BEAUTY_BODY            = 4 << OFFSET;
    //   {zh} Makeup 美妆       {en} Makeup beauty  
    public static final int TYPE_MAKEUP                 = 5 << OFFSET;
    //   {zh} Filter 滤镜       {en} Filter  
    public static final int TYPE_FILTER                 = 6 << OFFSET;
    //   {zh} 画质      {en} Palette
    public static final int TYPE_PALETTE                = 7 << OFFSET;
    //   {zh} 风格妆       {en} Style makeup
    public static final int TYPE_BLACK_TECHNOLOGY       = 8 << OFFSET;
    //   {zh} 口红      {en} Lipstick
    public static final int TYPE_LIPSTICK               = 9 << OFFSET;
    //   {zh} 染发      {en} Hair dye
    public static final int TYPE_HAIR_DYE               = 10 << OFFSET;
    //   {zh} 风格妆       {en} Style makeup
    public static final int TYPE_STYLE_MAKEUP           = 11 << OFFSET;
    //   {zh} 背景虚化 {en} background blur
    public static final int TYPE_BACK_GROUND           = 12 << OFFSET;
    //   {zh} 背景虚化 {en} background blur
    public static final int TYPE_BACK_GROUND1           = 13 << OFFSET;

    //   {zh} 二级菜单       {en} Secondary menu  
    //The secondary menu

    //   {zh} Beautify Suit 美颜套装       {en} Beautify suit
    public static final int TYPE_BEAUTY_SUIT_FACE_DEFAUT        = TYPE_BEAUTY_SUIT + (1 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_SUIT_FACE_JUVENILE      = TYPE_BEAUTY_SUIT + (2 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_SUIT_FACE_ROUND         = TYPE_BEAUTY_SUIT + (3 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_SUIT_FACE_SQUARE        = TYPE_BEAUTY_SUIT + (4 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_SUIT_FACE_LONG          = TYPE_BEAUTY_SUIT + (5 << SUB_OFFSET);

    //   {zh} Beautify face 美颜       {en} Beautify face  
    public static final int TYPE_BEAUTY_FACE_SMOOTH             = TYPE_BEAUTY_FACE + (1 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_FACE_WHITEN             = TYPE_BEAUTY_FACE + (2 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_FACE_SHARPEN            = TYPE_BEAUTY_FACE + (3 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_FACE_CLARITY            = TYPE_BEAUTY_FACE + (4 << SUB_OFFSET);

//     {zh} 美白     {en} whitening
    public static final int TYPE_BEAUTY_FACE_WHITEN_DEFAULT             = TYPE_BEAUTY_FACE_WHITEN + (11 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_FACE_WHITEN_LENGBAI             = TYPE_BEAUTY_FACE_WHITEN + (12 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_FACE_WHITEN_FENBAI             = TYPE_BEAUTY_FACE_WHITEN + (13 << SUB_OFFSET);

    //   {zh} Beautify reshape 美形       {en} Beautify reshape
    //  {zh} 面部  {en} Facial
    public static final int TYPE_BEAUTY_RESHAPE_FACE    = TYPE_BEAUTY_RESHAPE + (1 << SUB_OFFSET);// {zh} 面部 {en} Facial
    public static final int TYPE_BEAUTY_RESHAPE_FACE_OVERALL    = TYPE_BEAUTY_RESHAPE + (2 << SUB_OFFSET);// {zh} 瘦脸 {en} Skinny face
    public static final int TYPE_BEAUTY_RESHAPE_FACE_SMALL      = TYPE_BEAUTY_RESHAPE + (3 << SUB_OFFSET);// {zh} 小脸 {en} Little face
    public static final int TYPE_BEAUTY_RESHAPE_FACE_CUT        = TYPE_BEAUTY_RESHAPE + (4 << SUB_OFFSET);// {zh} 窄脸 {en} Narrow face
    public static final int TYPE_BEAUTY_RESHAPE_FACE_V        = TYPE_BEAUTY_RESHAPE + (5 << SUB_OFFSET);// {zh} V脸 {en} V face
    public static final int TYPE_BEAUTY_RESHAPE_FOREHEAD        = TYPE_BEAUTY_RESHAPE + (6 << SUB_OFFSET);// {zh} 额头/发际线 {en} Forehead/hairline
    public static final int TYPE_BEAUTY_RESHAPE_CHEEK           = TYPE_BEAUTY_RESHAPE + (7 << SUB_OFFSET);// {zh} 颧骨 {en} Cheekbones
    public static final int TYPE_BEAUTY_RESHAPE_JAW             = TYPE_BEAUTY_RESHAPE + (8 << SUB_OFFSET);// {zh} 下颌骨 {en} Mandible
    public static final int TYPE_BEAUTY_RESHAPE_CHIN            = TYPE_BEAUTY_RESHAPE + (9 << SUB_OFFSET);// {zh} 下巴 {en} Chin

    public static final int TYPE_BEAUTY_RESHAPE_FACE_OVERALL1    = TYPE_BEAUTY_RESHAPE + (10 << SUB_OFFSET);// {zh} 瘦脸 {en} Skinny face
    public static final int TYPE_BEAUTY_RESHAPE_FACE_OVERALL2    = TYPE_BEAUTY_RESHAPE + (11 << SUB_OFFSET);// {zh} 瘦脸 {en} Skinny face
    public static final int TYPE_BEAUTY_RESHAPE_FACE_OVERALL3    = TYPE_BEAUTY_RESHAPE + (12 << SUB_OFFSET);// {zh} 瘦脸 {en} Skinny face
    public static final int TYPE_BEAUTY_RESHAPE_FACE_OVERALL4    = TYPE_BEAUTY_RESHAPE + (13 << SUB_OFFSET);// {zh} 瘦脸 {en} Skinny face


    //  {zh} 眼睛  {en} Eyes
    public static final int TYPE_BEAUTY_RESHAPE_EYE             = TYPE_BEAUTY_RESHAPE + (20 << SUB_OFFSET);// {zh} 眼睛 {en} Eyes
    public static final int TYPE_BEAUTY_RESHAPE_EYE_SIZE             = TYPE_BEAUTY_RESHAPE + (21 << SUB_OFFSET);// {zh} 大眼 {en} Big eyes
    public static final int TYPE_BEAUTY_RESHAPE_EYE_HEIGHT             = TYPE_BEAUTY_RESHAPE + (22 << SUB_OFFSET);// {zh} 眼高度 {en} Eye height
    public static final int TYPE_BEAUTY_RESHAPE_EYE_WIDTH             = TYPE_BEAUTY_RESHAPE + (23 << SUB_OFFSET);// {zh} 眼宽度 {en} Eye width
    public static final int TYPE_BEAUTY_RESHAPE_EYE_MOVE        = TYPE_BEAUTY_RESHAPE + (24 << SUB_OFFSET); // {zh} 眼移动/眼位置 {en} Eye movement/eye position
    public static final int TYPE_BEAUTY_RESHAPE_EYE_SPACING     = TYPE_BEAUTY_RESHAPE + (25 << SUB_OFFSET);// {zh} 眼间距 {en} Eye spacing
    public static final int TYPE_BEAUTY_RESHAPE_EYE_LOWER_EYELID     = TYPE_BEAUTY_RESHAPE + (26 << SUB_OFFSET);// {zh} 眼睑下至 {en} Eyelid down to
    public static final int TYPE_BEAUTY_RESHAPE_EYE_PUPIL     = TYPE_BEAUTY_RESHAPE + (27 << SUB_OFFSET);// {zh} 瞳孔大小 {en} Pupil size
    public static final int TYPE_BEAUTY_RESHAPE_EYE_INNER_CORNER     = TYPE_BEAUTY_RESHAPE + (28 << SUB_OFFSET);// {zh} 内眼角 {en} Inner canthus
    public static final int TYPE_BEAUTY_RESHAPE_EYE_OUTER_CORNER     = TYPE_BEAUTY_RESHAPE + (28 << SUB_OFFSET);// {zh} 外眼角 {en} Outer corner of eye
    public static final int TYPE_BEAUTY_RESHAPE_EYE_ROTATE      = TYPE_BEAUTY_RESHAPE + (29 << SUB_OFFSET);// {zh} 眼角度、眼角上扬 {en} Eye angle, canthus rise


    // {zh} 鼻子 {en} Nose
    public static final int TYPE_BEAUTY_RESHAPE_NOSE       = TYPE_BEAUTY_RESHAPE + (40 << SUB_OFFSET);// {zh} 鼻子 {en} Nose
    public static final int TYPE_BEAUTY_RESHAPE_NOSE_SIZE       = TYPE_BEAUTY_RESHAPE + (41 << SUB_OFFSET);// {zh} 鼻子大小/瘦鼻 {en} Nose size/thin nose
    public static final int TYPE_BEAUTY_RESHAPE_NOSE_SWING       = TYPE_BEAUTY_RESHAPE + (42 << SUB_OFFSET);// {zh} 鼻翼 {en} Nose
    public static final int TYPE_BEAUTY_RESHAPE_NOSE_BRIDGE       = TYPE_BEAUTY_RESHAPE + (43 << SUB_OFFSET);// {zh} 鼻梁 {en} Bridge of nose
    public static final int TYPE_BEAUTY_RESHAPE_NOSE_MOVE       = TYPE_BEAUTY_RESHAPE + (44 << SUB_OFFSET);// {zh} 鼻子提升/长鼻 {en} Nose lift/long nose
    public static final int TYPE_BEAUTY_RESHAPE_NOSE_TIP       = TYPE_BEAUTY_RESHAPE + (45 << SUB_OFFSET);// {zh} 鼻尖 {en} Nose tip
    public static final int TYPE_BEAUTY_RESHAPE_NOSE_ROOT       = TYPE_BEAUTY_RESHAPE + (46 << SUB_OFFSET);// {zh} 山根 {en} Yamagata




    //  {zh} 眉毛  {en} Eyebrows
    public static final int TYPE_BEAUTY_RESHAPE_BROW             = TYPE_BEAUTY_RESHAPE    + (60 << SUB_OFFSET); //  {zh} 眉毛  {en} Eyebrows
    public static final int TYPE_BEAUTY_RESHAPE_BROW_SIZE         = TYPE_BEAUTY_RESHAPE    + (61 << SUB_OFFSET);//  {zh} 眉毛粗细  {en} Eyebrow thickness
    public static final int TTYPE_BEAUTY_RESHAPE_BROW_POSITION     = TYPE_BEAUTY_RESHAPE    + (62 << SUB_OFFSET); //  {zh} 眉毛位置  {en} Eyebrow position
    public static final int TYPE_BEAUTY_RESHAPE_BROW_TILT         = TYPE_BEAUTY_RESHAPE    + (63 << SUB_OFFSET); //  {zh} 眉毛倾斜  {en} Tilted eyebrows
    public static final int TYPE_BEAUTY_RESHAPE_BROW_RIDGE       = TYPE_BEAUTY_RESHAPE    + (64 << SUB_OFFSET); //  {zh} 眉峰  {en} Meifeng
    public static final int TYPE_BEAUTY_RESHAPE_BROW_DISTANCE     = TYPE_BEAUTY_RESHAPE    + (65 << SUB_OFFSET);//  {zh} 眉毛间距  {en} Eyebrow spacing
    public static final int TYPE_BEAUTY_RESHAPE_BROW_WIDTH        = TYPE_BEAUTY_RESHAPE    + (66 << SUB_OFFSET);//  {zh} 眉毛宽度  {en} Eyebrow width


    // {zh} 嘴巴 {en} Mouth
    public static final int TYPE_BEAUTY_RESHAPE_MOUTH            = TYPE_BEAUTY_RESHAPE    + (80 << SUB_OFFSET); //  {zh} 嘴巴  {en} Mouth
    public static final int TYPE_BEAUTY_RESHAPE_MOUTH_ZOOM        = TYPE_BEAUTY_RESHAPE    + (81 << SUB_OFFSET);//  {zh} 嘴巴大小/嘴形  {en} Mouth size/shape
    public static final int TYPE_BEAUTY_RESHAPE_MOUTH_WIDTH       = TYPE_BEAUTY_RESHAPE    + (82 << SUB_OFFSET);//  {zh} 嘴巴宽度  {en} Mouth width
    public static final int TYPE_BEAUTY_RESHAPE_MOUTH_MOVE        = TYPE_BEAUTY_RESHAPE    + (83 << SUB_OFFSET);//  {zh} 嘴巴位置/人中  {en} Mouth position/person
    public static final int TYPE_BEAUTY_RESHAPE_MOUTH_SMILE       = TYPE_BEAUTY_RESHAPE    + (84 << SUB_OFFSET);//  {zh} 微笑  {en} Smile

    // {zh} 妆 {en} Makeup
    public static final int TYPE_BEAUTY_RESHAPE_BRIGHTEN_EYE    = TYPE_BEAUTY_RESHAPE + (35 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_RESHAPE_REMOVE_POUCH    = TYPE_BEAUTY_RESHAPE + (36 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_RESHAPE_SMILE_FOLDS     = TYPE_BEAUTY_RESHAPE + (37 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_RESHAPE_WHITEN_TEETH    = TYPE_BEAUTY_RESHAPE + (38 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_RESHAPE_SINGLE_TO_DOUBLE_EYELID   = TYPE_BEAUTY_RESHAPE + (39 << SUB_OFFSET);

    //

    //   {zh} Beautify body 美体       {en} Beautify the body
    public static final int TYPE_BEAUTY_BODY_THIN = TYPE_BEAUTY_BODY + (1 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_BODY_LONG_LEG = TYPE_BEAUTY_BODY + (2 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_BODY_SLIM_LEG        = TYPE_BEAUTY_BODY  + (3 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_BODY_SLIM_WAIST      = TYPE_BEAUTY_BODY  + (4 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_BODY_ENLARGE_BREAST  = TYPE_BEAUTY_BODY  + (5 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_BODY_ENHANCE_HIP     = TYPE_BEAUTY_BODY  + (6 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_BODY_ENHANCE_NECK    = TYPE_BEAUTY_BODY  + (7 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_BODY_SLIM_ARM        = TYPE_BEAUTY_BODY  + (8 << SUB_OFFSET);
    public static final int TYPE_BEAUTY_BODY_SHRINK_HEAD     = TYPE_BEAUTY_BODY  + (9 << SUB_OFFSET);


    //   {zh} Makeup 美妆       {en} Makeup beauty
    public static final int TYPE_MAKEUP_LIP = TYPE_MAKEUP + (1 << SUB_OFFSET);
    public static final int TYPE_MAKEUP_BLUSHER = TYPE_MAKEUP + (2 << SUB_OFFSET);
    public static final int TYPE_MAKEUP_EYELASH = TYPE_MAKEUP + (3 << SUB_OFFSET);
    public static final int TYPE_MAKEUP_PUPIL = TYPE_MAKEUP + (4 << SUB_OFFSET);
    public static final  int TYPE_MAKEUP_HAIR = TYPE_MAKEUP + (5 << SUB_OFFSET);
    public static final int TYPE_MAKEUP_EYESHADOW = TYPE_MAKEUP + (6 << SUB_OFFSET);
    public static final int TYPE_MAKEUP_EYEBROW = TYPE_MAKEUP + (7 << SUB_OFFSET);
    public static final  int TYPE_MAKEUP_FACIAL = TYPE_MAKEUP + (8 << SUB_OFFSET);
    public static final  int TYPE_MAKEUP_WOCAN = TYPE_MAKEUP + (9 << SUB_OFFSET);
    public static final  int TYPE_MAKEUP_EYELIGHT = TYPE_MAKEUP + (10 << SUB_OFFSET);

    //   {zh} 风格妆       {en} Style makeup
    public static final int TYPE_STYLE_MAKEUP_2D = TYPE_STYLE_MAKEUP + (1 << SUB_OFFSET);
    public static final int TYPE_STYLE_MAKEUP_3D = TYPE_STYLE_MAKEUP + (2 << SUB_OFFSET);
    public static final int TYPE_LOCAL_STYLE_MAKEUP_2D = TYPE_STYLE_MAKEUP + (3 << SUB_OFFSET);
    public static final int TYPE_LOCAL_STYLE_MAKEUP_3D = TYPE_STYLE_MAKEUP + (4 << SUB_OFFSET);

    //   {zh} 口红       {en} Lipstick
    public static final int TYPE_LIPSTICK_GLOSSY        = TYPE_LIPSTICK + (1 << SUB_OFFSET);
    public static final int TYPE_LIPSTICK_MATTE         = TYPE_LIPSTICK + (2 << SUB_OFFSET);

    //   {zh} 染发       {en} Hair dye
    public static final int TYPE_HAIR_DYE_FULL        = TYPE_HAIR_DYE + (1 << SUB_OFFSET);
    public static final int TYPE_HAIR_DYE_HIGHLIGHT   = TYPE_HAIR_DYE + (2 << SUB_OFFSET);

    //   {zh} 画质       {en} Palette
    public static final int TYPE_PALETTE_TEMPERATURE = TYPE_PALETTE + (1 << SUB_OFFSET);
    public static final int TYPE_PALETTE_TONE = TYPE_PALETTE + (2 << SUB_OFFSET);
    public static final int TYPE_PALETTE_SATURATION = TYPE_PALETTE + (3 << SUB_OFFSET);
    public static final int TYPE_PALETTE_BRIGHTNESS = TYPE_PALETTE + (4 << SUB_OFFSET);
    public static final int TYPE_PALETTE_CONTRAST = TYPE_PALETTE + (5 << SUB_OFFSET);
    public static final int TYPE_PALETTE_HIGHLIGHT = TYPE_PALETTE + (6 << SUB_OFFSET);
    public static final int TYPE_PALETTE_SHADOW = TYPE_PALETTE + (7 << SUB_OFFSET);
    public static final int TYPE_PALETTE_LIGHT_SENSATION = TYPE_PALETTE + (8 << SUB_OFFSET);
    public static final int TYPE_PALETTE_PARTICLE = TYPE_PALETTE + (9 << SUB_OFFSET);
    public static final int TYPE_PALETTE_FADE = TYPE_PALETTE + (10 << SUB_OFFSET);
    public static final int TYPE_PALETTE_VIGNETTING = TYPE_PALETTE + (11 << SUB_OFFSET);

    //   {zh} 画质       {en} Palette
    public static final int TYPE_BLACK_TECHNOLOGY_ANTI_ACNE_SPOT_TOB = TYPE_BLACK_TECHNOLOGY + (1 << SUB_OFFSET);
    public static final int TYPE_BLACK_TECHNOLOGY_ANTI_ACNE_SPOT_LOW = TYPE_BLACK_TECHNOLOGY + (2 << SUB_OFFSET);
    public static final int TYPE_BLACK_TECHNOLOGY_YUNFU = TYPE_BLACK_TECHNOLOGY + (3 << SUB_OFFSET);
    public static final int TYPE_BLACK_TECHNOLOGY_FULING = TYPE_BLACK_TECHNOLOGY + (4 << SUB_OFFSET);
    public static final int TYPE_BLACK_TECHNOLOGY_LUNKUOPINGHUA = TYPE_BLACK_TECHNOLOGY + (5 << SUB_OFFSET);

    //   {zh} Node name 结点名称       {en} Node name node name
    static String NODE_BEAUTY_STANDARD = "beauty_Android_standard";
    static String NODE_BEAUTY_LITE = "beauty_Android_lite";
    static String NODE_BEAUTY_4ITEMS = "beauty_4Items";
    static String NODE_RESHAPE_STANDARD = "reshape_standard";
    static String NODE_RESHAPE_LITE = "reshape_lite";
    static String NODE_RESHAPE_LITE_V4 = "reshape_lite_eye_size";
    static String NODE_ALL_SLIM = "body/allslim";
    static String NODE_ALL_SLIM_BREAST = "body/allslim_breast";
    private EffectButtonItem mSuitDefaut = null;
    private  final Map<Integer, EffectButtonItem> mSavedItems = new HashMap<>();
    private  final Map<Integer, EffectButtonItem> mEffectFaceItems = new HashMap<>();
    private  final Map<Integer, EffectButtonItem> mEffectReshapeItems = new HashMap<>();

    //   {zh} 染发部位desc字段       {en} Hair dye parts desc
    public static final int DESC_HAIR_DYE_HIGHLIGHT_PART_A = 1;
    public static final int DESC_HAIR_DYE_HIGHLIGHT_PART_B = 2;
    public static final int DESC_HAIR_DYE_HIGHLIGHT_PART_C = 3;
    public static final int DESC_HAIR_DYE_HIGHLIGHT_PART_D = 4;
    public static final int DESC_HAIR_DYE_FULL = 5;
    public static final int DESC_HAIR_DYE_HIGHLIGHT = 6;

    private EffectType mEffectType;

    public EffectDataManager(EffectType effectType) {
        this.mEffectType = effectType;
    }

    public  String[][] generateComposerNodesAndTags(Set<EffectButtonItem> selectNodes) {
        List<EffectButtonItem> items = new ArrayList<>();
        Set<String> set = new HashSet<>();
        for (EffectButtonItem item : selectNodes) {
            if (item.getNode() != null) {
                String path = item.getNode().getPath();
                if (path.equals("face_defaut")) continue;
                if (!set.contains(path)) {
                    set.add(path);
                    items.add(item);
                }
            }
        }

        String[] nodes = new String[items.size()];
        String[] tags = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            nodes[i] = items.get(i).getNode().getPath();
            tags[i] = items.get(i).getNode().getTag();
        }
        return new String[][] {nodeRankList(nodes), tags};
//        return new String[][] {nodes, tags};
    }

    private String[] nodeRankList(String[] nodes) {
        String[] itemsArray = nodes;
        String[] QualityArray = new String[0];
        String[] ids = new String[] {"palette/color", "palette/contrast", "palette/light", "palette/vignette", "palette/particle"};
        for (int i=0; i<nodes.length; i++) {
            String obj = nodes[i];
            for (int j=0; j<ids.length; j++) {
                if (obj == ids[j]) {
                    String[] tmp = new String[QualityArray.length + 1];
                    System.arraycopy(QualityArray, 0, tmp, 0, QualityArray.length);
                    tmp[QualityArray.length] = obj;
                    QualityArray = tmp;
                    List<String> list1= Arrays.asList(itemsArray);
                    List<String> arrList = new ArrayList<String>(list1); //
                    arrList.remove(obj);
                    String[] strArr = new String[arrList.size()];
                    arrList.toArray(strArr);
                    itemsArray = strArr;
                }
            }
        }
        String[] itemsEndArray = new String[0];
        for (int j=0; j<ids.length; j++) {
            for (int i=0; i<QualityArray.length; i++) {
                if (ids[j] == QualityArray[i]) {
                    String[] tmp = new String[itemsEndArray.length + 1];
                    System.arraycopy(itemsEndArray, 0, tmp, 0, itemsEndArray.length);
                    tmp[itemsEndArray.length] = QualityArray[i];
                    itemsEndArray = tmp;
                }
            }
        }
        List<String> arrList= Arrays.asList(itemsArray);
        List<String> arrDndList = new ArrayList<String>(arrList); //
        List list = Arrays.asList(itemsEndArray);
        arrDndList.addAll(list);
        String[] strArr = new String[arrDndList.size()];
        arrDndList.toArray(strArr);
        itemsArray = strArr;
        return itemsArray;
    }


    public  String[][] generateComposerNodesAndTags(EffectButtonItem item) {
        String[] nodes = new String[1];
        String[] tags = new String[1];

        nodes[0] = (item==null || item.getNode() == null)?"":item.getNode().getPath();
        tags[0] = (item==null || item.getNode() == null)?"":item.getNode().getTag();


        return new String[][] {nodes, tags};
    }

    public EffectButtonItem getSuitDefautItem() {
        if (mSuitDefaut == null) {
            mSuitDefaut = new EffectButtonItem(TYPE_BEAUTY_SUIT_FACE_DEFAUT, R.drawable.ic_beauty_suit_default, R.string.beauty_suit_face_defaut, new ComposerNode("face_defaut"));
        }
        return mSuitDefaut;
    }

    public  Set<EffectButtonItem> getDefaultItems() {
        EffectButtonItem beautyFace = getItem(TYPE_BEAUTY_FACE);
        EffectButtonItem beautyReshape = getItem(TYPE_BEAUTY_RESHAPE);
        EffectButtonItem suitDefautItem =  getSuitDefautItem();
        resetAll();

        suitDefautItem.setSelected(true);
        suitDefautItem.setSelectedRelation(true);
        Set<EffectButtonItem> items = new HashSet<EffectButtonItem>();
        for (EffectButtonItem item : beautyFace.getChildren()) {
            if (isDefaultEffect(item.getId())) {
                item.setSelected(true);
                items.add(item);
            }
            for (EffectButtonItem childItem: item.getChildren()) {
                if (isDefaultEffect(childItem.getId())) {
                    childItem.setSelected(true);
                    childItem.setSelectedRelation(true);
                    items.add(childItem);
                }
            }
        }
        for (EffectButtonItem item : beautyReshape.getChildren()) {
            if (item.hasChildren()){
                for (EffectButtonItem child: item.getChildren()){
                    if (isDefaultEffect(child.getId())) {
                        child.setSelected(true);
//                        item.setSelectedRelation(true);
                        items.add(child);
                    }
                    for (EffectButtonItem childItem: child.getChildren()){
                        if (isDefaultEffect(childItem.getId())) {
                            childItem.setSelected(true);
                            childItem.setSelectedRelation(true);
                            items.add(childItem);
                        }
                    }
                }
            }

        }

        return items;
    }

    /**
     * 添加选中的套装选项，如果没有选择套装，则添加关闭item
     * @param items
     * @param localParams
     */
    private void appendSuitItemToStoredItems(Set<EffectButtonItem> items, ArrayList<LocalParamHelper.LocalParam> localParams) {
        if (items == null || localParams == null) {
            return;
        }
        Map<String, EffectButtonItem> suitItemsMap = new HashMap<>();
        EffectButtonItem closeItem = null;
        for (EffectButtonItem item : getItem(TYPE_BEAUTY_SUIT).getChildren()) {
            if (item.getNode() != null && item.getNode().getPath() != null) {
                suitItemsMap.put(item.getNode().getPath(), item);
            } else {
                closeItem = item;
            }
        }

        EffectButtonItem selectItem = null;
        for (LocalParamHelper.LocalParam param : localParams) {
            if (suitItemsMap.keySet().contains(param.path) && param.selected == true) {
                selectItem = suitItemsMap.get(param.path);
            }
        }

        if (null == selectItem) {
            selectItem = closeItem;
        }

        if (null != selectItem) {
            selectItem.setSelected(true);
            items.add(selectItem);
        }
    }

    /** {zh} 
     * @param status 用来区分素材类型，本地的还是下发的。0 表示默认，1表示本地
     * @return
     */
    /** {en} 
     * @param status  is used to distinguish between material types, local or distributed. 0 means default, 1 means local
     * @return
     */
    public  Set<EffectButtonItem> getLocalStoredItems(int status) {
        // return all the recorded effect items, selected but without value loaded.
        Set<EffectButtonItem> items = new HashSet<EffectButtonItem>();
        Set<String> paths = new HashSet<>();
        ArrayList<LocalParamHelper.LocalParam> localParams = LocalParamDataManager.allItems();
        if (localParams != null) {
            for (LocalParamHelper.LocalParam param : localParams) {
                if (param.effect) {
                    paths.add(param.path+"+"+param.key);
                }
            }
        }
        appendSuitItemToStoredItems(items, localParams);

        EffectButtonItem beautyFace = getItem(TYPE_BEAUTY_FACE);
        for (EffectButtonItem item : beautyFace.getChildren()) {
            for (EffectButtonItem childItem: item.getChildren()) {
                if ( childItem.getNode() != null
                        && !TextUtils.isEmpty(childItem.getNode().getPath())
                        && childItem.getNode().getKeyArray() != null
                        && !TextUtils.isEmpty(childItem.getNode().getKeyArray()[0])
                        && paths.contains(childItem.getNode().getPath()+"+"+childItem.getNode().getKeyArray()[0]) ) {
                    childItem.setSelected(true);
                    items.add(childItem);
                }
            }
            if ( item.getNode() != null
                    && !TextUtils.isEmpty(item.getNode().getPath())
                    && item.getNode().getKeyArray() != null
                    && !TextUtils.isEmpty(item.getNode().getKeyArray()[0])
                    && paths.contains(item.getNode().getPath()+"+"+item.getNode().getKeyArray()[0]) ) {
                item.setSelected(true);
                items.add(item);
            }
        }

        EffectButtonItem beautyReshape = getItem(TYPE_BEAUTY_RESHAPE);
        for (EffectButtonItem item : beautyReshape.getChildren()) {
            if (item.hasChildren()){
                for (EffectButtonItem child: item.getChildren()){
                    for (EffectButtonItem childIndex: child.getChildren()) {
                        if (
                                childIndex.getNode() != null
                                        && !TextUtils.isEmpty(childIndex.getNode().getPath())
                                        && childIndex.getNode().getKeyArray() != null
                                        && !TextUtils.isEmpty(childIndex.getNode().getKeyArray()[0])
                                        && paths.contains(childIndex.getNode().getPath()+"+"+childIndex.getNode().getKeyArray()[0])
                        ) {
                            childIndex.setSelected(true);
                            items.add(childIndex);
                        }
                    }
                    if (
                            child.getNode() != null
                                    && !TextUtils.isEmpty(child.getNode().getPath())
                                    && child.getNode().getKeyArray() != null
                                    && !TextUtils.isEmpty(child.getNode().getKeyArray()[0])
                                    && paths.contains(child.getNode().getPath()+"+"+child.getNode().getKeyArray()[0])
                    ) {
                        child.setSelected(true);
                        items.add(child);
                    }
                }
            }
        }

        EffectButtonItem beautyBody = getItem(TYPE_BEAUTY_BODY);
        for (EffectButtonItem item : beautyBody.getChildren()) {
            if ( item.getNode() != null
                    && !TextUtils.isEmpty(item.getNode().getPath())
                    && item.getNode().getKeyArray() != null
                    && !TextUtils.isEmpty(item.getNode().getKeyArray()[0])
                    && paths.contains(item.getNode().getPath()+"+"+item.getNode().getKeyArray()[0]) ) {
                item.setSelected(true);
                items.add(item);
            }
        }

        EffectButtonItem makeup = getItem(TYPE_MAKEUP);
        for (EffectButtonItem item : makeup.getChildren()) {
            if (item.hasChildren()){
                for (EffectButtonItem child: item.getChildren()){
                    if (
                            child.getNode() != null
                                    && !TextUtils.isEmpty(child.getNode().getPath())
                                    && child.getNode().getKeyArray() != null
                                    && !TextUtils.isEmpty(child.getNode().getKeyArray()[0])
                                    && paths.contains(child.getNode().getPath()+"+"+child.getNode().getKeyArray()[0])
                    ) {
                        child.setSelected(true);
                        items.add(child);
                    }
                }
            }
        }

        EffectButtonItem palette = getItem(TYPE_PALETTE);
        for (EffectButtonItem item : palette.getChildren()) {
            if ( item.getNode() != null
                    && !TextUtils.isEmpty(item.getNode().getPath())
                    && item.getNode().getKeyArray() != null
                    && !TextUtils.isEmpty(item.getNode().getKeyArray()[0])
                    && paths.contains(item.getNode().getPath()+"+"+item.getNode().getKeyArray()[0]) ) {
                item.setSelected(true);
                items.add(item);
            }
        }

        EffectButtonItem blackTech = getItem(TYPE_BLACK_TECHNOLOGY);
        for (EffectButtonItem item : blackTech.getChildren()) {
            if ( item.getNode() != null
                    && !TextUtils.isEmpty(item.getNode().getPath())
                    && item.getNode().getKeyArray() != null
                    && !TextUtils.isEmpty(item.getNode().getKeyArray()[0])
                    && paths.contains(item.getNode().getPath()+"+"+item.getNode().getKeyArray()[0]) ) {
                item.setSelected(true);
                items.add(item);
            }
        }

        EffectButtonItem styleMakeup2D = getSubItem(TYPE_STYLE_MAKEUP_2D);
        if(status==1) styleMakeup2D = getSubItem(TYPE_LOCAL_STYLE_MAKEUP_2D);
        for (EffectButtonItem item : styleMakeup2D.getChildren()) {
            if ( item.getNode() != null
                    && !TextUtils.isEmpty(item.getNode().getPath())
                    && item.getNode().getKeyArray() != null
                    && !TextUtils.isEmpty(item.getNode().getKeyArray()[0])
                    && paths.contains(item.getNode().getPath()+"+"+item.getNode().getKeyArray()[0]) ) {
                item.setSelected(true);
                items.add(item);
            }
        }

        EffectButtonItem styleMakeup3D = getSubItem(TYPE_STYLE_MAKEUP_3D);
        if(status==1) styleMakeup3D = getSubItem(TYPE_LOCAL_STYLE_MAKEUP_3D);
        for (EffectButtonItem item : styleMakeup3D.getChildren()) {
            if ( item.getNode() != null
                    && !TextUtils.isEmpty(item.getNode().getPath())
                    && item.getNode().getKeyArray() != null
                    && !TextUtils.isEmpty(item.getNode().getKeyArray()[0])
                    && paths.contains(item.getNode().getPath()+"+"+item.getNode().getKeyArray()[0]) ) {
                item.setSelected(true);
                items.add(item);
            }
        }

        return items;
    }

    public static  float[] getDefaultIntensity(int type,EffectType effectType) {
        Object intensity = getDefaultMap(effectType).get(type);
        if (intensity instanceof Float) {
            return new float[] {(Float) intensity};
        } else if (intensity instanceof float[]) {
            return Arrays.copyOf((float[]) intensity, ((float[]) intensity).length);
        }

        return new float[]{0.0f};
    }

    public static  float[] getDefaultIntensity(int type,EffectType effectType, boolean enableNegative) {
        float[] intensity = getDefaultIntensity(type, effectType);
        if (enableNegative && intensity[0] == 0f){
            intensity =  new float[]{0.5f};
        }
        return intensity;
    }




    private static   Map<Integer, Object> getDefaultMap(EffectType type) {
        if (type == null) {
            type = LITE_ASIA;
        }
        switch (type) {
            case LITE_ASIA:
            case LITE_NOT_ASIA:
                return DEFAULT_LITE_VALUE;
            case STANDARD_ASIA:
            case STANDARD_NOT_ASIA:
                return DEFAULT_STANDARD_VALUE;
        }
        return DEFAULT_STANDARD_VALUE;


    }

    public  void resetAll() {
        for (EffectButtonItem item : allItems()) {
                resetItem(item);
        }
    }

    public  void resetItem(EffectButtonItem item) {
        if (item.hasChildren()) {
            for (EffectButtonItem child : item.getChildren()) {
                resetItem(child);
            }
        }
        item.setSelectChild(null);
        if(item.getId() == EffectDataManager.TYPE_CLOSE){
            item.setSelected(true);
        }else {
            item.setSelected(false);
        }
        item.setIntensityArray(getDefaultIntensity(item.getId(),mEffectType,item.isEnableNegative()));
    }

    private static final Map<Integer, Object> DEFAULT_STANDARD_VALUE;
    private static final Map<Integer, Object> DEFAULT_LITE_VALUE;

    static {
        @SuppressLint("UseSparseArrays") Map<Integer, Object> standardMap = new HashMap<>();
        @SuppressLint("UseSparseArrays") Map<Integer, Object> liteMap = new HashMap<>();
        //   {zh} 美颜       {en} Beauty  
        // beauty face
        Object put = standardMap.put(TYPE_BEAUTY_FACE_SMOOTH, 0.65F);
        standardMap.put(TYPE_BEAUTY_FACE_WHITEN_DEFAULT, 0.35F);
        standardMap.put(TYPE_BEAUTY_FACE_SHARPEN, 0.25F);
        standardMap.put(TYPE_BEAUTY_FACE_CLARITY, 0.2F);
        //   {zh} 美型       {en} Beauty  
        // beaury reshape
        //  {zh} 面部  {en} Facial
        standardMap.put(TYPE_BEAUTY_RESHAPE_FACE_OVERALL1, 0.75F);
        standardMap.put(TYPE_BEAUTY_RESHAPE_CHEEK, 0.65F);
        standardMap.put(TYPE_BEAUTY_RESHAPE_FOREHEAD, 0.70F);
        standardMap.put(TYPE_BEAUTY_RESHAPE_SMILE_FOLDS, 0.5F);

        //  {zh} 眼睛  {en} Eyes
        standardMap.put(TYPE_BEAUTY_RESHAPE_EYE_SIZE, 0.65F);
        standardMap.put(TYPE_BEAUTY_RESHAPE_BRIGHTEN_EYE, 0.4F);
        standardMap.put(TYPE_BEAUTY_RESHAPE_REMOVE_POUCH, 0.5F);


        //  {zh} 鼻子  {en} Nose
        standardMap.put(TYPE_BEAUTY_RESHAPE_NOSE_SIZE, 0.65F);

        standardMap.put(TYPE_BEAUTY_RESHAPE_NOSE_MOVE, 0.6F);


        //  {zh} 眉毛  {en} Eyebrows

        // {zh} 嘴巴 {en} Mouth
        standardMap.put(TYPE_BEAUTY_RESHAPE_MOUTH_ZOOM, 0.6F);
        standardMap.put(TYPE_BEAUTY_RESHAPE_MOUTH_MOVE, 0.65F);
        standardMap.put(TYPE_BEAUTY_RESHAPE_WHITEN_TEETH, 0.3F);



        //   {zh} 美体       {en} Body  

        standardMap.put(TYPE_BEAUTY_BODY_ENHANCE_HIP, 0.5f);

        //   {zh} 美妆       {en} Beauty makeup  
        standardMap.put(TYPE_MAKEUP_LIP, new float[] {0.5F,0F,0F,0F});
        standardMap.put(TYPE_MAKEUP_HAIR, new float[] {0.5F,0F,0F,0F});
        standardMap.put(TYPE_MAKEUP_BLUSHER, new float[] {0.5F,0F,0F,0F});
        standardMap.put(TYPE_MAKEUP_FACIAL, 0.5F);
        standardMap.put(TYPE_MAKEUP_EYEBROW, new float[] {0.3F,0F,0F,0F});
        standardMap.put(TYPE_MAKEUP_EYESHADOW, 0.5F);
        standardMap.put(TYPE_MAKEUP_PUPIL, 0.5F);
        standardMap.put(TYPE_MAKEUP_EYELASH, new float[] {0.5F,0F,0F,0F});
        standardMap.put(TYPE_MAKEUP_EYELIGHT, 0.5F);
        standardMap.put(TYPE_MAKEUP_WOCAN, 0.5F);


        //   {zh} 风格妆       {en} Style makeup  
        standardMap.put(TYPE_STYLE_MAKEUP, new float[] {0.8f, 0.8f});
        standardMap.put(TYPE_STYLE_MAKEUP_2D, new float[] {0.8f, 0.8f});
        standardMap.put(TYPE_STYLE_MAKEUP_3D, new float[] {0.8f, 0.8f});

        //   {zh} 滤镜       {en} Filter  
        // filter
        standardMap.put(TYPE_FILTER, 0.8F);

//        //  {zh} 画质       {en} Palette
//        standardMap.put(TYPE_PALETTE_TEMPERATURE    , 0.0F);
//        standardMap.put(TYPE_PALETTE_TONE           , 0.0F);
//        standardMap.put(TYPE_PALETTE_SATURATION     , 0.0F);
//        standardMap.put(TYPE_PALETTE_BRIGHTNESS     , 0.0F);
//        standardMap.put(TYPE_PALETTE_CONTRAST       , 0.0F);
//        standardMap.put(TYPE_PALETTE_HIGHLIGHT      , 0.0F);
//        standardMap.put(TYPE_PALETTE_SHADOW         , 0.0F);
//        standardMap.put(TYPE_PALETTE_LIGHT_SENSATION, 0.0F);
//        standardMap.put(TYPE_PALETTE_SHARPEN        , 0.0F);
//        standardMap.put(TYPE_PALETTE_PARTICLE       , 0.0F);
//        standardMap.put(TYPE_PALETTE_FADE           , 0.0F);
//        standardMap.put(TYPE_PALETTE_VIGNETTING     , 0.0F);

        DEFAULT_STANDARD_VALUE = Collections.unmodifiableMap(standardMap);

        //   {zh} 美颜       {en} Beauty  
        // beauty face
        liteMap.put(TYPE_BEAUTY_FACE_SMOOTH, 0.5F);
        liteMap.put(TYPE_BEAUTY_FACE_WHITEN_DEFAULT, 0.35F);
        liteMap.put(TYPE_BEAUTY_FACE_WHITEN_LENGBAI, 0.35F);
        liteMap.put(TYPE_BEAUTY_FACE_SHARPEN, 0.3F);
        liteMap.put(TYPE_BEAUTY_FACE_CLARITY, 0.2F);

        //   {zh} 美型       {en} Beauty  
        // beauty reshape
        liteMap.put(TYPE_BEAUTY_RESHAPE_FACE_OVERALL1, 0.35F);
        liteMap.put(TYPE_BEAUTY_RESHAPE_CHEEK,0.2F);
        liteMap.put(TYPE_BEAUTY_RESHAPE_JAW,0.4F);
        liteMap.put(TYPE_BEAUTY_RESHAPE_SMILE_FOLDS,0.35F);

        //  {zh} 眼睛  {en} Eyes
        liteMap.put(TYPE_BEAUTY_RESHAPE_EYE_SIZE, 0.35F);
        liteMap.put(TYPE_BEAUTY_RESHAPE_EYE_SPACING, 0.15F);
        liteMap.put(TYPE_BEAUTY_RESHAPE_EYE_LOWER_EYELID, 0.15F);
        liteMap.put(TYPE_BEAUTY_RESHAPE_BRIGHTEN_EYE,0.5F);
        liteMap.put(TYPE_BEAUTY_RESHAPE_REMOVE_POUCH,0.5F);

        // {zh} 鼻子 {en} Nose
        liteMap.put(TYPE_BEAUTY_RESHAPE_NOSE_SWING,0.7F);

        // {zh} 嘴巴 {en} Mouth
        liteMap.put(TYPE_BEAUTY_RESHAPE_MOUTH_ZOOM, 0.65F);
        liteMap.put(TYPE_BEAUTY_RESHAPE_WHITEN_TEETH, 0.35F);
        //   {zh} 美体       {en} Body

        liteMap.put(TYPE_BEAUTY_BODY_ENHANCE_HIP, 0.5f);

        //   {zh} 美妆       {en} Beauty makeup  
        liteMap.put(TYPE_MAKEUP_LIP,  0.5F);
        liteMap.put(TYPE_MAKEUP_HAIR, 0.5F);
        liteMap.put(TYPE_MAKEUP_BLUSHER, 0.2F);
        liteMap.put(TYPE_MAKEUP_FACIAL, 0.35F);
        liteMap.put(TYPE_MAKEUP_EYEBROW, 0.35F);
        liteMap.put(TYPE_MAKEUP_EYESHADOW, 0.35F);
        liteMap.put(TYPE_MAKEUP_PUPIL, 0.4F);
        liteMap.put(TYPE_MAKEUP_EYELASH, 0.4F);
        liteMap.put(TYPE_MAKEUP_EYELIGHT, 0.4F);
        liteMap.put(TYPE_MAKEUP_WOCAN, 0.35F);

        //   {zh} 风格妆       {en} Style makeup  
        liteMap.put(TYPE_STYLE_MAKEUP, new float[] {0.8f, 0.8f});
        liteMap.put(TYPE_STYLE_MAKEUP_2D, new float[] {0.8f, 0.8f});
        liteMap.put(TYPE_STYLE_MAKEUP_3D, new float[] {0.8f, 0.8f});

        //   {zh} 滤镜       {en} Filter  
        // filter
        liteMap.put(TYPE_FILTER, 0.8F);
        DEFAULT_LITE_VALUE = Collections.unmodifiableMap(liteMap);


    }


   private static final HashMap<Integer, ArrayList<ColorItem>> colorForChooseMap = new HashMap<>();
    static {
        colorForChooseMap.put(TYPE_MAKEUP_LIP, new ArrayList<>(Arrays.asList(
                new ColorItem(R.string.lip_color_yuanqi, 0.867f, 0.388f, 0.388f),
                new ColorItem(R.string.lip_color_rouhefen,0.949f ,0.576f ,0.620f),
                new ColorItem(R.string.lip_color_xiyou,0.945f ,0.510f ,0.408f),
                new ColorItem(R.string.lip_color_huolongguo,0.714f,0.224f,0.388f),
                new ColorItem(R.string.lip_color_caomei,0.631f ,0.016f,0.016f))));

        colorForChooseMap.put(TYPE_MAKEUP_BLUSHER, new ArrayList<>(Arrays.asList(
                new ColorItem(R.string.blusher_color_qianfen,0.988f,0.678f,0.733f),
                new ColorItem(R.string.blusher_color_xingren,0.996f,0.796f,0.545f),
                new ColorItem(R.string.blusher_color_shanhu,1.000f ,0.565f ,0.443f),
                new ColorItem(R.string.blusher_color_fentao,1.000f,0.506f,0.529f),
                new ColorItem(R.string.blusher_color_qianzi,0.980f,0.722f,0.855f))));
        colorForChooseMap.put(TYPE_MAKEUP_EYEBROW, new ArrayList<>(Arrays.asList(
                new ColorItem(R.string.black,0.078f,0.039f,0.039f),
                new ColorItem(R.string.zong,0.420f ,0.314f ,0.239f))));

        colorForChooseMap.put(TYPE_MAKEUP_EYELASH, new ArrayList<>(Arrays.asList(
                new ColorItem(R.string.black,0.078f,0.039f,0.039f),
                new ColorItem(R.string.zong,0.420f ,0.314f,0.239f))));

        colorForChooseMap.put(TYPE_HAIR_DYE_HIGHLIGHT, new ArrayList<>(Arrays.asList(
                new ColorItem(R.string.hair_dye_blue_haze,0.541f,0.616f,0.706f),
                new ColorItem(R.string.hair_dye_foggy_gray,0.808f ,0.792f,0.745f),
                new ColorItem(R.string.hair_dye_rose_red,0.384f ,0.075f,0.086f))));

    }


    private static ArrayList<ColorItem> getColorForChoose(EffectType effectType, int type){
        if (effectType == LITE_ASIA || effectType == LITE_NOT_ASIA) return null;
        return colorForChooseMap.get(type);
    }

    public boolean isDefaultEffect(int type){
        if (mEffectType == LITE_ASIA || mEffectType == LITE_NOT_ASIA) {
            return DefaultLiteEffects.contains(type);

        }else {
            return DefaultStandardEffects.contains(type);

        }

    }
    private static final HashSet<Integer> DefaultLiteEffects = new HashSet();
    static {
        DefaultLiteEffects.add(TYPE_BEAUTY_FACE_SMOOTH);
        DefaultLiteEffects.add(TYPE_BEAUTY_FACE_WHITEN_DEFAULT);
        DefaultLiteEffects.add(TYPE_BEAUTY_FACE_SHARPEN);
        DefaultLiteEffects.add(TYPE_BEAUTY_RESHAPE_FACE_OVERALL1);
        DefaultLiteEffects.add(TYPE_BEAUTY_RESHAPE_EYE_SIZE);

    }
    private static final HashSet<Integer> DefaultStandardEffects = new HashSet();
    static {
        DefaultStandardEffects.add(TYPE_BEAUTY_FACE_SMOOTH);
        DefaultStandardEffects.add(TYPE_BEAUTY_FACE_WHITEN_DEFAULT);
        DefaultStandardEffects.add(TYPE_BEAUTY_FACE_SHARPEN);
        DefaultStandardEffects.add(TYPE_BEAUTY_RESHAPE_FACE_OVERALL1);
        DefaultStandardEffects.add(TYPE_BEAUTY_RESHAPE_EYE_SIZE);

    }

    private static final Map<String, Integer> TextDictionary = new HashMap<>();
    static {
        TextDictionary.put("2D", R.string.tab_2d);
        TextDictionary.put("复杂", R.string.tab_complex);
        TextDictionary.put("3D风格", R.string.tab_3d_stylization);
        TextDictionary.put("贴纸妆", R.string.tab_makeup_sticker);
        TextDictionary.put("美妆", R.string.tab_beauty_sticker);
        TextDictionary.put("虚拟头像", R.string.tab_avatar_drive);
        TextDictionary.put("风格化", R.string.tab_stylization);
        TextDictionary.put("3D", R.string.tab_3d);
        TextDictionary.put("小游戏", R.string.tab_small_game);
        TextDictionary.put("空间放置", R.string.tab_space_placement);
        TextDictionary.put("AR笔刷", R.string.tab_paintbrush_ar);
        TextDictionary.put("AR任意门", R.string.tab_anywhere_door_ar);
        TextDictionary.put("柱体", R.string.tab_cylinder_ar);
        TextDictionary.put("地标AR", R.string.tab_landmark_ar);
        TextDictionary.put("天空AR", R.string.tab_sky_ar);
        TextDictionary.put("地面AR", R.string.tab_ground_ar);
        TextDictionary.put("AR试包", R.string.tab_ar_purse_try_on);
        TextDictionary.put("AR美甲", R.string.tab_ar_nails_try_on);
        TextDictionary.put("AR试鞋", R.string.tab_ar_shoes_try_on);
        TextDictionary.put("AR帽子试戴", R.string.tab_ar_hat_try_on);
        TextDictionary.put("AR项链试戴", R.string.tab_ar_necklace_try_on);
        TextDictionary.put("AR眼镜试戴", R.string.tab_ar_glasses_try_on);
        TextDictionary.put("AR试手链", R.string.tab_ar_bracelet_try_on);
        TextDictionary.put("AR戒指试戴", R.string.tab_ar_ring_try_on);
        TextDictionary.put("AR耳环试戴", R.string.tab_ar_earrings_try_on);
        TextDictionary.put("AR手表试戴", R.string.tab_ar_watch_try_on);
        TextDictionary.put("背部挂载", R.string.feature_back_mount);
        TextDictionary.put("光泽款", R.string.tab_lipstick_glossy);
        TextDictionary.put("哑光款", R.string.tab_lipstick_matte);
        TextDictionary.put("直播礼物", R.string.feature_gift_sticker);
        TextDictionary.put("运镜", R.string.feature_cine_move);
    }


    public  EffectButtonItem getItem(int type) {
        EffectButtonItem item = mSavedItems.get(type);
        if (item != null) {
            return item;
        }
        switch (type & MASK) {
            case TYPE_BEAUTY_FACE:
                item = getBeautyFaceItems();
                break;
            case TYPE_BEAUTY_RESHAPE:
                item = getBeautyReshapeItems();
                break;
            case TYPE_BEAUTY_BODY:
                item = getBeautyBodyItems();
                break;
            case TYPE_MAKEUP:
                item = getMakeupItems();
                break;
//            case TYPE_STYLE_MAKEUP:
//                item = getStyleMakeupItems();
//                break;
            case TYPE_PALETTE:
                item = getPaletteItems();
                break;
            case TYPE_BLACK_TECHNOLOGY:
                item = getBlackTechnologyItems();
                break;
            case TYPE_BACK_GROUND:
                item = getBackGoundItems();
                break;
            case TYPE_BEAUTY_SUIT:
                item = getSuitItems();
                break;
        }
        if (item != null) {
            mSavedItems.put(type, item);
        }
        return item;
    }

    public  EffectButtonItem getSubItem(int type) {
        EffectButtonItem item = mSavedItems.get(type);
        if (item != null) {
            return item;
        }
        switch (type) {
            case TYPE_STYLE_MAKEUP_2D:
                item = getStyleMakeup2DItems();
                break;
            case TYPE_LOCAL_STYLE_MAKEUP_2D:
                item = getLocalStyleMakeup2DItems();
                break;
            case TYPE_STYLE_MAKEUP_3D:
                item = getStyleMakeup3DItems();
                break;
            case TYPE_LOCAL_STYLE_MAKEUP_3D:
                item = getLocalStyleMakeup3DItems();
                break;
            case TYPE_LIPSTICK_GLOSSY:
                item = getLipstickGlossyItems();
                break;
            case TYPE_LIPSTICK_MATTE:
                item = getLipstickMatteItems();
                break;
            case TYPE_HAIR_DYE_FULL:
                item = getHairDyeFullItems();
                break;
            case TYPE_HAIR_DYE_HIGHLIGHT:
                item = getHairDyeHighlightItems();
                break;
        }
        if (item != null) {
            mSavedItems.put(type, item);
        }
        return item;
    }

    private  EffectButtonItem getLipstickGlossyItems(){
        return new EffectButtonItem(
                TYPE_LIPSTICK_GLOSSY,
                new EffectButtonItem[]{
                        new EffectButtonItem(TYPE_CLOSE, R.drawable.clear, R.string.close),
                        new EffectButtonItem(TYPE_LIPSTICK_GLOSSY, R.drawable.ic_lipstick_caomeihong, R.string.lipstick_caomeihong, new ComposerNode("lipstick/caomeihong")),
                        new EffectButtonItem(TYPE_LIPSTICK_GLOSSY, R.drawable.ic_lipstick_meiguidousha, R.string.lipstick_meiguidousha, new ComposerNode("lipstick/meiguidousha")),
                        new EffectButtonItem(TYPE_LIPSTICK_GLOSSY, R.drawable.ic_lipstick_naikameigui, R.string.lipstick_naikameigui, new ComposerNode("lipstick/naikameigui")),
                        new EffectButtonItem(TYPE_LIPSTICK_GLOSSY, R.drawable.ic_lipstick_rixinaicha, R.string.lipstick_rixinaicha, new ComposerNode("lipstick/rixinaicha")),
                        new EffectButtonItem(TYPE_LIPSTICK_GLOSSY, R.drawable.ic_lipstick_shanhuluose, R.string.lipstick_shanhuluose, new ComposerNode("lipstick/shanhuluose")),
                        new EffectButtonItem(TYPE_LIPSTICK_GLOSSY, R.drawable.ic_lipstick_sijialihong, R.string.lipstick_sijialihong, new ComposerNode("lipstick/sijialihong"))
                },
                false
        );
    }

    private  EffectButtonItem getLipstickMatteItems(){
        return new EffectButtonItem(
                TYPE_LIPSTICK_MATTE,
                new EffectButtonItem[]{
                        new EffectButtonItem(TYPE_CLOSE, R.drawable.clear, R.string.close),
                        new EffectButtonItem(TYPE_LIPSTICK_MATTE, R.drawable.ic_lipstick_doushase, R.string.lipstick_doushase, new ComposerNode("lipstick/doushase")),
                        new EffectButtonItem(TYPE_LIPSTICK_MATTE, R.drawable.ic_lipstick_naiyouxiyou, R.string.lipstick_naiyouxiyou, new ComposerNode("lipstick/naiyouxiyou")),
                        new EffectButtonItem(TYPE_LIPSTICK_MATTE, R.drawable.ic_lipstick_nanguase, R.string.lipstick_nanguase, new ComposerNode("lipstick/nanguase")),
                        new EffectButtonItem(TYPE_LIPSTICK_MATTE, R.drawable.ic_lipstick_rouwufen, R.string.lipstick_rouwufen, new ComposerNode("lipstick/rouwufen")),
                        new EffectButtonItem(TYPE_LIPSTICK_MATTE, R.drawable.ic_lipstick_sironghong, R.string.lipstick_sironghong, new ComposerNode("lipstick/sironghong")),
                        new EffectButtonItem(TYPE_LIPSTICK_MATTE, R.drawable.ic_lipstick_yinghuase, R.string.lipstick_yinghuase, new ComposerNode("lipstick/yinghuase"))
                },
                false
        );
    }

    private  EffectButtonItem getHairDyeFullItems(){
        return new EffectButtonItem(
                TYPE_HAIR_DYE_FULL,
                new EffectButtonItem[]{
                        new EffectButtonItem(TYPE_CLOSE, R.drawable.clear, R.string.close,DESC_HAIR_DYE_FULL, new ComposerNode("hair/ranfa"), new ArrayList<>(List.of(new ColorItem(R.string.close, 0.0f, 0.0f, 0.0f, 0.0f)))),
                        new EffectButtonItem(TYPE_HAIR_DYE_FULL, R.drawable.icon_hair_dye_anlanse, R.string.hair_dye_dark_blue,DESC_HAIR_DYE_FULL, new ComposerNode("hair/ranfa"),new ArrayList<>(List.of(new ColorItem(R.string.hair_dye_dark_blue, 0.059f, 0.224f, 0.333f)))),
                        new EffectButtonItem(TYPE_HAIR_DYE_FULL, R.drawable.icon_hair_dye_molvse, R.string.hair_dye_black_green,DESC_HAIR_DYE_FULL, new ComposerNode("hair/ranfa"),new ArrayList<>(List.of(new ColorItem(R.string.hair_dye_black_green, 0.318f, 0.361f, 0.251f)))),
                        new EffectButtonItem(TYPE_HAIR_DYE_FULL, R.drawable.icon_hair_dye_shenzongse, R.string.hair_dye_dark_brown,DESC_HAIR_DYE_FULL, new ComposerNode("hair/ranfa"),new ArrayList<>(List.of(new ColorItem(R.string.hair_dye_dark_brown, 0.298f, 0.110f, 0.051f))))

                },
                false
        );
    }
    private  EffectButtonItem getHairDyeHighlightItems(){
        ArrayList<ColorItem> colorItems= getColorForChoose(mEffectType, TYPE_HAIR_DYE_HIGHLIGHT);
        return new EffectButtonItem(
                TYPE_HAIR_DYE_HIGHLIGHT,
                new EffectButtonItem[]{
                        new EffectButtonItem(TYPE_CLOSE, R.drawable.clear, R.string.close,DESC_HAIR_DYE_HIGHLIGHT, new ComposerNode("hair/ranfa"), new ArrayList<>(List.of(new ColorItem(R.string.close, 0.0f, 0.0f, 0.0f, 0.0f)))),
                        new EffectButtonItem(TYPE_HAIR_DYE_HIGHLIGHT, R.drawable.icon_hair_dye_highlight_part_a, R.string.hair_dye_part_a,DESC_HAIR_DYE_HIGHLIGHT_PART_A, new ComposerNode("hair/ranfa"),colorItems),
                        new EffectButtonItem(TYPE_HAIR_DYE_HIGHLIGHT, R.drawable.icon_hair_dye_highlight_part_b, R.string.hair_dye_part_b,DESC_HAIR_DYE_HIGHLIGHT_PART_B, new ComposerNode("hair/ranfa"),colorItems),
                        new EffectButtonItem(TYPE_HAIR_DYE_HIGHLIGHT, R.drawable.icon_hair_dye_highlight_part_c, R.string.hair_dye_part_c,DESC_HAIR_DYE_HIGHLIGHT_PART_C, new ComposerNode("hair/ranfa"),colorItems),
                        new EffectButtonItem(TYPE_HAIR_DYE_HIGHLIGHT, R.drawable.icon_hair_dye_highlight_part_d, R.string.hair_dye_part_d,DESC_HAIR_DYE_HIGHLIGHT_PART_D, new ComposerNode("hair/ranfa"),colorItems)
                },
                true
        );
    }

    public   List<EffectButtonItem> allItems() {
        List<EffectButtonItem> items = new ArrayList<>();
        for (Map.Entry<Integer, EffectButtonItem> en : mSavedItems.entrySet()) {
            items.add(en.getValue());
        }
        return items;
    }

    private  EffectButtonItem getBeautyFaceItemsLite() {
        String beautyNode = beautyNode(mEffectType);
        ArrayList<EffectButtonItem> items = new ArrayList<>();
            items.add(new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close));
            items.add(new EffectButtonItem(TYPE_BEAUTY_FACE_SMOOTH, R.drawable.ic_beauty_smooth, R.string.beauty_face_smooth, new ComposerNode(beautyNode, "smooth", getDefaultIntensity(TYPE_BEAUTY_FACE_SMOOTH, mEffectType)[0])));
            if (hasWhiten()){
                items.add(new EffectButtonItem(TYPE_BEAUTY_FACE_WHITEN, R.drawable.ic_beauty_whiten, R.string.beauty_face_whiten, getMakeupOptionItems(TYPE_BEAUTY_FACE_WHITEN), false));
            }
            items.add(new EffectButtonItem(TYPE_BEAUTY_FACE_SHARPEN, R.drawable.ic_beauty_sharpen, R.string.beauty_face_sharpen, new ComposerNode(beautyNode, "sharp", getDefaultIntensity(TYPE_BEAUTY_FACE_SHARPEN, mEffectType)[0])));
            items.add(new EffectButtonItem(TYPE_BEAUTY_FACE_CLARITY, R.drawable.ic_beauty_clarity, R.string.beauty_face_clarity, new ComposerNode(beautyNode, "clear", getDefaultIntensity(TYPE_BEAUTY_FACE_CLARITY, mEffectType)[0])));

        return new EffectButtonItem(TYPE_BEAUTY_FACE, items.toArray(new EffectButtonItem[items.size()]));
    }

    private  EffectButtonItem getBeautyFaceItemsStandard() {
        String beautyNode = beautyNode(mEffectType);
        ArrayList<EffectButtonItem> items = new ArrayList<>();
        items.add(new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close));
        items.add(new EffectButtonItem(TYPE_BEAUTY_FACE_SMOOTH, R.drawable.ic_beauty_smooth, R.string.beauty_face_smooth, new ComposerNode(beautyNode, "smooth", getDefaultIntensity(TYPE_BEAUTY_FACE_SMOOTH, mEffectType)[0])));
        if (hasWhiten()){
//            items.add(new EffectButtonItem(TYPE_BEAUTY_FACE_WHITEN_DEFAULT, R.drawable.ic_beauty_whiten, R.string.beauty_face_whiten, new ComposerNode(beautyNode, "whiten", getDefaultIntensity(TYPE_BEAUTY_FACE_WHITEN_DEFAULT, mEffectType)[0])));
            items.add(new EffectButtonItem(TYPE_BEAUTY_FACE_WHITEN, R.drawable.ic_beauty_whiten, R.string.beauty_face_whiten, getMakeupOptionItems(TYPE_BEAUTY_FACE_WHITEN), false));
        }
        items.add(new EffectButtonItem(TYPE_BEAUTY_FACE_SHARPEN, R.drawable.ic_beauty_sharpen, R.string.beauty_face_sharpen, new ComposerNode(beautyNode, "sharp", getDefaultIntensity(TYPE_BEAUTY_FACE_SHARPEN, mEffectType)[0])));
        items.add(new EffectButtonItem(TYPE_BEAUTY_FACE_CLARITY, R.drawable.ic_beauty_clarity, R.string.beauty_face_clarity, new ComposerNode(beautyNode, "clear", getDefaultIntensity(TYPE_BEAUTY_FACE_CLARITY, mEffectType)[0])));

        return new EffectButtonItem(TYPE_BEAUTY_FACE, items.toArray(new EffectButtonItem[items.size()]));
    }

    private boolean hasWhiten() {
        return (mEffectType == LITE_ASIA || mEffectType == STANDARD_ASIA);
    }

    private boolean hasDoubleEyeLip() {
        return (mEffectType == LITE_ASIA || mEffectType == STANDARD_ASIA);
    }

    public EffectType getEffectType() {
        return mEffectType;
    }

    private  EffectButtonItem getBeautyFaceItems() {


        EffectButtonItem retItem = null;
        switch (mEffectType) {
            case LITE_ASIA:
            case LITE_NOT_ASIA:
                retItem = getBeautyFaceItemsLite();
                break;
            case STANDARD_ASIA:
            case STANDARD_NOT_ASIA:
                retItem = getBeautyFaceItemsStandard();
            default:
                break;
        }
        if (retItem != null) {
            for (EffectButtonItem childItem : retItem.getChildren()) {
                if (childItem.getId() == TYPE_BEAUTY_FACE_WHITEN) {
                    for (EffectButtonItem overallChildItem : childItem.getChildren()) {
                        if (overallChildItem.getId() != TYPE_CLOSE) {
                            mEffectFaceItems.put(overallChildItem.getId(), overallChildItem);
                        }
                    }
                }
                else {
                    if (childItem.getId() != TYPE_CLOSE) {
                        mEffectFaceItems.put(childItem.getId(), childItem);
                    }
                }
            }
        }
        return retItem;
    }


    private  EffectButtonItem getBeautyReshapeItems() {

        EffectButtonItem retItem = null;
        switch (mEffectType) {
            case LITE_ASIA:
            case LITE_NOT_ASIA:
                retItem = getBeautyReshapeItemsLite();
                break;
            case STANDARD_ASIA:
            case STANDARD_NOT_ASIA:
                retItem = getBeautyReshapeItemsStandard();
            default:
                break;
        }
        if (retItem != null) {
            for (EffectButtonItem childItem1 : retItem.getChildren()){
                for (EffectButtonItem childItem : childItem1.getChildren()) {
                    if (childItem.getId() == TYPE_BEAUTY_RESHAPE_FACE_OVERALL) {
                        for (EffectButtonItem overallChildItem : childItem.getChildren()) {
                            if (overallChildItem.getId() != TYPE_CLOSE) {
                                mEffectReshapeItems.put(overallChildItem.getId(), overallChildItem);
                            }
                        }
                    }
                    else {
                        if (childItem.getId() != TYPE_CLOSE) {
                            mEffectReshapeItems.put(childItem.getId(), childItem);
                        }
                    }
                }
            }
        }
        return retItem;

    }

    public EffectButtonItem getSuitItems(){
        EffectButtonItem suitItems = new EffectButtonItem(TYPE_BEAUTY_SUIT,
                new EffectButtonItem[]{
                        new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                        getSuitDefautItem(),
                        new EffectButtonItem(TYPE_BEAUTY_SUIT_FACE_JUVENILE, R.drawable.ic_beauty_suit_juvenile, R.string.beauty_suit_face_juvenile, new ComposerNode("face_jucenile")),
                        new EffectButtonItem(TYPE_BEAUTY_SUIT_FACE_ROUND, R.drawable.ic_beauty_suit_round, R.string.beauty_suit_face_round, new ComposerNode("face_round")),
                        new EffectButtonItem(TYPE_BEAUTY_SUIT_FACE_LONG, R.drawable.ic_beauty_suit_long, R.string.beauty_suit_face_long, new ComposerNode("face_long")),
                        new EffectButtonItem(TYPE_BEAUTY_SUIT_FACE_SQUARE, R.drawable.ic_beauty_suit_square, R.string.beauty_suit_face_square, new ComposerNode("face_square")),
                },
                false
        );
        getSuitDefautItem().setSelected(true);
        suitItems.setSelectChild(getSuitDefautItem());
        return suitItems;
    }

    public HashMap<EffectButtonItem, Float> getSuitItemsAndDefaultValue(int type){
        HashMap<EffectButtonItem, Float> map = new HashMap<EffectButtonItem, Float>();
        switch (type) {
            case TYPE_BEAUTY_SUIT_FACE_DEFAUT: {
                // {zh} 磨皮 {en} Dermabrasion
                map.put(mEffectFaceItems.get(TYPE_BEAUTY_FACE_SMOOTH), 0.50f);
                if (hasWhiten()){
                    // {zh} 美白 {en} Whitening
                    map.put(mEffectFaceItems.get(TYPE_BEAUTY_FACE_WHITEN_DEFAULT), 0.36f);
                }
                // {zh} 锐化 {en} Sharpen
                map.put(mEffectFaceItems.get(TYPE_BEAUTY_FACE_SHARPEN), 0.33f);
                // {zh} 瘦脸 {en} Thin face
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_FACE_OVERALL1), 0.36f);
                // {zh} 大眼 {en} Big eyes
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_EYE_SIZE), 0.36f);
                break;
            }
            case TYPE_BEAUTY_SUIT_FACE_JUVENILE: {
                // {zh} 磨皮 {en} Dermabrasion
                map.put(mEffectFaceItems.get(TYPE_BEAUTY_FACE_SMOOTH), 0.50f);
                // {zh} 锐化 {en} Sharpen
                map.put(mEffectFaceItems.get(TYPE_BEAUTY_FACE_SHARPEN), 0.7f);
                // {zh} 瘦脸 {en} Thin face
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_FACE_OVERALL1), 0.35f);
                // {zh} 小脸 {en} Little face
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_FACE_SMALL), 0.35f);
                // {zh} 窄脸 {en} Narrow face
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_FACE_CUT), 0.3f);
                // {zh} 颧骨 {en} Zygomatic
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_CHEEK), 0.5f);
                // {zh} 下颌 {en} Mandibular
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_JAW), 0.3f);
                // {zh} 下巴长短 {en} Chin length
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_CHIN), 0.35f);
                // {zh} 发际线 {en} Hairline
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_FOREHEAD), 1.0f);
                // {zh} 法令纹 {en} Nasolabial folds
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_SMILE_FOLDS), 0.3f);
                // {zh} 眼睛大小 {en} Eye size
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_EYE_SIZE), 0.4f);
                // {zh} 眼睛位置 {en} Eye position
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_EYE_MOVE), 0.15f);
                // {zh} 眼距 {en} Eye distance
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_EYE_SPACING), 0.3f);
                // {zh} 亮眼 {en} Bright eyes
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_BRIGHTEN_EYE), 0.5f);
                // {zh} 黑眼圈 {en} Dark circles
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_REMOVE_POUCH), 0.5f);
                // {zh} 鼻翼 {en} Nose wing
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_NOSE_SWING), 0.75f);
                // {zh} 嘴巴大小 {en} Mouth size
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_MOUTH_ZOOM), 0.7f);
                // {zh} 嘴巴位置 {en} Mouth position
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_MOUTH_MOVE), 1.0f);
                // {zh} 白牙 {en} White tooth
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_WHITEN_TEETH), 0.35f);
                break;
            }
            case TYPE_BEAUTY_SUIT_FACE_ROUND: {
                // {zh} 磨皮 {en} Dermabrasion
                map.put(mEffectFaceItems.get(TYPE_BEAUTY_FACE_SMOOTH), 0.50f);
                // {zh} 锐化 {en} Sharpen
                map.put(mEffectFaceItems.get(TYPE_BEAUTY_FACE_SHARPEN), 0.7f);
                // {zh} 瘦脸 {en} Thin face
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_FACE_OVERALL1), 0.35f);
                // {zh} 窄脸 {en} Narrow face
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_FACE_CUT), 0.5f);
                // {zh} 颧骨 {en} Zygomatic
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_CHEEK), 0.2f);
                // {zh} 下颌 {en} Mandibular
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_JAW), 0.5f);
                // {zh} 发际线 {en} Hairline
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_FOREHEAD), 1.0f);
                // {zh} 法令纹 {en} Nasolabial folds
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_SMILE_FOLDS), 0.3f);
                // {zh} 眼睛大小 {en} Eye size
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_EYE_SIZE), 0.3f);
                // {zh} 眼睛位置 {en} Eye position
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_EYE_MOVE), 0.2f);
                // {zh} 眼距 {en} Eye distance
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_EYE_SPACING), 0.15f);
                // {zh} 亮眼 {en} Bright eyes
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_BRIGHTEN_EYE), 0.5f);
                // {zh} 黑眼圈 {en} Dark circles
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_REMOVE_POUCH), 0.5f);
                // {zh} 鼻翼 {en} Nose wing
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_NOSE_SWING), 0.75f);
                // {zh} 嘴巴大小 {en} Mouth size
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_MOUTH_ZOOM), 0.65f);
                // {zh} 嘴巴位置 {en} Mouth position
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_MOUTH_MOVE), 0.2f);
                // {zh} 白牙 {en} White tooth
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_WHITEN_TEETH), 0.35f);
                break;
            }
            case TYPE_BEAUTY_SUIT_FACE_SQUARE:{
                // {zh} 磨皮 {en} Dermabrasion
                map.put(mEffectFaceItems.get(TYPE_BEAUTY_FACE_SMOOTH), 0.50f);
                // {zh} 锐化 {en} Sharpen
                map.put(mEffectFaceItems.get(TYPE_BEAUTY_FACE_SHARPEN), 0.7f);
                // {zh} 瘦脸 {en} Thin face
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_FACE_OVERALL1), 0.4f);
                // {zh} 窄脸 {en} Narrow face
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_FACE_CUT), 0.6f);
                // {zh} 颧骨 {en} Zygomatic
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_CHEEK), 0.3f);
                // {zh} 下颌 {en} Mandibular
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_JAW), 0.7f);
                // {zh} 发际线 {en} Hairline
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_FOREHEAD), 1.0f);
                // {zh} 法令纹 {en} Nasolabial folds
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_SMILE_FOLDS), 0.3f);
                // {zh} 眼睛大小 {en} Eye size
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_EYE_SIZE), 0.3f);
                // {zh} 眼睛位置 {en} Eye position
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_EYE_MOVE), 0.2f);
                // {zh} 眼距 {en} Eye distance
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_EYE_SPACING), 0.15f);
                // {zh} 亮眼 {en} Bright eyes
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_BRIGHTEN_EYE), 0.5f);
                // {zh} 黑眼圈 {en} Dark circles
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_REMOVE_POUCH), 0.5f);
                // {zh} 鼻翼 {en} Nose wing
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_NOSE_SWING), 0.75f);
                // {zh} 嘴巴大小 {en} Mouth size
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_MOUTH_ZOOM), 0.65f);
                // {zh} 嘴巴位置 {en} Mouth position
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_MOUTH_MOVE), 0.2f);
                // {zh} 白牙 {en} White tooth
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_WHITEN_TEETH), 0.35f);
                break;
            }
            case TYPE_BEAUTY_SUIT_FACE_LONG:{
                // {zh} 磨皮 {en} Dermabrasion
                map.put(mEffectFaceItems.get(TYPE_BEAUTY_FACE_SMOOTH), 0.50f);
                // {zh} 锐化 {en} Sharpen
                map.put(mEffectFaceItems.get(TYPE_BEAUTY_FACE_SHARPEN), 0.7f);
                // {zh} 瘦脸 {en} Thin face
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_FACE_OVERALL1), 0.5f);
                // {zh} 窄脸 {en} Narrow face
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_FACE_CUT), 0.4f);
                // {zh} 小脸 {en} Little face
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_FACE_SMALL), 0.5f);
                // {zh} 颧骨 {en} Zygomatic
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_CHEEK), 0.2f);
                // {zh} 发际线 {en} Hairline
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_FOREHEAD), 1.0f);
                // {zh} 法令纹 {en} Nasolabial folds
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_SMILE_FOLDS), 0.3f);
                // {zh} 眼睛大小 {en} Eye size
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_EYE_SIZE), 0.3f);
                // {zh} 眼睛位置 {en} Eye position
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_EYE_MOVE), 0.2f);
                // {zh} 眼距 {en} Eye distance
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_EYE_SPACING), 0.15f);
                // {zh} 亮眼 {en} Bright eyes
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_BRIGHTEN_EYE), 0.5f);
                // {zh} 黑眼圈 {en} Dark circles
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_REMOVE_POUCH), 0.5f);
                // {zh} 鼻翼 {en} Nose wing
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_NOSE_SWING), 0.75f);
                // {zh} 提升 {en} Lift
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_NOSE_MOVE), 0.7f);
                // {zh} 嘴巴大小 {en} Mouth size
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_MOUTH_ZOOM), 0.65f);
                // {zh} 嘴巴位置 {en} Mouth position
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_MOUTH_MOVE), 0.3f);
                // {zh} 白牙 {en} White tooth
                map.put(mEffectReshapeItems.get(TYPE_BEAUTY_RESHAPE_WHITEN_TEETH), 0.35f);
                break;
            }
            default:
                break;
        }

        return map;
    }


    private EffectButtonItem getBeautyReshapeItemsLite(){

        String reshapeNode = reshapeNode(mEffectType);
        EffectButtonItem faceGroup = new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE,R.drawable.icon_face,R.string.beauty_reshape_face_group, new EffectButtonItem[]{
                new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE_OVERALL, R.drawable.ic_beauty_cheek_reshape, R.string.beauty_reshape_face_overall, getMakeupOptionItems(TYPE_BEAUTY_RESHAPE_FACE_OVERALL), false),
//                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE_OVERALL, R.drawable.ic_beauty_cheek_reshape, R.string.beauty_reshape_face_overall, new ComposerNode(NODE_RESHAPE_LITE, "Internal_Deform_Overall", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_FACE_OVERALL,mEffectType)[0])),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE_SMALL, R.drawable.ic_beauty_reshape_face_small, R.string.beauty_reshape_face_small, new ComposerNode(NODE_RESHAPE_LITE, "Internal_Deform_Face", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_FACE_SMALL,mEffectType)[0])),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE_CUT, R.drawable.ic_beauty_reshape_face_cut, R.string.beauty_reshape_face_cut, new ComposerNode(NODE_RESHAPE_LITE, "Internal_Deform_CutFace", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_FACE_CUT,mEffectType)[0])),
                new EffectButtonItem(TYPE_BEAUTY_BODY_SHRINK_HEAD, R.drawable.ic_beauty_body_shrink_head, R.string.beauty_body_shrink_head, new ComposerNode(NODE_RESHAPE_LITE, "BEF_BEAUTY_BODY_SHRINK_HEAD",getDefaultIntensity(TYPE_BEAUTY_BODY_SHRINK_HEAD,null)[0])),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_CHEEK, R.drawable.ic_beauty_reshape_cheek, R.string.beauty_reshape_face_cheek, new ComposerNode(NODE_RESHAPE_LITE, "Internal_Deform_Zoom_Cheekbone",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_CHEEK,mEffectType)[0])),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_JAW, R.drawable.ic_beauty_reshape_jaw, R.string.beauty_reshape_face_jaw, new ComposerNode(NODE_RESHAPE_LITE, "Internal_Deform_Zoom_Jawbone",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_JAW,mEffectType)[0])),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_CHIN, R.drawable.ic_beauty_reshape_chin, R.string.beauty_reshape_face_chin, new ComposerNode(NODE_RESHAPE_LITE, "Internal_Deform_Chin",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_CHIN,mEffectType,true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FOREHEAD, R.drawable.ic_beauty_reshape_forehead, R.string.beauty_reshape_forehead, new ComposerNode(NODE_RESHAPE_LITE, "Internal_Deform_Forehead",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_FOREHEAD,mEffectType,true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_SMILE_FOLDS, R.drawable.ic_beauty_reshape_smile_folds, R.string.beauty_face_smile_folds, new ComposerNode(NODE_BEAUTY_4ITEMS, "BEF_BEAUTY_SMILES_FOLDS",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_SMILE_FOLDS,mEffectType)[0]))

        });

        ArrayList<EffectButtonItem>eye_items = new ArrayList<>();
        eye_items.add(new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close));
        eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_EYE_SIZE, R.drawable.ic_reshape_eye_size, R.string.beauty_reshape_eye_size, new ComposerNode(NODE_RESHAPE_LITE_V4, "Internal_Deform_Eye", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_EYE_SIZE,mEffectType)[0])));
        eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_EYE_MOVE, R.drawable.ic_reshape_eye_move, R.string.beauty_reshape_eye_move, new ComposerNode(reshapeNode, "Internal_Deform_Eye_Move",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_EYE_MOVE,mEffectType)[0])));
        eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_EYE_SPACING, R.drawable.ic_reshape_eye_spacing, R.string.beauty_reshape_eye_spacing, new ComposerNode(reshapeNode, "Internal_Eye_Spacing",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_EYE_SPACING,mEffectType)[0])));
        eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_EYE_ROTATE, R.drawable.ic_reshape_eye_rotate, R.string.beauty_reshape_eye_rotate, new ComposerNode(reshapeNode, "Internal_Deform_RotateEye",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_EYE_ROTATE,mEffectType)[0])));
        eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_BRIGHTEN_EYE, R.drawable.ic_reshape_eye_brighten, R.string.beauty_face_brighten_eye,new ComposerNode(NODE_BEAUTY_4ITEMS, "BEF_BEAUTY_BRIGHTEN_EYE",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_BRIGHTEN_EYE,mEffectType)[0])));
        eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_REMOVE_POUCH, R.drawable.ic_reshape_eye_remove_pouch, R.string.beauty_face_remove_pouch, new ComposerNode(NODE_BEAUTY_4ITEMS, "BEF_BEAUTY_REMOVE_POUCH",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_REMOVE_POUCH,mEffectType)[0])));


        if (hasDoubleEyeLip()){
            eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_SINGLE_TO_DOUBLE_EYELID, R.drawable.ic_reshape_eye_double_eye_lid, R.string.beauty_face_eye_single_to_double_eyelid, new ComposerNode("double_eye_lid/newmoon", "BEF_BEAUTY_EYE_SINGLE_TO_DOUBLE",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_SINGLE_TO_DOUBLE_EYELID,mEffectType)[0])));
        }
        EffectButtonItem eyeGroup = new EffectButtonItem(TYPE_BEAUTY_RESHAPE_EYE,R.drawable.icon_eye,R.string.beauty_reshape_eye_group, eye_items.toArray(new EffectButtonItem[eye_items.size()]));


        EffectButtonItem noseGroup = new EffectButtonItem(TYPE_BEAUTY_RESHAPE_NOSE,R.drawable.icon_nose,R.string.beauty_reshape_nose_group, new EffectButtonItem[]{
                new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_NOSE_SWING, R.drawable.ic_reshape_nose_swing, R.string.beauty_reshape_nose_swing,new ComposerNode(NODE_RESHAPE_LITE, "Internal_Deform_Nose",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_NOSE_SWING,mEffectType,true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_NOSE_MOVE, R.drawable.ic_reshape_nose_move, R.string.beauty_reshape_nose_move,  new ComposerNode(NODE_RESHAPE_LITE, "Internal_Deform_MovNose", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_NOSE_MOVE,mEffectType,true)[0]),true)

        });


        EffectButtonItem mouthGroup = new EffectButtonItem(TYPE_BEAUTY_RESHAPE_MOUTH,R.drawable.icon_mouth,R.string.beauty_reshape_mouth_group, new EffectButtonItem[]{
                new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_MOUTH_ZOOM, R.drawable.ic_reshape_mouth_zoom, R.string.beauty_reshape_mouth_zoom, new ComposerNode(NODE_RESHAPE_LITE, "Internal_Deform_ZoomMouth", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_MOUTH_ZOOM,mEffectType,true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_MOUTH_MOVE, R.drawable.ic_reshape_mouth_move, R.string.beauty_reshape_mouth_move,  new ComposerNode(reshapeNode, "Internal_Deform_MovMouth", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_MOUTH_MOVE,mEffectType)[0])),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_MOUTH_SMILE, R.drawable.ic_reshape_mouth_smile, R.string.beauty_reshape_mouth_smile,  new ComposerNode(reshapeNode, "Internal_Deform_MouthCorner", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_MOUTH_SMILE,mEffectType)[0])),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_WHITEN_TEETH, R.drawable.ic_reshape_mouth_white_teeth, R.string.beauty_face_whiten_teeth,new ComposerNode(NODE_BEAUTY_4ITEMS, "BEF_BEAUTY_WHITEN_TEETH",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_WHITEN_TEETH,mEffectType)[0]))

        });


        return new EffectButtonItem(TYPE_BEAUTY_RESHAPE, new EffectButtonItem[]{
                new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                faceGroup,
                eyeGroup,
                noseGroup,
                mouthGroup
        }, true);
    }

    private EffectButtonItem getBeautyReshapeItemsStandard(){

        String reshapeNode = reshapeNode(mEffectType);
        EffectButtonItem faceGroup = new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE,R.drawable.icon_face,R.string.beauty_reshape_face_group, new EffectButtonItem[]{
                new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE_OVERALL, R.drawable.ic_beauty_cheek_reshape, R.string.beauty_reshape_face_overall, getMakeupOptionItems(TYPE_BEAUTY_RESHAPE_FACE_OVERALL), false),
//                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE_OVERALL1, R.drawable.ic_beauty_cheek_reshape, R.string.beauty_reshape_face_overall, new ComposerNode(reshapeNode, "Internal_Deform_Overall", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_FACE_OVERALL1,mEffectType, true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE_SMALL, R.drawable.ic_beauty_reshape_face_small, R.string.beauty_reshape_face_small, new ComposerNode(reshapeNode, "Internal_Deform_Face", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_FACE_SMALL,mEffectType)[0])),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE_CUT, R.drawable.ic_beauty_reshape_face_cut, R.string.beauty_reshape_face_cut, new ComposerNode(reshapeNode, "Internal_Deform_CutFace", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_FACE_CUT,mEffectType, true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_BODY_SHRINK_HEAD, R.drawable.ic_beauty_body_shrink_head, R.string.beauty_body_shrink_head, new ComposerNode(reshapeNode, "BEF_BEAUTY_BODY_SHRINK_HEAD",getDefaultIntensity(TYPE_BEAUTY_BODY_SHRINK_HEAD,null)[0])),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_CHEEK, R.drawable.ic_beauty_reshape_cheek, R.string.beauty_reshape_face_cheek, new ComposerNode(reshapeNode, "Internal_Deform_Zoom_Cheekbone",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_CHEEK,mEffectType, true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_JAW, R.drawable.ic_beauty_reshape_jaw, R.string.beauty_reshape_face_jaw, new ComposerNode(reshapeNode, "Internal_Deform_Zoom_Jawbone",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_JAW,mEffectType, true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE_V, R.drawable.ic_beauty_reshape_face_v, R.string.beauty_reshape_face_v, new ComposerNode(reshapeNode, "Internal_Deform_VFace",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_FACE_V,mEffectType)[0])),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_CHIN, R.drawable.ic_beauty_reshape_chin, R.string.beauty_reshape_face_chin, new ComposerNode(reshapeNode, "Internal_Deform_Chin",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_CHIN,mEffectType, true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FOREHEAD, R.drawable.ic_beauty_reshape_forehead, R.string.beauty_reshape_forehead, new ComposerNode(reshapeNode, "Internal_Deform_Forehead",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_FOREHEAD,mEffectType, true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_SMILE_FOLDS, R.drawable.ic_beauty_reshape_smile_folds, R.string.beauty_face_smile_folds, new ComposerNode(NODE_BEAUTY_4ITEMS, "BEF_BEAUTY_SMILES_FOLDS",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_SMILE_FOLDS,mEffectType)[0]))

        });

        ArrayList<EffectButtonItem>eye_items = new ArrayList<>();
        eye_items.add(new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close));
        eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_EYE_SIZE, R.drawable.ic_reshape_eye_size, R.string.beauty_reshape_eye_size, new ComposerNode(reshapeNode, "Internal_Deform_Eye", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_EYE_SIZE,mEffectType, true)[0]),true));
        eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_EYE_HEIGHT, R.drawable.ic_reshape_eye_height, R.string.beauty_reshape_eye_height, new ComposerNode(reshapeNode, "Internal_EyeHeight", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_EYE_HEIGHT,mEffectType, true)[0]),true));
        eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_EYE_WIDTH, R.drawable.ic_reshape_eye_width, R.string.beauty_reshape_eye_width,new ComposerNode(reshapeNode, "Internal_EyeWidth", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_EYE_WIDTH,mEffectType, true)[0]),true));
        eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_EYE_MOVE, R.drawable.ic_reshape_eye_move, R.string.beauty_reshape_eye_move, new ComposerNode(reshapeNode, "Internal_Deform_Eye_Move", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_EYE_MOVE,mEffectType, true)[0]),true));
        eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_EYE_SPACING, R.drawable.ic_reshape_eye_spacing, R.string.beauty_reshape_eye_spacing,new ComposerNode(reshapeNode, "Internal_Eye_Spacing", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_EYE_SPACING,mEffectType, true)[0]),true));
        eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_EYE_LOWER_EYELID, R.drawable.ic_reshape_eye_lower_eyelid, R.string.beauty_reshape_eye_lower_eyelid,new ComposerNode(reshapeNode, "Internal_LowerEyelid", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_EYE_LOWER_EYELID,mEffectType, true)[0]),true));
        eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_EYE_PUPIL, R.drawable.ic_reshape_eye_pupil, R.string.beauty_reshape_eye_pupil,new ComposerNode(reshapeNode, "Internal_EyePupil", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_EYE_PUPIL,mEffectType, true)[0]),true));
        eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_EYE_INNER_CORNER, R.drawable.ic_reshape_eye_inner_corner, R.string.beauty_reshape_eye_inner_corner,new ComposerNode(reshapeNode, "Internal_EyeInnerCorner", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_EYE_INNER_CORNER,mEffectType, true)[0]),true));
        eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_EYE_OUTER_CORNER, R.drawable.ic_reshape_eye_outer_corner, R.string.beauty_reshape_eye_outer_corner,new ComposerNode(reshapeNode, "Internal_EyeOuterCorner", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_EYE_OUTER_CORNER,mEffectType, true)[0]),true));
        eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_EYE_ROTATE, R.drawable.ic_reshape_eye_rotate, R.string.beauty_reshape_eye_rotate, new ComposerNode(reshapeNode, "Internal_Deform_RotateEye",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_EYE_ROTATE,mEffectType, true)[0]),true));
        eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_BRIGHTEN_EYE, R.drawable.ic_reshape_eye_brighten, R.string.beauty_face_brighten_eye,new ComposerNode(NODE_BEAUTY_4ITEMS, "BEF_BEAUTY_BRIGHTEN_EYE",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_BRIGHTEN_EYE,mEffectType)[0])));
        eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_REMOVE_POUCH, R.drawable.ic_reshape_eye_remove_pouch, R.string.beauty_face_remove_pouch,new ComposerNode(NODE_BEAUTY_4ITEMS, "BEF_BEAUTY_REMOVE_POUCH",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_REMOVE_POUCH,mEffectType)[0])));


        if (hasDoubleEyeLip()){
            eye_items.add(new EffectButtonItem(TYPE_BEAUTY_RESHAPE_SINGLE_TO_DOUBLE_EYELID, R.drawable.ic_reshape_eye_double_eye_lid, R.string.beauty_face_eye_single_to_double_eyelid, new ComposerNode("double_eye_lid/newmoon", "BEF_BEAUTY_EYE_SINGLE_TO_DOUBLE",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_SINGLE_TO_DOUBLE_EYELID,mEffectType)[0])));
        }
        EffectButtonItem eyeGroup = new EffectButtonItem(TYPE_BEAUTY_RESHAPE_EYE,R.drawable.icon_eye,R.string.beauty_reshape_eye_group, eye_items.toArray(new EffectButtonItem[eye_items.size()]));



        EffectButtonItem noseGroup = new EffectButtonItem(TYPE_BEAUTY_RESHAPE_NOSE,R.drawable.icon_nose,R.string.beauty_reshape_nose_group, new EffectButtonItem[]{
                new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_NOSE_SIZE, R.drawable.ic_reshape_nose_size, R.string.beauty_reshape_nose_size, new ComposerNode(reshapeNode, "Internal_Deform_NoseSize", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_NOSE_SIZE,mEffectType, true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_NOSE_SWING, R.drawable.ic_reshape_nose_swing, R.string.beauty_reshape_nose_swing,new ComposerNode(reshapeNode, "Internal_Deform_Nose",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_NOSE_SWING,mEffectType, true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_NOSE_BRIDGE, R.drawable.ic_reshape_nose_bridge, R.string.beauty_reshape_nose_bridge,new ComposerNode(reshapeNode, "Internal_Deform_NoseBridge",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_NOSE_BRIDGE,mEffectType, true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_NOSE_MOVE, R.drawable.ic_reshape_nose_move, R.string.beauty_reshape_nose_move,  new ComposerNode(reshapeNode, "Internal_Deform_MovNose", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_NOSE_MOVE,mEffectType, true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_NOSE_TIP, R.drawable.ic_reshape_nose_tip, R.string.beauty_reshape_nose_tip,new ComposerNode(reshapeNode, "Internal_Deform_NoseTip",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_NOSE_TIP,mEffectType, true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_NOSE_ROOT, R.drawable.ic_reshape_nose_root, R.string.beauty_reshape_nose_root,new ComposerNode(reshapeNode, "Internal_Deform_NoseRoot",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_NOSE_ROOT,mEffectType, true)[0]),true),


        });

        EffectButtonItem eyeBrowGroup = new EffectButtonItem(TYPE_BEAUTY_RESHAPE_BROW,R.drawable.icon_brow,R.string.beauty_reshape_brow_group, new EffectButtonItem[]{
                new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_BROW_SIZE, R.drawable.ic_beauty_reshape_brow_size, R.string.beauty_reshape_brow_size, new ComposerNode(reshapeNode, "Internal_BrowSize", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_BROW_SIZE,mEffectType, true)[0]),true),
                new EffectButtonItem(TTYPE_BEAUTY_RESHAPE_BROW_POSITION, R.drawable.ic_beauty_reshape_brow_position, R.string.beauty_reshape_brow_position,new ComposerNode(reshapeNode, "Internal_BrowPosition",getDefaultIntensity(TTYPE_BEAUTY_RESHAPE_BROW_POSITION,mEffectType, true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_BROW_TILT, R.drawable.ic_beauty_reshape_brow_tilt, R.string.beauty_reshape_brow_tilt,new ComposerNode(reshapeNode, "Internal_BrowTilt",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_BROW_TILT,mEffectType, true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_BROW_RIDGE, R.drawable.ic_beauty_reshape_brow_ridge, R.string.beauty_reshape_brow_ridge,  new ComposerNode(reshapeNode, "Internal_BrowRidge", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_BROW_RIDGE,mEffectType,true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_BROW_DISTANCE, R.drawable.ic_beauty_reshape_brow_distance, R.string.beauty_reshape_brow_distance,new ComposerNode(reshapeNode, "Internal_BrowDistance",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_BROW_DISTANCE,mEffectType, true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_BROW_WIDTH, R.drawable.ic_beauty_reshape_brow_width, R.string.beauty_reshape_brow_width,new ComposerNode(reshapeNode, "Internal_BrowWidth",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_BROW_WIDTH,mEffectType, true)[0]),true),


        });


        EffectButtonItem mouthGroup = new EffectButtonItem(TYPE_BEAUTY_RESHAPE_MOUTH,R.drawable.icon_mouth,R.string.beauty_reshape_mouth_group, new EffectButtonItem[]{
                new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_MOUTH_ZOOM, R.drawable.ic_reshape_mouth_zoom, R.string.beauty_reshape_mouth_zoom, new ComposerNode(reshapeNode, "Internal_Deform_ZoomMouth", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_MOUTH_ZOOM,mEffectType, true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_MOUTH_WIDTH, R.drawable.ic_reshape_mouth_width, R.string.beauty_reshape_mouth_width, new ComposerNode(reshapeNode, "Internal_MouseWidth", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_MOUTH_WIDTH,mEffectType, true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_MOUTH_MOVE, R.drawable.ic_reshape_mouth_move, R.string.beauty_reshape_mouth_move,  new ComposerNode(reshapeNode, "Internal_Deform_MovMouth", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_MOUTH_MOVE,mEffectType, true)[0]),true),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_MOUTH_SMILE, R.drawable.ic_reshape_mouth_smile, R.string.beauty_reshape_mouth_smile,  new ComposerNode(reshapeNode, "Internal_Deform_MouthCorner", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_MOUTH_SMILE,mEffectType)[0])),
                new EffectButtonItem(TYPE_BEAUTY_RESHAPE_WHITEN_TEETH, R.drawable.ic_reshape_mouth_white_teeth, R.string.beauty_face_whiten_teeth,new ComposerNode(NODE_BEAUTY_4ITEMS, "BEF_BEAUTY_WHITEN_TEETH",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_WHITEN_TEETH,mEffectType)[0]))

        });

        return new EffectButtonItem(TYPE_BEAUTY_RESHAPE, new EffectButtonItem[]{
                new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                faceGroup,
                eyeGroup,
                noseGroup,
                eyeBrowGroup,
                mouthGroup
        }, true);



    }




    private  EffectButtonItem getBeautyBodyItems() {
        if (mEffectType == LITE_ASIA || mEffectType == LITE_NOT_ASIA) {
            return new EffectButtonItem(
                    TYPE_BEAUTY_BODY,
                    new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                            new EffectButtonItem(TYPE_BEAUTY_BODY_THIN, R.drawable.ic_beauty_body_thin, R.string.beauty_body_thin, new ComposerNode(NODE_ALL_SLIM, "BEF_BEAUTY_BODY_THIN",getDefaultIntensity(TYPE_BEAUTY_BODY_THIN,null)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_BODY_LONG_LEG, R.drawable.ic_beauty_body_long_leg, R.string.beauty_body_long_leg, new ComposerNode(NODE_ALL_SLIM, "BEF_BEAUTY_BODY_LONG_LEG",getDefaultIntensity(TYPE_BEAUTY_BODY_LONG_LEG,null)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_BODY_SLIM_LEG, R.drawable.ic_beauty_body_slim_leg, R.string.beauty_body_leg_slim, new ComposerNode(NODE_ALL_SLIM, "BEF_BEAUTY_BODY_SLIM_LEG",getDefaultIntensity(TYPE_BEAUTY_BODY_SLIM_LEG,null)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_BODY_SLIM_WAIST, R.drawable.ic_beauty_body_thin, R.string.beauty_body_waist_slim, new ComposerNode(NODE_ALL_SLIM, "BEF_BEAUTY_BODY_SLIM_WAIST",getDefaultIntensity(TYPE_BEAUTY_BODY_SLIM_WAIST,null)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_BODY_ENLARGE_BREAST, R.drawable.ic_beauty_body_enlarge_breast, R.string.beauty_body_breast_enlarge, new ComposerNode(NODE_ALL_SLIM, "BEF_BEAUTY_BODY_ENLARGR_BREAST",getDefaultIntensity(TYPE_BEAUTY_BODY_ENLARGE_BREAST,null)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_BODY_ENHANCE_HIP, R.drawable.ic_beauty_body_enhance_hip, R.string.beauty_body_hip_enhance, new ComposerNode(NODE_ALL_SLIM, "BEF_BEAUTY_BODY_ENHANCE_HIP",getDefaultIntensity(TYPE_BEAUTY_BODY_ENHANCE_HIP,null,true)[0]),true),
                            new EffectButtonItem(TYPE_BEAUTY_BODY_ENHANCE_NECK, R.drawable.ic_beauty_body_enhance_neck, R.string.beauty_body_neck_enhance, new ComposerNode(NODE_ALL_SLIM, "BEF_BEAUTY_BODY_ENHANCE_NECK",getDefaultIntensity(TYPE_BEAUTY_BODY_ENHANCE_NECK,null)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_BODY_SLIM_ARM, R.drawable.ic_beauty_body_slim_arm, R.string.beauty_body_arm_slim, new ComposerNode(NODE_ALL_SLIM, "BEF_BEAUTY_BODY_SLIM_ARM",getDefaultIntensity(TYPE_BEAUTY_BODY_SLIM_ARM,null)[0])),
                    }
            );
        }
        else {
            return new EffectButtonItem(
                    TYPE_BEAUTY_BODY,
                    new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                            new EffectButtonItem(TYPE_BEAUTY_BODY_THIN, R.drawable.ic_beauty_body_thin, R.string.beauty_body_thin, new ComposerNode(NODE_ALL_SLIM, "BEF_BEAUTY_BODY_THIN",getDefaultIntensity(TYPE_BEAUTY_BODY_THIN,null)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_BODY_LONG_LEG, R.drawable.ic_beauty_body_long_leg, R.string.beauty_body_long_leg, new ComposerNode(NODE_ALL_SLIM, "BEF_BEAUTY_BODY_LONG_LEG",getDefaultIntensity(TYPE_BEAUTY_BODY_LONG_LEG,null)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_BODY_SLIM_LEG, R.drawable.ic_beauty_body_slim_leg, R.string.beauty_body_leg_slim, new ComposerNode(NODE_ALL_SLIM, "BEF_BEAUTY_BODY_SLIM_LEG",getDefaultIntensity(TYPE_BEAUTY_BODY_SLIM_LEG,null)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_BODY_SLIM_WAIST, R.drawable.ic_beauty_body_thin, R.string.beauty_body_waist_slim, new ComposerNode(NODE_ALL_SLIM, "BEF_BEAUTY_BODY_SLIM_WAIST",getDefaultIntensity(TYPE_BEAUTY_BODY_SLIM_WAIST,null)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_BODY_ENLARGE_BREAST, R.drawable.ic_beauty_body_enlarge_breast, R.string.beauty_body_breast_enlarge, new ComposerNode(NODE_ALL_SLIM_BREAST, "BEF_BEAUTY_BODY_ENLARGR_BREAST",getDefaultIntensity(TYPE_BEAUTY_BODY_ENLARGE_BREAST,null)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_BODY_ENHANCE_HIP, R.drawable.ic_beauty_body_enhance_hip, R.string.beauty_body_hip_enhance, new ComposerNode(NODE_ALL_SLIM_BREAST, "BEF_BEAUTY_BODY_ENHANCE_HIP",getDefaultIntensity(TYPE_BEAUTY_BODY_ENHANCE_HIP,null,true)[0]),true),
                            new EffectButtonItem(TYPE_BEAUTY_BODY_ENHANCE_NECK, R.drawable.ic_beauty_body_enhance_neck, R.string.beauty_body_neck_enhance, new ComposerNode(NODE_ALL_SLIM_BREAST, "BEF_BEAUTY_BODY_ENHANCE_NECK",getDefaultIntensity(TYPE_BEAUTY_BODY_ENHANCE_NECK,null)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_BODY_SLIM_ARM, R.drawable.ic_beauty_body_slim_arm, R.string.beauty_body_arm_slim, new ComposerNode(NODE_ALL_SLIM_BREAST, "BEF_BEAUTY_BODY_SLIM_ARM",getDefaultIntensity(TYPE_BEAUTY_BODY_SLIM_ARM,null)[0])),
                    }
            );
        }
    }

    private  EffectButtonItem getMakeupItems() {

        if (mEffectType == LITE_ASIA){
            return new EffectButtonItem(
                    TYPE_MAKEUP,
                    new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                            new EffectButtonItem(TYPE_MAKEUP_LIP, R.drawable.ic_makeup_lip, R.string.makeup_lip, getMakeupOptionItems(TYPE_MAKEUP_LIP), false),
                            new EffectButtonItem(TYPE_MAKEUP_BLUSHER, R.drawable.ic_makeup_blusher, R.string.makeup_blusher, getMakeupOptionItems(TYPE_MAKEUP_BLUSHER), false),
                            new EffectButtonItem(TYPE_MAKEUP_FACIAL, R.drawable.ic_makeup_facial, R.string.makeup_facial, getMakeupOptionItems(TYPE_MAKEUP_FACIAL), false),
                            new EffectButtonItem(TYPE_MAKEUP_EYEBROW, R.drawable.ic_makeup_eyebrow, R.string.makeup_eyebrow, getMakeupOptionItems(TYPE_MAKEUP_EYEBROW), false),
                            new EffectButtonItem(TYPE_MAKEUP_EYESHADOW, R.drawable.ic_makeup_eyeshadow, R.string.makeup_eye, getMakeupOptionItems(TYPE_MAKEUP_EYESHADOW), false),
                            new EffectButtonItem(TYPE_MAKEUP_PUPIL, R.drawable.ic_makeup_pupil, R.string.makeup_pupil, getMakeupOptionItems(TYPE_MAKEUP_PUPIL), false),
                            new EffectButtonItem(TYPE_MAKEUP_WOCAN, R.drawable.ic_makeup_eye_wocan, R.string.makeup_wocan, getMakeupOptionItems(TYPE_MAKEUP_WOCAN), false),
                            new EffectButtonItem(TYPE_MAKEUP_HAIR, R.drawable.ic_makeup_hair, R.string.makeup_hair, getMakeupOptionItems(TYPE_MAKEUP_HAIR), false),
                    }
            );

        } else if (mEffectType == LITE_NOT_ASIA) {
            return new EffectButtonItem(
                    TYPE_MAKEUP,
                    new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                            new EffectButtonItem(TYPE_MAKEUP_LIP, R.drawable.ic_makeup_lip, R.string.makeup_lip, getMakeupOptionItems(TYPE_MAKEUP_LIP), false),
                            new EffectButtonItem(TYPE_MAKEUP_BLUSHER, R.drawable.ic_makeup_blusher, R.string.makeup_blusher, getMakeupOptionItems(TYPE_MAKEUP_BLUSHER), false),
                            new EffectButtonItem(TYPE_MAKEUP_FACIAL, R.drawable.ic_makeup_facial, R.string.makeup_facial, getMakeupOptionItems(TYPE_MAKEUP_FACIAL), false),
                            new EffectButtonItem(TYPE_MAKEUP_EYEBROW, R.drawable.ic_makeup_eyebrow, R.string.makeup_eyebrow, getMakeupOptionItems(TYPE_MAKEUP_EYEBROW), false),
                            new EffectButtonItem(TYPE_MAKEUP_EYESHADOW, R.drawable.ic_makeup_eyeshadow, R.string.makeup_eye, getMakeupOptionItems(TYPE_MAKEUP_EYESHADOW), false),
                            new EffectButtonItem(TYPE_MAKEUP_PUPIL, R.drawable.ic_makeup_pupil, R.string.makeup_pupil, getMakeupOptionItems(TYPE_MAKEUP_PUPIL), false),
                            new EffectButtonItem(TYPE_MAKEUP_HAIR, R.drawable.ic_makeup_hair, R.string.makeup_hair, getMakeupOptionItems(TYPE_MAKEUP_HAIR), false),
                    }
            );
        } else if (mEffectType == STANDARD_ASIA) {
            return new EffectButtonItem(
                    TYPE_MAKEUP,
                    new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                            new EffectButtonItem(TYPE_MAKEUP_LIP, R.drawable.ic_makeup_lip, R.string.makeup_lip, getMakeupOptionItems(TYPE_MAKEUP_LIP), false),
                            new EffectButtonItem(TYPE_MAKEUP_BLUSHER, R.drawable.ic_makeup_blusher, R.string.makeup_blusher, getMakeupOptionItems(TYPE_MAKEUP_BLUSHER), false),
                            new EffectButtonItem(TYPE_MAKEUP_FACIAL, R.drawable.ic_makeup_facial, R.string.makeup_facial, getMakeupOptionItems(TYPE_MAKEUP_FACIAL), false),
                            new EffectButtonItem(TYPE_MAKEUP_EYEBROW, R.drawable.ic_makeup_eyebrow, R.string.makeup_eyebrow, getMakeupOptionItems(TYPE_MAKEUP_EYEBROW), false),
                            new EffectButtonItem(TYPE_MAKEUP_EYESHADOW, R.drawable.ic_makeup_eyeshadow, R.string.makeup_eye, getMakeupOptionItems(TYPE_MAKEUP_EYESHADOW), false),
                            new EffectButtonItem(TYPE_MAKEUP_EYELASH, R.drawable.ic_makeup_eyelash, R.string.makeup_eyelash, getMakeupOptionItems(TYPE_MAKEUP_EYELASH), false),
                            new EffectButtonItem(TYPE_MAKEUP_EYELIGHT, R.drawable.ic_makeup_eye_light, R.string.makeup_eyelight, getMakeupOptionItems(TYPE_MAKEUP_EYELIGHT), false),
                            new EffectButtonItem(TYPE_MAKEUP_PUPIL, R.drawable.ic_makeup_pupil, R.string.makeup_pupil, getMakeupOptionItems(TYPE_MAKEUP_PUPIL), false),
                            new EffectButtonItem(TYPE_MAKEUP_WOCAN, R.drawable.ic_makeup_eye_wocan, R.string.makeup_wocan, getMakeupOptionItems(TYPE_MAKEUP_WOCAN), false),
                            new EffectButtonItem(TYPE_MAKEUP_HAIR, R.drawable.ic_makeup_hair, R.string.makeup_hair, getMakeupOptionItems(TYPE_MAKEUP_HAIR), false),
                    }
            );
        } else {
            return new EffectButtonItem(
                    TYPE_MAKEUP,
                    new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                            new EffectButtonItem(TYPE_MAKEUP_LIP, R.drawable.ic_makeup_lip, R.string.makeup_lip, getMakeupOptionItems(TYPE_MAKEUP_LIP), false),
                            new EffectButtonItem(TYPE_MAKEUP_BLUSHER, R.drawable.ic_makeup_blusher, R.string.makeup_blusher, getMakeupOptionItems(TYPE_MAKEUP_BLUSHER), false),
                            new EffectButtonItem(TYPE_MAKEUP_FACIAL, R.drawable.ic_makeup_facial, R.string.makeup_facial, getMakeupOptionItems(TYPE_MAKEUP_FACIAL), false),
                            new EffectButtonItem(TYPE_MAKEUP_EYEBROW, R.drawable.ic_makeup_eyebrow, R.string.makeup_eyebrow, getMakeupOptionItems(TYPE_MAKEUP_EYEBROW), false),
                            new EffectButtonItem(TYPE_MAKEUP_EYESHADOW, R.drawable.ic_makeup_eyeshadow, R.string.makeup_eye, getMakeupOptionItems(TYPE_MAKEUP_EYESHADOW), false),
                            new EffectButtonItem(TYPE_MAKEUP_EYELASH, R.drawable.ic_makeup_eyelash, R.string.makeup_eyelash, getMakeupOptionItems(TYPE_MAKEUP_EYELASH), false),
                            new EffectButtonItem(TYPE_MAKEUP_EYELIGHT, R.drawable.ic_makeup_eye_light, R.string.makeup_eyelight, getMakeupOptionItems(TYPE_MAKEUP_EYELIGHT), false),
                            new EffectButtonItem(TYPE_MAKEUP_PUPIL, R.drawable.ic_makeup_pupil, R.string.makeup_pupil, getMakeupOptionItems(TYPE_MAKEUP_PUPIL), false),
                            new EffectButtonItem(TYPE_MAKEUP_HAIR, R.drawable.ic_makeup_hair, R.string.makeup_hair, getMakeupOptionItems(TYPE_MAKEUP_HAIR), false),
                    }
            );
        }

    }

    private  EffectButtonItem[] getMakeupOptionItems(int type) {
        switch (type & SUB_MASK) {
            case TYPE_MAKEUP_LIP:
                if (mEffectType == LITE_ASIA || mEffectType == LITE_NOT_ASIA) {
                    return new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                            new EffectButtonItem(TYPE_MAKEUP_LIP, R.drawable.ic_makeup_lip, R.string.lip_fuguhong, new ComposerNode("lip/lite/fuguhong", "Internal_Makeup_Lips",getDefaultIntensity(TYPE_MAKEUP_LIP,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_LIP, R.drawable.ic_makeup_lip, R.string.lip_shaonvfen, new ComposerNode("lip/lite/shaonvfen", "Internal_Makeup_Lips",getDefaultIntensity(TYPE_MAKEUP_LIP,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_LIP, R.drawable.ic_makeup_lip, R.string.lip_yuanqiju, new ComposerNode("lip/lite/yuanqiju", "Internal_Makeup_Lips",getDefaultIntensity(TYPE_MAKEUP_LIP,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_LIP, R.drawable.ic_makeup_lip, R.string.lip_xiyouse, new ComposerNode("lip/lite/xiyouse", "Internal_Makeup_Lips",getDefaultIntensity(TYPE_MAKEUP_LIP,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_LIP, R.drawable.ic_makeup_lip, R.string.lip_xiguahong, new ComposerNode("lip/lite/xiguahong", "Internal_Makeup_Lips",getDefaultIntensity(TYPE_MAKEUP_LIP,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_LIP, R.drawable.ic_makeup_lip, R.string.lip_sironghong, new ComposerNode("lip/lite/sironghong", "Internal_Makeup_Lips",getDefaultIntensity(TYPE_MAKEUP_LIP,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_LIP, R.drawable.ic_makeup_lip, R.string.lip_zangjuse, new ComposerNode("lip/lite/zangjuse", "Internal_Makeup_Lips",getDefaultIntensity(TYPE_MAKEUP_LIP,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_LIP, R.drawable.ic_makeup_lip, R.string.lip_meizise, new ComposerNode("lip/lite/meizise", "Internal_Makeup_Lips",getDefaultIntensity(TYPE_MAKEUP_LIP,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_LIP, R.drawable.ic_makeup_lip, R.string.lip_shanhuse, new ComposerNode("lip/lite/shanhuse", "Internal_Makeup_Lips",getDefaultIntensity(TYPE_MAKEUP_LIP,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_LIP, R.drawable.ic_makeup_lip, R.string.lip_doushafen, new ComposerNode("lip/lite/doushafen", "Internal_Makeup_Lips",getDefaultIntensity(TYPE_MAKEUP_LIP,null)[0]))

                    };

                }else {
                    ArrayList<ColorItem> colorItems= getColorForChoose(mEffectType, TYPE_MAKEUP_LIP);

                    return new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                            new EffectButtonItem(TYPE_MAKEUP_LIP, R.drawable.ic_makeup_lip, R.string.lip_liangze, new ComposerNode("lip/standard/liangze", new String[]{"Internal_Makeup_Lips", "R","G","B"},getDefaultIntensity(TYPE_MAKEUP_LIP,STANDARD_ASIA)),colorItems),
                            new EffectButtonItem(TYPE_MAKEUP_LIP, R.drawable.ic_makeup_lip, R.string.lip_wumian, new ComposerNode("lip/standard/wumian", new String[]{"Internal_Makeup_Lips", "R","G","B"},getDefaultIntensity(TYPE_MAKEUP_LIP,STANDARD_ASIA)),colorItems),
                            new EffectButtonItem(TYPE_MAKEUP_LIP, R.drawable.ic_makeup_lip, R.string.lip_yaochun, new ComposerNode("lip/standard/yaochun", new String[]{"Internal_Makeup_Lips", "R","G","B"},getDefaultIntensity(TYPE_MAKEUP_LIP,STANDARD_ASIA)),colorItems),
                            new EffectButtonItem(TYPE_MAKEUP_LIP, R.drawable.ic_makeup_lip, R.string.lip_yunruan, new ComposerNode("lip/standard/yunran", new String[]{"Internal_Makeup_Lips", "R","G","B"},getDefaultIntensity(TYPE_MAKEUP_LIP,STANDARD_ASIA)),colorItems)
                    };
                }

            case TYPE_MAKEUP_BLUSHER:
                if (mEffectType == LITE_ASIA || mEffectType == LITE_NOT_ASIA) {
                    return new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                            new EffectButtonItem(TYPE_MAKEUP_BLUSHER, R.drawable.ic_makeup_blusher, R.string.blusher_weixunfen, new ComposerNode("blush/lite/weixun", "Internal_Makeup_Blusher",getDefaultIntensity(TYPE_MAKEUP_BLUSHER,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_BLUSHER, R.drawable.ic_makeup_blusher, R.string.blusher_richang, new ComposerNode("blush/lite/richang", "Internal_Makeup_Blusher",getDefaultIntensity(TYPE_MAKEUP_BLUSHER,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_BLUSHER, R.drawable.ic_makeup_blusher, R.string.blusher_mitao, new ComposerNode("blush/lite/mitao", "Internal_Makeup_Blusher",getDefaultIntensity(TYPE_MAKEUP_BLUSHER,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_BLUSHER, R.drawable.ic_makeup_blusher, R.string.blusher_tiancheng, new ComposerNode("blush/lite/tiancheng", "Internal_Makeup_Blusher",getDefaultIntensity(TYPE_MAKEUP_BLUSHER,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_BLUSHER, R.drawable.ic_makeup_blusher, R.string.blusher_qiaopi, new ComposerNode("blush/lite/qiaopi", "Internal_Makeup_Blusher",getDefaultIntensity(TYPE_MAKEUP_BLUSHER,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_BLUSHER, R.drawable.ic_makeup_blusher, R.string.blusher_xinji, new ComposerNode("blush/lite/xinji", "Internal_Makeup_Blusher",getDefaultIntensity(TYPE_MAKEUP_BLUSHER,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_BLUSHER, R.drawable.ic_makeup_blusher, R.string.blusher_shaishang, new ComposerNode("blush/lite/shaishang", "Internal_Makeup_Blusher",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_EYE,null)[0])),
                    };

                }else {
                    ArrayList<ColorItem> colorItems= getColorForChoose(mEffectType, TYPE_MAKEUP_BLUSHER);

                    return new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                            new EffectButtonItem(TYPE_MAKEUP_BLUSHER, R.drawable.ic_makeup_blusher, R.string.blusher_mitao, new ComposerNode("blush/standard/mitao", new String[]{"Internal_Makeup_Blusher", "R","G","B"}, getDefaultIntensity(TYPE_MAKEUP_BLUSHER, STANDARD_ASIA)),colorItems),
                            new EffectButtonItem(TYPE_MAKEUP_BLUSHER, R.drawable.ic_makeup_blusher, R.string.blusher_weixun, new ComposerNode("blush/standard/weixun", new String[]{"Internal_Makeup_Blusher", "R","G","B"}, getDefaultIntensity(TYPE_MAKEUP_BLUSHER, STANDARD_ASIA)),colorItems),
                            new EffectButtonItem(TYPE_MAKEUP_BLUSHER, R.drawable.ic_makeup_blusher, R.string.blusher_yuanqi, new ComposerNode("blush/standard/yuanqi", new String[]{"Internal_Makeup_Blusher", "R","G","B"}, getDefaultIntensity(TYPE_MAKEUP_BLUSHER, STANDARD_ASIA)),colorItems),
                            new EffectButtonItem(TYPE_MAKEUP_BLUSHER, R.drawable.ic_makeup_blusher, R.string.blusher_qise, new ComposerNode("blush/standard/qise", new String[]{"Internal_Makeup_Blusher", "R","G","B"}, getDefaultIntensity(TYPE_MAKEUP_BLUSHER, STANDARD_ASIA)),colorItems),
                            new EffectButtonItem(TYPE_MAKEUP_BLUSHER, R.drawable.ic_makeup_blusher, R.string.blusher_shaishang, new ComposerNode("blush/standard/shaishang", new String[]{"Internal_Makeup_Blusher", "R","G","B"}, getDefaultIntensity(TYPE_MAKEUP_BLUSHER, STANDARD_ASIA)),colorItems),
                            new EffectButtonItem(TYPE_MAKEUP_BLUSHER, R.drawable.ic_makeup_blusher, R.string.blusher_rixi, new ComposerNode("blush/standard/rixi", new String[]{"Internal_Makeup_Blusher", "R","G","B"}, getDefaultIntensity(TYPE_MAKEUP_BLUSHER, STANDARD_ASIA)),colorItems),
                            new EffectButtonItem(TYPE_MAKEUP_BLUSHER, R.drawable.ic_makeup_blusher, R.string.blusher_suzui, new ComposerNode("blush/standard/suzui", new String[]{"Internal_Makeup_Blusher", "R","G","B"}, getDefaultIntensity(TYPE_MAKEUP_BLUSHER, STANDARD_ASIA)),colorItems),
                    };

                }
            //  {zh} 美瞳  {en} Pupils
            case TYPE_MAKEUP_PUPIL:
                if (mEffectType == LITE_ASIA || mEffectType == LITE_NOT_ASIA) {
                    return new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                            new EffectButtonItem(TYPE_MAKEUP_PUPIL, R.drawable.ic_makeup_pupil, R.string.pupil_hunxuezong, new ComposerNode("pupil/hunxuezong", "Internal_Makeup_Pupil",getDefaultIntensity(TYPE_MAKEUP_PUPIL,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_PUPIL, R.drawable.ic_makeup_pupil, R.string.pupil_kekezong, new ComposerNode("pupil/kekezong", "Internal_Makeup_Pupil",getDefaultIntensity(TYPE_MAKEUP_PUPIL,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_PUPIL, R.drawable.ic_makeup_pupil, R.string.pupil_mitaofen, new ComposerNode("pupil/mitaofen", "Internal_Makeup_Pupil",getDefaultIntensity(TYPE_MAKEUP_PUPIL,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_PUPIL, R.drawable.ic_makeup_pupil, R.string.pupil_shuiguanghei, new ComposerNode("pupil/shuiguanghei", "Internal_Makeup_Pupil",getDefaultIntensity(TYPE_MAKEUP_PUPIL,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_PUPIL, R.drawable.ic_makeup_pupil, R.string.pupil_xingkonglan, new ComposerNode("pupil/xingkonglan", "Internal_Makeup_Pupil",getDefaultIntensity(TYPE_MAKEUP_PUPIL,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_PUPIL, R.drawable.ic_makeup_pupil, R.string.pupil_chujianhui, new ComposerNode("pupil/chujianhui", "Internal_Makeup_Pupil",getDefaultIntensity(TYPE_MAKEUP_PUPIL,null)[0]))
                    };
                }else {
                    return new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                            new EffectButtonItem(TYPE_MAKEUP_PUPIL, R.drawable.ic_makeup_pupil, R.string.pupil_yuansheng, new ComposerNode("pupil/yuansheng", "Internal_Makeup_Pupil",getDefaultIntensity(TYPE_MAKEUP_PUPIL,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_PUPIL, R.drawable.ic_makeup_pupil, R.string.pupil_xinxinzi, new ComposerNode("pupil/xinxinzi", "Internal_Makeup_Pupil",getDefaultIntensity(TYPE_MAKEUP_PUPIL,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_PUPIL, R.drawable.ic_makeup_pupil, R.string.pupil_huoxing, new ComposerNode("pupil/huoxing", "Internal_Makeup_Pupil",getDefaultIntensity(TYPE_MAKEUP_PUPIL,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_PUPIL, R.drawable.ic_makeup_pupil, R.string.pupil_huilv, new ComposerNode("pupil/huilv", "Internal_Makeup_Pupil",getDefaultIntensity(TYPE_MAKEUP_PUPIL,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_PUPIL, R.drawable.ic_makeup_pupil, R.string.pupil_huitong, new ComposerNode("pupil/huitong", "Internal_Makeup_Pupil",getDefaultIntensity(TYPE_MAKEUP_PUPIL,null)[0])),
                    };

                }


            case TYPE_MAKEUP_HAIR:
                return new EffectButtonItem[]{
                        new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                        new EffectButtonItem(TYPE_MAKEUP_HAIR, R.drawable.ic_makeup_hair, R.string.hair_anlan, new ComposerNode("hair/anlan")),
                        new EffectButtonItem(TYPE_MAKEUP_HAIR, R.drawable.ic_makeup_hair, R.string.hair_molv, new ComposerNode("hair/molv")),
                        new EffectButtonItem(TYPE_MAKEUP_HAIR, R.drawable.ic_makeup_hair, R.string.hair_shenzong, new ComposerNode("hair/shenzong")),
                };
            case TYPE_MAKEUP_EYESHADOW:
                if (mEffectType == LITE_ASIA || mEffectType == LITE_NOT_ASIA) {
                    return new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                            new EffectButtonItem(TYPE_MAKEUP_EYESHADOW, R.drawable.ic_makeup_eyeshadow, R.string.eye_wanxiahong, new ComposerNode("eyeshadow/wanxiahong", "Internal_Makeup_Eye",getDefaultIntensity(TYPE_MAKEUP_EYESHADOW,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_EYESHADOW, R.drawable.ic_makeup_eyeshadow, R.string.eye_shaonvfen, new ComposerNode("eyeshadow/shaonvfen", "Internal_Makeup_Eye",getDefaultIntensity(TYPE_MAKEUP_EYESHADOW,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_EYESHADOW, R.drawable.ic_makeup_eyeshadow, R.string.eye_qizhifen, new ComposerNode("eyeshadow/qizhifen", "Internal_Makeup_Eye",getDefaultIntensity(TYPE_MAKEUP_EYESHADOW,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_EYESHADOW, R.drawable.ic_makeup_eyeshadow, R.string.eye_meizihong, new ComposerNode("eyeshadow/meizihong", "Internal_Makeup_Eye",getDefaultIntensity(TYPE_MAKEUP_EYESHADOW,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_EYESHADOW, R.drawable.ic_makeup_eyeshadow, R.string.eye_jiaotangzong, new ComposerNode("eyeshadow/jiaotangzong", "Internal_Makeup_Eye",getDefaultIntensity(TYPE_MAKEUP_EYESHADOW,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_EYESHADOW, R.drawable.ic_makeup_eyeshadow, R.string.eye_yuanqiju, new ComposerNode("eyeshadow/yuanqiju", "Internal_Makeup_Eye",getDefaultIntensity(TYPE_MAKEUP_EYESHADOW,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_EYESHADOW, R.drawable.ic_makeup_eyeshadow, R.string.eye_naichase, new ComposerNode("eyeshadow/naichase", "Internal_Makeup_Eye",getDefaultIntensity(TYPE_MAKEUP_EYESHADOW,null)[0])),
                    };
                }else {


                    return new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                            new EffectButtonItem(TYPE_MAKEUP_EYESHADOW, R.drawable.ic_makeup_eyeshadow, R.string.eye_dadizong, new ComposerNode("eyeshadow/dadizong", "Internal_Makeup_Eye",getDefaultIntensity(TYPE_MAKEUP_EYESHADOW,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_EYESHADOW, R.drawable.ic_makeup_eyeshadow, R.string.eye_hanxi, new ComposerNode("eyeshadow/hanxi", "Internal_Makeup_Eye",getDefaultIntensity(TYPE_MAKEUP_EYESHADOW,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_EYESHADOW, R.drawable.ic_makeup_eyeshadow, R.string.eye_nvshen, new ComposerNode("eyeshadow/nvshen", "Internal_Makeup_Eye",getDefaultIntensity(TYPE_MAKEUP_EYESHADOW,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_EYESHADOW, R.drawable.ic_makeup_eyeshadow, R.string.eye_xingzi, new ComposerNode("eyeshadow/xingzi", "Internal_Makeup_Eye",getDefaultIntensity(TYPE_MAKEUP_EYESHADOW,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_EYESHADOW, R.drawable.ic_makeup_eyeshadow, R.string.eye_bingtang, new ComposerNode("eyeshadow/bingtangshanzha", "Internal_Makeup_Eye",getDefaultIntensity(TYPE_MAKEUP_EYESHADOW,null)[0])),
                    };
                }

            case TYPE_MAKEUP_EYEBROW:
                if (mEffectType == LITE_ASIA || mEffectType == LITE_NOT_ASIA) {
                    return new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                            new EffectButtonItem(TYPE_MAKEUP_EYEBROW, R.drawable.ic_makeup_eyebrow, R.string.eyebrow_zongse, new ComposerNode("eyebrow/lite/BR01", "Internal_Makeup_Brow", getDefaultIntensity(TYPE_MAKEUP_EYEBROW, null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_EYEBROW, R.drawable.ic_makeup_eyebrow, R.string.eyebrow_cuhei, new ComposerNode("eyebrow/lite/BK01", "Internal_Makeup_Brow", getDefaultIntensity(TYPE_MAKEUP_EYEBROW, null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_EYEBROW, R.drawable.ic_makeup_eyebrow, R.string.eyebrow_heise, new ComposerNode("eyebrow/lite/BK02", "Internal_Makeup_Brow", getDefaultIntensity(TYPE_MAKEUP_EYEBROW, null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_EYEBROW, R.drawable.ic_makeup_eyebrow, R.string.eyebrow_xihei, new ComposerNode("eyebrow/lite/BK03", "Internal_Makeup_Brow", getDefaultIntensity(TYPE_MAKEUP_EYEBROW, null)[0])),
                    };
                }else{
                    ArrayList<ColorItem> colorItems= getColorForChoose(mEffectType, TYPE_MAKEUP_EYEBROW);

                    return new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                            new EffectButtonItem(TYPE_MAKEUP_EYEBROW, R.drawable.ic_makeup_eyebrow, R.string.eyebrow_biaozhun, new ComposerNode("eyebrow/standard/biaozhun", new String[]{"Internal_Makeup_Brow","R","G","B"}, getDefaultIntensity(TYPE_MAKEUP_EYEBROW, STANDARD_ASIA)),colorItems),
                            new EffectButtonItem(TYPE_MAKEUP_EYEBROW, R.drawable.ic_makeup_eyebrow, R.string.eyebrow_liuye, new ComposerNode("eyebrow/standard/liuye", new String[]{"Internal_Makeup_Brow","R","G","B"}, getDefaultIntensity(TYPE_MAKEUP_EYEBROW, STANDARD_ASIA)),colorItems),
                            new EffectButtonItem(TYPE_MAKEUP_EYEBROW, R.drawable.ic_makeup_eyebrow, R.string.eyebrow_rongrong, new ComposerNode("eyebrow/standard/rongrong", new String[]{"Internal_Makeup_Brow","R","G","B"}, getDefaultIntensity(TYPE_MAKEUP_EYEBROW, STANDARD_ASIA)),colorItems),
                            new EffectButtonItem(TYPE_MAKEUP_EYEBROW, R.drawable.ic_makeup_eyebrow, R.string.eyebrow_yesheng, new ComposerNode("eyebrow/standard/yesheng", new String[]{"Internal_Makeup_Brow","R","G","B"}, getDefaultIntensity(TYPE_MAKEUP_EYEBROW, STANDARD_ASIA)),colorItems),
                    };
                }
            case TYPE_MAKEUP_FACIAL:
                if (mEffectType == LITE_ASIA || mEffectType == LITE_NOT_ASIA){
                    return new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border,  R.string.close),
                            new EffectButtonItem(TYPE_MAKEUP_FACIAL, R.drawable.ic_makeup_facial,  R.string.facial_jingzhi,  new ComposerNode( "facial/jingzhi",  "Internal_Makeup_Facial",getDefaultIntensity(TYPE_MAKEUP_FACIAL,null)[0]))
                    };
                }else {
                    return new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border,  R.string.close),
                            new EffectButtonItem(TYPE_MAKEUP_FACIAL, R.drawable.ic_makeup_facial,  R.string.facial_ziran,  new ComposerNode( "facial/ziran",  "Internal_Makeup_Facial",getDefaultIntensity(TYPE_MAKEUP_FACIAL,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_FACIAL, R.drawable.ic_makeup_facial,  R.string.facial_xiaov,  new ComposerNode( "facial/xiaov",  "Internal_Makeup_Facial",getDefaultIntensity(TYPE_MAKEUP_FACIAL,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_FACIAL, R.drawable.ic_makeup_facial,  R.string.facial_fajixian,  new ComposerNode( "facial/fajixian",  "Internal_Makeup_Facial",getDefaultIntensity(TYPE_MAKEUP_FACIAL,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_FACIAL, R.drawable.ic_makeup_facial,  R.string.facial_gaoguang,  new ComposerNode( "facial/gaoguang",  "Internal_Makeup_Facial",getDefaultIntensity(TYPE_MAKEUP_FACIAL,null)[0]))
                    };

                }
                //  {zh} 睫毛  {en} Eyelashes
            case TYPE_MAKEUP_EYELASH:
                ArrayList<ColorItem> colorItems= getColorForChoose(mEffectType, TYPE_MAKEUP_EYELASH);
                return new EffectButtonItem[]{
                        new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border,  R.string.close),
                        new EffectButtonItem(TYPE_MAKEUP_EYELASH, R.drawable.ic_makeup_eyelash,  R.string.eyelash_ziran,  new ComposerNode( "eyelashes/ziran",  new String[]{"Internal_Makeup_Eyelash","R","G","B"},getDefaultIntensity(TYPE_MAKEUP_EYELASH,STANDARD_ASIA)),colorItems),
                        new EffectButtonItem(TYPE_MAKEUP_EYELASH, R.drawable.ic_makeup_eyelash,  R.string.eyelash_juanqiao,  new ComposerNode( "eyelashes/juanqiao",  new String[]{"Internal_Makeup_Eyelash","R","G","B"},getDefaultIntensity(TYPE_MAKEUP_EYELASH,STANDARD_ASIA)),colorItems),
                        new EffectButtonItem(TYPE_MAKEUP_EYELASH, R.drawable.ic_makeup_eyelash,  R.string.eyelash_chibang,  new ComposerNode( "eyelashes/chibang",  new String[]{"Internal_Makeup_Eyelash","R","G","B"},getDefaultIntensity(TYPE_MAKEUP_EYELASH,STANDARD_ASIA)),colorItems),
                        new EffectButtonItem(TYPE_MAKEUP_EYELASH, R.drawable.ic_makeup_eyelash,  R.string.eyelash_manhua,  new ComposerNode( "eyelashes/manhua",  new String[]{"Internal_Makeup_Eyelash","R","G","B"},getDefaultIntensity(TYPE_MAKEUP_EYELASH,STANDARD_ASIA)),colorItems),
                        new EffectButtonItem(TYPE_MAKEUP_EYELASH, R.drawable.ic_makeup_eyelash,  R.string.eyelash_xiachui,  new ComposerNode( "eyelashes/xiachui",  new String[]{"Internal_Makeup_Eyelash","R","G","B"},getDefaultIntensity(TYPE_MAKEUP_EYELASH,STANDARD_ASIA)),colorItems),
                };

            case TYPE_MAKEUP_WOCAN:
                if (mEffectType == LITE_ASIA || mEffectType == LITE_NOT_ASIA){
                    return new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border,  R.string.close),
                            new EffectButtonItem(TYPE_MAKEUP_WOCAN, R.drawable.ic_makeup_wocan,  R.string.wocan_ziran,  new ComposerNode( "wocan/ziran",  "Internal_Makeup_WoCan",getDefaultIntensity(TYPE_MAKEUP_WOCAN,null)[0])),
                    };
                }else {
                    return new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border,  R.string.close),
                            new EffectButtonItem(TYPE_MAKEUP_WOCAN, R.drawable.ic_makeup_wocan,  R.string.wocan_suyan,  new ComposerNode( "wocan/suyan",  "Internal_Makeup_WoCan",getDefaultIntensity(TYPE_MAKEUP_WOCAN,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_WOCAN, R.drawable.ic_makeup_wocan,  R.string.wocan_chulian,  new ComposerNode( "wocan/chulian",  "Internal_Makeup_WoCan",getDefaultIntensity(TYPE_MAKEUP_WOCAN,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_WOCAN, R.drawable.ic_makeup_wocan,  R.string.wocan_manhuayan,  new ComposerNode( "wocan/manhuayan",  "Internal_Makeup_WoCan",getDefaultIntensity(TYPE_MAKEUP_WOCAN,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_WOCAN, R.drawable.ic_makeup_wocan,  R.string.wocan_xiachui,  new ComposerNode( "wocan/xiachui",  "Internal_Makeup_WoCan",getDefaultIntensity(TYPE_MAKEUP_WOCAN,null)[0])),
                            new EffectButtonItem(TYPE_MAKEUP_WOCAN, R.drawable.ic_makeup_wocan,  R.string.wocan_taohua,  new ComposerNode( "wocan/taohua",  "Internal_Makeup_WoCan",getDefaultIntensity(TYPE_MAKEUP_WOCAN,null)[0])),
                    };

                }


            case TYPE_MAKEUP_EYELIGHT:
                return new EffectButtonItem[]{
                        new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border,  R.string.close),
                        new EffectButtonItem(TYPE_MAKEUP_EYELIGHT, R.drawable.ic_makeup_eye_light,  R.string.makeup_eyelight_ziranguang,  new ComposerNode( "eyelight/ziranguang",  "Internal_Makeup_EyeLight",getDefaultIntensity(TYPE_MAKEUP_EYELIGHT,null)[0])),
                        new EffectButtonItem(TYPE_MAKEUP_EYELIGHT, R.drawable.ic_makeup_eye_light,  R.string.makeup_eyelight_yueyaguang,  new ComposerNode( "eyelight/yueyaguang",  "Internal_Makeup_EyeLight",getDefaultIntensity(TYPE_MAKEUP_EYELIGHT,null)[0])),
                        new EffectButtonItem(TYPE_MAKEUP_EYELIGHT, R.drawable.ic_makeup_eye_light,  R.string.makeup_eyelight_juguangdeng,  new ComposerNode( "eyelight/juguangdeng",  "Internal_Makeup_EyeLight",getDefaultIntensity(TYPE_MAKEUP_EYELIGHT,null)[0]))
                };

            case TYPE_BEAUTY_RESHAPE_FACE_OVERALL:
                if (mEffectType == STANDARD_ASIA || mEffectType == STANDARD_NOT_ASIA) {
                    String reshapeNode = reshapeNode(mEffectType);
                    String reshapeNode_face_overall = "reshape_standard_girl";
                    String reshapeNode_face_overall_boy = "reshape_standard_boy";
                    String reshapeNode_face_overall_nature = "reshape_standard_nature";
                    return new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border,  R.string.close),
                            new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE_OVERALL1, R.drawable.ic_beauty_cheek_reshape_default, R.string.beauty_reshape_face_overall, new ComposerNode(reshapeNode, "Internal_Deform_Overall", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_FACE_OVERALL1,mEffectType, true)[0]),true),
                            new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE_OVERALL2, R.drawable.ic_beauty_cheek_reshape_nvshen_beta, R.string.beauty_reshape_face_beta_girl, new ComposerNode(reshapeNode_face_overall, "Internal_Deform_Overall", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_FACE_OVERALL2,mEffectType)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE_OVERALL3, R.drawable.ic_beauty_cheek_reshape_nanshen_beta, R.string.beauty_reshape_face_beta_boy, new ComposerNode(reshapeNode_face_overall_boy, "Internal_Deform_Overall", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_FACE_OVERALL3,mEffectType)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE_OVERALL4, R.drawable.ic_beauty_cheek_reshape_ziranlian_beta, R.string.beauty_reshape_face_beta_nature, new ComposerNode(reshapeNode_face_overall_nature, "Internal_Deform_Overall", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_FACE_OVERALL4,mEffectType)[0])),
                    };
                }
                else {
                    return new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border,  R.string.close),
                            new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE_OVERALL1, R.drawable.ic_beauty_cheek_reshape, R.string.beauty_reshape_face_overall, new ComposerNode(NODE_RESHAPE_LITE, "Internal_Deform_Overall", getDefaultIntensity(TYPE_BEAUTY_RESHAPE_FACE_OVERALL1,mEffectType)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE_OVERALL2, R.drawable.ic_beauty_cheek_reshape_nanshen,  R.string.beauty_reshape_face_nanshen,  new ComposerNode( "reshape_boy",  "Internal_Deform_Overall",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_FACE_OVERALL2,mEffectType)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE_OVERALL3, R.drawable.ic_beauty_cheek_reshape_nvshen,  R.string.beauty_reshape_face_nvshen,  new ComposerNode( "reshape_girl",  "Internal_Deform_Overall",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_FACE_OVERALL3,mEffectType)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_RESHAPE_FACE_OVERALL4, R.drawable.ic_beauty_cheek_reshape_ziranlian,  R.string.beauty_reshape_face_ziranlian,  new ComposerNode( "reshape_nature",  "Internal_Deform_Overall",getDefaultIntensity(TYPE_BEAUTY_RESHAPE_FACE_OVERALL4,mEffectType)[0]))
                    };
                }
            case TYPE_BEAUTY_FACE_WHITEN:
                    return new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border,  R.string.close),
                            new EffectButtonItem(TYPE_BEAUTY_FACE_WHITEN_DEFAULT, R.drawable.ic_beauty_whiten_default, R.string.beauty_face_whiten, new ComposerNode(mEffectType == STANDARD_ASIA || mEffectType == STANDARD_NOT_ASIA ? NODE_BEAUTY_STANDARD : NODE_BEAUTY_LITE, "whiten", getDefaultIntensity(TYPE_BEAUTY_FACE_WHITEN_DEFAULT,mEffectType)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_FACE_WHITEN_LENGBAI, R.drawable.ic_beauty_whiten_cool, R.string.style_makeup_baixi, new ComposerNode( mEffectType == STANDARD_ASIA || mEffectType == STANDARD_NOT_ASIA ? "beauty_Android_lengbai_standard" : "beauty_Android_lengbai_lite",  "whiten",getDefaultIntensity(TYPE_BEAUTY_FACE_WHITEN_LENGBAI,mEffectType)[0])),
                            new EffectButtonItem(TYPE_BEAUTY_FACE_WHITEN_FENBAI, R.drawable.ic_beauty_whiten_pink, R.string.beauty_face_whiten_fenbai, new ComposerNode( mEffectType == STANDARD_ASIA || mEffectType == STANDARD_NOT_ASIA ? "beauty_Android_fenbai_standard" : "beauty_Android_fenbai_lite",  "whiten",getDefaultIntensity(TYPE_BEAUTY_FACE_WHITEN_FENBAI,mEffectType)[0])),
                    };
        }
        return null;
    }

//    private  EffectButtonItem getStyleMakeupItems() {
//        return new EffectButtonItem(
//                TYPE_STYLE_MAKEUP,
//                new EffectButtonItem[]{
//                        new EffectButtonItem(TYPE_CLOSE, R.drawable.clear, R.string.close),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_baicha, R.string.style_makeup_baicha, new ComposerNode("style_makeup/baicha", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_xiaoxiong, R.string.style_makeup_xiaoxiong, new ComposerNode("style_makeup/meiyouxiaoxiong", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_lantong, R.string.style_makeup_bingchuan, new ComposerNode("style_makeup/bingchuan", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_qise, R.string.style_makeup_qise, new ComposerNode("style_makeup/qise", new String[]{"Filter_ALL", "Makeup_ALL"}, getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_aidou, R.string.style_makeup_aidou, new ComposerNode("style_makeup/aidou", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_youya, R.string.style_makeup_youya, new ComposerNode("style_makeup/youya", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_cwei, R.string.style_makeup_cwei, new ComposerNode("style_makeup/cwei", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_nuannan, R.string.style_makeup_nuannan, new ComposerNode("style_makeup/nuannan", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_baixi, R.string.style_makeup_baixi, new ComposerNode("style_makeup/baixi", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_wennuan, R.string.style_makeup_wennuan, new ComposerNode("style_makeup/wennuan", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_shensui, R.string.style_makeup_shensui, new ComposerNode("style_makeup/shensui", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_tianmei, R.string.style_makeup_tianmei, new ComposerNode("style_makeup/tianmei", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_duanmei, R.string.style_makeup_duanmei, new ComposerNode("style_makeup/duanmei", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_oumei, R.string.style_makeup_oumei, new ComposerNode("style_makeup/oumei", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_zhigan, R.string.style_makeup_zhigan, new ComposerNode("style_makeup/zhigan", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_hanxi, R.string.style_makeup_hanxi, new ComposerNode("style_makeup/hanxi", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_yuanqi, R.string.style_makeup_yuanqi, new ComposerNode("style_makeup/yuanqi", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_wumei, R.string.style_makeup_wumei, new ComposerNode("style_makeup/wumei", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_dazzling, R.string.style_makeup_dazzling_look, new ComposerNode("style_makeup/dazzling_look", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_scarlett, R.string.style_makeup_scarlett, new ComposerNode("style_makeup/scarlett", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA))),
//                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_yeshengqiangwei, R.string.style_makeup_yeshengqiangwei, new ComposerNode("style_makeup/yeshengqiangwei", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)))
////                        new EffectButtonItem(TYPE_STYLE_MAKEUP, R.drawable.icon_yapi, R.string.style_makeup_yapizhuang, new ComposerNode("style_makeup/yapizhuang", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)))
//                },
//                false
//        );
//    }

    private  EffectButtonItem getStyleMakeup2DItems() {
        return new EffectButtonItem(
                TYPE_STYLE_MAKEUP_2D,
                R.drawable.clear,
                R.string.sticker_2d,
                new EffectButtonItem[]{
                        new EffectButtonItem(TYPE_CLOSE, R.drawable.clear, R.string.close),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_baicha, R.string.style_makeup_baicha, new ComposerNode("style_makeup/baicha", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "baicha"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_xiaoxiong, R.string.style_makeup_xiaoxiong, new ComposerNode("style_makeup/meiyouxiaoxiong", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)),"meiyouxiaoxiong"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_lantong, R.string.style_makeup_bingchuan, new ComposerNode("style_makeup/bingchuan", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "bingchuan"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_qise, R.string.style_makeup_qise, new ComposerNode("style_makeup/qise", new String[]{"Filter_ALL", "Makeup_ALL"}, getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "qise"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_aidou, R.string.style_makeup_aidou, new ComposerNode("style_makeup/aidou", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "aidou"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_youya, R.string.style_makeup_youya, new ComposerNode("style_makeup/youya", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "youya"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_cwei, R.string.style_makeup_cwei, new ComposerNode("style_makeup/cwei", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "cwei"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_nuannan, R.string.style_makeup_nuannan, new ComposerNode("style_makeup/nuannan", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "nuannan"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_baixi, R.string.style_makeup_baixi, new ComposerNode("style_makeup/baixi", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "baixi"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_wennuan, R.string.style_makeup_wennuan, new ComposerNode("style_makeup/wennuan", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "wennuan"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_shensui, R.string.style_makeup_shensui, new ComposerNode("style_makeup/shensui", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "shensui"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_tianmei, R.string.style_makeup_tianmei, new ComposerNode("style_makeup/tianmei", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "tianmei"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_duanmei, R.string.style_makeup_duanmei, new ComposerNode("style_makeup/duanmei", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "duanmei"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_oumei, R.string.style_makeup_oumei, new ComposerNode("style_makeup/oumei", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "oumei"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_zhigan, R.string.style_makeup_zhigan, new ComposerNode("style_makeup/zhigan", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "zhigan"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_hanxi, R.string.style_makeup_hanxi, new ComposerNode("style_makeup/hanxi", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "hanxi"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_yuanqi, R.string.style_makeup_yuanqi, new ComposerNode("style_makeup/yuanqi", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "yuanqi"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_shuiwu, R.string.style_makeup_shuiwu, new ComposerNode("style_makeup/shuiwu", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "shuiwu"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_naicha, R.string.style_makeup_naicha, new ComposerNode("style_makeup/naicha", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "naicha"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_mitaofen, R.string.style_makeup_mitaofen, new ComposerNode("style_makeup/mitaofen3", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "mitaofen3"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_fenxia, R.string.style_makeup_fenxia, new ComposerNode("style_makeup/fenxia", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "fenxia"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_tiansang, R.string.style_makeup_tiansang, new ComposerNode("style_makeup/tiansang", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "tiansang"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_insfeng, R.string.style_makeup_insfeng, new ComposerNode("style_makeup/insfeng2", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "insfeng2"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_heisirong, R.string.style_makeup_heisirong, new ComposerNode("style_makeup/heisirong", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "heisirong"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_nyushen, R.string.style_makeup_nvsheng, new ComposerNode("style_makeup/nyushen", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "nyushen"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_qizhi, R.string.style_makeup_qizhi, new ComposerNode("style_makeup/qizhi", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "qizhi"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_naixiong, R.string.style_makeup_naixiong, new ComposerNode("style_makeup/naixiong", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "naixiong"),

                },
                false
        );
    }

    private  EffectButtonItem getLocalStyleMakeup2DItems() {
        return new EffectButtonItem(
                TYPE_STYLE_MAKEUP_2D,
                R.drawable.clear,
                R.string.sticker_2d,
                new EffectButtonItem[]{
                        new EffectButtonItem(TYPE_CLOSE, R.drawable.clear, R.string.close),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_baicha, R.string.style_makeup_baicha, new ComposerNode("style_makeup_local/baicha", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "baicha"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_xiaoxiong, R.string.style_makeup_xiaoxiong, new ComposerNode("style_makeup_local/meiyouxiaoxiong", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)),"meiyouxiaoxiong"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_lantong, R.string.style_makeup_bingchuan, new ComposerNode("style_makeup_local/bingchuan", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "bingchuan"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_2D, R.drawable.icon_qise, R.string.style_makeup_qise, new ComposerNode("style_makeup_local/qise", new String[]{"Filter_ALL", "Makeup_ALL"}, getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "qise"),
                },
                false
        );
    }


    private  EffectButtonItem getStyleMakeup3DItems() {
        return new EffectButtonItem(
                TYPE_STYLE_MAKEUP_3D,
                R.drawable.clear,
                R.string.sticker_3d,
                new EffectButtonItem[]{
                        new EffectButtonItem(TYPE_CLOSE, R.drawable.clear, R.string.close),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_3D, R.drawable.icon_ailingzhuang, R.string.style_makeup_baikaishui, new ComposerNode("style_makeup/baikaishui", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "baikaishui"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_3D, R.drawable.icon_ailingzhuang, R.string.style_makeup_hanguoxuemei,R.string.style_makeup_hanguoxuemei_toast, new ComposerNode("style_makeup/hanguoxuemei", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "hanguoxuemei"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_3D, R.drawable.icon_ailingzhuang, R.string.style_makeup_nenmozhuang, new ComposerNode("style_makeup/nenmozhuang", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "nenmozhuang"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_3D, R.drawable.icon_ailingzhuang, R.string.style_makeup_touxianggan,R.string.style_makeup_touxianggan_toast, new ComposerNode("style_makeup/touxianggan", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "touxianggan"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_3D, R.drawable.icon_ailingzhuang, R.string.style_makeup_wodenvhai,R.string.style_makeup_wodenvhai_toast, new ComposerNode("style_makeup/wodenvhai", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "wodenvhai"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_3D, R.drawable.icon_ailingzhuang, R.string.style_makeup_ailingzhuang,R.string.style_makeup_ailingzhuang_tost, new ComposerNode("style_makeup/ailingzhuang", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "ailingzhuang"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_3D, R.drawable.icon_dongnanyachunyu, R.string.style_makeup_dongnanyachunyu,R.string.style_makeup_dongnanyachunyu_tost, new ComposerNode("style_makeup/dongnanyachunyu", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "dongnanyachunyu"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_3D, R.drawable.icon_manhuashaonyu, R.string.style_makeup_manhuashaonyu, new ComposerNode("style_makeup/manhuashaonyu", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "manhuashaonyu"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_3D, R.drawable.icon_zhiyafangzhuang, R.string.style_makeup_zhiyafangzhuang, new ComposerNode("style_makeup/zhiyafangzhuang", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "zhiyafangzhuang")
                },
                false
        );
    }

    private  EffectButtonItem getLocalStyleMakeup3DItems() {
        return new EffectButtonItem(
                TYPE_STYLE_MAKEUP_3D,
                R.drawable.clear,
                R.string.sticker_3d,
                new EffectButtonItem[]{
                        new EffectButtonItem(TYPE_CLOSE, R.drawable.clear, R.string.close),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_3D, R.drawable.icon_ailingzhuang, R.string.style_makeup_ailingzhuang,R.string.style_makeup_ailingzhuang_tost, new ComposerNode("style_makeup_local/ailingzhuang", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "ailingzhuang"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_3D, R.drawable.icon_dongnanyachunyu, R.string.style_makeup_dongnanyachunyu,R.string.style_makeup_dongnanyachunyu_tost, new ComposerNode("style_makeup_local/dongnanyachunyu", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "dongnanyachunyu"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_3D, R.drawable.icon_manhuashaonyu, R.string.style_makeup_manhuashaonyu, new ComposerNode("style_makeup_local/manhuashaonyu", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "manhuashaonyu"),
                        new EffectButtonItem(TYPE_STYLE_MAKEUP_3D, R.drawable.icon_zhiyafangzhuang, R.string.style_makeup_zhiyafangzhuang, new ComposerNode("style_makeup_local/zhiyafangzhuang", new String[]{"Filter_ALL", "Makeup_ALL"},getDefaultIntensity(TYPE_STYLE_MAKEUP, LITE_ASIA)), "zhiyafangzhuang")
                },
                false
        );
    }

    private  EffectButtonItem getPaletteItems() {
        return new EffectButtonItem(
                TYPE_PALETTE,
                new EffectButtonItem[]{
                        new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                        new EffectButtonItem(TYPE_PALETTE_TEMPERATURE, R.drawable.icon_temperature, R.string.palette_temperature, new ComposerNode("palette/color", "Intensity_Temperature",getDefaultIntensity(TYPE_PALETTE_TEMPERATURE,null,true)[0]),true),
                        new EffectButtonItem(TYPE_PALETTE_TONE, R.drawable.icon_tone, R.string.palette_tone, new ComposerNode("palette/light", "Intensity_Hue",getDefaultIntensity(TYPE_PALETTE_TONE,null,true)[0]),true),
                        new EffectButtonItem(TYPE_PALETTE_SATURATION, R.drawable.icon_saturation, R.string.palette_saturation, new ComposerNode("palette/color", "Intensity_Saturation",getDefaultIntensity(TYPE_PALETTE_SATURATION,null,true)[0]),true),
                        new EffectButtonItem(TYPE_PALETTE_BRIGHTNESS, R.drawable.icon_brightness, R.string.palette_brightness, new ComposerNode("palette/light", "Intensity_Light",getDefaultIntensity(TYPE_PALETTE_BRIGHTNESS,null,true)[0]),true),
                        new EffectButtonItem(TYPE_PALETTE_CONTRAST, R.drawable.icon_contrast, R.string.palette_contrast, new ComposerNode("palette/contrast", "Intensity_Contrast",getDefaultIntensity(TYPE_PALETTE_CONTRAST,null,true)[0]),true),
                        new EffectButtonItem(TYPE_PALETTE_HIGHLIGHT, R.drawable.icon_highlight, R.string.palette_highlight, new ComposerNode("palette/light", "Intensity_Highlight",getDefaultIntensity(TYPE_PALETTE_HIGHLIGHT,null,true)[0]),true),
                        new EffectButtonItem(TYPE_PALETTE_SHADOW, R.drawable.icon_shadow, R.string.palette_shadow, new ComposerNode("palette/color", "Intensity_Shadow",getDefaultIntensity(TYPE_PALETTE_SHADOW,null,false)[0]),false),
                        new EffectButtonItem(TYPE_PALETTE_LIGHT_SENSATION, R.drawable.icon_light_perception, R.string.palette_light_sensation, new ComposerNode("palette/light", "Intensity_Light_Sensation",getDefaultIntensity(TYPE_PALETTE_LIGHT_SENSATION,null,true)[0]),true),
                        new EffectButtonItem(TYPE_PALETTE_PARTICLE, R.drawable.icon_particles, R.string.palette_particle, new ComposerNode("palette/particle", "Intensity_Particle",getDefaultIntensity(TYPE_PALETTE_PARTICLE,null,false)[0]),false),
                        new EffectButtonItem(TYPE_PALETTE_FADE, R.drawable.icon_fading, R.string.palette_fade, new ComposerNode("palette/color", "Intensity_Fade",getDefaultIntensity(TYPE_PALETTE_FADE,null,false)[0]),false),
                        new EffectButtonItem(TYPE_PALETTE_VIGNETTING, R.drawable.icon_vignette, R.string.palette_vignetting, new ComposerNode("palette/vignette", "Intensity_Vignette",getDefaultIntensity(TYPE_PALETTE_VIGNETTING,null,false)[0]),false)
                }
        );
    }

    private  EffectButtonItem getBlackTechnologyItems() {
        if (mEffectType == LITE_ASIA || mEffectType == LITE_NOT_ASIA) {
            return new EffectButtonItem(TYPE_BLACK_TECHNOLOGY,
                    new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE,R.drawable.clear_no_border,R.string.close),
                            new EffectButtonItem(TYPE_BLACK_TECHNOLOGY_ANTI_ACNE_SPOT_LOW, R.drawable.icon_black_technology_anti_apot, R.string.black_technology_anti_acne_spot,
                                    new ComposerNode(
                                            "hack/acne_removal_lite",
                                            "clean",
                                            0.5f
                                    ),
                                    false
                            )
                    }
            );
        } else {
            return new EffectButtonItem(TYPE_BLACK_TECHNOLOGY,
                    new EffectButtonItem[]{
                            new EffectButtonItem(TYPE_CLOSE, R.drawable.clear_no_border, R.string.close),
                            new EffectButtonItem(TYPE_BLACK_TECHNOLOGY_YUNFU, R.drawable.icon_black_technology_levelling, R.string.tab_yunfu,
                                    new ComposerNode(
                                            "hack/yunfu_fengying_liuchanglian_standard",
                                            "Levelling",
                                            0
                                    ),
                                    false
                            ),
                            new EffectButtonItem(TYPE_BLACK_TECHNOLOGY_FULING, R.drawable.icon_black_technology_full, R.string.tab_fengying,
                                    new ComposerNode(
                                            "hack/yunfu_fengying_liuchanglian_standard",
                                            "Full",
                                            0
                                    ),
                                    false
                            ),
                            new EffectButtonItem(TYPE_BLACK_TECHNOLOGY_LUNKUOPINGHUA, R.drawable.icon_black_technology_fluid_face, R.string.tab_liuchanglian,
                                    new ComposerNode(
                                            "hack/yunfu_fengying_liuchanglian_standard",
                                            "Fluid_Face",
                                            0
                                    ),
                                    false
                            )
                    }
            );
        }
    }

    private  String beautyNode(EffectType mEffectType) {

        return mEffectType == STANDARD_ASIA || mEffectType == STANDARD_NOT_ASIA ? NODE_BEAUTY_STANDARD : NODE_BEAUTY_LITE;
    }

    private  String reshapeNode(EffectType mEffectType) {
        return mEffectType == STANDARD_ASIA || mEffectType == STANDARD_NOT_ASIA ? NODE_RESHAPE_STANDARD : NODE_RESHAPE_LITE;
    }


    private  EffectButtonItem getBackGoundItems() {
        return new EffectButtonItem(
                TYPE_BACK_GROUND,
                new EffectButtonItem[]{
                        new EffectButtonItem(TYPE_BACK_GROUND, R.drawable.ic_background_blur_highlight, R.string.sticker_blur_bg_default, new ComposerNode("background_blur", "",getDefaultIntensity(TYPE_PALETTE_TEMPERATURE,null,false)[0]),false),
                        new EffectButtonItem(TYPE_BACK_GROUND, R.drawable.ic_background_blur_strong, R.string.tab_background_blur_strong, new ComposerNode("bg_blur_strong", "",getDefaultIntensity(TYPE_PALETTE_TONE,null,false)[0]),false),
                        new EffectButtonItem(TYPE_BACK_GROUND, R.drawable.ic_background_blur_in, R.string.tab_background_blur_in, new ComposerNode("background_blur_in", "Blur_intensity",getDefaultIntensity(TYPE_PALETTE_SATURATION,null,true)[0]),true)
                }
        );
    }

    public static String translateText(Context context, String srcText){
        if (TextDictionary == null) {
            return "";
        }
        Integer textId = TextDictionary.get(srcText);
        if (textId == null) {
            return "";
        }
        String targetText = context.getResources().getText(textId).toString();
        if (TextUtils.isEmpty(targetText)) {
            return "";
        }
        return targetText;
    }

    public static Integer getTranslationId(String srcText){
        if (TextDictionary != null) {
            if (TextDictionary.containsKey(srcText)) {
                return TextDictionary.get(srcText);
            }
        }
        return 0;
    }

}
