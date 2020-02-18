package cn.com.magnity.coresdksample.View;

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

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import cn.com.magnity.coresdk.MagDevice;
import cn.com.magnity.coresdk.types.CameraInfo;
import cn.com.magnity.coresdk.types.CorrectionPara;
import cn.com.magnity.coresdk.types.StatisticInfo;
import cn.com.magnity.coresdksample.MyApplication;
import cn.com.magnity.coresdksample.Service.LampService;
import cn.com.magnity.coresdksample.Service.handler.RecordHandler;
import cn.com.magnity.coresdksample.Service.handler.RecordHolder;
import cn.com.magnity.coresdksample.Temp.AreaUtil;
import cn.com.magnity.coresdksample.Temp.TempUtil;
import cn.com.magnity.coresdksample.Config;
import cn.com.magnity.coresdksample.Temp.FFCUtil;
import cn.com.magnity.coresdksample.usecache.CurrentConfig;
import cn.com.magnity.coresdksample.utils.ImageUtils;
import cn.com.magnity.coresdksample.utils.lampUtil;
import cn.com.magnity.coresdksample.utils.voice.TtsSpeak;

import static android.content.ContentValues.TAG;
import static cn.com.magnity.coresdksample.MyApplication.isGetFace;
import static cn.com.magnity.coresdksample.MyApplication.photoNameSave;
import static cn.com.magnity.coresdksample.Temp.FFCUtil.m_FrameWidth;

import static cn.com.magnity.coresdksample.Config.FFCTemps;
import static cn.com.magnity.coresdksample.Config.TempThreshold;
import static cn.com.magnity.coresdksample.Config.iftaken;
import static cn.com.magnity.coresdksample.Temp.FFCUtil.m_FrameHeight;
import static cn.com.magnity.coresdksample.utils.FlieUtil.getFolderPathToday;

public class MagSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
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
    private Paint SavePhotoPaint;
    private PaintFlagsDrawFilter mPfd;
    private int xProportion=160/640;
    private int yProportion=120/480;

    private  LpRtmp lpRtmp;
    public MagSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(6);
        mPaint.setTextSize(50);
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeCap(Paint.Cap.ROUND);


        SavePhotoPaint=new Paint();
        SavePhotoPaint.setStyle(Paint.Style.FILL);
        SavePhotoPaint.setStrokeWidth(1f);
        SavePhotoPaint.setTextSize(10f);
        SavePhotoPaint.setColor(Color.GREEN);
        SavePhotoPaint.setStrokeCap(Paint.Cap.ROUND);
        /* bilinear */
        mPfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        lpRtmp = new LpRtmp();
        lpRtmp.setSizeAndDgree(120,160,0);
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
        CorrectionPara correctionPara=new CorrectionPara();
        Log.i(TAG, "getCorrectionPara: "+mDev.getFixPara(correctionPara));
        Log.i(TAG, "fDistance: "+correctionPara.fDistance);
       // Log.i(TAG, "fTemp: "+correctionPara.fTemp);
        Log.i(TAG, "fTaoFilter: "+correctionPara.fTaoFilter);
        correctionPara.fTaoFilter=(float)0.85;
        correctionPara.fDistance=CurrentConfig.getInstance().getCurrentData().getDistance();
        mDev.setFixPara(correctionPara);
        Log.i(TAG, "fTaoFilter: "+correctionPara.fTaoFilter);
        Log.i(TAG, "fDistance: "+correctionPara.fDistance);
        mStatisticInfo = new StatisticInfo();

        mCameraInfo = new CameraInfo();
        mDev.getCameraInfo(mCameraInfo);

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

    private void drawImage(Canvas canvas, Rect dstRect, CameraInfo cameraInfo,
                           StatisticInfo info, Paint paint) {

        showTemperatureDataVideo(canvas, dstRect, cameraInfo, paint);
    }

    /**
     * 使用原始数据作为视频流
     * */
    private void showTemperatureDataVideo(Canvas canvas, Rect dstRect, CameraInfo cameraInfo, Paint paint) {
        mDev.lock();
        int[] temps = new int[160*120];
        mDev.getTemperatureData(temps,true,true);
        mDev.unlock();

        if(FFCTemps==null){//如果没有缓存
            Log.i(TAG, "读取本地FFC文件: ");
            FFCTemps= FFCUtil.readFfc();//读取本地
        }

        int []AfterTemps=new int[temps.length];
        //本地读取到有效的FFC
        if(FFCTemps.length>10){
            for(int i=0;i<AfterTemps.length;i++){
                AfterTemps[i]=temps[i]- FFCTemps[i];
            } //将原始数据通过FFc数据处理

        }else {//本地没有读取到有效的FFCTemps，就直接不做FFC处理，采用原始的temps
            AfterTemps=temps;
        }

        int []maxAndmin=TempUtil.MaxMinTemp(AfterTemps);
        final Bitmap bitmap;
        bitmap= TempUtil.CovertToBitMap(AfterTemps,maxAndmin[1],maxAndmin[0]);//转换为图

        Bitmap bitmap1=bitmap.copy(Bitmap.Config.ARGB_8888, true);//复制图，以便编辑

        canvas.drawBitmap(bitmap1, null, dstRect, null);//绘制图


        AfterTemps=TempUtil.ReLoad(AfterTemps);//旋转原始数据，x，y都旋转

        int []maxTemp=TempUtil.DDNgetRectTemperatureInfo(AfterTemps,0,m_FrameWidth,0,m_FrameHeight);//获取指定矩形区域中最大的值

        drawMaxTemp(canvas, dstRect, cameraInfo, maxTemp, paint);
        Bitmap bitmap2=bitmap1.copy(Bitmap.Config.ARGB_8888, true);//复制图，以便编辑
        Canvas rtmpCavas=new Canvas(bitmap2);
//        rtmpCavas.drawText("测试", 80, 80, paint);
        byte[] bytes = ImageUtils.bitmapToNv21(bitmap2, bitmap2.getWidth(), bitmap2.getHeight());
        lpRtmp.inputData(bytes);


        if(isGetFace){//如果捕捉到人脸
            isGetFace=false;
            // Log.i(TAG, "drawImage: 检测到人脸框，准备获取热成像相关温度");
            GetRectTemperature(cameraInfo,AfterTemps,bitmap1);//获取规定区域内的温度信息,并绘制人脸
            drawFaceDetect(canvas,dstRect,cameraInfo,paint);//画出预览画面中的人两款
        }
    }


    /**
 * 绘制人脸宽
 * */
    private void drawFaceDetect(Canvas canvas, Rect dstRect, CameraInfo cameraInfo, Paint paint) {
        paint.setColor(Color.GREEN);//设置画笔的颜色
        int len = (MyApplication.getInstance().faceRect.bound.bottom - MyApplication.getInstance().faceRect.bound.top) / 6;
        if (len / 6 >= 2) {
            paint.setStrokeWidth(len / 6);//设置画笔的粗度
        } else {
            paint.setStrokeWidth(2);//设置画笔的粗度
        }
        int drawL= CurrentConfig.getInstance().getCurrentData().getMovex()+MyApplication.getInstance().juGeFaceRect.getxStart()/4;
        int drawR= CurrentConfig.getInstance().getCurrentData().getMovex()+MyApplication.getInstance().juGeFaceRect.getxStop()/4;
        int drawU=(MyApplication.getInstance().juGeFaceRect.getyStart()/4)+ CurrentConfig.getInstance().getCurrentData().getMovey();//因为坐标原点在左下角，不是左上角，因此y轴是相反的。
        int drawD=(MyApplication.getInstance().juGeFaceRect.getyStop()/4)+ CurrentConfig.getInstance().getCurrentData().getMovey();

        drawL=drawL * dstRect.width() / cameraInfo.fpaWidth + dstRect.left;
        drawR=drawR * dstRect.width() / cameraInfo.fpaWidth + dstRect.left;
        drawD= drawD * dstRect.height() / cameraInfo.fpaHeight + dstRect.top;
        drawU= drawU * dstRect.height() / cameraInfo.fpaHeight + dstRect.top;
        //绘制人脸识别框，每两个一组
        canvas.drawLine(drawL, drawD, drawL, drawD - len, paint);
        canvas.drawLine(drawL, drawD, drawL + len, drawD, paint);
        canvas.drawLine(drawR, drawD, drawR, drawD - len, paint);
        canvas.drawLine(drawR, drawD, drawR - len, drawD, paint);
        canvas.drawLine(drawL, drawU, drawL, drawU + len, paint);
        canvas.drawLine(drawL, drawU, drawL + len, drawU, paint);
        canvas.drawLine(drawR, drawU, drawR, drawU + len, paint);
        canvas.drawLine(drawR, drawU, drawR - len, drawU, paint);
    }

    private void drawMaxTemp(Canvas canvas, Rect dstRect, CameraInfo cameraInfo,
                             int info[], Paint paint) {
        int temp = info[0];

        //get the fpa coordinate
     /*   int yFPA = info.maxPos / cameraInfo.fpaWidth;
        int xFPA = info.maxPos - yFPA * cameraInfo.fpaWidth;*/
        int yFPA=info[2];
        int xFPA=info[1];


      /*  Log.i(TAG, "yFPA: "+yFPA);
        Log.i(TAG, "xFPA: "+xFPA);
        Log.i(TAG, "maxPos: "+info.maxPos);
        Log.i(TAG, "maxPos: "+info.maxPos);
        Log.i(TAG, "cameraInfo.fpaWidth: "+cameraInfo.fpaWidth);
        Log.i(TAG, "maxTemperature: "+temp * 0.001);*/
        //convert to the screen coordinate
        int x = xFPA * dstRect.width() / cameraInfo.fpaWidth + dstRect.left;
        int y = /*dstRect.height() -*/ yFPA * dstRect.height() / cameraInfo.fpaHeight + dstRect.top;
      /*  Log.i(TAG, "x: "+x);
        Log.i(TAG, "y: "+y);*/
       /* Log.i(TAG, "bmp: getWidth "+bmp.getWidth());
        Log.i(TAG, "bmp: getHeight"+bmp.getHeight());*/
        /* text to show */
        String s = String.format(Locale.ENGLISH, "%.1fC", temp * 0.001f+CurrentConfig.getInstance().getCurrentData().getFFC_compensation_parameter());

        /* FIXME: allocate new object in high frequently running function */
        Rect rt = new Rect();
        mPaint.getTextBounds(s, 0, s.length(), rt);

        final int pad = 10;
        final int lineWidth = 8;
        int cx = rt.width();
        int cy = rt.height();

        /* draw cross for max temp point */
        canvas.drawLine(x - lineWidth, y, x + lineWidth, y, paint);
        canvas.drawLine(x, y - lineWidth, x, y + lineWidth, paint);

        /* draw text */
        x += pad;
        y += cy + pad;
        if (x > dstRect.width() - cx) {
            x -= pad * 2 + cx;
        }
        if (y > dstRect.height()) {
            y -= pad * 2 + cy * 2;
        }

        canvas.drawText(s, x, y, paint);


    }

    void drawBackground(Canvas canvas, Paint paint) {
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        paint.setColor(Color.GREEN);
    }


    private class DrawThread extends Thread {
        @Override
        public void run() {
            while(mIsDrawing) {
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
                        drawBackground(canvas, mPaint);
                        drawImage(canvas, mDstRect, mCameraInfo, mStatisticInfo, mPaint);
                        getHolder().unlockCanvasAndPost(canvas);
                    }
                }catch (Exception e){//捕捉 Exception locking surface异常
                    e.printStackTrace();
                }

            } //while
        }
    }


    //获取规定区域内的温度信息
                /**
                 public boolean getRectTemperatureInfo(int x0, int y0, int x1, int y1, int[] inf
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
                 * @param cameraInfo
                 * @param bitmap1*/
             private  void GetRectTemperature(CameraInfo cameraInfo, int[] temps, Bitmap bitmap1){

                  int x0= CurrentConfig.getInstance().getCurrentData().getMovex()+MyApplication.getInstance().juGeFaceRect.getxStart()/4;
                  int x1= CurrentConfig.getInstance().getCurrentData().getMovex()+MyApplication.getInstance().juGeFaceRect.getxStop()/4;
                  int y0=(MyApplication.getInstance().juGeFaceRect.getyStart()/4)+ CurrentConfig.getInstance().getCurrentData().getMovey();
                  int y1=(MyApplication.getInstance().juGeFaceRect.getyStop()/4)+ CurrentConfig.getInstance().getCurrentData().getMovey();
                  int area[]=AreaUtil.AreaLimit(x0,x1,y0,y1);

                  int []maxTemp=TempUtil.DDNgetRectTemperatureInfo(temps,area[0],area[1],area[2],area[3]);//获取指定矩形区域中最大的值

                  float maxTmp= (maxTemp[0]*0.001f+CurrentConfig.getInstance().getCurrentData().getFFC_compensation_parameter());//减去黑体补偿

                      if(maxTmp>TempThreshold&&! TtsSpeak.getInstance().isSpeaking()){
                          TempThreshold=maxTmp;//超过阈值，同一人重新赋值，避免反复保存相同温度照片maxTmp.substring(0,4);
//                          lampUtil.setlamp(2,500,3000);
                          LampService.setLamp(LampService.LampColor.red);
                          String maxTmp2=String.valueOf(TempThreshold);
                          if(String.valueOf(maxTmp).length()>=4){//最多保留4位
                              maxTmp2=maxTmp2.substring(0,4);
                              Log.i(TAG, "maxTmp2: "+maxTmp2);
                          }
                          //TtsSpeak.getInstance().SpeechRepead("口腔温度   "+maxTmp2,CurrentConfig.getInstance().getCurrentData().getError_voice());
                          //医院测试版本，取消多余播报直接播报温度
                          TtsSpeak.getInstance().SpeechRepead(" "+maxTmp2,CurrentConfig.getInstance().getCurrentData().getError_voice());
                         // Log.i(TAG, "maxTmp2222: "+maxTmp2);
                          Canvas saveBmpCanvas=new Canvas(bitmap1);
                          float x2=maxTemp[1];
                          float y2=maxTemp[2];
                          float xStart=x2-4f;
                          float xStop=x2+4f;
                          float yStart=y2-4f;
                          float yStop=y2+4f;
                          saveBmpCanvas.drawLine(xStart, y2, xStop, y2, SavePhotoPaint);
                          saveBmpCanvas.drawLine(x2, yStart, x2, yStop, SavePhotoPaint);
                          Rect rt2 = new Rect();
                          Paint textPaint=SavePhotoPaint;
                          textPaint.getTextBounds(maxTmp2, 0, maxTmp2.length(), rt2);
                          int cx2 = rt2.width();
                          int cy2 = rt2.height();
                          final int pad2 = 6;
                          x2 += pad2;
                          y2 += cy2 + pad2;
                          if (x2 > m_FrameWidth-cx2) {
                              x2 -= pad2 * 2 + cx2;
                          }
                          if (y2 >m_FrameHeight) {
                              y2 -= pad2 * 2 + cy2 * 2;
                          }
                          saveBmpCanvas.drawText(maxTmp2, x2, y2, textPaint);
                          //保存记录
                          RecordHandler.getInstance().sendRecord(RecordHandler.MSG_RECODE_TEMP,bitmap1,TempThreshold,MyApplication.getInstance().juGeFaceRect);
                          iftaken=true;//超过阈值，这先让人脸摄像头拍摄照片
                      }
                      else {
                          if(TempThreshold> CurrentConfig.getInstance().getCurrentData().getTemperature_threshold()){//当前温度阈值与默认温度阈值不同的时播报异常
                              //TtsSpeak.getInstance().SpeechRepead("体温异常   ",CurrentConfig.getInstance().getCurrentData().getError_voice());
//                              lampUtil.setlamp(2,500,3000);
                              LampService.setLamp(LampService.LampColor.red);
                          }
                          else {//在没有超过阈值的情况下才会播报异常
                              TtsSpeak.getInstance().SpeechRepead("体温正常   ", CurrentConfig.getInstance().getCurrentData().getError_voice());
//                              lampUtil.setlamp(1,500,2000);
                              LampService.setLamp(LampService.LampColor.green);
                          }

                      }

                  }






}
