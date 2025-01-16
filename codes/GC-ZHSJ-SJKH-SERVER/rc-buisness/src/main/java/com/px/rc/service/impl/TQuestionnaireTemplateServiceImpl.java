package com.px.rc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.px.rc.mapper.TQuestionnaireTemplateMapper;
import com.px.rc.domain.TQuestionnaireTemplate;
import com.px.rc.service.ITQuestionnaireTemplateService;

/**
 * 问卷模板Service业务层处理
 * 
 * @author 品讯科技
 * @date 2024-08
 */
@Service
public class TQuestionnaireTemplateServiceImpl implements ITQuestionnaireTemplateService 
{
    @Autowired
    private TQuestionnaireTemplateMapper tQuestionnaireTemplateMapper;

    /**
     * 查询问卷模板
     * 
     * @param id 问卷模板主键
     * @return 问卷模板
     */
    @Override
    public TQuestionnaireTemplate selectTQuestionnaireTemplateById(Long id)
    {
        return tQuestionnaireTemplateMapper.selectTQuestionnaireTemplateById(id);
    }

    /**
     * 查询问卷模板列表
     * 
     * @param tQuestionnaireTemplate 问卷模板
     * @return 问卷模板
     */
    @Override
    public List<TQuestionnaireTemplate> selectTQuestionnaireTemplateList(TQuestionnaireTemplate tQuestionnaireTemplate)
    {
        return tQuestionnaireTemplateMapper.selectTQuestionnaireTemplateList(tQuestionnaireTemplate);
    }

    /**
     * 新增问卷模板
     * 
     * @param tQuestionnaireTemplate 问卷模板
     * @return 结果
     */
    @Override
    public int insertTQuestionnaireTemplate(TQuestionnaireTemplate tQuestionnaireTemplate)
    {
        return tQuestionnaireTemplateMapper.insertTQuestionnaireTemplate(tQuestionnaireTemplate);
    }

    /**
     * 修改问卷模板
     * 
     * @param tQuestionnaireTemplate 问卷模板
     * @return 结果
     */
    @Override
    public int updateTQuestionnaireTemplate(TQuestionnaireTemplate tQuestionnaireTemplate)
    {
        return tQuestionnaireTemplateMapper.updateTQuestionnaireTemplate(tQuestionnaireTemplate);
    }

    /**
     * 批量删除问卷模板
     * 
     * @param ids 需要删除的问卷模板主键
     * @return 结果
     */
    @Override
    public int deleteTQuestionnaireTemplateByIds(Long[] ids)
    {
        return tQuestionnaireTemplateMapper.deleteTQuestionnaireTemplateByIds(ids);
    }

    /**
     * 删除问卷模板信息
     * 
     * @param id 问卷模板主键
     * @return 结果
     */
    @Override
    public int deleteTQuestionnaireTemplateById(Long id)
    {
        return tQuestionnaireTemplateMapper.deleteTQuestionnaireTemplateById(id);
    }
}
