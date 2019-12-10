package cn.com.magnity.coresdksample.ddnwebserver.model;

import com.alibaba.fastjson.annotation.JSONField;
import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;

/**
 * 当前设置信息
 * */
public class CurrentSettingData {
    @JSONField(name = WebConfig.DEVICE_NO)
    private String device_no;

    @JSONField(name = WebConfig.VERSION_NAME)
    private String version_name;

    @JSONField(name = WebConfig.WIFI_NAME)
    private String wifi_name;

    @JSONField(name = WebConfig.WIFI_PASSWD)
    private String wifi_passwd;

    @JSONField(name = WebConfig.SYSTEM_VOICE)
    private String system_voice;

    @JSONField(name = WebConfig.ERROR_VOICE)
    private String error_voice;

    @JSONField(name = WebConfig.NORMAL_VOICE)
    private String normal_voice;

    @JSONField(name = WebConfig.VOICE_SPEED)
    private String voice_speed;

    @JSONField(name = WebConfig.TEMPERATURE_THRESHOLD)
    private String temperature_threshold;

    @JSONField(name = WebConfig.DISTANCE)
    private String distance;

    @JSONField(name = WebConfig.FFC_COMPENSATION_PARAMETER)
    private String FFC_compensation_parameter;

    @JSONField(name = WebConfig.FFC_CALIBRATION_PARAMETER)
    private String FFC_calibration_parameter;

    @JSONField(name = WebConfig.CAMERA_EXPLORE)
    private String camera_explore;


    @JSONField(name = WebConfig.MOVEX)
    private String movex;

    @JSONField(name = WebConfig.MOVEY)
    private String movey;

    @JSONField(name = WebConfig.SCALE)
    private float scale;

    @JSONField(name = WebConfig.LINEUP)
    private String lineUp;

    @JSONField(name = WebConfig.LINELEFT)
    private String lineLeft;

    @JSONField(name = WebConfig.LINEDWON)
    private String lineDown;

    @JSONField(name = WebConfig.LINERIGHT)
    private String lineRight;

    public String getLineUp() {
        return lineUp;
    }

    public void setLineUp(String lineUp) {
        this.lineUp = lineUp;
    }

    public String getLineLeft() {
        return lineLeft;
    }

    public void setLineLeft(String lineLeft) {
        this.lineLeft = lineLeft;
    }

    public String getLineDown() {
        return lineDown;
    }

    public void setLineDown(String lineDown) {
        this.lineDown = lineDown;
    }

    public String getLineRight() {
        return lineRight;
    }

    public void setLineRight(String lineRight) {
        this.lineRight = lineRight;
    }

    public String getMovex() {
        return movex;
    }

    public void setMovex(String movex) {
        this.movex = movex;
    }

    public String getMovey() {
        return movey;
    }

    public void setMovey(String movey) {
        this.movey = movey;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getFFC_calibration_parameter() {
        return FFC_calibration_parameter;
    }

    public void setFFC_calibration_parameter(String FFC_calibration_parameter) {
        this.FFC_calibration_parameter = FFC_calibration_parameter;
    }

    public String getDevice_no() {
        return device_no;
    }

    public void setDevice_no(String device_no) {
        this.device_no = device_no;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getWifi_name() {
        return wifi_name;
    }

    public void setWifi_name(String wifi_name) {
        this.wifi_name = wifi_name;
    }

    public String getWifi_passwd() {
        return wifi_passwd;
    }

    public void setWifi_passwd(String wifi_passwd) {
        this.wifi_passwd = wifi_passwd;
    }

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

    public String getTemperature_threshold() {
        return temperature_threshold;
    }

    public void setTemperature_threshold(String temperature_threshold) {
        this.temperature_threshold = temperature_threshold;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getFFC_compensation_parameter() {
        return FFC_compensation_parameter;
    }

    public void setFFC_compensation_parameter(String FFC_compensation_parameter) {
        this.FFC_compensation_parameter = FFC_compensation_parameter;
    }

    public String getCamera_explore() {
        return camera_explore;
    }

    public void setCamera_explore(String camera_explore) {
        this.camera_explore = camera_explore;
    }

    @Override
    public String toString() {
        return "CurrentSettingData{" +
                "device_no='" + device_no + '\'' +
                ", version_name='" + version_name + '\'' +
                ", wifi_name='" + wifi_name + '\'' +
                ", wifi_passwd='" + wifi_passwd + '\'' +
                ", system_voice='" + system_voice + '\'' +
                ", error_voice='" + error_voice + '\'' +
                ", normal_voice='" + normal_voice + '\'' +
                ", voice_speed='" + voice_speed + '\'' +
                ", temperature_threshold='" + temperature_threshold + '\'' +
                ", distance='" + distance + '\'' +
                ", FFC_compensation_parameter='" + FFC_compensation_parameter + '\'' +
                ", FFC_calibration_parameter='" + FFC_calibration_parameter + '\'' +
                ", camera_explore='" + camera_explore + '\'' +
                ", movex='" + movex + '\'' +
                ", movey='" + movey + '\'' +
                ", scale=" + scale +
                ", lineUp='" + lineUp + '\'' +
                ", lineLeft='" + lineLeft + '\'' +
                ", lineDown='" + lineDown + '\'' +
                ", lineRight='" + lineRight + '\'' +
                '}';
    }
}
