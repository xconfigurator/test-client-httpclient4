package liuyang.testclienthttpclient4.modules.apache.httpclient4.utils;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.util.StringUtils;

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
 * 使用连接池(需要配合容器)
 * 对Apache HttpClient 4.5.x 封装
 *
 * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=16
 * @author liuyang(wx)
 * @since 2022/4/12 postJSON ok
 */
@Slf4j
public class HttpClientUtil {
    private static final int MAX_SIZE = 50;
    private static final int MAX_PER_ROUTE_SIZE = 50;
    private static final int TIME_OUT_TCP = 5000;// TCP连接建立时间
    private static final int TIME_OUT_REQUEST = 5000;// 获取响应超时
    private static final int TIME_OUT_GET_CONN_FROM_POOL = 5000;// 从连接池中获取连接超时

    private static HttpClientBuilder httpClientBuilder = HttpClients.custom();
    static {
        Registry<ConnectionSocketFactory> registry = null;
        // http & https
        try {
            registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https",skipValidationHttpsConnectionSocketFactory())
                    .build();

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
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

    public static String postJSON(String url, Object obj) {

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json; charset=UTF-8");

        if (obj != null) { // 202204121436 add 忠实反应发送方的意思
            StringEntity stringEntity = new StringEntity(new Gson().toJson(obj), StandardCharsets.UTF_8);
            httpPost.setEntity(stringEntity);
        }
        try(
                CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
                CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
        ) {
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static String postForm() {
        // TODO
        return null;
    }

    public static String postFile() {
        // TODO
        return null;
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
}
