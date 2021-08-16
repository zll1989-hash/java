package cn.egenie.architect.common.core.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * @author lucien
 * @since 2021/01/05
 */
public class IpUtils {

    private static final String UNKNOWN = "unknown";

    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces();
            while (faces.hasMoreElements()) {
                NetworkInterface face = faces.nextElement();
                if (face.isLoopback() || face.isVirtual() || !face.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> address = face.getInetAddresses();
                while (address.hasMoreElements()) {
                    InetAddress addr = address.nextElement();
                    if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress() && !addr.isAnyLocalAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        }
        catch (Exception e) {
            // ignore
        }

        return "127.0.0.1";
    }

    public static String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
