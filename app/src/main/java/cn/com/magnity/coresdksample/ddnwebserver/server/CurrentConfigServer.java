package cn.com.magnity.coresdksample.ddnwebserver.server;
import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;
import cn.com.magnity.coresdksample.ddnwebserver.model.CurrentSettingData;
import cn.com.magnity.coresdksample.utils.PreferencesUtils;


public class CurrentConfigServer {

    private static class InnerClass{
        private static CurrentConfigServer currentConfigServer=new CurrentConfigServer();
    }
    private CurrentConfigServer(){

    }
    /*静态内部类单例*/
    public static CurrentConfigServer getInstance(){
        return InnerClass.currentConfigServer;
    }
    private CurrentSettingData currentSettingData;

    private CurrentSettingData initData() {
        currentSettingData =new CurrentSettingData();
        currentSettingData.setDevice_no(PreferencesUtils.getString(WebConfig.DEVICE_NO,"DEVICE_NO"));
        currentSettingData.setWifi_name(PreferencesUtils.getString(WebConfig.WIFI_NAME,"WIFI_NAME"));
        currentSettingData.setWifi_passwd(PreferencesUtils.getString(WebConfig.WIFI_PASSWD,"WIFI_PASSWD"));
        currentSettingData.setCamera_explore(PreferencesUtils.getString(WebConfig.CAMERA_EXPLORE,"CAMERA_EXPLORE"));
        currentSettingData.setDistance(PreferencesUtils.getString(WebConfig.DISTANCE,"2.0"));
        currentSettingData.setError_voice(PreferencesUtils.getString(WebConfig.ERROR_VOICE,"10"));
        currentSettingData.setFFC_compensation_parameter(PreferencesUtils.getString(WebConfig.FFC_COMPENSATION_PARAMETER,"10"));
        currentSettingData.setFFC_calibration_parameter(PreferencesUtils.getString(WebConfig.FFC_CALIBRATION_PARAMETER,"10"));
        currentSettingData.setNormal_voice(PreferencesUtils.getString(WebConfig.NORMAL_VOICE,"10"));
        currentSettingData.setSystem_voice(PreferencesUtils.getString(WebConfig.SYSTEM_VOICE,"15"));
        currentSettingData.setTemperature_threshold(PreferencesUtils.getString(WebConfig.TEMPERATURE_THRESHOLD,"30"));
        currentSettingData.setVoice_speed(PreferencesUtils.getString(WebConfig.VOICE_SPEED,"1.0"));
        currentSettingData.setVersion_name(PreferencesUtils.getString(WebConfig.VERSION_NAME,"1.1.0"));
        currentSettingData.setMovex(PreferencesUtils.getString(WebConfig.MOVEX,"0"));
        currentSettingData.setMovey(PreferencesUtils.getString(WebConfig.MOVEY,"0"));
        currentSettingData.setScale(PreferencesUtils.getFloat(WebConfig.SCALE,1));
        currentSettingData.setLineUp(PreferencesUtils.getString(WebConfig.LINEUP,"20"));
        currentSettingData.setLineLeft(PreferencesUtils.getString(WebConfig.LINELEFT,"20"));
        currentSettingData.setLineDown(PreferencesUtils.getString(WebConfig.LINEDWON,"620"));
        currentSettingData.setLineRight(PreferencesUtils.getString(WebConfig.LINERIGHT,"450"));
        currentSettingData.toString();
        return currentSettingData;
    }
    public CurrentSettingData getSetting() {
        CurrentSettingData currentSettingData = initData();
        return currentSettingData;
    }
}
