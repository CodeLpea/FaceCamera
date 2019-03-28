package cn.com.magnity.coresdksample.Service;
import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import cn.com.magnity.coresdksample.MainActivity;
import cn.com.magnity.coresdksample.MyApplication;
import cn.com.magnity.coresdksample.utils.AppUtils;
import cn.com.magnity.coresdksample.utils.Config;
import cn.com.magnity.coresdksample.utils.PreferencesUtils;
import static cn.com.magnity.coresdksample.utils.Config.DdnPropertiesPath;
import static cn.com.magnity.coresdksample.utils.Config.DdnUpdateApkPath;
import static cn.com.magnity.coresdksample.utils.Config.InitLoadServieAction;
import static cn.com.magnity.coresdksample.utils.Config.MSG4;
import static cn.com.magnity.coresdksample.utils.Config.MSG7;
import static cn.com.magnity.coresdksample.utils.Config.MSG8;
import static cn.com.magnity.coresdksample.utils.Config.ReLoadServieAction;
import static cn.com.magnity.coresdksample.utils.FlieUtil.clearInfoForFile;
import static cn.com.magnity.coresdksample.utils.FlieUtil.isExistFlie;

/**
 * 开机后自动加载数据的服务
 */
public class LoadService extends IntentService {
    private static final String TAG="LoadService";


    private String [] MyPropertiesList=new String[]{"WifiName",
            "WifiPassWord",
            "CurrtentVoiceVolume",
            "NormolTempVoiceVolume",
            "HeightTempVoiceVolume",
            "TempThreshold",
            "ExploreValue",
            "DeviceName",
            "VersionName"
    };
    private List MyProperties_List = Arrays.asList(MyPropertiesList);//转换为List
    private  List<String> getPropertiesList=new ArrayList<>();
    private  List<String> RetainProperties;


    /**
     * 回调接口
     */
    public interface onLoadServiceListener{
        void setExplore(int values);//提示
    }

    public LoadService() {
        super("LoadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG, "onStart: ");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch (intent.getAction()){
            case InitLoadServieAction://第一次初始化时候，会全部进行
                Log.i(TAG, "第一次初始化: ");
                //读取设备信息
                LoadDevice();

                //加载温度人脸框微调信息
                LoadFacePlace();

                //加载配置文件的数据
                LoadConfigFlie(intent);
                break;
            case ReLoadServieAction://反复加载的时候，只读配置文件的变化
                Log.i(TAG, "反复加载配置文件: ");
                //加载配置文件的数据
                LoadConfigFlie(intent);
                UpadateApk();
                break;
        }



    }

    private void UpadateApk() {
        Log.i(TAG, "UpadateApkStart: ");
        File file = new File(DdnUpdateApkPath);
        if(file.exists()){//如果存在apk，则执行升级操作
            AppUtils.install(DdnUpdateApkPath);
            Log.i(TAG, "UpadateApkSuccess！！！！！！！！！ ");
        }
    }


    private void LoadDevice() {
       Config.DEVICENAME= AppUtils.getLocalMacAddressFromWifiInfo(this.getApplicationContext());//读取设备号
       Config.VERSIONNAME=AppUtils.getVersionName(this.getApplicationContext());//读取版本号
        Log.i(TAG, "LoadDevice.DEVICENAME "+ Config.DEVICENAME);
        Log.i(TAG, "LoadDevice.VERSIONNAME "+ Config.VERSIONNAME);

    }

    private void LoadFacePlace() {
        Config.XPalce=(Integer) PreferencesUtils.get(getApplicationContext(),Config.KeyXplace,-18);
        Config.YPalce=(Integer) PreferencesUtils.get(getApplicationContext(),Config.KeyYplace,6);
        Log.i(TAG, "LoadFacePlace: X  = "+Config.XPalce);
        Log.i(TAG, "LoadFacePlace: Y  = "+Config.YPalce);

    }

    private void LoadConfigFlie(Intent intent) {
        HashMap keyValueMap;
        Message message=Message.obtain();
        //进行加载任务
        Log.i(TAG, "--------------------------------进行加载数据----------------------------------");
        if (intent != null&&isExistFlie(DdnPropertiesPath)) {//如果文件夹存在才进行加载，否则为默认值
            try{
                keyValueMap=readKeyValueTxtToMap();//先读出配置文件
                if(Compare(keyValueMap))//比对一下配置文件是否正。
                {
                if(!intent.getAction().equals(ReLoadServieAction)){//反复加载数据的时候不播报语音
                message.what=MSG4;
                message.obj="读取配置文件成功";
                MainActivity.DelayStartHandler.sendMessageDelayed(message,7000);
                }
                }else{//配置文件读取失败，自动修复，提醒用户重填
                    message.what=MSG4;
                    message.obj="配置文件读取失败，已经自动修复，请检查";
                    MainActivity.DelayStartHandler.sendMessageDelayed(message,7000);
                }
            }catch (Exception e){
                e.printStackTrace();
                message.what=MSG4;
                message.obj="读取配置文件失败,请检查";
                MainActivity.DelayStartHandler.sendMessageDelayed(message,7000);
                //MyApplication.getInstance().ttsUtil.SpeechFlush("读取配置文件失败",8);
            }
        }else {//装载为默认值
        }
    }



/**
 * 读取本地txt
 * 存放各位hashmap键值对
 * */
    private HashMap readKeyValueTxtToMap() {
        //循环直至返回map
        final HashMap keyValueMap = new HashMap();//保存读取数据keyValueMap
        //每一个循环读取一组key=value
        while (true) {
            try {
               // final InputStream open = this.getAssets().open("key_value.txt");
                File f=new File(DdnPropertiesPath);
                final InputStream open =new FileInputStream(f);
                final byte[] readArray = new byte[open.available()];
                open.read(readArray);
                open.close();
                final StringTokenizer allLine = new StringTokenizer(new String(readArray, "UTF-8"), "\r\n");//以"\r\n"作为key=value的分解标志
                while (allLine.hasMoreTokens()) {
                    final StringTokenizer oneLine = new StringTokenizer(allLine.nextToken(), "=");//以"="作为分解标志
                    final String leftKey = oneLine.nextToken();//读取第一个字符串key
                    if (!oneLine.hasMoreTokens()) {
                        break;
                    }
                    final String rightValue = oneLine.nextToken();//读取第二个字符串value
                    keyValueMap.put(leftKey, rightValue);
                    Log.i(TAG, "Key:    "+leftKey);
                    getPropertiesList.add(leftKey);//添加到读取的配置文件集合中，准备比对，是否缺省或者错误
                    Log.i(TAG, "Value:  --------------------------"+rightValue);
                }
                return keyValueMap;
            } catch (IOException e) {
                e.printStackTrace();
                return keyValueMap;
            }
        }

    }


    /**
     * 对比读取到的配置文件键的值，是否正确
     * 判断读取到的配置文件格式和个数是否正确
     * 如有错误，清空文件，并将缓存的配置写入，进行修复
     * @param keyValueMap
     */
    private boolean Compare(HashMap keyValueMap){
        boolean flag=false;
        boolean isSame=true;
        boolean isUpdate=true;

    // 求差集：结果
    Collection MyPropertiesCollection = new ArrayList(MyProperties_List);//默认配置key的集合

    Collection getPropertiesCollectionretainAll = new ArrayList(getPropertiesList);//读取到的key转化为Collection准备求交集。

    Collection getPropertiesCollectionRemoveAll = new ArrayList(getPropertiesList);//读取到的key转化为Collection准备求差集。

    // 求交集
    MyPropertiesCollection.retainAll(getPropertiesCollectionretainAll);
    RetainProperties= (List<String>) MyPropertiesCollection;//将交集转换为List集合，方便读取
    System.out.println("交集结果：" + RetainProperties.size());//如果交集等于默认配置参数个数，则表示配置文件成功读取到所有key。
        for(String propertiesKey :RetainProperties){
            switch (propertiesKey){
                case  "WifiName" :
                    Config.WifiName=keyValueMap.get(propertiesKey).toString();
                    Log.i(TAG, "WifiName: "+  Config.WifiName);
                    break;
                case  "WifiPassWord" :
                    Config.WifiPassWord=keyValueMap.get(propertiesKey).toString();
                    Log.i(TAG, "WifiPassWord: "+  Config.WifiPassWord);
                    break;
                case  "CurrtentVoiceVolume" :
                    Config.currtentVoiceVolume= Integer.parseInt(keyValueMap.get(propertiesKey).toString());
                    Log.i(TAG, "currtentVoiceVolume: "+  Config.currtentVoiceVolume);
                    break;
                 case  "NormolTempVoiceVolume" :
                    Config.normolTempVoiceVolume= Integer.parseInt(keyValueMap.get(propertiesKey).toString());
                     Log.i(TAG, "normolTempVoiceVolume: "+  Config.normolTempVoiceVolume);
                    break;
                  case  "HeightTempVoiceVolume" :
                    Config.heightTempVoiceVolume= Integer.parseInt(keyValueMap.get(propertiesKey).toString());
                      Log.i(TAG, "heightTempVoiceVolume: "+  Config.heightTempVoiceVolume);
                    break;
                case  "TempThreshold" :
                    Config.TempThreshold= Float.parseFloat(keyValueMap.get(propertiesKey).toString());
                    Config.DefaultTempThreshold=Config.TempThreshold;
                    Log.i(TAG, "TempThreshold: "+  Config.TempThreshold);
                    break;
                case  "ExploreValue" :
                    Config.ExploreValue= Integer.parseInt(keyValueMap.get(propertiesKey).toString());
                    MainActivity.ReloadServiceHandler.sendEmptyMessage(MSG7);//进行亮度设置。
                    Log.i(TAG, "ExploreValue: "+  Config.ExploreValue);
                    break;
                case  "DeviceName" :
                    isSame=Config.DEVICENAME.equals(keyValueMap.get(propertiesKey).toString());//如果读取到的设备号与缓存的设备号不同，则需要修复
                    Log.i(TAG, "DeviceName: "+  Config.DEVICENAME);
                    break;
                case  "VersionName" :
                    isUpdate=Config.VERSIONNAME.equals(keyValueMap.get(propertiesKey).toString());//如果读取到的版本号与缓存的版本号不同，则表明已经升级
                    if(!isUpdate){//
                        Message message=Message.obtain();
                        message.what=MSG8;
                        message.obj="已升级到新版本";
                        MainActivity.DelayStartHandler.sendMessageDelayed(message,2000);
                    }
                    Log.i(TAG, "VERSIONNAME: "+  Config.VERSIONNAME);
                    break;
            }
        }

   /* //求差集
    getPropertiesCollectionRemoveAll.removeAll(MyPropertiesCollection);
    System.out.println("差集结果：" + getPropertiesCollectionRemoveAll);*/
    if(RetainProperties.size()!=MyProperties_List.size()||getPropertiesList.size()!=MyProperties_List.size()||!isSame){
        //如果交集个数不匹配，或者读出来的配置文件key个数与默认的不符合，或者设备号或者版本号被人为修改
        // 则清空配置文件，并重新填入读取到的正确的配置文件的缓存值。
        if(clearInfoForFile(DdnPropertiesPath)){   //清空文件
            writeTxtToFile();
        }
    }else {//完全符合,则返回ture，进行正常播报
        if(!isUpdate){//读取到新版本就应该更新配置文件
            if(clearInfoForFile(DdnPropertiesPath)){   //清空文件
                Log.i(TAG, "读取到新版本，更新配置文件: ");
                writeTxtToFile();
            }
        }
        flag=true;
    }

    return flag;
}


    /**
     *
     * 将字符串写入到文本文件中
     * 追加
     * 每次修复只能调用一次，避免重复调用
     */
    private void writeTxtToFile() {
        try {
        isExistFlie(DdnPropertiesPath);
        String strContent=
                "WifiName"+"="+Config.WifiName+ "\r\n"
                +"WifiPassWord"+"="+Config.WifiPassWord+ "\r\n"
                +"CurrtentVoiceVolume"+"="+Config.currtentVoiceVolume+ "\r\n"
                +"NormolTempVoiceVolume"+"="+Config.normolTempVoiceVolume+ "\r\n"
                +"HeightTempVoiceVolume"+"="+Config.heightTempVoiceVolume+ "\r\n"
                +"TempThreshold"+"="+Config.TempThreshold+ "\r\n"
                +"ExploreValue"+"="+Config.ExploreValue+ "\r\n"
                +"DeviceName"+"="+Config.DEVICENAME+ "\r\n"
                +"VersionName"+"="+Config.VERSIONNAME+ "\r\n";
        strContent.getBytes();
        strContent=new String(strContent.getBytes(),"GBK");
            File file =new File(DdnPropertiesPath);
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }


}
