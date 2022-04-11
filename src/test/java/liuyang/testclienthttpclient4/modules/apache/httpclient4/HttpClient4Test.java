package liuyang.testclienthttpclient4.modules.apache.httpclient4;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
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
    void test05() {
        // TODO
    }

    /**
     * get有参数请求URLEncode
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=6
     */
    @Test
    void test06() {
        // TODO
    }

    /**
     * 获取响应头以及相应的Content-Type
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=7
     */
    @Test
    void test07() {
        // TODO
    }

    /**
     * 保存网络图片到本地
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=8
     */
    @Test
    void test08() {
        // TODO
    }

    /**
     * 设置访问代理
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=9
     */
    @Test
    void test09() {
        // TODO
    }

    /**
     * 连接超时和读取超时
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=10
     */
    @Test
    void test10() {
        // TODO
    }

    /**
     * MIME type和Content-Type
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=11
     */
    @Test
    void test11() {
        // TODO
    }

    /**
     * 发送表单类型post请求
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=12
     */
    @Test
    void test12() {
        // TODO
    }

    /**
     * 发送json类型的post请求
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=13
     */
    @Test
    void test13() {
        // TODO
    }

    /**
     * 发送上传文件的post请求
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=14
     */
    @Test
    void test14() {
        // TODO
    }

    /**
     * 为何要绕过https安全认证
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=15
     */
    @Test
    void test15() {
        // TODO
    }

    /**
     * httpclient连接池和通用工具类封装
     * 视频：https://www.bilibili.com/video/BV1W54y1s7BZ?p=16
     */
    @Test
    void test16() {
        // TODO
    }

}
