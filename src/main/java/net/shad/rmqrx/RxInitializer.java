package net.shad.rmqrx;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.integration.config.IntegrationConfigurationInitializer;

/**
 * @author shad
 *
 * from DslIntegrationConfigurationInitializer
 */
public class RxInitializer implements IntegrationConfigurationInitializer {

    private static final String RX_INITIALIZER_BEAN_NAME = RxBeanPostProcessor.class.getName();

    public void initialize(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        /*
        Assert.isInstanceOf(BeanDefinitionRegistry.class, beanFactory,
                "To use Spring Integration Java DSL the 'beanFactory' has to be an instance of " +
                        "'BeanDefinitionRegistry'. Consider using 'GenericApplicationContext' implementation."
        );
        */
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        if(!registry.containsBeanDefinition(RX_INITIALIZER_BEAN_NAME))
            registry.registerBeanDefinition(RX_INITIALIZER_BEAN_NAME,
                    new RootBeanDefinition(RxBeanPostProcessor.class));
    }
}
