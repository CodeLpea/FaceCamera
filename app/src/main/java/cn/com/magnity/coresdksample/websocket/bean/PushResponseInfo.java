package cn.com.magnity.coresdksample.websocket.bean;

/**
 * Created by xiaoyuren on 2018/2/27.
 * 项目名称：didano-robot
 * 类描述：回复服务器推送消息格式
 * company：www.didano.cn
 * email：vin.qin@didano.cn
 * 创建时间：2018/2/27 18:06
 */

public class PushResponseInfo {

    /**
     * cmd : 101
     */

    private String deviceNo;
    private String cmd;

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return "PushResponseInfo{" +
                "deviceNo='" + deviceNo + '\'' +
                ", cmd='" + cmd + '\'' +
                '}';
    }
}
