package cn.flowboot.flowable;

import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.*;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.StartFormData;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.form.api.FormInfo;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1></h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2021/10/08
 */
@SpringBootTest
public class BasicsTest01 {


    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private FormService formService;


    @Test
    public void runtimeProcess(){
        //启动流程
        Authentication.setAuthenticatedUserId("admin");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave01");
        System.out.println("提交成功.流程Id为：" + processInstance.getId()+","+processInstance.getProcessDefinitionId());
//        FormInfo startFormModel = runtimeService.getStartFormModel(processInstance.getProcessDefinitionId(), processInstance.getProcessInstanceId());
//
//        System.out.println(startFormModel);
        //taskService.complete(processInstance.getId());
    }


    @Test
    public void searchTaskAll(){
        System.out.println("===== task ====");
        //获取需要审批的任务

        List<Task> tasks = taskService.createTaskQuery().list();

        for (Task task : tasks) {
            System.out.println("task ==> "+task+" assignee : "+task.getAssignee());
            List<FormProperty> formProperties = formService.getTaskFormData(task.getId()).getFormProperties();

            for (FormProperty formProperty : formProperties) {
                System.out.println(formProperty.getName());
            }

        }

    }
    @Test
    public void searchTaskByAssignee(){
        System.out.println("===== task ====");
        //获取需要审批的任务

        List<Task> tasks = taskService.createTaskQuery().taskAssignee("test").list();
        for (Task task : tasks) {
            System.out.println("task ==> "+task+" assignee : "+task.getAssignee());
        }
    }
    @Test
    public void completeTask(){
        System.out.println("===== task ====");
        //获取需要审批的任务

        taskService.complete("15008");

    }


    @Test
    public void saveForm(){
        Map<String,String> map = new HashMap<>();
        map.put("app","ccc");
        map.put("apk","acc");
        formService.saveFormData("15008",map);
    }

    @Test
    public void searchForm(){
        StartFormData startFormData = formService.getStartFormData("leave03:1:47504");
        System.out.println("===============" + startFormData.getFormKey() + ","+startFormData.getFormProperties());
        for (FormProperty formProperty : startFormData.getFormProperties()) {
            System.out.println(formProperty.getName()+","+formProperty.getId());
        }
    }


}
