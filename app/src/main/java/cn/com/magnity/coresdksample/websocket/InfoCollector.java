package cn.com.magnity.coresdksample.websocket;

import android.os.Build;
import android.util.Log;

import cn.com.magnity.coresdksample.Service.update.SoftWareUpgradeService;
import cn.com.magnity.coresdksample.websocket.bean.RunningInfo;
import cn.com.magnity.coresdksample.websocket.bean.SoftWareVersionsInfo;


/**
 * 测试使用的信息采集线程
 * 采集本机硬件信息状态
 * 启动记录
 * 摄像头，刷卡器状态
 * 语音模块状态
 */
public class InfoCollector implements Runnable {
    private static final String TAG = "InfoCollector";
    private static boolean status = false;

    @Override
    public void run() {
        while (status) {
            //十秒钟采集一次信息
            Log.i(TAG, "-------------InfoCollector十秒钟采集一次信息----------");
            TimeDelay(10 * 1000);

            testDo();
        }


    }

    private void testDo() {

        RunningInfo runningInfo=new RunningInfo();
        runningInfo.setInfrared_camera_status("温度摄像头测试信息");
        runningInfo.setCameraStatus("人脸摄像头测试信息");
        runningInfo.setError_info("异常测试信息");
        runningInfo.setConfiguration_status("配置测试信息");
        runningInfo.setVoiceStatus("语音测试信息");
        Log.i(TAG, "testDo: "+runningInfo.toString());
        runningInfo.upload();

    }

    public InfoCollector() {
        startCollect();
    }

    public void startCollect() {
        Log.i(TAG, "-------------startCollect-------------- ");
        status = true;
    }

    public void stopCollect() {
        Log.i(TAG, "-------------startCollstopCollectect-------------- ");
        status = false;
    }

    private void TimeDelay(int times) {
        try {
            Thread.sleep(times);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
