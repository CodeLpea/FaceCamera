package cn.com.magnity.coresdksample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.iflytek.cloud.FaceDetector;
import com.iflytek.cloud.SpeechUtility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import cn.com.magnity.coresdksample.Service.FtpService;
import cn.com.magnity.coresdksample.Service.ServiceManager;
import cn.com.magnity.coresdksample.surview.CamSurfView;
import cn.com.magnity.coresdksample.surview.MagSurfaceView;
import cn.com.magnity.coresdksample.surview.TrackSurfView;
import cn.com.magnity.coresdksample.ddnwebserver.model.CameraData;

public class MainActivity extends MagBaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private MagSurfaceView mMagSurfaceView;
    //用于显示预览图像
    private CamSurfView mCamSurfView;
    private TrackSurfView mTrackSurfView;
    //讯飞的SDK实现人脸识别
    private FaceDetector mFaceDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: ");

        //设置AppKey用于注册,AppID
        SpeechUtility.createUtility(this, "appid=" + "5833f456");
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //温度摄像头初始化
        initTemp();

        // 人像摄像头初始化
        initPersonCamera();

        startService();

    }

    private void startService() {
        ServiceManager.getInstance().startServices();
    }


    /**
     * 设置摄像头曝光值
     *
     * @param cameraData
     */
    @Subscribe()
    public void setCameraExplore(CameraData cameraData) {
        if (mCamSurfView == null) {
            Log.e(TAG, "mPerCamSurfView==null ");
            return;
        }
        mCamSurfView.setExploreParameters(cameraData);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }


    /**
     * 人像摄像头初始化
     */
    private void initPersonCamera() {

        mTrackSurfView = (TrackSurfView) findViewById(R.id.surf_track);
        mCamSurfView = (CamSurfView) findViewById(R.id.surf_cam);
        //实例化人脸检测对象
        mFaceDetector = FaceDetector.createDetector(this, null);
        mTrackSurfView.setmFaceDetector(mFaceDetector);
        mCamSurfView.setmTrackSurfView(mTrackSurfView);
    }

    /**
     * 温度摄像头初始化
     */
    private void initTemp() {
        mMagSurfaceView = (MagSurfaceView) findViewById(R.id.surf_mag);
        //放入mMagSurfaceView
        super.setMagSurfaceView(mMagSurfaceView);
    }


    @Override
    protected void onDestroy() {

        stopService(new Intent(this, FtpService.class));
        //关闭所有通用服务
        ServiceManager.getInstance().stopServices();

        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        //openCamera();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");

    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart: ");
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
        EventBus.getDefault().unregister(this);
    }


}
