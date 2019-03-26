package cn.com.magnity.coresdksample;

import android.app.Application;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.magnity.coresdk.MagDevice;
import cn.com.magnity.coresdksample.Detect.FaceRect;
import cn.com.magnity.coresdksample.Detect.JuGeFaceRect;
import cn.com.magnity.coresdksample.View.MagSurfaceView;
import cn.com.magnity.coresdksample.utils.Config;
import cn.com.magnity.coresdksample.utils.ShellUtils;
import cn.com.magnity.coresdksample.utils.TtsUtil;
import cn.com.magnity.coresdksample.utils.lampUtil;

public class MyApplication extends Application {
    private static final String TAG="MyApplication";
    public static  MagDevice mDev;
    public static  boolean isplay=false; //红外播放状态
    public static  boolean isQuest=false; //红外权限状态
    public static  boolean isGetFace=false; //红外权限状态
    public static    int WhereFragmentID=1;//用来标记当前出于哪个Fragment，1为linkFragment，2为LocationFragment；
    public TtsUtil ttsUtil;
    public JuGeFaceRect juGeFaceRect;
    public FaceRect faceRect;
    public MagSurfaceView mView;
    public TtsUtil getTtsUtil() {
        return ttsUtil;
    }
    private static MyApplication myApplication = null;
    public static MyApplication getInstance() {
        return myApplication;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        Log.i(TAG, "onCreate: ");
        mDev =new MagDevice();//初始化全局MagDevice
        juGeFaceRect=new JuGeFaceRect();//初始化全局标记框
        faceRect=new FaceRect();
        ttsUtil=new TtsUtil(this);//初始化全局语音播放工具
        new lampUtil(Config.LAMP,Config.TIME);
    }
}
/*测试用例
   //new lampUtil(Config.LAMP,Config.TIME);
        Timer timer = new Timer(true);
//delay为long,period为long：从现在起过delay毫秒以后，每隔period毫秒执行一次。
        timer.schedule(task1, 10,1000);
//time为Date类型：在指定时间执行一次。
        // timer.schedule(task, time);
//firstTime为Date类型,period为long，表示从firstTime时刻开始，每隔period毫秒执行一次。
        //  timer.schedule(task, firstTime, period);
//delay为long,period为long：从现在起过delay毫秒以后，每隔period毫秒执行一次。
        // timer.schedule(task, delay, period);
    }
    final  int[] count = {1};
    TimerTask task1 = new TimerTask() {
        public void run() {
            Log.i(TAG, "   count[0]++: "+count[0]);
            count[0]++;
            //每次需要执行的代码放到这里面。
                if(count[0] % 4== 0){
                    ShellUtils.execCommand("echo 0 > /sys/class/backlight/rk28_bl/blue", false);
                    ShellUtils.execCommand("echo 0 > /sys/class/backlight/rk28_bl/red", false);
                    ShellUtils.execCommand("echo 1 > /sys/class/backlight/rk28_bl/blue", false);//蓝灯亮
                }else if(count[0] % 4== 1) {
                    ShellUtils.execCommand("echo 0 > /sys/class/backlight/rk28_bl/blue", false);
                    ShellUtils.execCommand("echo 0 > /sys/class/backlight/rk28_bl/red", false);
                }else if(count[0] % 4== 2) {
                    ShellUtils.execCommand("echo 0 > /sys/class/backlight/rk28_bl/blue", false);
                    ShellUtils.execCommand("echo 0 > /sys/class/backlight/rk28_bl/red", false);
                    ShellUtils.execCommand("echo 1 > /sys/class/backlight/rk28_bl/red", false);//红灯亮
                }
                else if(count[0] % 4== 3) {
                    ShellUtils.execCommand("echo 0 > /sys/class/backlight/rk28_bl/blue", false);
                    ShellUtils.execCommand("echo 0 > /sys/class/backlight/rk28_bl/red", false);
                }
           if(count[0]>100){
                count[0]=1;
            }
        }
    };
* */
