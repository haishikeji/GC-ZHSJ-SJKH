package com.px.web.controller.rc;

import com.px.common.core.domain.entity.SysDept;
import com.px.common.utils.DateUtils;
import com.px.rc.domain.DevTask;
import com.px.rc.domain.TProgress;
import com.px.rc.service.ITProgressService;
import com.px.system.service.ISysDeptService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.px.common.annotation.Log;
import com.px.common.core.controller.BaseController;
import com.px.common.core.domain.AjaxResult;
import com.px.common.enums.BusinessType;
import com.px.rc.domain.TQuestionnaire;
import com.px.rc.service.ITQuestionnaireService;
import com.px.common.utils.poi.ExcelUtil;
import com.px.common.core.page.TableDataInfo;
import com.px.common.utils.StringUtils;

/**
 * 问卷Controller
 * 
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequestMapping("/rc/questionnaire")
public class TQuestionnaireController extends BaseController
{
    @Autowired
    private ITQuestionnaireService tQuestionnaireService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ITProgressService tProgressService;

    /**
     * 流程处理
     */
    @PutMapping("/handle")
    public AjaxResult handle(@RequestBody DevTask devTask) {
        TProgress progress = devTask.getProgress();
        TQuestionnaire questionnaire = tQuestionnaireService.selectTQuestionnaireById(progress.getQuestionnaireId());
        String taskId = devTask.getTaskId();
        String taskName = devTask.getTaskName();
        String condition = devTask.getCondition();
        Map<String, Object> param = new HashMap<>();
        param.put("condition", condition);
        String comment = "";
        if (taskName.equals("提交申请")) {
            // 进度审批状态修改为2-待审核
            progress.setApplyStatus("2");
            comment = "重新提交";
        } else if (taskName.equals("审核")) {
            if ("1".equals(condition)) {
                // 进度审批状态修改为4-已通过
                progress.setApplyStatus("4");
                // 问卷完成情况修改为1-完成
                questionnaire.setCompletionStatus("1");
                comment = "通过";
            } else {
                // 进度审批状态修改为3-未通过
                progress.setApplyStatus("3");
                comment = "驳回";
            }
        }
        // 使用任务id,获取任务对象，获取流程实例id
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        //利用任务对象，获取流程实例id
        String processInstancesId = task.getProcessInstanceId();
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //认领任务
        processEngine.getTaskService()
                .claim(taskId, getUserId().toString());
        taskService.addComment(taskId, processInstancesId, comment);
        taskService.complete(taskId, param);
//        // 标记taskId、TaskName
//        List<Task> list = processEngine.getTaskService()//获取任务service
//                .createTaskQuery()//创建查询对象
//                .taskCandidateOrAssigned(questionnaire.getReviewer()+"").list();
//        for (Task t : list) {
//            if (progress.getProcessId().equals(t.getProcessInstanceId())) {
//                progress.setTaskId(t.getId());
//                progress.setTaskName(t.getName());
//            }
//        }
        // 更新数据
        tProgressService.updateTProgress(progress);
        String year = questionnaire.getYear();
        if (StringUtils.isNotEmpty(year) && !"".equals(year) && year.length() > 4) {
            questionnaire.setYear(year.substring(0, year.indexOf("-")));
        }
        tQuestionnaireService.updateTQuestionnaire(questionnaire);
        return AjaxResult.success();
    }

    /**
     * 提交申请
     */
    @PutMapping("/apply")
    public AjaxResult apply(@RequestBody TProgress tProgress) {
        // 当前登录用户id
        String userId = getUserId().toString();

        // 开始流程
        Authentication.setAuthenticatedUserId(userId);
        Map<String, Object> variables = new HashMap<>();
        variables.put("personInCharge", tProgress.getPersonInCharge());
        variables.put("reviewer", tQuestionnaireService.selectTQuestionnaireById(tProgress.getQuestionnaireId()).getReviewer());
        long businessKey = tProgress.getId();
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("questionnaireProcess", String.valueOf(businessKey), variables);

        // 提交申请
        Map<String, Object> param = new HashMap<>();
        param.put("condition", "1");
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().taskCandidateOrAssigned(userId).list();
        String taskId = null;
        String processInstanceId = null;
        for (Task task : list) {
            processInstanceId = task.getProcessInstanceId();
            if (processInstanceId.equals(pi.getProcessInstanceId())) {
                taskId = task.getId();
            }
        }
        processEngine.getTaskService().claim(taskId, getUserId().toString());
        taskService.addComment(taskId, processInstanceId, "无");
        taskService.complete(taskId, param);

        tProgress.setProcessId(pi.getProcessInstanceId());
        tProgress.setApNo(DateUtils.dateTimeNow() + userId);
        // 进度审批状态修改为2-待审核
        tProgress.setApplyStatus("2");

        tProgressService.updateTProgress(tProgress);

        return AjaxResult.success();
    }

    /**
     * 查询问卷列表
     */
    @GetMapping("/list")
    public TableDataInfo list(TQuestionnaire tQuestionnaire)
    {
        startPage();
        List<TQuestionnaire> list = tQuestionnaireService.selectTQuestionnaireList(tQuestionnaire);
        for (TQuestionnaire obj : list) {
            String deptId = obj.getDeptId();
            if (StringUtils.isNotEmpty(deptId)) {
                if (deptId.indexOf(",") != -1) {
                    StringBuffer sb = new StringBuffer();
                    String[] ids = deptId.split(",");
                    for (String id : ids) {
                        SysDept sysDept = deptService.selectDeptById(Long.parseLong(id));
                        sb.append(sysDept.getDeptName()).append(" / ");
                    }
                    obj.setDeptName(sb.toString().substring(0, sb.length() - 3));
                } else {
                    obj.setDeptName(deptService.selectDeptById(Long.parseLong(deptId)).getDeptName());
                }
            }
        }
        return getDataTable(list);
    }

    /**
     * 导出问卷列表
     */
    @Log(title = "问卷", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TQuestionnaire tQuestionnaire)
    {
        List<TQuestionnaire> list = tQuestionnaireService.selectTQuestionnaireList(tQuestionnaire);
        ExcelUtil<TQuestionnaire> util = new ExcelUtil<TQuestionnaire>(TQuestionnaire.class);
        util.exportExcel(response, list, "问卷数据");
    }

    /**
     * 获取问卷详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        TQuestionnaire tQuestionnaire = tQuestionnaireService.selectTQuestionnaireById(id);
        String year = tQuestionnaire.getYear();
        if (StringUtils.isNotEmpty(year) && !"".equals(year) && year.length() > 4) {
            tQuestionnaire.setYear(year.substring(0, year.indexOf("-")));
        }
        return success(tQuestionnaire);
    }

    /**
     * 新增问卷
     */
    @Log(title = "问卷", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TQuestionnaire tQuestionnaire)
    {
        if (StringUtils.isNull(tQuestionnaire.getDeptId())) {
            tQuestionnaire.setDeptId(getLoginUser().getDeptId().toString());
        }
        return toAjax(tQuestionnaireService.insertTQuestionnaire(tQuestionnaire));
    }

    /**
     * 修改问卷
     */
    @Log(title = "问卷", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TQuestionnaire tQuestionnaire)
    {
        return toAjax(tQuestionnaireService.updateTQuestionnaire(tQuestionnaire));
    }

    /**
     * 删除问卷
     */
    @Log(title = "问卷", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tQuestionnaireService.deleteTQuestionnaireByIds(ids));
    }
}
