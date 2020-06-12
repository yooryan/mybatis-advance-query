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
     *设定别名
     */
    String alias() default "";
}
