package liuyang.testclienthttpclient4.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * 测试请求资源是否可以获得
 *
 * @author liuyang(wx)
 * @since 2022/4/20
 */
@Slf4j
public class IsWsdlReachable {

    private static final int CONNECT_TIMEOUT = 1000;// TCP握手超时
    private static final int SOCKET_TIMEOUT = 60000;// 请求响应超时

    public static boolean test(String wsdlLocation) {
        long begin = System.currentTimeMillis();
        HttpGet httpGet = new HttpGet(wsdlLocation);
        httpGet.setConfig(RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build());
        try (
                CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
                CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
                //CloseableHttpResponse closeableHttpResponse = HttpClientBuilder.create().disableAutomaticRetries().build().execute(httpGet);
        ){
            HttpEntity entity = closeableHttpResponse.getEntity();
            String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);// 确保流关闭。
            return true;// 能正确消费就证明可以访问
        } catch (NoRouteToHostException noRouteToHostException) {
            log.info("网线被拔出");
            log.debug(noRouteToHostException.getMessage(), noRouteToHostException);
            return false;
        } catch (ConnectException connectException) {
            log.info("资源不可达 {} ：IP可达，端口无监听", wsdlLocation);
            log.debug(connectException.getMessage(), connectException);
            return false;
        } catch (SocketTimeoutException socketTimeoutException) {
            log.info("资源不可达 {} ：IP不可达", wsdlLocation);
            log.debug(socketTimeoutException.getMessage(), socketTimeoutException);
            return false;
        } catch(Exception e) {
            log.info("资源不可达 {}", wsdlLocation);
            log.debug(e.getMessage(), e);
            return false;
        } finally {
            long end = System.currentTimeMillis();
            log.debug("耗时: {} ms", end - begin);
        }
    }
}
