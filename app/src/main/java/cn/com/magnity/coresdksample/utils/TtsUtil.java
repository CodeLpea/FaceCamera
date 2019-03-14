package cn.com.magnity.coresdksample.utils;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.iflytek.thirdparty.E;

import java.util.Locale;

public class TtsUtil {
    private static final String TAG="TtsUtil";
    private Context context;
    public TextToSpeech textToSpeech;
     //定义AudioManager，控制播放音量
    private AudioManager mgr;
    private int maxVolume;
    private int currentVolume;
    public TtsUtil(Context context) {
        this.context=context;
        //实例化
        mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
 /*       //最大音量
        maxVolume =mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //当前音量
        currentVolume =mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG, "最大音量:  "+maxVolume);
        Log.i(TAG, "当前音量:  "+currentVolume);*/

        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == textToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.CHINA);//安卓自带的Pico TTS，并不支持中文。所以需要安装 科大讯飞 tts1.0语音包。需要手动完成。
                    Log.i(TAG, "TtsUtil:status "+status);
                    Log.i(TAG, "TtsUtil:result "+result);
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE && result != TextToSpeech.LANG_AVAILABLE){
                    }
                }
            }
        });
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
    public void SetVoiceVolume(int tempVolume){
            mgr.setStreamVolume(AudioManager.STREAM_MUSIC,tempVolume,0);//tempVolume:音量绝对值
           // Log.i(TAG, "当前音量VoiceVolume:   "+mgr.getStreamVolume(AudioManager.STREAM_MUSIC));
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
