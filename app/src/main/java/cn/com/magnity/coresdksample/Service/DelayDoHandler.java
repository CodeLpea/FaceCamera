package cn.com.magnity.coresdksample.Service;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.com.magnity.coresdksample.usecache.CurrentConfig;
import cn.com.magnity.coresdksample.utils.voice.TtsSpeak;

/**
 * 延时操作Handler
 * 主要用于语音信息的延迟
 */
public class DelayDoHandler extends Handler {
    private String TAG = "DelayDoHandler";
    /*延迟Hanlder的MSG*/
    public static final int MSGDELAY1 = 200;//自动连接指定的wifi
    public static final int MSG_VOICE = MSGDELAY1 + 1;//ftp语音，包括wifi信息
    public static final int MSGDELAY3 = MSG_VOICE + 1;//人脸摄像头
    public static final int MSGDELAY4 = MSGDELAY3 + 1;//配置文件检查语音/检测是否温度摄像头是否在线
    public static final int MSGDELAY5 = MSGDELAY4 + 1;//温度摄像头语音
    public static final int MSGDELAY6 = MSGDELAY5 + 1;//反复加载配置服务
    public static final int MSGDELAY7 = MSGDELAY6 + 1;//亮度设置
    public static final int MSGDELAY8 = MSGDELAY7 + 1;//更新播报
    public static final int MSGDELAY9 = MSGDELAY8 + 1;//FFC校准播报
    public static final int MSG10 = MSGDELAY9 + 1;//FFC校准

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
        DelayDoHandler.getInstance().sendMessageDelayed(message, delayTimes);
    }

}
