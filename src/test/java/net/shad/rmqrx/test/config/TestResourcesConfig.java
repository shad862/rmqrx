package net.shad.rmqrx.test.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import javax.annotation.PostConstruct;

/**
 * @author shad
 */
@Configuration
@PropertySource("classpath:source-${profile_active}.properties")
public class TestResourcesConfig {

    /****************** AMQP PROPERTIES BEGIN ************************/
    @Value("${amqp.virtualhost}")
    private String amqpVirtualHost;

    @Value("${amqp.addresses}")
    private String amqpAddresses;

    @Value("${amqp.user}")
    private String amqpUser;

    @Value("${amqp.password}")
    private String amqpPassword;
    /****************** AMQP PROPERTIES END ************************/

    @PostConstruct
    public void configureJndiResources() throws Exception {
        SimpleNamingContextBuilder contextBuilder = new SimpleNamingContextBuilder();
        contextBuilder.bind("amqp/rmqrx/addresses", amqpAddresses);
        contextBuilder.bind("amqp/rmqrx/virtualhost", amqpVirtualHost);
        contextBuilder.bind("amqp/rmqrx/user", amqpUser);
        contextBuilder.bind("amqp/rmqrx/password", amqpPassword);
        contextBuilder.activate();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
