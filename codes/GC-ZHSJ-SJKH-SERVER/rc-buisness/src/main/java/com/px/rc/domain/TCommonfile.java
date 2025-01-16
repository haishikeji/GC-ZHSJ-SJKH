package com.px.rc.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.px.common.annotation.Excel;
import com.px.common.core.domain.BaseEntity;

/**
 * 通用附件对象 t_commonfile
 * 
 * @author 品讯科技
 * @date 2024-08
 */
public class TCommonfile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 业务id */
    @Excel(name = "业务id")
    private Long pId;

    /** 文件名 */
    @Excel(name = "文件名")
    private String fileName;

    /** 路径 */
    @Excel(name = "路径")
    private String fileUrl;

    /** 删除标识 */
    private Long delFlag;

    /** 创建人 */
    @Excel(name = "创建人")
    private String createrCode;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdate;

    /** 更新人 */
    @Excel(name = "更新人")
    private String updaterCode;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedate;

    /** 备注 */
    @Excel(name = "备注")
    private String remarks;

    /** 业务类型 */
    @Excel(name = "业务类型")
    private String pType;

    /** 文件大小 */
    @Excel(name = "文件大小")
    private String fileSize;

    /** 业务字段 */
    @Excel(name = "业务字段")
    private String pValue;

    /** 业务时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "业务时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date pDate;

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
    public void setpId(Long pId) 
    {
        this.pId = pId;
    }

    public Long getpId() 
    {
        return pId;
    }
    public void setFileName(String fileName) 
    {
        this.fileName = fileName;
    }

    public String getFileName() 
    {
        return fileName;
    }
    public void setFileUrl(String fileUrl) 
    {
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() 
    {
        return fileUrl;
    }
    public void setDelFlag(Long delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Long getDelFlag() 
    {
        return delFlag;
    }
    public void setCreaterCode(String createrCode) 
    {
        this.createrCode = createrCode;
    }

    public String getCreaterCode() 
    {
        return createrCode;
    }
    public void setCreatedate(Date createdate) 
    {
        this.createdate = createdate;
    }

    public Date getCreatedate() 
    {
        return createdate;
    }
    public void setUpdaterCode(String updaterCode) 
    {
        this.updaterCode = updaterCode;
    }

    public String getUpdaterCode() 
    {
        return updaterCode;
    }
    public void setUpdatedate(Date updatedate) 
    {
        this.updatedate = updatedate;
    }

    public Date getUpdatedate() 
    {
        return updatedate;
    }
    public void setRemarks(String remarks) 
    {
        this.remarks = remarks;
    }

    public String getRemarks() 
    {
        return remarks;
    }
    public void setpType(String pType) 
    {
        this.pType = pType;
    }

    public String getpType() 
    {
        return pType;
    }
    public void setFileSize(String fileSize) 
    {
        this.fileSize = fileSize;
    }

    public String getFileSize() 
    {
        return fileSize;
    }
    public void setpValue(String pValue) 
    {
        this.pValue = pValue;
    }

    public String getpValue() 
    {
        return pValue;
    }
    public void setpDate(Date pDate) 
    {
        this.pDate = pDate;
    }

    public Date getpDate() 
    {
        return pDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("pId", getpId())
            .append("fileName", getFileName())
            .append("fileUrl", getFileUrl())
            .append("delFlag", getDelFlag())
            .append("createrCode", getCreaterCode())
            .append("createdate", getCreatedate())
            .append("updaterCode", getUpdaterCode())
            .append("updatedate", getUpdatedate())
            .append("remarks", getRemarks())
            .append("pType", getpType())
            .append("fileSize", getFileSize())
            .append("pValue", getpValue())
            .append("pDate", getpDate())
            .toString();
    }
}
