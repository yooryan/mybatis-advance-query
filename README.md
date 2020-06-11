# MyBatis-advance-query 动态高级查询插件

## 前言

 动态SQL是MyBatis中强大的特性之一。但在许多时候数据查询条件是动态构建的,因此需要在*.xml文件中编写<where>与<if test"condtition"> 条件。

 虽然在MyBatis-plus的强大支持下已经封装了单表情况下的条件查询,但是在多表关联的时候还是需要手动编写各种<where>,<if>条件。这也太烦了,

 为了提升开发效率(更好的偷懒),因此封装了一个小插件。目前只是雏形状态,支持简单的SQL语句构建,后面会持续更新优化。

## Support

默认情况下使用MySQL方言进行查询条件构建,如果想要实现自己的查询逻辑，可以实现 `IDialectAdvanceQuery`(com.github.advancequery.dialects.IDialectAdvanceQuery)

1. `Mysql`

## 使用方法

### 1. 引入插件
 添加gradle依赖
```xml  
compile('cn.lvji:mybatis-plugin-advance-query:1.0.0-SNAPSHOT')
```

### 2. 配置拦截器插件
 在MyBatis配置类中加入以下拦截器
```java
@Bean
public AdvanceQueryInterceptor advanceQueryInterceptor(){
    return new AdvanceQueryInterceptor();
}
```
 tips:如果已经实现了自定义查询条件逻辑设置`dialectClazz`属性值即可
```java
advanceQueryInterceptor.setDialectClazz(dialectClazz);
```

### 3. 如何使用  

```java
//只需要在mapper接口参数属性值上加上@AdvanceSqlOp注解
userMapper.page((@Param("page") Page page, @Param("filter") User filter);

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
1.`value`:类型为枚举`SqlKeyWord`,包含一些常见的操作符号=, < , > 等

2.`camelCaseToUnderscoreMap` : 对属性值进行命名转换,驼峰命名转换为下划线(_)

3.`alias`:设定别名

**重要提示：**

1.在原始sql上务必返回需要动态构建的条件字段,否则报错

2.如果在原始sql中定义了驼峰别名,请设置别名或将默认的命名转换设置为false,否则会出现sql语法报错


```
报错原因: 原始SQL语句进行了语法上的包装
 如: SELECT * FROM ( select * from user) temp where    temp.name =    ? and temp.age <= ?
 当原始sql上没有返回name字段或age字段或字段名称无法匹配时语法编译不通过
```

## 结语
  目前只处于雏形阶段,并且缺乏测试验证,希望后续能逐渐优化
