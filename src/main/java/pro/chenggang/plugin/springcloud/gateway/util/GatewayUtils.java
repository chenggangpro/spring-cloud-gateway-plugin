package pro.chenggang.plugin.springcloud.gateway.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * Gateway Util
 * @author chenggang
 * @date 2019/01/29
 */
public class GatewayUtils {

    /**
     * get Real Ip Address
     * @param request ServerHttpRequest
     * @return
     * @author Evans
     */
    public static String getIpAddress(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("X-Real-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
        }
        if(ip != null && ip.length() > 15 && ip.contains(",")){
            ip = ip.substring(0,ip.indexOf(","));
        }
        return ip;
    }

    /**
     * get offline cache key
     * @return
     */
    public static String getOfflineCacheKey(String ip,int port){
        if(ip != null){
            return ip+":"+port;
        }
        return null;
    }
}
