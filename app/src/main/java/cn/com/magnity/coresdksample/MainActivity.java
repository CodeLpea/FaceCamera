package cn.com.magnity.coresdksample;

import android.Manifest;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;

import cn.com.magnity.coresdk.MagDevice;
import cn.com.magnity.coresdk.types.EnumInfo;

public class MainActivity extends AppCompatActivity implements MagDevice.ILinkCallback {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* global init */
        MagDevice.init(this);

        /* new object */
        mDev = new MagDevice();
        mDevices = new ArrayList<>();
        mDeviceStrings = new ArrayList<>();
        mListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, mDeviceStrings);

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
        Utils.requestRuntimePermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, 0, R.string.writeSDPermission);

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

    private void initUi() {
        mDevList = (ListView)findViewById(R.id.listDev);
        mDevList.setAdapter(mListAdapter);
        mDevList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EnumInfo dev = mDevices.get(position);
                if (mSelectedDev == null ||
                        mSelectedDev.id != dev.id ||
                        !mDev.isLinked()) {
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
            /* 设备拔出*/
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
                    mDev.setImageTransform(0, mDegree);
                    play();
                    break;
                case R.id.btnSavePic:
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        return;
                    }
                    File file = Environment.getExternalStorageDirectory();
                    if (null == file) {
                        return;
                    }
                    file = new File(file, "magnity/mx/media/");
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    if (mDev.saveBMP(0, file.getAbsolutePath() +
                            File.separator + System.currentTimeMillis() + ".bmp")) {
                        Toast.makeText(MainActivity.this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            mDevList.requestFocus();
        }
    }
}
