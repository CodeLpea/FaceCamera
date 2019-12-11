package cn.com.magnity.coresdksample;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;
import cn.com.magnity.coresdksample.usecache.CurrentConfig;
import cn.com.magnity.coresdksample.utils.PreferencesUtils;



public class AreaFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "AreaFragment";
    private Button bt_up_line,bt_left_line,bt_right_line,bt_bottom_line,bt_up,bt_left,bt_right,bt_bottom,bt_sava;
    private TextView tv_currentLine;
    private int WhichLine=1;//用于区分当前是哪个线条在调整，上：1   左：2    右：3   下：4
    public AreaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_area, container, false);
        initView(view);

        return view;
    }
    private void initView(View view) {
        //线条选择
        bt_up_line=(Button) view.findViewById(R.id.bt_area_up_line);
        bt_left_line=(Button) view.findViewById(R.id.bt_area_left_line);
        bt_right_line=(Button) view.findViewById(R.id.bt_area_right_line);
        bt_bottom_line=(Button) view.findViewById(R.id.bt_area_bottom_line);
        //线条移动
        bt_up=(Button) view.findViewById(R.id.bt_area_up);
        bt_left=(Button) view.findViewById(R.id.bt_area_left);
        bt_right=(Button) view.findViewById(R.id.bt_area_right);
        bt_bottom=(Button) view.findViewById(R.id.bt_area_bottom);
        //保存
        bt_sava=(Button) view.findViewById(R.id.bt_area_save);

        //显示当前线条
        tv_currentLine=(TextView)view.findViewById(R.id.tv_choose_line);

        //监听点击
        bt_up_line.setOnClickListener(this);
        bt_left_line.setOnClickListener(this);
        bt_right_line.setOnClickListener(this);
        bt_bottom_line.setOnClickListener(this);

        bt_up.setOnClickListener(this);
        bt_left.setOnClickListener(this);
        bt_right.setOnClickListener(this);
        bt_bottom.setOnClickListener(this);
        bt_sava.setOnClickListener(this);

        hideBt();//先隐藏所有调整按钮


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
            case R.id.bt_area_up_line://上线条
                tv_currentLine.setText("当前调整：上方线条");
                WhichLine=1;
                //则只能调整上下：
                showUB();
                break;
            case R.id.bt_area_left_line://左线条
                tv_currentLine.setText("当前调整：左侧线条");
                WhichLine=2;
                //则只能调整左右：
                 showLR();
                break;
            case R.id.bt_area_right_line://右线条
                tv_currentLine.setText("当前调整：右侧线条");
                WhichLine=3;
                //则只能调整左右：
                showLR();
                break;
            case R.id.bt_area_bottom_line://下线条
                tv_currentLine.setText("当前调整：下方线条");
                WhichLine=4;
                //则只能调整上下：
                showUB();
                break;

                //开始调整：
            case R.id.bt_area_up://向上调整（只能用于上下方的线条）
                switch (WhichLine){
                    case 1://1为上方的线条
                        if(CurrentConfig.getInstance().getCurrentData().getLineUp()>10){
                            //点击一下，就加1
                            PreferencesUtils.put(WebConfig.LINEUP,  CurrentConfig.getInstance().getCurrentData().getLineUp()-10);
                            CurrentConfig.getInstance().updateSetting();
                        }else {
                            Toast.makeText(getActivity(),"已到达最顶部",Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case 4://4为下方的线条
                        if(CurrentConfig.getInstance().getCurrentData().getLineDown()>10){
                            //点击一下，就加1
                            PreferencesUtils.put(WebConfig.LINEDWON,  CurrentConfig.getInstance().getCurrentData().getLineDown()-10);
                            CurrentConfig.getInstance().updateSetting();
                        }else {
                            Toast.makeText(getActivity(),"已到达最顶部",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

                break;
            case R.id.bt_area_left://左侧调整（只能用于左右的线条）
                switch (WhichLine){
                    case 2://2为左边方的线条
                        Log.i(TAG, "AreaLeftBefore: "+CurrentConfig.getInstance().getCurrentData().getLineLeft());
                        if(CurrentConfig.getInstance().getCurrentData().getLineLeft()>10){
                           //点击一下，就加1
                            PreferencesUtils.put(WebConfig.LINELEFT,  CurrentConfig.getInstance().getCurrentData().getLineLeft()-10);
                            CurrentConfig.getInstance().updateSetting();
                            Log.i(TAG, "AreaLeftAfter: "+CurrentConfig.getInstance().getCurrentData().getLineLeft());
                        }else {
                            Toast.makeText(getActivity(),"已到达最左侧",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 3://3为右方的线条
                        Log.i(TAG, "AreaRightBefore: "+CurrentConfig.getInstance().getCurrentData().getLineRight());
                        if(CurrentConfig.getInstance().getCurrentData().getLineRight()>10){
                           //点击一下，就加1
                            PreferencesUtils.put(WebConfig.LINERIGHT,  CurrentConfig.getInstance().getCurrentData().getLineRight()-10);
                            CurrentConfig.getInstance().updateSetting();
                            Log.i(TAG, "AreaRightAfter: "+CurrentConfig.getInstance().getCurrentData().getLineRight());
                        }else {
                            Toast.makeText(getActivity(),"已到达最左侧",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                break;
            case R.id.bt_area_right://右侧调整（只能用于左右的线条）
                switch (WhichLine){
                    case 2://2为左边方的线条
                        if(CurrentConfig.getInstance().getCurrentData().getLineLeft()<470){
                          //点击一下，就加1
                            PreferencesUtils.put(WebConfig.LINELEFT,  CurrentConfig.getInstance().getCurrentData().getLineLeft()+10);
                            CurrentConfig.getInstance().updateSetting();
                        }else {
                            Toast.makeText(getActivity(),"已到达最右侧",Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case 3://3为右方的线条
                        if(CurrentConfig.getInstance().getCurrentData().getLineRight()<470){
                           //点击一下，就加1
                            PreferencesUtils.put(WebConfig.LINERIGHT,  CurrentConfig.getInstance().getCurrentData().getLineRight()+10);
                            CurrentConfig.getInstance().updateSetting();
                        }else {
                            Toast.makeText(getActivity(),"已到达最右侧",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

                break;
            case R.id.bt_area_bottom://向下调整（只能用于上下方的线条）
                switch (WhichLine){
                    case 1://1为上方的线条
                        if(CurrentConfig.getInstance().getCurrentData().getLineUp()<630){
                           //点击一下，就加1
                            PreferencesUtils.put(WebConfig.LINEUP,  CurrentConfig.getInstance().getCurrentData().getLineUp()+10);
                            CurrentConfig.getInstance().updateSetting();
                        }else {
                            Toast.makeText(getActivity(),"已到达最底部",Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case 4://4为下方的线条
                        if(CurrentConfig.getInstance().getCurrentData().getLineDown()<630){
                            //点击一下，就加1
                            PreferencesUtils.put(WebConfig.LINEDWON,  CurrentConfig.getInstance().getCurrentData().getLineDown()+10);
                            CurrentConfig.getInstance().updateSetting();
                        }else {
                            Toast.makeText(getActivity(),"已到达最底部",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

                break;

                //保存
            case R.id.bt_area_save:
                CurrentConfig.getInstance().updateSetting();
                Toast.makeText(getActivity(),"已经保存成功",Toast.LENGTH_SHORT).show();
                break;







        }
    }


    /**
     * 只展示左右的调整按钮
     * */
    private void showLR(){
        bt_up.setVisibility(View.INVISIBLE);
        bt_bottom.setVisibility(View.INVISIBLE);
        bt_left.setVisibility(View.VISIBLE);
        bt_right.setVisibility(View.VISIBLE);
    }
    /**
     * 只展示上下的调整按钮
     * */
    private void showUB(){
        bt_up.setVisibility(View.VISIBLE);
        bt_bottom.setVisibility(View.VISIBLE);
        bt_left.setVisibility(View.INVISIBLE);
        bt_right.setVisibility(View.INVISIBLE);
    }
    /**
     * 隐藏所有的调整按钮
     * */
    private void hideBt(){
        bt_up.setVisibility(View.INVISIBLE);
        bt_bottom.setVisibility(View.INVISIBLE);
        bt_left.setVisibility(View.INVISIBLE);
        bt_right.setVisibility(View.INVISIBLE);
    }

}
