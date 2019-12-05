package cn.com.magnity.coresdksample.utils;

import android.os.Environment;

import java.io.File;

/**
 * Sp配置文件
 * lp
 */

public interface Constants {
    //程序数据存储的主路径
    String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DdnTempCamera";
    //照片保存路径
    String PIC_DIR = ROOT_DIR + File.separator + "pictures";
    //考勤临时缓存照片，保存温度摄像头和摄像头的照片
    String TMP_CACHE_PIC_DIR = PIC_DIR + File.separator + "tmp";
    //数据库存储路径
    String DB_DIR = ROOT_DIR + File.separator + "databases";
    //设备配置文件路径
    String CONFIG_DIR = ROOT_DIR + File.separator + "config";
    //配置文件名字
    String CONFIG_NAME = "DdnTempCameraConfig";
    //程序运行日志存放路径
    String XLOG_DIR = ROOT_DIR + File.separator + "xlog";
    //升级包下载存放路径
    String DOWNLOAD_DIR = ROOT_DIR + File.separator + "download";



    //语音播放速度
    String VOICE_SPEED = "voice_speed";
    int DEFAULT_VOICE_SPEED = 60;

    //识别阈值相关
    String FACE_RECOGNITION_THRESHOLD = "face_recognition_threshold";
    float DEFAULT_FACE_RECOGNITION_THRESHOLD = 0.6f;
    String FORMAL_FEATURE_UPDATE_THRESHOLD = "formal_feature_update_threshold";
    float DEFAULT_FORMAL_FEATURE_UPDATE_THRESHOLD = 0.8f;
    String CACHE_FEATURE_THRESHOLD = "cache_feature_threshold";
    float DEFAULT_CACHE_FEATURE_THRESHOLD = 0.4f;

    //远程控制相关
    int SOFTWARE_SYSTEM_TYTE = 8;

    //使用模式(仅电子班牌模式:该模式下只获取Web网页信息和操作设置界面)
    String ONLY_ELECTRONIC_CLASS_CARD_MODE = "only_electronic_class_card_mode";



    interface SP_HDetect_NAME {
        String SP_NAME = "SPNUOSHUA";
        /** 用户认证信息 v3*/
        String TOKEN = "token";
        String AUTHORIZATION_INFO = "Authorization";
        /** 蓝牙地址*/
        String BLUETOOTH_ADDRESS = "bluetoothAddress";
        /** 曝光*/
        String EXPLORE = "explore";
        /** 颜色选择记录*/
        String COLORSPINEER= "colorSpiner";
        /** 阈值选择记录*/
        String HoldValuePostion = "HoldValuePostions";
        String HoldValues = "HoldValues";
        /** 天数 */
        String CURRENT_DAY = "currentDay";
        /**学校id和学校名称*/
        String SchoolID="SchoolID";
        String SchoolName="SchoolName";
        String StuNum="StuNum";
        /** 手动设置曝光 */
        String isExplore="isExplore";
        String ProductName="ProductName";//产品名
        String ProductId="ProductId";//产品ID
        String VendorId="VendorId";//供应商ID
        String BcdDevice="BcdDevice";//FW版本
        /** app版本号*/
        String APPVERSION="APPVERSION";//FW版本
        /**
         * 网络摄像头登录信息
         *
         */
        String NET_IP = "NET_IP";
        String NET_PORT = "NET_PORT";
        String NET_USER = "NET_USER";
        String NET_OPSE = "NET_OPSE";

        /**
         * 语音音量
         * */
        String VOICE_LEVEL="VOICE_LEVEL";
        String VOICE_SPEED_LEVEL="VOICE_SPEED_LEVEL";



        /**
         * 服务器地址
         * */
        String NAME_TUOYUBAO_BASE ="托育宝正式服务器";//key名，用来隐藏和解释地址
        String NAME_XIAONUO_BASE ="小诺正式服务器";//key名，用来隐藏和解释地址
        String API_TUOYUBAO_BASE ="http://api.didano.com/";//默认的托育宝正式服务器地址
        String API_XIAONUO_BASE ="http://120.77.237.242:8081/";//默认的小诺的正式服务器地址
        String APISP_NAME ="APISP_NAME";//服务器地址的key
        String ADRESS_RECODE_NAME ="ADRESS_RECODE_NAME";//所有服务器地址的索引key
    }

}
