package com.px.rc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.px.rc.mapper.TChapterMapper;
import com.px.rc.domain.TChapter;
import com.px.rc.service.ITChapterService;

/**
 * 章节Service业务层处理
 * 
 * @author 品讯科技
 * @date 2024-08
 */
@Service
public class TChapterServiceImpl implements ITChapterService 
{
    @Autowired
    private TChapterMapper tChapterMapper;

    /**
     * 查询章节
     * 
     * @param id 章节主键
     * @return 章节
     */
    @Override
    public TChapter selectTChapterById(Long id)
    {
        return tChapterMapper.selectTChapterById(id);
    }

    /**
     * 查询章节列表
     * 
     * @param tChapter 章节
     * @return 章节
     */
    @Override
    public List<TChapter> selectTChapterList(TChapter tChapter)
    {
        return tChapterMapper.selectTChapterList(tChapter);
    }

    /**
     * 新增章节
     * 
     * @param tChapter 章节
     * @return 结果
     */
    @Override
    public int insertTChapter(TChapter tChapter)
    {
        return tChapterMapper.insertTChapter(tChapter);
    }

    /**
     * 修改章节
     * 
     * @param tChapter 章节
     * @return 结果
     */
    @Override
    public int updateTChapter(TChapter tChapter)
    {
        return tChapterMapper.updateTChapter(tChapter);
    }

    /**
     * 批量删除章节
     * 
     * @param ids 需要删除的章节主键
     * @return 结果
     */
    @Override
    public int deleteTChapterByIds(Long[] ids)
    {
        return tChapterMapper.deleteTChapterByIds(ids);
    }

    /**
     * 删除章节信息
     * 
     * @param id 章节主键
     * @return 结果
     */
    @Override
    public int deleteTChapterById(Long id)
    {
        return tChapterMapper.deleteTChapterById(id);
    }
}
