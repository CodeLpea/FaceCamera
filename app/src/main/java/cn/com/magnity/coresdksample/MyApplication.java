package cn.com.magnity.coresdksample;

import android.app.Application;

import cn.com.magnity.coresdk.MagDevice;

public class MyApplication extends Application {
    public static  MagDevice mDev ;
    public static  boolean istaken; //拍照状态按钮
    public static    int WhereFragmentID=1;//用来标记当前出于哪个Fragment，1为linkFragment，2为LocationFragment；
}
