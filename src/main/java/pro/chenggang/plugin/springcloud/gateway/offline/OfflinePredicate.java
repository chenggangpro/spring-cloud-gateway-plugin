package pro.chenggang.plugin.springcloud.gateway.offline;

import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.loadbalancer.Server;
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
        String hostIp = predicateServer.getHost();
        int hostPort = predicateServer.getPort();
        String appName = predicateServer.getMetaInfo().getAppName();
        ServerOfflineStatus offLineCache = OfflineServerCache.getOfflineCache(GatewayUtils.getOfflineCacheKey(hostIp,hostPort));
        if (null != offLineCache && appName.equalsIgnoreCase(offLineCache.getName())) {
            log.info("[OfflinePredicate]Service Is Temporary Offline，AppName:{},Offline Cache:{}", appName, offLineCache);
            return false;
        }
        return true;
    }
}
