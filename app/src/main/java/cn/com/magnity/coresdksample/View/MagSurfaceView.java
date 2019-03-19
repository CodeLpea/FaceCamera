package cn.com.magnity.coresdksample.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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
import cn.com.magnity.coresdksample.Detect.JuGeFaceRect;
import cn.com.magnity.coresdksample.MyApplication;
import cn.com.magnity.coresdksample.utils.Config;
import cn.com.magnity.coresdksample.utils.lampUtil;

import static android.content.ContentValues.TAG;
import static cn.com.magnity.coresdksample.MyApplication.isGetFace;
import static cn.com.magnity.coresdksample.utils.Config.DefaultTempThreshold;
import static cn.com.magnity.coresdksample.utils.Config.SavaRootDirName;
import static cn.com.magnity.coresdksample.utils.Config.SavaTestDirName;
import static cn.com.magnity.coresdksample.utils.Config.TempThreshold;
import static cn.com.magnity.coresdksample.utils.Config.iftaken;

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
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mPaint = null;
        mPfd = null;
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
        Log.i(TAG, "fTemp: "+correctionPara.fTemp);
        Log.i(TAG, "fTaoFilter: "+correctionPara.fTaoFilter);
        correctionPara.fTaoFilter=(float)0.85;
        mDev.setFixPara(correctionPara);
        Log.i(TAG, "fTaoFilter: "+correctionPara.fTaoFilter);
        mStatisticInfo = new StatisticInfo();

        mCameraInfo = new CameraInfo();
        mDev.getCameraInfo(mCameraInfo);

        /* 4 : 3 is default, but not always because of rotate */
        if (mCameraInfo.bmpWidth * 3 == mCameraInfo.bmpHeight * 4) {
            mDstRect = new Rect(0, 0, getWidth(), getHeight());
        } else {
            int h = getHeight();
            int w = h * mCameraInfo.bmpWidth / mCameraInfo.bmpHeight;
            int dx = (getWidth() - w) / 2;
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
    Bitmap bmp;
    private void drawImage(Canvas canvas, Rect dstRect, CameraInfo cameraInfo,
                           StatisticInfo info, Paint paint) {


        mDev.lock();
        bmp = mDev.getOutputImage();
        mDev.getFrameStatisticInfo(info);
       // Log.i(TAG, "指定区域的温度: "+  mDev.getTemperatureProbe(5,1));
        mDev.getCameraInfo(cameraInfo);
        mDev.unlock();

        if (bmp != null) {
            canvas.drawBitmap(bmp, null, dstRect, null);
            drawMaxTemp(canvas, dstRect, cameraInfo, info, paint);
            if(isGetFace){//如果捕捉到人脸
                isGetFace=false;
               // Log.i(TAG, "drawImage: 检测到人脸框，准备获取热成像相关温度");
                GetRectTemperature(cameraInfo);//获取规定区域内的温度信息,并绘制人脸
                drawFaceDetect(canvas,dstRect,cameraInfo,paint);//画出预览画面中的人两款
            }
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
        int drawL=Config.XPalce+MyApplication.getInstance().juGeFaceRect.getxStart()/4;;
        int drawR=Config.XPalce+MyApplication.getInstance().juGeFaceRect.getxStop()/4;
        int drawU=120-(MyApplication.getInstance().juGeFaceRect.getyStart()/4)+Config.YPalce;//因为坐标原点在左下角，不是左上角，因此y轴是相反的。
        int drawD=120-(MyApplication.getInstance().juGeFaceRect.getyStop()/4)+Config.YPalce;
     /*   int drawL = MyApplication.getInstance().faceRect.bound.left/4-len;
        int drawR = MyApplication.getInstance().faceRect.bound.right/4+len;
        int drawD = MyApplication.getInstance().faceRect.bound.top/4-len;
        int drawU = MyApplication.getInstance().faceRect.bound.bottom/4+len;*/
        drawL = drawL * dstRect.width() / cameraInfo.fpaWidth + dstRect.left;
        drawR = drawR * dstRect.width() / cameraInfo.fpaWidth + dstRect.left;
        drawD= dstRect.height() - drawD * dstRect.height() / cameraInfo.fpaHeight + dstRect.top;
        drawU= dstRect.height() - drawU * dstRect.height() / cameraInfo.fpaHeight + dstRect.top;
        //绘制人脸识别框，每两个一组
        canvas.drawLine(drawL, drawD, drawL, drawD - len, paint);
        canvas.drawLine(drawL, drawD, drawL + len, drawD, paint);
        canvas.drawLine(drawR, drawD, drawR, drawD - len, paint);
        canvas.drawLine(drawR, drawD, drawR - len, drawD, paint);
        canvas.drawLine(drawL, drawU, drawL, drawU + len, paint);
        canvas.drawLine(drawL, drawU, drawL + len, drawU, paint);
        canvas.drawLine(drawR, drawU, drawR, drawU + len, paint);
        canvas.drawLine(drawR, drawU, drawR - len, drawU, paint);
    /*    *//**
         * 绘制人脸监测点
         *//*
        if (MyApplication.getInstance().faceRect.point != null) {
            //遍历检测点，并绘制
            for (Point p : MyApplication.getInstance().faceRect.point) {
                   *//* p.y = getHeight() - p.y;*//*
                    canvas.drawPoint(p.x, p.y, paint);
            }
        }*/
    }

    private void drawMaxTemp(Canvas canvas, Rect dstRect, CameraInfo cameraInfo,
                             StatisticInfo info, Paint paint) {
        int temp = info.maxTemperature;

        //get the fpa coordinate
        int yFPA = info.maxPos / cameraInfo.fpaWidth;
        int xFPA = info.maxPos - yFPA * cameraInfo.fpaWidth;
      /*  Log.i(TAG, "yFPA: "+yFPA);
        Log.i(TAG, "xFPA: "+xFPA);
        Log.i(TAG, "maxPos: "+info.maxPos);
        Log.i(TAG, "maxPos: "+info.maxPos);
        Log.i(TAG, "cameraInfo.fpaWidth: "+cameraInfo.fpaWidth);
        Log.i(TAG, "maxTemperature: "+temp * 0.001);*/
        //convert to the screen coordinate
        int x = xFPA * dstRect.width() / cameraInfo.fpaWidth + dstRect.left;
        int y = dstRect.height() - yFPA * dstRect.height() / cameraInfo.fpaHeight + dstRect.top;
      /*  Log.i(TAG, "x: "+x);
        Log.i(TAG, "y: "+y);*/
       /* Log.i(TAG, "bmp: getWidth "+bmp.getWidth());
        Log.i(TAG, "bmp: getHeight"+bmp.getHeight());*/
        /* text to show */
        String s = String.format(Locale.ENGLISH, "%.1fC", temp * 0.001f);

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
     /*   if(isGetFace){//检测到人脸就判断是否超过阈值
            if(temp * 0.001f>TempThreshold){//超过默认阈值温度，就标记出来并保存。而且检测到人脸
                iftaken=true;//超过阈值，这先让人脸摄像头拍摄照片
                TempThreshold=temp * 0.001f;//超过阈值，同一人重新赋值，避免反复保存相同温度照片maxTmp.substring(0,4);
                String maxTmp=String.valueOf(TempThreshold);
                MyApplication.getInstance().ttsUtil.SpeechRepead("体温异常   "+maxTmp.substring(0,4), Config.heightTempVoiceVolume);
           *//* Log.i(TAG, "xFPA: "+xFPA);
            Log.i(TAG, "yFPA: "+yFPA);
            Log.i(TAG, "info.maxPos: "+info.maxPos);*//*
                Canvas saveBmpCanvas=new Canvas(bmp);
                float x2=xFPA;
                float y2=120-yFPA;
                float xStart=x2-4f;
                float xStop=x2+4f;
                float yStart=y2-4f;
                float yStop=y2+4f;
                saveBmpCanvas.drawLine(xStart, y2, xStop, y2, SavePhotoPaint);
                saveBmpCanvas.drawLine(x2, yStart, x2, yStop, SavePhotoPaint);
                *//* draw text *//*
                Rect rt2 = new Rect();
                Paint textPaint=SavePhotoPaint;
                textPaint.getTextBounds(s, 0, s.length(), rt2);
                int cx2 = rt2.width();
                int cy2 = rt2.height();
                final int pad2 = 6;
                x2 += pad2;
                y2 += cy2 + pad2;
                if (x2 > 160-cx2) {
                    x2 -= pad2 * 2 + cx2;
                }
                if (y2 >120) {
                    y2 -= pad2 * 2 + cy2 * 2;
                }
                saveBmpCanvas.drawText(s, x2, y2, textPaint);
                saveBitmap(bmp);
                Log.i(TAG, "saveBitmap: ");
            }else {
                if(TempThreshold!=DefaultTempThreshold){//当前温度阈值与默认温度阈值不同的时播报异常
                    MyApplication.getInstance().ttsUtil.SpeechRepead("体温异常   ", Config.heightTempVoiceVolume);
                }
                else {//在没有超过阈值的情况下才会播报异常
                    MyApplication.getInstance().ttsUtil.SpeechRepead("体温正常   ", Config.normolTempVoiceVolume);
                }


            }
        }*/

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
    /**
     * 保存图片到SD卡上
     */
   protected void saveBitmap(Bitmap baseBitmap) {
        try {
                   // 保存图片到SD卡上
            SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
            String formatStr =formatter.format(new Date());
            File file = new File(Environment.getExternalStorageDirectory(),
                    SavaRootDirName+File.separator+String.valueOf(TempThreshold)+"_"+formatStr+  "Temp.png");
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream stream = new FileOutputStream(file);
            baseBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
              } catch (Exception e) {
              e.printStackTrace();
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
                 * @param cameraInfo*/
             private  void GetRectTemperature(CameraInfo cameraInfo){
                  int x0=Config.XPalce+MyApplication.getInstance().juGeFaceRect.getxStart()/4;
                  int x1=Config.XPalce+MyApplication.getInstance().juGeFaceRect.getxStop()/4;
                  int y1=120-(MyApplication.getInstance().juGeFaceRect.getyStart()/4)+Config.YPalce;//因为坐标原点在左下角，不是左上角，因此y轴是相反的。
                  int y0=120-(MyApplication.getInstance().juGeFaceRect.getyStop()/4)+Config.YPalce;
                  if(x0<0){
                      x0=0;
                  }
                 if(x1<0){
                     x1=0;
                 }
                 if(y0<0){
                     y0=0;
                 }if(y1<0){
                     y1=0;
                 }
                  int[] inf=new int[5];
                  mDev.getRectTemperatureInfo(x0,y0,x1,y1,inf);//获取规定区域内的温度信息
                  /*Log.i(TAG, "GetRectTemperature:最高温度 "+inf[1]*0.001f);
                  Log.i(TAG, "GetRectTemperature:最高温度位置 "+inf[4]);*/
                  int maxTmp= (int) (inf[1]*0.001f);
                 int YFPA = inf[4] / cameraInfo.fpaWidth;
                 int XFPA = inf[4] - YFPA * cameraInfo.fpaWidth;


                      if(inf[1]*0.001f>TempThreshold){
                          iftaken=true;//超过阈值，这先让人脸摄像头拍摄照片
                          TempThreshold=inf[1]*0.001f;//超过阈值，同一人重新赋值，避免反复保存相同温度照片maxTmp.substring(0,4);
                          String maxTmp2=String.valueOf(TempThreshold);
                          if(String.valueOf(inf[1]*0.001f).length()>=4){//最多保留4位
                              maxTmp2=maxTmp2.substring(0,4);
                          }
                          MyApplication.getInstance().ttsUtil.SpeechRepead("体温异常   "+maxTmp2, Config.heightTempVoiceVolume);
                          lampUtil.setlamp(2,500,3000);
                          Canvas saveBmpCanvas=new Canvas(bmp);
                          float x2=XFPA;
                          float y2=120-YFPA;
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
                          if (x2 > 160-cx2) {
                              x2 -= pad2 * 2 + cx2;
                          }
                          if (y2 >120) {
                              y2 -= pad2 * 2 + cy2 * 2;
                          }
                          saveBmpCanvas.drawText(maxTmp2, x2, y2, textPaint);
                          saveBitmap(bmp);
                          Log.i(TAG, "saveBitmap: ");

                      }
                      else {
                          if(TempThreshold!=DefaultTempThreshold){//当前温度阈值与默认温度阈值不同的时播报异常
                              MyApplication.getInstance().ttsUtil.SpeechRepead("体温异常   ", Config.heightTempVoiceVolume);
                              lampUtil.setlamp(2,500,3000);
                          }
                          else {//在没有超过阈值的情况下才会播报异常
                              MyApplication.getInstance().ttsUtil.SpeechRepead("体温正常   ", Config.normolTempVoiceVolume);
                              lampUtil.setlamp(1,500,2000);
                          }

                      }

                  }





}
