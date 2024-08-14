import cn.hutool.cron.CronUtil;
import com.jiangying.ExampleSpringbootConsumerApplication;
import com.jiangying.customer.ExampleServiceImpl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(classes= ExampleSpringbootConsumerApplication.class)
class ExampleServiceImplTest {

    @Resource
    private ExampleServiceImpl exampleService;

    @Test
    void test() {
        CronUtil.stop();
        exampleService.test();
    }

}
