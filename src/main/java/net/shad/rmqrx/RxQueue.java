package net.shad.rmqrx;

/**
 * @author shad
 */
public enum RxQueue {

    RX_RPC1("rx_rpc1_queue"),
    RX_LOG_STOCK_QUEUE("rx_log_stock_queue");

    private final String name;

    RxQueue(String name) {
        this.name = name;
    }
}
