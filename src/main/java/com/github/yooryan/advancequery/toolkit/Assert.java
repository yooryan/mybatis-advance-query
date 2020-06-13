package com.github.yooryan.advancequery.toolkit;

import com.github.yooryan.advancequery.exception.AdvanceQueryException;

/**
 * @author linyunrui
 */
public class Assert {

    /**
     *  断言不为 null
     * @param object 断言实体
     * @param message 消息
     * @param params 参数
     */
    public static void notNull(Object object, String message, Object... params) {
        isTrue(object != null, message, params);
    }


    /**
     * 断言是否为true ,false则抛出异常
     * @param expression boolean值
     * @param message 消息
     * @param params 参数
     */
    public static void isTrue(boolean expression, String message, Object... params) {
        if (!expression) {
            throw new AdvanceQueryException(String.format(message,params));
        }
    }
}
