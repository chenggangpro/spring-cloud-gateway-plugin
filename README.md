# spring-cloud-gateway-plugin
Spring Cloud Gateway Extra Plugin

[![Build Status](https://travis-ci.com/chenggangpro/spring-cloud-gateway-plugin.svg?branch=master)](https://travis-ci.com/chenggangpro/spring-cloud-gateway-plugin)

## Current Version: 2.1.SR2.1.RELEASE


|Gateway Version|Plugin Version|
|:--|:--|
|`Greenwich.SR1`|`2.1.SR1.2.RELEASE`|
|`Greenwich.SR2`|`2.1.SR2.1.RELEASE`|


> More Detail See [Wiki](https://github.com/chenggangpro/spring-cloud-gateway-plugin/wiki)

### This Plugin Support Below Features:

* [x] Cache Request Body And Form Body
* [x] Add Request Log Filter To Log Request And Response
* [x] Add Read Json Response Body And Log Response Body
* [x] Add Global Exception Handler With Json
* [x] Add Custom Exception Handler
* [x] Add Grey Route With Ribbon And Eureka
* [x] Each Route Use different Hystrix CommandKey 
* [x] Support Dynamic Predicate With Existing Routes

### How To Use This Feature

##### Note:
 
   * This Dependency Base Spring Cloud Gateway[`Greenwich.SR2`],Suggest To Update To This SpringCloud Version,Official Resolve Some Issues , Fix Some Bugs.
   * The SpringBoot Version need to update to [`2.1.6.RELEASE`],It fix reactor-netty issues
   * This Dependency Is Now In Maven Central. 
   * The Feature To Read Request And Response Json Data Will Loss A Lot Of Performance,It Will Reduce The Gateway Traffic.

##### Step

* 1 . Include Dependency
    
    > Spring Cloud Gateway
    
    ```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
        <!--If you need to use grey route,you should add next dependency ,but grey route only can be used with eureka discover-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Greenwich.SR2</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    ```
    > Gateway Plugin
    
    ```xml
    <dependency>
        <groupId>pro.chenggang</groupId>
        <artifactId>spring-cloud-gateway-plugin</artifactId>
        <version>2.1.SR2.1.RELEASE</version>
    </dependency>
    ```
  
* 2 . Add Enable Annotation To You Application

    ```java
    @EnableGatewayPlugin
    public class SpringCloudGatewayPluginApplication {
    
        public static void main(String[] args) {
            SpringApplication.run(SpringCloudGatewayPluginApplication.class, args);
        }
    
    }
    ```

    Note: If You User SpringBoot 2.1.0+,You should set `allow-bean-definition-overriding` to `true`

    ```yaml
    spring:
      main:
        allow-bean-definition-overriding: true
    ```

* 3 . Choose Plugin Feature To Use

    By use this annotation `@EnableGatewayPlugin` to enable the plugin,the plugin support switch to choose feature
    By default,the `GatewayContext Filter` is always into system
    
    ```yaml
    spring:
      profiles:
        active: dev
      cloud:
        gateway:
          plugin:
            config:
              log-request: true
              read-request-data: true # this setting will read all request data
              read-response-data: true
              exception-json-handler: true
              enable-dynamic-route: true
            grey:
              enable: false
              grey-ribbon-rule: weight_response
    ```
* 4 . Specific Setting To Enable Read Request Data

    ```yaml
    spring:
      cloud:
        gateway:
          plugin:
            config:
              read-request-data-service-id-list:  #set specific serviceId from discover to read request Data
                - serviceId1
                - serviceId2
              read-request-data-path-list:        #set specific path to read request data
                - /service/path1/*
                - /service/path2/**
                - /service/path3  
    ```

* 5 . User GatewayContext

    You Can Use GatewayContext To Get Cache JsonBody Or Form Body,Just Use

    ```java
    GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.CACHE_GATEWAY_CONTEXT);
    ```
* 6 . The Deference Between `GreyRibbonRule.DEFAULT` And `GreyRibbonRule.WeightResponse`

    The Default GreyRibbonRule Just Use Round Rule As Base Ribbon Rule
    The WeightResponse GreyRibbonRule Use WeightResponse Rule As Base Ribbon Rule

* 7 . The Grey Route

    * Setup Gateway Properties
 
    ```yaml
    spring:
      cloud:
        gateway:
          plugin:
              grey:
                grey-rule-list:
                  - service-id: privider1
                    version: 2.0.0
                    operation: OR
                    rules:
                      - key: key1
                        value:
                          - value1
                          - value2
                          - value3
                      - key: key2
                        value:
                          - value4
                          - value5
                          - value6
                  - service-id: provider2
                    version: 2.0.0
                    operation: AND
                    rules:
                      - key: keya
                        value:
                          - value1a
                          - value2a
                          - value3a
                      - key: keyb
                        value:
                          - value4b
                          - value5b
                          - value6b
        
     ```
    
    * Set Up Service MetaInfo
    
        ```yaml
        #proiver1
        eureka:
          instance:
            metadata-map:
              version: 2.0.0 
        ```
    
    * The Properties Active Rule Principle
    
        When Request URL Route To Provider1,When The Request JsonBody Or Form Data Contain The Key ->`Key1` And Match Any Of The Value->[`value1`,`value2`,`value3`]
        The Route The Request To The Service Which Setup The MetaInfo With Specific Version Which Match The Gateway Grey Setup 
    

* 8 . How To Custom GlobalException Handler With Json

    In Order To Handle Other Exception,You Can Define Specific Bean Implements `ExceptionHandlerStrategy`
    By default,plugin supply `DefaultExceptionHandlerStrategy` In Case Of None Strategy Exist 

    ```java
    @Slf4j
    public class DefaultExceptionHandlerStrategy implements ExceptionHandlerStrategy {
    
        @Override
        public Class getHandleClass() {
            return Throwable.class;
        }
    
        @Override
        public ExceptionHandlerResult handleException(Throwable throwable) {
            ResponseResult<String> responseResult = new ResponseResult<>(SystemResponseInfo.GATEWAY_ERROR,throwable.getMessage());
            ExceptionHandlerResult result = new ExceptionHandlerResult(HttpStatus.INTERNAL_SERVER_ERROR, JSON.toJSONString(responseResult));
            log.debug("[DefaultExceptionHandlerStrategy]Handle Exception:{},Result:{}",throwable.getMessage(),result);
            log.error("[DefaultExceptionHandlerStrategy]Log Exception In Error Level,Exception Message:{}",throwable.getMessage());
            return result;
        }
    }
    ```
    
    Or You Can Use `@ExceptionHandler` just like below,
    
    The`@ResponseStatus` is optional,if you don't add `@ResponseStatus`,the default HttpStatus is HttpStatus.BAD_GATEWAY 
    
    ```java
    @Component
    public class DemoExceptionHandler {
    
        @ExceptionHandler({NotFoundException.class})
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public Map handlerException(ServerWebExchange exchange,TimeoutException throwable){
            LinkedHashSet<URI> originalRequestUris = exchange.getAttributeOrDefault(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR,null);
            Map map = Maps.newHashMapWithExpectedSize(2);
            map.put("URI",originalRequestUris);
            map.put("ExceptionMessage",throwable.getClass().getSimpleName());
            return map;
        }
      
        @ExceptionHandler({ClientException.class, TimeoutException.class})
        @ResponseStatus(HttpStatus.BAD_GATEWAY)
        public Map handlerException(ServerWebExchange exchange,TimeoutException throwable){
              LinkedHashSet<URI> originalRequestUris = exchange.getAttributeOrDefault(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR,null);
              Map map = Maps.newHashMapWithExpectedSize(2);
              map.put("URI",originalRequestUris);
              map.put("ExceptionMessage",throwable.getClass().getSimpleName());
              return map;
        }
    }
    ```

* 9 . How To Use Dynamic Predicate With Existing Routes

    * Define A Component Implements `DynamicRouteProcessor`,this processor process serverWebExchange for dynamic route predicate
    
    > the `DynamicRouteProcessor` definition
    
    ```java
    /**
     * Process ServerWebExchange for dynamic route predicate
     * @author chenggang
     * @date 2019/07/17
     */
    public interface DynamicRouteProcessor<T> {
    
        /**
         * preprocess action
         * @param exchange ServerWebExchange
         * @return process Result ,if result is Optional.empty(),then dynamic predicate not working
         */
        Optional<PreprocessResult<T>> preprocess(ServerWebExchange exchange);
    
        /**
         * process to unify config for predicate
         * @param preprocessResult pre process result
         * @param route current route
         * @return
         */
        Optional<DynamicRouteConfig> processConfig(PreprocessResult<T> preprocessResult, Route route);
    
        /**
         * target predicate bean 's class
         * @return RoutePredicateFactory Class
         */
        Optional<Class< ? extends AbstractRoutePredicateFactory>> targetPredicateBeanClass();
    }
    ```
    
    > custom dynamic route processor
    
    ```java
    @Component
    public class CustomDynamicRouteProcessor implements DynamicRouteProcessor {
    
        @Override
        public Optional<PreprocessResult> preprocess(ServerWebExchange exchange) {
            String route = exchange.getRequest().getHeaders().getFirst("route");
            if(StringUtils.isBlank(route)){
                return Optional.of(PreprocessResult.builder().result(false).build());
            }
            return Optional.of(PreprocessResult.builder().result(true).resultData(route).build());
        }
    
        @Override
        public Optional<DynamicRouteConfig> processConfig(PreprocessResult preprocessResult, Route route) {
            if(!preprocessResult.getResult()){
                return Optional.empty();
            }
            Object resultData = preprocessResult.getResultData();
            if(!(resultData instanceof String)){
                return Optional.empty();
            }
            String data = (String) resultData;
            DemoDynamicRoutePredicateFactory.Config config = DemoDynamicRoutePredicateFactory.Config.builder().header(data).route(route).build();
            return Optional.of(config);
        }
    
        @Override
        public Optional<Class<? extends AbstractRoutePredicateFactory>> targetPredicateBeanClass() {
            return Optional.of(DemoDynamicRoutePredicateFactory.class);
        }
    }

    ```
    
    > Define a `AbstractRoutePredicateFactory` ,the `Config` Class Must Implements `DynamicRouteConfig`
    
    ```java
    @Slf4j
    @Component
    public class DemoDynamicRoutePredicateFactory extends AbstractRoutePredicateFactory<DemoDynamicRoutePredicateFactory.Config> {
    
        public DemoDynamicRoutePredicateFactory() {
            super(Config.class);
        }
    
        @Override
        public Predicate<ServerWebExchange> apply(Config config) {
            return exchange -> {
                Route route = config.getRoute();
                if(Objects.isNull(route.getUri())){
                    log.debug("Route Uri Is NUll Return False,RouteID:{}",route.getId());
                    return false;
                }
                String routeUriHost = route.getUri().getHost();
                String headerData = config.getHeader();
                if(StringUtils.isBlank(routeUriHost) || StringUtils.isBlank(headerData)){
                    log.debug("Route Uri Host Or HeaderData Is Blank Return False,RouteID:{}",route.getId());
                    return false;
                }
                if(routeUriHost.equalsIgnoreCase(headerData)){
                    log.debug("Route Uri Host Matched Header Data Return True,RouteID:{}",route.getId());
                    route.getFilters();
                    return true;
                }
                log.debug("Route Uri Not Matched Return False,RouteID:{}",route.getId());
                return false;
            };
        }
    
        @Getter
        @Setter
        @Builder
        @ToString
        @AllArgsConstructor
        public static class Config implements DynamicRouteConfig {
    
            private Route route;
            private String header;
    
        }
    }
    ```
    
    This Feature Support dynamic predicate with existing routes,Fox example: You can according the custom header to match the loadbalance route,
    
    * More logical detail to see `DynamicRoutePredicateHandlerMapping`