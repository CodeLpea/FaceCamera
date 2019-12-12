package cn.com.magnity.coresdksample.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import cn.com.magnity.coresdksample.usecache.CurrentConfig;
import cn.com.magnity.coresdksample.utils.lampUtil;
import cn.com.magnity.coresdksample.utils.voice.TtsSpeak;
import cn.com.magnity.coresdksample.websocket.bean.RunningInfo;

import static cn.com.magnity.coresdksample.MyApplication.isInit;
import static cn.com.magnity.coresdksample.MyApplication.isplay;


public class UsbReceiver extends BroadcastReceiver {
    private static final String TAG="UsbReceiver";
    private static final String HDUSBCamera="HD USB Camera";//人脸摄像头名称
    private static final String ThermoCubeStream="ThermoCube stream";//热成像摄像头名称
    private RunningInfo runningInfo=new RunningInfo();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        String deviceName = usbDevice.getDeviceName();
        Log.e(TAG,"--- 接收到广播， action: " + action);
        Log.e(TAG,"--- 接收到广播， isInit: " + isInit);
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
         /*   Log.e(TAG, "USB device is AttacheddeviceName:        " + deviceName);
            Log.e(TAG, "USB device is Attached:getVendorId       " + usbDevice.getVendorId());
            Log.e(TAG, "USB device is Attached:getProductId      " + usbDevice.getProductId());
            Log.e(TAG, "USB device is Attached:getDeviceId       " + usbDevice.getDeviceId());
            Log.e(TAG, "USB device is Attached: getProductName   " + usbDevice.getProductName());*/
            if(usbDevice.getProductName().equals(HDUSBCamera)){
                Log.i(TAG, "人脸摄像头已连接，请重新开关机: ");
                runningInfo.setCameraStatus("人脸摄像头已连接，请重新开关机: ");
                lampUtil.setlamp(1,500,-1);//设置默认的故障灯光
                TtsSpeak.getInstance().SystemSpeech("人脸摄像头已连接，请重新开关机");
            }
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
        /*    Log.e(TAG, "USB device is Detached:deviceName:       " + deviceName);
            Log.e(TAG, "USB device is Detached:getVendorId       " + usbDevice.getVendorId());
            Log.e(TAG, "USB device is Detached:getProductId      " + usbDevice.getProductId());
            Log.e(TAG, "USB device is Detached:getDeviceId       " + usbDevice.getDeviceId());
            Log.e(TAG, "USB device is Detached: getProductName   " + usbDevice.getProductName());*/
            if(usbDevice.getProductName().equals(HDUSBCamera)){
                Log.i(TAG, "人脸摄像头已拔出: ");
                runningInfo.setCameraStatus("人脸摄像头已拔出: ");
                lampUtil.setlamp(2,500,-1);//设置默认的故障灯光
                TtsSpeak.getInstance().SystemSpeech("人脸摄像头已拔出");
            }else if(usbDevice.getProductName().equals(ThermoCubeStream)&&isplay==false&&isInit==false){
                Log.i(TAG, "热成像摄像头已拔出: ");
                runningInfo.setInfrared_camera_status("热成像摄像头已拔出: ");
                lampUtil.setlamp(2,500,-1);//设置默认的故障灯光
                TtsSpeak.getInstance().SystemSpeech("热成像摄像头已拔出");
            }else if(usbDevice.getProductName().equals(ThermoCubeStream)&&isInit==true){
                Log.i(TAG, "完成热成像摄像头初始化: ");
                runningInfo.setInfrared_camera_status("完成热成像摄像头初始化: ");
                isInit=false;//完成初始化
            }
        }
        runningInfo.upload();
    }
}

