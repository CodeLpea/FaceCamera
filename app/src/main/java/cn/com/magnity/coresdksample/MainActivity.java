package cn.com.magnity.coresdksample;

import android.Manifest;


import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

import cn.com.magnity.coresdk.MagDevice;
import cn.com.magnity.coresdk.types.EnumInfo;
import cn.com.magnity.coresdksample.Detect.DrawFaceRect;
import cn.com.magnity.coresdksample.Detect.FaceRect;
import cn.com.magnity.coresdksample.Detect.Result;


import static cn.com.magnity.coresdksample.utils.Config.SavaRootDirName;
import static cn.com.magnity.coresdksample.utils.Screenutil.setCameraDisplayOrientation;

public class MainActivity extends AppCompatActivity implements MagDevice.ILinkCallback {
    private static final String TAG="MainActivity";
    //const
    private static final int START_TIMER_ID = 0;
    private static final int TIMER_INTERVAL = 500;//ms

    private static final int STATUS_IDLE = 0;
    private static final int STATUS_LINK = 1;
    private static final int STATUS_TRANSFER = 2;

    private static final String STATUS_ARGS = "status";
    private static final String USBID_ARGS = "usbid";

    //non-const
    private MagDevice mDev;

    private ArrayList<EnumInfo> mDevices;
    private ArrayList<String> mDeviceStrings;
    private ArrayAdapter mListAdapter;
    private EnumInfo mSelectedDev;
    private ListView mDevList;
    private Button mLinkBtn;
    private Button mPlayBtn;
    private Button mStopBtn;
    private Button mDislinkBtn;
    private Button mRotateBtn;
    private Button mSavePicBtn;
    private TextView mTextSelectedDevice;
    private Handler mEnumHandler;
    private Handler mRestoreHandler;
    private Runnable mRestoreRunnable;
    private VideoFragment mVideoFragment;

    private int mDegree;//0 - 90, 1 - 180, 2 - 270



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
    private boolean istaken;//拍照状态按钮
    private boolean stop;//人脸检测开关
    private FragmentTransaction transaction;//定义用于加载连接和设置界面
    private LinkFragment linkFragment;
    private LoactionFragment loactionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_amin_activity);
        SpeechUtility.createUtility(this, "appid=" + "5833f456"); //设置AppKey用于注册,AppID
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        //initFragment();
        initJuge(savedInstanceState);//温度摄像头初始化
        initPersonCamera();// 人像摄像头初始化


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
        setFragment(linkFragment);
    }
    /**
     * 设置显示的fragement
     * @param fragment
     */
    public void setFragment(Fragment fragment) {
        Log.e(TAG, "setFragment: "+(fragment==null) );
        transaction.show(linkFragment);//展示
        transaction.commit();
      /*  Fragment current = getFragmentManager().findFragmentById(R.id.frame_layout);
        if (current != null && current instanceof LinkFragment){
            transaction.hide(loactionFragment);  //隐藏
            transaction.show(linkFragment);//展示
        }else {
            transaction.hide(linkFragment);  //隐藏
            transaction.show(loactionFragment);//展示
        }
        transaction.commit();*/
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
        initUi();

        /* enum timer handler */
        mEnumHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case START_TIMER_ID:
                        mEnumHandler.removeMessages(START_TIMER_ID);
                        updateDeviceList();
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
                    restore(usbId, status);
                }
            };

            /* restore after all ui component created */
            /* FIXME */
            mRestoreHandler.postDelayed(mRestoreRunnable, 200);
        }
    }
/**
 * 初始化ui*/
    private void initUi() {
        /* new object */
        mDev = new MagDevice();
        mDevices = new ArrayList<>();
        mDeviceStrings = new ArrayList<>();
        mListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, mDeviceStrings);

        mDevList = (ListView)findViewById(R.id.listDev);
        mDevList.setAdapter(mListAdapter);
        mDevList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EnumInfo dev = mDevices.get(position);
                if (mSelectedDev == null || mSelectedDev.id != dev.id || !mDev.isLinked()) {
                    mDev.dislinkCamera();
                    mSelectedDev = dev;
                    mTextSelectedDevice.setText(mSelectedDev.name);
                    updateButtons();
                }
            }
        });

        MagOnClickListener listener = new MagOnClickListener();
        mLinkBtn = (Button)findViewById(R.id.btnLink);
        mLinkBtn.setOnClickListener(listener);
        mPlayBtn = (Button)findViewById(R.id.btnPlay);
        mPlayBtn.setOnClickListener(listener);
        mStopBtn = (Button)findViewById(R.id.btnStop);
        mStopBtn.setOnClickListener(listener);
        mDislinkBtn = (Button)findViewById(R.id.btnDislink);
        mDislinkBtn.setOnClickListener(listener);
        mRotateBtn = (Button)findViewById(R.id.btnRotate);
        mRotateBtn.setOnClickListener(listener);
        mSavePicBtn = (Button)findViewById(R.id.btnSavePic);
        mSavePicBtn.setOnClickListener(listener);
        mTextSelectedDevice = (TextView) findViewById(R.id.tvSelectedName);

        updateButtons();

        FragmentManager fm = getSupportFragmentManager();
        mVideoFragment = (VideoFragment)fm.findFragmentById(R.id.videoLayout);
        if (mVideoFragment == null) {
            mVideoFragment = new VideoFragment();
            fm.beginTransaction().add(R.id.videoLayout, mVideoFragment).commit();
        }
    }

    private void restore(int usbId, int status) {
        /* restore list status */
       MagDevice.getDevices(this, 33596, 1, mDevices);

        mDeviceStrings.clear();
        for (EnumInfo dev : mDevices) {
            if (dev.id == usbId) {
                mSelectedDev = dev;
            }
            mDeviceStrings.add(dev.name);
        }
        if (mSelectedDev == null) {
            return;
        }

        mTextSelectedDevice.setText(mSelectedDev.name);

        /* restore camera status */
        switch (status) {
            case STATUS_IDLE:
                //do nothing
                break;
            case STATUS_LINK:
                mDev.linkCamera(MainActivity.this, mSelectedDev.id, MainActivity.this);
                updateButtons();
                break;
            case STATUS_TRANSFER:
                int r = mDev.linkCamera(MainActivity.this, mSelectedDev.id,
                    new MagDevice.ILinkCallback() {
                        @Override
                        public void linkResult(int result) {
                            if (result == MagDevice.CONN_SUCC) {
                            /* 连接成功 */
                                play();
                            } else if (result == MagDevice.CONN_FAIL) {
                            /* 连接失败 */
                            } else if (result == MagDevice.CONN_DETACHED) {
                            /* 连接失败*/
                            }
                            updateButtons();
                        }
                    });

                if (r == MagDevice.CONN_SUCC) {
                    play();
                }
                updateButtons();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        /* save parameter for restore when screen rotating */
        int status = STATUS_IDLE;
        if (mDev.isProcessingImage()) {
            status = STATUS_TRANSFER;
        } else if (mDev.isLinked()) {
            status = STATUS_LINK;
        }
        outState.putInt(STATUS_ARGS, status);
        if (mSelectedDev != null) {
            outState.putInt(USBID_ARGS, mSelectedDev.id);
        }
    }

    private void updateDeviceList() {
        MagDevice.getDevices(this, 33596, 1, mDevices);

        mDeviceStrings.clear();
        for (EnumInfo dev : mDevices) {
            mDeviceStrings.add(dev.name);
        }

        mListAdapter.notifyDataSetChanged();

        //直接连接link已经扫描到的设备
        if (mDevices!=null&&mDevices.size()>0){
            EnumInfo dev = mDevices.get(0);
        if (mSelectedDev == null || mSelectedDev.id != dev.id || !mDev.isLinked()) {//判断是否已经连接
            Log.i(TAG, "mDevices.get(0).name: "+mDevices.get(0).name);
            Log.i(TAG, "mDevices.get(0).id: "+mDevices.get(0).id);
            mDev.dislinkCamera();//确保断开连接
            mSelectedDev = dev;
            mTextSelectedDevice.setText(mSelectedDev.name);
            updateButtons();
            //以上将扫描到的设备显示出来，添加到mSelecteDev中缓存，以待下面连接

            Link();   //自动连接
        }
        }

    }
/**
 * 自动连接摄像头*/
    private void Link() {
        Toast.makeText(this,"开启播放：",Toast.LENGTH_SHORT).show();
        int r = mDev.linkCamera(MainActivity.this, mSelectedDev.id,
                new MagDevice.ILinkCallback() {
                    @Override
                    public void linkResult(int result) {
                        if (result == MagDevice.CONN_SUCC) {
                            /* 连接成功 */play();
                        } else if (result == MagDevice.CONN_FAIL) {
                            /* 连接失败 */
                        } else if (result == MagDevice.CONN_DETACHED) {
                            /* 连接失败*/
                        }
                        updateButtons();
                    }
                });

        if (r == MagDevice.CONN_SUCC) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    play();
                }
            });
        }
        updateButtons();

    }

    private void updateButtons() {
        if (mDev.isProcessingImage()) {
            mLinkBtn.setEnabled(false);
            mPlayBtn.setEnabled(false);
            mStopBtn.setEnabled(true);
            mDislinkBtn.setEnabled(true);
            mRotateBtn.setEnabled(true);
            mSavePicBtn.setEnabled(true);
        } else if (mDev.isLinked()) {
            mLinkBtn.setEnabled(false);
            mPlayBtn.setEnabled(true);
            mStopBtn.setEnabled(false);
            mDislinkBtn.setEnabled(true);
            mRotateBtn.setEnabled(true);
            mSavePicBtn.setEnabled(false);
        } else {
            mLinkBtn.setEnabled(mSelectedDev!=null);
            mPlayBtn.setEnabled(false);
            mStopBtn.setEnabled(false);
            mDislinkBtn.setEnabled(false);
            mRotateBtn.setEnabled(false);
            mSavePicBtn.setEnabled(false);
        }
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

    private void play() {
        mDev.setColorPalette(MagDevice.ColorPalette.PaletteIronBow);
        if (mDev.startProcessImage(mVideoFragment, 0, 0)) {
            mVideoFragment.startDrawingThread(mDev);
        }
    }

    @Override
    public void linkResult(int result) {
        if (result == MagDevice.CONN_SUCC) {
            /* 连接成功 */

        } else if (result == MagDevice.CONN_FAIL) {
            /* 连接失败 */

        } else if (result == MagDevice.CONN_DETACHED) {
            /* 连接失败*/

        }
        updateButtons();
    }

    private class MagOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btnLink:
                    mDev.linkCamera(MainActivity.this, mSelectedDev.id, MainActivity.this);
                    updateButtons();
                    break;
                case R.id.btnPlay:
                    play();
                    updateButtons();
                    break;
                case R.id.btnStop:
                    mDev.stopProcessImage();
                    mVideoFragment.stopDrawingThread();
                    updateButtons();
                    break;
                case R.id.btnDislink:
                    mDev.dislinkCamera();
                    mVideoFragment.stopDrawingThread();
                    mDegree = 0;
                    updateButtons();
                    break;
                case R.id.btnRotate:
                    mDegree++;
                    if (mDegree > 3) {
                        mDegree = 0;
                    }
                    mDev.stopProcessImage();
                    mVideoFragment.stopDrawingThread();
                    mDev.setImageTransform(0, mDegree);//在设置旋转方向之前要停止预览和标记操作
                    play();
                    break;
                case R.id.btnSavePic:
                    istaken=true;
                    //takePhoto();
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        return;
                    }
                    File file = Environment.getExternalStorageDirectory();
                    if (null == file) {
                        return;
                    }
                    file = new File(file, SavaRootDirName);
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    if (mDev.saveBMP(0, file.getAbsolutePath() +
                            File.separator + System.currentTimeMillis() + "JuGe.bmp")) {
                        Toast.makeText(MainActivity.this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            mDevList.requestFocus();
        }
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
        } catch (Exception e) {
            e.printStackTrace();
            closeCamera();
            return;
        }
        //获取摄像头参数对象
        Camera.Parameters params = mCamera.getParameters();
        //设置预览的格式
        params.setPreviewFormat(ImageFormat.NV21);
        //设置预览的分辨率，这里设置640*480，到目前为止只支持该分辨率的人脸检测
        params.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        params.setPictureSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        //给摄像头设置参数配置
        mCamera.setParameters(params);
        //给摄像头设置预览回到，这里使用的Lambda表达式代表的只有一个回调函数的匿名内部类
        //mCamera.setPreviewCallback((data, camera) -> System.arraycopy(data, 0, nv21, 0, data.length));
        try {
            mCamera.setPreviewDisplay(mPreviewSurface.getHolder());
            mCamera.startPreview();
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
     * 拍照*/
    public void takePhoto(){

        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                //技术：图片压缩技术（如果图片不压缩，图片大小会过大，会报一个oom内存溢出的错误）
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Matrix matrix = new Matrix();//获取矩阵对象用于图形变换
                matrix.setRotate(degrees);//设置旋转角度
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                try {
                    File pictureFile = new File(Environment.getExternalStorageDirectory(),
                            SavaRootDirName+File.separator+System.currentTimeMillis() + "Person.jpg");
                    FileOutputStream fos = new FileOutputStream(pictureFile);//图片保存路径
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);//压缩格式，质量，压缩路径
                    camera.stopPreview();
                    camera.startPreview();
                    bitmap.recycle();//回收bitmap空间
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

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
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mPreviewSurface.setLayoutParams(params);
    }
    /**
     * 用于显示摄像头拍摄到的图像
     */
    private SurfaceHolder.Callback mPreviewCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            closeCamera();//关闭摄像头，并释放资源
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //requestPermission(new String[]{Manifest.permission.CAMERA},2000);
            openCamera();//打开摄像头
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mScaleMatrix.setScale(width / (float) PREVIEW_HEIGHT,

                    height / (float) PREVIEW_WIDTH);//设置缩放比例
        }
    };

  @Override
    protected void onResume() {
        super.onResume();
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
                    }
                    String result = mFaceDetector.trackNV21(
                            buffer, PREVIEW_WIDTH, PREVIEW_HEIGHT, 1, direction);//获取人脸检测结果
                    FaceRect face = Result.result(result);//获取返回的数据
                    Log.e(TAG, "result:" + result);//输出检测结果,该结果为JSON数据
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
        stop = true;
        if (null != accelerometer) {
            accelerometer.stop();
        }
        closeCamera();

    }
}
