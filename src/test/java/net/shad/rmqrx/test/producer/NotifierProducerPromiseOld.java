package net.shad.rmqrx.test.producer;

import net.shad.rmqrx.test.domain.ScheduleNotifier;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import reactor.rx.Promise;

import java.util.Map;

/**
 * @author shad
 */
@MessagingGateway(asyncExecutor = "exec", reactorEnvironment = "reactorEnv")
public interface NotifierProducerPromiseOld {
    @Gateway(requestChannel = "rxRouterChannel", replyChannel = "rxMainResponseChannel")
    Promise<Map<String, ScheduleNotifier>> find(Map<String, String> idsVsRrns);
}
