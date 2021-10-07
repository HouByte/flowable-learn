# Flowable 学习笔记


## 一、Flowable简介
## 二、快速入门
### 2.1、pom依赖
#### 2.1.1 flowable 核心包
```html
<dependency>
    <groupId>org.flowable</groupId>
    <artifactId>flowable-spring-boot-starter-process</artifactId>
    <version>6.7.0</version>
</dependency>
```
#### 2.1.2 Flowable 内部日志采用 SLF4J
```html
<!-- Flowable 内部日志采用 SLF4J -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.21</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
    <version>1.7.21</version>
</dependency>
```
#### 2.1.3 数据库
```html
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
```
### 2.2、项目配置
#### 2.2.1 yaml 配置
```yaml
spring:
  application:
    name: flowboot
  datasource:
    #根据实际配置
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/flowable_demo?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: root



# flowable 配置
flowable:
  # 关闭异步，不关闭历史数据的插入就是异步的，会在同一个事物里面，无法回滚
  # 开发可开启会提高些效率，上线需要关闭
  async-executor-activate: false
  #开启更新表格,上线需要关闭
  database-schema-update: true 
```
#### 2.2.2 日志配置
```properties
log4j.rootLogger=DEBUG, CA
log4j.appender.CA=org.apache.log4j.ConsoleAppender
log4j.appender.CA.layout=org.apache.log4j.PatternLayout
log4j.appender.CA.layout.ConversionPattern= %d{hh:mm:ss,SSS} [%t] %-5p %c %x - %m%n
```

#### 2.2.3 数据库表生成器
```java
public class TableGeneration{
    public static void main(String[] args) {
        //1、创建ProcessEngineConfiguration实例,该实例可以配置与调整流程引擎的设置
        ProcessEngineConfiguration cfg=new StandaloneProcessEngineConfiguration()
                //2、通常采用xml配置文件创建ProcessEngineConfiguration，这里直接采用代码的方式
                //3、配置数据库相关参数
                .setJdbcUrl("jdbc:mysql://localhost:3306/flowable_demo?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2b8&nullCatalogMeansCurrent=true")
                .setJdbcUsername("root")
                .setJdbcPassword("root")
                .setJdbcDriver("com.mysql.jdbc.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        //4、初始化ProcessEngine流程引擎实例
        ProcessEngine processEngine=cfg.buildProcessEngine();
    }
}
```
#### 2.2.4 SpringBoot 项目配置
Spring启动时如果数据库不存在数据表进行创建,MySql数据库引擎建议InnoDB防止由于索引太长报错
```java
/**
 * <h1>流程引擎配置文件</h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2021/10/07
 */

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


@RequiredArgsConstructor
@Configuration
@Data
@Slf4j
public class ProcessEngineConfig {


    @Autowired
    private DataSource dataSource;

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(){
        SpringProcessEngineConfiguration springProcessEngineConfiguration = new SpringProcessEngineConfiguration();
        springProcessEngineConfiguration.setDataSource(dataSource);
        springProcessEngineConfiguration.setDatabaseSchemaUpdate("true");
        springProcessEngineConfiguration.setTransactionManager(dataSourceTransactionManager(dataSource));
        return springProcessEngineConfiguration;
    }


}
```

### 2.3、数据表说明
flowable命名规则:

> ACT_RE_* ：’ RE ’表示repository（存储）。RepositoryService接口操作的表。带此前缀的表包含的是静态信息，如，流程定义，流程的资源（图片，规则等）。
> 
> ACT_RU_* ：’ RU ’表示runtime。这是运行时的表存储着流程变量，用户任务，变量，职责（job）等运行时的数据。flowable只存储实例执行期间的运行时数据，当流程实例结束时，将删除这些记录。这就保证了这些运行时的表小且快。
> 
> ACT_ID_*  : ’ ID  ’表示identity(组织机构)。这些表包含标识的信息，如用户，用户组，等等。
> 
> ACT_HI_* :  ’ HI ’表示history。就是这些表包含着历史的相关数据，如结束的流程实例，变量，任务，等等。
> 
> ACT_GE_*  : 普通数据，各种情况都使用的数据。

34张表说明：

|  表分类   | 表名  |表说明|
|  ----  | ----  |---|
| 一般数据(2) | ACT_GE_BYTEARRAY | 通用的流程定义和流程资源  |
| ACT_GE_PROPERTY | 系统相关属性  |
| 流程历史记录(8) | ACT_HI_ACTINST | 历史的流程实例  |
| ACT_HI_ATTACHMENT | 历史的流程附件  |
| ACT_HI_COMMENT | 历史的说明性信息  |
| ACT_HI_DETAIL | 历史的流程运行中的细节信息  |
| ACT_HI_IDENTITYLINK | 历史的流程运行过程中用户关系  |
| ACT_HI_PROCINST | 历史的流程实例  |
| ACT_HI_TASKINST | 历史的任务实例  |
| ACT_HI_VARINST | 历史的流程运行中的变量信息  |
| 用户用户组表(9) | ACT_ID_BYTEARRAY | 二进制数据表  |
| ACT_ID_GROUP | 用户组信息表  |
| ACT_ID_INFO | 用户信息详情表  |
| ACT_ID_MEMBERSHIP | 人与组关系表  |
| ACT_ID_PRIV | 权限表  |
| ACT_ID_PRIV_MAPPING | 用户或组权限关系表  |
| ACT_ID_PROPERTY | 属性表  |
| ACT_ID_TOKEN | 系统登录日志表  |
| ACT_ID_USER | 用户表  |
| 流程定义表(3) | ACT_RE_DEPLOYMENT | 部署单元信息  |
| ACT_RE_MODEL | 模型信息  |
| ACT_RE_PROCDEF | 已部署的流程定义  |
| 运行实例表(10) | ACT_RU_DEADLETTER_JOB | 正在运行的任务表  |
| ACT_RU_EVENT_SUBSCR | 运行时事件  |
| ACT_RU_EXECUTION | 运行时流程执行实例  |
| ACT_RU_HISTORY_JOB | 历史作业表  |
| ACT_RU_IDENTITYLINK | 运行时用户关系信息  |
| ACT_RU_JOB | 运行时作业表  |
| ACT_RU_SUSPENDED_JOB | 暂停作业表  |
| ACT_RU_TASK | 运行时任务表  |
| ACT_RU_TIMER_JOB | 定时作业表  |
| ACT_RU_VARIABLE | 运行时变量表  |
| 其他表(2) | ACT_EVT_LOG | 事件日志表  |
| ACT_PROCDEF_INFO | 流程定义信息 |



#### 安装教程

1.  


#### 使用说明

1.  

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request

 
