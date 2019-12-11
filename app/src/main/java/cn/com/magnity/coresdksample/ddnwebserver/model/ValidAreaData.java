package cn.com.magnity.coresdksample.ddnwebserver.model;

import com.alibaba.fastjson.annotation.JSONField;
import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;

/**
 * 有效区域数据
 * */
public class ValidAreaData {
    @JSONField(name = WebConfig.LINEUP)
    private int lineUp;

    @JSONField(name = WebConfig.LINELEFT)
    private int lineLeft;

    @JSONField(name = WebConfig.LINEDWON)
    private int lineDown;

    @JSONField(name = WebConfig.LINERIGHT)
    private int lineRight;

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
        return "ValidAreaData{" +
                "lineUp='" + lineUp + '\'' +
                ", lineLeft='" + lineLeft + '\'' +
                ", lineDown='" + lineDown + '\'' +
                ", lineRight='" + lineRight + '\'' +
                '}';
    }
}
