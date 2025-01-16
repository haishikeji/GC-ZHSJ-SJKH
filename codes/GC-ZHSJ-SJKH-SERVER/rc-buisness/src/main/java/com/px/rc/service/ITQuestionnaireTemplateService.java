package com.px.rc.service;

import java.util.List;
import com.px.rc.domain.TQuestionnaireTemplate;

/**
 * 问卷模板Service接口
 * 
 * @author 品讯科技
 * @date 2024-08
 */
public interface ITQuestionnaireTemplateService 
{
    /**
     * 查询问卷模板
     * 
     * @param id 问卷模板主键
     * @return 问卷模板
     */
    public TQuestionnaireTemplate selectTQuestionnaireTemplateById(Long id);

    /**
     * 查询问卷模板列表
     * 
     * @param tQuestionnaireTemplate 问卷模板
     * @return 问卷模板集合
     */
    public List<TQuestionnaireTemplate> selectTQuestionnaireTemplateList(TQuestionnaireTemplate tQuestionnaireTemplate);

    /**
     * 新增问卷模板
     * 
     * @param tQuestionnaireTemplate 问卷模板
     * @return 结果
     */
    public int insertTQuestionnaireTemplate(TQuestionnaireTemplate tQuestionnaireTemplate);

    /**
     * 修改问卷模板
     * 
     * @param tQuestionnaireTemplate 问卷模板
     * @return 结果
     */
    public int updateTQuestionnaireTemplate(TQuestionnaireTemplate tQuestionnaireTemplate);

    /**
     * 批量删除问卷模板
     * 
     * @param ids 需要删除的问卷模板主键集合
     * @return 结果
     */
    public int deleteTQuestionnaireTemplateByIds(Long[] ids);

    /**
     * 删除问卷模板信息
     * 
     * @param id 问卷模板主键
     * @return 结果
     */
    public int deleteTQuestionnaireTemplateById(Long id);
}
