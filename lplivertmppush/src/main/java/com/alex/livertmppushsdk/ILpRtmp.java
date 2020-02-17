package com.alex.livertmppushsdk;

/**
 * Rtmp数据推流入口
 * */
public interface ILpRtmp {
    void startRtmp(String rtmpUrl);
    void stopRtmp();
    boolean isRtmpConnect();
    //放入数据
    void inputData(byte[] datas);
    //设置数据类型
    ILpRtmp setFormat(int imageFormat);
    //设置数据尺寸和方向
    ILpRtmp setSizeAndDgree(int mWidth, int mHeight, int degree);

}
