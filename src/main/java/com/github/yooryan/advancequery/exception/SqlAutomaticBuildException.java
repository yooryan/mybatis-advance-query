package com.github.yooryan.advancequery.exception;

/**
 * @author linyunrui
 */
public class SqlAutomaticBuildException extends Exception {

    public SqlAutomaticBuildException(String message) {
        super(message);
    }

    public SqlAutomaticBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlAutomaticBuildException(Throwable cause) {
        super(cause);
    }
}
