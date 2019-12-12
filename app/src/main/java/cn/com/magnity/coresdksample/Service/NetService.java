package cn.com.magnity.coresdksample.Service;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lpnetstatus.NetStatusBus;
import com.example.lpnetstatus.annotation.NetSubscribe;
import com.example.lpnetstatus.annotation.type.NetType;

import java.util.List;

import cn.com.magnity.coresdksample.Service.handler.DelayDoHandler;
import cn.com.magnity.coresdksample.ddnwebserver.WebCoreService;
import cn.com.magnity.coresdksample.usecache.CurrentConfig;
import cn.com.magnity.coresdksample.utils.NetUtil;
import cn.com.magnity.coresdksample.utils.WifiAdmin;

import static cn.com.magnity.coresdksample.Config.DefaultWifiName;
import static cn.com.magnity.coresdksample.Config.DefaultWifiPassWord;
import static cn.com.magnity.coresdksample.Config.MSG1;

/**
 * 网络服务
 * 维持网络的连接
 * 切换网络连接等
 */
public class NetService extends Service {
    private String TAG = "NetService";
    private WifiAdmin wifiAdmin;
    private MyWifiScanHandler MyWifiScanHandler = new MyWifiScanHandler();
    private String currentIp = "0.0.0.0";
    private static boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        isRunning=true;
        wifiAdmin = new WifiAdmin(getApplicationContext());
        NetStatusBus.getInstance().register(this);

        //启动
        MyWifiScanHandler.sendEmptyMessage(MSG1);
    }


    /**
     * 监听网络变化
     */
    @NetSubscribe()
    private void netStatus(NetType netType) {
        switch (netType) {
            case NONE:
                Log.e(TAG, "网络连接中断: ");
                DelayDoHandler.getInstance().sendDelayVoice("网络连接中断 请检查", 2 * 1000);
                currentIp = "0.0.0.0";
                break;
            case WIFI:
                //如果ip为null则表示没有连接上，正在连接中
                if (NetUtil.getLocalIPAddress() == null) {
                    return;
                }
                Log.e(TAG, "wifi已连接: ");
                wifiAdmin = new WifiAdmin(getApplicationContext());
                currentIp = NetUtil.getLocalIPAddress().getHostAddress();
                Log.e(TAG, "wifi名称: " + wifiAdmin.getSSID());
                Log.e(TAG, "IP: " + NetUtil.getLocalIPAddress().getHostAddress());
                DelayDoHandler.getInstance().sendDelayVoice("已连接wifi  " + wifiAdmin.getSSID() + "  地址为 " + currentIp, 2 * 1000);

                startNeedNetService();
                break;
            case MOBILE:
                Log.e(TAG, "移动网络已连接: ");
                startNeedNetService();
                break;
            case ETHERNET:
                if (currentIp.equals(NetUtil.getLocalIPAddress().getHostAddress())) {
                    return;
                }
                Log.e(TAG, "有线网络已经连接: ");
                currentIp = NetUtil.getLocalIPAddress().getHostAddress();
                Log.e(TAG, "IP: " + NetUtil.getLocalIPAddress().getHostAddress());
                DelayDoHandler.getInstance().sendDelayVoice("有线网络已连接   地址为" + currentIp, 2 * 1000);
                startNeedNetService();
                break;
            default:
        }

    }


    /**
     * wifi扫描连接
     */
    private class MyWifiScanHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG1:
                    //首先移除之前的Messageges
                    MyWifiScanHandler.removeMessages(MSG1);

                    //自动连接指定的wifi
                    //Log.i(TAG, "自动连接指定的wifi: ");
                    connectWifi(CurrentConfig.getInstance().getCurrentData().getWifi_name(), CurrentConfig.getInstance().getCurrentData().getWifi_passwd());
                    //检查网络
                    checkNet();
                    //每隔5秒钟询问一次
                    MyWifiScanHandler.sendEmptyMessageDelayed(MSG1, 5000);
                    break;

            }
        }
    }

    /**
     * 检查网络是否畅通
     */
    private void checkNet() {
        boolean networkConnected = NetUtil.isNetworkConnected(this);
        if (!networkConnected) {
            //两秒后播报网络状态
            DelayDoHandler.getInstance().sendDelayVoice("网络不通 请检查", 2 * 1000);
        }
    }

    /**
     * 连接wifi
     * 优先连接默认wifi
     * 然后连接指定wifi
     *
     * @param wifiName
     * @param wifiPasswd
     */
    private void connectWifi(String wifiName, String wifiPasswd) {
        //打开wifi
        wifiAdmin = new WifiAdmin(getApplicationContext());
        wifiAdmin.openWifi();
        //Log.i(TAG, "当前网络名称: " + wifiAdmin.getSSID() + "当前信号强度" + wifiAdmin.getWifRssi());
        //如果连接的wifi是默认wifi，则返回
        if (wifiAdmin.getSSID().equals(DefaultWifiName)) {
            //Log.e(TAG, "连接的wifi是默认wifi，: "+DefaultWifiName);
            return;
        }
        //搜索是否存在默认wifi
        boolean isDefaultWifi = searchWifi(DefaultWifiName);
        //如果找到默认wifi，就连接
        if (isDefaultWifi) {
            Log.e(TAG, "正在连接默认wifi，: ");
            wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(DefaultWifiName, DefaultWifiPassWord, 3));
            return;
        }

        //如果已经连接到目标wifi，也返回
        if (wifiAdmin.getSSID().equals(wifiName)) {
            // Log.e(TAG, "连接的wifi是指定wifi ："+wifiName);
            return;
        }

        //如果找到指定的wifi
        boolean isWifi = searchWifi(wifiName);
        if (isWifi) {
            Log.e(TAG, "正在连接指定wifi: " + wifiName);
            wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(wifiName, wifiPasswd, 3));
        }

    }

    /**
     * 搜索是否有指定wifi
     *
     * @param wifiName
     */
    private boolean searchWifi(String wifiName) {
        //开始搜索wifi
        wifiAdmin.startScan();
        List<ScanResult> scanResultList;
        scanResultList = wifiAdmin.getWifiList();
        //Log.i(TAG, "scanResultList.size(): "+scanResultList.size());
        if (scanResultList == null) {
            return false;
        }
        for (int i = 0; i < scanResultList.size(); i++) {
//            Log.i(TAG, "scanResultList.SSID(): " + scanResultList.get(i).SSID);
            if (scanResultList.get(i).SSID.equals(wifiName)) {//搜索到wifi名称相同
                Log.i(TAG, "找到指定wifi： " + wifiName + "  准备连接: ");
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {

    }

    public static boolean isServiceRunning() {
        return isRunning;
    }

    /**
     * 在网络已经连接的时候
     * 重新开启需要网络的服务
     * 因为ip已经变化
     * 所以需要重新开启
     * */
    private  void startNeedNetService(){
        Log.e(TAG, "startNeedNetService: ");
        //开启web配置服务
        this.startService(new Intent(this, WebCoreService.class));
        //开启ftp服务
        this.startService(new Intent(this, FtpService.class));
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        wifiAdmin = null;
        MyWifiScanHandler.removeCallbacksAndMessages(null);
        MyWifiScanHandler = null;
        isRunning=false;
    }
}
