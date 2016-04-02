package net.shad.rmqrx.test;

import net.shad.rmqrx.config.RxBootConfig;
import net.shad.rmqrx.test.config.TestResourcesConfig;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author shad
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({
        @ContextConfiguration(classes = TestResourcesConfig.class),
        @ContextConfiguration(classes = ConsumersIT.ConsumerConfig.class),
})
public class ConsumersIT {

    @Configuration
    @ComponentScan("net.shad.rmqrx.test.consumer")
    @Import(RxBootConfig.class)
    public static class ConsumerConfig {}

    private static final Logger LOGGER = Logger.getLogger(ConsumersIT.class);

    @Test
    public void rxLong() throws InterruptedException {
        sleep(1200000);
    }

    @Test
    public void rxShort() throws InterruptedException {
        sleep(20000);
    }

    private void sleep(int millis) throws InterruptedException {
        LOGGER.info("thread will sleep " + millis + " ms");
        Thread.sleep(millis);
    }
}