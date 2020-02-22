package cn.com.magnity.coresdksample;

import android.app.Application;
import android.util.Log;

import com.example.lpnetstatus.NetStatusBus;
import com.taopao.androidnginxrtmp.service.NginxUtils;

import org.litepal.LitePal;

import cn.com.magnity.coresdk.MagDevice;

import cn.com.magnity.coresdksample.database.UpRecordDb;
import cn.com.magnity.coresdksample.handler.RecordHandler;
import cn.com.magnity.coresdksample.handler.StabilityTestHandler;
import cn.com.magnity.coresdksample.handler.TempHandler;
import cn.com.magnity.coresdksample.ddnwebserver.server.SetConfigServer;
import cn.com.magnity.coresdksample.utils.FlieUtil;
import cn.com.magnity.coresdksample.utils.SPUtil;
import cn.com.magnity.coresdksample.utils.voice.TtsSpeak;

import static cn.com.magnity.coresdksample.Config.CONFIG_DIR;
import static cn.com.magnity.coresdksample.utils.FlieUtil.getFolderPathToday;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    public static MagDevice mDev;
    private static MyApplication myApplication;
    public static MyApplication getInstance() {
        return myApplication;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        Log.i(TAG, "程序启动完成: ");

        init();

    }

    private void init() {

        //初始化全局MagDevice
        mDev = new MagDevice();
        //初始化全局语音播放工具
        TtsSpeak.getInstance();
        //启动Nginx服务器（在新的进程中，否则与灯控制冲突）
        NginxUtils.startNginx(this);
        //启动日志
//        LogcatHelper.getInstance(this).start();
        SPUtil.assignDir(CONFIG_DIR);

        //初始化LitePal数据库
        LitePal.initialize(this);
        //提前绑定服务，避免使用时才绑定崩溃
        SetConfigServer.getInstance();
        NetStatusBus.getInstance().init(this);
        //初始化Handler单例
        TempHandler.getInstance();
        RecordHandler.getInstance();
        StabilityTestHandler.getInstance();

        //初始化文件夹
        FlieUtil.initFile(getFolderPathToday());

        LitePal.deleteAll(UpRecordDb.class);

    }

}

