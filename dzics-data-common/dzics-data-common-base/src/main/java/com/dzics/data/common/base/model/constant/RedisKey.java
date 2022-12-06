package com.dzics.data.common.base.model.constant;

public  class RedisKey {

    //未知tcp指令key前缀
    public static final String UNKNOWN_TCP = "unknown:data:";

    //redis查询为空时  判断是否需要查询mysql的标识符  (查询)
    public static final String SELECT_MYSQL_TRUE = "1";
    //redis查询为空时  判断是否需要查询mysql的标识符  (不用查询)
    public static final String SELECT_MYSQL_FALSE = "-1";
    /**
     * 初始化指令 前缀
     */
    public static final String TCP_CMD_PREFIX = "tcp:cmd:prefix:";
    public static final String PulseCompensation = "pulseCompensation:";
    public static final String FREQUENCY_MIN = "frequency:min:";
    public static final String MOM_REDIS_KEY = "mom:redis:key:";
    public static final String MOM_REDIS_KEY_GROUPID = "mom:redis:key:groupid:";
    /**
     * 生产率数据缓存
     */
    public static final String UPDATE_COMPLETION_RATE_ADD = "updateCompletionRate:add:";
    public static final String UPDATE_COMPLETION_RATE_SIGNAL = "updateCompletionRate:signal:";

    // 频繁触发异常的指令前缀
    public static final String ERR_TCP_VALUE = "err_tcp_value:";
    //产品绑定的需要显示的检测项缓存标识
    public static final String TEST_ITEM = "test_item:";

    public static final String USERPERSIONPFXKEY = "USERPERSIONPFXKEY";
    public static final String REF_TOEKN_TIME = "Ref:Toekn:Time:";
    public static final String REF_TOEKN_TIME_TOKEN = "Ref:Toekn:Time:Token";
    public static final String USER_NAME_AND_USER_TYPE = "User:Name:And:User:Type";

    public static final String LEASE_CAR_TOKEN_HISTORY = "lease:car:token:history:";
    public static final String SYS_BUS_TASK_ARRANGE = "SysBusTask:Arrange";
    public static final String KEY_RUN_MODEL_DANGER = "SYSTEM:KEY:RUN:MODEL:DANGER";
    /**
     * 根据订单产线号获取绑定设备的五日内产量
     */
    public static final String HOW_MUCH_DAILY_OUTPUT = "HowMuchDailyOutput:";


    /**
     * 根据订单产线号查询近五日产线计划分析
     */
    public static final String PLAN_ANALYSIS = "planAnalysis";

    /**
     * 日产 总产
     */
    public static final String DAY_SUM_AND_TOTAL_SUM = "daySumAndTotalSum:";
    /**
     * 根据订单产线号查询近五日稼动率
     */
    public static final String FIVE_DAY_CROP_MOVEMENT_RATE = "fiveDayCropMovementRate:";
    /**
     * 稼动率缓存 key 模块 （中间部分） 前缀名称
     */
    public static final String SOCKET_UTILIZATION = "socketUtilization:";
    /**
     * 合格/不合格数量  月产为单位
     */
    public static final String MONTH_DATA_OK_NG = "monthDataOkNg:";
    /**
     * 获取当前日期数据
     */
    public static final String GET_CURRENT_DATE = "DzDataCollectionServiceImpl:getCurrentDate:";

    public static final String DAY_WORK_SHIFT_NG_OK = "dayWorkShiftNgOk:";

    public static final String LINE_ID_DEVICE_IS_SHOW = "LineIdDeviceIsShow:";
    /**
     * 设备用时分析(旧)
     */
    public static final String DEVICE_TIME_ANALYSIS_OLD = "deviceTimeAnalysis:old";
    /**
     * 设备用时分析(新)
     */
    public static final String DEVICE_TIME_ANALYSIS_NEW = "deviceTimeAnalysis:new:";

    public static final String REF = "htmlRefreshController:wareHouseServiceImpl:refresh";
    public static final String SPLIT_DATE_DAY_TIME_INS = "split:Date:Day:TimeIns:";
    public static final String UPDATE_DAY_SUM = "remote:update_day_sum:";
    public static final String UPDATE_MONTH_SUM = "remote:update_month_sum:";
    /**
     * 设备停机次数
     */
    public static final String DEVICE_STOP_SUM = "device:stop:sum:";
    public static final String CHECK_PRODUCT = "check:product:";
    public static final String line_state_key = "line:state:key:";
    public static final String DzLineShiftDayService = "ShiftDayPlanService:getDeviceIdShift:";
    public static final String shiftProductionDetails = "ShiftDayPlanService:shiftProductionDetails:";
    public static final String GET_DAILY_PASS_RATE = "DayPassRate:getDailyPassRate:";
    public static final String SysConfigServiceImpl = "SysConfigServiceImpl:getMouthDate:";
    public static final String DETECTOR_DATA_SERVICE_IMPL = "HuaPeiDetectorDataServiceImpl:";

    public static final String WORKPIECE_EXCHANGE_RESET = "udp:workpiece:exchange:flag:reset";
    public static final String Last_Work_Number = "dzics:last:work:number";
    public static final String GIVE_AN_ALARM_HISTORY = "give:an:alarm";
    public static final String Rob_Call_Material = "ProTaskOrderServiceImpl:processDistribution:";
    /**
     * 产品检测数据缓存键 feishi
     */
    public static final String INSPECTION_DATA = "product:inspection:data:";
    public static final String SHARED_DATA = "shared:data:";
    /**
     * 机床告警日志最新数据
     * */
    public static final String Alarm_TOOL_TIME = "alarm:tool:time";
}
