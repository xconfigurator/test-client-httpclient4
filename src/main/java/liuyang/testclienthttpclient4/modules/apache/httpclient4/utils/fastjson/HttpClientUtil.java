package liuyang.testclienthttpclient4.modules.apache.httpclient4.utils.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用连接池（需要配合容器使用）
 * 1. 对Apache HttpClient 4.5.x的封装
 * 2. 序列化反序列化组件依赖同包下的JsonUtil.java方便对Fastjson序列化/反序列化特性的统一配置。
 *
 * @author liuyang(wx)
 * @since 2022/5/25 连接池、https、postJSON
 *        2022/6/6  增加get方法
 *        2022/6/8  测试发现如果关闭连接，则会导致连接池也关闭。目前方案：考虑暂时注销连接池。
 *        2022/6/23 重新开启线程池，修正response关闭方式。
 *        2022/7/15 根据SonaLint修改
 */
public class HttpClientUtil {
    private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final int MAX_SIZE = 50;                     // 连接池最大连接数
    private static final int MAX_PER_ROUTE_SIZE = 50;           // 每个路由默认有多少连接数
    private static final int TIME_OUT_TCP = 5000;               // TCP连接建立时间
    private static final int TIME_OUT_REQUEST = 5000;           // 获取响应超时
    private static final int TIME_OUT_GET_CONN_FROM_POOL = 5000;// 从连接池中获取连接超时

    private HttpClientUtil() {}

    private static HttpClientBuilder httpClientBuilder = HttpClients.custom();
    static {
        Registry<ConnectionSocketFactory> registry = null;
        // http & https
        try {
            registry = RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.INSTANCE)
                        .register("https", skipValidationHttpsConnectionSocketFactory())
                        .build();
        } catch (KeyStoreException e) {
            //e.printStackTrace();
            log.error(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            //e.printStackTrace();
            log.error(e.getMessage(), e);
        } catch (KeyManagementException e) {
            //e.printStackTrace();
            log.error(e.getMessage(), e);
        }
        // 连接池
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(registry);
        poolingHttpClientConnectionManager.setMaxTotal(MAX_SIZE);// 连接池最大连接数
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE_SIZE);// 每个路由默认有多少连接数
        httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);
        // 连接超时
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(TIME_OUT_TCP)
                .setSocketTimeout(TIME_OUT_REQUEST)
                .setConnectionRequestTimeout(TIME_OUT_GET_CONN_FROM_POOL)
                .build();
        httpClientBuilder.setDefaultRequestConfig(requestConfig);
        // 默认header
        List<Header> defaultHeaders = new ArrayList<>();
        defaultHeaders.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36 Edg/100.0.1185.36"));
        httpClientBuilder.setDefaultHeaders(defaultHeaders);
    }

    private static ConnectionSocketFactory skipValidationHttpsConnectionSocketFactory() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
        SSLContext sslContext = sslContextBuilder.loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                //return false;
                return true;
            }
        }).build();
        return new SSLConnectionSocketFactory(
                sslContext
                , new String[] {"TLSv1.2"}
                , null
                , NoopHostnameVerifier.INSTANCE);
    }

    public static String postJSON(String url, Object obj) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json; charset=UTF-8");

        if (obj != null) {// 忠实反应发送方的意思
            StringEntity stringEntity = new StringEntity(JsonUtil.toJSONString(obj), StandardCharsets.UTF_8);
            httpPost.setEntity(stringEntity);
        }
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        CloseableHttpResponse closeableHttpResponse = null;
        try {
            closeableHttpResponse = closeableHttpClient.execute(httpPost);
            //return EntityUtils.toString(closeableHttpResponse.getEntity());
            HttpEntity entity = closeableHttpResponse.getEntity();
            String resp = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);// 确保关闭流
            return resp;
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            HttpClientUtils.closeQuietly(closeableHttpResponse);
        }
    }

    public static String get(String url) {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        CloseableHttpResponse closeableHttpResponse = null;
        try {
            closeableHttpResponse = closeableHttpClient.execute(httpGet);
            HttpEntity entity = closeableHttpResponse.getEntity();
            String resp = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);// 确保流关闭。
            return resp;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            HttpClientUtils.closeQuietly(closeableHttpResponse);
        }
    }

    public static JSONObject get2(String url) {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        CloseableHttpResponse closeableHttpResponse = null;
        JSONObject jsonObject = null;
        try {
            closeableHttpResponse = closeableHttpClient.execute(httpGet);
            HttpEntity entity = closeableHttpResponse.getEntity();
            String resp = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);// 确保流关闭。

            jsonObject = JSON.parseObject(resp + "xx");

            return jsonObject;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("bar");
        } finally {
            HttpClientUtils.closeQuietly(closeableHttpResponse);
        }
    }
}
