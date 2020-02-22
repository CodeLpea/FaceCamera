package cn.com.magnity.coresdksample.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.litepal.LitePal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.com.magnity.coresdksample.database.UpRecordDb;
import cn.com.magnity.coresdksample.http.RetrofitClient;
import cn.com.magnity.coresdksample.http.UploadOssTask;
import cn.com.magnity.coresdksample.http.model.UpRecordEntry;
import cn.com.magnity.coresdksample.utils.NetUtil;
import cn.com.magnity.coresdksample.utils.TimeUitl;

/**
* 上传记录服务
* */
public class UpLoadRecordService extends Service {
    private static final String TAG = "UpLoadRecordService";
    private ExecutorService mExecutorService = null;
    private static boolean isRunning = false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate...");
        mExecutorService = Executors.newFixedThreadPool(10);
        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Log.i(TAG, "onStartCommand...");
        if(mUploadWorker == null){
            mUploadWorker = new Thread(mUploadRunnable);
            mUploadWorker.start();
            Log.i(TAG, "开启上传服务: ");
        }

        int ret = super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "onStartCommand ret = " + ret);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy...");
        isRunning = false;
        if(mExecutorService != null) {
            mExecutorService.shutdown();
        }
        if(mUploadWorker != null){
            try{
                mUploadWorker.join();
            }catch(InterruptedException e){
                e.printStackTrace();
            }finally {
                mUploadWorker = null;
            }
        }
    }

    public static boolean isServiceRunning(){
        return isRunning;
    }

    private Thread mUploadWorker = null;
    private Runnable mUploadRunnable =  new Runnable() {
        @Override
        public void run() {
            while(isRunning){
                //先检查网络是否连通
                boolean result=checkNetWork();
                if(!result){
                    TimeUitl.delayMs(2000);
                    continue;
                }
                //从缓存数据库中读取数据
                UpRecordDb upRecordDb = LitePal.findFirst(UpRecordDb.class);
                if(upRecordDb != null){
                    Log.i(TAG, upRecordDb.toString());
                    String personPicOSSPath = null;
                    String tempPicOSSPath = null;
                    if(upRecordDb.getImgUrl() != null){
                        //上传考勤照片到阿里云服务器
                        personPicOSSPath = UploadOssTask.getObjectKey(upRecordDb.getImgUrl(), 0);
                        tempPicOSSPath = UploadOssTask.getObjectKey(upRecordDb.getRedImgUrl(), 0);
                        Log.i(TAG, "personPicOSSPath : " + personPicOSSPath);
                        Log.i(TAG, "tempPicOSSPath : " + tempPicOSSPath);
                        //开启照片上传oss线程
                        if(personPicOSSPath != null) {
                            TimeUitl.delayMs(1000);//主要目的是等待照片保存在磁盘中
                            mExecutorService.submit(new UploadOssTask(getApplicationContext(), personPicOSSPath, upRecordDb.getImgUrl()));
                            mExecutorService.submit(new UploadOssTask(getApplicationContext(), tempPicOSSPath, upRecordDb.getRedImgUrl()));
                        }
                    }
                    //上传考勤记录
                    UpRecordEntry upRecordEntry = new UpRecordEntry();
                    upRecordEntry.setCreate_time(upRecordDb.getCreate_time());
                    upRecordEntry.setDevice_no(upRecordDb.getDevice_no());
                    upRecordEntry.setEnv_temperature(upRecordDb.getEnv_temperature());
                    upRecordEntry.setRedImgUrl(tempPicOSSPath);
                    upRecordEntry.setImgUrl(personPicOSSPath);
                    upRecordEntry.setTemperature(upRecordDb.getTemperature());
                    upRecordEntry.setImg_id(upRecordDb.getDevice_no()+upRecordDb.getCreate_time());


                    //上传不成功。每间隔10s重传一次
                    boolean isUploadOk = false;
                    while(true){
                        isUploadOk = RetrofitClient.getInstance().upLoadUnKonwRecord(upRecordEntry);
                        if(!isUploadOk){
                            int retry = 20;
                            while(isRunning && retry > 0){
                                TimeUitl.delayMs(500);
                                retry--;
                            }
                            continue;
                        }
                        break;
                    }
                    //上传成功,删除记录
                    if(isUploadOk){
                        Log.i(TAG, "上传成功,删除记录");
                        upRecordDb.delete();
                    }
                }else {
                    TimeUitl.delayMs(2000);
                }
            }
        }
    };

    private boolean checkNetWork() {
        boolean result=false;
        result= NetUtil.isNetworkConnected(getApplicationContext());
        return result;
    }
}
