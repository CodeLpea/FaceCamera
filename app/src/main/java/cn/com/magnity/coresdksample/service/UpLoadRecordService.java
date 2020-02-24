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
import cn.com.magnity.coresdksample.http.UpImgTOss;
import cn.com.magnity.coresdksample.http.model.UpRecordEntry;
import cn.com.magnity.coresdksample.utils.NetUtil;
import cn.com.magnity.coresdksample.utils.TimeUitl;

/**
 * 上传记录服务
 */
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
        if (mUploadWorker == null) {
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
        if (mExecutorService != null) {
            mExecutorService.shutdown();
        }
        if (mUploadWorker != null) {
            try {
                mUploadWorker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mUploadWorker = null;
            }
        }
    }

    public static boolean isServiceRunning() {
        return isRunning;
    }

    private Thread mUploadWorker = null;
    private Runnable mUploadRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRunning) {
                //先检查网络是否连通
                boolean result = checkNetWork();
                if (!result) {
                    TimeUitl.delayMs(2000);
                    continue;
                }
                //从缓存数据库中读取数据
                UpRecordDb upRecordDb = LitePal.findFirst(UpRecordDb.class);
                if (upRecordDb != null) {
                    Log.i(TAG, upRecordDb.toString());

                    if (upRecordDb.getImgUrl() != null) {
                        //上传考勤照片到阿里云服务器
                        UpImgTOss personUpload = new UpImgTOss(getApplicationContext(), upRecordDb.getImgUrl(), 0);
                        UpImgTOss tempUpload = new UpImgTOss(getApplicationContext(), upRecordDb.getRedImgUrl(), 0);
                        boolean personExecute = personUpload.execute();
                        boolean tempExecute = tempUpload.execute();
                        //设置OSS服务器上的objectKey
                        upRecordDb.setImgUrl(personUpload.getObjectKey());
                        upRecordDb.setRedImgUrl(tempUpload.getObjectKey());
                        Log.i(TAG, "personPicOSSPath : " + upRecordDb.getImgUrl());
                        Log.i(TAG, "tempPicOSSPath : " + upRecordDb.getRedImgUrl());
                        //如果没有上传成功,重新上传
                        if (!personExecute || !tempExecute) {
                            continue;
                        }

                    }
                    //上传考勤记录
                    UpRecordEntry upRecordEntry = new UpRecordEntry();
                    upRecordEntry.setCreate_time(upRecordDb.getCreate_time());
                    upRecordEntry.setDevice_no(upRecordDb.getDevice_no());
                    upRecordEntry.setEnv_temperature(upRecordDb.getEnv_temperature());
                    upRecordEntry.setRedImgUrl(upRecordDb.getRedImgUrl());
                    upRecordEntry.setImgUrl(upRecordDb.getImgUrl());
                    upRecordEntry.setTemperature(upRecordDb.getTemperature());
                    upRecordEntry.setImg_id(upRecordDb.getDevice_no() + upRecordDb.getCreate_time());


                    //上传不成功。每间隔10s重传一次
                    boolean isUploadOk = false;
                    while (true) {
                        isUploadOk = RetrofitClient.getInstance().upLoadUnKonwRecord(upRecordEntry);
                        if (!isUploadOk) {
                            int retry = 20;
                            while (isRunning && retry > 0) {
                                TimeUitl.delayMs(500);
                                retry--;
                            }
                            continue;
                        }
                        break;
                    }
                    //上传成功,删除记录
                    if (isUploadOk) {
                        Log.i(TAG, "上传成功,删除记录");
                        upRecordDb.delete();
                    }
                } else {
                    TimeUitl.delayMs(2000);
                }
            }
        }
    };

    private boolean checkNetWork() {
        boolean result = false;
        result = NetUtil.isNetworkConnected(getApplicationContext());
        return result;
    }
}
