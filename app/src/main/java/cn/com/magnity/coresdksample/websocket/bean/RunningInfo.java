package cn.com.magnity.coresdksample.websocket.bean;


import com.google.gson.annotations.SerializedName;

import cn.com.magnity.coresdksample.websocket.WSConstant;
/**
 *
 * 语音模块信息	voice_status
 摄像头信息	camera_status
 红外摄像头信息	Infrared_camera_status
 配置参数信息	configuration_status
 其他异常信息	error_info
 * */
public class RunningInfo extends AbstractLocatePackage{
    @SerializedName("voice_status")
    private String voiceStatus;
    @SerializedName("camera_status")
    private String cameraStatus;
    @SerializedName("infrared_camera_status")
    private String Infrared_camera_status;
    @SerializedName("configuration_status")
    private String configuration_status;
    @SerializedName("error_info")
    private String error_info;

    public RunningInfo() {
        methodName= WSConstant.RemoteLocationConstant.JUGERUNINFO;
    }

    public String getVoiceStatus() {
        return voiceStatus;
    }

    public void setVoiceStatus(String voiceStatus) {
        this.voiceStatus = voiceStatus;
    }

    public String getCameraStatus() {
        return cameraStatus;
    }

    public void setCameraStatus(String cameraStatus) {
        this.cameraStatus = cameraStatus;
    }

    public String getInfrared_camera_status() {
        return Infrared_camera_status;
    }

    public void setInfrared_camera_status(String infrared_camera_status) {
        Infrared_camera_status = infrared_camera_status;
    }

    public String getConfiguration_status() {
        return configuration_status;
    }

    public void setConfiguration_status(String configuration_status) {
        this.configuration_status = configuration_status;
    }

    public String getError_info() {
        return error_info;
    }

    public void setError_info(String error_info) {
        this.error_info = error_info;
    }

    @Override
    public String toString() {
        return "RunningInfo{" +
                "voiceStatus='" + voiceStatus + '\'' +
                ", cameraStatus='" + cameraStatus + '\'' +
                ", Infrared_camera_status='" + Infrared_camera_status + '\'' +
                ", configuration_status='" + configuration_status + '\'' +
                ", error_info='" + error_info + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
