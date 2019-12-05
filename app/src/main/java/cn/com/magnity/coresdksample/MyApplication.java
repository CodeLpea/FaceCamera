package cn.com.magnity.coresdksample;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import cn.com.magnity.coresdk.MagDevice;
import cn.com.magnity.coresdksample.Detect.FaceRect;
import cn.com.magnity.coresdksample.Detect.JuGeFaceRect;
import cn.com.magnity.coresdksample.View.MagSurfaceView;
import cn.com.magnity.coresdksample.utils.AppUtils;
import cn.com.magnity.coresdksample.utils.Config;
import cn.com.magnity.coresdksample.utils.LogcatHelper;
import cn.com.magnity.coresdksample.utils.SPUtil;
import cn.com.magnity.coresdksample.utils.TimeUitl;
import cn.com.magnity.coresdksample.utils.voice.TtsSpeak;
import cn.com.magnity.coresdksample.utils.lampUtil;
import cn.com.magnity.coresdksample.utils.logSave;
import cn.com.magnity.coresdksample.websocket.bean.SoftWareVersionsInfo;

import static cn.com.magnity.coresdksample.utils.Config.CONFIG_DIR;

public class MyApplication extends Application {
    private static final String TAG="MyApplication";
    public static  MagDevice mDev;
    public static  boolean isplay=false; //红外播放状态
    public static  boolean isQuest=false; //红外权限状态
    public static  boolean isGetFace=false; //红外权限状态
    public static  boolean isInit=true;//启动标志
    public static    int WhereFragmentID=1;//用来标记当前出于哪个Fragment，1为linkFragment，2为LocationFragment；

    public JuGeFaceRect juGeFaceRect;
    public FaceRect faceRect;
    public MagSurfaceView mView;
    public static logSave photoNameSave;//全局照片名称保存对象
    public static logSave photoNameSave2;//全局照片名称保存对象
    private static MyApplication myApplication = null;
    public static MyApplication getInstance() {
        return myApplication;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        Log.i(TAG, "程序启动完成: ");
        init();
        photoNameSaveLog();


    }



    private void init() {
        mDev =new MagDevice();//初始化全局MagDevice
        juGeFaceRect=new JuGeFaceRect();//初始化全局标记框
        faceRect=new FaceRect();
        TtsSpeak.getInstance();//初始化全局语音播放工具
        new lampUtil(Config.LAMP,Config.TIME);
        LogcatHelper.getInstance(this).start();//启动日志

        SPUtil.assignDir(CONFIG_DIR);

    }
    private void   photoNameSaveLog() {
        photoNameSave=new logSave();
        photoNameSave2=new logSave();
    }



}

