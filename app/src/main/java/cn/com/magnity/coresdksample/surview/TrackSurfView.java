package cn.com.magnity.coresdksample.surview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.iflytek.cloud.FaceDetector;

import cn.com.magnity.coresdksample.Detect.DrawFaceRect;
import cn.com.magnity.coresdksample.Detect.FaceRect;
import cn.com.magnity.coresdksample.Detect.FaceRectCollect;
import cn.com.magnity.coresdksample.Detect.Result;
import cn.com.magnity.coresdksample.usecache.CurrentConfig;
import cn.com.magnity.coresdksample.utils.TimeUitl;

import static cn.com.magnity.coresdksample.Config.TempThreshold;

/**
 * 绘制人脸框
 */
public class TrackSurfView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "PerCamSurfView";
    private FaceDetector mFaceDetector;
    private int PREVIEW_WIDTH = 640;
    private int PREVIEW_HEIGHT = 480;
    private volatile boolean ifDrwaing = false;
    private byte[] Nv21Datas;

    private String faceResultJson;
    private SurfaceHolder holder;
    // 缩放矩阵
    private Matrix mScaleMatrix;
    private DrawThread mDrawThread;

    public TrackSurfView(Context context) {
        this(context, null);
    }

    public TrackSurfView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrackSurfView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setmFaceDetector(FaceDetector mFaceDetector) {
        this.mFaceDetector = mFaceDetector;
    }

    public void setNv21Datas(byte[] nv21Datas) {
        Nv21Datas = nv21Datas;
    }

    private void init() {
        holder = getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.TRANSPARENT);
        mScaleMatrix = new Matrix();
        this.setZOrderOnTop(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        ifDrwaing = true;
        mDrawThread = new DrawThread();
        mDrawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mScaleMatrix.setScale(width / (float) PREVIEW_HEIGHT, height / (float) PREVIEW_WIDTH);//设置缩放比例
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        ifDrwaing = false;
        try {
            mDrawThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mDrawThread = null;

    }


    private class DrawThread extends Thread {
        @Override
        public void run() {
            while (ifDrwaing) {
                Canvas canvas = holder.lockCanvas();
                if (canvas != null) {
                    DrawRect(canvas);
                    holder.unlockCanvasAndPost(canvas);
                }


            }
        }
    }

    //绘制人脸框
    private void DrawRect(Canvas canvas) {
        if (Nv21Datas == null) {
            return;
        }
        synchronized (Nv21Datas) {
            //i3为原始的旋转角度0,1,2,3,4分别表示0,90,180,270和360度
            faceResultJson = mFaceDetector.trackNV21(Nv21Datas, PREVIEW_WIDTH, PREVIEW_HEIGHT, 1, 1);//获取人脸检测结果
        }
        //获取返回的数据
        FaceRect face = Result.result(faceResultJson);

        //清除之前的绘制图像
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        //适应屏幕大小显示，缩放相对位置
        canvas.setMatrix(mScaleMatrix);

        //没有人脸就返回
        if (face == null) {
            return;
        }

        //绘制有效区域
        DrawFaceRect.DrawScopeDetection(PREVIEW_HEIGHT, PREVIEW_WIDTH, canvas);

        //判断两次检测到人脸的间隔时间，如果超过500ms，则判断为第二个人，就重置温度阈值
        //否则同一个人不会反复拍摄同样温度的照片
        if (TimeUitl.timeInterval(500 * 2)) {
            Log.i(TAG, "检测到不同人脸  ");
            //超过间隔则表示第二个人，则回复默认的阈值
            TempThreshold = CurrentConfig.getInstance().getCurrentData().getTemperature_threshold();
        }
        //旋转人脸框
        face.faceRect = DrawFaceRect.RotateDeg90(face.faceRect, PREVIEW_HEIGHT);
        //绘制脸上关键点
        if (face.facePoints != null) {
            for (int i = 0; i < face.facePoints.length; i++) {
                face.facePoints[i] = DrawFaceRect.RotateDeg90(face.facePoints[i], PREVIEW_HEIGHT);
            }
            //绘制人脸检测的区域
            DrawFaceRect.drawFaceRect(canvas, face, PREVIEW_WIDTH, false);

            //检测是否在有效区域
            if (DrawFaceRect.scopeDetection(face)) {
//                //检测是否张嘴，张嘴才进行问读检测
//                if (DrawFaceRect.MouthDetection(face)) {
//                    //进行温度检测
//                }

                //将人脸框信息缓存，便于红外数据处理时对比使用
                FaceRectCollect.getInstance().setFaceRect(face);
                FaceRectCollect.getInstance().setNv21(Nv21Datas);
            }

        }


    }
}
