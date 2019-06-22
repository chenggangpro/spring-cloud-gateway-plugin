# spring-cloud-gateway-plugin
Spring Cloud Gateway Extra Plugin

[![Build Status](https://travis-ci.com/chenggangpro/spring-cloud-gateway-plugin.svg?branch=master)](https://travis-ci.com/chenggangpro/spring-cloud-gateway-plugin)

## Current Version: 2.1.SR2.1.RELEASE

### This Plugin Support Below Features:

* [x] Cache Request Body And Form Body
* [x] Add Request Log Filter To Log Request And Response
* [x] Add Read Json Response Body And Log Response Body
* [x] Add Global Exception Handler With Json
* [x] Add Custom Exception Handler
* [x] Add Grey Route With Ribbon
* [x] Each Route Use different Hystrix CommandKey 

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
        <version>2.1.SR2.RELEASE</version>
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
