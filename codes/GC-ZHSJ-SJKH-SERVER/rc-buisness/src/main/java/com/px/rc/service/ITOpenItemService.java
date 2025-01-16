package com.px.rc.service;

import java.util.List;
import com.px.rc.domain.TOpenItem;

/**
 * 开项Service接口
 * 
 * @author 品讯科技
 * @date 2024-08
 */
public interface ITOpenItemService 
{
    /**
     * 查询开项
     * 
     * @param id 开项主键
     * @return 开项
     */
    public TOpenItem selectTOpenItemById(Long id);

    /**
     * 查询开项列表
     * 
     * @param tOpenItem 开项
     * @return 开项集合
     */
    public List<TOpenItem> selectTOpenItemList(TOpenItem tOpenItem);

    /**
     * 新增开项
     * 
     * @param tOpenItem 开项
     * @return 结果
     */
    public int insertTOpenItem(TOpenItem tOpenItem);

    /**
     * 修改开项
     * 
     * @param tOpenItem 开项
     * @return 结果
     */
    public int updateTOpenItem(TOpenItem tOpenItem);

    /**
     * 批量删除开项
     * 
     * @param ids 需要删除的开项主键集合
     * @return 结果
     */
    public int deleteTOpenItemByIds(Long[] ids);

    /**
     * 删除开项信息
     * 
     * @param id 开项主键
     * @return 结果
     */
    public int deleteTOpenItemById(Long id);
}
