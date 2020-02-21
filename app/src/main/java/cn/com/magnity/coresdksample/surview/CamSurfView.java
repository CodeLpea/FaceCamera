package cn.com.magnity.coresdksample.surview;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.alex.livertmppushsdk.LpRtmp;

import cn.com.magnity.coresdksample.MyApplication;
import cn.com.magnity.coresdksample.Service.LampService;
import cn.com.magnity.coresdksample.Service.handler.DelayDoHandler;
import cn.com.magnity.coresdksample.ddnwebserver.model.CameraData;
import cn.com.magnity.coresdksample.usecache.CurrentConfig;
import cn.com.magnity.coresdksample.websocket.bean.RunningInfo;

/**
 * 用于摄像头预览画面
 * 获取预览byte[]数据
 */
public class CamSurfView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "PerCamSurfView";
    private LpRtmp mLpRtmp;
    private int PREVIEW_WIDTH = 640;
    private int PREVIEW_HEIGHT = 480;

    private TrackSurfView mTrackSurfView;

    private SurfaceHolder holder;
    // 缩放矩阵
    private Matrix mScaleMatrix;
    private Camera mCamera;//摄像头对象
    private byte[] Nv21;

    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;//设置为默认开启后置摄像头

    public CamSurfView(Context context) {
        this(context, null);
    }

    public CamSurfView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CamSurfView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        holder = getHolder();
        holder.addCallback(this);
        mScaleMatrix = new Matrix();
        Nv21 = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
    }

    public void setmTrackSurfView(TrackSurfView mTrackSurfView) {
        this.mTrackSurfView = mTrackSurfView;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated: ");
        mLpRtmp = new LpRtmp();
        mLpRtmp.setSizeAndDgree(480, 640, 90);
        mLpRtmp.startRtmp("rtmp://localhost:1935/live/camera");

        boolean openCamera = openCamera();//打开摄像头
        if (openCamera)
            mCamera.setPreviewCallback(new CamPreCallback());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mScaleMatrix.setScale(width / (float) PREVIEW_HEIGHT, height / (float) PREVIEW_WIDTH);//设置缩放比例

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed: ");
        closeCamera();//关闭摄像头，并释放资源
        mLpRtmp.stopRtmp();

    }

    /**
     * 打开摄像头
     */
    private boolean openCamera() {
        if (mCamera != null) {
            return true;
        }
        try {
            mCamera = Camera.open(mCameraId);
            Toast.makeText(MyApplication.getInstance(), "摄像头已开启", Toast.LENGTH_SHORT).show();
            //获取摄像头参数对象
            Camera.Parameters params = mCamera.getParameters();
            //设置预览的格式
            params.setPreviewFormat(ImageFormat.NV21);
            //设置预览的分辨率，这里设置640*480，到目前为止只支持该分辨率的人脸检测
            params.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
            params.setPictureSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
            params.setRotation(0);
            params.setExposureCompensation(CurrentConfig.getInstance().getCurrentData().getCamera_explore());
            //给摄像头设置参数配置
            mCamera.setParameters(params);
            //设置预览画面旋转角度
            mCamera.setDisplayOrientation(90);
            mCamera.addCallbackBuffer(new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 3 / 2]);

            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();

        } catch (Exception e) {
            e.printStackTrace();
            closeCamera();
            DelayDoHandler.getInstance().sendDelayVoice("人脸摄像头开启失败，请检查", 3 * 1000);

            RunningInfo runningInfo = new RunningInfo();
            runningInfo.setCameraStatus("人脸摄像头开启失败，请检查");
            runningInfo.upload();

            //设置默认的故障灯光
            LampService.setStatus(LampService.LampStatus.error);
            return false;
        }

        return true;

    }

    /**
     * 释放摄像头资源
     */
    private void closeCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void setExploreParameters(CameraData exploreParameters) {
        Camera.Parameters parameters = mCamera.getParameters();
        Log.e(TAG, "ExposureBefore++: " + parameters.getExposureCompensation());
        parameters.setAutoExposureLock(false);
        parameters.setExposureCompensation(exploreParameters.getExplorer());
        mCamera.setParameters(parameters);
        parameters = mCamera.getParameters();
        Log.e(TAG, "ExposureNow: " + parameters.getExposureCompensation());
    }

    private class CamPreCallback implements Camera.PreviewCallback {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            //添加缓存
            camera.addCallbackBuffer(data);
            //复制数据以便处理
            System.arraycopy(data, 0, Nv21, 0, data.length);

            //传递Nv21给人脸识别使用
            mTrackSurfView.setNv21Datas(Nv21);

            //发送到Rtmp服务器
            mLpRtmp.inputData(Nv21);
        }
    }

}
