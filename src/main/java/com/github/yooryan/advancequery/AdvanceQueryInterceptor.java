package com.github.yooryan.advancequery;

import com.github.yooryan.advancequery.annotation.AdvanceSqlOp;
import com.github.yooryan.advancequery.annotation.DbType;
import com.github.yooryan.advancequery.annotation.SqlKeyword;
import com.github.yooryan.advancequery.exception.SqlAutomaticBuildException;
import com.github.yooryan.advancequery.toolkit.CollectionUtils;
import com.github.yooryan.advancequery.toolkit.SqlParserUtils;
import com.github.yooryan.advancequery.toolkit.StringUtil;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author linyunrui
 */
@Setter
@Accessors(chain = true)
@Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
@Slf4j
public class AdvanceQueryInterceptor implements Interceptor {

    /**
     * 方言类型
     */
    private String dialectType;
    /**
     * 需实现 IDialectAdvanceQuery 接口的子类
     */
    private String dialectClazz;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];

        //获取原始sql,获取方法参数
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Object paramObj = boundSql.getParameterObject();

        //判断参数中是否存在AdvanceSqlOp注解
        Object query = null;
        if (paramObj instanceof Map){
            for (Object value : ((Map) paramObj).values()) {
                Class<?> aClass = value.getClass();
                Field[] declaredFields = aClass.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    AdvanceSqlOp annotation = declaredField.getAnnotation(AdvanceSqlOp.class);
                    if (null != annotation){
                        query = value;
                    }
                }
            }
        }

        //不需要自动构建查询sql
        if (query == null){
            invocation.proceed();
        }

        String originalSql = boundSql.getSql();
        //默认先给mysql吧
        DbType dbType = StringUtil.isNotEmpty(dialectType) ? DbType.getDbType(dialectType)
                : DbType.MYSQL;

        //存在查询对象
        List<AdvanceQuery> advanceQuery = createAdvanceQuery(query);
        if (log.isDebugEnabled()) {
            log.debug("AdvanceQueryInterceptor sql=" + originalSql);
        }
        if (!CollectionUtils.isEmpty(advanceQuery)){
          //  String tempSql = SqlParserUtils.getOriginalAdvanceQuerySql(originalSql,sqlOptimize);
            MappedStatement newMs = copyMappedStatement(mappedStatement, new AdvanceQuerySqlSource(boundSql));
            MetaObject msObject =  MetaObject.forObject(newMs, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(),new DefaultReflectorFactory());
            Map<String, Object> additionalParameters = (Map<String, Object>) msObject.getValue("sqlSource.boundSql.additionalParameters");
            AdvanceQueryModel model;
            try {
                model = AdvanceQueryFactory.buildAdvanceQuerySql(advanceQuery,originalSql, dbType, dialectClazz);
            } catch (SqlAutomaticBuildException e) {
                //构建sql失败
                return invocation.proceed();
            }
            Configuration configuration = mappedStatement.getConfiguration();
            List<ParameterMapping> mappings = new ArrayList<>(boundSql.getParameterMappings());
            model.consumers(mappings,configuration,additionalParameters);
            msObject.setValue("sqlSource.boundSql.sql", model.getDialectSql());
            msObject.setValue("sqlSource.boundSql.parameterMappings", mappings);
            invocation.getArgs()[0] = newMs;
        }
        return invocation.proceed();
    }

    /**
     * 查询sql拼接查询条件
     */
    private static List<AdvanceQuery> createAdvanceQuery(Object query){
        Class<?> queryClass = query.getClass();
        Field[] declaredFields = queryClass.getDeclaredFields();
        List<AdvanceQuery> advanceQueries = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            AdvanceSqlOp annotation = declaredField.getAnnotation(AdvanceSqlOp.class);
            if (null != annotation){
                AdvanceQuery advanceQuery = new AdvanceQuery();
                String name = declaredField.getName();
                //判断是否设置别名
                if (StringUtil.isEmpty(annotation.alias())){
                    //驼峰命名转换下划线
                    if (annotation.camelCaseToUnderscoreMap()){
                        name = SqlParserUtils.humpToLine(name);
                    }
                }else {
                    name = annotation.alias();
                }
                //是否设置字段前置表名
                if (!StringUtil.isEmpty(annotation.tableAlias())){
                    advanceQuery.setTableAlias(annotation.tableAlias() + ".");
                }

                SqlKeyword op = annotation.value();
                declaredField.setAccessible(true);
                try {
                    Object o = declaredField.get(query);
                    advanceQuery.setKey(name);
                    advanceQuery.setOp(op.getSqlSegment());
                    if (Objects.nonNull(o)){
                        List<Object> objectList;
                        //字符串类型需要判断是否为空串
                        if (o instanceof String && StringUtil.isEmpty((String) o)){
                            continue;
                        }
                        if (o instanceof Collection){
                            objectList = (List) o;
                        }else {
                            objectList = Collections.singletonList(o);
                        }
                        advanceQuery.setValue(objectList);
                        advanceQueries.add(advanceQuery);
                    }
                } catch (IllegalAccessException e) {
                    log.warn("AdvanceQueryInterceptor - Intercept processing failed, exception=" + e.getMessage());
                }
            }
        }
        return advanceQueries;
    }

    private MappedStatement copyMappedStatement(MappedStatement mappedStatement, SqlSource sqlSource){
        MappedStatement.Builder builder = new MappedStatement.Builder(mappedStatement.getConfiguration(),
                mappedStatement.getId(), sqlSource, mappedStatement.getSqlCommandType());
        builder.resource(mappedStatement.getResource());
        builder.fetchSize(mappedStatement.getFetchSize());
        builder.statementType(mappedStatement.getStatementType());
        builder.keyGenerator(mappedStatement.getKeyGenerator());
        if (mappedStatement.getKeyProperties() != null) {
            for (String keyProperty : mappedStatement.getKeyProperties()) {
                builder.keyProperty(keyProperty);
            }
        }
        builder.timeout(mappedStatement.getTimeout());
        builder.parameterMap(mappedStatement.getParameterMap());
        builder.resultMaps(mappedStatement.getResultMaps());
        builder.cache(mappedStatement.getCache());
        builder.useCache(mappedStatement.isUseCache());
        return builder.build();
    }

    @Override
    public void setProperties(Properties properties) {
        String dialectType = properties.getProperty("dialectType");
        String dialectClazz = properties.getProperty("dialectClazz");
        if (StringUtil.isNotEmpty(dialectType)) {
            this.dialectType = dialectType;
        }
        if (StringUtil.isNotEmpty(dialectClazz)) {
            this.dialectClazz = dialectClazz;
        }
    }

    public static class AdvanceQuerySqlSource implements SqlSource{
        BoundSql boundSql;

        public AdvanceQuerySqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
