package com.github.yooryan.advancequery.annotation;


import java.lang.annotation.*;

/**
 * @author linyunrui
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface AdvanceSqlOp {

    /**
     * 查询操作符
     * @return 操作符
     */
    SqlKeyword value();

    /**
     * 驼峰命名转换_
     * @return 默认true
     */
    boolean camelCaseToUnderscoreMap() default true;

    /**
     * 设定别名,按指定名称构建搜索字段
     * @return 默认为""
     */
    String alias() default "";

    /**
     * 该字段的前置表别名(多表的列字段存在冲突时使用)
     * @return 默认""
     */
    String tableAlias() default "";
}
