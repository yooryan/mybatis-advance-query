package com.github.yooryan.advancequery;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author linyunrui
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AdvanceQueryConfiguration.class)
public @interface EnableAdvanceQuery {
}
