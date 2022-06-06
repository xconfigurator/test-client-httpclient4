package liuyang.testclienthttpclient4.modules.retry.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;

/**
 * 参考资料：
 * https://www.jianshu.com/p/702fd5f3adf2
 *
 * @author liuyang(wx)
 * @since 2022/6/6
 */
@Configuration
@EnableRetry
// proxyTargetClass属性为true时，使用CGLIB。
//@EnableAspectJAutoProxy(proxyTargetClass = true)// 尝试一下是否必须添加aspectjweaver // 20220606 实测，必须添加aspectjweaver，但不需要添加本注解
public class RetryConfig {

    // pom.xml
    /*
       <dependency>
            <groupId>org.springframework.retry</groupId>
            <artifactId>spring-retry</artifactId>
       </dependency>
       <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
       </dependency>
     */

}
