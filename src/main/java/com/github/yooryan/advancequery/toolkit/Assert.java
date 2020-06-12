package com.github.yooryan.advancequery.toolkit;

import com.github.yooryan.advancequery.exception.AdvanceQueryException;

/**
 * @author linyunrui
 */
public class Assert {

    /**
     * 断言不为 null
     * <p>为 null 则抛异常</p>
     *
     * @param object  对象
     * @param message 消息
     */
    public static void notNull(Object object, String message, Object... params) {
        isTrue(object != null, message, params);
    }


    /**
     * 断言这个 boolean 为 true
     * <p>为 false 则抛出异常</p>
     *
     * @param expression boolean 值
     * @param message    消息
     */
    public static void isTrue(boolean expression, String message, Object... params) {
        if (!expression) {
            throw new AdvanceQueryException(String.format(message,params));
        }
    }
}
