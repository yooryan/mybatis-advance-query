# MyBatis-advance-query 动态高级查询插件

## 前言

 动态SQL是MyBatis中强大的特性之一。但在许多时候数据查询条件是需要动态构建的,因此需要
 
 在*.xml文件中拼写许多<where> , <if test "condition">条件。想要成为一名出色程序猿的必要条件就是
 
 如何提升开发效率(更好的偷懒),因此封装了一个可配置动态查询SQL的小插件。

## Support

默认情况下使用MySQL方言进行查询条件构建,如果想要实现自己的查询逻辑，可以实现 `IDialectAdvanceQuery`(com.github.yooryan.advancequery.dialects.IDialectAdvanceQuery)

1. `Mysql`

## 使用方法

### 1. 引入插件
 添加gradle依赖
```xml  
compile('com.github.yooryan:mybatis-advance-query:1.0.0-SNAPSHOT')
```

### 2. 配置拦截器插件
 在MyBatis配置类中加入以下拦截器
```java
@Bean
public AdvanceQueryInterceptor advanceQueryInterceptor(){
    return new AdvanceQueryInterceptor();
}
```
如果已经实现了自定义查询条件逻辑设置`dialectClazz`属性值即可
```java
advanceQueryInterceptor.setDialectClazz(dialectClazz);
```

### 3. 如何使用  

```java
//在mapper接口定义的条件查询类上的字段配置@AdvanceSqlOp注解
userMapper.page((@Param("page") Page page, @Param("filter") User user);

public User {
    //EQ即为=
    @AdvanceSqlOp(SqlKeyword.EQ)
    private String name;
    //LE即为<=
    @AdvanceSqlOp(SqlKeyword.LE)
    private Integer age;
}
```
##### 例一：
```java
User user = new User();
user.setName("Jenny");
user.setAge(18);
userMapper.page(page,user);
```

```sql
//拦截处理后的sql语句
SELECT * FROM ( select * from user) temp where	temp.name =	? and temp.age <= ?
```

#### 1). 参数介绍

```java
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
```
1.`value`:类型为枚举`SqlKeyWord`,包含一些常见的操作符号=, < , > ,IN , NOT IN , LIKE , BETWEEN等

2.`camelCaseToUnderscoreMap` : 对属性值进行命名转换,驼峰命名转换为下划线(_)

3.`alias`:设定别名

**重要提示：**

1.在原始sql上务必返回需要动态构建的条件字段,否则抛出异常(待优化)

2.如果在原始sql中定义了驼峰别名,请设置别名或将默认的命名转换设置为false,否则抛出sql语法错误异常(待优化)


```
报错原因: 原始SQL语句进行了语法上的包装
 如: SELECT * FROM ( select * from user) temp where    temp.name =    ? and temp.age <= ?
 当原始sql上没有返回name字段或age字段或字段名称无法匹配时语法编译不通过
```

## 结语
  目前只处于雏形阶段,缺乏大量的测试验证,以后多抽点抠脚的时间继续完善
