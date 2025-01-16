package com.px.rc.service.impl;

import com.px.rc.domain.TFile;
import com.px.rc.mapper.TFileMapper;
import com.px.rc.service.ITFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 附件Service业务层处理
 * 
 * @author 品讯科技
 * @date 2024-08
 */
@Service
public class TFileServiceImpl implements ITFileService
{
    @Autowired
    private TFileMapper tFileMapper;

    /**
     * 查询附件
     * 
     * @param id 附件主键
     * @return 附件
     */
    @Override
    public TFile selectTFileById(Long id)
    {
        return tFileMapper.selectTFileById(id);
    }

    /**
     * 查询附件列表
     * 
     * @param tFile 附件
     * @return 附件
     */
    @Override
    public List<TFile> selectTFileList(TFile tFile)
    {
        return tFileMapper.selectTFileList(tFile);
    }

    /**
     * 新增附件
     * 
     * @param tFile 附件
     * @return 结果
     */
    @Override
    public int insertTFile(TFile tFile)
    {
        return tFileMapper.insertTFile(tFile);
    }

    /**
     * 修改附件
     * 
     * @param tFile 附件
     * @return 结果
     */
    @Override
    public int updateTFile(TFile tFile)
    {
        return tFileMapper.updateTFile(tFile);
    }

    /**
     * 批量删除附件
     * 
     * @param ids 需要删除的附件主键
     * @return 结果
     */
    @Override
    public int deleteTFileByIds(Long[] ids)
    {
        return tFileMapper.deleteTFileByIds(ids);
    }

    /**
     * 删除附件信息
     * 
     * @param id 附件主键
     * @return 结果
     */
    @Override
    public int deleteTFileById(Long id)
    {
        return tFileMapper.deleteTFileById(id);
    }
}
