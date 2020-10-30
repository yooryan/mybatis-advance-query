package com.github.yooryan.advancequery.dialects;

import com.github.yooryan.advancequery.annotation.SqlKeyword;

/**
 * @author linyunrui
 */
public class MySqlPostGrammarAdapter implements IPostGrammarAdapter{


    @Override
    public int getNumberOfPostParameters(String dialectSql) {
        int argsOfPostGrammar = 0;
        //判断原始sql是否包含后置limit语法
        if (dialectSql.contains(SqlKeyword.LIMIT.getSqlSegment())) {
            int lastIndexOf = dialectSql.lastIndexOf("LIMIT");
            String substring = dialectSql.substring(lastIndexOf);
            for (int i = 0; i < substring.length(); i++) {
                char charAt = substring.charAt(i);
                if ('?' == charAt){
                    argsOfPostGrammar ++;
                }
            }
        }
        return argsOfPostGrammar;
    }
}
