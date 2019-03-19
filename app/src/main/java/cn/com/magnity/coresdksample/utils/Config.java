package cn.com.magnity.coresdksample.utils;

/**
 * 配置信息*/
public class Config {
    /*拍照标记*/
    public static  boolean iftaken; //拍照状态按钮

    /*温度阀值*/
    //判断两次检测到人脸的间隔时间，如果超过500ms，则判断为第二个人，就重置温度阈值
    //否则同一个人不会反复拍摄同样温度的照片
    public static float TempThreshold=30;
    public static float DefaultTempThreshold=30;


    /*x方向校准参数，y方向校准参数*/
    public static int XPalce=1;
    public static int YPalce=1;

    /*文件保存目录*/
    public static String SavaRootDirName="DdnTemp";
    public static String SavaTestDirName="Test";

    /*系统运行状态标记，用来显示灯光*/
    public static int LAMP=1;//1:绿色，2:红色
    public static int TIME=1000;



    /*配置参数*/
    public static int currtentVoiceVolume=15;
    public static int heightTempVoiceVolume=15;
    public static int normolTempVoiceVolume=10;
    public static String WifiName="XIAONUO1";
    public static String WifiPassWord="XiaoNuo2018";
    public static String DefaultWifiName="didano";
    public static String DefaultWifiPassWord="12345678";
    public static int Lithgt=5;


    /*存储键值对的key*/
    public  static String DdnProperties="DdnProperties.txt";
    public  static String KeycurrtentVoiceVolume="KeycurrtentVoiceVolume";
    public  static String KeyheightTempVoiceVolume="KeyheightTempVoiceVolume";
    public  static String KeynormolTempVoiceVolume="KeynormolTempVoiceVolume";
    public  static String KeyWifiName="KeyWifiName";
    public  static String KeyWifiPassWord="KeyWifiCode";
    public  static String KeyLithgt="KeyLithgt";
    public  static String KeyXplace="KeyXplace";
    public  static String KeyYplace="KeyYplace";

    /*静态语音延迟播放Hanlder的MSG*/
    public static final int MSG1 = 100;//自动连接指定的wifi
    public static final int MSG0 = 0;//自动连接指定的wifi
    public static final int MSG2 =MSG1+1 ;//ftp语音，包括wifi信息
    public static final int MSG3 = MSG2+1;//人脸摄像头
    public static final int MSG4 = MSG3+1;//配置文件检查语音
    public static final int MSG5 = MSG4+1;//温度摄像头语音


 /*   public static String SavaPersonDirName=SavaRootDirName+"/"+"Person"+"/";
    public static String SavaJuGeDirName=SavaRootDirName+"/"+"JuGe"+"/";*/

}
