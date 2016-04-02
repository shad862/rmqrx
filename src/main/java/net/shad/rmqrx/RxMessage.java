package net.shad.rmqrx;

import java.io.Serializable;

public class RxMessage<T> implements Serializable {
    private T payload;

    private RxError errorCode = RxError.NO_ERROR;
    private String errorMessage = null;
    private boolean isEmpty = true;

    public RxMessage(T payload) {
        this.payload = payload;
        this.isEmpty = false;
    }

    public RxMessage(RxError errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public T getPayload() {
        return payload;
    }

    public boolean isError() {
        return errorCode != RxError.NO_ERROR;
    }

    public RxError getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    //TODO: при возврате результата, мы можем выбрасывать Exception если это может понадобится.
    public boolean isThrowable() {
        return this.payload instanceof Throwable;
    }
}