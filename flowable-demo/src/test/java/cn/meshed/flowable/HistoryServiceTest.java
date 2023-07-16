package cn.meshed.flowable;

import com.alibaba.fastjson.JSON;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * <h1>流程历史</h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2021/10/09
 */
@SpringBootTest
public class HistoryServiceTest {

    @Autowired
    private HistoryService historyService;

    /**
     * 历史流程实例查询
     */
    @Test
    public void historicProcessInstanceQuery(){
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().processInstanceId("b3fbd774-c2dd-11ec-bcbc-004238a4ec73").list();
        for (HistoricProcessInstance historicProcessInstance : list) {
            System.out.println(historicProcessInstance.getProcessDefinitionName());
            System.out.println(JSON.toJSONString(historicProcessInstance));
        }
    }

    /**
     * 查询实例历史任务
     */
    @Test
    public void createHistoricTaskInstanceQuery(){
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                //按结束时间正向排序
                .orderByHistoricTaskInstanceEndTime().asc()
                .processInstanceId("b3fbd774-c2dd-11ec-bcbc-004238a4ec73").list();
        for (HistoricTaskInstance historicTaskInstance : list) {
            System.out.println("task "+historicTaskInstance.getName()+" == "+historicTaskInstance.getAssignee());
        }
    }

    /**
     * 查询用户历史任务
     */
    @Test
    public void createHistoricTaskInstanceQueryByTaskAssignee(){
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                //按结束时间正向排序
                .orderByHistoricTaskInstanceEndTime().asc()
                .taskAssignee("user").list();
        for (HistoricTaskInstance historicTaskInstance : list) {

            System.out.println("task "+historicTaskInstance.getName()+" == "+historicTaskInstance.getAssignee());
        }


    }
}
