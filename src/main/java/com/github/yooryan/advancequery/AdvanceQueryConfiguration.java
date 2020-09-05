package com.github.yooryan.advancequery;

import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * @author linyunrui
 */
public class AdvanceQueryConfiguration {

    @Bean
    public AdvanceQueryInterceptor advanceQueryInterceptor(){
        return new AdvanceQueryInterceptor();
    }
}

