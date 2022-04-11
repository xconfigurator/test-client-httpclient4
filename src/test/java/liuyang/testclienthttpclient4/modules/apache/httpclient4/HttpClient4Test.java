package liuyang.testclienthttpclient4.modules.apache.httpclient4;

import liuyang.testclienthttpclient4.common.utils.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 说明：配合《图解HTTP》一起看
 *
 * @author liuyang(wx)
 * @since 2022/4/11
 */
@Slf4j
public class HttpClient4Test {

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
     * enctype="application/x-www-form-urlencoded"
     */
    @Test
    void test12PostForm() {
        // TODO

    }

    /**
     * 发送json类型的post请求
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=13
     */
    @Test
    void test13PostJSON() {
        // TODO
    }

    /**
     * 发送上传文件的post请求
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=14
     *
     * POST文件
     * enctype="multipart/form-data"
     */
    @Test
    void test14FileUpload() {
        // TODO
    }

    /**
     * 为何要绕过https安全认证
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=15
     */
    @Test
    void test15Https() {
        // TODO
    }

    /**
     * httpclient连接池和通用工具类封装
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=16
     */
    @Test
    void test16ConnectionPool() {
        // TODO
    }

}
