package com.github.yooryan.advancequery;

import com.github.yooryan.advancequery.toolkit.Assert;
import lombok.Getter;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author linyunrui
 */
public class AdvanceQueryModel {
        /**
         * 参数名称
         */
        private List<String> paramNames;
        /**
         * 参数值
         */
        private List<Object> params;
        /**
         * 消费次数
         */
        private final int consumerCount;
        /**
         * 分页方言 sql
         */
        @Getter
        private final String dialectSql;
        /**
         * 提供 Configuration
         */
        private Configuration configuration;
        /**
         * 用 List<ParameterMapping> 消费参数值
         */
        private Consumer<List<ParameterMapping>> paramNameConsumer = i -> {
        };
        /**
         * 用 Map<String, Object> 消费参数值
         */
        private Consumer<Map<String, Object>> paramMapConsumer = i -> {
        };


        public AdvanceQueryModel(String dialectSql, List<String> paramNames, List<Object> params) {
            this.dialectSql = dialectSql;
            this.paramNames = paramNames;
            this.params = params;
            this.consumerCount = paramNames.size();
        }

        /**
         * 设置消费 List<ParameterMapping> 的方式
         * <p>不带下标的</p>
         *
         * @return this
         */
        public AdvanceQueryModel setConsumer(boolean isFirstParam) {
            if (isFirstParam) {
                this.paramNameConsumer = j -> {
                    for (int i = 0; i < consumerCount; i++) {
                        j.add(new ParameterMapping.Builder(configuration,paramNames.get(i), Object.class).build());
                    }
                };
            }
            this.setParamMapConsumer();
            return this;
        }

        /**
         * 设置消费 List<ParameterMapping> 的方式
         * <p>不带下标的,两个值都有</p>
         *
         * @return this
         */
        public AdvanceQueryModel setConsumerChain() {
            return setConsumer(true);
        }

        /**
         * 把内部所有需要消费的都消费掉
         *
         * @param parameterMappings    ParameterMapping 集合
         * @param configuration        Configuration
         * @param additionalParameters additionalParameters map
         */
        public void consumers(List<ParameterMapping> parameterMappings, Configuration configuration,
                              Map<String, Object> additionalParameters) {
            Assert.notNull(configuration, "configuration must notNull !");
            Assert.notNull(parameterMappings, "parameterMappings must notNull !");
            Assert.notNull(additionalParameters, "additionalParameters must notNull !");
            this.configuration = configuration;
            this.paramNameConsumer.accept(parameterMappings);
            this.paramMapConsumer.accept(additionalParameters);
        }

        /**
         * 设置消费 Map<String, Object> 的方式
         */
        private void setParamMapConsumer() {
            this.paramMapConsumer = j -> {
                for (int i = 0; i < consumerCount; i++) {
                    j.put(paramNames.get(i),params.get(i));
                }
            };
        }
    }
