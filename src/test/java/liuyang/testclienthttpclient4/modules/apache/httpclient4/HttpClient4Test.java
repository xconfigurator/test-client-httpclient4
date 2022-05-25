package liuyang.testclienthttpclient4.modules.apache.httpclient4;

import com.google.gson.Gson;
import liuyang.testclienthttpclient4.common.utils.IdUtils;
import liuyang.testclienthttpclient4.modules.apache.httpclient4.dto.UserDTO;
import liuyang.testclienthttpclient4.modules.apache.httpclient4.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.swing.text.AbstractDocument;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/**
 * 说明：配合《图解HTTP》一起看
 * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ
 *
 * @author liuyang(wx)
 * @since 2022/4/11
 */
@Slf4j
public class HttpClient4Test {

    private static final int CONNECT_TIMEOUT = 5000;// TCP握手超时
    private static final int SOCKET_TIMEOUT = 60000;// 请求响应超时

    /**
     * Get请求 ，无参
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=4
     */
    @Test
    void test04Get() {
        String url = "https://www.baidu.com";
        HttpGet httpGet = new HttpGet(url);
        try (
                CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
                CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
        ){
            HttpEntity entity = closeableHttpResponse.getEntity();
            String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            log.info("entityStr = {}", entityStr);
            EntityUtils.consume(entity);// 确保流关闭。
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * user-agent和refer请求头的作用
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=5
     */
    @Test
    void test05RequestHeader() {
        String url = "https://www.baidu.com";
        HttpGet httpGet = new HttpGet(url);
        // /////////////////////////////////////////////////////////
        // 添加请求头 begin
        // User-Agent: 如果没有User-Agent，可能会被网站识别为爬虫。
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36 Edg/100.0.1185.36");
        // Referer: 如果网站具有防盗链功能，则把它的域名写到这个请求头上
        httpGet.addHeader("Referer", url);
        // 添加请求头 end
        // /////////////////////////////////////////////////////////
        try (
                CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
                CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
        ){
            HttpEntity entity = closeableHttpResponse.getEntity();
            String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            log.info("entityStr = {}", entityStr);
            EntityUtils.consume(entity);// 确保流关闭。
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * get有参数请求URLEncode - Get方式提交表单
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=6
     * 配合项目：test-spring-boot-env (关闭Spring Security)
     * 核心代码：URLEncoder.encode("for test!", StandardCharsets.UTF_8.name())
     */
    @Test
    void test06GetForm() throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append("http://localhost/test-client-httpclient4/test1");
        sb.append("?id=").append(IdUtils.nextTaskId());
        sb.append("&username=").append("liuyang");
        // /////////////////////////////////////////////////////////
        // URLEncode begin 9:50 浏览器会自动做这个动作，但HttpClient需要手动来做。
        //sb.append("&info=").append("for test!");// 如果不转码，带空格是会报错的！显然不对！ 如果带| 也会报错。
        //sb.append("&info=").append("for+test!");// 如果不转码，接收处会把+替换为空格！显然不对！
        //sb.append("&info=").append(URLEncoder.encode("for test!", StandardCharsets.UTF_8.name()));// ok
        sb.append("&info=").append(URLEncoder.encode("for+test!", StandardCharsets.UTF_8.name()));// ok
        // URLEncode end
        // /////////////////////////////////////////////////////////
        sb.append("&d=").append(1.1d);
        sb.append("&bd=").append(new BigDecimal("1234582478124732847219072183271"));

        HttpGet httpGet = new HttpGet(sb.toString());
        try (
                CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
                CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
        ){
            HttpEntity entity = closeableHttpResponse.getEntity();
            String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            log.info("entityStr = {}", entityStr);
            EntityUtils.consume(entity);// 确保流关闭。
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 获取响应头以及相应的Content-Type
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=7
     */
    @Test
    void test07Response() {
        String url = "https://www.baidu.com";
        HttpGet httpGet = new HttpGet(url);
        try (
                CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
                CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
        ){
            HttpEntity entity = closeableHttpResponse.getEntity();
            String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            log.info("entityStr = {}", entityStr);
            EntityUtils.consume(entity);// 确保流关闭。

            // /////////////////////////////////////////////////////////
            // Status
            StatusLine statusLine = closeableHttpResponse.getStatusLine();
            log.info("Status = {}", statusLine.getStatusCode());
            // 响应首部 Response Header
            Header[] allHeaders = closeableHttpResponse.getAllHeaders();
            Arrays.asList(allHeaders).forEach(h -> System.out.println(h.getName() + " = " + h.getValue()));
            // 实体首部 Content-Type
            Header contentType = entity.getContentType();
            log.info("Content-Type name = {}", contentType.getName());
            log.info("Content-Type value = {}", contentType.getValue());
            // /////////////////////////////////////////////////////////
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 保存网络图片到本地
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=8
     */
    @Test
    void test08Images() {
        String url = "https://i0.hdslb.com/bfs/archive/ca375eb31fa90b8e23b88ed3433c2f60de1c2e6e.png";
        HttpGet httpGet = new HttpGet(url);
        try (
                CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
                CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
        ){
            HttpEntity entity = closeableHttpResponse.getEntity();
            // /////////////////////////////////////////////////////////
            // 图片 begin
            // 1. 处理保存到本地的文件后缀
            String contentType = entity.getContentType().getValue();
            String suffix = ".jpg";
            if (contentType.contains("jpg") || contentType.contains("jpeg")) {
                suffix = ".jpg";
            }
            if (contentType.contains("bmp") || contentType.contains("bitmap")) {
                suffix = ".bmp";
            }
            if (contentType.contains("png")) {
                suffix = ".png";
            }
            if (contentType.contains("gif")) {
                suffix = ".gif";
            }
            // 2. 处理图片本身
            byte[] bytes = EntityUtils.toByteArray(entity);
            String path = "d:/" + "foo_" + System.currentTimeMillis() + suffix;
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(bytes);
            fos.close();
            // 图片 end
            // /////////////////////////////////////////////////////////

            //String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            //log.info("entityStr = {}", entityStr);
            EntityUtils.consume(entity);// 确保流关闭。
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 设置访问代理 -> 防止被反爬虫程序禁掉
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=9
     */
    @Test
    void test09Proxy() {
        String url = "https://www.baidu.com";
        HttpGet httpGet = new HttpGet(url);
        // /////////////////////////////////////////////////////////
        // 代理 begin
        HttpHost proxy = new HttpHost("221.122.91.65",  80);// 免费或者有偿获取的IP http://www.66ip.cn/
        RequestConfig restConfig = RequestConfig.custom().setProxy(proxy).build();
        httpGet.setConfig(restConfig);// 每个请求的配置会覆盖全局的配置
        // 代理 end
        // /////////////////////////////////////////////////////////
        try (
                CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
                CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
        ){
            HttpEntity entity = closeableHttpResponse.getEntity();
            String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            log.info("entityStr = {}", entityStr);
            EntityUtils.consume(entity);// 确保流关闭。
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 连接超时和读取超时
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=10
     * 重点：三种超时
     */
    @Test
    void test10Timeout() {
        String url = "https://github.com";// 你懂的。
        HttpGet httpGet = new HttpGet(url);
        // /////////////////////////////////////////////////////////
        // 超时 begin
        RequestConfig requestConfig = RequestConfig.custom()
                // 1. 连接超时（完成TCP三次握手的时间上限） 单位毫秒 20220411 实测可观察到超时
                // Connect to github.com:443 [github.com/20.205.243.166] failed: connect timed out
                // org.apache.http.conn.ConnectTimeoutException: Connect to github.com:443 [github.com/20.205.243.166] failed: connect timed out
                // Caused by: java.net.SocketTimeoutException: connect timed out
                //.setConnectTimeout(1000)
                .setConnectTimeout(5000)
                // 2. 读取超时(表示从请求的网址处获得相应数据的时间间隔) 20220411 复现
                // 可以通过连接一个定制的Controller，在Controller中Sleep超过超时的时间，则触发该阈值。
                // Read timed out
                // java.net.SocketTimeoutException: Read timed out
                .setSocketTimeout(5000)
                // 3. 设置从连接池中获取连接的超时时间
                .setConnectionRequestTimeout(5000)
                .build();
        httpGet.setConfig(requestConfig);
        // 超时 end
        // /////////////////////////////////////////////////////////
        try (
                CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
                CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
        ){
            HttpEntity entity = closeableHttpResponse.getEntity();
            String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            log.info("entityStr = {}", entityStr);
            EntityUtils.consume(entity);// 确保流关闭。
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * MIME type和Content-Type
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=11
     * 参考文档：https://www.runoob.com/http/http-content-type.html
     */
    @Test
    void test11MIME() {
        // application/x-www-form-urlencoded
        // multipart/form-data
        // application/json
        // text/plain
    }

    /**
     * 发送表单类型post请求
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=12
     *
     * POST表单
     * content-type : application/x-www-form-urlencoded; charset=UTF-8 （HttpClient在使用UrlEncodedFormEntity的时候会默认添加，所以不需要显式设置）
     *
     * 关键点：UrlEncodedFormEntity
     */
    @Test
    void test12PostForm() {
        String url = "http://localhost/test-client-httpclient4/test2";
        HttpPost httpPost = new HttpPost(url);
        //httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=GBK");// 不设置也可, 但设置了就以这个为准。
        // /////////////////////////////////////////////////////////
        // 给Post对象设置参数 begin
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("id", IdUtils.nextTaskId()));
        nameValuePairs.add(new BasicNameValuePair("username", "liuyang"));
        nameValuePairs.add(new BasicNameValuePair("info", "foo test! 中文"));
        nameValuePairs.add(new BasicNameValuePair("d", "1.1"));// 注意观察服务器端double
        nameValuePairs.add(new BasicNameValuePair("bd", "1234582478124732847219072183271"));// 注意观察服务器端BigDecimal
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8);
        httpPost.setEntity(urlEncodedFormEntity);
        // 给Post对象设置参数 end
        // /////////////////////////////////////////////////////////
        try (
                CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
                CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
        ){
            HttpEntity entity = closeableHttpResponse.getEntity();
            String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            log.info("entityStr = {}", entityStr);
            EntityUtils.consume(entity);// 确保流关闭。
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 发送json类型的post请求
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=13
     *
     * POST JSON
     * content-type : application/json; charset=UTF-8 （必须设置）
     * 关键点：StringEntity
     */
    @Test
    void test13PostJSON() throws UnsupportedEncodingException {
        String url = "http://localhost/test-client-httpclient4/test3";
        HttpPost httpPost = new HttpPost(url);
        // 注意要指定！
        httpPost.addHeader("Content-Type", "application/json; charset=UTF-8");// 不设就报错！！ 因为默认类型是：Content type 'text/plain;charset=UTF-8'
        // /////////////////////////////////////////////////////////
        // 给Post对象设置参数 begin
        UserDTO userDTO = new UserDTO();
        userDTO.setId(IdUtils.nextTaskId());
        userDTO.setUsername("liuyang");
        userDTO.setInfo("foo test! 中文");
        userDTO.setD(1.1d);
        userDTO.setBd(new BigDecimal("1234582478124732847219072183271") );
        // Google Gson
        String jsonStr = new Gson().toJson(userDTO);
        // Jackson JSONObject put toString()
        // FastJson JSON.toJSONString()
        StringEntity stringEntity = new StringEntity(jsonStr, StandardCharsets.UTF_8);
        httpPost.setEntity(stringEntity);
        // 对比Form
        //UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8);
        //httpPost.setEntity(urlEncodedFormEntity);
        // 给Post对象设置参数 end
        // /////////////////////////////////////////////////////////
        try (
                CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
                CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
        ){
            HttpEntity entity = closeableHttpResponse.getEntity();
            String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            log.info("entityStr = {}", entityStr);
            EntityUtils.consume(entity);// 确保流关闭。
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 发送上传文件的post请求
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=14
     *
     * POST文件
     * enctype="multipart/form-data"
     *
     * 注：这里演示的是使用HTTP的文件上传，使用FTP的文件上传参考其他TODO代码。
     *
     * 关键点：MultipartEntityBuilder，来自httpmime (不在httpclient依赖包)，见pom.xml
     */
    @Test
    void test14FileUpload() {
        String url = "http://localhost/test-client-httpclient4/test4";
        HttpPost httpPost = new HttpPost(url);
        //httpPost.addHeader("Content-Type", "application/multipart/form-data; charset=UTF-8");// 不设置也可, 但设置了就以这个为准。

        /*
        // 在这写是没用的！
        // /////////////////////////////////////////////////////////
        // 给Post对象设置参数 begin
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("id", IdUtils.nextTaskId()));
        nameValuePairs.add(new BasicNameValuePair("username", "liuyang"));
        nameValuePairs.add(new BasicNameValuePair("info", "foo test! 中文"));
        nameValuePairs.add(new BasicNameValuePair("d", "1.1"));// 注意观察服务器端double
        nameValuePairs.add(new BasicNameValuePair("bd", "1234582478124732847219072183271"));// 注意观察服务器端BigDecimal
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8);
        // 注意：配合File一起发送，需要
        urlEncodedFormEntity.setContentType(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=GBK"));
        httpPost.setEntity(urlEncodedFormEntity);
        // 给Post对象设置参数 end
        // /////////////////////////////////////////////////////////
         */

        // /////////////////////////////////////////////////////////
        // 文件 begin
        // MultipartEntityBuilder
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.setCharset(Consts.UTF_8);
        // multipartEntityBuilder.setContentType(ContentType.MULTIPART_FORM_DATA);// 看看值就知道了，这样会有中文乱码！
        multipartEntityBuilder.setContentType(ContentType.create("multipart/form-data", Consts.UTF_8));
        multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        multipartEntityBuilder.addPart("fileName", new FileBody(new File("d:/foo_1649686014810.png")));
        multipartEntityBuilder.addBinaryBody("fileName", new File("d:/foo_1649686136526.png")); // 记不住参数，Ctrl + P 有惊喜！
        // 其他参数传递
        /*
        multipartEntityBuilder.addTextBody("foo", "bar");                               // UserDTO中没有
        multipartEntityBuilder.addTextBody("id", IdUtils.nextTaskId());                      // UserDTO.id
        multipartEntityBuilder.addTextBody("username", "liuyang");                      // UserDTO.username
        // 普通字段，如果含有中文，不可以通过addTextBody方法，否则就算设置对了ContentType，依然会乱码。
        multipartEntityBuilder.addTextBody("info", "foo test! 中文");                    // UserDTO.info 乱码！-> setContentType解决
        multipartEntityBuilder.addTextBody("d", "1.1");                                 // UserDTO.d
        multipartEntityBuilder.addTextBody("bd", "1234582478124732847219072183271");    // UserDTO.bd
        */
        multipartEntityBuilder.addPart("foo", new StringBody("bar", ContentType.create("text/plain", Consts.UTF_8)));               // UserDTO中没有
        multipartEntityBuilder.addPart("id", new StringBody(IdUtils.nextTaskId(), ContentType.create("text/plain", Consts.UTF_8)));      // UserDTO.id
        multipartEntityBuilder.addPart("username", new StringBody("liuyang", ContentType.create("text/plain", Consts.UTF_8)));       // UserDTO.username
        // 普通字段，如果含有中文，不可以通过addTextBody方法，否则就算设置对了ContentType，依然会乱码。
        multipartEntityBuilder.addPart("info", new StringBody("foo 中文！", ContentType.create("text/plain", Consts.UTF_8)));        // UserDTO.info 乱码！-> setContentType解决
        multipartEntityBuilder.addPart("d", new StringBody("1.1", ContentType.create("text/plain", Consts.UTF_8)));                                 // UserDTO.d
        multipartEntityBuilder.addPart("bd", new StringBody("1234582478124732847219072183271", ContentType.create("text/plain", Consts.UTF_8)));    // UserDTO.bd
        HttpEntity httpEntity = multipartEntityBuilder.build();
        httpPost.setEntity(httpEntity);
        // 文件 end
        // /////////////////////////////////////////////////////////

        try (
                CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
                CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
        ){
            HttpEntity entity = closeableHttpResponse.getEntity();
            String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            log.info("entityStr = {}", entityStr);
            EntityUtils.consume(entity);// 确保流关闭。
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 为何要绕过https安全认证
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=15
     *
     * 应对：自签名场景
     * 方案1：配置证书
     * 方案2：配置httpClient绕过https安全认证
     *
     * 这里显示绕过的方案
     */
    @Test
    void test15Https() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        // 对比test-client/HttpClientSSLTest
        // /////////////////////////////////////////////////////////
        // 主要就是定制HttpClient
        // 注意：这个操作里，不需要在客户端导入证书！
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https",skipValidationHttpsConnectionSocketFactory())
                .build();
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(registry);
        // SSL第四步：创建http请求
        //CloseableHttpClient closeableHttpClient = HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).build();
        // 对比一下普通http的
        // CloseableHttpClient httpClient = HttpClients.createDefault();
        // /////////////////////////////////////////////////////////

        String url = "https://www.baidu.com";// TODO 尝试一下自签名的， 貌似百度这种有合规签名的网站，无论客户端如何指定trust，都能访问通。
        HttpPost httpPost = new HttpPost(url);
        try (
                CloseableHttpClient closeableHttpClient = HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).build();
                CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
                ) {
            HttpEntity entity = closeableHttpResponse.getEntity();
            String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            log.info("entityStr = {}", entityStr);
            EntityUtils.consume(entity);// 确保流关闭。
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private ConnectionSocketFactory skipValidationHttpsConnectionSocketFactory() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
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

    /**
     * httpclient连接池和通用工具类封装
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=16
     */
    @Test
    void test16HttpClientUtil() {
        // 服务器项目是test-spring-boot-env
        //String s = HttpClientUtil.postJSON("http://localhost/test-client-httpclient4/test3", null);// {"msg":"Server ERROR!","code":500}
        //String s = HttpClientUtil.postJSON("http://localhost/test-client-httpclient4/test3", "");// {"msg":"Server ERROR!","code":500}

        UserDTO userDTO = new UserDTO();
        userDTO.setId(IdUtils.nextTaskId());
        userDTO.setUsername("liuyang");
        userDTO.setInfo("foo test! 中文");
        userDTO.setD(1.1d);
        userDTO.setBd(new BigDecimal("1234582478124732847219072183271") );
        String s = HttpClientUtil.postJSON("http://localhost/test-client-httpclient4/test3", userDTO);
        System.out.println(s);
    }

}
