package pro.chenggang.plugin.springcloud.gateway.grey;

import com.netflix.loadbalancer.AbstractServerPredicate;

import java.util.List;

/**
 * GreyDefaultRoundRule
 * @author chenggang
 * @date 2019/04/25
 */
public class GreyDefaultRoundRule extends RoundRule {

    public GreyDefaultRoundRule() {
    }

    public GreyDefaultRoundRule(List<AbstractServerPredicate> customPredicateList) {
        super(customPredicateList);
    }
}
