package net.shad.rmqrx.test.consumer;

import net.shad.rmqrx.annotation.RxConsumer;
import net.shad.rmqrx.annotation.RxService;
import org.apache.log4j.Logger;

/**
 * @author shad
 * @date 21.07.2015
 *
 * Consumer for php tests
 */
@RxService
public class JsonConsumer {

    private static Logger logger = Logger.getLogger(JsonConsumer.class.getName());

    @RxConsumer
    public String json(Object o){
        logger.info(o);
        return "{\"menu\": {\n" +
                "  \"id\": \"file\",\n" +
                "  \"value\": \"File\",\n" +
                "  \"popup\": {\n" +
                "    \"menuitem\": [\n" +
                "      {\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"},\n" +
                "      {\"value\": \"Open\", \"onclick\": \"OpenDoc()\"},\n" +
                "      {\"value\": \"Close\", \"onclick\": \"CloseDoc()\"}\n" +
                "    ]\n" +
                "  }\n" +
                "}}";
    }
}
