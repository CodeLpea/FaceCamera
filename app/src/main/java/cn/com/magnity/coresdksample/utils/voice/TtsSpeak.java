package cn.com.magnity.coresdksample.utils.voice;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.iflytek.thirdparty.E;

import java.util.Locale;

import cn.com.magnity.coresdksample.MyApplication;
import cn.com.magnity.coresdksample.usecache.CurrentConfig;
import cn.com.magnity.coresdksample.websocket.bean.RunningInfo;

public class TtsSpeak {
    private static final String TAG="TtsSpeak";
    private Context context;
    public TextToSpeech textToSpeech;
     //定义AudioManager，控制播放音量
    private AudioManager mgr;
    private int maxVolume;
    private int currentVolume;
    // 单例模式中获取唯一的TtsSpeek实例
    private static TtsSpeak instance;
    public static TtsSpeak getInstance() {
        if (instance == null) {
            instance = new TtsSpeak(MyApplication.getInstance());
        }
        return instance;
    }
    public TtsSpeak(Context context) {
        this.context=context;
        //实例化
        mgr = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
 /*       //最大音量
        maxVolume =mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //当前音量
        currentVolume =mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG, "最大音量:  "+maxVolume);
        Log.i(TAG, "当前音量:  "+currentVolume);*/

        textToSpeech = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == textToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.CHINA);//安卓自带的Pico TTS，并不支持中文。所以需要安装 科大讯飞 tts1.0语音包。需要手动完成。
                    Log.i(TAG, "TtsSpeak:status "+status);
                    Log.i(TAG, "TtsSpeak:result "+result);
                    if (result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e(TAG, "onInit: 不支持当前语言");
                        setVoiceInfo("不支持中文语音包，需要手动安装科大讯飞tts");

                    }
                }else {
                    Log.e(TAG, "onInit: 语音初始化失败");
                    setVoiceInfo("语音初始化失败");
                }
            }
        });
    }

    /**
     *语音模块异常信息传到诊断平台
     * */
    private void setVoiceInfo(String info){
        RunningInfo runningInfo=new RunningInfo();
        runningInfo.setVoiceStatus(info);
        runningInfo.upload();
    }
    /**
     * 排队播放
     * 设置语音大小 0-15
     * */
    public  void SpeechAdd(String text,int volume){
        this.SetVoiceVolume(volume);
        int result;
        result=textToSpeech.speak(text.toString(), TextToSpeech.QUEUE_ADD, null);
       // Log.i(TAG, "SpeechAdd: result"+result);
      //  Log.i(TAG, "GetVoiceVolume:   "+mgr.getStreamVolume(AudioManager.STREAM_MUSIC));
    }
    /**
     * 排队播放系统提示
     * */
    public void SystemSpeech(String text){
        this.SetVoiceVolume(CurrentConfig.getInstance().getCurrentData().getSystem_voice());
        textToSpeech.speak(text.toString(), TextToSpeech.QUEUE_ADD, null);
    }
    /**
     * 设置音量
     * */
    public void SetVoiceVolume(int tempVolume){
            mgr.setStreamVolume(AudioManager.STREAM_MUSIC,tempVolume,0);//tempVolume:音量绝对值
           // Log.i(TAG, "当前音量VoiceVolume:   "+mgr.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    /**
     * 设置语速
     * 1.0位正常语速
     * 线性变换
     * 0.5为一般速度，2为两倍速
     * */
    public void setSpeed(){
        textToSpeech.setSpeechRate(CurrentConfig.getInstance().getCurrentData().getVoice_speed());
    }

    /**
     *
     * 如果当前有播报，则不播报
     * 用于避免重复播报场景
     * 设置语音大小 0-15
     * */
    public void SpeechRepead(String text,int volume){
        this.SetVoiceVolume(volume);
        if(!textToSpeech.isSpeaking()){
            textToSpeech.speak(text.toString(), TextToSpeech.QUEUE_ADD, null);
        }

    }
    /**
     * 判断是否正在播报，如果正在播报，则不进行保存照片的操作
     * */
    public boolean isSpeaking(){

        return textToSpeech.isSpeaking();
    }

    /**
     * 打断当前语音，直接播放
     * 设置语音大小 0-15
     * */
    public void SpeechFlush(String text,int volume){
        this.SetVoiceVolume(volume);
        int result;
        result=textToSpeech.speak(text.toString(), TextToSpeech.QUEUE_FLUSH, null);
       // Log.i(TAG, "SpeechFlush: ");
    }

   /**
    * 释放资源
    * */
   public void ShotDownTts(){
       if(textToSpeech!=null){
           textToSpeech.shutdown();
       }
   }
}
