package cn.com.magnity.coresdksample.websocket.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import cn.com.magnity.coresdksample.websocket.InfoCollector;
import cn.com.magnity.coresdksample.websocket.WSManager;
import cn.com.magnity.coresdksample.websocket.WSThread;


/**
 * 启动websocket
 * 启动采集信息线程
 * 启动WsThread来发送websocket
 */
public class WebSocketService extends Service {
    private static final String TAG = "WebSocketService";
    private static boolean isRunning = false;
    private WSThread wsThread;

    public WebSocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        Log.i(TAG, "================开启WebSocketService================ ");
        isRunning = true;
        //备注：还需考虑网络状态变化
        //启动采集线程
        // new Thread(new InfoCollector()).start();
        //启动WebSorket
        boolean isOk = WSManager.getInstance().init();
        //如果初始化成功，则开始进行发送信息
        if (isOk) {
            Log.i(TAG, "================WebSocket初始化成功，则开始进行发送信息================ ");
            wsThread = new WSThread();
            wsThread.startThread();
        }

    }

    public static boolean isServiceRunning() {
        return isRunning;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        wsThread.stopThread();
    }
}
