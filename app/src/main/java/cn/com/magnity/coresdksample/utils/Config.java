package cn.com.magnity.coresdksample.utils;

import android.os.Environment;

import java.io.File;

/**
 * 配置信息*/
public class Config {
    /*拍照标记*/
    public static  boolean iftaken; //拍照状态按钮


    /*x方向校准参数，y方向校准参数*/
    public static int XPalce=1;//
    public static int YPalce=1;

    /*有效区域的划线*/
    public static int AreaUp=45;
    public static int AreaLeft=114;
    public static int AreaRight=448;
    public static int AreaBottom=576;


    /*文件保存目录*/
    public static String SavaRootDirName="DdnTemp";
    public static String SavaRootDirPath=Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DdnTempImg";
    public static String DdnPropertiesPath=Environment.getExternalStorageDirectory().getPath()+"/"+"DdnProperties.txt";//配置文件路径
    public static String DdnUpdateApkPath=Environment.getExternalStorageDirectory().getPath()+"/"+"DdnTemp.apk";//配置文件路径
    public static String SavaTestDirName="Test";

    /*系统运行状态标记，用来显示灯光*/
    public static int LAMP=1;//1:绿色，2:红色
    public static int TIME=1000;

    /*配置参数*/
    public static String DEVICENAME="didnao";
    public static String VERSIONNAME="1.3.2";
    public static int currtentVoiceVolume=15;
    public static int heightTempVoiceVolume=15;
    public static int normolTempVoiceVolume=10;
    public static String WifiName="XIAONUO1";
    public static String WifiPassWord="XiaoNuo2018";
    public static String DefaultWifiName="didano";
    public static String DefaultWifiPassWord="12345678";
    public static float FDistance= (float) 1.2;
    public static String IsFFC="0";
    public static float FFCcompensation =0;

    /*温度阀值*/
    //判断两次检测到人脸的间隔时间，如果超过500ms，则判断为第二个人，就重置温度阈值
    //否则同一个人不会反复拍摄同样温度的照片
    public static float TempThreshold=30;
    public static float DefaultTempThreshold=30;
    public static int ExploreValue=1; //-3 ---3
    public static boolean ifBlackfFFC =false;

    public static int []FFCTemps;




    /*存储键值对的key*/
    public  static String DdnProperties="DdnProperties.txt";
    public  static String KeyXplace="KeyXplace";
    public  static String KeyYplace="KeyYplace";
    public  static String KeyAreaLineUp="KeyAreaLineUp";//区域线的位置key
    public  static String KeyAreaLineLeft="KeyAreaLineLeft";//区域线的位置key
    public  static String KeyAreaLineRight="KeyAreaLineRight";//区域线的位置key
    public  static String KeyAreaLineBottom="KeyAreaLineBottom";//区域线的位置key




    /*静态语音延迟播放Hanlder的MSG*/
    public static final int MSG1 = 100;//自动连接指定的wifi
    public static final int MSG0 = 0;//自动连接指定的wifi
    public static final int MSG2 =MSG1+1 ;//ftp语音，包括wifi信息
    public static final int MSG3 = MSG2+1;//人脸摄像头
    public static final int MSG4 = MSG3+1;//配置文件检查语音/检测是否温度摄像头是否在线
    public static final int MSG5 = MSG4+1;//温度摄像头语音
    public static final int MSG6 = MSG5+1;//反复加载配置服务
    public static final int MSG7 = MSG6+1;//亮度设置
    public static final int MSG8 = MSG7+1;//更新播报
    public static final int MSG9 = MSG8+1;//FFC校准播报
    public static final int MSG10 = MSG9+1;//FFC校准


 /*   public static String SavaPersonDirName=SavaRootDirName+"/"+"Person"+"/";
    public static String SavaJuGeDirName=SavaRootDirName+"/"+"JuGe"+"/";*/


 /*IntentAction*/
 public static final String InitLoadServieAction="InitLoadServieAction";
 public static final String ReLoadServieAction="ReLoadServieAction";

}
