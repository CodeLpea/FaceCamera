package cn.com.magnity.coresdksample;

import android.app.Application;
import android.util.Log;

import com.example.lpnetstatus.NetStatusBus;
import com.taopao.androidnginxrtmp.service.NginxUtils;

import org.litepal.LitePal;

import cn.com.magnity.coresdk.MagDevice;
import cn.com.magnity.coresdksample.Detect.FaceRect;

import cn.com.magnity.coresdksample.Service.handler.RecordHandler;
import cn.com.magnity.coresdksample.Service.handler.StabilityTestHandler;
import cn.com.magnity.coresdksample.Service.handler.TempHandler;
import cn.com.magnity.coresdksample.surview.MagSurfaceView;
import cn.com.magnity.coresdksample.ddnwebserver.database.PhotoRecordDb;
import cn.com.magnity.coresdksample.ddnwebserver.server.SetConfigServer;
import cn.com.magnity.coresdksample.utils.FlieUtil;
import cn.com.magnity.coresdksample.utils.LogcatHelper;
import cn.com.magnity.coresdksample.utils.SPUtil;
import cn.com.magnity.coresdksample.utils.voice.TtsSpeak;
import cn.com.magnity.coresdksample.utils.logSave;

import static cn.com.magnity.coresdksample.Config.CONFIG_DIR;
import static cn.com.magnity.coresdksample.utils.FlieUtil.getFolderPathToday;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    public static MagDevice mDev;
    public static boolean isplay = false; //红外播放状态
    public static boolean isQuest = false; //红外权限状态
    public static boolean isInit = true;//启动标志
    public static int WhereFragmentID = 1;//用来标记当前出于哪个Fragment，1为linkFragment，2为LocationFragment；


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
        LitePal.initialize(this);//初始化LitePal数据库
        //提前绑定服务，避免使用时才绑定崩溃
        SetConfigServer.getInstance();
        NetStatusBus.getInstance().init(this);
        //初始化
        TempHandler.getInstance();
        RecordHandler.getInstance();
        StabilityTestHandler.getInstance();
        //初始化文件夹
        FlieUtil.initFile(getFolderPathToday());

        //putDataIntoDb();
        //LitePal.deleteAll(PhotoRecordDb.class);
    }


    //放入测试数据
    private void putDataIntoDb() {
        LitePal.deleteAll(PhotoRecordDb.class);
//        for(int i=0;i<100;i++){
//            PhotoRecordDb photoRecordDb=new PhotoRecordDb();
//            photoRecordDb.setPersonPath(person_path);
//            photoRecordDb.setTemperPath(temper_path);
//            photoRecordDb.setDate(Long.valueOf(TimeUtils.randomDate("20191001101010",TimeUtils.getYMDHMSDate())));
//            Random random=new Random();
//            float temp=random.nextFloat()+20;
//            photoRecordDb.setTemp(temp);
//            Log.i("放入测试数据", photoRecordDb.toString());
//            photoRecordDb.save();
//        }
    }

    private void init() {
        mDev = new MagDevice();//初始化全局MagDevice
        faceRect = new FaceRect();
        TtsSpeak.getInstance();//初始化全局语音播放工具
        //启动Nginx服务器（在新的进程中，否则与灯控制冲突）
        NginxUtils.startNginx(this);
        LogcatHelper.getInstance(this).start();//启动日志
        SPUtil.assignDir(CONFIG_DIR);

    }

    private void photoNameSaveLog() {
        photoNameSave = new logSave();
        photoNameSave2 = new logSave();
    }


}

