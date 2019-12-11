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
    private int system_voice;

    @JSONField(name = WebConfig.ERROR_VOICE)
    private int error_voice;

    @JSONField(name = WebConfig.NORMAL_VOICE)
    private int normal_voice;

    @JSONField(name = WebConfig.VOICE_SPEED)
    private float voice_speed;

    @JSONField(name = WebConfig.TEMPERATURE_THRESHOLD)
    private float temperature_threshold;

    @JSONField(name = WebConfig.DISTANCE)
    private float distance;

    @JSONField(name = WebConfig.FFC_COMPENSATION_PARAMETER)
    private float FFC_compensation_parameter;

    @JSONField(name = WebConfig.FFC_CALIBRATION_PARAMETER)
    private float FFC_calibration_parameter;

    @JSONField(name = WebConfig.CAMERA_EXPLORE)
    private int camera_explore;


    @JSONField(name = WebConfig.MOVEX)
    private int movex;

    @JSONField(name = WebConfig.MOVEY)
    private int movey;

    @JSONField(name = WebConfig.SCALE)
    private float scale;

    @JSONField(name = WebConfig.LINEUP)
    private int lineUp;

    @JSONField(name = WebConfig.LINELEFT)
    private int lineLeft;

    @JSONField(name = WebConfig.LINEDWON)
    private int lineDown;

    @JSONField(name = WebConfig.LINERIGHT)
    private int lineRight;

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

    public float getTemperature_threshold() {
        return temperature_threshold;
    }

    public void setTemperature_threshold(float temperature_threshold) {
        this.temperature_threshold = temperature_threshold;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getFFC_compensation_parameter() {
        return FFC_compensation_parameter;
    }

    public void setFFC_compensation_parameter(float FFC_compensation_parameter) {
        this.FFC_compensation_parameter = FFC_compensation_parameter;
    }

    public float getFFC_calibration_parameter() {
        return FFC_calibration_parameter;
    }

    public void setFFC_calibration_parameter(float FFC_calibration_parameter) {
        this.FFC_calibration_parameter = FFC_calibration_parameter;
    }

    public int getCamera_explore() {
        return camera_explore;
    }

    public void setCamera_explore(int camera_explore) {
        this.camera_explore = camera_explore;
    }

    public int getMovex() {
        return movex;
    }

    public void setMovex(int movex) {
        this.movex = movex;
    }

    public int getMovey() {
        return movey;
    }

    public void setMovey(int movey) {
        this.movey = movey;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public int getLineUp() {
        return lineUp;
    }

    public void setLineUp(int lineUp) {
        this.lineUp = lineUp;
    }

    public int getLineLeft() {
        return lineLeft;
    }

    public void setLineLeft(int lineLeft) {
        this.lineLeft = lineLeft;
    }

    public int getLineDown() {
        return lineDown;
    }

    public void setLineDown(int lineDown) {
        this.lineDown = lineDown;
    }

    public int getLineRight() {
        return lineRight;
    }

    public void setLineRight(int lineRight) {
        this.lineRight = lineRight;
    }

    @Override
    public String toString() {
        return "CurrentSettingData{" +
                "device_no='" + device_no + '\'' +
                ", version_name='" + version_name + '\'' +
                ", wifi_name='" + wifi_name + '\'' +
                ", wifi_passwd='" + wifi_passwd + '\'' +
                ", system_voice=" + system_voice +
                ", error_voice=" + error_voice +
                ", normal_voice=" + normal_voice +
                ", voice_speed=" + voice_speed +
                ", temperature_threshold=" + temperature_threshold +
                ", distance=" + distance +
                ", FFC_compensation_parameter=" + FFC_compensation_parameter +
                ", FFC_calibration_parameter=" + FFC_calibration_parameter +
                ", camera_explore=" + camera_explore +
                ", movex=" + movex +
                ", movey=" + movey +
                ", scale=" + scale +
                ", lineUp=" + lineUp +
                ", lineLeft=" + lineLeft +
                ", lineDown=" + lineDown +
                ", lineRight=" + lineRight +
                '}';
    }
}
