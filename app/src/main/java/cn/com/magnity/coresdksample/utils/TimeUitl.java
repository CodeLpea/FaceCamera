package cn.com.magnity.coresdksample.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeUitl {
    private  static long lastClickTime=0;//上次点击的时间

        public  static boolean timeInterval(int spaceTime) {//查看两次是否超过时间间隔
            long currentTime = System.currentTimeMillis();//当前系统时间
            boolean isAllowClick;//是否超过间隔
            if (currentTime - lastClickTime > spaceTime) {
                isAllowClick= true;
            } else {
                isAllowClick = false;
            }
            lastClickTime = currentTime;
            return isAllowClick;

        }
    /**
     * 延时
     *
     * @param ms 毫秒
     */
    public static void delayMs(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
        }
    }

    /**
     * 获取格式化后的时间
     *
     * @return 2019:11:19:11:22:33
     */
    public static String getNowDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String dateName = df.format(calendar.getTime());
        return dateName;
    }
    /**
     * 获取格式化后的时间
     *
     * @return 2019:11:19:11:22:33
     */
    public static String getDate() {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        String dateName = df.format(calendar.getTime());
        return dateName;
    }
    /**
     * 获取格式化后的时间
     *
     * @return 2019:11:19:11:22:33
     */
    public static String getNowRecordDate() {
        DateFormat df = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss");
        Calendar calendar = Calendar.getInstance();
        String dateName = df.format(calendar.getTime());
        return dateName;
    }

    /**
     * 获取当前时间
     */
    public static String currentDayTime() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String ret = year + "-" + month + "-" + day;
        return ret;
    }
    }


