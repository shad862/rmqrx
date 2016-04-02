package net.shad.rmqrx.test;

import net.shad.rmqrx.RxMessage;
import net.shad.rmqrx.config.RxBootConfig;
import net.shad.rmqrx.test.config.TestResourcesConfig;
import net.shad.rmqrx.test.producer.PromiseAdderProducer;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.rx.Promise;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author shad
 *
 * IntegrationFlow -> Amqp
 * http://stackoverflow.com/questions/24673960/inbound-and-outbound-gateway-amqp-annotation
 * RabbitTemplate timeout
 * http://forum.spring.io/forum/spring-projects/integration/amqp/122143-durable-rpc-with-spring-integration-and-rabbitmq
 *
 * AMQP understanding
 * http://spring.io/blog/2010/06/14/understanding-amqp-the-protocol-used-by-rabbitmq/
 * http://www.rabbitmq.com/tutorials/tutorial-six-java.html
 * http://docs.spring.io/spring-amqp/reference/html/sample-apps.html
 * http://www.javacodegeeks.com/2015/01/high-available-amqp-backed-message-channels-via-spring-integration-and-rabbitmq.html
 * http://franckyarch.blogspot.com/2011/07/rabbit-mq-spring-amqp-waiting-response.html
 *
 * https://typesafe.com/activator/template/akka-scala-spring *
 * @see org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
 *
 * http://www.apprenticeshipnotes.org/2013/03/springframework-using-annotations-to_14.html
 * http://www.springindepth.com/book/in-depth-ioc-bean-post-processors-and-beanFactory-post-processors.html
 * http://docs.spring.io/spring/docs/2.5.x/api/org/springframework/beans/factory/config/BeanPostProcessor.html
 * http://artofsoftwarereuse.com/2011/01/19/using-spring-java-annotations-to-inject-reusable-capabilities/
 * http://stackoverflow.com/questions/9126888/how-to-add-a-custom-annotation-to-spring-mvc
 * http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/beans.html#beans-factory-extension-bpp
 * http://forum.spring.io/forum/spring-projects/container/82717-custom-annotation-processing
 * http://stackoverflow.com/questions/15268544/how-do-spring-annotations-work
 * http://projectreactor.io/docs/reference/
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({
        @ContextConfiguration(classes = TestResourcesConfig.class),
        @ContextConfiguration(classes = {
                RxBootConfig.class,
                ProducersIT.ProducersScanConfig.class
        }),
})
public class ProducersIT {

    @Configuration
    @IntegrationComponentScan("net.shad.rmqrx.test.producer")
    public static class ProducersScanConfig{}

    private static Logger logger = Logger.getLogger(ProducersIT.class);

/*    @Autowired
    @Qualifier(value = "rxMainRequestChannel")
    DirectChannel rpcChannel;

    @Autowired
    @Qualifier(value = "rxMainResponseChannel")
    SubscribableChannel rpcChannelResponse;*/

/*    @Autowired
    RabbitAdmin rabbitAdmin;*/

/*    @Test
    public void rpcHelloWorldPlus() throws InterruptedException {
        rpcChannelResponse.subscribe(logger::info);
        rpcChannel.send(withPayload("Shad").
                setHeader("rxRoutingKey", "hello.test.test2").
                setHeader("rxExchangeName", "rx_main_exchange").
                build());
        Thread.sleep(60000);
    }*/

    @Autowired
    private net.shad.rmqrx.test.producer.PromiseAdderProducer adder;

    @Test
    public void promise() throws InterruptedException {
        List<Integer> params = new ArrayList<>();
        params.add(1);
        params.add(2);

        RxMessage<Integer> result = adder.add(params).await();
        assertTrue(result.getPayload() == 3);

        Promise<RxMessage<Integer>> promise = adder.add(params);
        promise.onComplete(res -> assertTrue(res.get().getPayload() == 3));
    }

/*    @Test
    public void rx_rpc_1_queue_01() throws InterruptedException {
        rpcChannelResponse.subscribe(message -> logger.info(message));
        rpcChannel.send(withPayload("Hello").
                setHeader("rxRoutingKey", "rx_rpc_1_queue").
                setHeader("rxExchangeName", "rx_main_exchange").
                build());
        Thread.sleep(60000);
    }*/

/*    @Autowired
    private NotifierProducerPromise rxNotifierFinder;

    @Test
    public void getNotifier() throws InterruptedException {
        Map<String, String> rrns = new HashMap<>();
        rrns.put("1", "141828178924");
        Promise<Map<String, ScheduleNotifier>> result = rxNotifierFinder.find(rrns).onSuccess(ns -> ns.forEach((id, sn) -> logger.info(id + " " + sn)));
        Map<String, ScheduleNotifier> await = result.await(5, TimeUnit.MINUTES);
        Assert.assertTrue(await.size() > 0);
        Thread.sleep(60000);
    }*/

    /*    @Autowired
    private NotifierProducer syncProducer;

    @Autowired
    private NotifierProducerAsync asyncProducer;

    @Autowired
    private NotifierProducerListenable listenableProducer;*/

        /*@Test
    public void produceNotifiers(){
        List<ScheduleNotifier> scheduleNotifiers = syncProducer.find(Arrays.asList("1", "2"));
        Assert.assertTrue(scheduleNotifiers.size() > 0);
    }

    @Test
    public void asyncProduceNotifier() throws ExecutionException, InterruptedException {
        Future<List<ScheduleNotifier>> future = asyncProducer.find(Arrays.asList("1"));
        Assert.assertTrue(future.get().size() > 0);
    }

    @Test
    public void listenableProduceNotifier() throws ExecutionException, InterruptedException {
        listenableProducer.find(Arrays.asList("1")).addCallback(
                        ns -> Assert.assertTrue(ns.size() > 0),
                        err -> Assert.assertTrue(true));
        Thread.sleep(60000);
    }
*/
}
