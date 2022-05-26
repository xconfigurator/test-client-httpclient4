package liuyang.testclienthttpclient4.modules.apache.httpclient4.utils.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.List;

/**
 * 本包内的HttpClientUtil是配合使用同包下的JsonUtil
 * @author liuyang(wx)
 * @since 2022/5/26
 */
@Slf4j
public class HttpClientUtilTest {

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
