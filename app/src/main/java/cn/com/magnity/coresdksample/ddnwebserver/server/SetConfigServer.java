package cn.com.magnity.coresdksample.ddnwebserver.server;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;


import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.List;

import cn.com.magnity.coresdksample.MyApplication;
import cn.com.magnity.coresdksample.handler.TempHandler;
import cn.com.magnity.coresdksample.usecache.CurrentConfig;
import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;
import cn.com.magnity.coresdksample.ddnwebserver.database.PhotoRecordDb;
import cn.com.magnity.coresdksample.ddnwebserver.model.CalibratPositionData;
import cn.com.magnity.coresdksample.ddnwebserver.model.CameraData;
import cn.com.magnity.coresdksample.ddnwebserver.model.FFCData;
import cn.com.magnity.coresdksample.ddnwebserver.model.PhotoDataRespons;
import cn.com.magnity.coresdksample.ddnwebserver.database.PictureData;
import cn.com.magnity.coresdksample.ddnwebserver.model.RecordQueryRequest;
import cn.com.magnity.coresdksample.ddnwebserver.model.TemperCameraData;
import cn.com.magnity.coresdksample.ddnwebserver.model.TemperatureData;
import cn.com.magnity.coresdksample.ddnwebserver.model.ValidAreaData;
import cn.com.magnity.coresdksample.ddnwebserver.model.VoiceData;
import cn.com.magnity.coresdksample.ddnwebserver.model.WifiData;
import cn.com.magnity.coresdksample.ddnwebserver.service.BindServiceTest;
import cn.com.magnity.coresdksample.ddnwebserver.util.TimeUtils;
import cn.com.magnity.coresdksample.utils.PreferencesUtils;
import cn.com.magnity.coresdksample.utils.voice.TtsSpeak;

import static android.content.Context.BIND_AUTO_CREATE;

/***
 * 设置参数到本机
 * */
public class SetConfigServer {

    private static final String TAG = "SetConfigServer";
    private BindServiceTest.Mybinder myBinder;

    private Context context = MyApplication.getInstance();


    private static class InnerClass {
        private static SetConfigServer setConfigServer = new SetConfigServer();
    }

    private SetConfigServer() {
        context.bindService(new Intent(context, BindServiceTest.class), connection, BIND_AUTO_CREATE);
    }

    /*静态内部类单例*/
    public static SetConfigServer getInstance() {
        return InnerClass.setConfigServer;
    }

    /**
     * 设置wifi信息到本机
     * 由于wifi存在与服务中
     * 因此需要与服务绑定，通过binder控制
     */
    public void setWifiData(WifiData wifiData) {
        /*SP操作，存入本地*/
        PreferencesUtils.put(WebConfig.WIFI_NAME, wifiData.getWifi_name());
        PreferencesUtils.put(WebConfig.WIFI_PASSWD, wifiData.getWifi_passwd());
        //缓存更新一下
        CurrentConfig.getInstance().updateSetting();
        /*启动wifi服务，发送*/
        if (myBinder != null) {
            myBinder.getBinderInfo();
            myBinder.setBinderInfo(wifiData);
        } else {
            Log.e(TAG, "myBinder==null ");
        }


    }

    /**
     * 设置语音信息到本机
     */
    public void setVoiceData(VoiceData voiceData) {
        Log.i(TAG, "setVoiceData: ");
        Log.i(TAG, "voiceData:getError_voice " + voiceData.getError_voice());
        Log.i(TAG, "voiceData:getNormal_voice" + voiceData.getNormal_voice());
        Log.i(TAG, "voiceData:getSystem_voice " + voiceData.getSystem_voice());
        Log.i(TAG, "voiceData:getVoice_speed " + voiceData.getVoice_speed());
        /*存入SP*/
        PreferencesUtils.put(WebConfig.VOICE_SPEED, voiceData.getVoice_speed());
        PreferencesUtils.put(WebConfig.NORMAL_VOICE, voiceData.getNormal_voice());
        PreferencesUtils.put(WebConfig.SYSTEM_VOICE, voiceData.getSystem_voice());
        PreferencesUtils.put(WebConfig.ERROR_VOICE, voiceData.getError_voice());
        CurrentConfig.getInstance().updateSetting();
        TtsSpeak.getInstance().setSpeed();
        /*直接修改Tts配置*/

    }

    /**
     * 设置温度信息到本机
     * 温度阈值
     */
    public void setTemperatureData(TemperatureData temperatureData) {
        /*存入SP*/
        /*直接修改配置*/
        Log.i(TAG, "setTemperatureData: " + temperatureData.toString());
        PreferencesUtils.put(WebConfig.TEMPERATURE_THRESHOLD, temperatureData.getTemperature_threshold1());
        CurrentConfig.getInstance().updateSetting();
    }

    /**
     * 设置温度摄像头相关信息到本机
     * 目标距离
     */
    public void setTemperatureCameraData(TemperCameraData temperCameraData) {
        /*存入SP*/
        /*直接修改配置*/
        Log.i(TAG, "setTemperatureCameraData: " + temperCameraData.toString());
        PreferencesUtils.put(WebConfig.DISTANCE, temperCameraData.getDistance());
        CurrentConfig.getInstance().updateSetting();
        //完成配置
        TempHandler.getInstance().sendTemperMessge(TempHandler.MSG_DISTANCE, temperCameraData.getDistance(), 100);
    }


    /**
     * 设置摄像头信息到本机
     * 摄像头曝光值
     */
    public void setCameraData(CameraData cameraData) {
        /*存入SP*/
        /*直接修改配置*/
        Log.i(TAG, "setCameraData: " + cameraData.toString());
        EventBus.getDefault().post(cameraData);
        PreferencesUtils.put(WebConfig.CAMERA_EXPLORE, cameraData.getExplorer());
        CurrentConfig.getInstance().updateSetting();
    }

    /**
     * 设置FFC信息到本机
     * FFC补偿参数，可为负数
     * FFC黑体校准参考值
     */
    public void setFFCData(FFCData ffcData) {
        /*存入SP*/
        /*直接修改配置*/
        //如果补偿不为空，则表示为设置FFC补偿参数
        if (ffcData == null) {
            //如果都是空的，则表示为平均黑体校准
            Log.i(TAG, "FFC平均黑体校准: ");
            //校准值为0，标志平均温度校准
            TempHandler.getInstance().sendTemperMessge(TempHandler.MSG_IN, 0, 100);
            return;
        }
        if (ffcData.getCompensation() != 0) {
            Log.i(TAG, "FFC补偿参数，可为负数 :" + ffcData.getCompensation());
            PreferencesUtils.put(WebConfig.FFC_COMPENSATION_PARAMETER, ffcData.getCompensation());
        } else if (ffcData.getCalibration() != 0) {
            //如果黑体校准参数不为空，则表示为设置黑体校准参数
            Log.i(TAG, "FFC黑体校准参考值: " + ffcData.getCalibration());
            //开启指定温度黑体校准
            TempHandler.getInstance().sendTemperMessge(TempHandler.MSG_IN, ffcData.getCalibration(), 100);
            PreferencesUtils.put(WebConfig.FFC_CALIBRATION_PARAMETER, ffcData.getCalibration());
        }
        CurrentConfig.getInstance().updateSetting();
    }


    /**
     * 获取图片路径
     * 人脸定位
     * 用于预览和校准
     */
    public PictureData getPictureData() {
        //寻找最新保存的图片信息
        PictureData pictureData = LitePal.findLast(PictureData.class);
        pictureData.setMoveX(PreferencesUtils.getInt(WebConfig.MOVEX, 0));
        pictureData.setMoveY(PreferencesUtils.getInt(WebConfig.MOVEY, 0));
        pictureData.setScale(PreferencesUtils.getFloat(WebConfig.SCALE, 1));
        return pictureData;
    }


    /**
     * 设置红外位置校准信息
     */
    public void setCalibratPosition(CalibratPositionData calibratPositionData) {
        /*存入SP*/
        /*直接修改配置*/
        PreferencesUtils.put(WebConfig.MOVEX, calibratPositionData.getMoveX());
        PreferencesUtils.put(WebConfig.MOVEY, calibratPositionData.getMoveY());
        PreferencesUtils.put(WebConfig.SCALE, calibratPositionData.getScale());

        CurrentConfig.getInstance().updateSetting();
        Log.i(TAG, "setCalibratPosition: " + calibratPositionData.toString());
    }

    /**
     * 获取有效区域信息
     */
    public ValidAreaData getValidAreaData() {
        ValidAreaData validAreaData = new ValidAreaData();
        validAreaData.setLineUp(PreferencesUtils.getInt(WebConfig.LINEUP, 20));
        validAreaData.setLineLeft(PreferencesUtils.getInt(WebConfig.LINELEFT, 20));
        validAreaData.setLineDown(PreferencesUtils.getInt(WebConfig.LINEDWON, 620));
        validAreaData.setLineRight(PreferencesUtils.getInt(WebConfig.LINERIGHT, 450));
        return validAreaData;
    }

    /**
     * 设置有效区域信息
     */
    public void setValidAreaData(ValidAreaData validAreaData) {
        PreferencesUtils.put(WebConfig.LINEUP, validAreaData.getLineUp());
        PreferencesUtils.put(WebConfig.LINELEFT, validAreaData.getLineLeft());
        PreferencesUtils.put(WebConfig.LINEDWON, validAreaData.getLineDown());
        PreferencesUtils.put(WebConfig.LINERIGHT, validAreaData.getLineRight());

        CurrentConfig.getInstance().updateSetting();
        Log.i(TAG, "setValidAreaData: " + validAreaData);
    }

    /**
     * 查询记录
     */
    public PhotoDataRespons queryRecord(RecordQueryRequest recordQueryRequest) {
        //根据条件查询的所有记录
        List<PhotoRecordDb> photoRecordDbList = null;
        //统计条件查询的数量
        List<PhotoRecordDb> countSizeList = null;
        //排序条件 asc为升序，desc为降序。
        String orders = "date asc";
//        String orders = "id asc";
        if (recordQueryRequest.getOrders() != null) {
//            //如果包含了date
//            if(recordQueryRequest.getOrders().contains("date")){
//                 recordQueryRequest.setOrders(recordQueryRequest.getOrders().replace("date","id"));
//            }
            Log.i(TAG, "queryRecord:orders " + orders);
        }
        //判断是否有时间条件，如果没有时间则默认为查询所有
        if (recordQueryRequest.getStarTime() == null) {
            photoRecordDbList = LitePal.select("personPath", "temperPath", "date", "temp")
                    .where(" temp>=" + recordQueryRequest.getMinTemp() + " and temp<=" + recordQueryRequest.getMaxTemp())
                    .limit(recordQueryRequest.getEverPageNumber())
                    .offset((recordQueryRequest.getCurrentpage() - 1) * recordQueryRequest.getEverPageNumber())
                    .order(orders)
                    .find(PhotoRecordDb.class);

            countSizeList = LitePal.select("personPath", "temperPath", "date", "temp")
                    .where(" temp>=" + recordQueryRequest.getMinTemp() + " and temp<=" + recordQueryRequest.getMaxTemp())
                    .find(PhotoRecordDb.class);
        } else {
            //根据时间区间，currentpage，size，温度区间。
            photoRecordDbList = LitePal.select("personPath", "temperPath", "date", "temp")
                    .where("date>=" + TimeUtils.getDatatoString(recordQueryRequest.getStarTime()) +
                            " and date<=" + TimeUtils.getDatatoString(recordQueryRequest.getEndTime()) +
                            " and temp>=" + recordQueryRequest.getMinTemp() +
                            " and temp<=" + recordQueryRequest.getMaxTemp())
                    .limit(recordQueryRequest.getEverPageNumber())
                    .offset((recordQueryRequest.getCurrentpage() - 1) * recordQueryRequest.getEverPageNumber())
                    .order(orders)
                    .find(PhotoRecordDb.class);

            countSizeList = LitePal.select("personPath", "temperPath", "date", "temp")
                    .where("date>=" + TimeUtils.getDatatoString(recordQueryRequest.getStarTime()) +
                            " and date<=" + TimeUtils.getDatatoString(recordQueryRequest.getEndTime()) +
                            " and temp>=" + recordQueryRequest.getMinTemp() +
                            " and temp<=" + recordQueryRequest.getMaxTemp())
                    .find(PhotoRecordDb.class);
        }

        Log.i(TAG, "queryRecord: " + countSizeList.size());
        //要把数据的数量包裹一起发过去
        PhotoDataRespons respons = new PhotoDataRespons();
        respons.setPhotoRecordDbList(photoRecordDbList);
        respons.setAllSize(countSizeList.size());
        return respons;
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (BindServiceTest.Mybinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


}
