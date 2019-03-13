package cn.com.magnity.coresdksample.utils;

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

    }


