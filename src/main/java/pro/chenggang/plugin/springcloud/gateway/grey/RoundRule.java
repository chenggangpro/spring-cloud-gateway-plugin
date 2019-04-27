package pro.chenggang.plugin.springcloud.gateway.grey;

import com.alibaba.fastjson.JSON;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.AvailabilityPredicate;
import com.netflix.loadbalancer.CompositePredicate;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.PredicateBasedRule;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round Rule For Weight Response Rule
 * @author chenggang
 * @date 2019/01/29
 */
public abstract class RoundRule extends PredicateBasedRule {
    private AtomicInteger nextServerCyclicCounter;
    private static final boolean AVAILABLE_ONLY_SERVERS = true;
    private static final boolean ALL_SERVERS = false;

    private static Logger log = LoggerFactory.getLogger(RoundRobinRule.class);

    private CompositePredicate compositePredicate;

    RoundRule(){
        this(null);
    }

    RoundRule(List<AbstractServerPredicate> customPredicateList) {
        nextServerCyclicCounter = new AtomicInteger(0);
        if(CollectionUtils.isEmpty(customPredicateList)){
            customPredicateList = new ArrayList<>(1);
        }
        AvailabilityPredicate availabilityPredicate = new AvailabilityPredicate(this,null);
        customPredicateList.add(availabilityPredicate);
        compositePredicate = createCompositePredicate(customPredicateList.toArray(new AbstractServerPredicate[0]));
    }

    private CompositePredicate createCompositePredicate(AbstractServerPredicate ... predicates) {
        return CompositePredicate.withPredicates(predicates).build();
    }

    @Override
    public AbstractServerPredicate getPredicate() {
        return this.compositePredicate;
    }

    public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            log.warn("no load balancer");
            return null;
        }

        Server server = null;
        int count = 0;
        while (count++ < 10) {
            List<Server> reachableServers = lb.getReachableServers();
            List<Server> allServers = getPredicate().getEligibleServers(lb.getAllServers(),key);
            int upCount = reachableServers.size();
            int serverCount = allServers.size();

            if ((upCount == 0) || (serverCount == 0)) {
                log.warn("No up servers available from load balancer: " + lb);
                return null;
            }

            int nextServerIndex = incrementAndGetModulo(serverCount);
            server = allServers.get(nextServerIndex);

            if (server == null) {
                /* Transient. */
                Thread.yield();
                continue;
            }

            if (server.isAlive() && (server.isReadyToServe())) {
                log.debug("[RoundRule]Choose Server ==> {} [MetaInfo:{}]",server.getId(), JSON.toJSONString(server.getMetaInfo()));
                return (server);
            }

            // Next.
            server = null;
        }

        if (count >= 10) {
            log.warn("No available alive servers after 10 tries from load balancer: "
                    + lb);
        }
        return server;
    }

    /**
     * Inspired by the implementation of {@link AtomicInteger#incrementAndGet()}.
     *
     * @param modulo The modulo to bound the value of the counter.
     * @return The next value.
     */
    private int incrementAndGetModulo(int modulo) {
        for (;;) {
            int current = nextServerCyclicCounter.get();
            int next = (current + 1) % modulo;
            if (nextServerCyclicCounter.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    @Override
    public Server choose(Object key) {
        return choose(getLoadBalancer(), key);
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {

    }

}
