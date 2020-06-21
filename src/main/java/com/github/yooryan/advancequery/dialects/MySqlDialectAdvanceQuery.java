package com.github.yooryan.advancequery.dialects;


import com.github.yooryan.advancequery.AdvanceQuery;
import com.github.yooryan.advancequery.AdvanceQueryModel;
import com.github.yooryan.advancequery.annotation.SqlKeyword;
import com.github.yooryan.advancequery.exception.AdvanceQueryException;
import com.github.yooryan.advancequery.exception.SqlAutomaticBuildException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.util.LinkedList;
import java.util.List;

/**
 * @author linyunrui
 */
public class MySqlDialectAdvanceQuery implements IDialectAdvanceQuery {



    @Override
    public AdvanceQueryModel buildAdvanceQuerySql(List<AdvanceQuery> advanceQueries, String originalSql) throws SqlAutomaticBuildException {
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
        String advanceSql = createKeyOpValueSql(advanceQueries,originalSql);
        return new AdvanceQueryModel(advanceSql, paramName, paramValue).setConsumerChain();
    }

    private String createKeyOpValueSql(List<AdvanceQuery> advanceQueries,String originalSql) throws SqlAutomaticBuildException  {

        try {
            Select selectStatement = (Select) CCJSqlParserUtil.parse(originalSql);
            PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
            StringBuilder sb = new StringBuilder();
            for (AdvanceQuery advanceQuery : advanceQueries) {
                String key = advanceQuery.getKey();
                String op = advanceQuery.getOp();
                String tableAlias = advanceQuery.getTableAlias() == null ? "" : advanceQuery.getTableAlias();
                List<Object> value = advanceQuery.getValue();
                sb.append(TAB).append(AND).append(TAB);
                if ("IN".equals(op)){
                    sb.append(tableAlias).append(key).append(TAB).append("IN").append("(");
                    for (int i = 0; i < value.size(); i++) {
                        sb.append(QUESTIO_NMARK);
                        if (i < value.size() -1){
                            sb.append(",");
                        }
                    }
                    sb.append(")");
                }else if ("BETWEEN".equals(op)){
                    sb.append(tableAlias).append(key).append(TAB);
                    sb.append(SqlKeyword.BETWEEN);
                    sb.append(QUESTIO_NMARK);
                    sb.append(AND).append(TAB);
                    sb.append(QUESTIO_NMARK);
                }else if ("LIKE".equals(op)){
                    if (value.size() == 1){
                        sb.append(tableAlias).append(key).append(TAB).append("LIKE CONCAT('%'," + QUESTIO_NMARK + ",'%')");
                    }else if(value.size() > 1){
                        for (int i = 0; i < value.size(); i++) {
                            sb.append("(");
                            sb.append(tableAlias).append(key).append(TAB).append("LIKE CONCAT('%'," + QUESTIO_NMARK + ",'%')");
                            sb.append(")");
                        }

                    }
                }else {
                    sb.append(tableAlias).append(key).append(TAB).append(op).append(TAB).append(QUESTIO_NMARK);
                }
            }
            Expression originalWhere = plainSelect.getWhere();
            plainSelect.setWhere(CCJSqlParserUtil.parseCondExpression(originalWhere + sb.toString()));
            return selectStatement.toString();
        } catch (Throwable e) {
           //抛出任何异常则不作处理
            throw new SqlAutomaticBuildException("Automatic build SQL failed");
        }
    }
}
