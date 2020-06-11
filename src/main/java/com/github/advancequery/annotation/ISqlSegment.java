package com.github.advancequery.annotation;


import java.io.Serializable;

/**
 * SQL 片段接口
 *
 * @author linyunrui
 */
@FunctionalInterface
public interface ISqlSegment extends Serializable {

    /**
     * SQL 片段
     */
    String getSqlSegment();
}
