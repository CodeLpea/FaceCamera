package cn.com.magnity.coresdksample.Service.handler;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.com.magnity.coresdksample.usecache.CurrentConfig;
import cn.com.magnity.coresdksample.utils.voice.TtsSpeak;
import cn.com.magnity.coresdksample.websocket.bean.RunningInfo;

/**
 * 延时操作Handler
 * 主要用于语音信息的延迟
 */
public class DelayDoHandler extends Handler {
    private String TAG = "DelayDoHandler";
    /*延迟Hanlder的MSG*/
    public static final int MSGDELAY1 = 200;//自动连接指定的wifi
    public static final int MSG_VOICE = MSGDELAY1 + 1;//ftp语音，包括wifi信息
    public static final int MSG_STARTBOOT = MSG_VOICE + 1;//FFC校准

    private static class InnerClass {
        public static DelayDoHandler intance = new DelayDoHandler();
    }

    /*静态内部类单例*/
    public static DelayDoHandler getInstance() {
        return InnerClass.intance;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSG_VOICE:
                String voiceInfo = msg.obj.toString();
                Log.e(TAG, "voiceInfo: " + voiceInfo);
                Log.i(TAG, "voice: " + CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                TtsSpeak.getInstance().SystemSpeech(voiceInfo);
                break;
            case MSG_STARTBOOT:
                Log.i(TAG, "延时启动模块: ");
                RunningInfo runningInfo=(RunningInfo) msg.obj;
                runningInfo.upload();
                break;
        }
    }

    /**
     * 发送延迟语音消息
     *
     * @param voice
     * @param delayTimes
     */
    public void sendDelayVoice(String voice, long delayTimes) {
        Message message = obtainMessage();
        message.what = MSG_VOICE;
        message.obj = voice;
        sendMessageDelayed(message, delayTimes);
    }

    /**
     * 发送延迟语音消息
     *
     * @param delayTimes
     */
    public void sendDelayStart(Object obj ,long delayTimes) {
        Message message = obtainMessage();
        message.what = MSG_STARTBOOT;
        message.obj = obj;
        sendMessageDelayed(message, delayTimes);
    }

}
