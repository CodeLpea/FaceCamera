package cn.com.magnity.coresdksample;

import android.os.Handler;
import android.os.Message;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import cn.com.magnity.coresdk.MagDevice;
import cn.com.magnity.coresdk.types.EnumInfo;
import cn.com.magnity.coresdksample.surview.MagSurfaceView;

import static cn.com.magnity.coresdksample.MyApplication.mDev;


public class MagBaseActivity extends BaseActivity {
    private static final String TAG = "MagBaseActivity";
    private static final int START_TIMER_ID = 101;
    private static final int TRANS_TIMER_ID = 102;
    private static final int TIMER_INTERVAL = 500 * 2;//ms
    private ArrayList<EnumInfo> mDevices;
    private DevicesHandler devicesHandler;


    private MagSurfaceView magSurfaceView;

    protected void setMagSurfaceView(MagSurfaceView magSurfaceView) {
        this.magSurfaceView = magSurfaceView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        devicesHandler = new DevicesHandler();
        devicesHandler.sendEmptyMessageDelayed(START_TIMER_ID, TIMER_INTERVAL);
        /* global init */
        MagDevice.init(this);
        /* new object */

        mDevices = new ArrayList<>();

    }


    private void updateDeviceList() {
        MagDevice.getDevices(this, 33596, 1, mDevices);
        if (mDevices.size() > 0) {
            int linkCamera = mDev.linkCamera(this, mDevices.get(0).id, new MagDevice.ILinkCallback() {
                @Override
                public void linkResult(int result) {
                    Log.i(TAG, "linkResult: " + result);
                    if (result != MagDevice.CONN_SUCC) {
                        devicesHandler.sendEmptyMessageDelayed(START_TIMER_ID, TIMER_INTERVAL);
                    }
                }
            });
            if (linkCamera == MagDevice.CONN_SUCC) {
                Log.e(TAG, "linkCamera: " + linkCamera);
                if (mDev.startProcessImage(magSurfaceView, 0, 0)) {
                    magSurfaceView.startDrawingThread(mDev);
                    devicesHandler.sendEmptyMessageDelayed(TRANS_TIMER_ID, TIMER_INTERVAL * 2);
                }
            }

        } else {
            devicesHandler.sendEmptyMessageDelayed(START_TIMER_ID, TIMER_INTERVAL);
        }
        mDevices.clear();
    }


    private class DevicesHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_TIMER_ID:
                    if (!mDev.isLinked() && magSurfaceView != null) {
                        Log.i(TAG, "updateDeviceList: ");
                        updateDeviceList();
                    }
                    break;
                case TRANS_TIMER_ID:
                    mDev.stopProcessImage();
                    magSurfaceView.stopDrawingThread();
                    mDev.setImageTransform(0, 3);
                    if (mDev.startProcessImage(magSurfaceView, 0, 0)) {
                        magSurfaceView.startDrawingThread(mDev);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /* disconnect camera when app exited */
        if (mDev.isProcessingImage()) {
            mDev.stopProcessImage();
            magSurfaceView.stopDrawingThread();
        }
        if (mDev.isLinked()) {
            mDev.dislinkCamera();
        }
        mDev=null;
    }
}
