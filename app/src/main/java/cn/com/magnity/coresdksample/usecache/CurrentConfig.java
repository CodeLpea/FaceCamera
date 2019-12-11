package cn.com.magnity.coresdksample.usecache;
import cn.com.magnity.coresdksample.MyApplication;
import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;
import cn.com.magnity.coresdksample.ddnwebserver.model.CurrentSettingData;
import cn.com.magnity.coresdksample.utils.AppUtils;
import cn.com.magnity.coresdksample.utils.PreferencesUtils;


/**
 * 配置缓存
 * 避免反复读取SP
 * */
public class CurrentConfig {

    private static class InnerClass{
        private static CurrentConfig currentConfig =new CurrentConfig();
    }
    private CurrentConfig(){
        currentSettingData =new CurrentSettingData();
    }
    /*静态内部类单例*/
    public static CurrentConfig getInstance(){
        return InnerClass.currentConfig;
    }
    private CurrentSettingData currentSettingData;

    private CurrentSettingData updateData() {
        currentSettingData.setDevice_no(PreferencesUtils.getString(WebConfig.DEVICE_NO, AppUtils.getLocalMacAddressFromWifiInfo(MyApplication.getInstance())));
        currentSettingData.setWifi_name(PreferencesUtils.getString(WebConfig.WIFI_NAME,"XiaoNuo2018"));
        currentSettingData.setWifi_passwd(PreferencesUtils.getString(WebConfig.WIFI_PASSWD,"12345678"));
        currentSettingData.setCamera_explore(PreferencesUtils.getInt(WebConfig.CAMERA_EXPLORE,1));
        currentSettingData.setDistance(PreferencesUtils.getFloat(WebConfig.DISTANCE,1));
        currentSettingData.setError_voice(PreferencesUtils.getInt(WebConfig.ERROR_VOICE,10));
        currentSettingData.setFFC_compensation_parameter(PreferencesUtils.getFloat(WebConfig.FFC_COMPENSATION_PARAMETER,0));
        currentSettingData.setFFC_calibration_parameter(PreferencesUtils.getFloat(WebConfig.FFC_CALIBRATION_PARAMETER,0));
        currentSettingData.setNormal_voice(PreferencesUtils.getInt(WebConfig.NORMAL_VOICE,10));
        currentSettingData.setSystem_voice(PreferencesUtils.getInt(WebConfig.SYSTEM_VOICE,15));
        currentSettingData.setTemperature_threshold(PreferencesUtils.getFloat(WebConfig.TEMPERATURE_THRESHOLD,30));
        currentSettingData.setVoice_speed(PreferencesUtils.getFloat(WebConfig.VOICE_SPEED,1));
        currentSettingData.setVersion_name(PreferencesUtils.getString(WebConfig.VERSION_NAME,AppUtils.getVersionName(MyApplication.getInstance())));
        currentSettingData.setMovex(PreferencesUtils.getInt(WebConfig.MOVEX,0));
        currentSettingData.setMovey(PreferencesUtils.getInt(WebConfig.MOVEY,0));
        currentSettingData.setScale(PreferencesUtils.getFloat(WebConfig.SCALE,1));
        currentSettingData.setLineUp(PreferencesUtils.getInt(WebConfig.LINEUP,45));
        currentSettingData.setLineLeft(PreferencesUtils.getInt(WebConfig.LINELEFT,114));
        currentSettingData.setLineDown(PreferencesUtils.getInt(WebConfig.LINEDWON,448));
        currentSettingData.setLineRight(PreferencesUtils.getInt(WebConfig.LINERIGHT,576));
        currentSettingData.toString();
        return currentSettingData;
    }
    /**
     * 保存最新的信息
     * */
    public CurrentSettingData updateSetting() {
        CurrentSettingData currentSettingData = updateData();
        return currentSettingData;
    }
    /**
     * 获取最新的信息
     * */
    public CurrentSettingData getCurrentData() {
        return this.currentSettingData;
    }
}
