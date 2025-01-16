package com.px.rc.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.px.common.annotation.Excel;
import com.px.common.core.domain.BaseEntity;

/**
 * 装置信息对象 t_dept_info
 * 
 * @author 品讯科技
 * @date 2024-08
 */
public class TDeptInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 装置信息 */
    @Excel(name = "装置信息")
    private String deptInfo;

    /** 装置id */
    @Excel(name = "装置id")
    private String deptId;

    /** 年份 */
    @Excel(name = "年份", width = 30)
    private String year;

    /** 装置 */
    @Excel(name = "装置")
    private String deptName;

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
    public void setDeptInfo(String deptInfo) 
    {
        this.deptInfo = deptInfo;
    }

    public String getDeptInfo() 
    {
        return deptInfo;
    }
    public void setDeptId(String deptId)
    {
        this.deptId = deptId;
    }

    public String getDeptId()
    {
        return deptId;
    }
    public void setYear(String year)
    {
        this.year = year;
    }

    public String getYear()
    {
        return year;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptInfo", getDeptInfo())
            .append("deptId", getDeptId())
            .append("year", getYear())
            .toString();
    }
}
