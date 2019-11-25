package cn.com.magnity.coresdksample.websocket.bean;



import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.com.magnity.coresdksample.MyApplication;
import cn.com.magnity.coresdksample.utils.AppUtils;
import cn.com.magnity.coresdksample.websocket.LocationDataCacher;

import static cn.com.magnity.coresdksample.utils.Config.SOFTWARE_SYSTEM_TYTE;


/**
 * 其他采集数据结构必须继承该抽象类
 * Created by Long on 2018/3/6.
 */

public abstract class AbstractLocatePackage {
    protected transient String methodName; //使用transient修饰，这样gson就不会其当做json字段来解析
    protected String getMethodName() {
        return methodName;
    }
    public void upload(){
        //生成json包
        RemoteLocatePackage locatePackage = new RemoteLocatePackage();
        locatePackage.setMethodName(this.getMethodName());
        locatePackage.setDeviceNo(AppUtils.getLocalMacAddressFromWifiInfo(MyApplication.getInstance()));
        locatePackage.setSystemType(String.valueOf(SOFTWARE_SYSTEM_TYTE));
        //将采集数据对象转化成JsonObject对象
        Gson payload = new Gson();
        JsonObject object = new JsonParser().parse(payload.toJson(this)).getAsJsonObject();
        locatePackage.setPlayload(object);
        //将与服务器通信的远程定位json包转成json字符串，存储到队列中
        String msg = new Gson().toJson(locatePackage);
        LocationDataCacher.getInstance().PushCacher(msg);
    }
}
