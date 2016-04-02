package net.shad.rmqrx.test.producer;

import net.shad.rmqrx.test.domain.ScheduleNotifier;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author shad
 */
@MessagingGateway
public interface NotifierProducerAsync {
    @Gateway(requestChannel = "rxRouterChannel", replyChannel = "rxMainResponseChannel")
    Future<List<ScheduleNotifier>> find(List<String> tids);
}
