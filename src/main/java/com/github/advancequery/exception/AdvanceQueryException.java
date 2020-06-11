package com.github.advancequery.exception;

/**
 * @author linyunrui
 */
public class AdvanceQueryException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public AdvanceQueryException(String message) {
        super(message);
    }

    public AdvanceQueryException(Throwable throwable) {
        super(throwable);
    }

    public AdvanceQueryException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
