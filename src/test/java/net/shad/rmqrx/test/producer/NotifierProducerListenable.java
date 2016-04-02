package net.shad.rmqrx.test.producer;

import net.shad.rmqrx.test.domain.ScheduleNotifier;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;

/**
 * @author shad
 */
@MessagingGateway(asyncExecutor = "exec")
public interface NotifierProducerListenable {
    @Gateway(requestChannel = "rxRouterChannel", replyChannel = "rxMainResponseChannel")
    ListenableFuture<List<ScheduleNotifier>> find(List<String> ts);
}
