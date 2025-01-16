package com.px.rc.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.px.common.annotation.Excel;
import com.px.common.core.domain.BaseEntity;

/**
 * 会议对象 t_meeting
 * 
 * @author 品讯科技
 * @date 2024-08
 */
public class TMeeting extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 年份 */
    @Excel(name = "年份")
    private String year;

    /** 涉及code */
    @Excel(name = "涉及code")
    private Long questionnaireId;

    /** 负责人 */
    @Excel(name = "负责人")
    private Long personInCharge;

    private String personInChargeName;

    /** 备注 */
    @Excel(name = "备注")
    private String remarks;

    /** 装置id */
    @Excel(name = "装置id")
    private String deptId;

    /** 装置 */
    @Excel(name = "装置")
    private String deptName;

    public String getPersonInChargeName() {
        return personInChargeName;
    }

    public void setPersonInChargeName(String personInChargeName) {
        this.personInChargeName = personInChargeName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setYear(String year) 
    {
        this.year = year;
    }

    public String getYear() 
    {
        return year;
    }
    public void setQuestionnaireId(Long questionnaireId) 
    {
        this.questionnaireId = questionnaireId;
    }

    public Long getQuestionnaireId() 
    {
        return questionnaireId;
    }
    public void setPersonInCharge(Long personInCharge) 
    {
        this.personInCharge = personInCharge;
    }

    public Long getPersonInCharge() 
    {
        return personInCharge;
    }
    public void setRemarks(String remarks) 
    {
        this.remarks = remarks;
    }

    public String getRemarks() 
    {
        return remarks;
    }
    public void setDeptId(String deptId) 
    {
        this.deptId = deptId;
    }

    public String getDeptId() 
    {
        return deptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("year", getYear())
            .append("questionnaireId", getQuestionnaireId())
            .append("personInCharge", getPersonInCharge())
            .append("remarks", getRemarks())
            .append("deptId", getDeptId())
            .toString();
    }
}
