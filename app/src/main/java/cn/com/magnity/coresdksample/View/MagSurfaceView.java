package cn.com.magnity.coresdksample.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.iflytek.thirdparty.E;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import cn.com.magnity.coresdk.MagDevice;
import cn.com.magnity.coresdk.types.CameraInfo;
import cn.com.magnity.coresdk.types.StatisticInfo;

import static android.content.ContentValues.TAG;
import static cn.com.magnity.coresdksample.utils.Config.SavaTestDirName;

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


        }
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
        Log.i(TAG, "x: "+x);
        Log.i(TAG, "y: "+y);
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
        if(temp * 0.001f>30){//超过30度，就标记出来并保存。
            Canvas saveBmpCanvas=new Canvas(bmp);
            float x2=xFPA;
            float y2=120-yFPA;
            float xStart=x2-4f;
            float xStop=x2+4f;
            float yStart=y2-4f;
            float yStop=y2+4f;
            saveBmpCanvas.drawLine(xStart, y2, xStop, y2, SavePhotoPaint);
            saveBmpCanvas.drawLine(x2, yStart, x2, yStop, SavePhotoPaint);
            /* draw text */
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
        }
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
     114      * 保存图片到SD卡上
     115      */
   protected void saveBitmap(Bitmap baseBitmap) {
        try {
                   // 保存图片到SD卡上
            File file = new File(Environment.getExternalStorageDirectory(),
                    SavaTestDirName+File.separator+System.currentTimeMillis()+ ".png");
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
}
