package com.github.yooryan.advancequery.annotation;


import java.io.Serializable;

/**
 * SQL 片段接口
 *
 * @author linyunrui
 */
@FunctionalInterface
public interface ISqlSegment extends Serializable {

    /**
     * @return SQL 片段
     */
    String getSqlSegment();
}
