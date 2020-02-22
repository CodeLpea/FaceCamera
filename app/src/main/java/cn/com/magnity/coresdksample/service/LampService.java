package cn.com.magnity.coresdksample.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.magnity.coresdksample.utils.ShellUtils;

/**
 * 控制灯服务
 */
public class LampService extends Service {
    private static boolean isRuning = false;
    private Thread lampThread;
    private static LampColor lampColor = LampColor.green;
    private static LampStatus lampStatus = LampStatus.normal;
    public static int times = 500;
    private TimerTask timerTask;
    private Timer timer;
    private Context context;

    //设置灯光颜色
    public static void setLamp(LampColor Color) {
        lampColor = Color;
    }

    /**
     *设置常亮状态，红为Error
     * */
    public static void setStatus(LampStatus Status) {
        lampStatus = Status;
    }

    public static boolean isServiceRunning() {
        return isRuning;
    }

    public enum LampStatus {
        normal,
        error
    }

    public enum LampColor {
        green,
        red
    }

    public LampService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        isRuning = true;
        lampThread = new Thread(new LampRunnable());
        lampThread.start();
        startTimer();

    }


    private void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (lampStatus == LampStatus.normal) {
                    lampColor = LampColor.green;
                } else {
                    lampColor = LampColor.red;
                }
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, times, 6 * times);
    }

    private void stopTimer() {
        timer.cancel();
        timerTask.cancel();
        timerTask = null;
        timer = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private class LampRunnable implements Runnable {
        @Override
        public void run() {
            while (isRuning) {
                switch (lampColor) {
                    case red:
                        //亮红灯，先关闭所有灯光time毫秒，再打开红灯times毫秒
                        ShellUtils.execCommand("echo 0 > /sys/class/backlight/rk28_bl/blue", false);
                        ShellUtils.execCommand("echo 0 > /sys/class/backlight/rk28_bl/red", false);
                        try {
                            Thread.sleep(times);
                        } catch (Exception e) {

                        }
                        ShellUtils.execCommand("echo 1 > /sys/class/backlight/rk28_bl/red", false);
                        try {
                            Thread.sleep(times);
                        } catch (Exception e) {

                        }
                        break;
                    case green:
                        ShellUtils.execCommand("echo 0 > /sys/class/backlight/rk28_bl/blue", false);
                        ShellUtils.execCommand("echo 0 > /sys/class/backlight/rk28_bl/red", false);
                        try {
                            Thread.sleep(times);
                        } catch (Exception e) {

                        }
                        ShellUtils.execCommand("echo 1 > /sys/class/backlight/rk28_bl/blue", false);
                        try {
                            Thread.sleep(times);
                        } catch (Exception e) {

                        }
                        break;
                }


            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRuning = false;
        stopTimer();
        lampThread.interrupt();
    }
}
