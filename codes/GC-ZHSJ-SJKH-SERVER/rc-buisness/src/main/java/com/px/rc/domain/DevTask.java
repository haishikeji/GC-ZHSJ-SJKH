package com.px.rc.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 申请管理我的待办显示实体类
 *
 * @author 品讯科技
 * @date 2024-08
 */
public class DevTask {

    /** 待办任务ID */
    private String taskId;

    /** 待办任务name */
    private String taskName;

    /** 待办任务创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date taskCreateTime;

    /** 待办任务结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date taskEndTime;

    /** 隐患申请对象 */
    private Object approveObj;

    /** 流程ID */
    private String processId;

    /** 流程名称 */
    private String processName;

    /** 流程创建时间 */
    private Date processCreateTime;

    /** 业务键 */
    private String businessKey;

    /** 隐患申请状态 */
    private String submitType;

    /** 审批评论 */
    private String comment;

    /** 流转办理人姓名 */
    private String userName;

    /** 执行人措施前url */
    private String beforeUrl;

    /** 执行人措施前文件名称 */
    private String beforeFilename;

    /** 执行人措施后url */
    private String afterUrl;

    /** 执行人措施后文件名称 */
    private String afterFilename;

    private String condition;

    private String apNo;

    private TProgress progress;

    private TOpenItem openItem;

    public TOpenItem getOpenItem() {
        return openItem;
    }

    public void setOpenItem(TOpenItem openItem) {
        this.openItem = openItem;
    }

    public TProgress getProgress() {
        return progress;
    }

    public void setProgress(TProgress progress) {
        this.progress = progress;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getApNo() {
        return apNo;
    }

    public void setApNo(String apNo) {
        this.apNo = apNo;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Date getTaskCreateTime() {
        return taskCreateTime;
    }

    public void setTaskCreateTime(Date taskCreateTime) {
        this.taskCreateTime = taskCreateTime;
    }

    public Date getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(Date taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Date getProcessCreateTime() {
        return processCreateTime;
    }

    public void setProcessCreateTime(Date processCreateTime) {
        this.processCreateTime = processCreateTime;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getSubmitType() {
        return submitType;
    }

    public void setSubmitType(String submitType) {
        this.submitType = submitType;
    }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }
    public void setBeforeUrl(String beforeUrl) { this.beforeUrl = beforeUrl; }

    public String getBeforeUrl() { return beforeUrl; }
    public void setBeforeFilename(String beforeFilename) { this.beforeFilename = beforeFilename; }

    public String getBeforeFilename() { return beforeFilename; }
    public void setAfterUrl(String afterUrl) { this.afterUrl = afterUrl; }

    public String getAfterUrl() { return afterUrl; }
    public void setAfterFilename(String afterFilename) { this.afterFilename = afterFilename; }

    public String getAfterFilename() { return afterFilename; }

    public Object getApproveObj() {
        return approveObj;
    }

    public void setApproveObj(Object approveObj) {
        this.approveObj = approveObj;
    }

    @Override
    public String toString() {
        return "DevTask{" +
                "taskId='" + taskId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", taskCreateTime=" + taskCreateTime +
                ", taskEndTime=" + taskEndTime +
                ", processId='" + processId + '\'' +
                ", processName='" + processName + '\'' +
                ", processCreateTime=" + processCreateTime +
                ", businessKey='" + businessKey + '\'' +
                ", submitType='" + submitType + '\'' +
                ", comment='" + comment + '\'' +
                ", userName='" + userName + '\'' +
                ", beforeUrl='" + beforeUrl + '\'' +
                ", beforeFilename='" + beforeFilename + '\'' +
                ", afterUrl='" + afterUrl + '\'' +
                ", afterFilename='" + afterFilename + '\'' +
                '}';
    }
}
