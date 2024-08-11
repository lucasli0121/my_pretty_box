package com.effectsar.labcv.effect.manager;

import android.content.Context;
import android.text.TextUtils;


import com.effectsar.labcv.common.database.LocalParamHelper;
import com.effectsar.labcv.core.effect.EffectResourceProvider;
import com.effectsar.labcv.R;
import com.effectsar.labcv.effect.model.EffectButtonItem;
import com.effectsar.labcv.effect.model.FilterItem;
import com.effectsar.labcv.core.effect.EffectResourceHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterDataManager {
    private Context context = null;

    public static final String FILTER_CATEGORY_PORTRAIT = "filter_category_portrait";
    public static final String FILTER_CATEGORY_CUISINE = "filter_category_cuisine";
    public static final String FILTER_CATEGORY_LANDSCAPE = "filter_category_landscape";
    public static final String FILTER_CATEGORY_VINTAGE = "filter_category_vintage";
    public static final String FILTER_CATEGORY_STYLIZATION = "filter_category_stylization";

    public FilterDataManager(Context context) {
        this.context = context;
        mResourceProvider = new EffectResourceHelper(context);
    }

    private  final Map<Integer, FilterItem> mSavedItems = new HashMap<>();

    private EffectResourceProvider mResourceProvider;

    private List<FilterItem> mItems;

    public FilterItem getCategory(String category){
        float defaultIntensity = EffectDataManager.getDefaultIntensity(EffectDataManager.TYPE_FILTER,null)[0];
        switch (category) {
            case FILTER_CATEGORY_PORTRAIT:
                return new FilterItem(EffectDataManager.TYPE_FILTER, R.string.tab_portrait,R.drawable.icon_filter_category_portrait, new FilterItem[]{
                        new FilterItem(EffectDataManager.TYPE_CLOSE, R.string.filter_normal, R.drawable.clear),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_chalk, R.drawable.icon_filter_roubai, "Filter_01_38", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_ziran, R.drawable.icon_filter_ziran, "Filter_38_F1", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_chujian, R.drawable.icon_filter_chujian, "Filter_25_Po3", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_qingtou, R.drawable.icon_filter_qingtou, "Filter_37_L5", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_wenrou, R.drawable.icon_filter_wenrou, "Filter_23_Po1", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_lianaichaotian, R.drawable.icon_filter_lianaichaotian, "Filter_24_Po2", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_soft, R.drawable.icon_filter_soft, "Filter_28_Po6", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_lengbaipi, R.drawable.icon_filter_lengbaipi, "Filter_48_4001", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_touliang, R.drawable.icon_filter_touliang, "Filter_49_4002", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_liangju, R.drawable.icon_filter_liangju, "Filter_50_4003", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_gaojihui, R.drawable.icon_filter_gaojihui, "Filter_32_Po10", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_haibianrenxiang, R.drawable.icon_filter_haibianrenxiang, "Filter_31_Po9", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_lengyang, R.drawable.icon_filter_lengyang, "Filter_30_Po8", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_hongzong, R.drawable.icon_filter_hongzong, "Filter_36_L4", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_naicha, R.drawable.icon_filter_naicha, "Filter_27_Po5", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_andiao, R.drawable.icon_filter_andiao, "Filter_26_Po4", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_musi, R.drawable.icon_filter_qiannuan, "Filter_10_11", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_hutaomu, R.drawable.icon_filter_hutaomu, "Filter_51_4004", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_shenhe, R.drawable.icon_filter_shenhe, "Filter_52_4005", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_degula, R.drawable.icon_filter_degula, "Filter_53_4006", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_cream, R.drawable.icon_filter_naiyou, "Filter_02_14", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_makalong, R.drawable.icon_filter_makalong, "Filter_07_06", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_oxgen, R.drawable.icon_filter_yangqi, "Filter_03_20", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_wuyu, R.drawable.icon_filter_wuyu, "Filter_11_09", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_lolita, R.drawable.icon_filter_luolita, "Filter_05_10", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_campan, R.drawable.icon_filter_jiegeng, "Filter_04_12", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_lise, R.drawable.icon_filter_lise, "Filter_54_4007", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_yelin, R.drawable.icon_filter_yelin, "Filter_55_4008", defaultIntensity),
                }, false);
            case FILTER_CATEGORY_CUISINE:
                return new FilterItem(EffectDataManager.TYPE_FILTER, R.string.tab_cuisine,R.drawable.icon_filter_category_cusine, new FilterItem[]{
                        new FilterItem(EffectDataManager.TYPE_CLOSE, R.string.filter_normal, R.drawable.clear),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_shise, R.drawable.icon_filter_shise, "Filter_41_F4", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_chuanwei, R.drawable.icon_filter_chuanwei, "Filter_42_F5", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_suda, R.drawable.icon_filter_suda, "Filter_39_F2", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_nuanshi, R.drawable.icon_filter_nuanshi, "Filter_56_4009", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_qipaoshui, R.drawable.icon_filter_qipaoshui, "Filter_57_4010", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_liaoli, R.drawable.icon_filter_liaoli, "Filter_58_4011", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_tanshao, R.drawable.icon_filter_tanshao, "Filter_59_4012", defaultIntensity),
                }, false);
            case FILTER_CATEGORY_LANDSCAPE:
                return new FilterItem(EffectDataManager.TYPE_FILTER, R.string.tab_landscape,R.drawable.icon_filter_category_landscape, new FilterItem[]{
                        new FilterItem(EffectDataManager.TYPE_CLOSE, R.string.filter_normal, R.drawable.clear),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_yinhua, R.drawable.icon_filter_yinghua, "Filter_09_19", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_jiazhou, R.drawable.icon_filter_jiazhou, "Filter_40_F3", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_mitao, R.drawable.icon_filter_mitao, "Filter_06_03", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_qianxia, R.drawable.icon_filter_qianxia, "Filter_34_L2", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_haidao, R.drawable.icon_filter_haidao, "Filter_33_L1", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_beihaidao, R.drawable.icon_filter_beihaidao, "Filter_12_08", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_xiyang, R.drawable.icon_filter_xiyang, "Filter_29_Po7", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_yese, R.drawable.icon_filter_yese, "Filter_35_L3", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_jingdu, R.drawable.icon_filter_jingdu, "Filter_60_4013", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_haian, R.drawable.icon_filter_haian, "Filter_61_4014", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_lyuyan, R.drawable.icon_filter_lyuyan, "Filter_62_4015", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_wanying, R.drawable.icon_filter_wanying, "Filter_63_4016", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_qingcheng, R.drawable.icon_filter_qingcheng, "Filter_64_4017", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_aizhicheng, R.drawable.icon_filter_aizhicheng, "Filter_65_4018", defaultIntensity),
                }, false);

            case FILTER_CATEGORY_VINTAGE:
                return new FilterItem(EffectDataManager.TYPE_FILTER, R.string.tab_vintage,R.drawable.icon_filter_category_vintage, new FilterItem[]{
                        new FilterItem(EffectDataManager.TYPE_CLOSE, R.string.filter_normal, R.drawable.clear),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_meishijiaopian, R.drawable.icon_filter_meishijiaopian, "Filter_43_S1", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_hongsefugu, R.drawable.icon_filter_hongsefugu, "Filter_44_S2", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_lvtu, R.drawable.icon_filter_lyutu, "Filter_45_S3", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_nuanhuang, R.drawable.icon_filter_nuanhuang, "Filter_46_S4", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_landiaojiaopian, R.drawable.icon_filter_landiaojiaopian, "Filter_47_S5", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_riza, R.drawable.icon_filter_riza, "Filter_13_02", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_kangtaishi, R.drawable.icon_filter_ct, "Filter_66_4019", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_aikefa, R.drawable.icon_filter_ek, "Filter_67_4020", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_fushi, R.drawable.icon_filter_fj, "Filter_68_4021", defaultIntensity),
                }, false);
            case FILTER_CATEGORY_STYLIZATION:
                return new FilterItem(EffectDataManager.TYPE_FILTER, R.string.tab_stylization,R.drawable.icon_filter_category_stylization, new FilterItem[]{
                        new FilterItem(EffectDataManager.TYPE_CLOSE, R.string.filter_normal, R.drawable.clear),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_hongchun, R.drawable.icon_filter_hongchun, "Filter_19_37", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_julandiao, R.drawable.icon_filter_julandiao, "Filter_20_05", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_tuise, R.drawable.icon_filter_tuise, "Filter_21_01", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_heibai, R.drawable.icon_filter_heibai, "Filter_22_16", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_heijin, R.drawable.icon_filter_heijin, "Filter_69_4022", defaultIntensity),
                        new FilterItem(EffectDataManager.TYPE_FILTER, R.string.filter_heben, R.drawable.icon_filter_heben, "Filter_70_4023", defaultIntensity),
                }, false);

            default:
                return null;
        }
    }

    public FilterItem getItems() {
        FilterItem item = mSavedItems.get(EffectDataManager.TYPE_FILTER);
        if (item != null) {
            return item;
        }
        item = new FilterItem(EffectDataManager.TYPE_FILTER, new FilterItem[]{
                new FilterItem(EffectDataManager.TYPE_CLOSE, R.string.filter_normal, R.drawable.clear_no_border),
                getCategory(FILTER_CATEGORY_PORTRAIT),
                getCategory(FILTER_CATEGORY_CUISINE),
                getCategory(FILTER_CATEGORY_LANDSCAPE),
                getCategory(FILTER_CATEGORY_VINTAGE),
                getCategory(FILTER_CATEGORY_STYLIZATION),
        });
        if (item != null) {
            mSavedItems.put(EffectDataManager.TYPE_FILTER, item);
        }
        return item;
    }

    public   List<EffectButtonItem> allItems() {
        List<EffectButtonItem> items = new ArrayList<>();
        for (Map.Entry<Integer, FilterItem> en : mSavedItems.entrySet()) {
            items.add(en.getValue());
        }
        return items;
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
        item.setSelected(false);
        item.setIntensityArray(EffectDataManager.getDefaultIntensity(item.getId(),null));
    }

    public FilterItem getLocalStoredItems() {
        FilterItem items = null;
        String path = null;
        ArrayList<LocalParamHelper.LocalParam> localParams = LocalParamDataManager.allItems();
        if (localParams != null) {
            for (LocalParamHelper.LocalParam param : localParams) {
                if (param.category.equals(LocalParamHelper.CATEGORY_FILTER) && param.effect) {
                    path = param.path;
                }
            }
        }

        FilterItem filterItems = getItems();
        for (EffectButtonItem item : filterItems.getChildren()) {
            if (item.hasChildren()){
                for (EffectButtonItem child: item.getChildren()){
                    if (
                            child.getNode() != null
                                    && !TextUtils.isEmpty(child.getNode().getPath())
                                    && child.getNode().getPath().equals(path)
                    ) {
                        child.setSelected(true);
                        items = (FilterItem) child;
                    }
                }
            }
        }
        if (items == null) {
            return (FilterItem) filterItems.getChildren()[0];
        }
        return items;
    }
}