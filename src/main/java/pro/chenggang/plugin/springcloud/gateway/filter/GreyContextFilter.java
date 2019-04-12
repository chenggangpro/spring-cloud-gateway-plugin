package pro.chenggang.plugin.springcloud.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import pro.chenggang.plugin.springcloud.gateway.context.GatewayContext;
import pro.chenggang.plugin.springcloud.gateway.context.GreyContext;
import pro.chenggang.plugin.springcloud.gateway.option.FilterOrderEnum;
import pro.chenggang.plugin.springcloud.gateway.properties.GreyProperties;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Grey Context Filter
 * @author chenggang
 * @date 2019/01/29
 */
@Slf4j
@AllArgsConstructor
public class GreyContextFilter implements GlobalFilter,Ordered {

    private GreyProperties greyProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getAttributeOrDefault(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR,null);
        if(null == route){
            return chain.filter(exchange);
        }
        /*
         * get route if route isn't lb just return chain
         */
        URI routeUri = route.getUri();
        if(!"lb".equalsIgnoreCase(routeUri.getScheme())){
            return chain.filter(exchange);
        }
        /*
         * reset Grey Filter ThreadLocal
         */
        GreyLoadBalancerClientFilter.contextThreadLocal.remove();
        String serviceId = routeUri.getHost().toLowerCase();
        GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.CACHE_GATEWAY_CONTEXT);
        GreyProperties.GreyRule greyRule = greyProperties.getGreyRule(serviceId);
        /*
         * grey rule is empty or null ,return chain
         */
        if(null == greyRule || null == greyRule.getRules() || greyRule.getRules().isEmpty()){
            return chain.filter(exchange);
        }
        MediaType contentType = exchange.getRequest().getHeaders().getContentType();
        LinkedHashMap<String, List<String>> ruleMap = greyRule.getRuleMap();
        Set<String> ruleKeys = ruleMap.keySet();
        GreyProperties.GreyRule.Operation operation = greyRule.getOperation();
        MultiValueMap<String, String> allRequestData = gatewayContext.getAllRequestData();
        HttpHeaders requestHeaders = gatewayContext.getRequestHeaders();
        /*
         * init filtered request data with grey rule keys
         */
        Set<Map.Entry<String, List<String>>> filteredRequestData = new HashSet<>();
        if(MediaType.APPLICATION_JSON.equals(contentType) || MediaType.APPLICATION_JSON_UTF8.equals(contentType)){
            String jsonBody = gatewayContext.getRequestBody();
            if(StringUtils.isNotBlank(jsonBody)){
                Map<String,List<String>> jsonParam = new HashMap<>();
                ruleKeys.forEach(key->{
                    Object eval = JSONPath.eval(JSONObject.parseObject(jsonBody), String.format("$..%s", key));
                    if(null != eval){
                        List value = (List) eval;
                        if(!value.isEmpty()){
                            List<String> valueList = new ArrayList<>(value.size());
                            for(Object tempValue : value){
                                valueList.add(tempValue.toString());
                            }
                            jsonParam.put(key,valueList);
                        }
                    }
                });
                if(!jsonParam.isEmpty()){
                    filteredRequestData.addAll(jsonParam.entrySet());
                }
            }
        }
        if(!allRequestData.isEmpty()){
            filteredRequestData.addAll(
                    allRequestData.entrySet()
                    .stream()
                    .filter(stringListEntry -> ruleKeys.contains(stringListEntry.getKey()))
                    .collect(Collectors.toSet()));
        }
        if(!requestHeaders.isEmpty()){
            filteredRequestData.addAll(requestHeaders.entrySet().stream().filter(stringListEntry -> ruleKeys.contains(stringListEntry.getKey()))
                    .collect(Collectors.toSet()));
        }
        boolean matched = false;
        switch (operation){
            case OR:
                matched = validateGreyOrOperation(filteredRequestData,ruleMap);
                break;
            case AND:
                matched = validateGreyAndOperation(filteredRequestData,ruleMap);
                break;
            default:
                break;
        }
        /*
         * cache grey context
         */
        GreyContext greyContext = new GreyContext();
        greyContext.setServiceId(serviceId);
        greyContext.setVersion(greyRule.getVersion());
        greyContext.setMatched(matched);
        exchange.getAttributes().put(GreyContext.CACHE_GREY_CONTEXT,greyContext);
        log.debug("[GreyContextGlobalFilter]ServiceId:{},Matched Grey Rules,Cache GreyContext:{}",serviceId,greyContext);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return FilterOrderEnum.GREY_CONTEXT_FILTER.getOrder();
    }

    /**
     * validate grey rule use or operation
     * @param filteredRequestData
     * @param rules
     * @return
     */
    private boolean validateGreyOrOperation(Set<Map.Entry<String, List<String>>> filteredRequestData,LinkedHashMap<String, List<String>> rules){
        if(null == filteredRequestData || filteredRequestData.isEmpty() || null == rules || rules.isEmpty()){
            return false;
        }
        List<String> paramValue;
        String paramKey;
        for(Map.Entry<String, List<String>> entry:filteredRequestData){
            paramValue= entry.getValue();
            paramKey = entry.getKey();
            for(String ruleParam : rules.get(paramKey)){
                if(paramValue.contains(ruleParam)){
                    log.debug("[GreyContextFilter](ValidateGreyOrOperation)Match Grey Rules In RequestParam,Grey Rule Key:{},RuleValue:{},ParamValueList:{}",paramKey,ruleParam,paramValue);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * validate grey rule use and operation
     * @param filteredRequestData
     * @param rules
     * @return
     */
    private boolean validateGreyAndOperation(Set<Map.Entry<String, List<String>>> filteredRequestData,LinkedHashMap<String, List<String>> rules){
        if(null == filteredRequestData || filteredRequestData.isEmpty() || null == rules || rules.isEmpty()){
            return false;
        }
        List<String> paramValue;
        String paramKey;
        int matchedSize =0;
        for(Map.Entry<String, List<String>> entry:filteredRequestData){
            paramValue= entry.getValue();
            paramKey = entry.getKey();
            for(String ruleParam : rules.get(paramKey)){
                if(paramValue.contains(ruleParam)){
                    matchedSize++;
                }
            }
        }
        if(matchedSize == rules.size()){
            log.debug("[GreyContextFilter](ValidateGreyAndOperation)Matched Grey Rules,RuleSize:{},Matched Size:{},Return True",rules.size(),matchedSize);
            return true;
        }
        return false;
    }
}
