/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50720
Source Host           : localhost:3306
Source Database       : blog

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2020-04-11 21:32:07
*/

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for blog
-- ----------------------------
DROP TABLE IF EXISTS `blog`;
CREATE TABLE `blog`
(
    `blog_id`         int(11) NOT NULL AUTO_INCREMENT,
    `type_id`         int(11)      DEFAULT NULL,
    `user_id`         int(11)      DEFAULT NULL,
    `title`           varchar(255) DEFAULT NULL,
    `content`         longtext,
    `first_picture`   varchar(255) DEFAULT NULL,
    `flag`            varchar(255) DEFAULT NULL,
    `views`           int(11)      DEFAULT NULL,
    `appreciation`    tinyint(1)   DEFAULT '0',
    `share_statement` tinyint(1)   DEFAULT '0',
    `comment`         tinyint(1)   DEFAULT '0',
    `publish`         tinyint(1)   DEFAULT '0',
    `recommend`       tinyint(1)   DEFAULT '0',
    `create_time`     datetime     DEFAULT NULL,
    `update_time`     datetime     DEFAULT NULL,
    `description`     longtext,
    PRIMARY KEY (`blog_id`),
    KEY `FK_Reference_1` (`type_id`),
    KEY `FK_Reference_2` (`user_id`),
    CONSTRAINT `FK_Reference_1` FOREIGN KEY (`type_id`) REFERENCES `type` (`type_id`),
    CONSTRAINT `FK_Reference_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 24
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of blog
-- ----------------------------
INSERT INTO `blog`
VALUES ('8', '6', '1', '条件注解@Conditional',
        '# 简介\r\n在学习SpringBoot的时候我们会在各种自动配置类中看到大量使用了@Conditional注解，这篇文章主要简单说下@Conditional注解的使用方法。\r\n\r\n**定义：**\r\n按照《Spring Boot实战》书中所说的：“@Conditional根据满足某一特定条件创建一个特定的Bean。比方说，当某一个jar包在一个类路径下的时候，自动配置一个或多个Bean；或者只有某个Bean被创建才会才会创建另外一个Bean。总的来说，就是根据特定判定条件来控制Bean的创建行为，这样我们可以利用这个特性进行一些自动的配置。”\r\n\r\n在使用该注解之前我们来看一下@Conditional的源码\r\n```java\r\n@Target({ElementType.TYPE, ElementType.METHOD})\r\n@Retention(RetentionPolicy.RUNTIME)\r\n@Documented\r\npublic @interface Conditional {\r\n    Class<? extends Condition>[] value();\r\n}\r\n```\r\n\r\n我们关注下面的两点：\r\n\r\n* @Target({ElementType.TYPE, ElementType.METHOD})：表示该注解可以加在类上，表示该类下面的所有@Bean都会启用配置；也可以加在方法上，只对该方法有效\r\n\r\n* Class<? extends Condition>[] value()：表示要传一个Class集合给该注解，即@Conditional({XXX.class,XXX.class}) ，而且XXX类要继承Condition接口。关于Condition下面会说。\r\n\r\n\r\n# 示例\r\n我们以自己电脑的操作系统为判定条件，若我们的操作系统为Linux则创建Linux之父的Person的Bean；若操作系统为Windows则创建Windows之父的Person的Bean。\r\n\r\n**一、首先我们来创建一个Person类：**\r\n\r\n```java\r\npackage com.study.spring.annotation.bean;\r\n\r\n/**\r\n * Created on 2020/3/31\r\n * Package com.study.spring.annotation.bean\r\n *\r\n * @author dsy\r\n */\r\npublic class Person {\r\n    private String name;\r\n    private Integer age;\r\n\r\n    public Person() {\r\n    }\r\n\r\n    public Person(String name, Integer age) {\r\n        this.name = name;\r\n        this.age = age;\r\n    }\r\n\r\n    public String getName() {\r\n        return name;\r\n    }\r\n\r\n    public void setName(String name) {\r\n        this.name = name;\r\n    }\r\n\r\n    public Integer getAge() {\r\n        return age;\r\n    }\r\n\r\n    public void setAge(Integer age) {\r\n        this.age = age;\r\n    }\r\n\r\n    @Override\r\n    public String toString() {\r\n        return \"Person{\" +\r\n                \"name=\'\" + name + \'\\\'\' +\r\n                \", age=\" + age +\r\n                \'}\';\r\n    }\r\n}\r\n\r\n```\r\n**二、再创建一个配置类MyConfig：**\r\n\r\n```java\r\npackage com.study.spring.annotation.config;\r\n\r\nimport com.study.spring.annotation.bean.Person;\r\nimport com.study.spring.annotation.condition.LinuxCondition;\r\nimport com.study.spring.annotation.condition.WindowsCondition;\r\nimport org.springframework.context.annotation.*;\r\n\r\n/**\r\n * Created on 2020/3/31\r\n * Package com.study.spring.annotation.config\r\n *\r\n * @author dsy\r\n */\r\n@Configuration        //标识该类是一个配置类，相当于之前的一个xml配置文件\r\npublic class MyConfig {\r\n \r\n    /**\r\n     * @Conditional({Condition})  注解：按照一定的条件将bean添加到IOC容器中\r\n     */\r\n    @Conditional({LinuxCondition.class})\r\n    @Bean(\"linux\")\r\n    public Person person01(){\r\n        return new Person(\"linux之父\",55);\r\n    }\r\n\r\n    @Bean(\"windows\")\r\n    @Conditional({WindowsCondition.class})\r\n    public Person person02(){\r\n        return new Person(\"windows之父\",48);\r\n    }\r\n\r\n}\r\n\r\n```\r\n\r\n**三、接下来创建两个Condition类：LinuxCondition和WindowsCondition：**\r\n\r\n```java\r\npackage com.study.spring.annotation.condition;\r\n\r\nimport org.springframework.context.annotation.Condition;\r\nimport org.springframework.context.annotation.ConditionContext;\r\nimport org.springframework.core.env.Environment;\r\nimport org.springframework.core.type.AnnotatedTypeMetadata;\r\n\r\n/**\r\n * Created on 2020/3/31\r\n * Package com.study.spring.annotation.condition\r\n *\r\n * @author dsy\r\n */\r\n//判断是否Linux系统 要进行判断必须实现Condition接口\r\npublic class LinuxCondition implements Condition {\r\n\r\n    /**\r\n     *\r\n     * @param conditionContext  判断条件能使用的上下文环境\r\n     * @param annotatedTypeMetadata 当前标注了@Conditional注解的注释信息\r\n     * @return Boolean\r\n     */\r\n    @Override\r\n    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {\r\n        Environment environment = conditionContext.getEnvironment();\r\n        String property = environment.getProperty(\"os.name\");\r\n        return property!=null&&property.contains(\"Linux\");\r\n    }\r\n}\r\n\r\n```\r\n\r\n```java\r\npackage com.study.spring.annotation.condition;\r\n\r\nimport org.springframework.beans.factory.config.ConfigurableListableBeanFactory;\r\nimport org.springframework.beans.factory.support.BeanDefinitionRegistry;\r\nimport org.springframework.context.annotation.Condition;\r\nimport org.springframework.context.annotation.ConditionContext;\r\nimport org.springframework.core.env.Environment;\r\nimport org.springframework.core.type.AnnotatedTypeMetadata;\r\n\r\n/**\r\n * Created on 2020/3/31\r\n * Package com.study.spring.annotation.condition\r\n *\r\n * @author dsy\r\n */\r\n//判断是否Windows系统 要进行判断必须实现Condition接口\r\npublic class WindowsCondition implements Condition {\r\n\r\n    /**\r\n     *\r\n     * @param conditionContext  判断条件能使用的上下文环境\r\n     * @param annotatedTypeMetadata 当前标注了@Conditional注解的注释信息\r\n     * @return Boolean\r\n     */\r\n    @Override\r\n    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {\r\n        Environment environment = conditionContext.getEnvironment();\r\n        String property = environment.getProperty(\"os.name\");\r\n        return property != null && property.contains(\"Windows\");\r\n    }\r\n}\r\n\r\n```\r\n\r\n**说明：** 这两个类均实现了Condition接口，里面只有一个matches方法返回值为boolea类型，也就是说@Conditional({LinuxCondition.class})或者@Conditional({WindowsCondition.class})通过判断注解括号里面条件类的matches方法的返回值来判定要不要在IOC容器中创建由@Conditional注解标识的这个Bean；若matches返回值为true则创建该Bean，若返回false则不创建该Bean。\r\n\r\n```java\r\n@FunctionalInterface\r\npublic interface Condition {\r\n    boolean matches(ConditionContext var1, AnnotatedTypeMetadata var2);\r\n}\r\n```\r\n\r\n**四、测试：**\r\n\r\n```java\r\npackage com.study.spring;\r\n\r\nimport com.study.spring.annotation.bean.Person;\r\nimport com.study.spring.annotation.config.MyConfig;\r\nimport org.junit.jupiter.api.Test;\r\nimport org.springframework.boot.test.context.SpringBootTest;\r\nimport org.springframework.context.annotation.AnnotationConfigApplicationContext;\r\n\r\nimport java.util.Map;\r\n\r\n@SpringBootTest\r\nclass ApplicationTests {\r\n    /**\r\n     * @Conditional 注解来按照条件获取bean实例\r\n     */\r\n    @Test\r\n    void testAnnotationConfig2(){\r\n        //创建IOC容器\r\n        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MyConfig.class);\r\n        //getBeanNamesForType方法按照类来获取该类的Bean的id\r\n        String[] names = applicationContext.getBeanNamesForType(Person.class);\r\n        //打印\r\n        for (String name:names){\r\n            System.out.println(name);\r\n        }\r\n        //获取具体的Bean\r\n        Map<String, Person> beans = applicationContext.getBeansOfType(Person.class);\r\n        System.out.println(beans);\r\n    }\r\n\r\n}\r\n\r\n```\r\n**打印输出：**\r\n\r\n```java\r\nwindows\r\n{windows=Person{name=\'windows之父\', age=48}}\r\n```\r\n\r\n若我们改变运行环境，加上参数：-Dos.name=Linux\r\n![在这里插入图片描述](https://img-blog.csdnimg.cn/20200401134456722.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zOTI5NjI4Mw==,size_16,color_FFFFFF,t_70#pic_center)\r\n再次运行打印输出：\r\n\r\n```java\r\nlinux\r\n{linux=Person{name=\'linux之父\', age=55}}\r\n```\r\n\r\n# 总结\r\n1、@Conditional注解的作用是按照一定的判定条件来控制Bean的创建\r\n2、@Conditional可以加在一个配置类上，表示该类下面的全部的@Bean都会启用配置；也可以加在一个@Bean的方法上面，只对该方法有效\r\n3、判定条件类XXXCondition类要实现Condition接口，并重写matches方法，在该方法里面写自己的判定逻辑\r\n\r\n参考资料：《Spring Boot实战》\r\n',
        'https://images.unsplash.com/photo-1523895665936-7bfe172b757d?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80',
        '原创', '2589', '1', '1', '1', '1', '1', '2020-04-08 09:12:44', '2020-04-11 07:03:28',
        '在学习SpringBoot的时候我们会在各种自动配置类中看到大量使用了@Conditional注解，这篇文章主要简单说下@Conditional注解的使用方法。');
INSERT INTO `blog`
VALUES ('15', '6', '1', 'Java合并两个int数组',
        '```java\r\npublic static void main(String[] args) {\r\n        int[] a = {1,2};\r\n        int[] b = {3,4};\r\n        System.out.println(Arrays.toString(contact(a, b)));\r\n    }\r\npublic static int[] contact(int[] a, int[] b) {\r\n        int[] result = new int[a.length + b.length];\r\n        for (int i = 0; i < result.length; i++) {\r\n            if (i < a.length) {\r\n                result[i] = a[i];\r\n            } else {\r\n                result[i] = b[i - a.length];\r\n            }\r\n        }\r\n        return result;\r\n    }\r\n```\r\n\r\n打印输出：\r\n\r\n```java\r\n[1, 2, 3, 4]\r\n```\r\n',
        'https://images.unsplash.com/photo-1586398798112-9aa9aa7b35f1?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80',
        '原创', '2222', '1', '1', '1', '1', '1', '2020-04-11 07:15:46', '2020-04-11 07:15:46', '记录下Java代码实现合并两个int数组');
INSERT INTO `blog`
VALUES ('16', '6', '1', 'redis学习笔记（一）：String',
        '# String类型\r\n\r\n## 简介\r\nstring类型是Redis最基本的数据类型，一个键最大能存储512MB。\r\nstring数据结构是最简单的key-value类型，value不仅可以是string，也可以是数字，是包含很多种类型的特殊结构类型。\r\nstring类型是二进制安全的。意思是redis可以包含任何数据。比如序列化的对象进行存储，比如一张图片进行二进制存储，再比如一个简单的字符串，数值等等。\r\n## String命令\r\n#### 赋值语法：\r\n\r\n**一、SET key value [EX seconds|PX milliseconds] [NX|XX] [KEEPTTL]**\r\nSET 命令用于设置给定 key 的值。如果 key 已经存储值， SET 就覆写旧值，且无视类型 。\r\n### Options\r\nThe SET command supports a set of options that modify its behavior:\r\n\r\n* EX seconds -- Set the specified expire time, in seconds.\r\n* PX milliseconds -- Set the specified expire time, in milliseconds.\r\n* NX -- Only set the key if it does not already exist.\r\n* XX -- Only set the key if it already exist.\r\n* KEEPTTL -- Retain the time to live associated with the key.\r\n### Examples\r\n\r\n```java\r\n127.0.0.1:6379> set name dsy\r\nOK\r\n127.0.0.1:6379> get name\r\n\"dsy\"\r\n127.0.0.1:6379> set age 23 EX 10   //有效期为60秒\r\nOK\r\n127.0.0.1:6379> keys *   //立刻查询\r\n 1) \"name\"\r\n 2) \"age\"\r\n\r\n127.0.0.1:6379> keys *   //过一会查询\r\n 1) \"name\"\r\n\r\n```\r\n\r\n**二、SETNX key value**\r\n如果键不存在，则将键设置为value值，在这种情况下，它等于SET。当键已经具有值时，将不执行任何操作。 SETNX是“ SET if Not eXists”的缩写。\r\n\r\n**返回值**\r\n* 0：如果key之前已经被set\r\n* 1：如果key之前不存在\r\n### Examples\r\n\r\n```java\r\n127.0.0.1:6379> setnx key1 hello\r\n(integer) 1\r\n127.0.0.1:6379> setnx key1 world\r\n(integer) 0\r\n127.0.0.1:6379> get key1\r\n\"hello\"\r\n\r\n```\r\n**三、SETEX key seconds value**\r\n将值 value 关联到 key ，并将 key 的过期时间设为 seconds (以秒为单位)。如果 key 已经存在， SETEX 命令将会替换旧的值。\r\n### Examples\r\n\r\n```javascript\r\n127.0.0.1:6379> keys *\r\n1) \"name\"\r\n2) \"key1\"\r\n127.0.0.1:6379> setex name 10 aaa\r\nOK\r\n127.0.0.1:6379> get name\r\n\"aaa\"\r\n127.0.0.1:6379> get name\r\n(nil)\r\n\r\n```\r\n\r\n**四、SETRANGE key offset value**\r\n\r\n用指定的字符串覆盖给定 key 所储存的字符串值，覆盖的位置从偏移量 offset 开始。\r\n\r\n**返回值：** 被修改后的字符串长度。\r\n### Examples\r\n\r\n```java\r\n\r\n127.0.0.1:6379> set name hello world\r\n(error) ERR syntax error\r\n127.0.0.1:6379> set name \"hello world\"\r\nOK\r\n127.0.0.1:6379> setrange name 6 redis\r\n(integer) 11\r\n127.0.0.1:6379> get name\r\n\"hello redis\"\r\n\r\n```\r\n\r\n#### 取值语法：\r\n**一、GET key**\r\nRedis Get 命令用于获取指定 key 的值。如果 key 不存在，返回 nil 。如果key 储存的值不是字符串类型，返回一个错误。\r\n### Examples\r\n\r\n```java\r\n\r\n127.0.0.1:6379> keys *\r\n1) \"name\"\r\n2) \"key1\"\r\n127.0.0.1:6379> get name\r\n\"hello redis\"\r\n127.0.0.1:6379> get key1\r\n\"hello\"\r\n127.0.0.1:6379> get aaa\r\n(nil)\r\n\r\n```\r\n\r\n**二、GETRANGE key start end**\r\nRedis Getrange 命令用于获取存储在指定 key 中字符串的子字符串。字符串的截取范围由 start 和 end 两个偏移量决定(包括 start 和 end 在内)。\r\n### Examples\r\n\r\n```java\r\n127.0.0.1:6379> get name\r\n\"hello redis\"\r\n127.0.0.1:6379> getrange name 6 10\r\n\"redis\"\r\n\r\n```\r\n**三、GETSET key value**\r\nRedis Getset 命令用于设置指定 key 的值，并返回 key 旧的值。\r\n### Examples\r\n```java\r\n\r\n127.0.0.1:6379> set number 9\r\nOK\r\n127.0.0.1:6379> incr number\r\n(integer) 10\r\n127.0.0.1:6379> getset number 8\r\n\"10\"\r\n127.0.0.1:6379> get number\r\n\"8\"\r\n\r\n```\r\n**三、STRLEN key**\r\nRedis Strlen 命令用于获取指定 key 所储存的字符串值的长度。当 key 储存的不是字符串值时，返回一个错误。\r\n### Examples\r\n```java\r\n\r\n127.0.0.1:6379> get name\r\n\"hello redis\"\r\n127.0.0.1:6379> strlen name\r\n(integer) 11\r\n\r\n```\r\n\r\n#### 删除语法：\r\n**DEL key**\r\nRedis DEL 命令用于删除已存在的键。不存在的 key 会被忽略。\r\n### Examples\r\n```java\r\n\r\n127.0.0.1:6379> keys *\r\n1) \"number\"\r\n2) \"name\"\r\n3) \"key1\"\r\n127.0.0.1:6379> del number\r\n(integer) 1\r\n127.0.0.1:6379> del number\r\n(integer) 0\r\n\r\n\r\n```\r\n\r\n',
        'https://images.unsplash.com/photo-1586397148211-1f5967549ceb?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60',
        '原创', '2345', '1', '1', '1', '1', '1', '2020-04-11 07:16:59', '2020-04-11 07:16:59',
        'string类型是Redis最基本的数据类型，一个键最大能存储512MB。string数据结构是最简单的key-value类型，value不仅可以是string，也可以是数字，是包含很多种类型的特殊结构类型。string类型是二进制安全的。意思是redis可以包含任何数据。比如序列化的对象进行存储，比如一张图片进行二进制存储，再比如一个简单的字符串，数值等等。');
INSERT INTO `blog`
VALUES ('17', '6', '1', 'redis学习笔记（二）：Hash',
        '# 简介\r\nRedis hash 是一个string类型的field和value的映射表，hash特别适合用于存储对象。 Redis 中每个 hash 可以存储 232 - 1 键值对（40多亿）\r\n 可以看成具有KEY和VALUE的MAP容器，该类型非常适合于存储值对象的信息， 如：uname,uage等。该类型的数据仅占用很少的磁盘空间（相比于JSON） 。\r\n\r\n# Hash命令\r\n### 赋值语法\r\n**HSET key field value [field value ...]**\r\n\r\nRedis Hset 命令用于为哈希表中的字段赋值 。\r\n如果哈希表不存在，一个新的哈希表被创建并进行 HSET 操作。\r\n如果字段已经存在于哈希表中，旧值将被覆盖。\r\n\r\n##### Examples\r\n\r\n```powershell\r\n127.0.0.1:6379> hset people name dsy age 22 height 175\r\n(integer) 3\r\n127.0.0.1:6379> keys *\r\n1) \"people\"\r\n\r\n```\r\n### 取值语法\r\n**一、HGET key field**\r\n\r\nRedis Hget 命令用于返回哈希表中指定字段的值。\r\n##### Examples\r\n\r\n```powershell\r\n127.0.0.1:6379> hget people name\r\n\"dsy\"\r\n127.0.0.1:6379> hget people age\r\n\"22\"\r\n127.0.0.1:6379> hget people height\r\n\"175\"\r\n\r\n```\r\n**二、HGETALL key field**\r\n\r\nRedis Hgetall 命令用于返回哈希表中，所有的字段和值。\r\n在返回值里，紧跟每个字段名(field name)之后是字段的值(value)，所以返回值的长度是哈希表大小的两倍。\r\n##### Examples\r\n\r\n```powershell\r\n\r\n127.0.0.1:6379> hgetall people\r\n1) \"name\"\r\n2) \"dsy\"\r\n3) \"age\"\r\n4) \"22\"\r\n5) \"height\"\r\n6) \"175\"\r\n\r\n```\r\n**三、HMGET key field [field ...]**\r\n\r\nRedis Hmget 命令用于返回哈希表中，一个或多个给定字段的值。\r\n如果指定的字段不存在于哈希表，那么返回一个 nil 值。\r\n##### Examples\r\n\r\n```powershell\r\n\r\n127.0.0.1:6379> hmget people name age height weight\r\n1) \"dsy\"\r\n2) \"22\"\r\n3) \"175\"\r\n4) (nil)\r\n\r\n```\r\n\r\n**四、HKEYS key**\r\n\r\nRedis Hkeys 命令用于获取哈希表中的所有字段名。\r\n##### Examples\r\n\r\n```powershell\r\n\r\n127.0.0.1:6379> hkeys people\r\n1) \"name\"\r\n2) \"age\"\r\n3) \"height\"\r\n\r\n```\r\n\r\n### 删除语法\r\n**HDEL key field [field ...]**\r\n\r\nRedis Hdel 命令用于删除哈希表 key 中的一个或多个指定字段，不存在的字段将被忽略。\r\n##### Examples\r\n\r\n```powershell\r\n\r\n127.0.0.1:6379> hset cat number 2 color yellow\r\n(integer) 2\r\n127.0.0.1:6379> hdel cat number\r\n(integer) 1\r\n127.0.0.1:6379> hgetall cat\r\n1) \"color\"\r\n2) \"yellow\"\r\n\r\n```\r\n\r\n### 其他语法\r\n**一、HEXISTS key field**\r\n\r\nRedis Hexists 命令用于查看哈希表的指定字段是否存在。\r\n##### Examples\r\n\r\n```powershell\r\n\r\n127.0.0.1:6379> hexists people name\r\n(integer) 1\r\n127.0.0.1:6379> hexists people color\r\n(integer) 0\r\n\r\n```\r\n\r\n**二、HINCRBY key field increment**\r\n\r\nRedis Hincrby 命令用于为哈希表中的字段值加上指定增量值。\r\n增量也可以为负数，相当于对指定字段进行减法操作。\r\n如果哈希表的 key 不存在，一个新的哈希表被创建并执行 HINCRBY 命令。\r\n如果指定的字段不存在，那么在执行命令前，字段的值被初始化为 0 。\r\n对一个储存字符串值的字段执行 HINCRBY 命令将造成一个错误。\r\n本操作的值被限制在 64 位(bit)有符号数字表示之内。\r\n##### Examples\r\n\r\n```powershell\r\n127.0.0.1:6379> hget people age\r\n\"22\"\r\n127.0.0.1:6379> hincrby people age 10\r\n(integer) 32\r\n127.0.0.1:6379> hincrby people age -10\r\n(integer) 22\r\n127.0.0.1:6379> hincrby people money -10\r\n(integer) -10\r\n127.0.0.1:6379> hgetall people\r\n1) \"name\"\r\n2) \"dsy\"\r\n3) \"age\"\r\n4) \"22\"\r\n5) \"height\"\r\n6) \"175\"\r\n7) \"money\"\r\n8) \"-10\"\r\n127.0.0.1:6379> hincrby people name 1\r\n(error) ERR hash value is not an integer\r\n\r\n```\r\n**三、HINCRBYFLOAT key field increment**\r\n\r\nRedis Hincrbyfloat 命令用于为哈希表中的字段值加上指定浮点数增量值。\r\n如果指定的字段不存在，那么在执行命令前，字段的值被初始化为 0 。\r\n##### Examples\r\n\r\n```powershell\r\n127.0.0.1:6379> hgetall people\r\n1) \"name\"\r\n2) \"dsy\"\r\n3) \"age\"\r\n4) \"22\"\r\n5) \"height\"\r\n6) \"175\"\r\n7) \"money\"\r\n8) \"-10\"\r\n127.0.0.1:6379> hincrbyfloat people money 100.5\r\n\"90.5\"\r\n\r\n```\r\n**四、HLEN key**\r\n\r\nRedis Hlen 命令用于获取哈希表中字段的数量。\r\n##### Examples\r\n\r\n```powershell\r\n127.0.0.1:6379> hkeys people\r\n1) \"name\"\r\n2) \"age\"\r\n3) \"height\"\r\n4) \"money\"\r\n127.0.0.1:6379> hlen people\r\n(integer) 4\r\n\r\n```\r\n\r\n**四、HVALS key**\r\n\r\nRedis Hvals 命令返回哈希表所有字段的值。\r\n\r\n##### Examples\r\n\r\n```powershell\r\n\r\n127.0.0.1:6379> hgetall people\r\n1) \"name\"\r\n2) \"dsy\"\r\n3) \"age\"\r\n4) \"22\"\r\n5) \"height\"\r\n6) \"175\"\r\n7) \"money\"\r\n8) \"90.5\"\r\n127.0.0.1:6379> hkeys people\r\n1) \"name\"\r\n2) \"age\"\r\n3) \"height\"\r\n4) \"money\"\r\n127.0.0.1:6379> hvals people\r\n1) \"dsy\"\r\n2) \"22\"\r\n3) \"175\"\r\n4) \"90.5\"\r\n\r\n```',
        'https://images.unsplash.com/photo-1586521995568-39abaa0c2311?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60',
        '原创', '3045', '1', '1', '1', '1', '1', '2020-04-11 07:18:04', '2020-04-11 07:18:04',
        'Redis hash 是一个string类型的field和value的映射表，hash特别适合用于存储对象。 Redis 中每个 hash 可以存储 232 - 1 键值对（40多亿）可以看成具有KEY和VALUE的MAP容器，该类型非常适合于存储值对象的信息， 如：uname,uage等。该类型的数据仅占用很少的磁盘空间（相比于JSON） 。');
INSERT INTO `blog`
VALUES ('18', '6', '1', 'Java常见关键字final、static、super、abstract、this',
        '用此blog记录我学习的过程，文中若有不恰当的地方，还望友情指出互相学习。\r\n***\r\nJava有超过50多个关键字，这篇文章主要讲解一些常见的几个关键字，有兴趣的同学还可以[点此查看更多关键字](https://data-flair.training/blogs/java-keywords/)，下面进入正题：\r\n# final\r\nfinal关键字主要有三种用法：修饰类、修饰方法、修饰成员变量\r\n* **修饰类**\r\n    * 如果一个类被final修饰，则这个类不能被继承，例如：\r\n    ```java\r\n    final class A {\r\n    }\r\n    ```\r\n    下面的代码则无法编译：\r\n    ```java\r\n    class B extends A {    //编译错误\r\n    }\r\n    ```\r\n\r\n* **修饰方法**\r\n    * 当final关键字修饰在方法上时，这表明此方法不能被重写，例如：\r\n    ```java\r\n    class A {\r\n    	final void method(){}\r\n    }\r\n    ```\r\n    下面的代码则无法编译\r\n    ```java\r\n    class B extends A{\r\n    	void method(){}          //编译错误\r\n    }\r\n    ```\r\n* **修饰成员变量**\r\n    * 对于基本类型 final使其数值不变\r\n    * 对于引用类型 final使其引用不变\r\n    ```java\r\n    final String message = \"hello\";\r\n    message = \"world\";  //cannot assign value to final variable \'message\'\r\n    ```\r\n****注意：一个类不能同时被final和abstract修饰。因为被final修饰的类不能被继承；而被abstract修饰的类本身没有实现，必须通过子类来实现，也就是说要被继承****\r\n***\r\n# static\r\nstatic关键字主要有三种用法：静态内部类、静态变量、静态方法、静态导包\r\n* **静态内部类与非静态内部类**\r\n    * 非静态内部类依赖于外部类的实例，也就是说要先创建外部类的实例，才能用这个实例去创建非静态内部类，而静态内部类不需要\r\n    * 静态内部类可以声明静态成员变量和方法，而非静态内部类不行\r\n    * 非静态内部类可以随意访问外部类的成员变量和方法（静态+非静态），而静态内部类只能访问外部类的静态成员变量和方法\r\n```java\r\npublic class OuterClass {\r\n\r\n    public String name;\r\n    public static Integer number;\r\n\r\n    /**\r\n     * 非静态内部类\r\n     */\r\n    public class InnerClass{\r\n        //static Integer age; //非静态内部类不可以声明静态方法或变量\r\n        Integer age;\r\n\r\n        public void method() {\r\n            System.out.println(name);  //非静态内部类可以随意访问外部类的成员变量和方法\r\n        }\r\n    }\r\n\r\n    /**\r\n     * 静态内部类\r\n     */\r\n    public static class StaticInnerClass{\r\n        static Integer age;    //静态内部类可以声明静态成员变量和方法\r\n        public void method(){\r\n            System.out.println(number);   //静态内部类可以访问外部类的静态成员变量和方法，但不能访问外部类的非静态的成员变量和方法\r\n        }\r\n    }\r\n\r\n    public static void main(String[] args) {\r\n        OuterClass outerClass = new OuterClass();\r\n        InnerClass innerClass = outerClass.new InnerClass();\r\n        StaticInnerClass staticInnerClass = new StaticInnerClass();\r\n    }\r\n}\r\n```\r\n* **静态方法与静态成员变量**\r\n    * 静态方法与静态变量可以通过类名去访问\r\n```java\r\npublic class A  {\r\n\r\n    static String phone;\r\n\r\n    static void staticMethod(){\r\n    }\r\n\r\n    void NonStaticMethod(){\r\n    }\r\n\r\n    public static void main(String[] args) {\r\n        A.phone = \"123456\";\r\n        A.staticMethod();\r\n        \r\n        A a = new A();\r\n        a.NonStaticMethod();\r\n    }\r\n}\r\n```\r\n * **静态导包**\r\n 格式为：import static 这两个关键字连用可以指定导入某个类中的指定静态资源，并且不需要使用类名调用类中静态成员，可以直接使用类中静态成员变量和成员方法\r\n***\r\n# super\r\n当派生类和基类具有相同的成员变量和方法时，在这种情况下，可能存在歧义，可以使用super关键字来解决，下面通过具体例子讲解：\r\n```java\r\nclass Vehicle {\r\n    int maxSpeed = 100;\r\n    void stop(){\r\n    	System.out.println(\"Vehicle stop\"); \r\n    }\r\n}\r\n	\r\nclass Car extends Vehicle{\r\n	int maxSpeed = 120;\r\n	void display() { \r\n	    /* print maxSpeed of base class (vehicle) */\r\n	    System.out.println(\"Maximum Speed: \" + super.maxSpeed); \r\n    }\r\n    void stop(){\r\n    	super.stop();\r\n    } \r\n}\r\n	\r\nclass Test { \r\n	public static void main(String[] args) { \r\n	     Car car= new Car(); \r\n	     car.display();\r\n	     car.stop(); \r\n	} \r\n} \r\n```\r\n输出结果：\r\n```java\r\nMaximum Speed: 100\r\nVehicle stop\r\n```\r\n***\r\n# abstract\r\n* abstract关键字可以修饰类或方法\r\n* abstract类可以扩展，但不能直接实例化，抽象类的目的是由派生类继承\r\n* abstract方法不在声明他的类中实现，但必须在某个子类中重写\r\n* 抽象类既可以有抽象方法又可以拥有非抽象方法\r\n* 当一个类中某个方法被abstract修饰，则该类也必须被abstract修饰\r\n\r\n举例说明：\r\n```java\r\nabstract class A  \r\n{ \r\n    abstract void m1();  //abstract方法不在声明他的类中实现\r\n      \r\n    // 抽象类既可以有抽象方法又可以拥有非抽象方法 \r\n    void m2() \r\n    { \r\n        System.out.println(\"This is a concrete method.\"); \r\n    } \r\n} \r\n   \r\nclass B extends A  \r\n{  \r\n    // B必须重写A中的抽象方法m1()\r\n    void m1() { \r\n        System.out.println(\"B\'s implementation of m2.\"); \r\n    } \r\n} \r\n  \r\npublic class AbstractDemo  \r\n{ \r\n    public static void main(String args[])  \r\n    { \r\n        B b = new B(); \r\n        b.m1(); \r\n        b.m2(); \r\n    } \r\n} \r\n```\r\n输出结果：\r\n\r\n```java\r\nB\'s implementation of m2.\r\nThis is a concrete method.\r\n```\r\n***\r\n# this\r\n基本上，this关键字用于引用类的当前实例。例如：\r\n\r\n```java\r\nclass Manager {\r\n    Employees[] employees;\r\n     \r\n    void manageEmployees() {\r\n        int totalEmp = this.employees.length;\r\n        System.out.println(\"Total employees: \" + totalEmp);\r\n        this.report();\r\n    }\r\n     \r\n    void report() { }\r\n}\r\n```\r\n在上面的例子中，this关键字用在了两个地方：\r\n* this.employees.length:用于获取类Manager当前实例的变量\r\n* this.report(): 调用类Manager当前实例的方法\r\n\r\n**注意：不能使用this关键字访问静态变量或方法，因为静态成员属于静态类实例而不是对象实例**',
        'https://images.unsplash.com/photo-1586521995568-39abaa0c2311?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60',
        '原创', '4554', '1', '1', '1', '1', '1', '2020-04-11 07:19:08', '2020-04-11 07:19:08',
        'Java有超过50多个关键字，这篇文章主要讲解一些常见的几个关键字，有兴趣的同学还可以点此查看更多关键字，下面进入正题：');
INSERT INTO `blog`
VALUES ('19', '4', '1', 'Java中 equals() 和 hashCode()的关系',
        '用此blog记录我学习的过程，文中若有不恰当的地方，还望友情指出互相学习。\r\n***\r\n上篇文章谈到了[==与equals()的区别](https://blog.csdn.net/weixin_39296283/article/details/104452201)，关于equals()方法与hashCode()方法网上有很多教程，这里我翻译一篇国外作者写的文章（文末会注明出处），我觉得写得很棒，分享给大家\r\n# Java equals()\r\n在Object类中定义了equals()方法，源码如下：\r\n```java\r\npublic boolean equals(Object obj) {\r\n        return (this == obj);\r\n}\r\n```\r\n根据[java文档](https://docs.oracle.com/javase/8/docs/api/)中对equals()方法的说明，该方法的实现要满足以下原则：\r\n - 自反性 ：对于任何非空的引用值x ， x.equals(x)应该返回true \r\n - 对称性：对于任何非空引用值x和y ， x.equals(y)应该返回true当且仅当y.equals(x)返回true \r\n - 传递性 ：对于任何非空引用值x ，y和z ，如果x.equals(y)返回true且y.equals(z)返回rue ，那么x.equals(z)应该返回true \r\n - 一致性 ：除非对equals()方法实现中使用的任何对象属性进行修改，否则x.equals(y)的多次调用应返回相同的结果（true or false）\r\n - 对于任何非空的引用值x ， x.equals(null)应该返回false \r\n# Java hashCode()\r\nJava hashCode()是一种native方法，它返回对象的整数哈希码值， hashCode()方法的常规协定为：\r\n - 除非对equals（）方法中使用的object属性进行修改，否则hashCode（）的多次调用应返回相同的整数值\r\n - 对象的哈希码值可以在同一应用程序的多次执行中更改\r\n - 如果根据equals（）方法，两个对象相等，则它们的哈希码相同，即x.equals(y)返回true, 则，x.hashCode()==y.hashCode()返回true\r\n - 如果根据equals（）方法，两个对象不相等，则它们的哈希码值可能相等也可能不相等\r\n# equals() and hashCode() 方法的原则\r\nJava hashCode（）和equals（）方法基于哈希表的实现，用于存储和检索数据。\r\nequals（）和hashCode（）的实现应遵循以下规则：\r\n - If o1.equals(o2), then o1.hashCode() == o2.hashCode() should always be true.\r\n - If o1.hashCode() == o2.hashCode is true, it doesn’t mean that o1.equals(o2) will be true.\r\n# 何时重写equals() 和hashCode() 方法\r\n当我们重写equals()方法时，几乎肯定要重写hashCode()方法了，以免我们的代码实现违反其规则\r\n请注意，如果违反了equals（）和hashCode（）合约，则程序不会引发任何异常；如果你不打算将此类用作哈希表键，则不会造成任何问题\r\n但如果你打算将此类用作hash表键，那么必须同时重写equals()与hashCode()方法\r\n我们先举个例字看看将一个类作为hash表键，但并不重写equals()与hashCode()方法会发生什么：\r\n```java\r\npublic class Student {\r\n    private String name;\r\n    private Integer age;\r\n\r\n	//getter and setter methods\r\n	\r\n    @Override\r\n    public String toString() {\r\n        return \"Student{\" +\r\n                \"name=\'\" + name + \'\\\'\' +\r\n                \", age=\" + age +\r\n                \'}\';\r\n    }\r\n}\r\n```\r\n```java\r\npublic class HashingTest {\r\n    public static void main(String[] args) {\r\n        Map<Student, Integer> map = getAllData();\r\n        Student student = new Student();\r\n        student.setAge(1);\r\n        student.setName(\"世银\");\r\n        System.out.println(student.hashCode());\r\n        Integer value = map.get(student);\r\n        System.out.println(value);  //null\r\n    }\r\n\r\n    private static Map<Student, Integer> getAllData() {\r\n        Map<Student, Integer> map = new HashMap<>();\r\n        Student student = new Student();\r\n        student.setAge(1);\r\n        student.setName(\"世银\");\r\n        System.out.println(student.hashCode());\r\n        map.put(student, 10);\r\n        return map;\r\n    }\r\n}\r\n```\r\n当我们运行程序时，会打印输出null。这是因为当使用Object的 hashCode（）方法来查找存储区以查找key，我们再次创建该key以检索数据，因此你会注意到两个对象的哈希码值不同，因此找不到该值\r\n# 重写 equals() and hashCode() 方法\r\n我们可以定义自己的equals（）和hashCode（）方法实现，但是如果我们不仔细实现它们，则在运行时可能会出现奇怪的问题，幸运的是，如今，大多数IDE提供了自动实现它们的方法，并且如果需要，我们可以根据需要进行更改。以下以IDEA为例，鼠标右键即可：\r\n![idea](https://img-blog.csdnimg.cn/20200223142938120.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zOTI5NjI4Mw==,size_16,color_FFFFFF,t_70)\r\n下面是自动生成的equals()方法与hashCode()方法：\r\n```java\r\n@Override\r\n    public boolean equals(Object o) {\r\n        if (this == o) return true;\r\n        if (o == null || getClass() != o.getClass()) return false;\r\n        Student student = (Student) o;\r\n        return Objects.equals(name, student.name) &&\r\n                Objects.equals(age, student.age);\r\n    }\r\n\r\n    @Override\r\n    public int hashCode() {\r\n        return Objects.hash(name, age);\r\n    }\r\n```\r\n注意到equals（）和hashCode（）方法都使用相同的字段进行计算，再次运行上面的测试程序会发现输出结果为10\r\n# 什么是 Hash 碰撞\r\n简单来说，Java Hash表使用以下逻辑来实现对数据的获取和放置操作：\r\n\r\n1. 首先使用key的hash码来确定要使用的存储桶\r\n2. 如果存储桶中不存在具有相同哈希码的对象，则添加该对象以进行放置操作，并返回空值以进行获取操作\r\n3. 如果存储桶中还有其他具有相同哈希码的对象，则键的equals()方法开始起作用：\r\n* 如果equals（）返回true且是放置操作，则对象值将被覆盖\r\n* 如果equals（）返回false且是放置操作，则新entry将添加到存储桶中\r\n* 如果equals（）返回true并且是get操作，则返回对象值\r\n* 如果equals（）返回false并且是get操作，则返回null\r\n\r\n下图显示了HashMap的存储桶项以及它们的equals（）和hashCode（）是如何关联的\r\n![](https://img-blog.csdnimg.cn/20200223145030560.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zOTI5NjI4Mw==,size_16,color_FFFFFF,t_70)两个键具有相同哈希码的现象称为哈希冲突，上图中K1、K2和K3都具有相同的哈希码，因此发生了哈希冲突。如果hashCode（）方法未正确实现，则哈希冲突数量将更多，并且映射条目将无法正确分布，从而导致get和put操作变慢。这就是在生成哈希码时使用质数的原因，以便映射条目正确分布在所有存储桶中。*（感觉这都是数据结构中的内容哈）*\r\n# 如果不同时实现equals()方法和hashCode()方法会怎样\r\n* 上面我们已经看到，如果未实现hashCode（），我们将无法检索该值，因为HashMap使用哈希码来查找存储桶以查找数据\r\n* 如果我们仅使用hashCode（）而未实现equals（），那么由于equals（）方法将返回false，因此也不会检索到value\r\n# 实现equals()和hashCode()方法的最佳实践\r\n* 在equals（）和hashCode（）方法实现中使用相同的属性，以便在更新任何属性时都不会违反其合同\r\n* 最好将不可变的对象用作哈希表键，以便我们可以缓存哈希码，而不是在每次调用时都对其进行计算。这就是为什么String是哈希表键的理想选择的原因，因为它是不可变的，并且可以缓存哈希码值\r\n* 实现hashCode（）方法，以使发生最少数量的哈希冲突，并且条目在所有存储桶中均匀分布\r\n***\r\n\r\n**注：总之就是equals()方法与hashCode()方法要一起重写，尽量用String来作为哈希表键**\r\n\r\n原文出处：\r\nhttps://www.journaldev.com/21095/java-equals-hashcode\r\n\r\n\r\n',
        'https://images.unsplash.com/photo-1586397148211-1f5967549ceb?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60',
        '翻译', '4225', '1', '1', '1', '1', '1', '2020-04-11 07:19:58', '2020-04-11 07:19:58',
        '上篇文章谈到了==与equals()的区别，关于equals()方法与hashCode()方法网上有很多教程，这里我翻译一篇国外作者写的文章（文末会注明出处），我觉得写得很棒，分享给大家');
INSERT INTO `blog`
VALUES ('21', '10', '1', 'MarkDown标题示例', '# 一级标题\r\n## 二级标题\r\n### 三级标题\r\n#### 四级标题\r\n##### 五级标题\r\n###### 六级标题',
        'https://images.unsplash.com/photo-1586398798112-9aa9aa7b35f1?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80',
        '原创', '4441', '1', '1', '1', '1', '1', '2020-04-11 12:37:20', '2020-04-11 12:37:20',
        'Markdown 是一种轻量级的「标记语言」，创始人为约翰·格鲁伯，用简洁的语法代替排版，目前被越来越多的知识工作者、写作爱好者、程序员或研究员广泛使用。其常用的标记符号不超过十个，相对于更为复杂的 HTML 标记语言来说，Markdown 十分的轻量，学习成本也不需要太多，且一旦熟悉这种语法规则，会有沉浸式编辑的效果。');
INSERT INTO `blog`
VALUES ('22', '4', '1', '随笔',
        '| 帐户类型 | 免费帐户 | 标准帐户 | 高级帐户 |\r\n| --- | --- | --- | --- |\r\n| 帐户流量 | 60M | 1GB | 10GB |\r\n| 设备数目 | 2台 | 无限制 | 无限制 |\r\n| 当前价格 | 免费 | ￥8.17/月 | ￥12.33/月|',
        'https://images.unsplash.com/photo-1586398798112-9aa9aa7b35f1?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80',
        '原创', '5466', '1', '1', '0', '1', '1', '2020-04-11 12:39:54', '2020-04-11 12:39:54',
        'Markdown 是一种轻量级的「标记语言」，创始人为约翰·格鲁伯，用简洁的语法代替排版，目前被越来越多的知识工作者、写作爱好者、程序员或研究员广泛使用。其常用的标记符号不超过十个，相对于更为复杂的 HTML 标记语言来说，Markdown 十分的轻量，学习成本也不需要太多，且一旦熟悉这种语法规则，会有沉浸式编辑的效果。');
INSERT INTO `blog`
VALUES ('23', '7', '1', '欢迎使用 Markdown在线编辑器 MdEditor',
        '# 欢迎使用 Markdown在线编辑器 MdEditor\r\n\r\n**Markdown是一种轻量级的「标记语言」**\r\n\r\n\r\n![markdown](https://www.mdeditor.com/images/logos/markdown.png \"markdown\")\r\n\r\n\r\nMarkdown是一种可以使用普通文本编辑器编写的标记语言，通过简单的标记语法，它可以使普通文本内容具有一定的格式。它允许人们使用易读易写的纯文本格式编写文档，然后转换成格式丰富的HTML页面，Markdown文件的后缀名便是“.md”\r\n\r\n\r\n## MdEditor是一个在线编辑Markdown文档的编辑器\r\n\r\n*MdEditor扩展了Markdown的功能（如表格、脚注、内嵌HTML等等），以使让Markdown转换成更多的格式，和更丰富的展示效果，这些功能原初的Markdown尚不具备。*\r\n\r\n> Markdown增强版中比较有名的有Markdown Extra、MultiMarkdown、 Maruku等。这些衍生版本要么基于工具，如~~Pandoc~~，Pandao；要么基于网站，如GitHub和Wikipedia，在语法上基本兼容，但在一些语法和渲染效果上有改动。\r\n\r\nMdEditor源于Pandao的JavaScript开源项目，开源地址[Editor.md](https://github.com/pandao/editor.md \"Editor.md\")，并在MIT开源协议的许可范围内进行了优化，以适应广大用户群体的需求。向优秀的markdown开源编辑器原作者Pandao致敬。\r\n\r\n\r\n![Pandao editor.md](https://pandao.github.io/editor.md/images/logos/editormd-logo-180x180.png \"Pandao editor.md\")\r\n\r\n\r\n\r\n## MdEditor的功能列表演示\r\n\r\n# 标题H1\r\n\r\n## 标题H2\r\n\r\n### 标题H3\r\n\r\n#### 标题H4\r\n\r\n##### 标题H5\r\n\r\n###### 标题H5\r\n\r\n### 字符效果和横线等\r\n----\r\n\r\n~~删除线~~ <s>删除线（开启识别HTML标签时）</s>\r\n\r\n*斜体字*      _斜体字_\r\n\r\n**粗体**  __粗体__\r\n\r\n***粗斜体*** ___粗斜体___\r\n\r\n上标：X<sub>2</sub>，下标：O<sup>2</sup>\r\n\r\n**缩写(同HTML的abbr标签)**\r\n> 即更长的单词或短语的缩写形式，前提是开启识别HTML标签时，已默认开启\r\n\r\nThe <abbr title=\"Hyper Text Markup Language\">HTML</abbr> specification is maintained by the <abbr title=\"World Wide Web Consortium\">W3C</abbr>.\r\n### 引用 Blockquotes\r\n\r\n> 引用文本 Blockquotes\r\n\r\n引用的行内混合 Blockquotes\r\n\r\n> 引用：如果想要插入空白换行`即<br />标签`，在插入处先键入两个以上的空格然后回车即可，[普通链接](https://www.mdeditor.com/)。\r\n\r\n### 锚点与链接 Links\r\n[普通链接](https://www.mdeditor.com/)\r\n[普通链接带标题](https://www.mdeditor.com/ \"普通链接带标题\")\r\n直接链接：<https://www.mdeditor.com>\r\n[锚点链接][anchor-id]\r\n[anchor-id]: https://www.mdeditor.com/\r\n[mailto:test.test@gmail.com](mailto:test.test@gmail.com)\r\nGFM a-tail link @pandao\r\n邮箱地址自动链接 test.test@gmail.com  www@vip.qq.com\r\n> @pandao\r\n\r\n### 多语言代码高亮 Codes\r\n\r\n#### 行内代码 Inline code\r\n\r\n\r\n执行命令：`npm install marked`\r\n\r\n#### 缩进风格\r\n\r\n即缩进四个空格，也做为实现类似 `<pre>` 预格式化文本 ( Preformatted Text ) 的功能。\r\n\r\n    <?php\r\n        echo \"Hello world!\";\r\n    ?>\r\n预格式化文本：\r\n\r\n    | First Header  | Second Header |\r\n    | ------------- | ------------- |\r\n    | Content Cell  | Content Cell  |\r\n    | Content Cell  | Content Cell  |\r\n\r\n#### JS代码\r\n```javascript\r\nfunction test() {\r\n	console.log(\"Hello world!\");\r\n}\r\n```\r\n\r\n#### HTML 代码 HTML codes\r\n```html\r\n<!DOCTYPE html>\r\n<html>\r\n    <head>\r\n        <mate charest=\"utf-8\" />\r\n        <meta name=\"keywords\" content=\"Editor.md, Markdown, Editor\" />\r\n        <title>Hello world!</title>\r\n        <style type=\"text/css\">\r\n            body{font-size:14px;color:#444;font-family: \"Microsoft Yahei\", Tahoma, \"Hiragino Sans GB\", Arial;background:#fff;}\r\n            ul{list-style: none;}\r\n            img{border:none;vertical-align: middle;}\r\n        </style>\r\n    </head>\r\n    <body>\r\n        <h1 class=\"text-xxl\">Hello world!</h1>\r\n        <p class=\"text-green\">Plain text</p>\r\n    </body>\r\n</html>\r\n```\r\n### 图片 Images\r\n\r\n图片加链接 (Image + Link)：\r\n\r\n\r\n[![](https://www.mdeditor.com/images/logos/markdown.png)](https://www.mdeditor.com/images/logos/markdown.png \"markdown\")\r\n\r\n> Follow your heart.\r\n\r\n----\r\n### 列表 Lists\r\n\r\n#### 无序列表（减号）Unordered Lists (-)\r\n\r\n- 列表一\r\n- 列表二\r\n- 列表三\r\n\r\n#### 无序列表（星号）Unordered Lists (*)\r\n\r\n* 列表一\r\n* 列表二\r\n* 列表三\r\n\r\n#### 无序列表（加号和嵌套）Unordered Lists (+)\r\n+ 列表一\r\n+ 列表二\r\n    + 列表二-1\r\n    + 列表二-2\r\n    + 列表二-3\r\n+ 列表三\r\n    * 列表一\r\n    * 列表二\r\n    * 列表三\r\n\r\n#### 有序列表 Ordered Lists (-)\r\n\r\n1. 第一行\r\n2. 第二行\r\n3. 第三行\r\n\r\n#### GFM task list\r\n\r\n- [x] GFM task list 1\r\n- [x] GFM task list 2\r\n- [ ] GFM task list 3\r\n    - [ ] GFM task list 3-1\r\n    - [ ] GFM task list 3-2\r\n    - [ ] GFM task list 3-3\r\n- [ ] GFM task list 4\r\n    - [ ] GFM task list 4-1\r\n    - [ ] GFM task list 4-2\r\n\r\n----\r\n\r\n### 绘制表格 Tables\r\n\r\n| 项目        | 价格   |  数量  |\r\n| --------   | -----:  | :----:  |\r\n| 计算机      | $1600   |   5     |\r\n| 手机        |   $12   |   12   |\r\n| 管线        |    $1    |  234  |\r\n\r\nFirst Header  | Second Header\r\n------------- | -------------\r\nContent Cell  | Content Cell\r\nContent Cell  | Content Cell\r\n\r\n| First Header  | Second Header |\r\n| ------------- | ------------- |\r\n| Content Cell  | Content Cell  |\r\n| Content Cell  | Content Cell  |\r\n\r\n| Function name | Description                    |\r\n| ------------- | ------------------------------ |\r\n| `help()`      | Display the help window.       |\r\n| `destroy()`   | **Destroy your computer!**     |\r\n\r\n| Left-Aligned  | Center Aligned  | Right Aligned |\r\n| :------------ |:---------------:| -----:|\r\n| col 3 is      | some wordy text | $1600 |\r\n| col 2 is      | centered        |   $12 |\r\n| zebra stripes | are neat        |    $1 |\r\n\r\n| Item      | Value |\r\n| --------- | -----:|\r\n| Computer  | $1600 |\r\n| Phone     |   $12 |\r\n| Pipe      |    $1 |\r\n\r\n----\r\n\r\n#### 特殊符号 HTML Entities Codes\r\n\r\n&copy; &  &uml; &trade; &iexcl; &pound;\r\n&amp; &lt; &gt; &yen; &euro; &reg; &plusmn; &para; &sect; &brvbar; &macr; &laquo; &middot;\r\n\r\nX&sup2; Y&sup3; &frac34; &frac14;  &times;  &divide;   &raquo;\r\n\r\n18&ordm;C  &quot;  &apos;\r\n\r\n[========]\r\n\r\n### Emoji表情 :smiley:\r\n\r\n> Blockquotes :star:\r\n\r\n#### GFM task lists & Emoji & fontAwesome icon emoji & editormd logo emoji :editormd-logo-5x:\r\n\r\n- [x] :smiley: @mentions, :smiley: #refs, [links](), **formatting**, and <del>tags</del> supported :editormd-logo:;\r\n- [x] list syntax required (any unordered or ordered list supported) :editormd-logo-3x:;\r\n- [x] [ ] :smiley: this is a complete item :smiley:;\r\n- [ ] []this is an incomplete item [test link](#) :fa-star: @pandao;\r\n- [ ] [ ]this is an incomplete item :fa-star: :fa-gear:;\r\n    - [ ] :smiley: this is an incomplete item [test link](#) :fa-star: :fa-gear:;\r\n    - [ ] :smiley: this is  :fa-star: :fa-gear: an incomplete item [test link](#);\r\n\r\n#### 反斜杠 Escape\r\n\r\n\\*literal asterisks\\*\r\n\r\n[========]\r\n### 科学公式 TeX(KaTeX)\r\n\r\n$$E=mc^2$$\r\n\r\n行内的公式$$E=mc^2$$行内的公式，行内的$$E=mc^2$$公式。\r\n\r\n$$x > y$$\r\n\r\n$$\\(\\sqrt{3x-1}+(1+x)^2\\)$$\r\n\r\n$$\\sin(\\alpha)^{\\theta}=\\sum_{i=0}^{n}(x^i + \\cos(f))$$\r\n\r\n多行公式：\r\n\r\n```math\r\n\\displaystyle\r\n\\left( \\sum\\_{k=1}^n a\\_k b\\_k \\right)^2\r\n\\leq\r\n\\left( \\sum\\_{k=1}^n a\\_k^2 \\right)\r\n\\left( \\sum\\_{k=1}^n b\\_k^2 \\right)\r\n```\r\n```katex\r\n\\displaystyle\r\n    \\frac{1}{\r\n        \\Bigl(\\sqrt{\\phi \\sqrt{5}}-\\phi\\Bigr) e^{\r\n        \\frac25 \\pi}} = 1+\\frac{e^{-2\\pi}} {1+\\frac{e^{-4\\pi}} {\r\n        1+\\frac{e^{-6\\pi}}\r\n        {1+\\frac{e^{-8\\pi}}\r\n         {1+\\cdots} }\r\n        }\r\n    }\r\n```\r\n```latex\r\nf(x) = \\int_{-\\infty}^\\infty\r\n    \\hat f(\\xi)\\,e^{2 \\pi i \\xi x}\r\n    \\,d\\xi\r\n```\r\n### 分页符 Page break\r\n\r\n> Print Test: Ctrl + P\r\n\r\n[========]\r\n\r\n### 绘制流程图 Flowchart\r\n\r\n```flow\r\nst=>start: 用户登陆\r\nop=>operation: 登陆操作\r\ncond=>condition: 登陆成功 Yes or No?\r\ne=>end: 进入后台\r\n\r\nst->op->cond\r\ncond(yes)->e\r\ncond(no)->op\r\n```\r\n[========]\r\n\r\n### 绘制序列图 Sequence Diagram\r\n\r\n```seq\r\nAndrew->China: Says Hello\r\nNote right of China: China thinks\\nabout it\r\nChina-->Andrew: How are you?\r\nAndrew->>China: I am good thanks!\r\n```\r\n### End',
        'https://images.unsplash.com/photo-1586398798112-9aa9aa7b35f1?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80',
        '原创', '8888', '1', '1', '1', '1', '1', '2020-04-11 12:46:37', '2020-04-11 12:46:37',
        'Markdown是一种可以使用普通文本编辑器编写的标记语言，通过简单的标记语法，它可以使普通文本内容具有一定的格式。它允许人们使用易读易写的纯文本格式编写文档，然后转换成格式丰富的HTML页面，Markdown文件的后缀名便是“.md”');

-- ----------------------------
-- Table structure for blog_tag
-- ----------------------------
DROP TABLE IF EXISTS `blog_tag`;
CREATE TABLE `blog_tag`
(
    `blog_tag_id` int(11) NOT NULL AUTO_INCREMENT,
    `blog_id`     int(11) DEFAULT NULL,
    `tag_id`      int(11) DEFAULT NULL,
    PRIMARY KEY (`blog_tag_id`),
    KEY `FK_Reference_4` (`blog_id`),
    KEY `FK_Reference_5` (`tag_id`),
    CONSTRAINT `FK_Reference_4` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`blog_id`),
    CONSTRAINT `FK_Reference_5` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`tag_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 70
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of blog_tag
-- ----------------------------
INSERT INTO `blog_tag`
VALUES ('55', '8', '1');
INSERT INTO `blog_tag`
VALUES ('56', '15', '22');
INSERT INTO `blog_tag`
VALUES ('57', '16', '21');
INSERT INTO `blog_tag`
VALUES ('58', '17', '21');
INSERT INTO `blog_tag`
VALUES ('59', '18', '22');
INSERT INTO `blog_tag`
VALUES ('60', '19', '22');
INSERT INTO `blog_tag`
VALUES ('61', '21', '24');
INSERT INTO `blog_tag`
VALUES ('62', '22', '24');
INSERT INTO `blog_tag`
VALUES ('63', '23', '1');
INSERT INTO `blog_tag`
VALUES ('64', '23', '2');
INSERT INTO `blog_tag`
VALUES ('65', '23', '20');
INSERT INTO `blog_tag`
VALUES ('66', '23', '21');
INSERT INTO `blog_tag`
VALUES ('67', '23', '22');
INSERT INTO `blog_tag`
VALUES ('68', '23', '23');
INSERT INTO `blog_tag`
VALUES ('69', '23', '24');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`
(
    `comment_id`        int(11) NOT NULL AUTO_INCREMENT,
    `blog_id`           int(11)      DEFAULT NULL,
    `nick_name`         varchar(255) DEFAULT NULL,
    `email`             varchar(255) DEFAULT NULL,
    `content`           varchar(255) DEFAULT NULL,
    `avatar`            varchar(255) DEFAULT NULL,
    `create_time`       datetime     DEFAULT NULL,
    `parent_comment_id` int(11)      DEFAULT NULL,
    PRIMARY KEY (`comment_id`),
    KEY `FK_Reference_3` (`blog_id`),
    CONSTRAINT `FK_Reference_3` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`blog_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 22
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment`
VALUES ('19', '23', '路人1', '981179142@qq.com', '很强',
        'https://images.unsplash.com/photo-1537815749002-de6a533c64db?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=845&q=80',
        '2020-04-11 13:17:02', '-1');
INSERT INTO `comment`
VALUES ('21', '23', '路人乙', 'dushiyin@gmail.com', '666',
        'https://images.unsplash.com/photo-1537815749002-de6a533c64db?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=845&q=80',
        '2020-04-11 13:18:28', '-1');

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`
(
    `tag_id` int(11) NOT NULL AUTO_INCREMENT,
    `name`   varchar(255) DEFAULT NULL,
    PRIMARY KEY (`tag_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 25
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag`
VALUES ('1', 'Spring');
INSERT INTO `tag`
VALUES ('2', 'Mybatis');
INSERT INTO `tag`
VALUES ('20', '设计模式');
INSERT INTO `tag`
VALUES ('21', 'Redis');
INSERT INTO `tag`
VALUES ('22', 'Java');
INSERT INTO `tag`
VALUES ('23', 'MySQL');
INSERT INTO `tag`
VALUES ('24', '测试');

-- ----------------------------
-- Table structure for type
-- ----------------------------
DROP TABLE IF EXISTS `type`;
CREATE TABLE `type`
(
    `type_id` int(11) NOT NULL AUTO_INCREMENT,
    `name`    varchar(255) DEFAULT NULL,
    PRIMARY KEY (`type_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 16
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of type
-- ----------------------------
INSERT INTO `type`
VALUES ('4', '随笔');
INSERT INTO `type`
VALUES ('5', '思考');
INSERT INTO `type`
VALUES ('6', '学习日志');
INSERT INTO `type`
VALUES ('7', '生活感悟');
INSERT INTO `type`
VALUES ('8', '生活日志');
INSERT INTO `type`
VALUES ('10', '测试');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `user_id`     int(11) NOT NULL AUTO_INCREMENT,
    `nick_name`   varchar(255) DEFAULT NULL,
    `username`    varchar(255) DEFAULT NULL,
    `password`    varchar(255) DEFAULT NULL,
    `email`       varchar(255) DEFAULT NULL,
    `avatar`      varchar(255) DEFAULT NULL,
    `type`        int(11)      DEFAULT NULL,
    `create_time` datetime     DEFAULT NULL,
    `update_time` datetime     DEFAULT NULL,
    PRIMARY KEY (`user_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user`
VALUES ('1', '起风了', 'admin', '21232F297A57A5A743894A0E4A801FC3', '981179142@qq.com',
        'https://images.unsplash.com/photo-1534375971785-5c1826f739d8?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80',
        null, null, null);
