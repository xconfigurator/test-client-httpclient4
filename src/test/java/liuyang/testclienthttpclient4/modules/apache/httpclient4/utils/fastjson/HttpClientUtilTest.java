package liuyang.testclienthttpclient4.modules.apache.httpclient4.utils.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * 本包内的HttpClientUtil是配合使用同包下的JsonUtil
 * @author liuyang(wx)
 * @since 2022/5/26
 */
@Slf4j
public class HttpClientUtilTest {

    // db065523e7dc405e9f9c262673d08b26
    // 7cd013d9b2874d70a2a11f0a460b870c
    @Test
    void test202206061845StopAll() {
        StringBuilder url = new StringBuilder();
        url.append("http://localhost/scheduler/stopAll");
        String result = HttpClientUtil.get(url.toString());
        log.info(result);
    }

    @Test
    void test202206061835StopJob() {
        StringBuilder url = new StringBuilder();
        url.append("http://localhost/scheduler/stop");
        url.append("?taskId=");
        url.append("0ded3c0b3af44a018f502b9e2ea3693f");
        String result = HttpClientUtil.get(url.toString());
        log.info(result);
    }

    //@Test
    @RepeatedTest(value = 4)
    void test202206061834AddJob() {
        StringBuilder url = new StringBuilder();
        url.append("http://localhost/scheduler/add");
        url.append("?delay=");
        url.append(2000l);
        String result = HttpClientUtil.get(url.toString());
        log.info(result);
    }

    @Test
    void test() {
        // 场景：使用工具类发送值对象
        String result = HttpClientUtil.postJSON("http://localhost/inma_smart/util", produceRestResult01());
        log.info("result = {}", result);

        // 场景：提取返回值JSON中的信息
        // 示例：{"code":200,"reason":"请求成功","result":null}
        JSONObject jsonObject = JSON.parseObject(result);
        log.info("code = {}", jsonObject.get("code"));
        log.info("reason = {}", jsonObject.get("reason"));
        log.info("result = {}", jsonObject.get("result"));
    }

    RequestBody<List<String>> produceRestResult01() {
        RequestBody<List<String>> rb = new RequestBody<>();
        rb.setCode(rb.CODE_SUCCESS);
        rb.setReason(rb.DEFAULT_SUCCESS_MESSAGE);
        rb.setResult(Arrays.asList("foo", "bar"));
        return rb;
    }

    @Data
    class RequestBody<T> {
        public static final int CODE_SUCCESS = 200;
        public static final int CODE_FAIL = 500;
        public static final String DEFAULT_SUCCESS_MESSAGE = "操作成功";
        public static final String DEFAULT_ERROR_MESSAGE = "操作失败";

        private int code;
        private String reason;
        private T result;
    }
}
