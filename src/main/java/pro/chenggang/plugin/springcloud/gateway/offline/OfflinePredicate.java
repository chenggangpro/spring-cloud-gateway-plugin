package pro.chenggang.plugin.springcloud.gateway.offline;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import lombok.extern.slf4j.Slf4j;
import pro.chenggang.plugin.springcloud.gateway.util.GatewayUtils;

/**
 * OfflinePredicate
 * @author chenggang
 * @date 2019/04/25
 */
@Slf4j
public class OfflinePredicate extends AbstractServerPredicate {

    @Override
    public boolean apply(PredicateKey predicateKey) {
        Server predicateServer = predicateKey.getServer();
        if (predicateServer instanceof DiscoveryEnabledServer) {
            DiscoveryEnabledServer server = (DiscoveryEnabledServer) predicateServer;
            String hostIp = server.getHost();
            int hostPort = server.getPort();
            InstanceInfo instanceInfo = server.getInstanceInfo();
            String appName = instanceInfo.getAppName();
            long serviceUpTimestamp = instanceInfo.getLeaseInfo().getServiceUpTimestamp();
            ServerOfflineStatus offLineCache = OfflineServerCache.getOfflineCache(GatewayUtils.getOfflineCacheKey(hostIp,hostPort));
            if (null != offLineCache && appName.equalsIgnoreCase(offLineCache.getName()) && offLineCache.isOffline(serviceUpTimestamp)) {
                log.info("[OfflinePredicate]Service Is Temporary Offline，ServerInstanceInfo:{},Offline Cache:{}", instanceInfo, offLineCache);
                return false;
            }
        }
        return true;
    }
}
