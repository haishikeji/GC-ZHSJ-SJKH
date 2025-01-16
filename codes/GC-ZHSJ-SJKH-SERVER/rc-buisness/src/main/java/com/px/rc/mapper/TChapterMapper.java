package com.px.rc.mapper;

import java.util.List;
import com.px.rc.domain.TChapter;

/**
 * 章节Mapper接口
 * 
 * @author 品讯科技
 * @date 2024-08
 */
public interface TChapterMapper 
{
    /**
     * 查询章节
     * 
     * @param id 章节主键
     * @return 章节
     */
    public TChapter selectTChapterById(Long id);

    /**
     * 查询章节列表
     * 
     * @param tChapter 章节
     * @return 章节集合
     */
    public List<TChapter> selectTChapterList(TChapter tChapter);

    /**
     * 新增章节
     * 
     * @param tChapter 章节
     * @return 结果
     */
    public int insertTChapter(TChapter tChapter);

    /**
     * 修改章节
     * 
     * @param tChapter 章节
     * @return 结果
     */
    public int updateTChapter(TChapter tChapter);

    /**
     * 删除章节
     * 
     * @param id 章节主键
     * @return 结果
     */
    public int deleteTChapterById(Long id);

    /**
     * 批量删除章节
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTChapterByIds(Long[] ids);
}
