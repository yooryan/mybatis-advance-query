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
        return Objects.isNull(limit) ? 0 : 1;
    }
}
