package liuyang.testclienthttpclient4.modules.apache.httpclient4.utils.fastjson;

import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
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
 * @since 2022/5/25
 */
public class HttpClientUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final int MAX_SIZE = 50;                     // 连接池最大连接数
    private static final int MAX_PER_ROUTE_SIZE = 50;           // 每个路由默认有多少连接数
    private static final int TIME_OUT_TCP = 5000;               // TCP连接建立时间
    private static final int TIME_OUT_REQUEST = 5000;           // 获取响应超时
    private static final int TIME_OUT_GET_CONN_FROM_POOL = 5000;// 从连接池中获取连接超时

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
            LOGGER.error(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            //e.printStackTrace();
            LOGGER.error(e.getMessage(), e);
        } catch (KeyManagementException e) {
            //e.printStackTrace();
            LOGGER.error(e.getMessage(), e);
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
                , new String[] {"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"}
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
        try (
                CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
                CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
        ) {
            return EntityUtils.toString(response.getEntity());
        } catch(Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    // TODO
    // 增加一个可以添加url参数的方法

}
