package cn.com.magnity.coresdksample.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.magnity.coresdksample.MyApplication;

import static cn.com.magnity.coresdksample.Config.DdnUpdateApkPath;
import static cn.com.magnity.coresdksample.Config.MAC_ADRESS;

//跟App相关的辅助类
public class AppUtils {
    private static final String TAG = "AppUtils";

    /**
     * 获取应用程序名称
     */
    public static synchronized String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回当前程序版本
     */
    public static String getAppVersion(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            //versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
        }
        return versionName;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取图标 bitmap
     *
     * @param context
     */
    public static synchronized Bitmap getBitmap(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext()
                    .getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        Drawable d = packageManager.getApplicationIcon(applicationInfo); //xxx根据自己的情况获取drawable
        BitmapDrawable bd = (BitmapDrawable) d;
        Bitmap bm = bd.getBitmap();
        return bm;
    }

    /**
     * 根据wifi信息获取本地mac
     *
     * @param context
     * @return
     */
    public static String getLocalMacAddressFromWifiInfo(Context context) {
        String mac=null;
        String mac_local_adress = PreferencesUtils.getString(MAC_ADRESS, "");
        Log.i(TAG, "mac_local_adress: "+mac_local_adress);
        //判断本地数据是否保存有，有的话就直接用
        if(!mac_local_adress.equals("02000000000")&&!mac_local_adress.equals("")){
            mac=PreferencesUtils.getString("MAC_ADRESS","");
            Log.i(TAG, "直接用mac: "+mac);
            return mac;
        }
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        Log.i(TAG, "wifi: "+wifi.toString());
        WifiInfo winfo = wifi.getConnectionInfo();
        Log.i(TAG, "winfo: "+winfo.toString());
        mac = winfo.getMacAddress();
        Log.i(TAG, "winfo.getMacAddress()"+winfo.getMacAddress());
        Log.i(TAG, "mac: "+mac.toString());
        //6.0以上获取mac地址
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mac = getMacFromHardware();
        }
        //不保留冒号
        if(mac.contains(":")){
            mac = mac.replace(":", "");
        }
        PreferencesUtils.put(MAC_ADRESS,mac);
        return mac;
    }

    /**
     * 根据wifi信息获取本地mac
     * 7.0权限
     *
     * @param
     * @return
     */
    public static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 执行具体的静默安装逻辑，需要手机ROOT。
     *
     * @param apkPath 要安装的apk文件的路径
     * @return 安装成功返回true，安装失败返回false。
     */
    public static boolean install(String apkPath) {
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            // 申请su权限
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            String command = "pm install -r " + apkPath + "\n";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = errorStream.readLine()) != null) {
                msg += line;
            }
            Log.i("TAG", "install msg is " + msg);
            // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
            if (!msg.contains("Failure")) {
                result = true;
                //安装成功后的操作

                //静态注册自启动广播
                Intent intent = new Intent();
                //与清单文件的receiver的anction对应
                intent.setAction("android.intent.action.PACKAGE_REPLACED");
                //发送广播
                MyApplication.getInstance().sendBroadcast(intent);
            }
        } catch (Exception e) {
            Log.e("TAG", e.getMessage(), e);
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                Log.e("TAG", e.getMessage(), e);
            }
        }
        return result;
    }

    /***
     * 删除已经安装的apk
     * */
    public static void rmoveApk() {

        File outputFile = new File(DdnUpdateApkPath);
        if (outputFile.exists()) {
            outputFile.delete();
            Log.i(TAG, "rmoveApk: ");
        }
    }

    /**
     * 获取下载的升级包包名
     *
     * @return
     */
    public static void getRemoveFile(String Rootpath) {
        List<File> packages = new ArrayList<>();
        FlieUtil.getFiles(new File(Rootpath), packages, new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().contains(".apk")) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        if (!packages.isEmpty()) {
            for (File aPackage : packages) {
                Log.i(TAG, "getRemoveFile: " + aPackage.getName().trim());
                aPackage.delete();
            }
        }

    }


}
