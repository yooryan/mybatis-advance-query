package com.github.advancequery.dialects;


import com.github.advancequery.AdvanceQuery;
import com.github.advancequery.AdvanceQueryModel;
import com.github.advancequery.toolkit.StringPool;

import java.util.List;

/**
 * @author linyunrui
 */
public interface IDialectAdvanceQuery {

    /**
     * 换行符
     */
    String TAB = StringPool.TAB;
    /**
     * 变量别名
     */
    String ALIAS_TEMP = "temp.";
    /**
     * and
     */
    String AND = StringPool.AND;
    /**
     * ?
     */
    String QUESTIO_NMARK = StringPool.QUESTION_MARK;

    /**
     * 构建高级查询sql
     * @param advanceQueries 查询条件对象
     * @param originalSql 原始sql
     * @return 高级查询sql
     */
    AdvanceQueryModel buildAdvanceQuerySql(List<AdvanceQuery> advanceQueries, String originalSql);
}
