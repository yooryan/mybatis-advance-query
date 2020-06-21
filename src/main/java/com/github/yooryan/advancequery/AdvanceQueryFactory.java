package com.github.yooryan.advancequery;

import com.github.yooryan.advancequery.annotation.DbType;
import com.github.yooryan.advancequery.dialects.IDialectAdvanceQuery;
import com.github.yooryan.advancequery.dialects.MySqlDialectAdvanceQuery;
import com.github.yooryan.advancequery.exception.AdvanceQueryException;
import com.github.yooryan.advancequery.exception.SqlAutomaticBuildException;
import com.github.yooryan.advancequery.toolkit.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author linyunrui
 */
public class AdvanceQueryFactory {

    /**
     * 方言缓存
     */
    private static final Map<String, IDialectAdvanceQuery> DIALECT_CACHE = new ConcurrentHashMap<>();

    public static AdvanceQueryModel buildAdvanceQuerySql(List<AdvanceQuery> advanceQueries, String buildSql, DbType dbType, String dialectClazz) throws SqlAutomaticBuildException {
        // fix #196
        return getDialect(dbType, dialectClazz).buildAdvanceQuerySql(advanceQueries,buildSql);
    }

    /**
     * 获取数据库方言
     *
     * @param dbType       数据库类型
     * @param dialectClazz 自定义方言实现类
     * @return ignore
     */
    private static IDialectAdvanceQuery getDialect(DbType dbType, String dialectClazz) {
        IDialectAdvanceQuery dialectAdvanceQuery = DIALECT_CACHE.get(dbType.getDb());
        if (null == dialectAdvanceQuery) {
            // 自定义方言
            if (StringUtil.isNotEmpty(dialectClazz)) {
                dialectAdvanceQuery = DIALECT_CACHE.get(dialectClazz);
                if (null != dialectAdvanceQuery) {
                    return dialectAdvanceQuery;
                }
                try {
                    Class<?> clazz = Class.forName(dialectClazz);
                    //判断clazz是否继承IDialectAdvanceQuery
                    if (IDialectAdvanceQuery.class.isAssignableFrom(clazz)) {
                        dialectAdvanceQuery = (IDialectAdvanceQuery) clazz.newInstance();
                        DIALECT_CACHE.put(dialectClazz, dialectAdvanceQuery);
                    }
                } catch (ClassNotFoundException e) {
                    throw new AdvanceQueryException("Class : "+dialectClazz+" is not found", e);
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new AdvanceQueryException("Class : "+ dialectClazz +" can not be instance", e);
                }
            } else {
                // 缓存方言
                dialectAdvanceQuery = getDialectByDbType(dbType);
                DIALECT_CACHE.put(dbType.getDb(), dialectAdvanceQuery);
            }
            //配置方言失败
            if (null == dialectAdvanceQuery){
                throw new AdvanceQueryException("The value of the dialect property in mybatis configuration.xml is not defined.");
            }
        }
        return dialectAdvanceQuery;
    }

    /**
     * 根据数据库类型选择不同分页方言
     *
     * @param dbType 数据库类型
     * @return 分页语句组装类
     */
    private static IDialectAdvanceQuery getDialectByDbType(DbType dbType) {
        switch (dbType) {
            case MYSQL:
                return new MySqlDialectAdvanceQuery();
            default:
                throw new AdvanceQueryException("The Database's IDialect Not Supported!");
        }
    }
}
