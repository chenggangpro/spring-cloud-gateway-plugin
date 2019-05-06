package pro.chenggang.plugin.springcloud.gateway.grey.support;

import com.netflix.loadbalancer.AbstractServerPredicate;

/**
 * PredicateInitWorker
 * @author chenggang
 * @date 2019/04/25
 */
@FunctionalInterface
public interface PredicateInitWorker {

    /**
     * Init Predicate
     * @return
     */
    AbstractServerPredicate initPredicate();
}
