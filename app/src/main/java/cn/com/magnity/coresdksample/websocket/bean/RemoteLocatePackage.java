package cn.com.magnity.coresdksample.websocket.bean;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * 用户传输数据
 * 基类消息包
 */

public class RemoteLocatePackage {
    @SerializedName("methodName")
    private String methodName; //指定采集数据类型
    @SerializedName("deviceNo")
    private String deviceNo;   //指定当前机器的设备号
    @SerializedName("system_type")
    private String systemType; //指定采集数据的系统类型, 1表示Android，2表示Linux
    @SerializedName("info")
    private JsonObject playload;   //存放每项采集数据

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public JsonObject getPlayload() {
        return playload;
    }

    public void setPlayload(JsonObject playload) {
        this.playload = playload;
    }
}
