package cn.com.magnity.coresdksample.websocket;


import cn.com.magnity.coresdksample.MyApplication;
import cn.com.magnity.coresdksample.utils.AppUtils;

/**
 * Created by xiaoyuren on 2018/2/27.
 * 项目名称：didano-robot
 * 类描述：
 * company：www.didano.cn
 * email：vin.qin@didano.cn
 * 创建时间：2018/2/27 14:01
 */

public interface WSConstant {
    //        String BASE_API = InvisibleConfig.getConfig(InvisibleConfigKey.REMOTE_CTROL_SERVER) + "/" + AppUtils.getLocalMacAddressFromWifiInfo(Myapplication.getInstance());
//String BASE_API = "http://192.168.4.190:8555/robot/api/ws"+ "/" + AppUtils.getLocalMacAddressFromWifiInfo(MyApplication.getInstance());
    String BASE_API = "ws://119.23.152.164:8555/robot/api/ws" + "/" + AppUtils.getLocalMacAddressFromWifiInfo(MyApplication.getInstance());

    interface CMD_TYPE {
        String UPDATE_STUDENT_LIST = "101";
        String UPDATE_SOFTWARE_VERSION = "102";
        String REBOOT_SYSTEM = "103";
        String DOWNLOAD_LEAVE_MESSAGE = "104";
        String SYNCTHING_REPAIRING = "105";//控制syncthingandroid重新配对
    }

    /**
     * 采集的数据的类型区分,MethodName
     */
    interface RemoteLocationConstant {
        //数据结构类型
        public static final String JUGEINFORMATION = "jugeInformation";
        public static final String JUGERUNINFO = "jugeRunInfo";

    }

}
