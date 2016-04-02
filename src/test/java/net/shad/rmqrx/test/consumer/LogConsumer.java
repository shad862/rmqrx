package net.shad.rmqrx.test.consumer;

import net.shad.rmqrx.QueueNamingPolicy;
import net.shad.rmqrx.annotation.RxConsumer;
import net.shad.rmqrx.annotation.RxService;
import org.apache.log4j.Logger;

/**
 * @author shad
 */
@RxService
public class LogConsumer {

    Logger logger = Logger.getLogger(LogConsumer.class);

    @RxConsumer(namingPolicy = QueueNamingPolicy.CUSTOM, queueName = "rx_log_stock_queue")
    public void log(Object message){
        logger.info(message);
    }
}
