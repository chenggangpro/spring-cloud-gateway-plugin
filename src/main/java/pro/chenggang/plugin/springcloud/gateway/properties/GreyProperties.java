package pro.chenggang.plugin.springcloud.gateway.properties;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Grey Properties
 * @author chenggang
 * @date 2019/01/29
 */
@Slf4j
@ToString
public class GreyProperties implements InitializingBean{

    public static final String GREY_PROPERTIES_PREFIX = "spring.cloud.gateway.plugin.grey";

    public static Map<String,GreyRule> greyRuleMap;
    /**
     * Enable Grey Route
     */
    @Getter
    @Setter
    private Boolean enable = false;
    /**
     * Choose Grey Ribbon Rule
     */
    @Getter
    @Setter
    private GreyRibbonRule greyRibbonRule = GreyRibbonRule.DEFAULT;
    /**
     * Grey Rule
     */
    @Getter
    @Setter
    private List<GreyRule> greyRuleList = Collections.emptyList();

    @Override
    public void afterPropertiesSet() {
        if(null == greyRuleList || greyRuleList.isEmpty()){
            greyRuleMap = Collections.emptyMap();
            return;
        }
        greyRuleMap = Maps.newHashMapWithExpectedSize(greyRuleList.size());
        for(GreyRule grayRule : greyRuleList){
            greyRuleMap.put(grayRule.getServiceId(),grayRule);
        }
        log.debug("Load Grey Rule Map :{}",greyRuleMap);
    }

    /**
     * get Grey Rule By ServiceId
     * @param serviceId
     * @return
     */
    public GreyRule getGreyRule(String serviceId){
        if(StringUtils.isBlank(serviceId)){
            return null;
        }
        return greyRuleMap.get(serviceId.toLowerCase());
    }


    @ToString
    public static class GreyRule implements InitializingBean{
        @Getter
        @Setter
        private String serviceId;
        @Getter
        @Setter
        private String version;
        @Getter
        @Setter
        private Operation operation = Operation.OR;
        @Getter
        @Setter
        private List<Rule> rules = Collections.emptyList();
        @Getter
        private LinkedHashMap<String,List<String>> ruleMap = new LinkedHashMap<>();

        @Override
        public void afterPropertiesSet() {
            if(rules.isEmpty()){
               return;
            }
            for(Rule rule : rules){
                ruleMap.put(rule.getKey(),rule.getValue());
            }
        }

        @Getter
        @Setter
        @ToString
        public static class Rule{
            private String key;
            private List<String> value;
        }

        public enum Operation{
            /**
             * AND Operation
             */
            AND,
            /**
             * OR Operation
             */
            OR
        }

    }

    /**
     * GreyRule
     */
    public enum GreyRibbonRule{
        /**
         * default grey rule based on  round rule
         */
        DEFAULT,
        /**
         * weight response rule base on WeightResponseRUle
         */
        WEIGHT_RESPONSE,
    }
}
