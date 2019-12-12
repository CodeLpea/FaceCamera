package cn.com.magnity.coresdksample.Service.handler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.com.magnity.coresdk.types.CorrectionPara;
import cn.com.magnity.coresdksample.Temp.FFCUtil;
import cn.com.magnity.coresdksample.Temp.SaveTemps;
import cn.com.magnity.coresdksample.Temp.TempUtil;
import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;
import cn.com.magnity.coresdksample.usecache.CurrentConfig;
import cn.com.magnity.coresdksample.utils.PreferencesUtils;
import cn.com.magnity.coresdksample.utils.voice.TtsSpeak;

import static cn.com.magnity.coresdksample.Config.FFCTemps;
import static cn.com.magnity.coresdksample.MyApplication.mDev;

/**
 * 温度校准Handler
 */
public class TempHandler extends Handler {
    private String TAG = "TempHandler";
    //基础
    public static final int MSGTEMP = 300;
    //FFC平均/黑体校准，入口
    public static final int MSG_IN = MSGTEMP + 1;
    //校准过程
    public static final int MSG_DO = MSG_IN + 1;
    //校准完成
    public static final int MSG_OVER = MSG_DO + 1;
    public static final int MSG_OVER_TEST = MSG_OVER + 1;

    //接收到命令的标志
    private static boolean runSign = false;

    private float calibrationValue = 0;

    //单张温度图片图片的矩阵值
    private int[] FFCSingleTemps = new int[120 * 160];
    //多张温度图片的矩阵值的和
    private int[] FFCALLTemps = new int[120 * 160];
    //间隔多少帧数
    private static final int INTERVALFRAME = 10;
    //计数
    private int nowInterval = 0;

    private static class InnerClass {
        public static TempHandler intance = new TempHandler();
    }

    /*静态内部类单例*/
    public static TempHandler getInstance() {
        return InnerClass.intance;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        switch (msg.what) {
            case MSG_IN:
                //温度校准标志
                if (runSign == true) {
                    //如果标志已经为true，则正在运行中，不需要重复校准
                    Log.e(TAG, "温度校准运行中，不需要重复校准: ");
                    TtsSpeak.getInstance().SystemSpeech("温度校准运行中");
                    return;
                }
                //修改运行标志
                runSign=true;
                Log.e(TAG, "开始温度校准: ");
                //开始之前，重置所有需要用到的变量。
                resetAllVaria();
                float agvValues = Float.valueOf(msg.obj.toString());
                //十五秒后开始执行平均温度校准,通过校准值来区黑体校准或者平均校准。
                sendTemperMessge(MSG_DO, agvValues, 20 * 1000);
                TtsSpeak.getInstance().SystemSpeech("开始温度校准， 十五秒后开始校准");
                break;
            case MSG_DO:
                //执行多征率温度校准的循环
                calibrationValue = Float.valueOf(msg.obj.toString());
                //多帧率校准
                MuiltFFC(calibrationValue);
                break;

            case MSG_OVER:
                //校准完成
                Log.e(TAG, "校准完成: ");
                //完成校准后保存一次测试记录
                afterCalibratSave();
                //语音播报，提醒遮盖测试
                TtsSpeak.getInstance().SystemSpeech(" 校准完成  请均匀遮挡温度摄像头，五秒后开始测试FFC效果");
                //实际10秒后开始测试效果
                sendTemperMessge(MSG_OVER_TEST, 0, 10 * 1000);
                break;
            case MSG_OVER_TEST:
                Log.e(TAG, "测试校准效果: ");
                //测试效果
                testCalibrat();
                //还原标志，方便下次校准
                runSign=false;
                break;

        }

    }

    /**
     * 测试一下校准效果
     */
    private void testCalibrat() {
        //获取测试数据
        int[] testCalibrat = getCameraTemps();
        //获取测试数据之后，还原目标距离
        setTempCorrect(CurrentConfig.getInstance().getCurrentData().getDistance());
        //获得校准后的数据
        int[] calibratFFC = getcalibratFFC(testCalibrat);
        //获得校准后的数据的最大，最小，平均值
        int[] maxAndmin = TempUtil.MaxMinTemp(calibratFFC);
        //获取每个点的温度与平均值的绝对值的和的平均值。
        int TDEV = TempUtil.DDNgetTdevTemperatureInfo(calibratFFC);

        int cha = maxAndmin[0] - maxAndmin[1];
        int max = (int) (maxAndmin[0] + CurrentConfig.getInstance().getCurrentData().getFFC_compensation_parameter() * 1000);//统一刻度
        int min = (int) (maxAndmin[1] + CurrentConfig.getInstance().getCurrentData().getFFC_compensation_parameter() * 1000);
        int avg = (int) (maxAndmin[2] + CurrentConfig.getInstance().getCurrentData().getFFC_compensation_parameter() * 1000);

        TtsSpeak.getInstance().SystemSpeech("TDEV为：    " + String.valueOf(TDEV * 0.001f).substring(0, 4));
        TtsSpeak.getInstance().SystemSpeech("最大温度为： " + String.valueOf(max * 0.001f).substring(0, 4));
        TtsSpeak.getInstance().SystemSpeech("最小温度为： " + String.valueOf(min * 0.001f).substring(0, 4));
        TtsSpeak.getInstance().SystemSpeech("温度极差为： " + String.valueOf(cha * 0.001f).substring(0, 4));
        TtsSpeak.getInstance().SystemSpeech("平均温度为： " + String.valueOf(avg * 0.001f).substring(0, 4));
        TtsSpeak.getInstance().SystemSpeech("黑体补偿为： " + String.valueOf(CurrentConfig.getInstance().getCurrentData().getFFC_compensation_parameter()));
        TtsSpeak.getInstance().SystemSpeech("测试结束");
    }

    /**
     * 温度校准结束之后
     * 保存一次测试记录
     */
    private void afterCalibratSave() {
        //获得最新一帧，然后补偿出数据
        int[] afterCalibrat = getcalibratFFC(getCameraTemps());
        //保存测试矩阵，方便查看效果
        SaveTemps.saveIntTemps(afterCalibrat, SaveTemps.SaveType.AFTER);
    }

    /**
     * 简化发送信息
     *
     * @param MSG
     * @param calibrationValue
     * @param times
     */
    public  void sendTemperMessge(int MSG, float calibrationValue, long times) {
        Message message = this.obtainMessage();
        message.what = MSG;
        message.obj = calibrationValue;
        //times毫秒之后开始进行校准,传递校准的参数。
        this.sendMessageDelayed(message, times);
    }

    /**
     * 设置温度摄像头相关参数
     * 使校准更加准确
     * 透光率 默认0.85
     * 距离
     *
     * @param distance
     */
    private void setTempCorrect(float distance) {
        CorrectionPara correctionPara = new CorrectionPara();
        Log.i(TAG, "getCorrectionPara: " + mDev.getFixPara(correctionPara));
        Log.i(TAG, "fDistance: " + correctionPara.fDistance);
        // Log.i(TAG, "fTemp: "+correctionPara.fTemp);
        Log.i(TAG, "fTaoFilter: " + correctionPara.fTaoFilter);
        correctionPara.fTaoFilter = (float) 0.85;
        correctionPara.fDistance = distance;
        mDev.setFixPara(correctionPara);
    }

    /**
     * 获取温度摄像头当前预览的矩阵
     *
     * @return int[]
     */
    private int[] getCameraTemps() {
        int[] temps = new int[120 * 160];
        mDev.lock();
        mDev.getTemperatureData(temps, true, true);
        mDev.unlock();
        return temps;
    }


    /**
     * 重置所有变量
     */
    private void resetAllVaria() {
        nowInterval = 0;
        calibrationValue = 0;
        FFCSingleTemps = null;
        FFCALLTemps = null;
        FFCALLTemps = new int[120 * 160];
        FFCSingleTemps = new int[120 * 160];
    }

    /**
     * 多帧率FFC校准
     * 输入校准的温度
     * 0 平均校准
     * others 黑体校准
     *
     * @param targetTemp
     */
    private void MuiltFFC(float targetTemp) {
        Log.i(TAG, "温度校准Step：  " + nowInterval);
        removeMessages(MSG_DO);
        //自增
        nowInterval++;

        /*设置距离为0，避免使用时干扰*/
        setTempCorrect(0);

        //获取当前温度矩阵
        FFCSingleTemps = getCameraTemps();

        //将每个点累加起来，用于最终结果中求每个点的平均
        for (int i = 0; i < FFCSingleTemps.length; i++) {
            FFCALLTemps[i] = FFCSingleTemps[i] + FFCALLTemps[i];
        }
        //如果还没有计算到指定帧率10
        if (nowInterval != INTERVALFRAME) {
            //就等待100ms继续发送
            sendTemperMessge(MSG_DO, targetTemp, 100);
            return;
        }
        //当到达指定帧数的时候
        Log.i(TAG, "到达指定帧数: nowInterval  " + nowInterval);

        //求出每个点的平均值 ，得到总帧率下的单帧值
        for (int i = 0; i < FFCALLTemps.length; i++) {
            FFCSingleTemps[i] = FFCALLTemps[i] / nowInterval;
        }
        //单帧值的每个点，与平均温度的补偿，就是FFC矩阵
        FFCTemps = FFCUtil.getFFC(FFCSingleTemps);

        //将每个点的校准数组FFCTemps保存到本地
        saveFFCtoLoacl(FFCSingleTemps, FFCTemps);

        //如果目标值为不0，则还需要根据黑体温度更新补偿值
        if (targetTemp != 0) {
            Log.i(TAG, "黑体温度校准，更新补偿值: ");
            //获取当前原始温度数据
//            int[] origin = getCameraTemps();
            int avg = TempUtil.MaxMinTemp(FFCSingleTemps)[2];//原始数据的平均值
            float BlackTempCom = targetTemp * 1000 - avg;  //计算黑体补偿,统一单位
            //黑体补偿等于原始数据平均值减去目标黑体温度。用于之后补偿。
            PreferencesUtils.put(WebConfig.FFC_COMPENSATION_PARAMETER, BlackTempCom * 0.001f);
            //更新补偿值
            CurrentConfig.getInstance().updateSetting();
        }

        //发送结束标志
        sendTemperMessge(MSG_OVER, 0, 1000);
    }

    /**
     * 将ffc数据保存到本地
     */
    private void saveFFCtoLoacl(int[] origin, int[] ffc) {
        //保存校准图，方便使用时，从本地读取
        Log.i(TAG, "保存校准图: ");
        FFCUtil.saveIntFfc(ffc);
        SaveTemps.saveIntTemps(origin, SaveTemps.SaveType.ORIGIN);
        SaveTemps.saveIntTemps(ffc, SaveTemps.SaveType.FFC);

    }

    /**
     * 将原始温度数据
     * 经过FFC校准返回
     *
     * @param originTemps
     */
    private int[] getcalibratFFC(int[] originTemps) {
        //从本地读取刚刚保存下来的校准矩阵
        int[] calibratFFC = FFCUtil.readFfc();
        //补偿矩阵信息
        if (calibratFFC == null) {
            Log.e(TAG, "getcalibratFFC: 没有在本地找到");
            return originTemps;
        }
        for (int i = 0; i < calibratFFC.length; i++) {
            originTemps[i] = originTemps[i] - calibratFFC[i];
        }
        return originTemps;
    }
}
