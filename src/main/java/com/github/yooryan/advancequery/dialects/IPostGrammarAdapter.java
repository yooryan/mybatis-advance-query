package com.github.yooryan.advancequery.dialects;

import lombok.Getter;

/**
 * 判断后置语法适配器
 *
 * @author linyunrui
 */
public interface IPostGrammarAdapter {

    /**
     * 获取不同方言后置语法参数数量
     * @param dialectSql 方言sql
     * @return 参数数量
     */
    int getNumberOfPostParameters(final String dialectSql);
}