package cn.com.magnity.coresdksample.Detect;

/**
 * public boolean getRectTemperatureInfo(int x0, int y0, int x1, int y1, int[] inf
 功能描述：获取指定矩形区域内的温度统计信息，如果正在传输温度数据，从本地获取数据，否则从热像仪端远程获取。
 输入参数：
 x0：矩形左下角坐标，必须小于下x1
 y0：矩形左下角坐标，必须小于下y1
 x1：矩形右上角坐标
 y1：矩形右上角坐标
 info：返回区域温度统计信息，数组长度应不小于5，依次为[0] - 最低温度; [1] - 最高温度; [2] - 平均温度， [3] - 最低温度位置; [4] - 最高温
 度位置。位置计算公式如下：
 y = pos / w;
 x = pos - y * w; (w - CameraInfo中的FPAWidth值)
 * */
public class JuGeFaceRect {
    private int xStart;
    private int yStart;
    private int xStop;
    private int yStop;

    public JuGeFaceRect() {
    }

    public int getxStart() {
        return xStart;
    }

    public void setxStart(int xStart) {
        this.xStart = xStart;
    }

    public int getyStart() {
        return yStart;
    }

    public void setyStart(int yStart) {
        this.yStart = yStart;
    }

    public int getxStop() {
        return xStop;
    }

    public void setxStop(int xStop) {
        this.xStop = xStop;
    }

    public int getyStop() {
        return yStop;
    }

    public void setyStop(int yStop) {
        this.yStop = yStop;
    }
}
