package cn.com.magnity.coresdksample;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cn.com.magnity.coresdksample.utils.AppUtils;
import cn.com.magnity.coresdksample.utils.TimeUitl;
import cn.com.magnity.coresdksample.websocket.bean.SoftWareVersionsInfo;

public class BaseActivity extends AppCompatActivity {
    private static  final  String TAG="BaseActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        SoftWareVersionsInfo();
    }
    //软件版本信息采集
    private void SoftWareVersionsInfo(){
        SoftWareVersionsInfo info = new SoftWareVersionsInfo();
        //获取app版本号
        info.setSoftware(AppUtils.getAppVersion(this));
        //获取android系统版本号
        info.setSystem(Build.VERSION.RELEASE);
        info.setTime(TimeUitl.getNowDate());
        info.setHardWareVersion(Build.DEVICE);
        //获取内核版本号
        info.upload();
        Log.i(TAG,
                "---------------------SoftWareVersionsInfo: " +
                        "\n" + info.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
