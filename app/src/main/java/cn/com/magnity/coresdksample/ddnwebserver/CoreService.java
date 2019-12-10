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

import cn.com.magnity.coresdksample.ddnwebserver.util.NetUtils;

/**
 * Created by Zhenjie Yan on 2018/6/9.
 */
public class CoreService extends Service {
    private final static String TAG="CoreService";

    private Server mServer;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        mServer = AndServer.serverBuilder(this)
            .inetAddress(NetUtils.getLocalIPAddress())
            .port(8080)
            .timeout(10, TimeUnit.SECONDS)
            .listener(new Server.ServerListener() {
                @Override
                public void onStarted() {

                    String hostAddress = mServer.getInetAddress().getHostAddress();
                    //开启成功的回调
                    Log.e(TAG, "web服务开启成功: "+hostAddress);
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
            Log.i(TAG, "startServer: isRunning");
            String hostAddress = mServer.getInetAddress().getHostAddress();
        } else {
            Log.i(TAG, "startServer: startup");
            mServer.startup();
        }
    }

    /**
     * Stop server.
     */
    private void stopServer() {
        mServer.shutdown();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}