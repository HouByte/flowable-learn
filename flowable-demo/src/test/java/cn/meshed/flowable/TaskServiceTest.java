package cn.meshed.flowable;

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


    /**
     * 查询所有任务
     */
    @Test
    public void searchTaskAll(){
        System.out.println("===== task ====");
        //获取需要审批的任务
        List<Task> tasks = taskService.createTaskQuery().processInstanceBusinessKey("XXX").list();
        for (Task task : tasks) {
            System.out.println("task ==> "+task+" ProcessInstanceId = "+task.getProcessInstanceId()+" assignee : "+task.getAssignee());
        }

    }

    /**
     * 获取指定用户需要审批的任务
     */
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
        taskService.complete("9d159d68-c2d3-11ec-89c5-004238a4ec73");

    }

    /**
     * 有参数完成任务
     */
    @Test
    public void completeTaskParam(){
        System.out.println("===== task ====");
        Map<String,Object> param = new HashMap<>();
        param.put("day","30");
        taskService.complete("b400e08b-c2dd-11ec-bcbc-004238a4ec73",param);

    }

    /**
     * 添加评论
     */
    @Test
    public void addComment(){
        Authentication.setAuthenticatedUserId("admin");
        taskService.addComment("509a6056-c2e0-11ec-bfe1-004238a4ec73","50955741-c2e0-11ec-bfe1-004238a4ec73","record note 1");

    }

    /**
     * 查询任务评论
     */
    @Test
    public void getComment(){
        System.out.println("=========comment==============");
        List<Comment> taskComments = taskService.getTaskComments("655e1887-c2d9-11ec-b0dd-004238a4ec73");
        for (Comment taskComment : taskComments) {
            System.out.println(taskComment.getFullMessage());
            System.out.println(taskComment.getUserId());
        }
    }

    /**
     * 查询实例评论
     */
    @Test
    public void getProcessInstanceComment(){
        System.out.println("=========ProcessInstance Comment==============");
        List<Comment> taskComments = taskService.getProcessInstanceComments("50955741-c2e0-11ec-bfe1-004238a4ec73");
        for (Comment taskComment : taskComments) {
            System.out.println(taskComment.getTaskId());
            System.out.println(taskComment.getType());
            System.out.println(taskComment.getTime());
            System.out.println(taskComment.getFullMessage());
            System.out.println(taskComment.getUserId());
        }
    }

    /**
     * 创建附件
     */
    @Test
    public void createAttachment(){
        // 添加附件(地址位于/url/test.png)到task中
        taskService.createAttachment("file","655e1887-c2d9-11ec-b0dd-004238a4ec73",
                "fe7a3640-c2d4-11ec-a222-004238a4ec73",
                "name",
                "desc",
                "/url/test.png");



    }

    /**
     * 根据任务ID获取附件
     */
    @Test
    public void getAttachment(){
        // 查询附件
        List<Attachment> attachmentList = taskService.getTaskAttachments("655e1887-c2d9-11ec-b0dd-004238a4ec73");
        for(Attachment attachment : attachmentList){
            System.out.println("type="+attachment.getType()+",description="+attachment.getDescription()+",url"+attachment.getUrl());
        }
    }

    /**
     * 根据流程实例获取ID
     */
    @Test
    public void getAttachmentByProcessInstanceId(){
        // 查询附件
        List<Attachment> attachmentList = taskService.getProcessInstanceAttachments("52501");
        for(Attachment attachment : attachmentList){
            System.out.println("attach="+attachment.getDescription()+","+attachment.getUrl());
        }

    }

    /**
     * 获取参数数据
     */
    @Test
    public void getVariables(){
        Map<String, Object> variables = taskService.getVariables("655e1887-c2d9-11ec-b0dd-004238a4ec73");
        System.out.println(variables);
    }



}
