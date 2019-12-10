package cn.com.magnity.coresdksample.ddnwebserver.model;


import com.alibaba.fastjson.annotation.JSONField;
import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;

/**
 * 图片数据
 * 红外，人脸图片路径
 * 人脸框定位参数
 * */
public class PictureData {

    @JSONField(name = WebConfig.TEMPERPATH)
    private String temperPath;

    @JSONField(name = WebConfig.PERSONPATH)
    private String personPath;

    @JSONField(name = WebConfig.LoactionX1)
    private String x1;

    @JSONField(name = WebConfig.LoactionX2)
    private String x2;

    @JSONField(name = WebConfig.LoactionX3)
    private String x3;

    @JSONField(name = WebConfig.LoactionX4)
    private String x4;

    @JSONField(name = WebConfig.LoactionY1)
    private String y1;

    @JSONField(name = WebConfig.LoactionY2)
    private String y2;

    @JSONField(name = WebConfig.LoactionY3)
    private String y3;

    @JSONField(name = WebConfig.LoactionY4)
    private String y4;

    @JSONField(name = WebConfig.MOVEX)
    private String moveX;

    @JSONField(name = WebConfig.MOVEY)
    private String moveY;

    @JSONField(name = WebConfig.SCALE)
    private float scale;

    public String getMoveX() {
        return moveX;
    }

    public void setMoveX(String moveX) {
        this.moveX = moveX;
    }

    public String getMoveY() {
        return moveY;
    }

    public void setMoveY(String moveY) {
        this.moveY = moveY;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

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

    public String getX1() {
        return x1;
    }

    public void setX1(String x1) {
        this.x1 = x1;
    }

    public String getX2() {
        return x2;
    }

    public void setX2(String x2) {
        this.x2 = x2;
    }

    public String getX3() {
        return x3;
    }

    public void setX3(String x3) {
        this.x3 = x3;
    }

    public String getX4() {
        return x4;
    }

    public void setX4(String x4) {
        this.x4 = x4;
    }

    public String getY1() {
        return y1;
    }

    public void setY1(String y1) {
        this.y1 = y1;
    }

    public String getY2() {
        return y2;
    }

    public void setY2(String y2) {
        this.y2 = y2;
    }

    public String getY3() {
        return y3;
    }

    public void setY3(String y3) {
        this.y3 = y3;
    }

    public String getY4() {
        return y4;
    }

    public void setY4(String y4) {
        this.y4 = y4;
    }

    @Override
    public String toString() {
        return "PictureData{" +
                "temperPath='" + temperPath + '\'' +
                ", personPath='" + personPath + '\'' +
                ", x1='" + x1 + '\'' +
                ", x2='" + x2 + '\'' +
                ", x3='" + x3 + '\'' +
                ", x4='" + x4 + '\'' +
                ", y1='" + y1 + '\'' +
                ", y2='" + y2 + '\'' +
                ", y3='" + y3 + '\'' +
                ", y4='" + y4 + '\'' +
                ", moveX='" + moveX + '\'' +
                ", moveY='" + moveY + '\'' +
                ", scale=" + scale +
                '}';
    }
}
