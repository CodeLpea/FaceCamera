package cn.com.magnity.coresdksample.ddnwebserver.model;

import com.alibaba.fastjson.annotation.JSONField;

import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;

/**
 * 温度信息
 * */
public class TemperatureData {
    @JSONField(name = WebConfig.TEMPERATURE_THRESHOLD)
    private String Temperature;

    public String getTemperature() {
        return Temperature;
    }

    public void setTemperature(String temperature) {
        Temperature = temperature;
    }

    @Override
    public String toString() {
        return "TemperatureData{" +
                "Temperature='" + Temperature + '\'' +
                '}';
    }
}
