package cn.com.magnity.coresdksample.websocket;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 缓存采集到的数据，使远程定位于服务器通信线程达到异步处理
 * Created by Long on 2018/3/5.
 */

public class LocationDataCacher {
    private static final String TAG = "LocationDataCacher";
    private static BlockingQueue<String> mQueue = null;
    private static int QUEUE_SIZE = 200;
    private LocationDataCacher() {
        mQueue = new ArrayBlockingQueue<String>(QUEUE_SIZE);
    }

    private static class LocationDataCacherInner{
        private static LocationDataCacher mLocationDataCacher = new LocationDataCacher();
    }

    public static LocationDataCacher getInstance(){
        return LocationDataCacherInner.mLocationDataCacher;
    }

    public static void PushCacher(String json){
          // Logger.i(TAG + " write : " +json + " size :" + mQueue.size());
            try{
                if(mQueue.size() < QUEUE_SIZE){
                    mQueue.add(json);
                }
            }catch(Exception e){
                e.printStackTrace();
            }

        //  Logger.i(TAG + " size : " + mQueue.size() + " " + mQueue);
    }

    /**
     * 读取缓存中队列中的数据
     * @param msec 超时时间， -1表示一直阻塞，大于0表示阻塞超时时间 单位毫秒
     * @return
     */
    public static String PopFromCacher(int msec) {
        String json = null;
        try{
//            Logger.i(TAG + " size : " + mQueue.isEmpty() + " " + mQueue);
            if(msec < 0){
                json = mQueue.take();//如果队列为空，则阻塞
            }else{
                json = mQueue.poll(msec, TimeUnit.MILLISECONDS);
            }
//            Logger.i(TAG + " read : " + json);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
//        Logger.i(TAG + " size : " + mQueue.isEmpty() + " " + mQueue);
//        json = mQueue.poll();
//        if(json != null){
//            Logger.i(TAG + " read : " + json);
//        }
        return json;
    }
}
