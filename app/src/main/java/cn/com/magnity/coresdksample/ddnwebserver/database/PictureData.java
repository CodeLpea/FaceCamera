package cn.com.magnity.coresdksample.ddnwebserver.database;


import com.alibaba.fastjson.annotation.JSONField;

import org.litepal.crud.LitePalSupport;

import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;

/**
 * 图片数据
 * 红外，人脸图片路径
 * 人脸框定位参数
 * */
public class PictureData  extends LitePalSupport{

    @JSONField(name = WebConfig.TEMPERPATH)
    private String temperPath;

    @JSONField(name = WebConfig.PERSONPATH)
    private String personPath;

    @JSONField(name = WebConfig.LoactionX1)
    private int x1;

    @JSONField(name = WebConfig.LoactionX2)
    private int x2;

    @JSONField(name = WebConfig.LoactionX3)
    private int x3;

    @JSONField(name = WebConfig.LoactionX4)
    private int x4;

    @JSONField(name = WebConfig.LoactionY1)
    private int y1;

    @JSONField(name = WebConfig.LoactionY2)
    private int y2;

    @JSONField(name = WebConfig.LoactionY3)
    private int y3;

    @JSONField(name = WebConfig.LoactionY4)
    private int y4;

    @JSONField(name = WebConfig.MOVEX)
    private int moveX;

    @JSONField(name = WebConfig.MOVEY)
    private int moveY;

    @JSONField(name = WebConfig.SCALE)
    private float scale;

    public String getTemperPath() {
        return temperPath;
    }

    public void setTemperPath(String temperPath) {
        this.temperPath = temperPath;
    }

    public String getPersonPath() {
        return personPath;
    }

    public void setPersonPath(String personPath) {
        this.personPath = personPath;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getX3() {
        return x3;
    }

    public void setX3(int x3) {
        this.x3 = x3;
    }

    public int getX4() {
        return x4;
    }

    public void setX4(int x4) {
        this.x4 = x4;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public int getY3() {
        return y3;
    }

    public void setY3(int y3) {
        this.y3 = y3;
    }

    public int getY4() {
        return y4;
    }

    public void setY4(int y4) {
        this.y4 = y4;
    }

    public int getMoveX() {
        return moveX;
    }

    public void setMoveX(int moveX) {
        this.moveX = moveX;
    }

    public int getMoveY() {
        return moveY;
    }

    public void setMoveY(int moveY) {
        this.moveY = moveY;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public String toString() {
        return "PictureData{" +
                "temperPath='" + temperPath + '\'' +
                ", personPath='" + personPath + '\'' +
                ", x1=" + x1 +
                ", x2=" + x2 +
                ", x3=" + x3 +
                ", x4=" + x4 +
                ", y1=" + y1 +
                ", y2=" + y2 +
                ", y3=" + y3 +
                ", y4=" + y4 +
                ", moveX=" + moveX +
                ", moveY=" + moveY +
                ", scale=" + scale +
                '}';
    }
}
