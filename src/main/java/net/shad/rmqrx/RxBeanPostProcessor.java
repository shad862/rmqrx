package net.shad.rmqrx;

import net.shad.rmqrx.annotation.RxConsumer;
import net.shad.rmqrx.exception.NotImplementedException;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.config.IntegrationFlowBeanPostProcessor;
import org.springframework.integration.dsl.support.GenericHandler;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.springframework.integration.dsl.amqp.Amqp.inboundAdapter;
import static org.springframework.integration.dsl.amqp.Amqp.inboundGateway;

/**
 * @author shad
 */
public class RxBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private static final String RX_FLOW_PREFIX = "flow_";

    private ApplicationContext context;

    private static Integer counter = 0;

    private static Integer consumerCount = 1;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> aClass = bean.getClass();
        for(Method method : aClass.getDeclaredMethods()){
            if(method.isAnnotationPresent(RxConsumer.class)){
                try {
                    processConsumer(bean, method);
                } catch (NotImplementedException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }

    private void processConsumer(Object bean, Method method) throws NotImplementedException {
        RxConsumer rxConsumer = method.getAnnotationsByType(RxConsumer.class)[0];
        CachingConnectionFactory cf = getCachingConnectionFactory(rxConsumer);
        RabbitAdmin rabbitAdmin = getRabbitAdmin();

        Queue queue = declareQueue(method, rxConsumer, rabbitAdmin);

        //if method return type is void then adapter else gateway
        IntegrationFlowBuilder builder = void.class.isAssignableFrom(method.getReturnType()) ?
                IntegrationFlows.from(inboundAdapter(cf, queue).taskExecutor(getSimpleAsyncTaskExecutor(queue)).concurrentConsumers(consumerCount)) :
                IntegrationFlows.from(inboundGateway(cf, queue).taskExecutor(getSimpleAsyncTaskExecutor(queue)).concurrentConsumers(consumerCount));

        IntegrationFlow flow = builder.handle(Object.class, getHandler(bean, method)).get();

        TopicExchange mainExchange = (TopicExchange) context.getBean("mainExchange");
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(mainExchange).with(queue.getName()));

        register(flow, assignName(method));
    }

    private SimpleAsyncTaskExecutor getSimpleAsyncTaskExecutor(Queue queue) {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setThreadNamePrefix(queue.getName() + "-" + counter++ + "-");
        return taskExecutor;
    }

    private GenericHandler<Object> getHandler(Object bean, Method method) {
        return (payload, headers) -> {
            try {
                if (payload instanceof RxMessage) {
                    RxMessage<Object[]> message = (RxMessage)payload;
                    return method.invoke(bean, message.getPayload());
                }
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                e.printStackTrace();
            }
            return new RxMessage(RxError.BAD_REQUEST, null);
        };
    }

    private Queue declareQueue(Method method, RxConsumer rxConsumer, RabbitAdmin rabbitAdmin) throws NotImplementedException {
        Queue queue;
        QueueNamingPolicy namingPolicy = rxConsumer.namingPolicy();
        String queueName;
        if(namingPolicy == QueueNamingPolicy.METHOD) {
            queueName = QueueNaming.RX_QUEUE_PREFIX + method.getName();
        }
        else if(namingPolicy == QueueNamingPolicy.CUSTOM) {
            queueName = rxConsumer.queueName();
            Assert.hasLength(queueName, "Queue name must be set with CUSTOM naming policy");
        }
        else
            throw new NotImplementedException();

        queue = new Queue(queueName);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    private CachingConnectionFactory getCachingConnectionFactory(RxConsumer rxConsumer) {
        String connectionName = rxConsumer.connectionName();
        CachingConnectionFactory cf = (CachingConnectionFactory)context.getBean(connectionName);
        Assert.notNull(cf);
        return cf;
    }

    private RabbitAdmin getRabbitAdmin() {
        RabbitAdmin rabbitAdmin = context.getBean(RabbitAdmin.class);
        Assert.notNull(rabbitAdmin);
        return rabbitAdmin;
    }

    private void register(IntegrationFlow flow, String flowName) {
        DefaultListableBeanFactory factory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        IntegrationFlowBeanPostProcessor processor = context.getBean(IntegrationFlowBeanPostProcessor.class);
        factory.initializeBean(flow, flowName);
        factory.registerSingleton(flowName, flow);
        processor.postProcessBeforeInitialization(flow, flowName);
    }

    private String assignName(Method method) {
        return RX_FLOW_PREFIX + method.getName();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}