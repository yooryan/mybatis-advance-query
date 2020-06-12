package com.github.yooryan.advancequery.dialects;


import com.github.yooryan.advancequery.AdvanceQuery;
import com.github.yooryan.advancequery.AdvanceQueryModel;
import com.github.yooryan.advancequery.annotation.SqlKeyword;

import java.util.LinkedList;
import java.util.List;

/**
 * @author linyunrui
 */
public class MySqlDialectAdvanceQuery implements IDialectAdvanceQuery {

    @Override
    public AdvanceQueryModel buildAdvanceQuerySql(List<AdvanceQuery> advanceQueries, String originalSql) {
        List<String> paramName = new LinkedList<>();
        List<Object> paramValue = new LinkedList<>();
        for (AdvanceQuery advanceQuery : advanceQueries) {
            String key = advanceQuery.getKey();
            List<Object> value = advanceQuery.getValue();
            if (value.size() > 1){
                for (int i = 0; i < value.size(); i++) {
                    paramName.add("_frch" + key + "_" + i);
                    paramValue.add(value.get(i));
                }
            }else {
                paramName.add(key);
                paramValue.add(value.get(0));
            }
        }
        //sql拼接
        String fragmentSql = createKeyOpValueSql(advanceQueries);
        String advanceSql = originalSql + fragmentSql;
        return new AdvanceQueryModel(advanceSql, paramName, paramValue).setConsumerChain();
    }

    private String createKeyOpValueSql(List<AdvanceQuery> advanceQueries) {
        StringBuilder sb = new StringBuilder();
        for (AdvanceQuery advanceQuery : advanceQueries) {
            String key = advanceQuery.getKey();
            String op = advanceQuery.getOp();
            List<Object> value = advanceQuery.getValue();
            sb.append(TAB).append(AND).append(TAB);
            if ("IN".equals(op)){
                sb.append(ALIAS_TEMP).append(key).append(TAB).append("IN").append("(");
                for (int i = 0; i < value.size(); i++) {
                    sb.append(QUESTIO_NMARK);
                    if (i < value.size() -1){
                        sb.append(",");
                    }
                }
                sb.append(")");
            }else if ("BETWEEN".equals(op)){
                sb.append(ALIAS_TEMP).append(key).append(TAB);
                sb.append(SqlKeyword.BETWEEN);
                sb.append(QUESTIO_NMARK);
                sb.append(AND).append(TAB);
                sb.append(QUESTIO_NMARK);
            }else if ("LIKE".equals(op)){
                if (value.size() == 1){
                    sb.append(ALIAS_TEMP).append(key).append(TAB).append("LIKE CONCAT('%'," + QUESTIO_NMARK + ",'%')");
                }else if(value.size() > 1){
                    for (int i = 0; i < value.size(); i++) {
                        sb.append("(");
                        sb.append(ALIAS_TEMP).append(key).append(TAB).append("LIKE CONCAT('%'," + QUESTIO_NMARK + ",'%')");
                        sb.append(")");
                    }

                }
            }else {
                sb.append(ALIAS_TEMP).append(key).append(TAB).append(op).append(TAB).append(QUESTIO_NMARK);
            }
        }
        return sb.toString().replaceFirst(AND,"where");
    }
}
