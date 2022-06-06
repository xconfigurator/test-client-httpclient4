package liuyang.testclienthttpclient4.modules.retry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

/**
 * @author liuyang(wx)
 * @since 2022/6/6
 */
@Service
@Slf4j
public class RetryService {

    public static int counter = 0;

    @Retryable(value = {Exception.class}
                , maxAttempts = 4
                , backoff = @Backoff(value = 2000l, multiplier = 1))
    public void foo() {
        log.info("foo at {}", System.currentTimeMillis());

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 【测试场景1】 每次都抛异常，所以会确定重传3次（此时会执行@Recover方法）
        //log.info("foo 运行将抛出异常...");
        //throw new RuntimeException("hey");

        // 【测试场景2】 模拟在第3次执行的时候成功（只要执行成功了就不会再执行@Recover方法）
        // 这里只是测试效果，不要使用这种
        if (++counter <= 2) {
            log.info("foo 运行将抛出异常...");
            throw new RuntimeException("hey");
        } else {
            log.info("foo 执行成功。");
            counter = 0;// 复位
        }
    }

    @Recover
    public void fooRecover() {
        log.info("fooRecover");
        log.info("fooRecover 有尝试重传3次， 将丢弃这一批数据... 策略：选出当前最大的告警编号，并记录偏移量（使用DataCacheUtil）");

        // counter 复位
        counter = 0;
    }
}
