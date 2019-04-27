package pro.chenggang.plugin.springcloud.gateway.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pro.chenggang.plugin.springcloud.gateway.offline.OfflineServerCache;
import pro.chenggang.plugin.springcloud.gateway.offline.ServerOfflineStatus;
import pro.chenggang.plugin.springcloud.gateway.util.GatewayUtils;
import reactor.core.publisher.Mono;

/**
 * PredicateFactory
 * @author chenggang
 * @date 2019/04/25
 */
@Slf4j
@RestController
public class GatewayPluginEndpoint {

    @PostMapping("/gateway/plugin/offline")
    public Mono<String> offLine(@RequestBody(required = false) ServerOfflineStatus serverOfflineStatus){
        if(serverOfflineStatus != null && StringUtils.isNotBlank(serverOfflineStatus.getName())&& StringUtils.isNotBlank(serverOfflineStatus.getIp()) && null !=serverOfflineStatus.getPort()){
            OfflineServerCache.addOfflineCache(GatewayUtils.getOfflineCacheKey(serverOfflineStatus.getIp(),serverOfflineStatus.getPort()),serverOfflineStatus);
            log.debug("[ServerTemporaryOffLine]Add Server Offline Cache,ServerOffLineStatus:{}",serverOfflineStatus);
            return Mono.just("success");
        }
        return Mono.just("data-empty-error");
    }

}
