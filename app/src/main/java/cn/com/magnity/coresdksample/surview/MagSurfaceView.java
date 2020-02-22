package cn.com.magnity.coresdksample.surview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.alex.livertmppushsdk.LpRtmp;

import java.util.Locale;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import cn.com.magnity.coresdk.MagDevice;
import cn.com.magnity.coresdk.types.CameraInfo;
import cn.com.magnity.coresdk.types.CorrectionPara;
import cn.com.magnity.coresdk.types.StatisticInfo;
import cn.com.magnity.coresdksample.detect.FaceRect;
import cn.com.magnity.coresdksample.detect.FaceRectCollect;

import cn.com.magnity.coresdksample.service.LampService;
import cn.com.magnity.coresdksample.handler.RecordHandler;
import cn.com.magnity.coresdksample.Temp.AreaUtil;
import cn.com.magnity.coresdksample.Temp.TempUtil;
import cn.com.magnity.coresdksample.Temp.FFCUtil;
import cn.com.magnity.coresdksample.usecache.CurrentConfig;
import cn.com.magnity.coresdksample.utils.ImageUtils;
import cn.com.magnity.coresdksample.utils.voice.TtsSpeak;

import static android.content.ContentValues.TAG;

import static cn.com.magnity.coresdksample.Temp.FFCUtil.m_FrameWidth;

import static cn.com.magnity.coresdksample.Config.FFCTemps;
import static cn.com.magnity.coresdksample.Config.TempThreshold;
import static cn.com.magnity.coresdksample.Temp.FFCUtil.m_FrameHeight;

public class MagSurfaceView extends SurfaceView implements SurfaceHolder.Callback, MagDevice.INewFrameCallback {
    private volatile boolean mIsDrawing;
    private DrawThread mDrawThread;

    private volatile boolean mIsSignaled;
    private ReentrantLock mLock;
    private Condition mNewFrameCond;

    private MagDevice mDev;

    /* avoid continuously alloc new memory*/
    private Rect mDstRect;
    private StatisticInfo mStatisticInfo;
    private CameraInfo mCameraInfo;

    private Paint mPaint;
    private PaintFlagsDrawFilter mPfd;
    private int xProportion = 160 / 640;
    private int yProportion = 120 / 480;

    private LpRtmp lpRtmp;
    public MagSurfaceView(Context context) {
        this(context, null);
    }

    public MagSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MagSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        getHolder().addCallback(this);
        this.setZOrderOnTop(true);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1.5f);
        mPaint.setTextSize(15);
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mPaint.setAntiAlias(true);
        //函数是用来对位图进行滤波处理
        mPaint.setFilterBitmap(true);

        /* bilinear */
        mPfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        lpRtmp = new LpRtmp();
        lpRtmp.setSizeAndDgree(120, 160, 0);
        lpRtmp.startRtmp("rtmp://localhost:1935/live/temp");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mPaint = null;
        mPfd = null;
        lpRtmp.stopRtmp();
    }

    public void invalidate_() {
        if (!mIsDrawing) {
            return;
        }

        mLock.lock();
        mIsSignaled = true;
        mNewFrameCond.signal();
        mLock.unlock();
    }


    public void startDrawingThread(MagDevice dev) {

        if (mIsDrawing) {
            return;
        }

        mDrawThread = new DrawThread();
        mLock = new ReentrantLock();
        mNewFrameCond = mLock.newCondition();

        mDev = dev;
        CorrectionPara correctionPara = new CorrectionPara();
        correctionPara.fTaoFilter = (float) 0.85;
        correctionPara.fDistance = CurrentConfig.getInstance().getCurrentData().getDistance();
        mDev.setFixPara(correctionPara);
        mStatisticInfo = new StatisticInfo();
        mCameraInfo = new CameraInfo();
        mDev.getCameraInfo(mCameraInfo);

        Log.i(TAG, "getCorrectionPara: " + mDev.getFixPara(correctionPara));
        Log.i(TAG, "fDistance: " + correctionPara.fDistance);
        // Log.i(TAG, "fTemp: "+correctionPara.fTemp);
        Log.i(TAG, "fTaoFilter: " + correctionPara.fTaoFilter);
        Log.i(TAG, "fTaoFilter: " + correctionPara.fTaoFilter);
        Log.i(TAG, "fDistance: " + correctionPara.fDistance);

        /* 4 : 3 is default, but not always because of rotate */
        if (mCameraInfo.bmpWidth * 3 == mCameraInfo.bmpHeight * 4) {
            mDstRect = new Rect(0, 0, getWidth(), getHeight());//getWidth()，getHeight()
        } else {
            int h = getHeight();//getHeight()
            int w = h * mCameraInfo.bmpWidth / mCameraInfo.bmpHeight;
            int dx = (getWidth() - w) / 2;//getWidth()-w
            mDstRect.set(dx, 0, dx + w, h);
        }

        /* start drawing thread */
        mIsDrawing = true;
        mIsSignaled = false;
        mDrawThread.start();
    }

    public void stopDrawingThread() {
        if (!mIsDrawing) {
            return;
        }


        mIsDrawing = false;

        mLock.lock();
        mIsSignaled = true;
        mNewFrameCond.signal();
        mLock.unlock();

        try {
            mDrawThread.join();
        } catch (InterruptedException ex) {
        } finally {
            mDrawThread = null;
        }

        mDev = null;
    }

    @Override
    public void newFrame(int i, int i1) {
        invalidate_();
    }


    private class DrawThread extends Thread {
        @Override
        public void run() {
            while (mIsDrawing) {
                /* wait for a new frame coming */
                try {
                    mLock.lock();
                    while (!mIsSignaled) {
                        mNewFrameCond.await();
                    }
                    mIsSignaled = false;
                } catch (InterruptedException ex) {
                } finally {
                    mLock.unlock();
                }

                if (!mIsDrawing) {
                    break;
                }
                try {
                    Canvas canvas = getHolder().lockCanvas();
                    if (canvas != null) {
                        canvas.setDrawFilter(mPfd);
                        drawBackground(canvas);
                        showTemperatureDataVideo(canvas, mDstRect);
                        getHolder().unlockCanvasAndPost(canvas);
                    }
                } catch (Exception e) {
                    //捕捉 Exception locking surface异常
                    e.printStackTrace();
                }

            } //while
        }
    }

    void drawBackground(Canvas canvas) {
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        mPaint.setColor(Color.GREEN);
    }

    /**
     * 使用原始数据作为视频流
     */
    private void showTemperatureDataVideo(Canvas canvas, Rect dstRect) {
        int[] temps = new int[160 * 120];
        //获取旋转后的红外矩阵，坐标系为左上角
        temps = TempUtil.getTemps(mDev);
        if (FFCTemps == null) {//如果没有缓存
            Log.i(TAG, "读取本地FFC文件: ");
            FFCTemps = FFCUtil.readFfc();//读取本地
        }

        int[] AfterTemps = new int[temps.length];
        //本地读取到有效的FFC
        if (FFCTemps.length > 10) {
            for (int i = 0; i < AfterTemps.length; i++) {
                //将原始数据通过FFc数据处理
                AfterTemps[i] = temps[i] - FFCTemps[i];
            }
        } else {
            //本地没有读取到有效的FFCTemps，就直接不做FFC处理，采用原始的temps
            AfterTemps = temps;
        }

        int[] maxAndmin = TempUtil.MaxMinTemp(AfterTemps);
        final Bitmap bitmap;
        //转换为图
        bitmap = TempUtil.CovertToBitMap(AfterTemps, maxAndmin[1], maxAndmin[0]);
        //复制图，以便编辑
        Bitmap TempBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        // TODO :将转换为图的数据，进行标注信息，最大温度和人脸框
        Canvas tempCavas = new Canvas(TempBitmap);

        //如果捕捉到人脸
        FaceRect faceRect = FaceRectCollect.getInstance().getFaceRect();
        byte[] nv21 = FaceRectCollect.getInstance().getNv21();
        if (faceRect != null) {
            //转换人脸框，获取指定测温区域
            float[] limitRect = transRect(faceRect);
            //画出预览画面中的人脸框
            drawFaceDetect(tempCavas, limitRect);

            //计算指定区域内最大温度点信息
            int[] maxTemp = getMaxTemp(AfterTemps, limitRect);

            //绘制指定区域内最大温度点
            drawTempPoint(tempCavas, maxTemp);

            //获取规定区域内的温度信息,并判断温度，是否保存
            getRectTemperature(TempBitmap, faceRect, nv21, maxTemp);

            //清除人脸框
            FaceRectCollect.getInstance().clearFaceRect();
        } else {
            int[] maxTemp = getMaxTemp(AfterTemps, null);
            //没有检测到人脸，则标记的是整个画面中的最大温度标记点
            drawTempPoint(tempCavas, maxTemp);
        }

        //绘制图
        canvas.drawBitmap(TempBitmap, null, dstRect, null);
        //图片转NV21
        byte[] bytes = ImageUtils.bitmapToNv21(TempBitmap, TempBitmap.getWidth(), TempBitmap.getHeight());
        //NV21格式RTMP数据发送
        lpRtmp.inputData(bytes);


    }

    /**
     * 获取温度矩阵中，指定区域rect的最大温度值
     * maxTemp[0] = b[i][k];//算出最大值
     * maxTemp[1]=i;//最大值的位置x
     * maxTemp[2]=k;//最大值的位置y
     */
    private int[] getMaxTemp(int[] temps, float[] rect) {
        int[] maxTemp;
        //如果检测到人脸框，则按照人脸框中最大温度来标记
        if (rect != null) {
            int xStart = (int) rect[0];
            int xStop = (int) rect[1];
            int yStrat = (int) rect[2];
            int yStop = (int) rect[3];
            int area[] = AreaUtil.AreaLimit(xStart, xStop, yStrat, yStop);
            //获取指定矩形区域中最大的值
            maxTemp = TempUtil.DDNgetRectTemperatureInfo(temps, area[0], area[1], area[2], area[3]);
        } else {
            //获取指定矩形区域中最大的值
            maxTemp = TempUtil.DDNgetRectTemperatureInfo(temps, 0, m_FrameWidth, 0, m_FrameHeight);
        }
        return maxTemp;
    }

    /**
     * 将人脸框位置，准换到温度位图位置对应区域
     */
    private float[] transRect(FaceRect faceRect) {
        float scale = CurrentConfig.getInstance().getCurrentData().getScale();

        //计算出同比例下的温度框，加上偏移量
        float xStart = ((faceRect.faceRect.left / 4) + CurrentConfig.getInstance().getCurrentData().getMovex());
        float xStop = ((faceRect.faceRect.right / 4) + CurrentConfig.getInstance().getCurrentData().getMovex());
        float yStart = ((faceRect.faceRect.top / 4) + CurrentConfig.getInstance().getCurrentData().getMovey());
        float yStop = ((faceRect.faceRect.bottom / 4) + CurrentConfig.getInstance().getCurrentData().getMovey());

        //计算边长
        float x_len = xStop - xStart;
        float y_len = yStop - yStart;

        //计算中心点
        float x_center = xStart + (x_len) / 2;
        float y_center = yStart + (y_len) / 2;

        //缩放
        xStart = x_center - (x_len * scale) / 2;
        xStop = x_center + (x_len * scale) / 2;
        yStart = y_center - (y_len * scale) / 2;
        yStop = y_center + (y_len * scale) / 2;

        return new float[]{xStart, xStop, yStart, yStop};

    }

    private void drawTempPoint(Canvas tempCanvas, int[] maxTemp) {
        int location_x = maxTemp[1];
        int location_y = maxTemp[2];
        int max = maxTemp[0];

        String sMax = String.format(Locale.ENGLISH, "%.1f", max * 0.001f
                + CurrentConfig.getInstance().getCurrentData().getFFC_compensation_parameter());

        //画交叉标记点
        tempCanvas.drawLine(location_x - 4, location_y, location_x + 4, location_y, mPaint);
        tempCanvas.drawLine(location_x, location_y - 4, location_x, location_y + 4, mPaint);

        Rect rt2 = new Rect();
        mPaint.getTextBounds(sMax, 0, sMax.length(), rt2);
        int cx2 = rt2.width();
        int cy2 = rt2.height();
        final int pad2 = 8;
        location_x += pad2;
        location_y += cy2 + pad2;
        if (location_x > m_FrameWidth - cx2) {
            location_x -= pad2 * 2 + cx2;
        }
        if (location_y > m_FrameHeight) {
            location_y -= pad2 * 2 + cy2 * 2;
        }
        tempCanvas.drawText(sMax, location_x, location_y, mPaint);

    }

    /**
     * 绘制人脸框
     */
    private void drawFaceDetect(Canvas canvas, float[] rect) {

        float xStart = rect[0];
        float xStop = rect[1];
        float yStrat = rect[2];
        float yStop = rect[3];
        //绘制人脸识别框
        //左
        canvas.drawLine(xStart, yStrat, xStart, yStop, mPaint);
        //右
        canvas.drawLine(xStop, yStrat, xStop, yStop, mPaint);
        //上
        canvas.drawLine(xStart, yStrat, xStop, yStrat, mPaint);
        //下
        canvas.drawLine(xStart, yStop, xStop, yStop, mPaint);

    }


    /**
     * public boolean getRectTemperatureInfo(int x0, int y0, int x1, int y1, int[] inf
     * 功能描述：获取指定矩形区域内的温度统计信息，如果正在传输温度数据，从本地获取数据，否则从热像仪端远程获取。
     * 输入参数：
     * x0：矩形左下角坐标，必须小于下x1
     * y0：矩形左下角坐标，必须小于下y1
     * x1：矩形右上角坐标
     * y1：矩形右上角坐标
     * info：返回区域温度统计信息，数组长度应不小于5，依次为[0] - 最低温度; [1] - 最高温度; [2] - 平均温度， [3] - 最低温度位置; [4] - 最高温
     * 度位置。位置计算公式如下：
     * y = pos / w;
     * x = pos - y * w; (w - CameraInfo中的FPAWidth值)
     *
     * @param tempBitmap
     * @param faceRect
     * @param nv21
     */
    private void getRectTemperature(Bitmap tempBitmap, FaceRect faceRect, byte[] nv21, int[] maxTemp) {
        //减去黑体补偿
        float maxTmp = (maxTemp[0] * 0.001f + CurrentConfig.getInstance().getCurrentData().getFFC_compensation_parameter());

        //如果最大温度超过阈值，而且没有语音播报时
        if (maxTmp > TempThreshold && !TtsSpeak.getInstance().isSpeaking()) {

            //超过阈值，让人脸摄像头保存照片
            Bitmap personBitmap = ImageUtils.Nv21ToBitmap(nv21);

            //超过阈值，同一人重新赋值，避免反复保存相同温度照片maxTmp.substring(0,4);
            TempThreshold = maxTmp;
            LampService.setLamp(LampService.LampColor.red);
            String sMax = String.format(Locale.ENGLISH, "%.1f", maxTmp);
            //TtsSpeak.getInstance().SpeechRepead("口腔温度   "+maxTmp2,CurrentConfig.getInstance().getCurrentData().getError_voice());

            //医院测试版本，取消多余播报直接播报温度
            TtsSpeak.getInstance().SpeechRepead(sMax, CurrentConfig.getInstance().getCurrentData().getError_voice());

            //保存记录，温度图片，人像图片，最高温度，人脸框信息
            RecordHandler.getInstance().sendRecord(RecordHandler.MSG_RECODE, tempBitmap, personBitmap, sMax, faceRect);

        } else {
            if (TempThreshold > CurrentConfig.getInstance().getCurrentData().getTemperature_threshold()) {//当前温度阈值与默认温度阈值不同的时播报异常
                //TtsSpeak.getInstance().SpeechRepead("体温异常   ",CurrentConfig.getInstance().getCurrentData().getError_voice());

                LampService.setLamp(LampService.LampColor.red);
            } else {//在没有超过阈值的情况下才会播报异常
                TtsSpeak.getInstance().SpeechRepead("体温正常   ", CurrentConfig.getInstance().getCurrentData().getError_voice());

                LampService.setLamp(LampService.LampColor.green);
            }

        }

    }


}
