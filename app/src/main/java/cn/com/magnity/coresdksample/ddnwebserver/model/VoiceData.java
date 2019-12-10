package cn.com.magnity.coresdksample.ddnwebserver.model;

import com.alibaba.fastjson.annotation.JSONField;
import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;

/**
 * 语音参数信息
 * */
public class VoiceData {

    @JSONField(name = WebConfig.SYSTEM_VOICE)
    private String system_voice;

    @JSONField(name = WebConfig.ERROR_VOICE)
    private String error_voice;

    @JSONField(name = WebConfig.NORMAL_VOICE)
    private String normal_voice;

    @JSONField(name = WebConfig.VOICE_SPEED)
    private String voice_speed;

    public String getSystem_voice() {
        return system_voice;
    }

    public void setSystem_voice(String system_voice) {
        this.system_voice = system_voice;
    }

    public String getError_voice() {
        return error_voice;
    }

    public void setError_voice(String error_voice) {
        this.error_voice = error_voice;
    }

    public String getNormal_voice() {
        return normal_voice;
    }

    public void setNormal_voice(String normal_voice) {
        this.normal_voice = normal_voice;
    }

    public String getVoice_speed() {
        return voice_speed;
    }

    public void setVoice_speed(String voice_speed) {
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
