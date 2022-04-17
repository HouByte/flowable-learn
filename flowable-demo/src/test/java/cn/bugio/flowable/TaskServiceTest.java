package cn.bugio.flowable;

import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.TaskService;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.task.Attachment;
import org.flowable.engine.task.Comment;
import org.flowable.form.api.FormInfo;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1>任务操作API</h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2021/10/09
 */
@SpringBootTest
public class TaskServiceTest {

    @Autowired
    private TaskService taskService;


    @Test
    public void searchTaskAll(){
        System.out.println("===== task ====");
        //获取需要审批的任务
        List<Task> tasks = taskService.createTaskQuery().list();
        for (Task task : tasks) {
            System.out.println("task ==> "+task+" ProcessInstanceId = "+task.getProcessInstanceId()+" assignee : "+task.getAssignee());
        }

    }
    @Test
    public void searchTaskByAssignee(){
        System.out.println("===== task ====");
        //获取指定用户需要审批的任务
        List<Task> tasks = taskService.createTaskQuery().taskAssignee("user").list();
        for (Task task : tasks) {
            System.out.println("task ==> "+task+" assignee : "+task.getAssignee());
        }
    }

    /**
     * 无参数完成任务
     */
    @Test
    public void completeTask(){
        System.out.println("===== task ====");
        taskService.complete("47509");

    }

    /**
     * 有参数完成任务
     */
    @Test
    public void completeTaskParam(){
        System.out.println("===== task ====");
        Map<String,Object> param = new HashMap<>();
        param.put("assigner","root");
        taskService.complete("82508",param);

    }

    @Test
    public void addComment(){
        Authentication.setAuthenticatedUserId("admin");
        taskService.addComment("62503","52501","record note 2");

    }

    @Test
    public void getComment(){
        System.out.println("=========comment==============");
        List<Comment> taskComments = taskService.getTaskComments("62503");
        for (Comment taskComment : taskComments) {
            System.out.println(taskComment.getFullMessage());
            System.out.println(taskComment.getUserId());
        }
    }
    @Test
    public void getProcessInstanceComment(){
        System.out.println("=========ProcessInstance Comment==============");
        List<Comment> taskComments = taskService.getProcessInstanceComments("52501");
        for (Comment taskComment : taskComments) {
            System.out.println(taskComment.getFullMessage());
            System.out.println(taskComment.getUserId());
        }
    }

    @Test
    public void createAttachment(){
        // 添加附件(地址位于/url/test.png)到task中
        taskService.createAttachment("url","52508",
                "52501",
                "name",
                "desc",
                "/url/test.png");


    }
    @Test
    public void getAttachment(){
        // 查询附件
        List<Attachment> attachmentList = taskService.getTaskAttachments("62503");
        for(Attachment attachment : attachmentList){
            System.out.println("attach="+attachment.getDescription()+","+attachment.getUrl());
        }
    }

    @Test
    public void getAttachmentByProcessInstanceId(){
        // 查询附件
        List<Attachment> attachmentList = taskService.getProcessInstanceAttachments("52501");
        for(Attachment attachment : attachmentList){
            System.out.println("attach="+attachment.getDescription()+","+attachment.getUrl());
        }

    }

    @Test
    public void getVariables(){
        Map<String, Object> variables = taskService.getVariables("72509");
        System.out.println(variables);
    }



}
