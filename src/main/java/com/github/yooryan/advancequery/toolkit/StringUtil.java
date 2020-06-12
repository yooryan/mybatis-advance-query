package com.github.yooryan.advancequery.toolkit;

/**
 * @author linyunrui
 */
public class StringUtil {
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 安全的进行字符串 format
     *
     * @param target 目标字符串
     * @param params format 参数
     * @return format 后的
     */
    public static String format(String target, Object... params) {
        if (target.contains("%s") && (params != null && params.length > 0)) {
            return String.format(target, params);
        }
        return target;
    }
}
