package cn.com.magnity.coresdksample.utils;

import android.content.Context;
import android.util.Log;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import cn.com.magnity.coresdksample.R;

public class EthernetUtil {
    public static String getEthernetIp()  {
    String hostIp ="127.0.0.1";
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
                    hostIp = ia.getHostAddress();
                   // Log.i("EthernetUtil", hostIp);
                    break;
                }else {
                    hostIp="127.0.0.1";
                }
            }
        }
    } catch (SocketException e) {
        Log.i("EthernetUtil", "SocketException");
        e.printStackTrace();
    }
    return hostIp;

}

}
