package cn.com.magnity.coresdksample.Service;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.com.magnity.coresdk.types.CorrectionPara;
import cn.com.magnity.coresdksample.MainActivity;
import cn.com.magnity.coresdksample.Temp.FFCHolder;
import cn.com.magnity.coresdksample.Temp.FFCUtil;
import cn.com.magnity.coresdksample.Temp.SaveTemps;
import cn.com.magnity.coresdksample.Temp.TempUtil;
import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;
import cn.com.magnity.coresdksample.usecache.CurrentConfig;
import cn.com.magnity.coresdksample.utils.PreferencesUtils;
import cn.com.magnity.coresdksample.utils.voice.TtsSpeak;

import static cn.com.magnity.coresdksample.Config.FFCTemps;
import static cn.com.magnity.coresdksample.Config.MSG10;
import static cn.com.magnity.coresdksample.Config.MSG9;
import static cn.com.magnity.coresdksample.Config.ifBlackfFFC;
import static cn.com.magnity.coresdksample.MyApplication.mDev;

/**
 * 温度校准Handler
 * */
public class TempHandler  extends Handler{
    private String TAG="TempHandler";
    public static final int MSGTEMP = 300;//自动连接指定的wifi
    public static final int MSG_1 = MSGTEMP + 1;//ftp语音，包括wifi信息

    private int[] FFCSingleTemps = new int[120 * 160];
    private int[] FFCALLTemps = new int[120 * 160];
    //间隔多少帧数
    public int intervalFrame = 10;
    //计数
    private int nowInterval = 0;
    private static class InnerClass {
        private static TempHandler intance = new TempHandler();
    }
    /*静态内部类单例*/
    public static TempHandler getInstance() {
        return InnerClass.intance;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        switch (msg.what) {
            case MSG9://延时播放温度摄像头语音，FFC校准成功的播报和测试
                String voice9 = msg.obj.toString();
                if (voice9.equals("10秒钟后开始校准FFC")) {
                    TtsSpeak.getInstance().SpeechAdd(voice9, CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                    Log.i(TAG, "10秒钟后开始校准FFC: " + voice9);
                }
                if (voice9.equals("FFC校准成功")) {//校准成功后再保存校准后的数据
                    int[] temps = new int[160 * 120];
                    mDev.lock();
                    mDev.getTemperatureData(temps, true, true);
                    mDev.unlock();

                    int[] readeFfcs = FFCUtil.readFfc();
                    for (int i = 0; i < readeFfcs.length; i++) {
                        temps[i] = temps[i] - readeFfcs[i];
                    }
                    SaveTemps.saveIntTemps(temps, "After");

                    TtsSpeak.getInstance().SpeechAdd(voice9 + "    请重新遮挡温度摄像头，五秒后开始测试FFC效果",CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                    Log.i(TAG, "FFC校准语音播报: " + voice9);

                    Message message = Message.obtain();
                    message.what = MSG9;
                    message.obj = "FFC校准后测试";
                    MainActivity.DelayStartHandler.sendMessageDelayed(message, 15000);
                }
                if (voice9.equals("FFC校准后测试")) {
                    int[] temps = new int[120 * 160];
                    mDev.lock();
                    mDev.getTemperatureData(temps, true, true);
                    mDev.unlock();
                    /*还原CorrectionPara配置*/
                    CorrectionPara correctionPara = new CorrectionPara();
                    Log.i(TAG, "getCorrectionPara: " + mDev.getFixPara(correctionPara));
                    Log.i(TAG, "fDistance: " + correctionPara.fDistance);
                    // Log.i(TAG, "fTemp: "+correctionPara.fTemp);
                    Log.i(TAG, "fTaoFilter: " + correctionPara.fTaoFilter);
                    correctionPara.fTaoFilter = (float) 0.85;
                    correctionPara.fDistance = CurrentConfig.getInstance().getCurrentData().getDistance();
                    mDev.setFixPara(correctionPara);

                    int[] AfterTemps = new int[temps.length];
                    if (FFCTemps.length > 10) {//本地读取到有效的FFC
                        for (int i = 0; i < AfterTemps.length; i++) {
                            AfterTemps[i] = temps[i] - FFCTemps[i];
                        } //将原始数据通过FFc数据处理
                    }
                    int[] maxAndmin = TempUtil.MaxMinTemp(AfterTemps);
                    int cha = maxAndmin[0] - maxAndmin[1];
                    int max = (int) (maxAndmin[0] + CurrentConfig.getInstance().getCurrentData().getFFC_compensation_parameter() * 1000);//统一刻度
                    int min = (int) (maxAndmin[1] + CurrentConfig.getInstance().getCurrentData().getFFC_compensation_parameter()* 1000);
                    int avg = (int) (maxAndmin[2] + CurrentConfig.getInstance().getCurrentData().getFFC_compensation_parameter() * 1000);
                    int TDEV = TempUtil.DDNgetTdevTemperatureInfo(AfterTemps);
                    TtsSpeak.getInstance().SpeechAdd("TDEV为：    " + String.valueOf(TDEV * 0.001f).substring(0, 4), CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                    TtsSpeak.getInstance().SpeechAdd("最大温度为： " + String.valueOf(max * 0.001f).substring(0, 4),CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                    TtsSpeak.getInstance().SpeechAdd("最小温度为： " + String.valueOf(min * 0.001f).substring(0, 4), CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                    TtsSpeak.getInstance().SpeechAdd("温度极差为： " + String.valueOf(cha * 0.001f).substring(0, 4),CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                    TtsSpeak.getInstance().SpeechAdd("平均温度为： " + String.valueOf(avg * 0.001f).substring(0, 4),CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                    TtsSpeak.getInstance().SpeechAdd("黑体补偿为： " + String.valueOf(CurrentConfig.getInstance().getCurrentData().getFFC_compensation_parameter()), CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                    TtsSpeak.getInstance().SpeechAdd("测试结束", CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                }
                break;
            case MSG10://延时播放温度摄像头语音，进行多帧率FFC
                FFCHolder ffcHolder = (FFCHolder) msg.obj;
                String voice10 = ffcHolder.getSpeechString();
                float targetTemp = ffcHolder.getTemp();
                if (voice10.equals("开始校准")) {//只有第一次进入才会播报
                    TtsSpeak.getInstance().SpeechAdd(voice10, CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                    Log.i(TAG, "FFC校准: " + voice10);
                }

                MuiltFFC(targetTemp);//进入多帧率FFC


                break;
        }

    }

    /**
     * 按照平均温度校准FFC
     * 使成像表面温度分布均匀
     * */
    private void avgFFC(){

    }

    /**
     * 多帧率FFC校准
     * @param targetTemp
     */
    private void MuiltFFC(float targetTemp) {
        MainActivity.DelayStartHandler.removeMessages(MSG10);
        nowInterval++;
        FFCSingleTemps = null;
        FFCSingleTemps = new int[120 * 160];
        /*设置距离为0，避免使用时干扰*/
        CorrectionPara correctionPara = new CorrectionPara();
        Log.i(TAG, "getCorrectionPara: " + mDev.getFixPara(correctionPara));
        Log.i(TAG, "fDistance: " + correctionPara.fDistance);
        // Log.i(TAG, "fTemp: "+correctionPara.fTemp);
        Log.i(TAG, "fTaoFilter: " + correctionPara.fTaoFilter);
        correctionPara.fTaoFilter = (float) 0.85;
        correctionPara.fDistance = 0;
        mDev.setFixPara(correctionPara);

        mDev.lock();
        mDev.getTemperatureData(FFCSingleTemps, true, true);
        mDev.unlock();
        for (int i = 0; i < FFCSingleTemps.length; i++) {
            FFCALLTemps[i] = FFCSingleTemps[i] + FFCALLTemps[i];
        }
        if (nowInterval == intervalFrame) {
            int[] origin = new int[120 * 160];
            mDev.lock();
            mDev.getTemperatureData(origin, true, true);
            mDev.unlock();
            nowInterval = 0;
            int[] ffctemps = new int[120 * 160];
            for (int i = 0; i < FFCSingleTemps.length; i++) {
                ffctemps[i] = FFCSingleTemps[i] / intervalFrame;
            }

            if (targetTemp == 1) {//如果目标值为1，则表示使用平均值来校准
                FFCTemps = FFCUtil.getFFC(ffctemps);
            } else {
                int avg = TempUtil.MaxMinTemp(origin)[2];//原始数据的平均值
                FFCTemps = FFCUtil.getFFC(ffctemps);//先得到由平均值补偿后的FFC矩阵。
                float BlackTempCom = targetTemp * 1000 - avg;  //计算黑体补偿,统一单位
              /*  FFCTemps=FFCUtil.getFFC(ffctemps,(int)targetTemp*1000);
                float conmpensation=targetTemp*1000-avg;//*/
//                Config.FFCcompensation = BlackTempCom * 0.001f;
                //黑体补偿等于原始数据平均值减去目标黑体温度。用于之后补偿。
                PreferencesUtils.put(WebConfig.FFC_COMPENSATION_PARAMETER,BlackTempCom * 0.001f);
                CurrentConfig.getInstance().updateSetting();
                ifBlackfFFC = true;//标志了已经进行了FFC黑体校准
            }

            FFCUtil.saveIntFfc(FFCTemps);//保存校准图
            SaveTemps.saveIntTemps(origin, "Origin");
            SaveTemps.saveIntTemps(FFCTemps, "FFC");

            Message message = Message.obtain();
            message.what = MSG9;
            message.obj = "FFC校准成功";

            FFCALLTemps = null;
            FFCALLTemps = new int[120 * 160];//清空总数据，避免下次校准叠加

            MainActivity.DelayStartHandler.sendMessageDelayed(message, 2000);
        } else {
            FFCHolder myHolder = new FFCHolder();
            myHolder.setSpeechString("多帧率FFC");
            myHolder.setTemp(targetTemp);
            Message FFCmessage = Message.obtain();
            FFCmessage.what = MSG10;
            FFCmessage.obj = myHolder;
            MainActivity.DelayStartHandler.sendMessageDelayed(FFCmessage, 100);
        }
    }
}
