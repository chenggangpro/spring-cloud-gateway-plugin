package pro.chenggang.plugin.springcloud.gateway.grey;

import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.AvailabilityPredicate;
import com.netflix.loadbalancer.CompositePredicate;
import com.netflix.loadbalancer.PredicateBasedRule;

/**
 * Default Grey Predicate Rule
 * @author chenggang
 * @date 2019/01/29
 */
public class GreyPredicateRule extends PredicateBasedRule {

    private CompositePredicate compositePredicate;

    public GreyPredicateRule() {
        GreyPredicate greyPredicate = new GreyPredicate();
        AvailabilityPredicate availabilityPredicate = new AvailabilityPredicate(this,null);
        compositePredicate = createCompositePredicate(greyPredicate, availabilityPredicate);
    }

    private CompositePredicate createCompositePredicate(GreyPredicate p1, AvailabilityPredicate p2) {
        return CompositePredicate.withPredicates(p1, p2).build();

    }

    @Override
    public AbstractServerPredicate getPredicate() {
        return this.compositePredicate;
    }
}
