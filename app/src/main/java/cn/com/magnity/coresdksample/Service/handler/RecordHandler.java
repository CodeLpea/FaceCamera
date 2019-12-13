package cn.com.magnity.coresdksample.Service.handler;

import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;
import android.util.TimeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Handler;

import cn.com.magnity.coresdksample.Detect.JuGeFaceRect;
import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;
import cn.com.magnity.coresdksample.ddnwebserver.component.LoginInterceptor;
import cn.com.magnity.coresdksample.ddnwebserver.database.PhotoRecordDb;
import cn.com.magnity.coresdksample.ddnwebserver.database.PictureData;
import cn.com.magnity.coresdksample.utils.PreferencesUtils;
import cn.com.magnity.coresdksample.utils.TimeUitl;

import static cn.com.magnity.coresdksample.Config.ROOT_DIR_NAME;
import static cn.com.magnity.coresdksample.Config.TempThreshold;
import static cn.com.magnity.coresdksample.MyApplication.photoNameSave;
import static cn.com.magnity.coresdksample.utils.FlieUtil.getFileName;
import static cn.com.magnity.coresdksample.utils.FlieUtil.getFolderPathToday;

/**
 * 保存信息handler
 */
public class RecordHandler  extends Handler {
    private String TAG="RecordHandler";
    public static final int MSG_RECODE_PERSON=500;
    public static final int MSG_RECODE_TEMP=MSG_RECODE_PERSON+1;

    private static long cacheTime = 0;

    private String personPath=null;
    private String tempPath=null;
    private float temp;
    private JuGeFaceRect faceRect;
    private static class InnerClass {
        public static RecordHandler intance = new RecordHandler();
    }

    /*静态内部类单例*/
    public static RecordHandler getInstance() {
        return InnerClass.intance;
    }

    public enum photoType {
        //人像
        PERSON,
        //温度
        TEMP
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case MSG_RECODE_TEMP:
                //保存温度记录信息
                RecordHolder tempRecordHolder=(RecordHolder)msg.obj;
                temp=tempRecordHolder.getTemp();
                //保存图片，获得地址
                tempPath = saveBitmap(tempRecordHolder.getBitmap(), tempRecordHolder.getTemp(), "Temp");
                break;
            case MSG_RECODE_PERSON:
                //保存人脸记录信息
                RecordHolder personRecordHolder=(RecordHolder)msg.obj;
                //保存图片，获得地址
                personPath = saveBitmap(personRecordHolder.getBitmap(), personRecordHolder.getTemp(), "Person");

                //添加记录进数据库
                recordData(personPath,tempPath, temp,TimeUitl.getDate());
                recordPicViewData(personPath,tempPath,faceRect);
                break;
        }
    }

    public void sendRecord(int MSG, Bitmap bitmap,float temp,JuGeFaceRect faceRect){
        Message message=this.obtainMessage();
        message.what=MSG;
        RecordHolder recordHolder=new RecordHolder();
        recordHolder.setTemp(temp);
        recordHolder.setBitmap(bitmap);
        message.obj=recordHolder;
        //延时，避免保存冲突，确保每次间隔100毫秒。
        sendMessageDelayed(message,100);
        this.faceRect=faceRect;
    }


    private void recordData(String personPath, String tempPath, float temp, String date) {
        Log.e(TAG, "记录保存到数据库: ");
        PhotoRecordDb photoRecordDb=new PhotoRecordDb();
        photoRecordDb.setDate(Long.valueOf(date));
        photoRecordDb.setPersonPath(personPath);
        photoRecordDb.setTemperPath(tempPath);
        photoRecordDb.setTemp(temp);
        photoRecordDb.save();
        Log.e(TAG, "photoRecordDb: "+photoRecordDb.toString());

    }

    private void recordPicViewData(String personPath, String tempPath, JuGeFaceRect faceRect) {
        PictureData pictureData=new PictureData();
        pictureData.setPersonPath(personPath);
        pictureData.setTemperPath(tempPath);
        pictureData.setX1(faceRect.getxStart());
        pictureData.setX2(faceRect.getxStart());
        pictureData.setX3(faceRect.getxStop());
        pictureData.setX4(faceRect.getxStop());
        pictureData.setY1(faceRect.getyStart());
        pictureData.setY2(faceRect.getyStart());
        pictureData.setY3(faceRect.getyStop());
        pictureData.setY4(faceRect.getyStop());
        pictureData.save();
        Log.e(TAG, "recordPicViewData: "+pictureData.toString());
    }

    /**
     * 保存图片到SD卡上
     */
    private String saveBitmap(Bitmap bitmap, float temp, String type) {
        Log.i(TAG, "保存图片到SD卡上: ");
        // 保存图片到SD卡上
        String maxTmp = String.valueOf(temp);
        if (String.valueOf(maxTmp).length() >= 4) {//最多保留4位
            maxTmp = maxTmp.substring(0, 4);
        }
        String fileName = TimeUitl.getDate() + "_" + maxTmp + "_" + type + ".png";
        File file = new File(getFolderPathToday(), fileName);
        if (file.exists()) {
            file.delete();
        } else {
            photoNameSave.saveLog(type+"照片", fileName + "\r\n");
        }
        //保存到指定文件
        saveBitmapIntoLocal(file, bitmap);

        //必须保存相对路径到数据库，web服务才可以访问到
        String relativePath = ROOT_DIR_NAME + File.separator + "Pictures" + File.separator + getFileName() + File.separator + fileName;
        Log.e(TAG, "relativePath: "+relativePath);
        return relativePath;

    }

    /**
     * 将图片保存到指定File
     * */
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
