package com.px.rc.service;

import java.util.List;
import com.px.rc.domain.TDeptInfo;

/**
 * 装置信息Service接口
 * 
 * @author 品讯科技
 * @date 2024-08
 */
public interface ITDeptInfoService 
{
    /**
     * 查询装置信息
     * 
     * @param id 装置信息主键
     * @return 装置信息
     */
    public TDeptInfo selectTDeptInfoById(Long id);

    /**
     * 查询装置信息列表
     * 
     * @param tDeptInfo 装置信息
     * @return 装置信息集合
     */
    public List<TDeptInfo> selectTDeptInfoList(TDeptInfo tDeptInfo);

    /**
     * 新增装置信息
     * 
     * @param tDeptInfo 装置信息
     * @return 结果
     */
    public int insertTDeptInfo(TDeptInfo tDeptInfo);

    /**
     * 修改装置信息
     * 
     * @param tDeptInfo 装置信息
     * @return 结果
     */
    public int updateTDeptInfo(TDeptInfo tDeptInfo);

    /**
     * 批量删除装置信息
     * 
     * @param ids 需要删除的装置信息主键集合
     * @return 结果
     */
    public int deleteTDeptInfoByIds(Long[] ids);

    /**
     * 删除装置信息信息
     * 
     * @param id 装置信息主键
     * @return 结果
     */
    public int deleteTDeptInfoById(Long id);
}
