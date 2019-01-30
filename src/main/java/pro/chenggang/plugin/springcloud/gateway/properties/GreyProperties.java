package pro.chenggang.plugin.springcloud.gateway.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.HashMap;
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
@ConfigurationProperties(GreyProperties.GREY_PROPERTIES_PREFIX)
public class GreyProperties implements InitializingBean{

    public static final String GREY_PROPERTIES_PREFIX = "spring.cloud.gateway.grey";
    /**
     * Grey Rule
     */
    @Getter
    @Setter
    private List<GreyRule> greyRuleList = Collections.emptyList();
    /**
     * Grey Rule Map
     */
    @Getter
    private Map<String,GreyRule> greyRuleMap = Collections.emptyMap();

    @Override
    public void afterPropertiesSet() {
        if(null == greyRuleList || greyRuleList.isEmpty()){
            return;
        }
        greyRuleMap = new HashMap<>(greyRuleList.size(),1);
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
}
