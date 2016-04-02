package net.shad.rmqrx;

/**
 * @author shad
 */
public enum RxExchange {

    RX_MAIN("rx_main_exchange"),
    RX_LOG("rx_log_exchange"),
    RX_BROADCAST("rx_broadcast_exchange");

    private final String name;

    RxExchange(String name){
        this.name = name;
    }
}
