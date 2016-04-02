package net.shad.rmqrx.annotation;

import net.shad.rmqrx.QueueNamingPolicy;

import java.lang.annotation.*;

/**
 * @author shad
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RxConsumer {

    QueueNamingPolicy namingPolicy() default QueueNamingPolicy.METHOD;

    /**
     * Connection factory name
     *
     * @return
     */
    String connectionName() default "cf";

    String queueName() default "";
}
