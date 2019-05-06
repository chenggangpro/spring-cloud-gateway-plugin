package pro.chenggang.plugin.springcloud.gateway.offline;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * OfflineServerCache
 * @author chenggang
 * @date 2019/04/25
 */
public class OfflineServerCache {

    public static Cache<String, ServerOfflineStatus> cache(){
        return SingletonHandler.OFF_LINE_CACHE;
    }

    private static class SingletonHandler {

        private static final Cache<String, ServerOfflineStatus> OFF_LINE_CACHE= CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(200)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build();
    }

    /**
     * add offline server cache
     * @param key
     * @param status
     */
    public static void addOfflineCache(String key, ServerOfflineStatus status){
        OfflineServerCache.cache().put(key, status);
    }

    /**
     * get offline cache
     * @param key
     * @return
     */
    public static ServerOfflineStatus getOfflineCache(String key){
        return OfflineServerCache.cache().getIfPresent(key);
    }
}
