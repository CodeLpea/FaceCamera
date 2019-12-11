package cn.com.magnity.coresdksample.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.com.magnity.coresdksample.MainActivity;
import cn.com.magnity.coresdksample.utils.AppUtils;

import static cn.com.magnity.coresdksample.Config.DdnDownLoadApkPath;

/**
 * 更新后自启动
 * */
public class UpdateRestartReceiver extends BroadcastReceiver {
    private static final  String  TAG="UpdateRestartReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")){
            //Toast.makeText(context,"已升级到新版本",Toast.LENGTH_SHORT).show();
            Log.i(TAG, "已升级到新版本: ");
            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
            AppUtils.rmoveApk();//移除apk
            AppUtils.getRemoveFile(DdnDownLoadApkPath);

        }
    }
}
