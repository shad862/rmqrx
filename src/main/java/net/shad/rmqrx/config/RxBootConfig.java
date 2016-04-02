package net.shad.rmqrx.config;

import com.google.common.collect.ImmutableMap;
import net.shad.rmqrx.annotation.RxEnable;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.integration.amqp.channel.PublishSubscribeAmqpChannel;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.amqp.Amqp;

import static net.shad.rmqrx.utils.Jndis.jndiStrFor;
import static org.springframework.amqp.core.BindingBuilder.bind;
import static org.springframework.integration.dsl.amqp.Amqp.publishSubscribeChannel;

/**
 * @author shad
 */
@RxEnable
public class RxBootConfig {

    /**
     * Используется в @RxGateway as asyncExecutor of MessagingGateway
     * @return
     */
    @Bean public AsyncTaskExecutor exec() {
        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
        simpleAsyncTaskExecutor.setThreadNamePrefix("rx-");
        return simpleAsyncTaskExecutor;
    }

    @Bean public CachingConnectionFactory cf() throws Exception {
        CachingConnectionFactory cf = new CachingConnectionFactory();
        cf.setAddresses(jndiStrFor("amqp/rmqrx/addresses"));
        cf.setVirtualHost(jndiStrFor("amqp/rmqrx/virtualhost"));
        cf.setUsername(jndiStrFor("amqp/rmqrx/user"));
        cf.setPassword(jndiStrFor("amqp/rmqrx/password"));
        return cf;
    }

    @Bean public RabbitAdmin rabbitAdmin() throws Exception {
        return new RabbitAdmin(cf());
    }

    @Bean public RabbitTemplate rabbitTemplate() throws Exception {
        RabbitTemplate rt = new RabbitTemplate(cf());
        rt.setReplyTimeout(60000*5);// TODO: 10.12.2015 Вынести время таймаута в конфигурацию
        return rt;
    }

    /************************ main path ************************/
    @Bean public TopicExchange  mainExchange()  throws Exception  { return newTopicExchange("rx_main_exchange");}

    /************************ log path ************************/
    @Bean public TopicExchange  logExchange()   throws Exception  { return newTopicExchange("rx_log_exchange");}
    @Bean public Queue          logStockQueue() throws Exception { return newQueue("rx_log_stock_queue"); }
    @Bean public Binding        logExchangeBinding() throws Exception { // FIXME: 10.12.2015 autodeclare binding
        Binding binding = bind(logExchange()).to(mainExchange()).with("#");
        rabbitAdmin().declareBinding(binding);
        return binding;
    }
    @Bean public Binding        logBinding() throws Exception { // FIXME: 10.12.2015 autodeclare binding
        Binding binding = bind(logStockQueue()).to(logExchange()).with("#");
        rabbitAdmin().declareBinding(binding);
        return binding;
    }

    /************************ broadcast path ************************/
    @Bean public FanoutExchange broadExchange() throws Exception  { return newFanoutExchange("rx_broad_exchange");}
    @Bean public PublishSubscribeAmqpChannel broadcastChannel() throws Exception {
        return (PublishSubscribeAmqpChannel) publishSubscribeChannel("broadcastChannel", cf())
                .exchange(broadExchange())
                .autoStartup(false)
                .get();
    }

    /************************ gateways channels ************************/
    @Bean public DirectChannel rxRouterChannel(){ return new DirectChannel(); }
    @Bean public DirectChannel rxMainRequestChannel() { return new DirectChannel(); }
    @Bean public PublishSubscribeChannel rxMainResponseChannel() { return  new PublishSubscribeChannel(); }
    @Bean public DirectChannel rxDefaultExchangeEnricherChannel() { return new DirectChannel(); }

    /************************ amqp flows ************************/
    @Bean public IntegrationFlow producerFlow() throws Exception {
        //TODO:  where payload is throwable throw new Exception.
        return IntegrationFlows
                .from(rxMainRequestChannel())
                .handle(Amqp.outboundGateway(rabbitTemplate())
                        .exchangeNameExpression("headers.rxExchangeName")
                        .routingKeyExpression("headers.rxRoutingKey"))
                .channel(rxMainResponseChannel())
                .get();
    }

    //TODO: Разобраться почему не отрабатывает subflow в route .subFlowMapping("false", defaultExchangeEnricherFlow())  //UnsupportedOperationException
    @Bean public IntegrationFlow routerFlow() throws Exception {
        return IntegrationFlows.from(rxRouterChannel())
                .route("headers['rxExchangeName'] == null ? false : true", router -> router
                        .channelMapping("false", rxDefaultExchangeEnricherChannel().getComponentName())
                        .channelMapping("true", rxMainRequestChannel().getComponentName()))
                .get();
    }

    @Bean public IntegrationFlow defaultExchangeEnricherFlow() {
        return IntegrationFlows.from(rxDefaultExchangeEnricherChannel())
                // FIXME: 10.12.2015 add dead_letters_queue and consumer for it
                .enrichHeaders(ImmutableMap.of("rxExchangeName", "rx_main_exchange", "rxRoutingKey", "rx_dead_letters"))
                .channel(rxMainRequestChannel())
                .get();
    }

    /************************  helpers ************************/
    private TopicExchange newTopicExchange(String name) throws Exception {
        TopicExchange exchange = new TopicExchange(name);
        rabbitAdmin().declareExchange(exchange);
        return exchange;
    }

    private FanoutExchange newFanoutExchange(String name) throws Exception {
        FanoutExchange exchange = new FanoutExchange(name);
        rabbitAdmin().declareExchange(exchange);
        return exchange;
    }

    private Queue newQueue(String name) throws Exception {
        Queue queue = new Queue(name);
        rabbitAdmin().declareQueue(queue);
        return queue;
    }
}
