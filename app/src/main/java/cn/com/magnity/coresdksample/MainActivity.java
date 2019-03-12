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
import android.net.wifi.ScanResult;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;
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
import java.util.ArrayList;
import java.util.List;

import cn.com.magnity.coresdk.MagDevice;
import cn.com.magnity.coresdk.types.EnumInfo;
import cn.com.magnity.coresdksample.Detect.DrawFaceRect;
import cn.com.magnity.coresdksample.Detect.FaceRect;
import cn.com.magnity.coresdksample.Detect.Result;
import cn.com.magnity.coresdksample.Service.FtpService;
import cn.com.magnity.coresdksample.View.QiuView;
import cn.com.magnity.coresdksample.utils.Config;
import cn.com.magnity.coresdksample.utils.TtsUtil;
import cn.com.magnity.coresdksample.utils.WifiAdmin;
import cn.com.magnity.coresdksample.utils.WifiUtil;


import static cn.com.magnity.coresdksample.MyApplication.WhereFragmentID;
//import static cn.com.magnity.coresdksample.MyApplication.isplay;
import static cn.com.magnity.coresdksample.MyApplication.isGetFace;
import static cn.com.magnity.coresdksample.MyApplication.istaken;
import static cn.com.magnity.coresdksample.MyApplication.mDev;
import static cn.com.magnity.coresdksample.utils.Config.SavaRootDirName;
import static cn.com.magnity.coresdksample.utils.Config.SavaTestDirName;
import static cn.com.magnity.coresdksample.utils.Screenutil.setCameraDisplayOrientation;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG="MainActivity";
    //const
    private static final int START_TIMER_ID = 0;
    private static final int TIMER_INTERVAL = 500;//ms

    private static final String STATUS_ARGS = "status";
    private static final String USBID_ARGS = "usbid";
    private static final int MSG1 = 100;//自动连接指定的wifi
    private static final int MSG2 =MSG1+1 ;
    private static final int MSG3 = MSG2+1;
    private static final int MSG4 = MSG3+1;

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
    //校准相关控件
    private TextView Tvlocation1,Tvlocation2,Tvpoint1,Tvpoint2;
    private QiuView QiuView1,QiuView2;
    private Button BtLocate,BtLink;
    //wifi管理
    WifiAdmin wifiAdmin ;
    private Handler WifiScanHandler;
    private Handler DelayStartHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_vertical);
        Log.i(TAG, "onCreate: ");
        SpeechUtility.createUtility(this, "appid=" + "5833f456"); //设置AppKey用于注册,AppID
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        //初始化控件
        initView();
        //初始化Fragment
        initFragment();
        //温度摄像头初始化
        initJuge(savedInstanceState);
        // 人像摄像头初始化
        initPersonCamera();
       //连接指定wifi
        wifiScan();
        //延时启动ftp wifi扫描等功能
        initFile();//初始化文件夹
        delayStart();
    }
/**
 * 初始化文件夹
* */
    private void initFile() {
        File file1 = Environment.getExternalStorageDirectory();
        File file2 = Environment.getExternalStorageDirectory();
        if (null != file1) {
            file1 = new File(file1, SavaRootDirName);
            if (!file1.exists()) {
                file1.mkdirs();
            }
        }
        if (null != file2) {
            file2 = new File(file2, SavaTestDirName);
            if (!file2.exists()) {
                file2.mkdirs();
            }
        }

    }

    /**
 * 延时启动ftp wifi扫描等功能
 * 以便tts语音已经被初始化
 * */
    private void delayStart() {
        DelayStartHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case MSG2://启动ftp服务器
                        //初始化ftp
                        initFtp();
                        break;
                    case MSG3://延时播放语音
                        String voice=msg.obj.toString();
                        MyApplication.getInstance().getTtsUtil().SpeechAdd(voice, Config.currtentVoiceVolume);
                        Log.i(TAG, "延时播放语音: "+voice);
                        break;
                }
            }
        };
        DelayStartHandler.sendEmptyMessageDelayed(MSG2,5000);
    }

    /**
     * 连接指定的wifi
     * */
    private void wifiScan() {
        WifiScanHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case MSG1://自动连接指定的wifi
                        WifiScanHandler.removeMessages(1);
                        wifiAdmin= new WifiAdmin(getApplicationContext());//刷新wifiAdmin
                        wifiAdmin.openWifi();
                        //执行搜索
                        String di="XIAONUO1";
                        if(!wifiAdmin.getSSID().equals("\"" +di + "\"")){
                            //Log.i(TAG, "wifiAdmin.getSSID(): "+wifiAdmin.getSSID());
                            wifiAdmin.startScan();
                            List<ScanResult> scanResultList;
                            scanResultList= wifiAdmin.getWifiList();
                            //Log.i(TAG, "scanResultList.size(): "+scanResultList.size());
                            for(int i=0;i<scanResultList.size();i++){
                                // Log.i(TAG, "scanResultList.SSID(): "+scanResultList.get(i).SSID);
                                if (scanResultList.get(i).SSID.equals("XIAONUO1")){
                                    Log.i(TAG, "找到指定wifi： XIAONUO1  准备连接: ");
                                    wifiAdmin.addNetwork(
                                            wifiAdmin.CreateWifiInfo("XIAONUO1",
                                                    "XiaoNuo2018",3));
                                    break;
                                }
                            }
                        }
                        WifiScanHandler.sendEmptyMessageDelayed(MSG1,500);
                        break;

                }
            }
        };
        WifiScanHandler.sendEmptyMessage(1);//启动
    }

/***
 * 初始化ftp
 * */
    private void initFtp() {
        WifiUtil wifiUtil=new WifiUtil();
        String ip = wifiUtil.getIp(this);
        if(TextUtils.isEmpty(ip)){
            Log.e(TAG,"获取不到IP，请连接网络");
            MyApplication.getInstance().getTtsUtil().SpeechAdd("网络不通，请检查",Config.currtentVoiceVolume);
        }else{
            String str = "请在IE浏览器上输入网址访问FTP服务\n" +
                    "ftp://"+ip+":2221\n" +
                    "账号:didano\n" +
                    "密码:12345678";
            Log.i(TAG,str);
            MyApplication.getInstance().getTtsUtil().SpeechAdd("正在开启ftp服务器，当前 i p 为"+ip,Config.currtentVoiceVolume);
        }
        startService(new Intent(this, FtpService.class));
    }

    /**
     * initView
     * */
    private void initView() {
        Tvlocation1=(TextView) findViewById(R.id.tv_location1);
        Tvlocation2=(TextView) findViewById(R.id.tv_location2);
        Tvpoint1=(TextView) findViewById(R.id.tv_point1);
        Tvpoint2=(TextView) findViewById(R.id.tv_point2);
        QiuView1= (QiuView) findViewById(R.id.view1);
        QiuView2= (QiuView)findViewById(R.id.view2);
        BtLink= (Button) findViewById(R.id.bt_linkSet);
        BtLocate= (Button) findViewById(R.id.bt_locateSet);
        BtLink.setOnClickListener(this);
        BtLocate.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_linkSet://连接设置跳转到LINKFragemnt
                MyApplication.getInstance().getTtsUtil().SpeechAdd("连接设置跳转到LINKFragemnt",Config.currtentVoiceVolume);
                transaction=getFragmentManager().beginTransaction();
                //初始化transaction
                transaction.hide(loactionFragment);
                transaction.show(linkFragment);//连接LinkFragment隐藏locationFragment
                WhereFragmentID=1;
                showLocationView(false);//隐藏LocationFragment中需要用到的mainActivity的控件
                transaction.commit();
                break;
            case R.id.bt_locateSet://连接设置跳转到LocationFragemnt
                MyApplication.getInstance().getTtsUtil().SpeechAdd("连接设置跳转到LINKFragemnt",Config.currtentVoiceVolume);
                transaction=getFragmentManager().beginTransaction();
                //初始化transaction
                transaction.hide(linkFragment);
                transaction.show(loactionFragment);//默认进入locationFragment隐藏LinkFragment
                WhereFragmentID=2;
                showLocationView(true);//展示出LocationFragment中需要用到的mainActivity的控件
                transaction.commit();
                break;
        }
    }
/**
 *展示/隐藏
 * LocationFragment中需要用到的mainActivity的控件
 * */
    private void showLocationView(Boolean status) {
        if(status){
            Tvlocation1.setVisibility(View.VISIBLE);
            Tvlocation2.setVisibility(View.VISIBLE);
            Tvpoint1.setVisibility(View.VISIBLE);
            Tvpoint2.setVisibility(View.VISIBLE);
            QiuView1.setVisibility(View.VISIBLE);
            QiuView2.setVisibility(View.VISIBLE);
        }else {//隐藏控件
            Tvlocation1.setVisibility(View.INVISIBLE);
            Tvlocation2.setVisibility(View.INVISIBLE);
            Tvpoint1.setVisibility(View.INVISIBLE);
            Tvpoint2.setVisibility(View.INVISIBLE);
            QiuView1.setVisibility(View.INVISIBLE);
            QiuView2.setVisibility(View.INVISIBLE);
        }

    }

    /**初始化Fragment
     * */
    private void initFragment(){
        linkFragment=new LinkFragment();
        loactionFragment=new LoactionFragment();
        transaction=getFragmentManager().beginTransaction();
        //初始化transaction
        transaction.add(R.id.frame_layout,linkFragment);
        transaction.add(R.id.frame_layout,loactionFragment);
        transaction.hide(loactionFragment);
        transaction.show(linkFragment);//默认进入linkFragment隐藏locationFragment
        transaction.commit();
    }
    /**
     * 人像摄像头初始化
     * */
    private void initPersonCamera() {
        mPreviewSurface = (SurfaceView) findViewById(R.id.preview);
        mPreviewSurface.getHolder().addCallback(mPreviewCallback);
        mFaceSurface = (SurfaceView) findViewById(R.id.face);
        mSurfaceHolder =mFaceSurface.getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mFaceSurface.setZOrderOnTop(true);
        nv21 = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
        buffer = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
        accelerometer = new Accelerometer(this);
        mFaceDetector = FaceDetector.createDetector(this, null);//实例化人脸检测对象
        setSurfaceSize();
        // openCamera();//打开摄像头

    }
    /**温度摄像头初始化
     * */
    private void initJuge(Bundle savedInstanceState ) {
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
                        if(!mDev.isLinked()){
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
        mVideoFragment = (VideoFragment)fm.findFragmentById(R.id.videoLayout);
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
        if (mRestoreHandler != null) {
            mRestoreHandler.removeCallbacksAndMessages(null);
            mRestoreRunnable = null;
            mRestoreHandler = null;
        }
        WifiScanHandler.removeCallbacksAndMessages(null);
        DelayStartHandler.removeCallbacksAndMessages(null);
        /* disconnect camera when app exited */
        if (mDev.isProcessingImage()) {
            mDev.stopProcessImage();
            mVideoFragment.stopDrawingThread();
        }
        if (mDev.isLinked()) {
            mDev.dislinkCamera();
        }
        mDev = null;
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
            Toast.makeText(this,"摄像头已开启,请勿拔下摄像头",Toast.LENGTH_SHORT).show();
            this.degrees=setCameraDisplayOrientation(this, mCameraId, mCamera);//设置摄像头的预览方向
            //获取摄像头参数对象
            Camera.Parameters params = mCamera.getParameters();
            //设置预览的格式
            params.setPreviewFormat(ImageFormat.NV21);
            //设置预览的分辨率，这里设置640*480，到目前为止只支持该分辨率的人脸检测
            params.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
            params.setPictureSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
            params.setPreviewFrameRate(5);
            //给摄像头设置参数配置
            mCamera.setParameters(params);
            //给摄像头设置预览回到，这里使用的Lambda表达式代表的只有一个回调函数的匿名内部类
            //mCamera.setPreviewCallback((data, camera) -> System.arraycopy(data, 0, nv21, 0, data.length));
        } catch (Exception e) {
            e.printStackTrace();
            closeCamera();
            Message message=Message.obtain();
            message.what=MSG3;
            message.obj="人脸摄像头开启失败，请检查";
            DelayStartHandler.sendMessageDelayed(message,6000);
            return;
        }
        try {
            mCamera.setPreviewDisplay(mPreviewSurface.getHolder());
            mCamera.startPreview();
            Message message=Message.obtain();
            message.what=MSG3;
            message.obj="人脸摄像头开启成功";
            DelayStartHandler.sendMessageDelayed(message,6000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
                System.arraycopy(bytes, 0, nv21, 0, bytes.length);
                if(istaken==true){
                    istaken=false;
                    Camera.Size size = camera.getParameters().getPreviewSize();
                    try{
                        YuvImage image = new YuvImage(bytes, ImageFormat.NV21, size.width, size.height, null);
                        if(image!=null){
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
                            Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                            saveBitmap(bmp);
                            stream.close();
                        }
                    }catch(Exception ex){
                        Log.e("Sys","Error:"+ex.getMessage());
                    }
                }
            }
        });


    }

    public  void saveBitmap(Bitmap bitmap) {
        Log.e(TAG, "保存图片");
        File f = new File(Environment.getExternalStorageDirectory(),
                SavaRootDirName+File.separator+System.currentTimeMillis() + "Person.jpg");
        if (f.exists()) {
            f.delete();
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
        Log.i(TAG, "setSurfaceSize:width "+width);
        Log.i(TAG, "setSurfaceSize:height "+height);
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
            Log.i(TAG, "surfaceChanged:width "+width);
            Log.i(TAG, "surfaceChanged:height "+height);
            Log.i(TAG, "surfaceChanged:width / (float) PREVIEW_HEIGHT "+width / (float) PREVIEW_HEIGHT);
            Log.i(TAG, "surfaceChanged:height / (float) PREVIEW_WIDTH "+height / (float) PREVIEW_WIDTH);
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
                        System.arraycopy(nv21, 0, buffer, 0, nv21.length);
                    }
                    int direction = Accelerometer.getDirection();// 获取手机朝向
                    boolean frontCamera = (Camera.CameraInfo.CAMERA_FACING_FRONT == mCameraId);
                    if (frontCamera) {
                        direction = (4 - direction) % 4;// 0,1,2,3,4分别表示0,90,180,270和360度
                        Log.i(TAG, "direction: "+direction);
                    }
                    String result = mFaceDetector.trackNV21(
                            buffer, PREVIEW_WIDTH, PREVIEW_HEIGHT, 1, 1);//获取人脸检测结果
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
                    if (face != null) {
                        isGetFace=true;
                        Log.i(TAG, "检测到人脸: ");
                        face.bound = DrawFaceRect.RotateDeg90(face.bound, PREVIEW_HEIGHT);//绘制人脸的区域
                        if (face.point != null) {//绘制脸上关键点
                            for (int i = 0; i < face.point.length; i++) {
                                face.point[i] = DrawFaceRect.RotateDeg90(face.point[i], PREVIEW_HEIGHT);
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


}
