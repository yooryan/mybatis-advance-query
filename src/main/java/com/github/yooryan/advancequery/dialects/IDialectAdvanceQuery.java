package com.github.yooryan.advancequery.dialects;


import com.github.yooryan.advancequery.AdvanceQuery;
import com.github.yooryan.advancequery.AdvanceQueryModel;
import com.github.yooryan.advancequery.exception.AdvanceQueryException;
import com.github.yooryan.advancequery.exception.SqlAutomaticBuildException;
import com.github.yooryan.advancequery.toolkit.StringPool;

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
     /**
     * 构建高级查询sql
     * @param advanceQueries 查询条件对象
     * @param originalSql 原始sql
     * @return 高级查询sql
     * @throws SqlAutomaticBuildException 构建查询sql失败异常
     */
    AdvanceQueryModel buildAdvanceQuerySql(List<AdvanceQuery> advanceQueries, String originalSql) throws SqlAutomaticBuildException;
}
