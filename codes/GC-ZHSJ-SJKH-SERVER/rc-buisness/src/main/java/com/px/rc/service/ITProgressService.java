package com.px.rc.service;

import java.util.List;
import com.px.rc.domain.TProgress;

/**
 * 进度Service接口
 * 
 * @author 品讯科技
 * @date 2024-08
 */
public interface ITProgressService 
{
    /**
     * 查询进度
     * 
     * @param id 进度主键
     * @return 进度
     */
    public TProgress selectTProgressById(Long id);

    /**
     * 查询进度列表
     * 
     * @param tProgress 进度
     * @return 进度集合
     */
    public List<TProgress> selectTProgressList(TProgress tProgress);

    /**
     * 新增进度
     * 
     * @param tProgress 进度
     * @return 结果
     */
    public int insertTProgress(TProgress tProgress);

    /**
     * 修改进度
     * 
     * @param tProgress 进度
     * @return 结果
     */
    public int updateTProgress(TProgress tProgress);

    /**
     * 批量删除进度
     * 
     * @param ids 需要删除的进度主键集合
     * @return 结果
     */
    public int deleteTProgressByIds(Long[] ids);

    /**
     * 删除进度信息
     * 
     * @param id 进度主键
     * @return 结果
     */
    public int deleteTProgressById(Long id);
}
