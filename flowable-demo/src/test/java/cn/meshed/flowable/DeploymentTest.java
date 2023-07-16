package cn.meshed.flowable;

import com.alibaba.druid.util.Base64;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>流程部署</h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2021/10/09
 */
@SpringBootTest
public class DeploymentTest {


    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ProcessEngine processEngine;

    @Test
    public void testProcessEngine(){
        RepositoryService repository = processEngine.getRepositoryService();
        System.out.println(repository.toString());
    }
    /**
     * 通过文件部署
     */
    @Test
    public void deploymentByFile(){
        //加载流程
        Deployment deployment=repositoryService.createDeployment()
                .addClasspathResource("bpmn/countersign.bpmn20.xml")
                .name("会签")
                .deploy();

    }

    /**
     * 通过文件部署
     */
    @Test
    public void deploymentByXML(){
        //部署流程
        String xml = "holiday-request.bpmn20.xml 内容，此处略";
        Deployment deployment=repositoryService.createDeployment()
                .addString("holiday-request.bpmn20.xml",xml)
                .name("请假流程ByXml")
                .deploy();

    }

    /**
     * 通过BpmnModel部署
     */
    @Test
    public void deploymentByBpmnModel(){
        //BpmnModel构建
        BpmnModel bpmnModel = new BpmnModel();
        StartEvent start = new StartEvent();
        start.setId("start");
        start.setName("开始节点");

        SequenceFlow flow1 = new SequenceFlow();
        flow1.setId("flow1");
        flow1.setName("开始节点-->请假申请");
        flow1.setSourceRef("start");
        flow1.setTargetRef("userTask");

        UserTask userTask = new UserTask();
        userTask.setId("userTask");
        userTask.setName("请假申请");

        //请假申请-->网关处理
        SequenceFlow flow2 = new SequenceFlow();
        flow2.setId("flow2");
        flow2.setName("请假申请-->网关处理");
        flow2.setSourceRef("userTask");
        flow2.setTargetRef("exclusiveGateway");
        //拍它
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setId("exclusiveGateway");


        SequenceFlow flow3 = new SequenceFlow();
        flow3.setId("flow3");
        flow3.setName("请假大于 3 天");
        flow3.setSourceRef("exclusiveGateway");
        flow3.setTargetRef("hrTask");
        flow3.setConditionExpression("${day>3}");

        UserTask hrTask = new UserTask();
        hrTask.setId("hrTask");
        hrTask.setName("HR 审批");

        SequenceFlow flow4 = new SequenceFlow();
        flow4.setId("flow4");
        flow4.setName("HR 审批--> 结束");
        flow4.setSourceRef("hrTask");
        flow4.setTargetRef("endEvent");


        SequenceFlow flow5 = new SequenceFlow();
        flow5.setId("flow5");
        flow5.setName("请假小于等于 1 天");
        flow5.setSourceRef("exclusiveGateway");
        flow5.setTargetRef("endEvent");
        flow5.setConditionExpression("${day<=1}");

        EndEvent end = new EndEvent();
        end.setId("endEvent");
        end.setName("结束节点");

        Process process=new Process();
        process.setId("process1");
        process.setName("process1");

        process.addFlowElement(start);
        process.addFlowElement(flow1);
        process.addFlowElement(userTask);
        process.addFlowElement(flow2);
        process.addFlowElement(exclusiveGateway);
        process.addFlowElement(flow3);
        process.addFlowElement(hrTask);
        process.addFlowElement(flow4);
        process.addFlowElement(flow5);
        process.addFlowElement(end);

        bpmnModel.addProcess(process);

        //部署流程
        Deployment deployment=repositoryService.createDeployment()
                .addBpmnModel("holiday-request-3.bpmn20.xml",bpmnModel)
                .name("请假流程-BpmnModel")
                .deploy();

    }

    /**
     * 通过ID删除
     */
    @Test
    public void delDeployment(){
        //删除流程
        repositoryService.deleteDeployment("122501",true);

    }

    /**
     * 通过ID删除
     */
    @Test
    public void delDeploymentAll(){
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        for (Deployment deployment : list) {
            //删除流程
            repositoryService.deleteDeployment(deployment.getId(),true);
        }


    }
    /**
     * 查询已经部署的流程
     */
    @Test
    public void searchDeployment(){
        System.out.println("===================");
        //查询流程定义
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        for (Deployment deployment : list) {
            System.out.println("deployment ==> "+deployment);
        }
    }

    /**
     * 查询已经部署的流程定义
     */
    @Test
    public void searchProcessDefinitionList(){
        System.out.println("===================");
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        for (ProcessDefinition processDefinition : list) {
            System.out.println("processDefinition ==> "+processDefinition.getId()+" , "+processDefinition.getName()+"  key:"+ processDefinition.getKey());
        }
    }


    /**
     * 读取xml
     *
     * @param deployId
     * @return
     */
    public InputStream readImage(String deployId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployId).singleResult();

        //获得图片流
        DefaultProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());

        //自动布局
        BpmnAutoLayout bpmnAutoLayout = new BpmnAutoLayout(bpmnModel);
        bpmnAutoLayout.setTaskHeight(120);
        bpmnAutoLayout.setTaskWidth(120);
        bpmnAutoLayout.execute();
        //输出为图片
        return diagramGenerator.generateDiagram(
                bpmnModel,
                "png",
                Collections.emptyList(),
                Collections.emptyList(),
                "宋体",
                "宋体",
                "宋体",
                null,
                1.0,
                false);

    }

    /**
     * 将InputStream写入本地文件
     * @param destination 写入本地目录
     * @param input 输入流
     * @throws IOException IOException
     */
    public static void writeToLocal(String destination, InputStream input)
            throws IOException {
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream downloadFile = new FileOutputStream(destination);
        while ((index = input.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
            downloadFile.flush();
        }
        input.close();
        downloadFile.close();

    }
    @SneakyThrows
    @Test
    public void readImageFileTest(){
        InputStream inputStream = readImage("130001");
        writeToLocal("x.png",inputStream);
    }


    @SneakyThrows
    @Test
    public void readImageBase64Test(){
        InputStream inputStream = readImage("5001");
        String base64FromInputStream = getBase64FromInputStream(inputStream);
        System.out.println("data:image/xxx;base64,"+base64FromInputStream);
    }
    /**
     * 将inputstream转为Base64
     *
     * @param is
     * @return
     * @throws Exception
     */
    private String getBase64FromInputStream(InputStream is) throws Exception {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;

        // 读取图片字节数组
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = is.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new Exception("输入流关闭异常");
                }
            }
        }

        return Base64.byteArrayToBase64(data);
    }


    public InputStream readXml(String deployId){
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployId).singleResult();
        return repositoryService.getResourceAsStream(deployId,processDefinition.getResourceName());
    }

    @SneakyThrows
    @Test
    public void readXmlTest(){
        InputStream inputStream = readXml("5001");
        String result = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining(System.lineSeparator()));
        System.out.println(result);
    }

}
