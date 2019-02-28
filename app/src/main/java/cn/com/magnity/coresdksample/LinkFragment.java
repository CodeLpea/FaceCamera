package cn.com.magnity.coresdksample;



import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;


import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static cn.com.magnity.coresdksample.MyApplication.mDev;
import static cn.com.magnity.coresdksample.utils.Config.SavaRootDirName;


public class LinkFragment extends Fragment {
    private static final String TAG="LinkFragment";
    // TODO: Rename parameter arguments, choose names that match
    private View view;


    //non-const
    //private MagDevice mDev;
    private int mDegree;//0 - 90, 1 - 180, 2 - 270
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
    private VideoFragment mVideoFragment;
    public LinkFragment() {
        // Required empty public constructor
    }
    public void setmVideoFragment(VideoFragment mVideoFragment) {
        this.mVideoFragment = mVideoFragment;
    }
    public void setmDevices(ArrayList<EnumInfo> mDevices) {
        this.mDevices = mDevices;
    }

    public void setmDeviceStrings(ArrayList<String> mDeviceStrings) {
        this.mDeviceStrings = mDeviceStrings;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_link, container, false);
        initUi();
        return view;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "onAttach: ");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach: ");
    }

    private void initUi() {
        /* new object */
       // mDev = new MagDevice();
        mDevices = new ArrayList<>();
        mDeviceStrings = new ArrayList<>();
        mListAdapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_expandable_list_item_1, mDeviceStrings);
        mDevList = (ListView)view.findViewById(R.id.listDev);
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
        mLinkBtn = (Button)view.findViewById(R.id.btnLink);
        mLinkBtn.setOnClickListener(listener);
        mPlayBtn = (Button)view.findViewById(R.id.btnPlay);
        mPlayBtn.setOnClickListener(listener);
        mStopBtn = (Button)view.findViewById(R.id.btnStop);
        mStopBtn.setOnClickListener(listener);
        mDislinkBtn = (Button)view.findViewById(R.id.btnDislink);
        mDislinkBtn.setOnClickListener(listener);
        mRotateBtn = (Button)view.findViewById(R.id.btnRotate);
        mRotateBtn.setOnClickListener(listener);
        mSavePicBtn = (Button)view.findViewById(R.id.btnSavePic);
        mSavePicBtn.setOnClickListener(listener);
        mTextSelectedDevice = (TextView)view.findViewById(R.id.tvSelectedName);

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

    public void autoConnect() {
        //直接连接link已经扫描到的设备
        if (mDevices!=null&&mDevices.size()>0){
            EnumInfo dev = mDevices.get(0);
            if (!mDev.isLinked()) {//判断是否已经连接
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


    private class MagOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btnLink:
                    mDev.linkCamera(view.getContext(), mSelectedDev.id, new MagDevice.ILinkCallback() {
                        @Override
                        public void linkResult(int i) {
                            Log.i(TAG, "linkResult: "+i);
                        }
                    });
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
                    MyApplication.istaken=true;
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
                        Toast.makeText(view.getContext(), file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            mDevList.requestFocus();
        }
    }

    private void Link() {
        Toast.makeText(view.getContext(),"开启播放：",Toast.LENGTH_SHORT).show();
        int r = mDev.linkCamera(view.getContext(), mSelectedDev.id,
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    play();
                }
            });
        }
        updateButtons();
    }
    private void play() {
        mDev.setColorPalette(MagDevice.ColorPalette.PaletteIronBow);
        if (mDev.startProcessImage(mVideoFragment, 0, 0)) {
            mVideoFragment.startDrawingThread(mDev);
        }
    }
}
