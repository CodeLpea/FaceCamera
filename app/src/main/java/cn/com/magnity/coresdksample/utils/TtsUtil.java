package cn.com.magnity.coresdksample.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.iflytek.thirdparty.E;

import java.util.Locale;

public class TtsUtil {
    private static final String TAG="TtsUtil";
    public TextToSpeech textToSpeech;
    public TtsUtil(Context context) {
       // ShotDownTts();
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == textToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.CHINA);
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
     * */
    public  void SpeechAdd(String text){
        int result;
        result=textToSpeech.speak(text.toString(), TextToSpeech.QUEUE_ADD, null);
        Log.i(TAG, "SpeechAdd: result"+result);
    }
    /**
     * 打断当前语音，直接播放
     * */
    public void SpeechFlush(String text){
        int result;
        result=textToSpeech.speak(text.toString(), TextToSpeech.QUEUE_FLUSH, null);
        Log.i(TAG, "SpeechFlush: ");
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
