package cn.com.magnity.coresdksample.ddnwebserver.model;

import com.alibaba.fastjson.annotation.JSONField;
import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;

/**
 * 温度摄像头参数
 * */
public class TemperCameraData {
    @JSONField(name = WebConfig.DISTANCE)
    private float distance;

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "TemperCameraData{" +
                "distance='" + distance + '\'' +
                '}';
    }
}
