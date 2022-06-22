### 一句话解释
- 就当它是axios用。
- 注：这个类干了一件事，就是把Apache 的HttpClient工具封装得使用方式更像axios。原生Apache HttpClient可配项还是挺多，你看HttpClientUtil里就知道了，超时也是在那个里面配置。
- 注：这个东西是同步的，不像axios那样基于promise。如果需要更骚气的用法，Java也有办法，结合ComleteableFuture接口和Java的线程池框架也可以完成很复杂的交互。用到再说。

### 使用方法
- liuyang.testclienthttpclient4.modules.apache.httpclient4.utils.fastjson.HttpClientUtilTest
> 摘要(调用示例)
```java
    @Test
    void test() {
        // 场景：使用工具类发送值对象
        String result = HttpClientUtil.postJSON("http://localhost/inma_smart/util", produceRestResult01());
        log.info("result = {}", result);

        // 场景：提取返回值JSON中的信息
        // 示例：{"code":200,"reason":"请求成功","result":null}
        // JSON和JSONObject是fastjson提供的类
        JSONObject jsonObject = JSON.parseObject(result);
        log.info("code = {}", jsonObject.get("code"));
        log.info("reason = {}", jsonObject.get("reason"));
        log.info("result = {}", jsonObject.get("result"));
    }
```

### 组件以及依赖
> 两个文件都需要，HttpClientUtil里面调用了JsonUtil。
- liuyang.testclienthttpclient4.modules.apache.httpclient4.utils.fastjson.HttpClientUtil.java
- liuyang.testclienthttpclient4.modules.apache.httpclient4.utils.fastjson.JsonUtil.java
> 可用方法
目前就俩
- get(String url)
- post(String url, Object msg)
> 依赖 pom.xml(如果只需要这东西)
```xml
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>4.5.6</version>
        </dependency>

        <!-- FastJSON -->
        <!-- https://mvnrepository.com/artifact/com.alibaba/fastjson -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <!--<version>1.2.76</version>-->
            <version>1.2.83</version><!-- 小于80的版本爆出安全漏洞 liuyang 20220606 upgrade-->
        </dependency>
```

