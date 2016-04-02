package net.shad.rmqrx.test.consumer;

import net.shad.rmqrx.annotation.RxConsumer;
import net.shad.rmqrx.annotation.RxService;
import net.shad.rmqrx.test.domain.ScheduleNotifier;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shad
 */
@RxService
public class Consumer{

    Logger logger = Logger.getLogger(Consumer.class);

    @RxConsumer
    public List<ScheduleNotifier> produceNotifier(List<String> message){
        ArrayList<ScheduleNotifier> scheduleNotifiers = new ArrayList<>();
        scheduleNotifiers.add(new ScheduleNotifier());
        return scheduleNotifiers;
    }


}
