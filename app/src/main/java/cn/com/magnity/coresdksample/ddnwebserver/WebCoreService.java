/*
 * Copyright © 2018 Zhenjie Yan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.com.magnity.coresdksample.ddnwebserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.util.concurrent.TimeUnit;

import cn.com.magnity.coresdksample.handler.DelayDoHandler;
import cn.com.magnity.coresdksample.ddnwebserver.util.NetUtils;
import cn.com.magnity.coresdksample.usecache.CurrentConfig;
import cn.com.magnity.coresdksample.utils.NetUtil;

/**
 * Created by Zhenjie Yan on 2018/6/9.
 */
public class WebCoreService extends Service {
    private final static String TAG="WebCoreService";
    private static boolean isRunning = false;
    private Server mServer;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        isRunning=true;
        //初始化默认值
        CurrentConfig.getInstance().updateSetting();
        initServer();
    }

    /**
     * 初始化Server
     * 在ip变化时，也需要重新初始化
     * */
    private void initServer(){
        mServer = AndServer.serverBuilder(this)
                .inetAddress(NetUtils.getLocalIPAddress())
                .port(8088)
                .timeout(10, TimeUnit.SECONDS)
                .listener(new Server.ServerListener() {
                    @Override
                    public void onStarted() {

                        String hostAddress = mServer.getInetAddress().getHostAddress();
                        //开启成功的回调
                        Log.e(TAG, "web服务开启成功: "+hostAddress);
                        DelayDoHandler.getInstance().sendDelayVoice("web服务开启成功: ",2*1000);
                    }

                    @Override
                    public void onStopped() {
                        Log.i(TAG, "停止web服务: ");
                    }

                    @Override
                    public void onException(Exception e) {
                        Log.e(TAG, "web服务onException: ");
                    }
                })
                .build();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopServer();
        super.onDestroy();
    }


    /**
     * Start server.
     */
    private void startServer() {
        Log.i(TAG, "startServer: ");
        if (mServer.isRunning()) {
            //正在运行中
            Log.i(TAG, "startServer: isRunning");
//            Log.i(TAG, "mServer.getInetAddress().getHostAddress(): "+mServer.getInetAddress().getHostAddress());
//            Log.i(TAG, "NetUtil.getLocalIPAddress(): "+NetUtil.getLocalIPAddress());
            //如果运行中的服务ip也连接的ip不符。则需要重新连接
            if (NetUtil.getLocalIPAddress() == null) {
                return;
            }
            if(!mServer.getInetAddress().getHostAddress().equals(NetUtil.getLocalIPAddress().getHostAddress())){
                Log.i(TAG, "服务ip也连接的ip不符。则需要重新连接: ");
                initServer();
                mServer.startup();
            }
        } else {
            Log.i(TAG, "startServer: startup");
            mServer.startup();
        }
    }

    /**
     * Stop server.
     */
    private void stopServer() {
        isRunning=false;
        mServer.shutdown();
    }

    public static boolean isServiceRunning() {
        return isRunning;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}