package cn.com.magnity.coresdksample.websocket;

;

/**
 * Created by xiaoyuren on 2018/2/27.
 * 项目名称：didano-robot
 * 类描述：处理websocket消息
 * company：www.didano.cn
 * email：vin.qin@didano.cn
 * 创建时间：2018/2/27 16:02
 */

public class WSThread extends Thread {
    private boolean isThreadLoop = false;

    /**
     * 初始化工作
     *
     * @return
     */
    public boolean init() {
        return WSManager.getInstance().init() ? true : false;
    }

    /**
     * 开启线程
     *
     * @return 成功 true 失败 false
     */
    public boolean startThread() {
        boolean ret = false;
        ret = init();
        if (!ret) {
            return ret;
        }
        isThreadLoop = true;
        start();
        return true;
    }

    /**
     * 关闭线程
     *
     * @return 成功 true 失败 false
     */
    public boolean stopThread() {
        isThreadLoop = false;
        interrupt();
        return true;
    }


    @Override
    public void run() {
        while (isThreadLoop) {
            TimeDelay(10 * 1000);
            //检查是否连接上服务器
            while (WSManager.getInstance().getWsStatus() != WSStatus.CONNECT_SUCCESS) {
                TimeDelay(1000);
//                    Logger.i("====wait==" + WSManager.getInstance().getWsStatus());
            }
            //检查是否有数据要上传
            String msg = LocationDataCacher.getInstance().PopFromCacher(2 * 1000);
            if (msg != null) {
//                   Logger.i("send count ...." + (++sendCount));
                WSManager.getInstance().send(msg);
                //  Log.i(TAG, "msg : " + msg);
            } else {
                TimeDelay(100);
            }
        }
    }

    private void TimeDelay(int times) {
        try {
            Thread.sleep(times);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
