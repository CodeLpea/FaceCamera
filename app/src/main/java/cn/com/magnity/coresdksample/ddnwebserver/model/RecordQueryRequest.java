package cn.com.magnity.coresdksample.ddnwebserver.model;

import com.alibaba.fastjson.annotation.JSONField;
import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;

import java.util.Date;

/**
 * 条件查询请求数据
 * */
public class RecordQueryRequest {

    //当前多少页
    @JSONField(name = WebConfig.CURRENTPAGE)
    private int currentpage;

    //每页多少条
    @JSONField(name = WebConfig.EVERPAGENUMBER)
    private int everPageNumber;

    //过滤条件
    @JSONField(name = WebConfig.STARTIME)
    private Date starTime;
    @JSONField(name = WebConfig.ENDTIME)
    private Date endTime;
    @JSONField(name = WebConfig.MINTEMP)
    private String minTemp;
    @JSONField(name = WebConfig.MATEMP)
    private String maxTemp;

    //排序条件
    @JSONField(name = WebConfig.ORDERS)
    private String orders;

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }

    public int getCurrentpage() {
        return currentpage;
    }

    public void setCurrentpage(int currentpage) {
        this.currentpage = currentpage;
    }

    public int getEverPageNumber() {
        return everPageNumber;
    }

    public void setEverPageNumber(int everPageNumber) {
        this.everPageNumber = everPageNumber;
    }

    public Date getStarTime() {
        return starTime;
    }

    public void setStarTime(Date starTime) {
        this.starTime = starTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    @Override
    public String toString() {
        return "RecordQueryRequest{" +
                "currentpage=" + currentpage +
                ", everPageNumber=" + everPageNumber +
                ", starTime=" + starTime +
                ", endTime=" + endTime +
                ", minTemp='" + minTemp + '\'' +
                ", maxTemp='" + maxTemp + '\'' +
                ", orders='" + orders + '\'' +
                '}';
    }
}
