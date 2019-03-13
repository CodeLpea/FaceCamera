package cn.com.magnity.coresdksample.utils;

/**
 * 配置信息*/
public class Config {
    /*文件保存目录*/
    public static String SavaRootDirName="DdnTemp";
    public static String SavaTestDirName="Test";
    /*音量参数*/
    public static int currtentVoiceVolume=10;
    public static int heightTempVoiceVolume=10;
    public static int normolTempVoiceVolume=5;
    public static String WifiName="XIAONUO1";
    public static String WifiPassWord="XiaoNuo2018";
    public static int Lithgt=5;


    /*存储键值对的key*/
    public  static String DdnProperties="DdnProperties.txt";
    public  static String KeycurrtentVoiceVolume="KeycurrtentVoiceVolume";
    public  static String KeyheightTempVoiceVolume="KeyheightTempVoiceVolume";
    public  static String KeynormolTempVoiceVolume="KeynormolTempVoiceVolume";
    public  static String KeyWifiName="KeyWifiName";
    public  static String KeyWifiPassWord="KeyWifiCode";
    public  static String KeyLithgt="KeyLithgt";

    /*静态语音延迟播放Hanlder的MSG*/
    public static final int MSG1 = 100;//自动连接指定的wifi
    public static final int MSG2 =MSG1+1 ;//ftp语音，包括wifi信息
    public static final int MSG3 = MSG2+1;//人脸摄像头
    public static final int MSG4 = MSG3+1;//配置文件检查语音
    public static final int MSG5 = MSG4+1;//温度摄像头语音


 /*   public static String SavaPersonDirName=SavaRootDirName+"/"+"Person"+"/";
    public static String SavaJuGeDirName=SavaRootDirName+"/"+"JuGe"+"/";*/

}
