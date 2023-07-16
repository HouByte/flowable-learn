package cn.meshed.flowable.countersign;

/**
 * <h1></h1>
 * 对应多任务(审批节点)xml里面的 <completionCondition>${multiInstance.accessCondition(execution)}</completionCondition>
 * @author Vincent Vic
 * @version 1.0
 */

import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.ExtensionAttribute;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * https://juejin.cn/post/7161769357482639396
 */
@Component(value = "multiInstance")
@Slf4j
public class MultiInstanceCompleteTask implements Serializable {

    public boolean accessCondition(DelegateExecution execution) {
        log.info("审批的条件处理");
        // 会签、还是或签，默认或签
        boolean flag = false;
        FlowElement element = execution.getCurrentFlowElement();
        if (element instanceof UserTask) {
            UserTask userTask = (UserTask) element;

            // 获取时会签还是或签
            List<ExtensionAttribute> checkType = userTask.getAttributes().get("checkType");
            if (checkType != null) {
                ExtensionAttribute extensionAttribute = checkType.get(0);
                if (extensionAttribute.getValue().equals("AND")) {
                    flag = true;
                }
            }
        }

        //否决判断，一票否决
        if (execution.getVariable("agree") != null && execution.getVariable("agree").equals("false")) {
            //输出方向为拒绝
            execution.setVariable("agree", "false");
            //一票否决其他实例没必要做，结束
            return true;
        }

        //获取所有的实例数
        int AllInstance = (int) execution.getVariable("nrOfInstances");
        // 已经完成的实例数
        int completedInstance = (int) execution.getVariable("nrOfCompletedInstances");

        if (flag) {
            // 会签
            if (AllInstance == completedInstance) {
                //输出方向为赞同
                execution.setVariable("agree", "true");
                return true;
            }
        } else {
            // 或签
            if (completedInstance >= 1) {
                execution.setVariable("agree", "true");
                return true;
            }
        }
        return false;
    }

}
