package cn.com.magnity.coresdksample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.com.magnity.coresdk.MagDevice;
import cn.com.magnity.coresdksample.View.MagSurfaceView;

public class VideoFragment extends Fragment implements MagDevice.INewFrameCallback {
/*    private MagSurfaceView mView;*/
    private static final String TAG="VideoFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        Log.i(TAG, "onCreateView: ");
        View root = inflater.inflate(R.layout.fragment_video, container, false);
        MyApplication.getInstance().mView = (MagSurfaceView)root.findViewById(R.id.video);
        return root;
    }

    @Override
    public void newFrame(int cameraState, int streamType) {
/* notify drawing image */
        if (MyApplication.getInstance().mView != null) {
            MyApplication.getInstance().mView.invalidate_();
        }
    }

    public void startDrawingThread(MagDevice dev) {
        if ( MyApplication.getInstance().mView != null) {
            Log.i(TAG, "startDrawingThread: ");
            MyApplication.getInstance().mView.startDrawingThread(dev);
        }
    }

    public void stopDrawingThread() {
        if (MyApplication.getInstance().mView != null) {
            Log.i(TAG, "stopDrawingThread: ");
            MyApplication.getInstance().mView.stopDrawingThread();
        }
    }
}
