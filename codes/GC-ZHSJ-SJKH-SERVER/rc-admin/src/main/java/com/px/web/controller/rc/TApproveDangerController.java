package com.px.web.controller.rc;

import com.alibaba.fastjson.JSON;
import com.px.common.core.controller.BaseController;
import com.px.common.core.domain.entity.SysUser;
import com.px.common.core.page.TableDataInfo;
import com.px.common.utils.PageUtils;
import com.px.rc.domain.DevProcess;
import com.px.rc.domain.DevTask;
import com.px.rc.domain.TOpenItem;
import com.px.rc.domain.TProgress;
import com.px.rc.service.ITOpenItemService;
import com.px.rc.service.ITProgressService;
import com.px.system.service.ISysUserService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.px.common.utils.http.HttpContextUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程处理
 *
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequestMapping("/ehs/approvedanger")
public class TApproveDangerController extends BaseController {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ITProgressService tProgressService;

    @Autowired
    private ITOpenItemService tOpenItemService;

    /**
     * 我的申请列表
     */
    @GetMapping("/myApprovelist")
    public TableDataInfo myApprovelist(@RequestParam Map<String, Object> params) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //查询发起流程
        List<HistoricProcessInstance> historicProcessInstanceList =
                historyService.createHistoricProcessInstanceQuery()
                        .startedBy(getUserId().toString()).orderByProcessInstanceStartTime().desc()//参与者，组任务查询
                        .listPage(PageUtils.getStart(params), Integer.parseInt((String) params.get("pageSize")));
        Long count = historyService.createHistoricProcessInstanceQuery()
                .startedBy(getUserId().toString()).count();
        logger.info(JSON.toJSONString(historicProcessInstanceList));
        //整合我的申请数据
        List<DevProcess> list = new ArrayList<>();
        if (historicProcessInstanceList.size() > 0)
            for (HistoricProcessInstance h : historicProcessInstanceList
            ) {
                HistoricProcessInstance pi = processEngine.getHistoryService()
                        .createHistoricProcessInstanceQuery().processInstanceId(h.getId()).singleResult();
                logger.info(JSON.toJSONString(pi));
                if (pi == null) {
                    continue;
                }
                DevProcess devProcess = new DevProcess();
                logger.info("审批类型" + pi.getProcessDefinitionName());
                try {
                    if (pi.getProcessDefinitionName().equals("问卷审批流程")) {
                        TProgress progress = tProgressService.selectTProgressById(Long.parseLong(h.getBusinessKey()));
                        devProcess.setProgress(progress);
                        devProcess.setApNo(progress.getApNo());
                    } else if (pi.getProcessDefinitionName().equals("开项审批流程")) {
                        TOpenItem openItem = tOpenItemService.selectTOpenItemById(Long.parseLong(h.getBusinessKey()));
                        devProcess.setOpenItem(openItem);
                        devProcess.setApNo(openItem.getApNo());
                    }
                } catch (Exception e) {
                    logger.error(e.toString());
                }
                devProcess.setBusinessKey(pi.getBusinessKey());
                devProcess.setProcessCreateTime(pi.getStartTime());
                devProcess.setProcessId(pi.getId());
                devProcess.setProcessName(pi.getProcessDefinitionName());

                if (pi.getEndActivityId() != null) {
                    devProcess.setEnd(true);
                } else {
                    devProcess.setEnd(false);
                }
                logger.info("devProcess:" + devProcess);
                list.add(devProcess);
            }
        return getDataTable(list , count);
    }

    /**
     * 我的待办列表
     */
    @GetMapping("/backlogList")
    public TableDataInfo backlogList(@RequestParam Map<String, Object> params) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<Task> taskList = processEngine.getTaskService()//获取任务service
                .createTaskQuery()//创建查询对象
                .taskCandidateOrAssigned(getUserId().toString())
//                .taskAssignee(getUserId().toString())
                .orderByTaskCreateTime().desc()//参与者，组任务查询
                .listPage(PageUtils.getStart(params), Integer.parseInt((String) params.get("pageSize")));
        //分页数据
        Long count = processEngine.getTaskService().createTaskQuery().taskCandidateUser(getUserId().toString()).count();
        List<DevTask> list = new ArrayList<>();
        if (taskList.size() > 0) {
            for (Task task : taskList) {
                ProcessInstance pi = processEngine.getRuntimeService()/**表示正在执行的流程实例和执行对象*/
                        .createProcessInstanceQuery()/**创建流程实例查询*/
                        .processInstanceId(task.getProcessInstanceId())/**使用流程实例ID查询*/
                        .singleResult();
                logger.info("待办任务ID:" + task.getId());
                logger.info("待办任务name:" + task.getName());
                logger.info("待办任务创建时间:" + task.getCreateTime());
                logger.info("待办任务办理人:" + task.getAssignee());
                logger.info("流程实例ID:" + task.getProcessInstanceId());
                logger.info("执行对象ID:" + task.getExecutionId());
                logger.info(task.getTenantId());
                logger.info(task.getCategory());
                logger.info(task.getFormKey());
                logger.info("流程实例Name:" + pi.getProcessDefinitionName());
                DevTask devTask = new DevTask();
                devTask.setTaskId(task.getId());
                devTask.setTaskName(task.getName());
                devTask.setTaskCreateTime(task.getCreateTime());
                devTask.setProcessId(task.getProcessInstanceId());
                devTask.setProcessName(pi.getProcessDefinitionName());
                devTask.setProcessCreateTime(pi.getStartTime());
                devTask.setBusinessKey(pi.getBusinessKey());
                devTask.setBusinessKey(pi.getBusinessKey());
                if (pi.getProcessDefinitionName().equals("问卷审批流程")) {
                    TProgress progress = tProgressService.selectTProgressById(Long.parseLong(pi.getBusinessKey()));
                    devTask.setProgress(progress);
                    devTask.setApNo(progress.getApNo());
                } else if (pi.getProcessDefinitionName().equals("开项审批流程")) {
                    TOpenItem openItem = tOpenItemService.selectTOpenItemById(Long.parseLong(pi.getBusinessKey()));
                    devTask.setOpenItem(openItem);
                    devTask.setApNo(openItem.getApNo());
                }
                list.add(devTask);
            }
        }
        return getDataTable(list,count);
    }

    /**
     * 已办任务列表
     */
    @GetMapping("/doneTaskList")
    public TableDataInfo doneTaskList(@RequestParam Map<String, Object> params) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<HistoricTaskInstance> taskList = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(getUserId().toString())
                .finished().orderByHistoricTaskInstanceEndTime().desc()
                .listPage(PageUtils.getStart(params), Integer.parseInt((String) params.get("pageSize")));
        //分页数据
        Long count = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(getUserId().toString())
                .finished().count();
        logger.info(JSON.toJSONString(taskList));
        List<DevProcess> list = new ArrayList<>();
        if (taskList.size() > 0) {
            for (HistoricTaskInstance h : taskList) {
                //logger.info("HistoricTaskInstance:" + JSON.toJSONString(h));
                HistoricProcessInstance pi = processEngine.getHistoryService()
                        .createHistoricProcessInstanceQuery().processInstanceId(h.getProcessInstanceId()).singleResult();
                logger.info(JSON.toJSONString(pi));
                if (pi == null) {
                    continue;
                }
                DevProcess devProcess = new DevProcess();
                if (pi.getProcessDefinitionName().equals("问卷审批流程")) {
                    TProgress progress = tProgressService.selectTProgressById(Long.parseLong(pi.getBusinessKey()));
                    devProcess.setApNo(progress.getApNo());
                    devProcess.setProgress(progress);
                } else if (pi.getProcessDefinitionName().equals("开项审批流程")) {
                    TOpenItem openItem = tOpenItemService.selectTOpenItemById(Long.parseLong(pi.getBusinessKey()));
                    devProcess.setApNo(openItem.getApNo());
                    devProcess.setOpenItem(openItem);
                }
                devProcess.setBusinessKey(pi.getBusinessKey());
                devProcess.setProcessCreateTime(pi.getStartTime());
                devProcess.setProcessId(pi.getId());
                devProcess.setProcessName(pi.getProcessDefinitionName());

                logger.info("devProcess:" + devProcess);
                if (pi.getEndActivityId() != null) {
                    devProcess.setEnd(true);
                } else {
                    devProcess.setEnd(false);
                }
                list.add(devProcess);
            }
        }
        return getDataTable(list,count);
    }

    /**
     * 流转列表
     */
    @GetMapping("/hiTaskList")
    public TableDataInfo hiTaskList(@RequestParam Map<String, Object> params) {
        String pid = (String) params.get("processId");
        logger.info("processId：" + JSON.toJSONString(pid));
        HistoricTaskInstanceQuery htiq = historyService.createHistoricTaskInstanceQuery();
        List<HistoricTaskInstance> htiLists = htiq.processInstanceId(pid).finished().orderByHistoricTaskInstanceEndTime().asc().list();


        logger.info("历史任务：" + JSON.toJSONString(htiLists));
        List<DevTask> devTaskList = new ArrayList<>();
        if (htiLists.size() > 0) {
            for (HistoricTaskInstance hi : htiLists
            ) {
                List<Comment> commentList = taskService.getTaskComments(hi.getId());
                logger.info("评论列表：" + JSON.toJSONString(commentList));
                DevTask devTask = new DevTask();
                devTask.setTaskName(hi.getName());
                devTask.setTaskCreateTime(hi.getCreateTime());
                devTask.setTaskEndTime(hi.getEndTime());
                if (commentList.size() > 0) {
                    devTask.setComment(commentList.get(0).getFullMessage());
                }
                SysUser user = sysUserService.selectUserById(Long.parseLong(hi.getAssignee()));
                devTask.setUserName(user.getNickName());
                devTaskList.add(devTask);
            }
        }
        //当前任务
        try {
            Task task = taskService.createTaskQuery().processInstanceId(pid).active().singleResult();
            if (task!= null) {
                task.getName();
                task.getCreateTime();
                DevTask devTask = new DevTask();
                devTask.setTaskName(task.getName());
                devTask.setTaskCreateTime(task.getCreateTime());
                if (task.getAssignee()!= null) {
                    SysUser user = sysUserService.selectUserById(Long.parseLong(task.getAssignee()));
                    devTask.setUserName(user.getNickName());
                }
                devTaskList.add(devTask);
            }
        }catch (Exception e) {
            logger.error(e.toString());
        }
        return getDataTable(devTaskList);
    }

    @GetMapping("/processImg/{processId}")
    public void currentProcessInstanceImage(@PathVariable("processId") String processId, HttpServletResponse response) throws IOException {
        InputStream inputStream = currentProcessInstanceImage(processId);
        OutputStream outputStream = response.getOutputStream();
        HttpContextUtils.copyImageStream(inputStream, outputStream);
    }

    /**
     * 获取当前任务流程图
     *
     * @param processId
     * @return
     */
    public InputStream currentProcessInstanceImage(String processId) {
//        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
//        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processId);
        HistoricProcessInstance hi = historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(hi.getProcessDefinitionId());

        BpmnModel bpmnModel = repositoryService.getBpmnModel(hi.getProcessDefinitionId());
        // ID 为 流程定义Key
        Process process = bpmnModel.getProcessById(processDefinition.getKey());

        // 流程节点ID
        // 获取历史流程实例
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();
        // 获取流程中已经执行的节点，按照执行先后顺序排序
        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery().processInstanceId(processId)
                .orderByHistoricActivityInstanceId().asc().list();
        // 高亮已经执行流程节点ID集合
        List<String> highLightedActivitiIds = new ArrayList<>();
        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            highLightedActivitiIds.add(historicActivityInstance.getActivityId());
        }
        //获取已完成的节点
        List<HistoricActivityInstance> finished = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processId)
                .finished().list();
        //高亮线路id集合
//        List<String> highLightedFlowIds = getHighLightedFlows(bpmnModel, historicActivityInstances);
        List<String> highLightedFlowIds = getHighLine(bpmnModel, historicActivityInstances,finished);
        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();

//     生成流程图
//        InputStream inputStream = generator.generateJpgDiagram(bpmnModel);
//        InputStream inputStream = generator.generatePngDiagram(bpmnModel);
//        InputStream inputStream = generator.generateDiagram(bpmnModel, "jpg", highLightedActivities);

// 生成图片
        InputStream inputStream = generator.generateDiagram(bpmnModel, "jpg", highLightedActivitiIds, highLightedFlowIds, "宋体", "宋体", "宋体", null, 2.0);
        return inputStream;
    }

    /**
     * 获取流程定义图片
     *
     * @param processDefinitionId
     * @return
     */
    public InputStream getDefinitionImage(String processDefinitionId) {

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getDiagramResourceName());
        return inputStream;
    }

    /**
     * 获取已经流转的线
     *
     * @param bpmnModel
     * @param historicActivityInstances
     * @return
     */
    private static List<String> getHighLightedFlows(BpmnModel bpmnModel, List<HistoricActivityInstance> historicActivityInstances) {
        // 高亮流程已发生流转的线id集合
        List<String> highLightedFlowIds = new ArrayList<>();
        // 全部活动节点
        List<FlowNode> historicActivityNodes = new ArrayList<>();
        // 已完成的历史活动节点
        List<HistoricActivityInstance> finishedActivityInstances = new ArrayList<>();

        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId(), true);
            historicActivityNodes.add(flowNode);
            if (historicActivityInstance.getEndTime() != null) {
                finishedActivityInstances.add(historicActivityInstance);
            }
        }

        FlowNode currentFlowNode = null;
        FlowNode targetFlowNode = null;
        // 遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的
        for (HistoricActivityInstance currentActivityInstance : finishedActivityInstances) {
            // 获得当前活动对应的节点信息及outgoingFlows信息
            currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currentActivityInstance.getActivityId(), true);
            List<SequenceFlow> sequenceFlows = currentFlowNode.getOutgoingFlows();

            /**
             * 遍历outgoingFlows并找到已已流转的 满足如下条件认为已已流转： 1.当前节点是并行网关或兼容网关，则通过outgoingFlows能够在历史活动中找到的全部节点均为已流转 2.当前节点是以上两种类型之外的，通过outgoingFlows查找到的时间最早的流转节点视为有效流转
             */
            if ("parallelGateway".equals(currentActivityInstance.getActivityType()) || "inclusiveGateway".equals(currentActivityInstance.getActivityType())) {
                // 遍历历史活动节点，找到匹配流程目标节点的
                for (SequenceFlow sequenceFlow : sequenceFlows) {
                    targetFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(sequenceFlow.getTargetRef(), true);
                    if (historicActivityNodes.contains(targetFlowNode)) {
                        highLightedFlowIds.add(targetFlowNode.getId());
                    }
                }
            } else {
                List<Map<String, Object>> tempMapList = new ArrayList<>();
                for (SequenceFlow sequenceFlow : sequenceFlows) {
                    for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                        if (historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("highLightedFlowId", sequenceFlow.getId());
                            map.put("highLightedFlowStartTime", historicActivityInstance.getStartTime().getTime());
                            tempMapList.add(map);
                        }
                    }
                }

                if (!CollectionUtils.isEmpty(tempMapList)) {
                    // 遍历匹配的集合，取得开始时间最早的一个
                    long earliestStamp = 0L;
                    String highLightedFlowId = null;
                    for (Map<String, Object> map : tempMapList) {
                        long highLightedFlowStartTime = Long.valueOf(map.get("highLightedFlowStartTime").toString());
                        if (earliestStamp == 0 || earliestStamp >= highLightedFlowStartTime) {
                            highLightedFlowId = map.get("highLightedFlowId").toString();
                            earliestStamp = highLightedFlowStartTime;
                        }
                    }

                    highLightedFlowIds.add(highLightedFlowId);
                }

            }

        }
        return highLightedFlowIds;
    }

    private List<String> getHighLine(BpmnModel bpmnModel, List<HistoricActivityInstance> historicActivityInstances, List<HistoricActivityInstance> finishedActivityInstances) {

        // 高亮流程已发生流转的线id集合
        List<String> highLightedFlowIds = new ArrayList<>();
        // 全部活动节点
        List<FlowNode> historicActivityNodes = new ArrayList<>();

        //1、拿到所有的活动节点定义
        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId(), true);
            historicActivityNodes.add(flowNode);
        }

        FlowNode currentFlowNode;
        FlowNode targetFlowNode;
        // 2、遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的
        for (HistoricActivityInstance currentActivityInstance : finishedActivityInstances) {
            // 获得当前活动对应的节点信息及outgoingFlows信息
            currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currentActivityInstance.getActivityId(), true);
            List<SequenceFlow> outgoingFlows = currentFlowNode.getOutgoingFlows();

            /*
             * 遍历outgoingFlows并找到已流转的 满足如下条件认为已已流转：
             * 1.当前节点是并行网关或包含网关，则通过outgoingFlows能够在历史活动中找到的全部节点均为已流转
             * 2.当前节点是以上两种类型之外的，通过outgoingFlows查找到的开始时间最早的流转节点视为有效流转
             */
            String activityType = currentActivityInstance.getActivityType();

            if ("parallelGateway".equals(currentActivityInstance.getActivityType()) || "inclusiveGateway".equals(currentActivityInstance.getActivityType())) {
                // 遍历历史活动节点，找到匹配流程目标节点的
                for (SequenceFlow sequenceFlow : outgoingFlows) {
                    targetFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(sequenceFlow.getTargetRef(), true);
                    if (historicActivityNodes.contains(targetFlowNode)) {
                        highLightedFlowIds.add(targetFlowNode.getId());
                    }
                }
                continue;
            }

            //1、拿到所有的TargetRef
            List<String> targetRefList = outgoingFlows.stream()
                    .map(SequenceFlow::getTargetRef)
                    .collect(Collectors.toList());

            //2、拿到ActivityId == TargetRef的所有 HistoricActivityInstance
            List<HistoricActivityInstance> historicActivityInstanceList = historicActivityInstances.stream()
                    .filter((HistoricActivityInstance instance) -> targetRefList.contains(instance.getActivityId()))
                    .collect(Collectors.toList());

            //3、开始时间>=当前节点结束时间的第一个 HistoricActivityInstance
            List<HistoricActivityInstance> historicActivityInstanceResultList = historicActivityInstanceList.stream()
                    .filter(historicActivityInstance -> historicActivityInstance.getStartTime()
                            .compareTo(currentActivityInstance.getEndTime()) >= 0)
                    .sorted(Comparator.comparing(HistoricActivityInstance::getStartTime))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(historicActivityInstanceResultList)) {
                continue;
            }

            HistoricActivityInstance historicActivityInstanceResult = historicActivityInstanceResultList.get(0);

            //4、HistoricActivityInstance拿到flowid
            List<SequenceFlow> sequenceFlowList = outgoingFlows.stream()
                    .filter(sequenceFlow -> sequenceFlow.getTargetRef().equals(historicActivityInstanceResult.getActivityId()))
                    .collect(Collectors.toList());
            highLightedFlowIds.addAll(sequenceFlowList.stream().map(e->e.getId()).collect(Collectors.toList()));
        }
        return highLightedFlowIds;
    }
}
