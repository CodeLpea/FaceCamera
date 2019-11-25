package cn.com.magnity.coresdksample.websocket.bean;

/**
 * Created by xiaoyuren on 2018/2/27.
 * 项目名称：didano-robot
 * 类描述：服务器推送消息格式
 * company：www.didano.cn
 * email：vin.qin@didano.cn
 * 创建时间：2018/2/27 18:02
 */

public class PushRequestInfo {

    /**
     * cmd : 101
     * depict : 通知终端更新学生列表
     */

    private String cmd;
    private String depict;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getDepict() {
        return depict;
    }

    public void setDepict(String depict) {
        this.depict = depict;
    }
}
