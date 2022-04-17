package cn.bugio.flowable;

import com.alibaba.fastjson.JSON;
import liquibase.pro.packaged.O;
import liquibase.pro.packaged.S;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.form.api.FormInfo;
import org.flowable.form.api.FormModel;
import org.flowable.form.model.FormField;
import org.flowable.form.model.SimpleFormModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1>启动操作</h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2021/10/09
 */
@SpringBootTest
public class RuntimeServiceTest {

    @Autowired
    private RuntimeService runtimeService;


    /**
     * 最基础启动一个流程
     */
    @Test
    public void startProcess(){
        //启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oa_leave");
        System.out.println("提交成功.流程Id为：" + processInstance.getId());
    }




    @Test
    public void startProcessAddParameter(){
        //设置参数
        Map<String, Object> param = new HashMap<>();
        param.put("day","10");
        //启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oa_leave",param);
        System.out.println("提交成功.流程Id为：" + processInstance.getId());
    }


    /**
     * 最基础启动一个流程
     */
    @Test
    public void startProcessByInitiatorAndParameter(){
        //设置流程发起人id 流程引擎会对应到变量：INITIATOR
        Authentication.setAuthenticatedUserId("user");
        //设置参数
        Map<String, Object> param = new HashMap<>();
        param.put("day","10");
        //启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oa_leave_2",param);
        //这个方法最终使用一个ThreadLocal类型的变量进行存储，也就是与当前的线程绑定，所以流程实例启动完毕之后，需要设置为null，防止多线程的时候出问题。
        Authentication.setAuthenticatedUserId(null);
        System.out.println("提交成功.流程Id为：" + processInstance.getId());
        System.out.println("提交成功.流程实例Id为：" + processInstance.getProcessInstanceId());

    }


    @Test
    public void getStartFormModel(){
        FormInfo formInfo = runtimeService.getStartFormModel("leave03:1:130004","132501");
        SimpleFormModel formModel = (SimpleFormModel)formInfo.getFormModel();
        for (FormField field : formModel.getFields()) {
            System.out.println(field);
        }
    }

    @Test
    public void list(){
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
        for (ProcessInstance processInstance : list) {
            System.out.println(processInstance.getId()+","+ processInstance.isSuspended());
        }
    }
    @Test
    public void activateOrsuspend(){
        int state = 1;
        // 激活
        if (state == 1) {
            runtimeService.activateProcessInstanceById("132501");
        }
        // 挂起
        if (state == 2) {
            runtimeService.suspendProcessInstanceById("132501");
        }
    }
}
