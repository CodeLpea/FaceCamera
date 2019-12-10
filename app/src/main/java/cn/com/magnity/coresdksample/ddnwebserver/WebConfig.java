package cn.com.magnity.coresdksample.ddnwebserver;

import cn.com.magnity.coresdksample.utils.Config;

public interface WebConfig {

    /***
     * 字段的命名
     * */
    String DEVICE_NO = "device_no";
    String VERSION_NAME = "version_name";
    String WIFI_NAME = "wifi_name";
    String WIFI_PASSWD = "wifi_passwd";
    String SYSTEM_VOICE = "system_voice";
    String ERROR_VOICE = "error_voice";
    String NORMAL_VOICE = "normal_voice";
    String VOICE_SPEED = "voice_speed";
    String TEMPERATURE_THRESHOLD = "temperature_threshold";
    String DISTANCE = "distance";
    String FFC_COMPENSATION_PARAMETER = "ffc_compensation_parameter";
    String FFC_CALIBRATION_PARAMETER = "ffc_calibration_parameter";
    String CAMERA_EXPLORE = "camera_explore";

    //人脸框定位字段
    String LoactionX1 = "loaction_x_1";
    String LoactionX2 = "loaction_x_2";
    String LoactionX3 = "loaction_x_3";
    String LoactionX4 = "loaction_x_4";
    String LoactionY1 = "loaction_y_1";
    String LoactionY2 = "loaction_y_2";
    String LoactionY3 = "loaction_y_3";
    String LoactionY4 = "loaction_y_4";

    //红外框位置
    String MOVEX = "move_x";
    String MOVEY = "move_y";
    String SCALE = "scale";

    //有效区域线条位置
    String LINEUP = "line_up";
    String LINELEFT = "line_left";
    String LINERIGHT = "line_right";
    String LINEDWON = "line_down";


    String TEMPERPATH = "temper_path";
    String PERSONPATH = "person_path";


    //图片数据字段名
    String PERSONRECODPATH = "person_recodpath";
    String TEMPERRECODPATH = "temper_recodpath";
    String DATA = "date";
    String TEMP = "temp";

    //图片筛选字段名
    String DATALIST = "data_list";
    String ALLSIZE = "all_size";
    String CURRENTPAGE = "current_page";
    String EVERPAGENUMBER = "everpagenumber";
    String STARTIME = "start_time";
    String ENDTIME = "end_time";
    String MINTEMP = "min_temp";
    String MATEMP = "max_temp";
    String ORDERS = "orders";

    //api路径
    //设置wifi
    String setWifiRequestPath = "/set/wifi";
    //设置语音
    String setVoiceRequestPath = "/set/voice";
    //设置温度阀值
    String setTemperatureRequestPath = "/set/temperature";
    //设置摄像头曝光
    String setExploreRequestPath = "/set/camera_explore";
    //设置FFC补偿
    String setFFCCompensationRequestPath = "/set/FFC_compensation";
    //设置黑体校准参数
    String setBlackCalibrationnRequestPath = "/set/FFC_black_calibrationn";
    //设置平均温度校准
    String setAvgCalibrationnRequestPath = "/set/FFC_avg_calibrationn";
    //设置距离
    String setDistanceRequestPath = "/set/temper_distance";

    //获取图片预览信息
    String getPhotoPreviewRequestPath = "/set/photo_preview";
    //设置红外校准定位信息
    String setTemperLoactionRequestPath = "/set/temp_location";

    //有效区域信息
    String getValidAreaRequestPath = "/set/get_valid_area";
    String setValidAreaRequestPath = "/set/set_valid_area";

    //查询信息

    String queryDataRequestPath = "/set/query_data";


    /*测试图片路径，该路径为基于根目录的相对路径*/
    String person_path = "/DdnTempCamera"+"/"+"person.png";
    String temper_path = "/DdnTempCamera"+"/"+"temper.png";
//    String person_path="https://image-didanuo.oss-cn-shenzhen.aliyuncs.com/person.png";
//    String temper_path="https://image-didanuo.oss-cn-shenzhen.aliyuncs.com/temper.png";


}
