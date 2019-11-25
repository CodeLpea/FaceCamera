package cn.com.magnity.coresdksample.websocket.bean;


import com.google.gson.annotations.SerializedName;

import cn.com.magnity.coresdksample.websocket.WSConstant;
/**
* 硬件名称	HARD_WARE_VERSION
 软件版本	SOFT_VERSION
 安卓版本	ANDROID_VERSION
 启动时间	APP_START_TIME
*
* */

public class SoftWareVersionsInfo extends AbstractLocatePackage {
    @SerializedName("hard_ware_version")
    private String hardWareVersion;//硬件ingc
    @SerializedName("soft_version")
    private String software; //APP软件版本
    @SerializedName("android_version")
    private String system; //系统版本
    @SerializedName("app_start_time")
    private String time; //启动时间
    public SoftWareVersionsInfo(){
        methodName = WSConstant.RemoteLocationConstant.JUGEINFORMATION;
    }

    public String getHardWareVersion() {
        return hardWareVersion;
    }

    public void setHardWareVersion(String hardWareVersion) {
        this.hardWareVersion = hardWareVersion;
    }

    public String getSoftware() {
        return software;
    }

    public void setSoftware(String software) {
        this.software = software;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String kernel) {
        this.time = kernel;
    }

    @Override
    public String toString() {
        return "SoftWareVersionsInfo{" +
                "hardWareVersion='" + hardWareVersion + '\'' +
                ", software='" + software + '\'' +
                ", system='" + system + '\'' +
                ", time='" + time + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
