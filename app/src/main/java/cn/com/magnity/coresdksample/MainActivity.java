package cn.com.magnity.coresdksample;

import android.Manifest;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.iflytek.cloud.FaceDetector;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.util.Accelerometer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.com.magnity.coresdk.MagDevice;
import cn.com.magnity.coresdk.types.CorrectionPara;
import cn.com.magnity.coresdk.types.EnumInfo;
import cn.com.magnity.coresdksample.Detect.DrawFaceRect;
import cn.com.magnity.coresdksample.Detect.FaceRect;
import cn.com.magnity.coresdksample.Detect.Result;
import cn.com.magnity.coresdksample.Service.DelayDoHandler;
import cn.com.magnity.coresdksample.Service.FtpService;
import cn.com.magnity.coresdksample.Service.LoadService;
import cn.com.magnity.coresdksample.Service.ServiceManager;
import cn.com.magnity.coresdksample.Temp.FFCHolder;
import cn.com.magnity.coresdksample.Temp.TempUtil;
import cn.com.magnity.coresdksample.View.QiuView;
import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;
import cn.com.magnity.coresdksample.usecache.CurrentConfig;
import cn.com.magnity.coresdksample.utils.EthernetUtil;
import cn.com.magnity.coresdksample.Temp.FFCUtil;
import cn.com.magnity.coresdksample.utils.FlieUtil;
import cn.com.magnity.coresdksample.Temp.SaveTemps;
import cn.com.magnity.coresdksample.utils.PreferencesUtils;
import cn.com.magnity.coresdksample.utils.Screenutil;
import cn.com.magnity.coresdksample.utils.TimeUitl;
import cn.com.magnity.coresdksample.utils.Utils;
import cn.com.magnity.coresdksample.utils.WifiAdmin;
import cn.com.magnity.coresdksample.utils.WifiUtil;
import cn.com.magnity.coresdksample.utils.lampUtil;
import cn.com.magnity.coresdksample.utils.voice.TtsSpeak;
import cn.com.magnity.coresdksample.websocket.bean.RunningInfo;


import static cn.com.magnity.coresdksample.MyApplication.WhereFragmentID;
//import static cn.com.magnity.coresdksample.MyApplication.isplay;
import static cn.com.magnity.coresdksample.MyApplication.isGetFace;
import static cn.com.magnity.coresdksample.MyApplication.mDev;
import static cn.com.magnity.coresdksample.MyApplication.photoNameSave2;
import static cn.com.magnity.coresdksample.Config.FFCTemps;
import static cn.com.magnity.coresdksample.Config.InitLoadServieAction;
import static cn.com.magnity.coresdksample.Config.MSG10;
import static cn.com.magnity.coresdksample.Config.MSG2;
import static cn.com.magnity.coresdksample.Config.MSG3;
import static cn.com.magnity.coresdksample.Config.MSG4;
import static cn.com.magnity.coresdksample.Config.MSG5;
import static cn.com.magnity.coresdksample.Config.MSG6;
import static cn.com.magnity.coresdksample.Config.MSG7;
import static cn.com.magnity.coresdksample.Config.MSG8;
import static cn.com.magnity.coresdksample.Config.MSG9;
import static cn.com.magnity.coresdksample.Config.ReLoadServieAction;
import static cn.com.magnity.coresdksample.Config.TempThreshold;
import static cn.com.magnity.coresdksample.Config.ifBlackfFFC;
import static cn.com.magnity.coresdksample.Config.iftaken;
import static cn.com.magnity.coresdksample.utils.FlieUtil.getFolderPathToday;
import static cn.com.magnity.coresdksample.utils.Screenutil.setCameraDisplayOrientation;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    //const
    private static final int START_TIMER_ID = 0;
    private static final int TIMER_INTERVAL = 500;//ms

    private static final String STATUS_ARGS = "status";
    private static final String USBID_ARGS = "usbid";

    //non-const
    //  private MagDevice mDev;

    private ArrayList<EnumInfo> mDevices;
    private ArrayList<String> mDeviceStrings;
    private Handler mEnumHandler;
    private Handler mRestoreHandler;
    private Runnable mRestoreRunnable;
    private VideoFragment mVideoFragment;
    private SurfaceView mPreviewSurface;//用于显示预览图像
    private SurfaceView mFaceSurface;//用于绘制检测人脸的返回信息
    private SurfaceHolder mSurfaceHolder;//纹理控制器
    private Camera mCamera;//摄像头对象
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;//设置为默认开启后置摄像头
    // 默认设置640*480,截至目前也只是支持640*480
    private int PREVIEW_WIDTH = 640;
    private int PREVIEW_HEIGHT = 480;
    // 预览帧数据存储数组和缓存数组
    private byte[] nv21;
    private byte[] buffer;
    // 缩放矩阵
    private Matrix mScaleMatrix = new Matrix();
    // 加速度感应器，用于获取手机的朝向
    private Accelerometer accelerometer;
    private FaceDetector mFaceDetector;//调用讯飞的SDK来实现人脸识别
    private int degrees;//旋转角度
    /*    private boolean istaken;//拍照状态按钮*/
    private boolean stop;//人脸检测开关
    private FragmentTransaction transaction;//定义用于加载连接和设置界面
    private LinkFragment linkFragment;
    private LoactionFragment loactionFragment;
    private AreaFragment areaFragment;
    //校准相关控件
    private TextView Tvlocation1, Tvlocation2, Tvpoint1, Tvpoint2;
    private QiuView QiuView1, QiuView2;
    private Button BtLocate, BtLink, BtArea;
    //wifi管理
    WifiAdmin wifiAdmin;

    private String NoewIp = "0.0.0.0";
    public static Handler DelayStartHandler;
    public static Handler ReloadServiceHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_vertical);
        Log.i(TAG, "onCreate: ");
        SpeechUtility.createUtility(this, "appid=" + "5833f456"); //设置AppKey用于注册,AppID
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        //初始化控件
        initView();

        //初始化Fragment
        initFragment();

        //连接指定wifi
//        wifiScan();

        //初始化延迟Handler
        delayStart();
        //温度摄像头初始化
        initJuge(savedInstanceState);

        // 人像摄像头初始化
        initPersonCamera();

        //检查文件夹
      /*  FlieUtil.initFile(SavaRootDirName);//初始化文件夹
        FlieUtil.initFile(SavaTestDirName);//初始化文件夹*/
        FlieUtil.initFile(getFolderPathToday());//初始化文件夹

        //启动加载数据的服务（包括声音配置，wifi配置，亮度配置等）
        initLoadService();


        //初始化ftp
        // initFtp();
        startService();

    }
    private void startService() {
        ServiceManager.getInstance().startServices();
    }
    /**
     * 启动加载数据的服务（包括声音配置，wifi配置，亮度配置等）
     * 反复加载配置文件
     */
    private void initLoadService() {
        Log.i(TAG, "initLoadService: 开启加载数据服务");
        LoadService loadService = new LoadService();
        Intent toLoadService = new Intent(this, loadService.getClass());
        toLoadService.setAction(InitLoadServieAction);
        startService(toLoadService);
        ReloadServiceHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG6://反复加载配置服务
                        ReloadServiceHandler.removeMessages(MSG6);
                        Intent intent = (Intent) msg.obj;
                        intent.setAction(ReLoadServieAction);
                        startService(intent);
                        Message message = Message.obtain();
                        message.what = MSG6;
                        message.obj = msg.obj;
                        ReloadServiceHandler.sendMessageDelayed(message, 1000 * 30);//每隔三十秒加载一次
                        break;
                    case MSG7:
                        if (mCamera != null) {
                            Camera.Parameters parameters = mCamera.getParameters();
                            Log.e(TAG, "ExposureBefore++: " + parameters.getExposureCompensation());
                            parameters.setAutoExposureLock(false);
                            parameters.setExposureCompensation(CurrentConfig.getInstance().getCurrentData().getCamera_explore());
                            mCamera.setParameters(parameters);
                            parameters = mCamera.getParameters();
                            Log.e(TAG, "ExposureNow: " + parameters.getExposureCompensation());
                        }
                        break;
                }

            }
        };
        Message message = Message.obtain();
        message.what = MSG6;
        message.obj = toLoadService;
        ReloadServiceHandler.sendMessageDelayed(message, 1000 * 30);//每隔三十秒加载一次

    }


    /**
     * 延时启动ftp wifi扫描等功能
     * 以便tts语音已经被初始化
     */
    private void delayStart() {
        DelayStartHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG9://延时播放温度摄像头语音，FFC校准成功的播报和测试
                        String voice9 = msg.obj.toString();
                        if (voice9.equals("10秒钟后开始校准FFC")) {
                            TtsSpeak.getInstance().SpeechAdd(voice9, CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                            Log.i(TAG, "10秒钟后开始校准FFC: " + voice9);
                        }
                        if (voice9.equals("FFC校准成功")) {//校准成功后再保存校准后的数据
                            int[] temps = new int[160 * 120];
                            mDev.lock();
                            mDev.getTemperatureData(temps, true, true);
                            mDev.unlock();

                            int[] readeFfcs = FFCUtil.readFfc();
                            for (int i = 0; i < readeFfcs.length; i++) {
                                temps[i] = temps[i] - readeFfcs[i];
                            }
                            SaveTemps.saveIntTemps(temps, "After");

                            TtsSpeak.getInstance().SpeechAdd(voice9 + "    请重新遮挡温度摄像头，五秒后开始测试FFC效果",CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                            Log.i(TAG, "FFC校准语音播报: " + voice9);

                            Message message = Message.obtain();
                            message.what = MSG9;
                            message.obj = "FFC校准后测试";
                            MainActivity.DelayStartHandler.sendMessageDelayed(message, 15000);
                        }
                        if (voice9.equals("FFC校准后测试")) {
                            int[] temps = new int[120 * 160];
                            mDev.lock();
                            mDev.getTemperatureData(temps, true, true);
                            mDev.unlock();
                            /*还原CorrectionPara配置*/
                            CorrectionPara correctionPara = new CorrectionPara();
                            Log.i(TAG, "getCorrectionPara: " + mDev.getFixPara(correctionPara));
                            Log.i(TAG, "fDistance: " + correctionPara.fDistance);
                            // Log.i(TAG, "fTemp: "+correctionPara.fTemp);
                            Log.i(TAG, "fTaoFilter: " + correctionPara.fTaoFilter);
                            correctionPara.fTaoFilter = (float) 0.85;
                            correctionPara.fDistance = CurrentConfig.getInstance().getCurrentData().getDistance();
                            mDev.setFixPara(correctionPara);

                            int[] AfterTemps = new int[temps.length];
                            if (FFCTemps.length > 10) {//本地读取到有效的FFC
                                for (int i = 0; i < AfterTemps.length; i++) {
                                    AfterTemps[i] = temps[i] - FFCTemps[i];
                                } //将原始数据通过FFc数据处理
                            }
                            int[] maxAndmin = TempUtil.MaxMinTemp(AfterTemps);
                            int cha = maxAndmin[0] - maxAndmin[1];
                            int max = (int) (maxAndmin[0] + CurrentConfig.getInstance().getCurrentData().getFFC_compensation_parameter() * 1000);//统一刻度
                            int min = (int) (maxAndmin[1] + CurrentConfig.getInstance().getCurrentData().getFFC_compensation_parameter()* 1000);
                            int avg = (int) (maxAndmin[2] + CurrentConfig.getInstance().getCurrentData().getFFC_compensation_parameter() * 1000);
                            int TDEV = TempUtil.DDNgetTdevTemperatureInfo(AfterTemps);
                            TtsSpeak.getInstance().SpeechAdd("TDEV为：    " + String.valueOf(TDEV * 0.001f).substring(0, 4), CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                            TtsSpeak.getInstance().SpeechAdd("最大温度为： " + String.valueOf(max * 0.001f).substring(0, 4),CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                            TtsSpeak.getInstance().SpeechAdd("最小温度为： " + String.valueOf(min * 0.001f).substring(0, 4), CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                            TtsSpeak.getInstance().SpeechAdd("温度极差为： " + String.valueOf(cha * 0.001f).substring(0, 4),CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                            TtsSpeak.getInstance().SpeechAdd("平均温度为： " + String.valueOf(avg * 0.001f).substring(0, 4),CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                            TtsSpeak.getInstance().SpeechAdd("黑体补偿为： " + String.valueOf(CurrentConfig.getInstance().getCurrentData().getFFC_compensation_parameter()), CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                            TtsSpeak.getInstance().SpeechAdd("测试结束", CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                        }
                        break;
                    case MSG10://延时播放温度摄像头语音，进行多帧率FFC
                        FFCHolder ffcHolder = (FFCHolder) msg.obj;
                        String voice10 = ffcHolder.getSpeechString();
                        float targetTemp = ffcHolder.getTemp();
                        if (voice10.equals("开始校准")) {//只有第一次进入才会播报
                            TtsSpeak.getInstance().SpeechAdd(voice10, CurrentConfig.getInstance().getCurrentData().getSystem_voice());
                            Log.i(TAG, "FFC校准: " + voice10);
                        }

                        MuiltFFC(targetTemp);//进入多帧率FFC


                        break;
                }
            }
        };
        //DelayStartHandler.sendEmptyMessageDelayed(MSG2,5000);
    }



    /**
     * initView
     */
    private void initView() {
        Tvlocation1 = (TextView) findViewById(R.id.tv_location1);
        Tvlocation2 = (TextView) findViewById(R.id.tv_location2);
        Tvpoint1 = (TextView) findViewById(R.id.tv_point1);
        Tvpoint2 = (TextView) findViewById(R.id.tv_point2);
        QiuView1 = (QiuView) findViewById(R.id.view1);
        QiuView2 = (QiuView) findViewById(R.id.view2);
        BtLink = (Button) findViewById(R.id.bt_linkSet);
        BtLocate = (Button) findViewById(R.id.bt_locateSet);
        BtArea = (Button) findViewById(R.id.bt_areaSet);
        BtLink.setOnClickListener(this);
        BtLocate.setOnClickListener(this);
        BtArea.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_linkSet://连接设置跳转到LINKFragemnt
                // MyApplication.getInstance().getTtsSpeak().SpeechAdd("连接设置跳转到LINKFragemnt",Config.currtentVoiceVolume);
                transaction = getFragmentManager().beginTransaction();
                //初始化transaction
                transaction.hide(loactionFragment);
                transaction.hide(areaFragment);
                transaction.show(linkFragment);//连接LinkFragment隐藏locationFragment
                WhereFragmentID = 1;
                showLocationView(false);//隐藏LocationFragment中需要用到的mainActivity的控件
                transaction.commit();
                break;
            case R.id.bt_locateSet://连接设置跳转到LocationFragemnt
                //  MyApplication.getInstance().getTtsSpeak().SpeechAdd("连接设置跳转到LINKFragemnt",Config.currtentVoiceVolume);
                transaction = getFragmentManager().beginTransaction();
                //初始化transaction
                transaction.hide(linkFragment);
                transaction.hide(areaFragment);
                transaction.show(loactionFragment);//默认进入locationFragment隐藏LinkFragment
                WhereFragmentID = 2;
                showLocationView(true);//展示出LocationFragment中需要用到的mainActivity的控件
                transaction.commit();
                break;
            case R.id.bt_areaSet://连接设置跳转到LocationFragemnt
                //  MyApplication.getInstance().getTtsSpeak().SpeechAdd("连接设置跳转到LINKFragemnt",Config.currtentVoiceVolume);
                transaction = getFragmentManager().beginTransaction();
                //初始化transaction
                transaction.hide(linkFragment);
                transaction.hide(loactionFragment);
                transaction.show(areaFragment);//默认进入locationFragment隐藏LinkFragment
                WhereFragmentID = 3;
                showLocationView(true);//展示出LocationFragment中需要用到的mainActivity的控件
                transaction.commit();
                break;
        }
    }

    /**
     * 展示/隐藏
     * LocationFragment中需要用到的mainActivity的控件
     */
    private void showLocationView(Boolean status) {
        if (status) {
            Tvlocation1.setVisibility(View.VISIBLE);
            Tvlocation2.setVisibility(View.VISIBLE);
            Tvpoint1.setVisibility(View.VISIBLE);
            Tvpoint2.setVisibility(View.VISIBLE);
            QiuView1.setVisibility(View.VISIBLE);
            QiuView2.setVisibility(View.VISIBLE);
        } else {//隐藏控件
            Tvlocation1.setVisibility(View.INVISIBLE);
            Tvlocation2.setVisibility(View.INVISIBLE);
            Tvpoint1.setVisibility(View.INVISIBLE);
            Tvpoint2.setVisibility(View.INVISIBLE);
            QiuView1.setVisibility(View.INVISIBLE);
            QiuView2.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        linkFragment = new LinkFragment();
        loactionFragment = new LoactionFragment();
        areaFragment = new AreaFragment();
        transaction = getFragmentManager().beginTransaction();
        //初始化transaction
        transaction.add(R.id.frame_layout, linkFragment);
        transaction.add(R.id.frame_layout, loactionFragment);
        transaction.add(R.id.frame_layout, areaFragment);
        transaction.hide(loactionFragment);
        transaction.hide(areaFragment);
        transaction.show(linkFragment);//默认进入linkFragment隐藏locationFragment
        transaction.commit();
    }

    /**
     * 人像摄像头初始化
     */
    private void initPersonCamera() {
        mPreviewSurface = (SurfaceView) findViewById(R.id.preview);
        mPreviewSurface.getHolder().addCallback(mPreviewCallback);
        mFaceSurface = (SurfaceView) findViewById(R.id.face);
        mSurfaceHolder = mFaceSurface.getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mFaceSurface.setZOrderOnTop(true);
        nv21 = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
        buffer = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
        accelerometer = new Accelerometer(this);
        mFaceDetector = FaceDetector.createDetector(this, null);//实例化人脸检测对象
        setSurfaceSize();
        // openCamera();//打开摄像头

    }

    /**
     * 温度摄像头初始化
     */
    private void initJuge(Bundle savedInstanceState) {
        /* global init */
        MagDevice.init(this);
        /* init ui */
        // initUi();
        initJuGeUi();
        /* enum timer handler */
        mEnumHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case START_TIMER_ID:
                        mEnumHandler.removeMessages(START_TIMER_ID);
                        if (!mDev.isLinked()) {
                            updateDeviceList();
                        }
                        mEnumHandler.sendEmptyMessageDelayed(START_TIMER_ID, TIMER_INTERVAL);
                        break;
                }
            }
        };

        /* start timer */
        mEnumHandler.sendEmptyMessage(START_TIMER_ID);

        /* runtime permit */
        Utils.requestRuntimePermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 0, R.string.writeSDPermission);
        /* restore parameter */
        if (savedInstanceState != null) {
            final int usbId = savedInstanceState.getInt(USBID_ARGS);
            final int status = savedInstanceState.getInt(STATUS_ARGS);
            mRestoreHandler = new Handler();
            mRestoreRunnable = new Runnable() {
                @Override
                public void run() {
                    updateDeviceList();
                    Log.i(TAG, "restore");

                }
            };

            /* restore after all ui component created */
            /* FIXME */
            mRestoreHandler.postDelayed(mRestoreRunnable, 200);
        }
    }

    private void initJuGeUi() {
        //mDev = new MagDevice();
        mDevices = new ArrayList<>();
        mDeviceStrings = new ArrayList<>();
        FragmentManager fm = getSupportFragmentManager();
        mVideoFragment = (VideoFragment) fm.findFragmentById(R.id.videoLayout);
        linkFragment.setmVideoFragment(mVideoFragment);
        if (mVideoFragment == null) {
            mVideoFragment = new VideoFragment();
            linkFragment.setmVideoFragment(mVideoFragment);
            fm.beginTransaction().add(R.id.videoLayout, mVideoFragment).commit();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private void updateDeviceList() {
        MagDevice.getDevices(this, 33596, 1, mDevices);
        linkFragment.setmDevices(mDevices);
        linkFragment.autoConnect();

    }


    @Override
    protected void onDestroy() {
        /* remove pending messages */
        mEnumHandler.removeCallbacksAndMessages(null);
        mEnumHandler = null;
        if (mRestoreHandler != null) {
            mRestoreHandler.removeCallbacksAndMessages(null);
            mRestoreRunnable = null;
            mRestoreHandler = null;
        }

        DelayStartHandler.removeCallbacksAndMessages(null);

        ReloadServiceHandler.removeCallbacksAndMessages(null);
        ReloadServiceHandler = null;
        /* disconnect camera when app exited */
        if (mDev.isProcessingImage()) {
            mDev.stopProcessImage();
            mVideoFragment.stopDrawingThread();
        }
        if (mDev.isLinked()) {
            mDev.dislinkCamera();
        }
        mDev = null;

        stopService(new Intent(this, FtpService.class));
        //关闭所有通用服务
        ServiceManager.getInstance().stopServices();

        super.onDestroy();
    }

    /**
     * 打开摄像头
     */
    private void openCamera() {
        if (mCamera != null) {
            return;
        }
        try {
            mCamera = Camera.open(mCameraId);
            Toast.makeText(this, "摄像头已开启,请勿拔下摄像头", Toast.LENGTH_SHORT).show();
            this.degrees = setCameraDisplayOrientation(this, mCameraId, mCamera);//设置摄像头的预览方向
            Log.i(TAG, "this.degrees: "+this.degrees);
            //获取摄像头参数对象
            Camera.Parameters params = mCamera.getParameters();
            //设置预览的格式
            params.setPreviewFormat(ImageFormat.NV21);
            //设置预览的分辨率，这里设置640*480，到目前为止只支持该分辨率的人脸检测
            params.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
            params.setPictureSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
            params.setPreviewFrameRate(5);
            params.setRotation(degrees);
            //给摄像头设置参数配置
            mCamera.setParameters(params);
            //给摄像头设置预览回到，这里使用的Lambda表达式代表的只有一个回调函数的匿名内部类
            //mCamera.setPreviewCallback((data, camera) -> System.arraycopy(data, 0, nv21, 0, data.length));


        } catch (Exception e) {
            e.printStackTrace();
            closeCamera();
            DelayDoHandler.getInstance().sendDelayVoice("人脸摄像头开启失败，请检查",3*1000);

            RunningInfo runningInfo = new RunningInfo();
            runningInfo.setCameraStatus("人脸摄像头开启失败，请检查");
            runningInfo.upload();

            lampUtil.setlamp(2, 500, -1);//设置默认的故障灯光
            return;
        }
        try {
            mCamera.setPreviewDisplay(mPreviewSurface.getHolder());
            mCamera.startPreview();
          /*  Message message=Message.obtain();
            message.what=MSG3;
            message.obj="人脸摄像头开启成功";
            DelayStartHandler.sendMessageDelayed(message,6000);*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
                System.arraycopy(bytes, 0, nv21, 0, bytes.length);
                if (iftaken == true) {
                    iftaken = false;
                    Camera.Size size = camera.getParameters().getPreviewSize();
                    try {
                        YuvImage image = new YuvImage(bytes, ImageFormat.NV21, size.width, size.height, null);
                        if (image != null) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
                            Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                            bmp = Screenutil.rotaingImageView(90, bmp);//旋转照片
                            saveBitmap(bmp);
                            stream.close();
                        }
                    } catch (Exception ex) {
                        Log.e("Sys", "Error:" + ex.getMessage());
                    }
                }
            }
        });


    }

    public void saveBitmap(Bitmap bitmap) {
        Log.e(TAG, "保存人脸图片");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
        String formatStr = formatter.format(new Date());
        String maxTmp = String.valueOf(TempThreshold);
        if (String.valueOf(maxTmp).length() >= 4) {//最多保留4位
            maxTmp = maxTmp.substring(0, 4);
        }
        String fileName = formatStr + "_" + maxTmp + "Person.jpg";
        File f = new File(getFolderPathToday(), fileName);
        if (f.exists()) {
            f.delete();
        } else {
            photoNameSave2.saveLog2("人脸照片", fileName + "\r\n");
        }


        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    /**
     * 设置纹理尺寸
     */
    private void setSurfaceSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = (int) (width * PREVIEW_WIDTH / (float) PREVIEW_HEIGHT);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        Log.i(TAG, "setSurfaceSize:width " + width);
        Log.i(TAG, "setSurfaceSize:height " + height);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mPreviewSurface.setLayoutParams(params);
    }

    /**
     * 用于显示摄像头拍摄到的图像
     */
    private SurfaceHolder.Callback mPreviewCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "surfaceDestroyed: ");
            closeCamera();//关闭摄像头，并释放资源
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //requestPermission(new String[]{Manifest.permission.CAMERA},2000);
            Log.i(TAG, "surfaceCreated: ");
            openCamera();//打开摄像头
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mScaleMatrix.setScale(width / (float) PREVIEW_HEIGHT,

                    height / (float) PREVIEW_WIDTH);//设置缩放比例
            Log.i(TAG, "surfaceChanged:width " + width);
            Log.i(TAG, "surfaceChanged:height " + height);
            Log.i(TAG, "surfaceChanged:width / (float) PREVIEW_HEIGHT " + width / (float) PREVIEW_HEIGHT);
            Log.i(TAG, "surfaceChanged:height / (float) PREVIEW_WIDTH " + height / (float) PREVIEW_WIDTH);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        //openCamera();
        if (null != accelerometer) {
            accelerometer.start();
        }
        stop = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stop) {
                    if (null == nv21) {
                        continue;
                    }
                    synchronized (nv21) {
                        //System.arraycopy(nv21, 0, buffer, 0, nv21.length);
                    }
                    int direction = Accelerometer.getDirection();// 获取手机朝向
                    boolean frontCamera = (Camera.CameraInfo.CAMERA_FACING_FRONT == mCameraId);
                    if (frontCamera) {
                        direction = (4 - direction) % 4;// 0,1,2,3,4分别表示0,90,180,270和360度
                        Log.i(TAG, "direction: " + direction);
                    }
                    String result = mFaceDetector.trackNV21(
                            nv21, PREVIEW_WIDTH, PREVIEW_HEIGHT, 1, 1);//获取人脸检测结果
                    FaceRect face = Result.result(result);//获取返回的数据
                    // Log.e(TAG, "result:" + result);//输出检测结果,该结果为JSON数据
                    Canvas canvas = mSurfaceHolder.lockCanvas();//锁定画布用于绘制
                    if (null == canvas) {
                        continue;
                    }
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清除之前的绘制图像
                    canvas.setMatrix(mScaleMatrix);
                    if (face == null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                        continue;
                    }
                    DrawFaceRect.DrawScopeDetection(PREVIEW_HEIGHT, PREVIEW_WIDTH, canvas);
                    if (face != null) {
                        //判断两次检测到人脸的间隔时间，如果超过500ms，则判断为第二个人，就重置温度阈值
                        //否则同一个人不会反复拍摄同样温度的照片
                        if (TimeUitl.timeInterval(500)) {
                            Log.i(TAG, "检测到不同人脸  ");
                            TempThreshold = CurrentConfig.getInstance().getCurrentData().getTemperature_threshold();//超过间隔则表示第二个人，则回复默认的阈值
                        }
                        face.bound = DrawFaceRect.RotateDeg90(face.bound, PREVIEW_HEIGHT);//绘制人脸的区域
                        if (face.point != null) {//绘制脸上关键点
                            for (int i = 0; i < face.point.length; i++) {
                                face.point[i] = DrawFaceRect.RotateDeg90(face.point[i], PREVIEW_HEIGHT);
                            }
                            //检测是否在有效区域
                            if (DrawFaceRect.scopeDetection(face, PREVIEW_HEIGHT, PREVIEW_WIDTH, canvas)) {
                                //检测是否张嘴
                                if (DrawFaceRect.MouthDetection(face)) {//张嘴才进行问读检测
                                    isGetFace = true;//进行温度检测
                                }
                            }
                            //绘制人脸检测的区域
                            DrawFaceRect.drawFaceRect(canvas, face, PREVIEW_WIDTH, frontCamera);
                        }
                    } else {
                        Log.e(TAG, "没有检测出人脸");
                    }
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }).start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
        stop = true;
        if (null != accelerometer) {
            accelerometer.stop();
        }

        //closeCamera();

    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "onRestart: ");
        super.onRestart();
    }

    private int[] FFCZhenTemps = new int[120 * 160];
    private int[] FFCZhenTempsALL = new int[120 * 160];
    public int zhenCount = 10;//间隔多少帧数
    private int FFCCount = 0;//计数

    /**
     * 多帧率FFC校准
     */
    private void MuiltFFC(float targetTemp) {
        MainActivity.DelayStartHandler.removeMessages(MSG10);
        FFCCount++;
        FFCZhenTemps = null;
        FFCZhenTemps = new int[120 * 160];
        /*设置距离为0，避免使用时干扰*/
        CorrectionPara correctionPara = new CorrectionPara();
        Log.i(TAG, "getCorrectionPara: " + mDev.getFixPara(correctionPara));
        Log.i(TAG, "fDistance: " + correctionPara.fDistance);
        // Log.i(TAG, "fTemp: "+correctionPara.fTemp);
        Log.i(TAG, "fTaoFilter: " + correctionPara.fTaoFilter);
        correctionPara.fTaoFilter = (float) 0.85;
        correctionPara.fDistance = 0;
        mDev.setFixPara(correctionPara);

        mDev.lock();
        mDev.getTemperatureData(FFCZhenTemps, true, true);
        mDev.unlock();
        for (int i = 0; i < FFCZhenTemps.length; i++) {
            FFCZhenTempsALL[i] = FFCZhenTemps[i] + FFCZhenTempsALL[i];
        }
        if (FFCCount == zhenCount) {
            int[] origin = new int[120 * 160];
            mDev.lock();
            mDev.getTemperatureData(origin, true, true);
            mDev.unlock();
            FFCCount = 0;
            int[] ffctemps = new int[120 * 160];
            for (int i = 0; i < FFCZhenTemps.length; i++) {
                ffctemps[i] = FFCZhenTempsALL[i] / zhenCount;
            }

            if (targetTemp == 1) {//如果目标值为1，则表示使用平均值来校准
                FFCTemps = FFCUtil.getFFC(ffctemps);
            } else {
                int avg = TempUtil.MaxMinTemp(origin)[2];//原始数据的平均值
                FFCTemps = FFCUtil.getFFC(ffctemps);//先得到由平均值补偿后的FFC矩阵。
                float BlackTempCom = targetTemp * 1000 - avg;  //计算黑体补偿,统一单位
              /*  FFCTemps=FFCUtil.getFFC(ffctemps,(int)targetTemp*1000);
                float conmpensation=targetTemp*1000-avg;//*/
//                Config.FFCcompensation = BlackTempCom * 0.001f;
                //黑体补偿等于原始数据平均值减去目标黑体温度。用于之后补偿。
                PreferencesUtils.put(WebConfig.FFC_COMPENSATION_PARAMETER,BlackTempCom * 0.001f);
                CurrentConfig.getInstance().updateSetting();
                ifBlackfFFC = true;//标志了已经进行了FFC黑体校准
            }

            FFCUtil.saveIntFfc(FFCTemps);//保存校准图
            SaveTemps.saveIntTemps(origin, "Origin");
            SaveTemps.saveIntTemps(FFCTemps, "FFC");

            Message message = Message.obtain();
            message.what = MSG9;
            message.obj = "FFC校准成功";

            FFCZhenTempsALL = null;
            FFCZhenTempsALL = new int[120 * 160];//清空总数据，避免下次校准叠加

            MainActivity.DelayStartHandler.sendMessageDelayed(message, 2000);
        } else {
            FFCHolder myHolder = new FFCHolder();
            myHolder.setSpeechString("多帧率FFC");
            myHolder.setTemp(targetTemp);
            Message FFCmessage = Message.obtain();
            FFCmessage.what = MSG10;
            FFCmessage.obj = myHolder;
            MainActivity.DelayStartHandler.sendMessageDelayed(FFCmessage, 100);
        }
    }

}
