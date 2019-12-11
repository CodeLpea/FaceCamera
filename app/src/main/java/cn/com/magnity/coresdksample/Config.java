package cn.com.magnity.coresdksample;

import android.os.Environment;

import java.io.File;

/**
 * 配置信息
 */
public class Config {

    /*文件保存目录*/

    //程序数据存储的主路径

    public static final String ROOT_DIR_NAME = "DdnTempCamera";
    public static final String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ROOT_DIR_NAME;
    //程序运行日志存放路径
    public static final String XLOG_DIR = ROOT_DIR + File.separator + "xlog";
    //设备配置文件路径
    public static final String CONFIG_DIR = ROOT_DIR + File.separator + "config";
    //数据库存储路径
    public static final String DB_DIR = ROOT_DIR + File.separator + "databases";
    //缓存保存温度摄像头和摄像头的照片
    public static final String TMP_CACHE_PIC_DIR = ROOT_DIR + File.separator + "tmp";
    //照片保存路径
    public static final String SavaRootDirPath = ROOT_DIR + File.separator + "Pictures";
    //配置文件路径
    public static final String DdnPropertiesPath = ROOT_DIR+ "/" + "DdnProperties.txt";
    //ftp形式升级的apk文件目录
    public static final String DdnUpdateApkPath = ROOT_DIR + "/" + "DdnTemp.apk";
    //远程升级apk下载路径
    public static final String DdnDownLoadApkPath = ROOT_DIR + File.separator +"download";
    //温度摄像头校准参数保存
    public static final String Temp_DIR=ROOT_DIR+ File.separator + "Temp";


    /*x方向校准参数，y方向校准参数*/
//    public static int XPalce = 1;//
//    public static int YPalce = 1;
    /*有效区域的划线*/
//    public static int AreaUp = 45;
//    public static int AreaLeft = 114;
//    public static int AreaRight = 448;
//    public static int AreaBottom = 576;
    /*拍照标记*/
    public static boolean iftaken; //拍照状态按钮


    /*系统运行状态标记，用来显示灯光*/
    public static int LAMP = 1;//1:绿色，2:红色
    public static int TIME = 1000;

    /*配置参数*/
    public static String DEVICENAME = "didnao";
    public static String VERSIONNAME = "1.3.2";
//    public static int currtentVoiceVolume = 15;
//    public static int heightTempVoiceVolume = 15;
//    public static int normolTempVoiceVolume = 10;
    public static String WifiName = "XIAONUO1";
    public static String WifiPassWord = "XiaoNuo2018";
    public static String DefaultWifiName = "didano";
    public static String DefaultWifiPassWord = "12345678";
//    public static float FDistance = (float) 1.2;
//    public static String IsFFC = "0";
//    public static float FFCcompensation = 0;

    /*温度阀值*/
    //判断两次检测到人脸的间隔时间，如果超过500ms，则判断为第二个人，就重置温度阈值
    //否则同一个人不会反复拍摄同样温度的照片
    public static float TempThreshold = 30;
//    public static float DefaultTempThreshold = 30;
    public static int ExploreValue = 1; //-3 ---3
    public static boolean ifBlackfFFC = false;

    //FFC校准矩阵
    public static int[] FFCTemps;


    /*存储键值对的key*/
    public static String KeyXplace = "KeyXplace";
    public static String KeyYplace = "KeyYplace";
    //区域线的位置key
    public static String KeyAreaLineUp = "KeyAreaLineUp";
    //区域线的位置key
    public static String KeyAreaLineLeft = "KeyAreaLineLeft";
    //区域线的位置key
    public static String KeyAreaLineRight = "KeyAreaLineRight";
    //区域线的位置key
    public static String KeyAreaLineBottom = "KeyAreaLineBottom";

    public static String MAC_ADRESS = "MAC_ADRESS";


    /*静态语音延迟播放Hanlder的MSG*/
    public static final int MSG1 = 100;//自动连接指定的wifi
    public static final int MSG0 = 0;//自动连接指定的wifi
    public static final int MSG2 = MSG1 + 1;//ftp语音，包括wifi信息
    public static final int MSG3 = MSG2 + 1;//人脸摄像头
    public static final int MSG4 = MSG3 + 1;//配置文件检查语音/检测是否温度摄像头是否在线
    public static final int MSG5 = MSG4 + 1;//温度摄像头语音
    public static final int MSG6 = MSG5 + 1;//反复加载配置服务
    public static final int MSG7 = MSG6 + 1;//亮度设置
    public static final int MSG8 = MSG7 + 1;//更新播报
    public static final int MSG9 = MSG8 + 1;//FFC校准播报
    public static final int MSG10 = MSG9 + 1;//FFC校准


    /*IntentAction*/
    public static final String InitLoadServieAction = "InitLoadServieAction";
    public static final String ReLoadServieAction = "ReLoadServieAction";


    //服务器相关
    public static final String API_XIAONUO_BASE = "http://120.77.237.242:8081/";//默认的小诺的正式服务器地址
    public static final String VERSIONURL = "api/service/getVersion";
    public static final int SOFTWARE_SYSTEM_TYTE = 12;//设备版本号

}
