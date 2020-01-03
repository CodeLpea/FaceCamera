package cn.com.magnity.coresdksample.Service.handler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import cn.com.magnity.coresdksample.Temp.FFCUtil;
import cn.com.magnity.coresdksample.Temp.TempUtil;
import cn.com.magnity.coresdksample.usecache.CurrentConfig;
import cn.com.magnity.coresdksample.utils.TimeUitl;

import static cn.com.magnity.coresdksample.Config.XLOG_DIR;
import static cn.com.magnity.coresdksample.MyApplication.mDev;
import static cn.com.magnity.coresdksample.utils.FlieUtil.getFileName;

/**
 * 温度稳定性测试
 * 间隔时间，获取温度数据并保存
 */
public class StabilityTestHandler extends Handler {
    private String TAG = "StabilityTestHandler";
    //基础
    public static final int MSGSTABILITY = 1024;
    public static final int MSGSTABILITY_START = MSGSTABILITY + 1;

    private static class InnerClass {
        public static StabilityTestHandler intance = new StabilityTestHandler();
    }

    /*静态内部类单例*/
    public static StabilityTestHandler getInstance() {
        return InnerClass.intance;
    }

    private StabilityTestHandler() {

    }

    public void startStability() {
        Log.e(TAG, "开始稳定性测试 ");
        sendEmptyMessage(MSGSTABILITY_START);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSGSTABILITY_START:
                removeMessages(MSGSTABILITY_START);
                //开始测试
                Log.i(TAG, "开始测试: ");
//                testStability();
                testareaPoint();
                sendEmptyMessageDelayed(MSGSTABILITY_START, 60 * 1000);
                break;

        }
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

    /**
     * 测试
     */
    private void testStability() {
        //获取测试数据
        int[] testCalibrat = getCameraTemps();
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

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\n\r");
        stringBuffer.append(TimeUitl.getNowDate() + "\n\r");
        stringBuffer.append("TDEV为：    " + String.valueOf(TDEV * 0.001f) + "\n\r");
        stringBuffer.append("最大温度为： " + String.valueOf(max * 0.001f) + "\n\r");
        stringBuffer.append("最小温度为： " + String.valueOf(min * 0.001f) + "\n\r");
        stringBuffer.append("温度极差为： " + String.valueOf(cha * 0.001f) + "\n\r");
        stringBuffer.append("平均温度为： " + String.valueOf(avg * 0.001f) + "\n\r");
        stringBuffer.append("黑体补偿为： " + String.valueOf(CurrentConfig.getInstance().getCurrentData().getFFC_compensation_parameter()) + "\n\r");

        saveLog(stringBuffer.toString());
    }

    /**
     * 远距离测试黑体温度
     * 采用最高温度点的数据保存，时间，坐标，温度
     */
    private void testareaPoint() {
        //获取测试数据
        int[] testCalibrat = getCameraTemps();
        //排序
        ArrayList areaResult = findArea(testCalibrat);
        StringBuffer stringBuffer = new StringBuffer();
        String nowDate = TimeUitl.getNowDate();
        for (int i = 0; i < areaResult.size(); i++) {
            stringBuffer.append(nowDate + areaResult.get(i).toString() + "\n\r");
        }
        saveLog(stringBuffer.toString());
    }

    //找到温度最高的区域，并返回坐标
    private ArrayList findArea(int[] testCalibrat) {
        int max = 0;
        int maxIndex = 0;
        ArrayList result = new ArrayList();
        for (int j = 0; j < testCalibrat.length; j++) {
            //大于20度的都保存
            if (testCalibrat[j] > 20) {
                max = testCalibrat[j];
                maxIndex = j;
                result.add( "-坐标-" + maxIndex + "-温度-" + max);
            }
        }

        return result;
    }

    /**
     * 温度照片
     */

    private void saveLog(String str) {
        String logpath = XLOG_DIR + File.separator + getFileName();
        File file1 = new File(logpath);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        String path = logpath + File.separator + "stability" + TimeUitl.currentDayTime() + ".txt";
        Log.i(TAG, "path: " + path);
        FileWriter file = null;
        try {
            //直接在文中后面追加
            file = new FileWriter(path, true);
            file.write(str);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
