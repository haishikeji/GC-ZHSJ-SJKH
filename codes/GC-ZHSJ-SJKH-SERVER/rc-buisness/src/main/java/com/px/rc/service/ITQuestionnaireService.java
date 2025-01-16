package com.px.rc.service;

import java.util.List;
import com.px.rc.domain.TQuestionnaire;

/**
 * 问卷Service接口
 * 
 * @author 品讯科技
 * @date 2024-08
 */
public interface ITQuestionnaireService 
{
    /**
     * 查询问卷
     * 
     * @param id 问卷主键
     * @return 问卷
     */
    public TQuestionnaire selectTQuestionnaireById(Long id);

    /**
     * 查询问卷列表
     * 
     * @param tQuestionnaire 问卷
     * @return 问卷集合
     */
    public List<TQuestionnaire> selectTQuestionnaireList(TQuestionnaire tQuestionnaire);

    /**
     * 新增问卷
     * 
     * @param tQuestionnaire 问卷
     * @return 结果
     */
    public int insertTQuestionnaire(TQuestionnaire tQuestionnaire);

    /**
     * 修改问卷
     * 
     * @param tQuestionnaire 问卷
     * @return 结果
     */
    public int updateTQuestionnaire(TQuestionnaire tQuestionnaire);

    /**
     * 批量删除问卷
     * 
     * @param ids 需要删除的问卷主键集合
     * @return 结果
     */
    public int deleteTQuestionnaireByIds(Long[] ids);

    /**
     * 删除问卷信息
     * 
     * @param id 问卷主键
     * @return 结果
     */
    public int deleteTQuestionnaireById(Long id);
}
