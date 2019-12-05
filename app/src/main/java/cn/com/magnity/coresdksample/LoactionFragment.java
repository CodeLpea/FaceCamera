package cn.com.magnity.coresdksample;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cn.com.magnity.coresdksample.utils.Config;
import cn.com.magnity.coresdksample.utils.PreferencesUtils;


public class LoactionFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "LoactionFragment";

    private Button bt_up,bt_left,bt_right,bt_bottom,bt_sava;


    public LoactionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_loaction, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        bt_up=(Button) view.findViewById(R.id.bt_location_up);
        bt_left=(Button) view.findViewById(R.id.bt_location_left);
        bt_right=(Button) view.findViewById(R.id.bt_location_right);
        bt_bottom=(Button) view.findViewById(R.id.bt_location_bottom);
        bt_sava=(Button) view.findViewById(R.id.bt_location_save);
        bt_up.setOnClickListener(this);
        bt_left.setOnClickListener(this);
        bt_right.setOnClickListener(this);
        bt_bottom.setOnClickListener(this);
        bt_sava.setOnClickListener(this);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "onAttach: ");

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_location_up:
                Log.i(TAG, "上移动: ");
                Config.YPalce=Config.YPalce+1;
                break;
            case R.id.bt_location_bottom:
                Config.YPalce=Config.YPalce-1;
                Log.i(TAG, "下移动: ");
                break;
            case R.id.bt_location_left:
                Config.XPalce=Config.XPalce-1;
                Log.i(TAG, "左移动: ");
                break;
            case R.id.bt_location_right:
                Config.XPalce=Config.XPalce+1;
                Log.i(TAG, "右移动: ");
                break;
            case R.id.bt_location_save:
                PreferencesUtils.put(Config.KeyXplace,Config.XPalce);
                PreferencesUtils.put(Config.KeyYplace,Config.YPalce);
                Log.i(TAG, "保存，成功");
                break;


        }

    }
}
