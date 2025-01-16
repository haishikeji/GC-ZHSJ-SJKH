package com.px.web.controller.rc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.px.common.core.domain.entity.SysDept;
import com.px.common.utils.DateUtils;
import com.px.common.utils.StringUtils;
import com.px.rc.domain.*;
import com.px.rc.service.ITQuestionnaireService;
import com.px.system.service.ISysDeptService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.px.rc.service.ITOpenItemService;
import com.px.common.utils.poi.ExcelUtil;
import com.px.common.core.page.TableDataInfo;

/**
 * 开项Controller
 * 
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequestMapping("/rc/openitem")
public class TOpenItemController extends BaseController
{
    @Autowired
    private ITOpenItemService tOpenItemService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ITQuestionnaireService tQuestionnaireService;

    /**
     * 流程处理
     */
    @PutMapping("/handle")
    public AjaxResult handle(@RequestBody DevTask devTask) {
        TOpenItem openItem = devTask.getOpenItem();
        TQuestionnaire questionnaire = tQuestionnaireService.selectTQuestionnaireById(openItem.getQuestionnaireId());
        String taskId = devTask.getTaskId();
        String taskName = devTask.getTaskName();
        String condition = devTask.getCondition();
        Map<String, Object> param = new HashMap<>();
        param.put("condition", condition);
        String comment = "";
        if (taskName.equals("提交申请")) {
            // 进度审批状态修改为2-待审核
            openItem.setStatus("2");
            comment = "重新提交";
        } else if (taskName.equals("审核")) {
            if ("1".equals(condition)) {
                // 进度审批状态修改为4-已通过
                openItem.setStatus("4");
                comment = "通过";
            } else {
                // 进度审批状态修改为3-未通过
                openItem.setStatus("3");
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
//            if (openItem.getProcessId().equals(t.getProcessInstanceId())) {
//                openItem.setTaskId(t.getId());
//                openItem.setTaskName(t.getName());
//            }
//        }
        // 更新数据
        tOpenItemService.updateTOpenItem(openItem);
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
    public AjaxResult apply(@RequestBody TOpenItem tOpenItem) {
        // 当前登录用户id
        String userId = getUserId().toString();

        // 开始流程
        Authentication.setAuthenticatedUserId(userId);
        Map<String, Object> variables = new HashMap<>();
        variables.put("personInCharge", tOpenItem.getPersonInCharge());
        variables.put("reviewer", tOpenItem.getReviewer());
        long businessKey = tOpenItem.getId();
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("openitemProcess", String.valueOf(businessKey), variables);

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

        tOpenItem.setProcessId(pi.getProcessInstanceId());
        tOpenItem.setApNo(DateUtils.dateTimeNow() + userId);
        // 进度审批状态修改为2-待审核
        tOpenItem.setStatus("2");

        tOpenItemService.updateTOpenItem(tOpenItem);

        return AjaxResult.success();
    }


    /**
     * 查询开项列表
     */
    @PreAuthorize("@ss.hasPermi('rc:openitem:list')")
    @GetMapping("/list")
    public TableDataInfo list(TOpenItem tOpenItem)
    {
        startPage();
        List<TOpenItem> list = tOpenItemService.selectTOpenItemList(tOpenItem);
        for (TOpenItem obj : list) {
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
     * 导出开项列表
     */
    @PreAuthorize("@ss.hasPermi('rc:openitem:export')")
    @Log(title = "开项", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TOpenItem tOpenItem)
    {
        List<TOpenItem> list = tOpenItemService.selectTOpenItemList(tOpenItem);
        ExcelUtil<TOpenItem> util = new ExcelUtil<TOpenItem>(TOpenItem.class);
        util.exportExcel(response, list, "开项数据");
    }

    /**
     * 获取开项详细信息
     */
    @PreAuthorize("@ss.hasPermi('rc:openitem:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(tOpenItemService.selectTOpenItemById(id));
    }

    /**
     * 新增开项
     */
    @PreAuthorize("@ss.hasPermi('rc:openitem:add')")
    @Log(title = "开项", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TOpenItem tOpenItem)
    {
        tOpenItem.setDeptId(getLoginUser().getDeptId().toString());
        int i = tOpenItemService.insertTOpenItem(tOpenItem);
        this.apply(tOpenItem);
        return toAjax(i);
    }

    /**
     * 修改开项
     */
    @PreAuthorize("@ss.hasPermi('rc:openitem:edit')")
    @Log(title = "开项", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TOpenItem tOpenItem)
    {
        return toAjax(tOpenItemService.updateTOpenItem(tOpenItem));
    }

    /**
     * 删除开项
     */
    @PreAuthorize("@ss.hasPermi('rc:openitem:remove')")
    @Log(title = "开项", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tOpenItemService.deleteTOpenItemByIds(ids));
    }
}
