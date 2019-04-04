package cn.com.magnity.coresdksample.utils;

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


