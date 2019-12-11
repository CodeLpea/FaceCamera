package cn.com.magnity.coresdksample.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;
import static android.content.Context.WIFI_SERVICE;

/**
 * Created by xiaoyuren on 2018/2/27.
 * 项目名称：didano-robot
 * 类描述：网络工具类
 * company：www.didano.cn
 * email：vin.qin@didano.cn
 * 创建时间：2018/2/27 15:13
 */

public class NetUtil {

    //网络未连接
    private static final boolean NETWORK_NONE = false;
    //移动数据或无线网络连接
    private static final boolean NETWORK_AVAILABLE = true;

    /**
     * 获取当前网络状态
     * @param context 上下文对象
     * @return boolean
     */
    public static boolean getNetStatus(Context context) {
        // 获取系统连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取网络状态信息
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager
                .getActiveNetworkInfo() : null;
        //判断网络NetworkInfo是否不为空且连接
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            android.util.Log.i(TAG, "网络连接可用: ");
            //网络连接可用
            return NETWORK_AVAILABLE;

        } else {
            android.util.Log.e(TAG, "网络连接不可用: ");
            return NETWORK_NONE;//网络不可用（未连接）
        }

    }
    /**
     * 判断网络是否可用，包括wifi、mobile、ethernet等
     *
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null)
            return networkInfo.isAvailable();
        return false;
    }

    /**
     * 判断wifi是否可用
     *
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
            return networkInfo.isAvailable();
        return false;
    }
    /**
     * 获取无线网络的IP地址
     * @return
     */
    public static String getIpAddr(Context context) {
        String ipAddress = null;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            ipAddress = longToIpString(wifiInfo.getIpAddress());//得到IPV4地址
        }
        return ipAddress;
    }


    /**
     * 获取连接上的WiFi的一些信息
     * @return 成功返回WiFi信息对象，失败返回null
     */
    public static NetUtil.ConnectedWifiInfo getConnectedWifiInfo1(Context context){
        NetUtil.ConnectedWifiInfo wifiInfo = null;
        WifiInfo info = getConnectedWifiInfo2(context);
        if(info != null && info.getBSSID() != null) {
            wifiInfo = new NetUtil().new ConnectedWifiInfo();
            wifiInfo.setSsid(info.getSSID());
            wifiInfo.setSpeed(info.getLinkSpeed());
            wifiInfo.setUnits(WifiInfo.LINK_SPEED_UNITS);
            wifiInfo.setStrength(info.getRssi());
        }
        return wifiInfo;
    }

    /**
     * 获取当前连接上的WiFi信息
     * @param context
     * @return
     */
    public static WifiInfo getConnectedWifiInfo2(Context context){
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        return info;
    }

    /**
     * 获取以太网的IP地址
     * @param context
     * @return
     */
    public static String getEth0IpAddr(Context context){
        String ethIp = null;
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET){
            try {
                Enumeration nis = NetworkInterface.getNetworkInterfaces();
                InetAddress ia = null;
                while (nis.hasMoreElements()) {
                    NetworkInterface ni = (NetworkInterface) nis.nextElement();
                    Enumeration<InetAddress> ias = ni.getInetAddresses();
                    while (ias.hasMoreElements()) {
                        ia = ias.nextElement();
                        if (ia instanceof Inet6Address) {
                            continue;// skip ipv6
                        }
                        String ip = ia.getHostAddress();
                        if (!"127.0.0.1".equals(ip)) {
                            ethIp = ia.getHostAddress();
                            break;
                        }
                    }
                }
            } catch (SocketException e) {

            }
        }
        return ethIp;
    }

    /**
     * Ipv4 address check.
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(" + "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}" +
                    "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

    /**
     * Check if valid IPV4 address.
     *
     * @param input the address string to check for validity.
     *
     * @return True if the input parameter is a valid IPv4 address.
     */
    public static boolean isIPv4Address(String input) {
        return IPV4_PATTERN.matcher(input).matches();
    }

    /**
     * Get local Ip address.
     * 可以获得wifiip和有线网络的ip，不用专门区分。
     * ip格式：/192.168.8.197
     */
    public static InetAddress getLocalIPAddress() {
        Enumeration<NetworkInterface> enumeration = null;
        try {
            enumeration = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                NetworkInterface nif = enumeration.nextElement();
                Enumeration<InetAddress> inetAddresses = nif.getInetAddresses();
                if (inetAddresses != null) {
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        if (!inetAddress.isLoopbackAddress() && isIPv4Address(inetAddress.getHostAddress())) {
                            return inetAddress;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String longToIpString(long ip) {

        return (ip & 0xFF ) + "." +
                ((ip >> 8 ) & 0xFF) + "." +
                ((ip >> 16 ) & 0xFF) + "." +
                ( ip >> 24 & 0xFF) ;
    }
    /**
     * ip地址字符串转long型
     * @param strIp
     * @return 正确返回具体的值，错误返回-1
     */
    //ip 转为整数
    public static long ipStringToLong(String strIp) {
        String[] ip = strIp.split("\\.");
        return (Long.parseLong(ip[0]) << 24) + (Long.parseLong(ip[1]) << 16) + (Long.parseLong(ip[2]) << 8) + Long.parseLong(ip[3]);
    }

    /**
     * 检查IP地址是否合法
     * @param ip 要检查的IP地址
     * @return 合法返回true， 不合法返回false
     */
    public static boolean isLegalIpAddress(String ip){
        boolean ret = false;
        if(ip != null && !ip.isEmpty()){
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                         + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                         + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                         + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            ret = ip.matches(regex);
        }

        return ret;
    }

    /**
     * 判读子网掩码的格式是否合法
     * @param netmask 要判断的子网掩码
     * @return 合法返回true， 不合法返回false
     */
    public static boolean isLegalNetMask(String netmask){
        if(netmask != null && !netmask.isEmpty()) {
            Pattern pattern = Pattern.compile("(254|252|248|240|224|192|128|0)\\.0\\.0\\.0|255\\.(254|252|248|240|224|192|128|0)\\.0\\.0|255\\.255\\.(254|252|248|240|224|192|128|0)\\.0|255\\.255\\.255\\.(255|254|252|248|240|224|192|128|0)|^[1-9]$|^2\\d$|^3[0-2]$");
            return pattern.matcher(netmask).matches();
        }

        return false;
    }

    private static final String ETH0_MAC_ADDR = "/sys/class/net/eth0/address";
    private static final String WLAN0_MAC_ADDR = "/sys/class/net/wlan0/address";
    public static final String MAC_ERROR = "unavailable";
    /**
     * 获取以太网的mac地址
     *
     * @return
     */
    public static String getEthMacAddress() {
        try {
            return readLine(ETH0_MAC_ADDR);
        } catch (IOException e) {

            return MAC_ERROR;
        }
    }

    /**
     * 获取WiFi的mac地址
     *
     * @return
     */
    public static String getWlanMacAddress() {
        try {
            return readLine(WLAN0_MAC_ADDR);
        } catch (IOException e) {

            return MAC_ERROR;
        }
    }

    private static String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }

    public class ConnectedWifiInfo{
        private String ssid; // Wifi源名称
        private int speed; // 链接速度
        private String units; // 链接速度单位
        private int strength;//链接信号强度

        public String getSsid() {
            return ssid;
        }

        public void setSsid(String ssid) {
            this.ssid = ssid;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }

        public String getUnits() {
            return units;
        }

        public void setUnits(String units) {
            this.units = units;
        }

        public int getStrength() {
            return strength;
        }

        public void setStrength(int strength) {
            this.strength = strength;
        }

        @Override
        public String toString() {
            return "ConnectedWifiInfo{" +
                    "ssid='" + ssid + '\'' +
                    ", speed=" + speed +
                    ", units='" + units + '\'' +
                    ", strength=" + strength +
                    '}';
        }
    }

}
