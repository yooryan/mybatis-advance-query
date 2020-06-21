package com.github.yooryan.advancequery.toolkit;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author linyunrui
 */
public class SqlParserUtils {


    /**
     * 驼峰命名转换下划线
     */
    private static Pattern humpPattern = Pattern.compile("[A-Z]");
    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
