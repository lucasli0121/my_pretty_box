package com.effectsar.labcv.core;

public class Config {
    public static final String LICENSE_NAME = "maliang_test_20240710_20240810_com.media.demo_4.6.2_1746.licbag";
    public static final String CHECK_RESULT_BROADCAST_ACTION = "com.effectsar.labcv.core.check_result:action";
    public static final String ASSESS_KEY = "dp@1oa334f";
    public static final String PANEL_KEY = "v460";
    // {zh} 宏名称禁止修改，下方同行代码后禁止添加注释，会影响CI出包，false为关闭模型下发，true为开启模型下发 {en} The macro name is prohibited from being modified, and it is prohibited to add comments after the same code below, which will affect the CI package. False is to close the model for distribution, and true is to open the model for distribution.
    public static final boolean IS_ONLINE_MODEL = false;

    // When ENABLE_ASSETS_SYNC is true, all the bundles will be loaded onto SD card directly.
    // Relative dir hierarchy on SD card will be completely synchronize to bundles.
    public static final boolean ENABLE_ASSETS_SYNC = true;

    //  {zh} 算法内存增量开关，用来给测试测算法内存增量的  {en} Algorithm memory increment switch, used to increment the memory of the test algorithm
    public static boolean ALGORITHM_MEMORY_SWITCH = false;
}
