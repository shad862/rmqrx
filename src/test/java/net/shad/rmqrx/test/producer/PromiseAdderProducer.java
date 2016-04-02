package net.shad.rmqrx.test.producer;

import net.shad.rmqrx.RxMessage;
import net.shad.rmqrx.annotation.RxGateway;
import net.shad.rmqrx.annotation.RxMethod;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.GatewayHeader;
import org.springframework.messaging.handler.annotation.Payload;
import reactor.rx.Promise;

import java.util.List;

/**
 * @author shad
 */
@RxGateway
public interface PromiseAdderProducer {
    //@RxMethod
    @Gateway(headers = @GatewayHeader(name = "rxRoutingKey", value = "rx_add"))
    @Payload("new net.shad.rmqrx.RxMessage(#args)")
    Promise<RxMessage<Integer>> add(List<Integer> params);
}
