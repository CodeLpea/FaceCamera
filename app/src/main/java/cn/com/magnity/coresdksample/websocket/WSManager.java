package cn.com.magnity.coresdksample.websocket;

import android.os.Handler;
import android.util.Log;


import com.google.gson.Gson;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.logging.Logger;

import cn.com.magnity.coresdksample.MyApplication;
import cn.com.magnity.coresdksample.utils.AppUtils;
import cn.com.magnity.coresdksample.utils.NetUtil;
import cn.com.magnity.coresdksample.websocket.bean.PushRequestInfo;
import cn.com.magnity.coresdksample.websocket.bean.PushResponseInfo;

import static cn.com.magnity.coresdksample.websocket.WSConstant.CMD_TYPE.DOWNLOAD_LEAVE_MESSAGE;
import static cn.com.magnity.coresdksample.websocket.WSConstant.CMD_TYPE.REBOOT_SYSTEM;
import static cn.com.magnity.coresdksample.websocket.WSConstant.CMD_TYPE.SYNCTHING_REPAIRING;
import static cn.com.magnity.coresdksample.websocket.WSConstant.CMD_TYPE.UPDATE_SOFTWARE_VERSION;
import static cn.com.magnity.coresdksample.websocket.WSConstant.CMD_TYPE.UPDATE_STUDENT_LIST;


/**
 * Created by xiaoyuren on 2018/2/27.
 * 项目名称：didano-robot
 * 类描述：websocket管理类 具有连接、断开、接收信息、发送信息功能
 * company：www.didano.cn
 * email：vin.qin@didano.cn
 * 创建时间：2018/2/27 13:52
 */

public class WSManager {
    private static final String TAG = "WSManager";
    /**
     * 重连最小时间间隔2s
     */
    private final int RETRY_MIN_INTERVAL = 3000;

    /**
     * 重连最大时间间隔
     */
    private final int RETRY_MAX_INTERVAL = 5000;

    /**
     * 检测网络状态时间间隔 5s
     */
    private final int CHECK_NETWORK_INTERVAL = 5000;

    /**
     * 重连次数
     */
    private int reconnectCount = 0;

    /**
     * 延时任务
     */
    private Handler handler = new Handler();

    /**
     * websocket客户端
     */
    private WebSocketClient client;
    /**
     * 状态
     */
    private WSStatus wsStatus;
    private static volatile WSManager instance;

    private boolean isInitOk = false;

    public WSStatus getWsStatus() {
        return wsStatus;
    }

    public void setWsStatus(WSStatus wsStatus) {
        this.wsStatus = wsStatus;
    }

    private WSManager() {
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static WSManager getInstance() {
        if (instance == null) {
            synchronized (WSManager.class) {
                if (instance == null)
                    instance = new WSManager();
            }
        }
        return instance;
    }

    /**
     * 初始化websocket
     *
     * @return
     */
    public boolean init() {
        Log.i(TAG, "-----------websocket init-----------------: ");
        Log.i(TAG, "-----------isInitOk: "+isInitOk+"------------");
        /**
         * 已经初始化了，无需再次初始化
         */
        if (isInitOk == true){
            return true;
        }
        else {
            Log.i(TAG, "********************初始化:    "+isInitOk);
            isInitOk = connect();

            return isInitOk;
        }
    }

    public boolean connect() {
        try {
            if (client != null){
                client = null;
            }
            Log.i(TAG, "----------client——before: "+client);
            client = new WSClient(new URI(WSConstant.BASE_API));
            Log.i(TAG, "connect:BASE_API "+WSConstant.BASE_API);
            Log.i(TAG, "----------client——after "+client);
            setWsStatus(WSStatus.CONNECTTING);
            client.connect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getAnonymousLogger("websocket init error");
            return false;
        }
    }

    public void reconnect() {

        if (!NetUtil.isNetworkConnected(MyApplication.getInstance())) {
            /**
             * 网络连接不可用，那么暂停一段时间再请求
             */
            handler.postDelayed(checkNetworkTask, CHECK_NETWORK_INTERVAL);

            return;
        }

        /**
         * 连接已经断开，且不是正在重连
         */
        if (client != null && !client.isOpen() && wsStatus != WSStatus.CONNECTTING) {
            setWsStatus(WSStatus.CONNECTTING);
            reconnectCount++;
            int reconnectTime = RETRY_MIN_INTERVAL;
            if (reconnectCount > 3) {
                int temp = RETRY_MIN_INTERVAL * (reconnectCount - 2);
                reconnectTime = temp > RETRY_MAX_INTERVAL ? RETRY_MAX_INTERVAL : temp;
            }

            handler.postDelayed(reconnectTask, reconnectTime);
        }
    }

    private Runnable reconnectTask = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "reconnect to websocket server: ");
            connect();
        }
    };

    private Runnable checkNetworkTask = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "network is unavailable websocket check status ");
            reconnect();
        }
    };

    /**
     * 取消重连
     */
    private void cancelReconnect() {
        reconnectCount = 0;
        handler.removeCallbacks(reconnectTask);
    }

    /**
     * 断开连接
     *
     * @return
     */
    public boolean disconnect() {
        client.close();
        return true;
    }

    /**
     * 非阻塞发送方法
     * 有可能多线程调用 加锁
     *
     * @param message
     */
    public synchronized void send(String message) {
        if (client != null && client.isOpen()) {
            client.send(message);
        }
    }

    class WSClient extends WebSocketClient {

        public WSClient(URI serverUri) {
            super(serverUri);
        }

        /**
         * Called after an opening handshake has been performed and the given websocket is ready to be written on.
         *
         * @param handshakedata The handshake of the websocket instance
         */
        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.i(TAG, "websocket开启成功: ");
            //连接建立成功
            wsStatus = WSStatus.CONNECT_SUCCESS;
            cancelReconnect();

        }

        @Override
        public void onMessage(String message) {
            if (message != null)
                dispatchMessage(message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
//            Logger.i("ws connect close");
            Log.i(TAG, "code : " + code + " reason : " + reason + " remote : " + remote);
            wsStatus = WSStatus.CONNECT_FAIL;
            reconnect();

        }

        /**
         * Called when errors occurs. If an error causes the websocket connection to fail {@link #onClose(int, String, boolean)} will be called additionally.<br>
         * This method will be called primarily because of IO or protocol errors.<br>
         * If the given exception is an RuntimeException that probably means that you encountered a bug.<br>
         *
         * @param ex The exception causing this error
         **/
        @Override
        public void onError(Exception ex) {
//            Logger.i("ws connect error");
//            wsStatus = WSStatus.CONNECT_FAIL;
//            reconnect();
        }
    }

    /**
     * 分发信息给相应的模块
     *
     * @param msg
     */
    public void dispatchMessage(String msg) {
        Log.i(TAG,"receive push message:" + msg);
        try {
            /**
             * 先回复该命令，再继续其它操作
             */
            Gson gson = new Gson();
            PushRequestInfo pushRequestInfo = gson.fromJson(msg, PushRequestInfo.class);
            String cmd = pushRequestInfo.getCmd();
            PushResponseInfo pushResponseInfo = new PushResponseInfo();
            pushResponseInfo.setDeviceNo(AppUtils.getLocalMacAddressFromWifiInfo(MyApplication.getInstance()));
            pushResponseInfo.setCmd(cmd);
            String response = gson.toJson(pushResponseInfo);
            send(response);

            if (cmd.equals(UPDATE_STUDENT_LIST)) {
                Log.i(TAG, "update student list...");
                /**
                 * 当当前学校在服务器端发生增删改时触发该命令更新学生信息
                 */
//                Intent intent = new Intent(Myapplication.getInstance(), UpdateStudentInfoService.class);
//                Myapplication.getInstance().startService(intent);

            } else if (cmd.equals(UPDATE_SOFTWARE_VERSION)) {
                Log.i(TAG, "check app version...");
                /**
                 * 检查版本更新
                 */
//                String serviceName = UpdateVersionService.class.getSimpleName();
//                if(!AppUtil.isServiceRunning(serviceName)) {
//                    Intent intent = new Intent(App.getContext(), UpdateVersionService.class);
//                    App.getContext().startService(intent);
//                }

            } else if (cmd.equals(REBOOT_SYSTEM)) {
                /**
                 * 重启系统
                 */
                Log.i(TAG,"reboot system...");
//                SystemUtil.rebootSystem2();
            } else if (cmd.equals(DOWNLOAD_LEAVE_MESSAGE)) {
                //下载留言信息
            }else if(cmd.equals(SYNCTHING_REPAIRING)){
                Log.i(TAG, "syncthong repairing...");
                //重新配对同步软件
              //  App.getSyncthingExecutor().repair();
            }
//            Log.getImpl().appenderFlush(false);
        } catch (Exception e) {
        }
    }
}
