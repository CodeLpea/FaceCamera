package cn.com.magnity.coresdksample.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.thirdparty.E;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import java.util.ArrayList;
import java.util.List;
import cn.com.magnity.coresdksample.MainActivity;
import cn.com.magnity.coresdksample.utils.NetUtil;

import static cn.com.magnity.coresdksample.Config.MSG2;



/**
 * Created by Administrator on 2019.03.08.
 */

public class FtpService extends Service {

  private static final String TAG="FtpService";
    private FtpServer server;
    private String user = "didano";
    private String password = "12345678";
    private static String rootPath;
    private int port = 2221;
    private static boolean isRuning=false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRuning=true;
    }

    private void initFtp() {
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        try {
            init();
            Log.i(TAG, "启动ftp服务成功: ");
        } catch (FtpException e) {
            e.printStackTrace();
            Log.i(TAG, "启动ftp服务失败: ");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initFtp();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
        Log.i(TAG, "onDestroy: 关闭ftp服务");
    }

    /**
     * 初始化
     *
     * @throws FtpException
     */
    public void init() throws FtpException {
        release();
        startFtp();
    }

    private void startFtp() throws FtpException {
        FtpServerFactory serverFactory = new FtpServerFactory();

        //设置访问用户名和密码还有共享路径
        BaseUser baseUser = new BaseUser();
        baseUser.setName(user);
        baseUser.setPassword(password);
        baseUser.setHomeDirectory(rootPath);

        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(new WritePermission());
        baseUser.setAuthorities(authorities);
        serverFactory.getUserManager().save(baseUser);


        ListenerFactory factory = new ListenerFactory();
        factory.setPort(port); //设置端口号 非ROOT不可使用1024以下的端口
        serverFactory.addListener("default", factory.createListener());

        if (NetUtil.getLocalIPAddress() == null) {
            return;
        }
        server = serverFactory.createServer();
        server.start();
        Log.i(TAG, "server.isStopped(): "+server.isStopped());
        Log.e(TAG, "startFtp: "+ "ftp://" + NetUtil.getLocalIPAddress().getHostAddress() +
                ":2221\n" +
                "账号:didano\n" +
                "密码:12345678");
    }



    /**
     * 释放资源
     */
    public void release() {
        isRuning=false;
        stopFtp();
    }

    private void stopFtp() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }
    public static boolean isServiceRunning(){
        return isRuning;
    }


}
