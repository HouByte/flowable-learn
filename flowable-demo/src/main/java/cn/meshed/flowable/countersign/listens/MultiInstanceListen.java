package cn.meshed.flowable.countersign.listens;

import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Component
@Slf4j
public class MultiInstanceListen implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {
        log.info("==============MultiInstanceListen==========");
        FlowElement element = execution.getCurrentFlowElement();
        if (element instanceof UserTask) {
            UserTask userTask = (UserTask) element;
            List<String> candidateUsers = userTask.getCandidateUsers();

            //多任务每个任务都会执行一次这个监听器，所以更新、插入需要小心、避免重复操作
            Object flag = execution.getVariable(userTask.getId().concat("_assigneeList"));
            if (flag == null) {
                log.info("candidateUsers value:", candidateUsers.toString());
                // 设置 setVariableLocal 会导致找不到 assigneeList 变量
                // userTask.getId() 就是节点定义ID，拼上它，可以解决一个流程里面多个审批节点问题
                execution.setVariable(userTask.getId().concat("_assigneeList"), candidateUsers);
            }

        }
    }
}
