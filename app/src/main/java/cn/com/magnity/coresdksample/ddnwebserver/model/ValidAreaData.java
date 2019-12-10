package cn.com.magnity.coresdksample.ddnwebserver.model;

import com.alibaba.fastjson.annotation.JSONField;
import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;

/**
 * 有效区域数据
 * */
public class ValidAreaData {
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

    @Override
    public String toString() {
        return "ValidAreaData{" +
                "lineUp='" + lineUp + '\'' +
                ", lineLeft='" + lineLeft + '\'' +
                ", lineDown='" + lineDown + '\'' +
                ", lineRight='" + lineRight + '\'' +
                '}';
    }
}
