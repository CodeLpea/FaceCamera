package cn.com.magnity.coresdksample;

import android.app.Application;
import android.util.Log;

import cn.com.magnity.coresdk.MagDevice;

public class MyApplication extends Application {
    private static final String TAG="MyApplication";
    public static  MagDevice mDev;
    public static  boolean istaken; //拍照状态按钮
    public static  boolean isplay=false; //红外播放状态
    public static  boolean isQuest=false; //红外权限状态
    public static    int WhereFragmentID=1;//用来标记当前出于哪个Fragment，1为linkFragment，2为LocationFragment；

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        mDev =new MagDevice();

    }
}
