package liuyang.testclienthttpclient4.modules.scheduler.spring;

import liuyang.testclienthttpclient4.modules.retry.RetryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author liuyang(wx)
 * @since 2022/6/6
 */
@ConditionalOnProperty(prefix = "enable", name = "modules.scheduler.spring", havingValue = "true")
@Service
@Slf4j
public class SchedulerService {

    @Autowired
    private RetryService retryService;

    @Scheduled(cron = "0/30 * * * * MON-SAT")
    public void testRetry() {
        log.info("test Retry...");
        retryService.foo();
    }
}
