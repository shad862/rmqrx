package net.shad.rmqrx.annotation;

import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import reactor.spring.context.config.EnableReactor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author shad
 *
 * Для обеспечения работоспособности consumers необходимо добавить сканирование @ComponentScan("package"),
 * а для обеспечения работоспособности producers необходимо добавить @IntegrationComponentScan("package")
 * в конфигурацию приложения. В настоящий момент не осуществляется дефолтного сканирования пакетов
 * на наличие вышеуказанных.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Configuration
@EnableIntegration
@EnableReactor
public @interface RxEnable {}
