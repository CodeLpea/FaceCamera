package cn.com.magnity.coresdksample.ddnwebserver.model;

import com.alibaba.fastjson.annotation.JSONField;
import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;

public class WifiData {


    @JSONField(name = WebConfig.WIFI_NAME)
    private String wifi_name;

    @JSONField(name = WebConfig.WIFI_PASSWD)
    private String wifi_passwd;

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

    @Override
    public String toString() {
        return "WifiData{" +
                "wifi_name='" + wifi_name + '\'' +
                ", wifi_passwd='" + wifi_passwd + '\'' +
                '}';
    }
}

