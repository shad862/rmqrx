package net.shad.rmqrx.test.producer;

import net.shad.rmqrx.annotation.RxGateway;
import net.shad.rmqrx.test.domain.ScheduleNotifier;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.GatewayHeader;
import reactor.rx.Promise;

import java.util.Map;

/**
 * @author shad
 */
@RxGateway
public interface NotifierProducerPromise {
    @Gateway(headers = @GatewayHeader(name = "rxRoutingKey", value = "rx_findNotifiersByRrn"))
    Promise<Map<String, ScheduleNotifier>> find(Map<String, String> idsVsRrns);
}
