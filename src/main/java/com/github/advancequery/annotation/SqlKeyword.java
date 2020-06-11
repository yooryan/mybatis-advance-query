package com.github.advancequery.annotation;


import com.github.advancequery.toolkit.StringPool;

/**
 * SQL 保留关键字枚举
 *
 * @author linyunrui
 */
public enum SqlKeyword implements ISqlSegment {
    AND("AND"),
    OR("OR"),
    IN("IN"),
    NOT("NOT"),
    LIKE("LIKE"),
    EQ(StringPool.EQUALS),
    NE("<>"),
    GT(StringPool.RIGHT_CHEV),
    GE(">="),
    LT(StringPool.LEFT_CHEV),
    LE("<="),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    GROUP_BY("GROUP BY"),
    HAVING("HAVING"),
    ORDER_BY("ORDER BY"),
    EXISTS("EXISTS"),
    BETWEEN("BETWEEN"),
    ASC("ASC"),
    DESC("DESC");

    private final String keyword;

    SqlKeyword(final String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String getSqlSegment() {
        return this.keyword;
    }
}
