package cn.com.magnity.coresdksample.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import cn.com.magnity.coresdksample.service.LampService;
import cn.com.magnity.coresdksample.handler.DelayDoHandler;
import cn.com.magnity.coresdksample.utils.voice.TtsSpeak;
import cn.com.magnity.coresdksample.websocket.bean.RunningInfo;


public class UsbReceiver extends BroadcastReceiver {
    private static final String TAG = "UsbReceiver";
    private static final String HDUSBCamera = "HD USB Camera";//人脸摄像头名称
    private static final String ThermoCubeStream = "ThermoCube stream";//热成像摄像头名称
    private RunningInfo runningInfo = new RunningInfo();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        String deviceName = usbDevice.getDeviceName();
        Log.e(TAG, "--- 接收到广播， action: " + action);
        //USB连接
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            Log.e(TAG, "USB device is AttacheddeviceName:        " + deviceName);
            Log.e(TAG, "USB device is Attached:getVendorId       " + usbDevice.getVendorId());
            Log.e(TAG, "USB device is Attached:getProductId      " + usbDevice.getProductId());
            Log.e(TAG, "USB device is Attached:getDeviceId       " + usbDevice.getDeviceId());
            Log.e(TAG, "USB device is Attached: getProductName   " + usbDevice.getProductName());
            if (usbDevice.getProductName().equals(HDUSBCamera)) {
                Log.i(TAG, "人脸摄像头已重连，请重新开关机: ");
                runningInfo.setCameraStatus("人脸摄像头重连，请重新开关机");
//
                LampService.setStatus(LampService.LampStatus.error);
                TtsSpeak.getInstance().SystemSpeech("人脸摄像头重连，请重新开关机");
            }
            else if (usbDevice.getProductName().equals(ThermoCubeStream)) {
                Log.i(TAG, "热成像摄像头已重连: ");
                runningInfo.setInfrared_camera_status("热成像摄像头已重连");
                LampService.setStatus(LampService.LampStatus.error);
                TtsSpeak.getInstance().SystemSpeech("热成像摄像头已重连");
            }
        }
        //USB断开
        else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            Log.e(TAG, "USB device is Detached:deviceName:       " + deviceName);
            Log.e(TAG, "USB device is Detached:getVendorId       " + usbDevice.getVendorId());
            Log.e(TAG, "USB device is Detached:getProductId      " + usbDevice.getProductId());
            Log.e(TAG, "USB device is Detached:getDeviceId       " + usbDevice.getDeviceId());
            Log.e(TAG, "USB device is Detached: getProductName   " + usbDevice.getProductName());
            if (usbDevice.getProductName().equals(HDUSBCamera)) {
                Log.i(TAG, "人脸摄像头已拔出: ");
                runningInfo.setCameraStatus("人脸摄像头已拔出: ");
                LampService.setStatus(LampService.LampStatus.error);
                TtsSpeak.getInstance().SystemSpeech("人脸摄像头已拔出");
            } else if (usbDevice.getProductName().equals(ThermoCubeStream)) {
                Log.i(TAG, "热成像摄像头已拔出: ");
                runningInfo.setInfrared_camera_status("热成像摄像头已拔出");
                LampService.setStatus(LampService.LampStatus.error);
                TtsSpeak.getInstance().SystemSpeech("热成像摄像头已拔出");
            }
        }
        //延时发送websocket，因为开机自启动的时候，系统没启动完成获取mac地址失败
        DelayDoHandler.getInstance().sendDelayStart(runningInfo, 1000);

    }
}

