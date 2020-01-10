## 全文检索综合实战

> 编程环境介绍 es6.8.6 + mysql5.7 + idea + logstsh

## 一、数据爬虫

### 1、网页分析

目标网站：

JSON数据代表

华为手机商城：https://consumer.huawei.com/cn/phones/?ic_medium=hwdc&ic_source=corp_header_consumer

HTML代表

魅族手机商城：<https://lists.meizu.com/page/list?categoryid=76>

[html特殊字符编码对照表](https://www.jb51.net/onlineread/htmlchar.htm)

### 1、Apache HttpComponents

[官网地址](http://hc.apache.org/index.html)

[Maven仓库](https://mvnrepository.com/)

Apache的一个开源项目，主要模拟HTTP请求。

### 2、jsoup

[官网地址](https://jsoup.org/)

jsoup 是一款Java 的HTML解析器，可直接解析某个URL地址、HTML文本内容。它提供了一套非常省力的API，可通过DOM，CSS以及类似于jQuery的操作方法来取出和操作数据。

帮助手册：http://hc.apache.org/httpcomponents-client-4.5.x/quickstart.html

- 手机实体类

  

- 爬取华为手机

```Java
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("https://consumer.huawei.com/cn/phones/?ic_medium=hwdc&ic_source=corp_header_consumer"); // 创建httpget实例
        CloseableHttpResponse response = httpclient.execute(httpget); // 执行get请求
        HttpEntity entity = response.getEntity(); // 获取返回实体
        String content = EntityUtils.toString(entity, "utf-8");
        response.close(); // 关闭流和释放系统资源

        Document document = Jsoup.parse(content);

        Elements elements = document.select("#content-v3-plp #pagehidedata .plphidedata");
        for (Element element : elements) {
            String jsonStr = element.text();
            List<HuaWeiPhoneBean> huaWeiPhoneBeanlist = JSON.parseArray(jsonStr, HuaWeiPhoneBean.class);
            for (HuaWeiPhoneBean bean : huaWeiPhoneBeanlist){
                String productName = bean.getProductName();
                List<ColorModeBean> colorModeBeanList = bean.getColorsItemMode();

                String colors = "";

                for (ColorModeBean colorModeBean : colorModeBeanList){
                    String colorName = colorModeBean.getColorName();
                    colors += colorName + ";";
                }

                List<String> sellingPointList = bean.getSellingPoints();
                String sellingPoints = "";
                for (String sellingPoint : sellingPointList) {
                    sellingPoints += sellingPoint+";";
                }
                System.out.println("产品名：" + productName);
                System.out.println("颜  色：" + colors);
                System.out.println("买  点：" + sellingPoints);
                Phone phone = new Phone();
                phone.setName(productName);
                phone.setColors(colors);
                phone.setSellingPoints(sellingPoints);
                phone.setCreateTime(new Date());
                phone.setMarketTime(new Date());
                phoneMysqlRepository.save(phone);
            }
        }
        return content;
```

- 爬取魅族

```java
        CloseableHttpClient httpclient = HttpClients.createDefault(); // 创建httpclient实例
        HttpGet httpget = new HttpGet("https://lists.meizu.com/page/list?categoryid=76"); // 创建httpget实例

        CloseableHttpResponse response = httpclient.execute(httpget); // 执行get请求
        HttpEntity entity=response.getEntity(); // 获取返回实体fsdf
        //System.out.println("网页内容："+ EntityUtils.toString(entity, "utf-8")); // 指定编码打印网页内容

        String content = EntityUtils.toString(entity, "utf-8");
        response.close(); // 关闭流和释放系统资

        Document document = Jsoup.parse(content);
        Elements names = document.select("#goodsListWrap .gl-item .gl-item-link .item-title");

        Elements cellingPoints = document.select("#goodsListWrap .gl-item .gl-item-link .item-desc");

        Elements colorsElements = document.select(".container .goods-list #goodsListWrap .gl-item .gl-item-link .item-slide");
        int i = 0;

        for (Element nameElement : names) {
            Phone phone = new Phone();
            phone.setName(nameElement.text());
            Elements elements = colorsElements.get(i).select(".item-slide-dot");
            String endcolors = "";
            for (Element color : elements){
                endcolors += color.attr("title") + ";";
            }
//            System.out.println(endcolors);
            phone.setSellingPoints(cellingPoints.get(i).text());
            phone.setColors(endcolors);
            phone.setCreateTime(new Date());
            phone.setMarketTime(new Date());
            phoneMysqlRepository.save(phone);
        }
        return null;
```

### 3、json在线编辑器

<http://www.newjson.com/Static/Json/jsoneditor.html>

## 二、MySQL数据库

[模型为什么要继承Serializable类](https://www.jianshu.com/p/3efef93f7e8f)

> MySQL驱动，数据访问抽象层jpa，连接池

### 1、添加依赖

#### MySQL驱动连接

```xml
        <!--添加数据库链接 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
```

#### 阿里云仓库地址

```xml
    <repositories><!-- 代码库 -->
        <repository>
            <id>maven-ali</id>
            <url>http://maven.aliyun.com/nexus/content/groups/public//</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
                <checksumPolicy>fail</checksumPolicy>
            </snapshots>
        </repository>
    </repositories>
```

#### mybatis-plus

```xml
# maven 依赖    
	<dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.3.0</version>
    </dependency>
# yml配置
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/amussh?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false
    username: root
    password: root
# Logger Config
logging:
  level:
    com.amu.esstudy.mapper: debug
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```



### 2、[官网spring-data-jpa手册](https://spring.io/projects/spring-data-jpa)

关键字：`@Query`，`@Entity`，` #{#entityName}`，`%:lastname%`，`@Param("lastname")`，`extends Repository`

### 3、数据库参数配置

```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/es?characterEncoding=utf-8&serverTimezone=GMT%2B8
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=root
# 使用 druid 数据源
spring.datasource.type: com.alibaba.druid.pool.DruidDataSource
spring.datasource.initialSize: 5
spring.datasource.minIdle: 5
spring.datasource.maxActive: 20
spring.datasource.maxWait: 60000
spring.datasource.timeBetweenEvictionRunsMillis: 60000
spring.datasource.minEvictableIdleTimeMillis: 300000
spring.datasource.validationQuery: SELECT 1 FROM DUAL
spring.datasource.testWhileIdle: true
spring.datasource.testOnBorrow: false
spring.datasource.testOnReturn: false
spring.datasource.poolPreparedStatements: true
spring.datasource.filters: stat
spring.datasource.maxPoolPreparedStatementPerConnectionSize: 20
spring.datasource.useGlobalDataSourceStat: true
spring.datasource.connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500
# SpringBoot JPA
spring.jpa.show-sql=true
# create 每次都重新创建表，update，表若存在则不重建
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQL55Dialect
```

### 4、数据库连接测试

看官网手册

新建一个全文检索方法

### 5、SQL日志，打印参数

在`rousouces`目录下新建`logback.xml`

内容如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true">
    <!-- 从application.yml 中注入变量  -->
    <!-- <springProperty scope="context" name="LOG_PATH" source="log.home"/> -->
    <!-- <springProperty scope="context" name="APPDIR" source="spring.application.name"/> -->
    <property name="LOG_PATH" value="./logs"/>
    <property name="APPDIR" value="graceLogs"/>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>1-%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger - %msg%n</pattern>
            <charset>GBK</charset>
        </encoder>
    </appender>

    <!-- error级别日志文件输出,按日期时间滚动记录输出 -->
    <appender name="FILEERROR" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/${APPDIR}/log_error.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/${APPDIR}/error/log-error-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>500MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
        <append>true</append>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %logger Line:%-3L - %msg%n</pattern>
            <charset>utf-8</charset>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>error</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- warn级别日志文件输出,按日期时间滚动记录输出 -->
    <appender name="FILEWARN" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/${APPDIR}/log_warn.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/${APPDIR}/warn/log-warn-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>2MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
        <append>true</append>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %logger Line:%-3L - %msg%n</pattern>
            <charset>utf-8</charset>        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>warn</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- info级别日志文件输出,按日期时间滚动记录输出 -->
    <appender name="FILEINFO" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/${APPDIR}/log_info.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/${APPDIR}/info/log-info-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>2MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
        <append>true</append>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %logger Line:%-3L - %msg%n</pattern>
            <charset>utf-8</charset>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>info</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %logger Line:%-3L - %msg%n</pattern>
        </encoder>
    </appender>

    <!--设置为OFF,即屏蔽; 留下sqltiming作为INFO级别输出-->
    <logger name="jdbc.connection" level="OFF"/>
    <logger name="jdbc.resultset" level="OFF"/>
    <logger name="jdbc.resultsettable" level="OFF"/>
    <logger name="jdbc.audit" level="OFF"/>
    <logger name="jdbc.sqltiming" level="OFF"/>
    <logger name="jdbc.sqlonly" level="OFF"/>
    
    <logger name="org.hibernate.SQL" level="DEBUG"/>
    <logger name="org.hibernate.engine.QueryParameters" level="DEBUG"/>
    <logger name="org.hibernate.engine.query.HQLQueryPlan" level="DEBUG"/>
    <logger name="org.hibernate.type.descriptor.sql.BasicBinder" level="TRACE"/>
    <logger name="org.hibernate.SQL" level="DEBUG"/>
    
    <!--设置日志打印级别为INFO-->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILEINFO"/>
        <appender-ref ref="FILEWARN"/>
        <appender-ref ref="FILEERROR"/>
    </root>

</configuration>
```

## 三、ES数据库

[开发文档](<https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.8/java-rest-overview.html>)

### 1、新建索引，类型，指定分词插件



### 2、分词插件的使用

```
GET _analyze?pretty
{
  "analyzer": "ik_smart",
  "text": "中华人民共和国国歌"
}

GET _analyze?pretty
{
  "analyzer": "ik_smart",
  "text": "可折叠设计，靓丽全面屏;巴龙5000，华为首款多模5G芯片;55W华为超级快充"
}
```



## 四、红娘Logstash

### 1、下载Logstash



### 2、插件安装

```bash
logstash-plugin install logstash-input-jdbc
logstash-plugin install logstash-output-elasticsearch
```

### 3、配置

```conf
input {
	jdbc{
		# jdbc驱动包的位置
		jdbc_driver_library => "C:\\exp\logstash-6.8.6\\config\\mysql-connector-java-8.0.17.jar"
		# 要使用的驱动包类，不同的数据库不同的类
		jdbc_driver_class => "com.mysql.cj.jdbc.Driver"
		# 数据库的链接信息 
		jdbc_connection_string => "jdbc:mysql://127.0.0.1:3306/es?characterEncoding=utf-8&serverTimezone=GMT%2B8"
		# mysql用户
		jdbc_user => "root"
		# mysql密码
		jdbc_password => "root"
		# 定时任务，多久执行一次查询，默认一分钟,这种配置是无延迟
		schedule => "* * * * *"
		# 清空上次的sql_last_value记录
		clean_run => true
		# 你要执行的语句
		statement => "SELECT * FROM phone WHERE create_time > :sql_last_value AND create_time < NOW() ORDER BY create_time desc"

	}
}
output {
	elasticsearch{
		# es host:port
		hosts => ["http://localhost:9200"]
		# 索引
		index => "phones"
		# _id
		document_id => "%{id}"
		document_type => "phone"
	}
}
```

### 4、启动

`logstash -f mysql2es.conf`

同步数据到ES

## 五、扩展

mybatis中文文档分为以下几个部分：

XML配置：<https://mybatis.org/mybatis-3/zh/configuration.html>

XML映射：<https://mybatis.org/mybatis-3/zh/sqlmap-xml.html>

动态SQL：<https://mybatis.org/mybatis-3/zh/dynamic-sql.html>

Java API：<https://mybatis.org/mybatis-3/zh/java-api.html>

SQL语句构建器：<https://mybatis.org/mybatis-3/zh/statement-builders.html>

日志：<https://mybatis.org/mybatis-3/zh/logging.html>

## 另外，spring与mybatis相结合使用的中文文档为：

<http://mybatis.org/spring/zh/>

## 六、常用接口查询语句

> `text、keyword、date、``long``、integer、``short``、``byte``、``double``、``float``、half_float、scaled_float、boolean、ip`

```
# 创建指定的分词插件的索引 put
curl --location --request PUT 'localhost:9200/fulltext' \
--header 'Content-Type: application/json' \
--data-raw '{
  "mappings": {
    "phone": {
      "properties": {
        "id": {
          "type": "integer"
        },
        "name": {
          "type": "text",
          "analyzer": "ik_max_word",
          "search_analyzer": "ik_max_word"
        },
        "colors": {
          "type": "text",
          "analyzer": "ik_max_word",
          "search_analyzer": "ik_max_word"
        },
        "selling_points": {
          "type": "text",
          "analyzer": "ik_max_word",
          "search_analyzer": "ik_max_word"
        }
      }
    }
  }
}'
```

