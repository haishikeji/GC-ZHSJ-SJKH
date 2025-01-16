package com.px.rc.service;

import com.px.rc.domain.TFile;

import java.util.List;

/**
 * 附件Service接口
 * 
 * @author 品讯科技
 * @date 2024-08
 */
public interface ITFileService 
{
    /**
     * 查询附件
     * 
     * @param id 附件主键
     * @return 附件
     */
    public TFile selectTFileById(Long id);

    /**
     * 查询附件列表
     * 
     * @param tFile 附件
     * @return 附件集合
     */
    public List<TFile> selectTFileList(TFile tFile);

    /**
     * 新增附件
     * 
     * @param tFile 附件
     * @return 结果
     */
    public int insertTFile(TFile tFile);

    /**
     * 修改附件
     * 
     * @param tFile 附件
     * @return 结果
     */
    public int updateTFile(TFile tFile);

    /**
     * 批量删除附件
     * 
     * @param ids 需要删除的附件主键集合
     * @return 结果
     */
    public int deleteTFileByIds(Long[] ids);

    /**
     * 删除附件信息
     * 
     * @param id 附件主键
     * @return 结果
     */
    public int deleteTFileById(Long id);
}
