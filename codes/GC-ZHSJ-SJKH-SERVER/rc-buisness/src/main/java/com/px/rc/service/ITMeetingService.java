package com.px.rc.service;

import java.util.List;
import com.px.rc.domain.TMeeting;

/**
 * 会议Service接口
 * 
 * @author 品讯科技
 * @date 2024-08
 */
public interface ITMeetingService 
{
    /**
     * 查询会议
     * 
     * @param id 会议主键
     * @return 会议
     */
    public TMeeting selectTMeetingById(Long id);

    /**
     * 查询会议列表
     * 
     * @param tMeeting 会议
     * @return 会议集合
     */
    public List<TMeeting> selectTMeetingList(TMeeting tMeeting);

    /**
     * 新增会议
     * 
     * @param tMeeting 会议
     * @return 结果
     */
    public int insertTMeeting(TMeeting tMeeting);

    /**
     * 修改会议
     * 
     * @param tMeeting 会议
     * @return 结果
     */
    public int updateTMeeting(TMeeting tMeeting);

    /**
     * 批量删除会议
     * 
     * @param ids 需要删除的会议主键集合
     * @return 结果
     */
    public int deleteTMeetingByIds(Long[] ids);

    /**
     * 删除会议信息
     * 
     * @param id 会议主键
     * @return 结果
     */
    public int deleteTMeetingById(Long id);
}
