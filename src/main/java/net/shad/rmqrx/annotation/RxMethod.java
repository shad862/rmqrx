package net.shad.rmqrx.annotation;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.GatewayHeader;

import java.lang.annotation.*;

/**
 * @author shad
 * 08.01.2016
 */
@Gateway(headers = @GatewayHeader(name = "rxRoutingKey", value = "rx_add"))
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RxMethod {

    String requestChannel() default "";

    String replyChannel() default "";

    long requestTimeout() default Long.MIN_VALUE;

    long replyTimeout() default Long.MIN_VALUE;

    String payloadExpression() default "";

    GatewayHeader[] headers() default {};
}
