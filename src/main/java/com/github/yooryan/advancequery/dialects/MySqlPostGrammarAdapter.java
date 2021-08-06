package com.github.yooryan.advancequery.dialects;

import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.util.Objects;

/**
 * @author linyunrui
 */
public class MySqlPostGrammarAdapter implements IPostGrammarAdapter{


    @Override
    public int getNumberOfPostParameters(Select dialectSql) {
        final PlainSelect selectBody = (PlainSelect) dialectSql.getSelectBody();
        final Limit limit = selectBody.getLimit();
//        final boolean aNull =
//        int argsOfPostGrammar = 0;
//        //判断原始sql是否包含后置limit语法
//        if (dialectSql.contains(SqlKeyword.LIMIT.getSqlSegment())) {
//            int lastIndexOf = dialectSql.lastIndexOf("LIMIT");
//            String substring = dialectSql.substring(lastIndexOf);
//            for (int i = 0; i < substring.length(); i++) {
//                char charAt = substring.charAt(i);
//                if ('?' == charAt){
//                    argsOfPostGrammar ++;
//                }
//            }
//        }
//        return argsOfPostGrammar;
        return Objects.isNull(limit) ? 0 : 1;
    }
}
