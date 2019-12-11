package cn.com.magnity.coresdksample.ddnwebserver.model;

import com.alibaba.fastjson.annotation.JSONField;
import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;

/**
 * 语音参数信息
 * */
public class VoiceData {

    @JSONField(name = WebConfig.SYSTEM_VOICE)
    private int system_voice;

    @JSONField(name = WebConfig.ERROR_VOICE)
    private int error_voice;

    @JSONField(name = WebConfig.NORMAL_VOICE)
    private int normal_voice;

    @JSONField(name = WebConfig.VOICE_SPEED)
    private float voice_speed;

    public int getSystem_voice() {
        return system_voice;
    }

    public void setSystem_voice(int system_voice) {
        this.system_voice = system_voice;
    }

    public int getError_voice() {
        return error_voice;
    }

    public void setError_voice(int error_voice) {
        this.error_voice = error_voice;
    }

    public int getNormal_voice() {
        return normal_voice;
    }

    public void setNormal_voice(int normal_voice) {
        this.normal_voice = normal_voice;
    }

    public float getVoice_speed() {
        return voice_speed;
    }

    public void setVoice_speed(float voice_speed) {
        this.voice_speed = voice_speed;
    }

    @Override
    public String toString() {
        return "VoiceData{" +
                "system_voice='" + system_voice + '\'' +
                ", error_voice='" + error_voice + '\'' +
                ", normal_voice='" + normal_voice + '\'' +
                ", voice_speed='" + voice_speed + '\'' +
                '}';
    }
}
