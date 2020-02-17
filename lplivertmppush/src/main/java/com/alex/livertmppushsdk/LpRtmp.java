package com.alex.livertmppushsdk;

import android.graphics.ImageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * LpRtmp入口
 */
public class LpRtmp implements ILpRtmp {
    //rtmp算法中的宽高和摄像头是相反的
    private int WIDTH_DEF = 480;
    private int HEIGHT_DEF = 640;
    private final int FRAMERATE_DEF = 20;
    private final int BITRATE_DEF = 800 * 1000;

    private int _iCameraCodecType = android.graphics.ImageFormat.NV21;
    private byte[] _yuvEdit = new byte[WIDTH_DEF * HEIGHT_DEF * 3 / 2];
    private String _rtmpUrl = "rtmp://localhost:1935/live/camera";
    private RtmpSessionManager _rtmpSessionMgr = null;
    private SWVideoEncoder _swEncH264 = null;
    private int degree = 90;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    //用于复制data
    private byte[] buffer = new byte[WIDTH_DEF * HEIGHT_DEF * 2];

    public LpRtmp() {
        _swEncH264 = new SWVideoEncoder(WIDTH_DEF, HEIGHT_DEF, FRAMERATE_DEF, BITRATE_DEF);
        _swEncH264.start(_iCameraCodecType);
        _rtmpSessionMgr = new RtmpSessionManager();
    }

    @Override
    public ILpRtmp setFormat(int imageFormat) {
        this._iCameraCodecType = imageFormat;
        return this;
    }

    @Override
    public ILpRtmp setSizeAndDgree(int mWidth, int mHeight, int degree) {
        this.WIDTH_DEF = mWidth;
        this.HEIGHT_DEF = mHeight;
        this.degree = degree;
        return this;
    }

    @Override
    public void startRtmp(String rtmpUrl) {
        this._rtmpUrl = rtmpUrl;
        _rtmpSessionMgr.Start(_rtmpUrl);

    }

    @Override
    public void stopRtmp() {
        _rtmpSessionMgr.Stop();
    }

    @Override
    public boolean isRtmpConnect() {
        return false;
    }

    @Override
    public void inputData(byte[] datas) {
        System.arraycopy(datas, 0, buffer, 0, datas.length);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                excuteData(buffer);
            }
        });

    }

    /**
     * 执行
     */
    private void excuteData(byte[] datas) {
        byte[] yuv420 = null;
        //判断视频帧编码方式，转化为yuv420
        if (_iCameraCodecType == android.graphics.ImageFormat.YV12) {
            yuv420 = new byte[datas.length];
            _swEncH264.swapYV12toI420_Ex(datas, yuv420, HEIGHT_DEF, WIDTH_DEF);
        } else if (_iCameraCodecType == ImageFormat.NV21) {
            yuv420 = _swEncH264.swapNV21toI420(datas, HEIGHT_DEF, WIDTH_DEF);
        }
        if (yuv420 == null) {
            return;
        }
        //判断是否前置摄像头，决定旋转，90对应后置摄像头
        if (degree == 270) {
            _yuvEdit = _swEncH264.YUV420pRotate270(yuv420, HEIGHT_DEF, WIDTH_DEF);
        } else if (degree == 90) {
            _yuvEdit = _swEncH264.YUV420pRotate90(yuv420, HEIGHT_DEF, WIDTH_DEF);
        }
        byte[] h264Data = _swEncH264.EncoderH264(_yuvEdit);
        if (h264Data != null) {
            _rtmpSessionMgr.InsertVideoData(h264Data);
        }
    }

}
