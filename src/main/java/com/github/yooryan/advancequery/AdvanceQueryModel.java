package com.github.yooryan.advancequery;

import com.github.yooryan.advancequery.annotation.SqlKeyword;
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
        private final List<String> paramNames;
        /**
         * 参数值
         */
        private final List<Object> params;

        /**
         * 消费次数
         */
        private final int consumerCount;
        /**
         * 方言 sql
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
         *
         * @return this
         */
        public AdvanceQueryModel setConsumer() {
            this.paramNameConsumer = j -> {
                boolean isPostGrammar = false;
                int argsOfPostGrammar = 0;
                //判断原始sql是否包含后置语法
                if (dialectSql.contains(SqlKeyword.LIMIT.getSqlSegment())) {
                    isPostGrammar = true;
                    int lastIndexOf = dialectSql.lastIndexOf("LIMIT");
                    String substring = dialectSql.substring(lastIndexOf);
                    for (int i = 0; i < substring.length(); i++) {
                        char charAt = substring.charAt(i);
                        if ('?' == charAt){
                            argsOfPostGrammar ++;
                        }
                    }
                }
                if (isPostGrammar){
                    int startIndex = j.size() - argsOfPostGrammar;
                    for (int i = 0; i < consumerCount; i++) {
                        j.add(startIndex,new ParameterMapping.Builder(configuration,paramNames.get(i), Object.class).build());
                        startIndex++;
                    }
                }else {
                    for (int i = 0; i < consumerCount; i++) {
                        j.add(new ParameterMapping.Builder(configuration,paramNames.get(i), Object.class).build());
                    }
                }
            };
            this.setParamMapConsumer();
            return this;
        }

        /**
         *
         * @return this
         */
        public AdvanceQueryModel setConsumerChain() {
            return setConsumer();
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
