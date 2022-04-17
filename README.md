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
                // 初始化基础表，不需要的可以改为 DB_SCHEMA_UPDATE_FALSE
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
<table>
<tbody><tr><th>表分类</th>
<th>表名</th>
<th>表说明</th>
</tr><tr>
    <td rowspan="2"> 一般数据(2)</td>
    <td>ACT_GE_BYTEARRAY</td>
    <td>通用的流程定义和流程资源 </td>
</tr>
<tr>
    <td>ACT_GE_PROPERTY</td>
    <td>系统相关属性</td>
</tr>
<tr>
    <td rowspan="8">流程历史记录(8)</td>
    <td>ACT_HI_ACTINST</td>
     <td>历史的流程实例</td>
</tr>
<tr>
    <td>ACT_HI_ATTACHMENT</td>
    <td>历史的流程附件</td>
</tr>
<tr>
    <td>ACT_HI_COMMENT</td>
    <td>历史的说明性信息</td>
</tr>
<tr>
    <td>ACT_HI_DETAIL</td>
    <td>历史的流程运行中的细节信息</td>
</tr>
<tr>
    <td>ACT_HI_IDENTITYLINK</td>
    <td>历史的流程运行过程中用户关系
</td>
</tr>
<tr>
    <td>ACT_HI_PROCINST</td>
    <td>历史的流程实例</td>
</tr>
<tr>
    <td>ACT_HI_TASKINST</td>
    <td>历史的任务实例</td>
</tr>
<tr>
    <td>ACT_HI_VARINST</td>
    <td>历史的流程运行中的变量信息</td>
</tr>
<tr>
    <td rowspan="9">用户用户组表(9)</td>
    <td>ACT_ID_BYTEARRAY</td>
     <td>二进制数据表</td>
</tr>
<tr>
    <td>ACT_ID_GROUP</td>
    <td>用户组信息表</td>
</tr>
<tr>
    <td>ACT_ID_INFO</td>
    <td>用户信息详情表</td>
</tr>
<tr>
    <td>ACT_ID_MEMBERSHIP</td>
    <td>人与组关系表</td>
</tr>
<tr>
    <td>ACT_ID_PRIV</td>
    <td>权限表
</td>
</tr>
<tr>
    <td>ACT_ID_PRIV_MAPPING</td>
    <td>用户或组权限关系表</td>
</tr>
<tr>
    <td>ACT_ID_PROPERTY</td>
    <td>属性表</td>
</tr>
<tr>
    <td>ACT_ID_TOKEN</td>
    <td>系统登录日志表</td>
</tr>
<tr>
    <td>ACT_ID_USER</td>
    <td>用户表</td>
</tr>
<tr>
    <td rowspan="3">流程定义表(3)</td>
    <td>ACT_RE_DEPLOYMENT</td>
     <td>部署单元信息</td>
</tr>
<tr>
    <td>ACT_RE_MODEL</td>
    <td>模型信息</td>
</tr>
<tr>
    <td>ACT_RE_PROCDEF</td>
    <td>已部署的流程定义</td>
</tr>
<tr>
    <td rowspan="10">运行实例表(10)</td>
    <td>ACT_RU_DEADLETTER_JOB</td>
     <td>正在运行的任务表</td>
</tr>
<tr>
    <td>ACT_RU_EVENT_SUBSCR</td>
    <td>运行时事件</td>
</tr>
<tr>
    <td>ACT_RU_EXECUTION</td>
    <td>运行时流程执行实例</td>
</tr>
<tr>
    <td>ACT_RU_HISTORY_JOB</td>
    <td>历史作业表</td>
</tr>
<tr>
    <td>ACT_RU_IDENTITYLINK</td>
    <td>运行时用户关系信息
</td>
</tr>
<tr>
    <td>ACT_RU_JOB</td>
    <td>运行时作业表</td>
</tr>
<tr>
    <td>ACT_RU_SUSPENDED_JOB</td>
    <td>暂停作业表</td>
</tr>
<tr>
    <td>ACT_RU_TASK</td>
    <td>运行时任务表</td>
</tr>
<tr>
    <td>ACT_RU_TIMER_JOB</td>
    <td>定时作业表</td>
</tr>
<tr>
    <td>ACT_RU_VARIABLE</td>
    <td>运行时变量表</td>
</tr>
<tr>
    <td rowspan="2">其他表(2)</td>
    <td>ACT_EVT_LOG</td>
     <td>事件日志表</td>
</tr>
<tr>
    <td>ACT_PROCDEF_INFO</td>
    <td>流程定义信息</td>
</tr>
</tbody></table>

### 2.4 服务说明
RepositoryService 流程存储服务
> 管理流程定义文件xml及静态资源的服务
> 
> 对特定流程的暂停和激活
> 
> 流程定义启动权限管理
> 
> 类内部重要的成员有：
> 
> > deploymentBuilder 部署文件构造器
> >
> > deploymentQuery 部署文件查询器
> >
> > ProcessDefinitionQuery 流程定义文件查询对象
> >
> > Deployment 流程部署文件对象
> >
> > ProcessDefinition 流程定义文件对象
> >
> > BpmnModel 流程定义的java格式

RuntimeService 流程运行控制服务
> 启动流程及对流程数据的控制
> 
> 流程实例(ProcessInstance)与执行流(Execution)的查询
> 
> 触发流程操作,接收消息和信号

RuntimeService启动流程及变量管理
> 启动流程的常用方法(id,key,message)
> 
> 启动流程可选参数(businessKey,variables,tenantId)
> 
> 变量(variables)的设置和获取



TaskService
> 对用户任务UserTask的管理和流程的控制
> 
> 设置用户任务的权限信息（设置候选人等）
> 
> 针对用户任务添加任务附件，任务评论和事件记录
> 
 TaskService对Task的管理和控制
> 
> Task对象的创建和删除
> 
> 查询Task,驱动Task节点完成执行
> 
> Task相关参数变量variable设置

```java
// 查询task
Task task1 = taskService.createTaskQuery().singleResult();
 // 设置全局变量
taskService.setVariable(task1.getId(),"key1","value1");
// 设置局部变量
taskService.setVariableLocal(task1.getId(),"key2","value2");

// 获取全局变量
Map<String,Object> a = taskService.getVariables(task1.getId());
// 获取局部变量
Map<String,Object> b = taskService.getVariablesLocal(task1.getId());
// 获取全局变量
Map<String,Object> c = runtimeService.getVariables(processInstance.getId());
```
使用taskService设置局部变量后，局部变量的作用域只限于当前任务内，如果任务结束后，那么局部变量也就随着消失了。

除了直接设置变量外，在任务提交的时候也可以附带变量，即使用taskService.complete(),这个complete方法多个重载方法：

```java
public void complete(String taskId);
public void complete(String taskId, Map<String, Object> variables);
public void complete(String taskId, Map<String, Object> variables, boolean localScope) ;
```
其中：taskId(对应act_ru_task中的id_)，variables（下一次任务所需要的参数）,作用是完成这一次任务，并且下一步任务需要流程变量的。要注意的是localScope这个参数：localScope（存储范围：本任务） 。当这个布尔值为true表示作用范围为当前任务，当任务结束后，再也取不到这个值了，act_ru_variables这个表中也没有这个参数的信息了；如果为false表示这个变量是全局的，任务结束后在act_ru_variables表中仍然能查到变量信息。相关内容可以查看下面这篇文章：
https://blog.csdn.net/u013026207/article/details/53405265



### 2.5 入门案例
#### 2.5.1 部署流程文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:flowable="http://flowable.org/bpmn" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC" xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI" typeLanguage="http://www.w3.org/2001/XMLSchema" expressionLanguage="http://www.w3.org/1999/XPath" targetNamespace="http://www.flowable.org/processdef" exporter="Flowable Open Source Modeler" exporterVersion="6.7.0">
  <process id="leave01" name="leave01" isExecutable="true">
    <startEvent id="startEvent1" flowable:formFieldValidation="true" flowable:initiator="INITIATOR"></startEvent>
    <userTask id="sid-5EE822E8-2767-4E99-A4CA-315C039C0CCF" name="请假申请" flowable:assignee="${INITIATOR}" flowable:formFieldValidation="true">
      <extensionElements>
        <modeler:activiti-idm-initiator xmlns:modeler="http://flowable.org/modeler"><![CDATA[true]]></modeler:activiti-idm-initiator>
      </extensionElements>
    </userTask>
    <sequenceFlow id="sid-F011844D-3488-4034-9700-FA1A60EF4DD7" sourceRef="startEvent1" targetRef="sid-5EE822E8-2767-4E99-A4CA-315C039C0CCF"></sequenceFlow>
    <userTask id="sid-4717A9F9-4A71-4CA3-8664-75B9A6BC7CD2" name="主管审批" flowable:assignee="test" flowable:formFieldValidation="true">
      <extensionElements>
        <modeler:initiator-can-complete xmlns:modeler="http://flowable.org/modeler"><![CDATA[false]]></modeler:initiator-can-complete>
      </extensionElements>
    </userTask>
    <sequenceFlow id="sid-E37769EF-ADE0-46F7-A762-C04E4C3BE109" sourceRef="sid-5EE822E8-2767-4E99-A4CA-315C039C0CCF" targetRef="sid-4717A9F9-4A71-4CA3-8664-75B9A6BC7CD2"></sequenceFlow>
    <endEvent id="sid-A0F6FC66-EFD3-4895-834F-E2DCE0D8FA3D"></endEvent>
    <sequenceFlow id="sid-7F9119AF-B9A8-491E-BEA1-CC2D5F3C4EF1" sourceRef="sid-4717A9F9-4A71-4CA3-8664-75B9A6BC7CD2" targetRef="sid-A0F6FC66-EFD3-4895-834F-E2DCE0D8FA3D"></sequenceFlow>
  </process>
</definitions>

```
> 流程描述:
> 
>       开始 -> 请假申请 -> 主管审批 -> 结束
> 
> 请假开始：流程发起人
> 
> 主管审批：固定值 test


xml文件部署代码
```java
//加载流程
Deployment deployment=repositoryService.createDeployment()
        .addClasspathResource("流程文件名.bpmn20.xml")
        .name("流程名称")
        .deploy();
```

#### 2.5.2 查询流程部署
```java
//查询流程定义
List<Deployment> list = repositoryService.createDeploymentQuery().list();
for (Deployment deployment : list) {
    System.out.println("deployment ==> "+deployment);

}
```
#### 2.5.3 查询流程定义
```java
List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
for (ProcessDefinition processDefinition : list) {
    System.out.println("xxx "+ processDefinition.getId());
    System.out.println("processDefinition ==> "+processDefinition.getId()+" , "+processDefinition.getName()+"  key:"+ processDefinition.getKey());
}
```

#### 2.5.4 删除流程定义
```java
//删除流程
repositoryService.deleteDeployment("taskId",true);
```
#### 2.5.5 启动流程
```java
//设置流程发起人id 流程引擎会对应到变量：INITIATOR 本操作对应leave01中的流程
Authentication.setAuthenticatedUserId("userId");
//启动流程
ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave01");
System.out.println("提交成功.流程Id为：" + processInstance.getId());
```

#### 2.5.6 获取所有需要审批的任务
```java
//获取需要审批的任务
List<Task> tasks = taskService.createTaskQuery().list();
for (Task task : tasks) {
    System.out.println("task ==> "+task+" assignee : "+task.getAssignee());
}
```
#### 2.5.7 获取指定用户需要审批的任务
```java
//获取指定用户需要审批的任务
List<Task> tasks = taskService.createTaskQuery().taskAssignee("test").list();
for (Task task : tasks) {
    System.out.println("task ==> "+task+" assignee : "+task.getAssignee());
}
```
#### 2.5.8 完成任务
```java
//未携带参数
taskService.complete("taskId");
```

#### 2.5.9 任务添加评论
```java
Authentication.setAuthenticatedUserId("userId");
taskService.addComment("taskId","processInstanceId", "message");
```

#### 2.5.10 获取当前任务/实例评论
```java
//获取当前任务评论
List<Comment> taskComments = taskService.getTaskComments("taskId");
//获取当前实例评论
List<Comment> taskComments = taskService.getProcessInstanceComments("processInstanceId");
for (Comment taskComment : taskComments) {
    System.out.println(taskComment.getFullMessage());
}
```

#### 2.5.10 添加附件
```java
// 添加附件(地址位于/url/test.png)到task中
taskService.createAttachment("url","taskId",
        "processInstanceId",
        "name",
        "desc",
        "/url/test.png");
```

#### 2.5.11 查询当前任务/审批流程附件
```java
// 查询当前任务附件

List<Attachment> attachmentList = taskService.getTaskAttachments("taskId");
// 查询当前审批流程附件
List<Attachment> attachmentList = taskService.getProcessInstanceAttachments("processInstanceId");
for(Attachment attachment : attachmentList){
    System.out.println("attach="+attachment.getDescription()+","+attachment.getUrl());
}
```

#### 2.5.12 查询实例历史任务
```java
List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
            //按结束时间正向排序
            .orderByHistoricTaskInstanceEndTime().asc()
            .processInstanceId("52501").list();
    for (HistoricTaskInstance historicTaskInstance : list) {
        System.out.println("task "+historicTaskInstance.getName()+" == "+historicTaskInstance.getAssignee());
    }
```

#### 2.5.13 查询用户历史任务
```java
List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
        //按结束时间正向排序
        .orderByHistoricTaskInstanceEndTime().asc()
        .taskAssignee("admin").list();
for (HistoricTaskInstance historicTaskInstance : list) {
    System.out.println("task "+historicTaskInstance.getName()+" == "+historicTaskInstance.getAssignee());
}
```

#### 2.5.14 任务动态参数
部署leave02流程图 操作与上述一致
```xml
<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:flowable="http://flowable.org/bpmn" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC" xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI" typeLanguage="http://www.w3.org/2001/XMLSchema" expressionLanguage="http://www.w3.org/1999/XPath" targetNamespace="http://www.flowable.org/processdef" exporter="Flowable Open Source Modeler" exporterVersion="6.7.0">
  <process id="leave02" name="leave01" isExecutable="true">
    <startEvent id="startEvent1" flowable:formFieldValidation="true" flowable:initiator="INITIATOR"></startEvent>
    <userTask id="sid-5EE822E8-2767-4E99-A4CA-315C039C0CCF" name="请假申请" flowable:assignee="${INITIATOR}" flowable:formFieldValidation="true">
      <extensionElements>
        <modeler:activiti-idm-initiator xmlns:modeler="http://flowable.org/modeler"><![CDATA[true]]></modeler:activiti-idm-initiator>
      </extensionElements>
    </userTask>
    <sequenceFlow id="sid-F011844D-3488-4034-9700-FA1A60EF4DD7" sourceRef="startEvent1" targetRef="sid-5EE822E8-2767-4E99-A4CA-315C039C0CCF"></sequenceFlow>
    <userTask id="sid-4717A9F9-4A71-4CA3-8664-75B9A6BC7CD2" name="主管审批" flowable:assignee="${assigner}" flowable:formFieldValidation="true">
      <extensionElements>
        <modeler:initiator-can-complete xmlns:modeler="http://flowable.org/modeler"><![CDATA[false]]></modeler:initiator-can-complete>
      </extensionElements>
    </userTask>
    <sequenceFlow id="sid-E37769EF-ADE0-46F7-A762-C04E4C3BE109" sourceRef="sid-5EE822E8-2767-4E99-A4CA-315C039C0CCF" targetRef="sid-4717A9F9-4A71-4CA3-8664-75B9A6BC7CD2"></sequenceFlow>
    <endEvent id="sid-A0F6FC66-EFD3-4895-834F-E2DCE0D8FA3D"></endEvent>
    <sequenceFlow id="sid-7F9119AF-B9A8-491E-BEA1-CC2D5F3C4EF1" sourceRef="sid-4717A9F9-4A71-4CA3-8664-75B9A6BC7CD2" targetRef="sid-A0F6FC66-EFD3-4895-834F-E2DCE0D8FA3D"></sequenceFlow>
  </process>
</definitions>
```
> 主管审批为动态，其他部分与leave01相同

1、启动时指定
```java
//设置流程发起人id 流程引擎会对应到变量：INITIATOR 本操作对应leave01中的流程
Authentication.setAuthenticatedUserId("userId");
//设置参数
Map<String, Object> param = new HashMap<>();
param.put("assigner","root");
//启动流程
ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave02",param);
System.out.println("提交成功.流程Id为：" + processInstance.getId());
```
2. 检查参数是否设置到位
```java
Map<String, Object> variables = taskService.getVariables("currentTaskId");
System.out.println(variables);
```
ps：完成第一个任务，查看是否节点任务到了指定用户执行

3.完成任务时指定

> 无参数方式启动实例

带参数完成任务
```java
Map<String,Object> param = new HashMap<>();
param.put("assigner","root");
taskService.complete("taskId",param);
```
ps:同样查看当前所有任务中是否符合预期:主管审批节点分配人多一个root的记录

#### 2.5.15 动态表单
部署leave03流程图 操作与上述一致
```xml
<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:flowable="http://flowable.org/bpmn" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC" xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI" typeLanguage="http://www.w3.org/2001/XMLSchema" expressionLanguage="http://www.w3.org/1999/XPath" targetNamespace="http://www.flowable.org/processdef" exporter="Flowable Open Source Modeler" exporterVersion="6.7.0">
  <process id="leave03" name="leave03" isExecutable="true">
    <startEvent id="startEvent1" flowable:formFieldValidation="true" flowable:initiator="INITIATOR">
      <extensionElements>
        <flowable:formProperty id="days" name="请假天数" type="string"/>
        <flowable:formProperty id="info" name="请假理由" type="string"/>
      </extensionElements>
    </startEvent>
    <userTask id="sid-267F24AC-E90E-4ECF-A5FE-A50434288631" name="发起人动态表单" flowable:assignee="$INITIATOR" flowable:formFieldValidation="true">
      <extensionElements>
        <modeler:activiti-idm-initiator xmlns:modeler="http://flowable.org/modeler">
          <![CDATA[ true ]]>
        </modeler:activiti-idm-initiator>
      </extensionElements>
    </userTask>
    <sequenceFlow id="sid-1946B11C-014B-4594-801E-6034F7795D85" sourceRef="startEvent1" targetRef="sid-267F24AC-E90E-4ECF-A5FE-A50434288631"/>
    <userTask id="sid-458E459B-8E59-4F94-AAF1-03106BBEA35B" name="主管审批" flowable:assignee="test" flowable:formFieldValidation="true">
      <extensionElements>
        <modeler:initiator-can-complete xmlns:modeler="http://flowable.org/modeler">
          <![CDATA[ false ]]>
        </modeler:initiator-can-complete>
      </extensionElements>
    </userTask>
    <sequenceFlow id="sid-25FF4EB5-19C1-4335-B4FB-4E41F6F3D84A" sourceRef="sid-267F24AC-E90E-4ECF-A5FE-A50434288631" targetRef="sid-458E459B-8E59-4F94-AAF1-03106BBEA35B"/>
    <endEvent id="sid-C0CC4AE5-62C3-45B3-83AC-62D257C46294"/>
    <sequenceFlow id="sid-369046FA-FAA7-40CF-AB7A-D4660999C853" sourceRef="sid-458E459B-8E59-4F94-AAF1-03106BBEA35B" targetRef="sid-C0CC4AE5-62C3-45B3-83AC-62D257C46294"/>
  </process>
</definitions>
```

> 表单为启动时需要的表单，也可以在某一个节点设置表单，提交时按表单需求提交

1.查询实例需要的表单
```java
StartFormData startFormData = formService.getStartFormData("leave03:1:87504");
for (FormProperty formProperty : startFormData.getFormProperties()) {
    System.out.println("id = "+formProperty.getId()+",label ="+formProperty.getName());
}
```
2.启动实例
#### 安装教程

1.  


#### 使用说明

1.  

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request

 
