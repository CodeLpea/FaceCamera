package cn.com.magnity.coresdksample;

import android.app.Application;
import android.util.Log;

import cn.com.magnity.coresdk.MagDevice;
import cn.com.magnity.coresdksample.Detect.FaceRect;
import cn.com.magnity.coresdksample.Detect.JuGeFaceRect;
import cn.com.magnity.coresdksample.View.MagSurfaceView;
import cn.com.magnity.coresdksample.utils.TtsUtil;

public class MyApplication extends Application {
    private static final String TAG="MyApplication";
    public static  MagDevice mDev;
    public static  boolean istaken; //拍照状态按钮
    public static  boolean isplay=false; //红外播放状态
    public static  boolean isQuest=false; //红外权限状态
    public static  boolean isGetFace=false; //红外权限状态
    public static    int WhereFragmentID=1;//用来标记当前出于哪个Fragment，1为linkFragment，2为LocationFragment；
    public TtsUtil ttsUtil;
    public JuGeFaceRect juGeFaceRect;
    public FaceRect faceRect;
    public MagSurfaceView mView;
    public TtsUtil getTtsUtil() {
        return ttsUtil;
    }
    private static MyApplication myApplication = null;
    public static MyApplication getInstance() {
        return myApplication;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        Log.i(TAG, "onCreate: ");
        mDev =new MagDevice();//初始化全局MagDevice
        juGeFaceRect=new JuGeFaceRect();//初始化全局标记框
        faceRect=new FaceRect();
        ttsUtil=new TtsUtil(this);//初始化全局语音播放工具
    }
}
