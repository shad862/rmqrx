package net.shad.rmqrx.test.consumer;

import net.shad.rmqrx.RxMessage;
import net.shad.rmqrx.annotation.RxConsumer;
import net.shad.rmqrx.annotation.RxService;

import java.util.List;

/**
 * @author shad
 * 21.07.2015
 */
@RxService
public class SimpleAdderConsumer{

    @RxConsumer
    public RxMessage<Integer> add(List<Integer> xs) throws Exception {
        if(xs.size() != 2)
            throw new Exception();
        return new RxMessage<>(xs.get(0) + xs.get(1));
    }

}
