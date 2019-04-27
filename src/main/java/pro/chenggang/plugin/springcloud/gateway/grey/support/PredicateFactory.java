package pro.chenggang.plugin.springcloud.gateway.grey.support;

import com.netflix.loadbalancer.AbstractServerPredicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PredicateFactory
 * @author chenggang
 * @date 2019/04/25
 */
public class PredicateFactory {

    private Map<Class<? extends AbstractServerPredicate>,PredicateInitWorker> predicateInitWorkerMap;

    public PredicateFactory(){
        predicateInitWorkerMap = new HashMap<>();
    }

    public void putPredicateInitWorker(Class<? extends AbstractServerPredicate> predicateClass,PredicateInitWorker predicateInitWorker){
        predicateInitWorkerMap.put(predicateClass, predicateInitWorker);
    }

    public List<AbstractServerPredicate> getAllPredicate(Class<? extends AbstractServerPredicate> ... predicateClass){
        List<AbstractServerPredicate> predicateList = new ArrayList<>(predicateInitWorkerMap.size());
        for(Class<? extends AbstractServerPredicate> clazz : predicateClass){
            if(predicateInitWorkerMap.containsKey(clazz)){
                predicateList.add(predicateInitWorkerMap.get(clazz).initPredicate());
            }
        }
        return predicateList;
    }
}
