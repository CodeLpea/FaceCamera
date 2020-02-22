package cn.com.magnity.coresdksample.handler;

import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import android.os.Handler;

import cn.com.magnity.coresdksample.Config;
import cn.com.magnity.coresdksample.MyApplication;
import cn.com.magnity.coresdksample.database.UpRecordDb;
import cn.com.magnity.coresdksample.detect.FaceRect;
import cn.com.magnity.coresdksample.ddnwebserver.database.PhotoRecordDb;
import cn.com.magnity.coresdksample.ddnwebserver.database.PictureData;
import cn.com.magnity.coresdksample.usecache.CurrentConfig;
import cn.com.magnity.coresdksample.utils.AppUtils;
import cn.com.magnity.coresdksample.utils.TimeUitl;

import static cn.com.magnity.coresdksample.Config.ROOT_DIR_NAME;
import static cn.com.magnity.coresdksample.Config.TempThreshold;
import static cn.com.magnity.coresdksample.utils.FlieUtil.getFileName;
import static cn.com.magnity.coresdksample.utils.FlieUtil.getFolderPathToday;

/**
 * 保存信息handler
 */
public class RecordHandler extends Handler {
    private String TAG = "RecordHandler";
    public static final int MSG_RECODE = 500;


    private String personPath = null;
    private String tempPath = null;

    private static class InnerClass {
        public static RecordHandler intance = new RecordHandler();
    }

    /*静态内部类单例*/
    public static RecordHandler getInstance() {
        return InnerClass.intance;
    }


    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSG_RECODE:
                //保存温度记录信息
                RecordHolder tempRecordHolder = (RecordHolder) msg.obj;

                String temp=tempRecordHolder.getTemp();
                //保存图片，获得地址
                tempPath = saveBitmap(tempRecordHolder.getTempBitmap(),temp, "Temp");
                personPath = saveBitmap(tempRecordHolder.getPersonBitmap(), temp, "Person");

                //添加温度记录进数据库
                recordData(personPath, tempPath, Float.parseFloat(temp), TimeUitl.getDate());

                //添加预览记录
                recordPicViewData(personPath, tempPath, tempRecordHolder.getFaceRect());
                break;
        }
    }

    public void sendRecord(int MSG, Bitmap tempBitmap, Bitmap personBitmap, String temp, FaceRect faceRect) {
        Message message = this.obtainMessage();
        message.what = MSG;
        RecordHolder recordHolder = new RecordHolder();
        recordHolder.setTempBitmap(tempBitmap);
        recordHolder.setPersonBitmap(personBitmap);
        recordHolder.setTemp(temp);
        recordHolder.setFaceRect(faceRect);
        message.obj = recordHolder;

        //延时，避免保存冲突，确保每次间隔100毫秒。
        sendMessageDelayed(message, 100);

    }


    private void recordData(String personPath, String tempPath, float temp, String date) {
        Log.e(TAG, "记录保存到数据库: ");
        PhotoRecordDb photoRecordDb = new PhotoRecordDb();
        photoRecordDb.setDate(Long.valueOf(date));
        photoRecordDb.setPersonPath(personPath);
        photoRecordDb.setTemperPath(tempPath);
        photoRecordDb.setTemp(temp);
        photoRecordDb.save();
        Log.e(TAG, "photoRecordDb: " + photoRecordDb.toString());


        //保存需要上传的数据
        UpRecordDb upRecordDb=new UpRecordDb();
        upRecordDb.setCreate_time(date);
        upRecordDb.setDevice_no(AppUtils.getLocalMacAddressFromWifiInfo(MyApplication.getInstance()));
        upRecordDb.setEnv_temperature(String.valueOf(CurrentConfig.getInstance().getCurrentData().getTemperature_threshold()));
        upRecordDb.setRedImgUrl(Config.ROOT+File.separator+tempPath);
        upRecordDb.setImgUrl(Config.ROOT+File.separator+personPath);
        upRecordDb.setTemperature(String.valueOf(temp));
        Log.e(TAG, "upRecordDb: " + upRecordDb.toString());


        upRecordDb.save();

    }

    private void recordPicViewData(String personPath, String tempPath, FaceRect faceRect) {
        PictureData pictureData = new PictureData();
        pictureData.setPersonPath(personPath);
        pictureData.setTemperPath(tempPath);
        pictureData.setX1(faceRect.faceRect.left);
        pictureData.setX2(faceRect.faceRect.left);
        pictureData.setX3(faceRect.faceRect.right);
        pictureData.setX4(faceRect.faceRect.right);
        pictureData.setY1(faceRect.faceRect.top);
        pictureData.setY2(faceRect.faceRect.top);
        pictureData.setY3(faceRect.faceRect.bottom);
        pictureData.setY4(faceRect.faceRect.bottom);
        pictureData.save();
        Log.e(TAG, "recordPicViewData: " + pictureData.toString());
    }

    /**
     * 保存图片到SD卡上
     */
    private String saveBitmap(Bitmap bitmap, String temp, String type) {
        Log.i(TAG, "保存图片到SD卡上: ");

        // 保存图片到SD卡
        String fileName = TimeUitl.getDate() + "_" + temp + "_" + type + ".png";
        File file = new File(getFolderPathToday(), fileName);
        if (file.exists()) {
            Log.e(TAG, "已存在"+file.getPath());
            file.delete();
        }
        //保存到指定文件
        saveBitmapIntoLocal(file, bitmap);

        //必须保存相对路径到数据库，web服务才可以访问到
        String relativePath = ROOT_DIR_NAME + File.separator + "Pictures" + File.separator + getFileName() + File.separator + fileName;
        Log.e(TAG, "relativePath: " + relativePath);
        return relativePath;

    }

    /**
     * 将图片保存到指定File
     */
    private void saveBitmapIntoLocal(File file, Bitmap bitmap) {
        Log.e(TAG, "将图片保存到指定File: ");
        //保存到指定文件
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
