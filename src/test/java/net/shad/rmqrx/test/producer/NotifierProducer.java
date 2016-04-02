package net.shad.rmqrx.test.producer;

import net.shad.rmqrx.test.domain.ScheduleNotifier;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import java.util.List;

/**
 * @author shad
 */
@MessagingGateway
public interface NotifierProducer {
    @Gateway(requestChannel = "rxRouterChannel", replyChannel = "rxMainResponseChannel")
    List<ScheduleNotifier> find(List<String> tids);
}
