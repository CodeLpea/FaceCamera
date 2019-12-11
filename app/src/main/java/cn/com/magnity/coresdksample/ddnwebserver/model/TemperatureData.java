package cn.com.magnity.coresdksample.ddnwebserver.model;

import com.alibaba.fastjson.annotation.JSONField;

import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;

/**
 * 温度信息
 * */
public class TemperatureData {

    @JSONField(name = WebConfig.TEMPERATURE_THRESHOLD)
    private String temperature_threshold1;

    public String getTemperature_threshold1() {
        return temperature_threshold1;
    }

    public void setTemperature_threshold1(String temperature_threshold) {
        this.temperature_threshold1 = temperature_threshold;
    }

    @Override
    public String toString() {
        return "TemperatureData{" +
                "temperature_threshold='" + temperature_threshold1 + '\'' +
                '}';
    }
}
