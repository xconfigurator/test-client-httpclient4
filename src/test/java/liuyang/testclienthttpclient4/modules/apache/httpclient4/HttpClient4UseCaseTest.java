package liuyang.testclienthttpclient4.modules.apache.httpclient4;

import liuyang.testclienthttpclient4.common.utils.IsWsdlReachable;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * @author liuyang(wx)
 * @since 2022/4/20
 */
@Slf4j
public class HttpClient4UseCaseTest {

    @Test
    void testIsWsdlReachable () {
        // 1. 正常
        //String url = "http://20.48.250.70:8080/pdt-nms/ProvinceTopService?wsdl";// 正常
        // 2. IP可达，端口无监听
        //String url = "http://20.48.250.70:8082/pdt-nms/ProvinceTopService?wsdl";
        // 3. IP不可达
        //String url = "http://1.2.3.4:8082/pdt-nms/ProvinceTopService?wsdl";
        // 4. IP可达，端口有监听，但服务不匹配
        //String url = "http://20.48.250.70:/pdt-nms/ProvinceTopService?wsdl";// SoapWs/CityAlmServiceWsdl.wsdl

        String url = "http://20.48.250.70:8080/pdt-nms/ProvinceTaskService?wsdl";
        boolean test = IsWsdlReachable.test(url);
        log.info("test = {}", test);
    }

    @Test
    void testAvailable() {
        long begin = System.currentTimeMillis();

        String url = "http://20.48.250.70:8080/pdt-nms/ProvinceTopService?wsdl";
        //String url = "http://20.48.250.70:8082/pdt-nms/ProvinceTopService?wsdl";
        //String url = "http://1.2.3.4:8082/pdt-nms/ProvinceTopService?wsdl";
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(RequestConfig.custom().setConnectTimeout(3000).setSocketTimeout(60000).build());
        try (
                CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
                CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
        ) {
            HttpEntity entity = closeableHttpResponse.getEntity();
            String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            log.info("entityStr = {}", entityStr);
            EntityUtils.consume(entity);// 确保流关闭。
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            long end = System.currentTimeMillis();
            log.info("耗时: {} ms", end - begin);
        }
    }
}
