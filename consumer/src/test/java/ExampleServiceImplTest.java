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
        exampleService.test();
    }

}
