package com.github.yooryan.advancequery;

import lombok.Data;

import java.util.List;

/**
 * @author linyunrui
 */
@Data
public class AdvanceQuery {

    /**
     * 查询条件
     */
    private String key;

    /**
     * 查询操作
     */
    private String op;

    /**
     * 查询值
     */
    private List<Object> value;
}
