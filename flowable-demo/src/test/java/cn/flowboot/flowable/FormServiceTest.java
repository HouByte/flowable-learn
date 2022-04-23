package cn.flowboot.flowable;

import org.flowable.engine.FormService;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.StartFormData;
import org.flowable.engine.form.TaskFormData;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.form.api.FormDeployment;
import org.flowable.form.api.FormRepositoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1>表单</h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2021/10/09
 */
@SpringBootTest
public class FormServiceTest {

    @Autowired
    private FormService formService;

    @Autowired
    private FormRepositoryService formRepositoryService;

    @Test
    public void deployForm(){
        formRepositoryService.createDeployment()
                .addClasspathResource("form/form2.form")
                .name("外置表单").parentDeploymentId("52504")
                .deploy();
    }

    @Test
    public void getForm(){
        List<FormDeployment> list = formRepositoryService.createDeploymentQuery().list();
        for (FormDeployment formDeployment : list) {
            System.out.println(formDeployment.getName());

        }
    }


    @Test
    public void getStartFormData(){
        StartFormData startFormData = formService.getStartFormData("oa_leave_2:1:52504");
        for (FormProperty formProperty : startFormData.getFormProperties()) {
            System.out.println("id = "+formProperty.getId()+",label ="+formProperty.getName()+",value ="+formProperty.getValue());
        }
    }

    @Test
    public void getStartFormKey(){
        String formKey = formService.getStartFormKey("oa_leave_2:1:47bf424d-bda6-11ec-9715-004238a4ec73");
        System.out.println(formKey);
    }

    @Test
    public void submitStartFormData(){
        // 提交开始节点的表单
        Map<String,String> properties = new HashMap<String,String>();
        properties.put("days","10");
        properties.put("info","病假");
       formService.submitTaskFormData("115007",properties);

    }

    @Test
    public void  getFormData(){

        TaskFormData taskFormData = formService.getTaskFormData("135003");
        for (FormProperty formProperty : taskFormData.getFormProperties()) {
            System.out.println("id = "+formProperty.getId()+",label ="+formProperty.getName()+",value ="+formProperty.getValue());
        }

    }
}
