package com.px.web.controller.rc;

import com.px.system.service.ISysDeptService;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.px.common.annotation.Log;
import com.px.common.core.controller.BaseController;
import com.px.common.core.domain.AjaxResult;
import com.px.common.enums.BusinessType;
import com.px.rc.domain.TChapterTemplate;
import com.px.rc.service.ITChapterTemplateService;
import com.px.common.utils.poi.ExcelUtil;
import com.px.common.core.page.TableDataInfo;
import com.px.common.utils.StringUtils;

/**
 * 章节模板Controller
 * 
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequestMapping("/rc/chaptertemplate")
public class TChapterTemplateController extends BaseController
{
    @Autowired
    private ITChapterTemplateService tChapterTemplateService;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询全部章节模板列表
     */
    @PreAuthorize("@ss.hasPermi('rc:chaptertemplate:list')")
    @GetMapping("/listAll")
    public AjaxResult listAll(TChapterTemplate tChapterTemplate)
    {
        return success(tChapterTemplateService.selectTChapterTemplateList(tChapterTemplate));
    }

    /**
     * 查询章节模板列表
     */
    @PreAuthorize("@ss.hasPermi('rc:chaptertemplate:list')")
    @GetMapping("/list")
    public TableDataInfo list(TChapterTemplate tChapterTemplate)
    {
        startPage();
        List<TChapterTemplate> list = tChapterTemplateService.selectTChapterTemplateList(tChapterTemplate);
        return getDataTable(list);
    }

    /**
     * 导出章节模板列表
     */
    @PreAuthorize("@ss.hasPermi('rc:chaptertemplate:export')")
    @Log(title = "章节模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TChapterTemplate tChapterTemplate)
    {
        List<TChapterTemplate> list = tChapterTemplateService.selectTChapterTemplateList(tChapterTemplate);
        ExcelUtil<TChapterTemplate> util = new ExcelUtil<TChapterTemplate>(TChapterTemplate.class);
        util.exportExcel(response, list, "章节模板数据");
    }

    /**
     * 获取章节模板详细信息
     */
    @PreAuthorize("@ss.hasPermi('rc:chaptertemplate:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(tChapterTemplateService.selectTChapterTemplateById(id));
    }

    /**
     * 新增章节模板
     */
    @PreAuthorize("@ss.hasPermi('rc:chaptertemplate:add')")
    @Log(title = "章节模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TChapterTemplate tChapterTemplate)
    {
        if (StringUtils.isNull(tChapterTemplate.getDeptId()) || "".equals(tChapterTemplate.getDeptId())) {
            tChapterTemplate.setDeptId(getLoginUser().getDeptId().toString());
        }
        return toAjax(tChapterTemplateService.insertTChapterTemplate(tChapterTemplate));
    }

    /**
     * 修改章节模板
     */
    @PreAuthorize("@ss.hasPermi('rc:chaptertemplate:edit')")
    @Log(title = "章节模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TChapterTemplate tChapterTemplate)
    {
        return toAjax(tChapterTemplateService.updateTChapterTemplate(tChapterTemplate));
    }

    /**
     * 删除章节模板
     */
    @PreAuthorize("@ss.hasPermi('rc:chaptertemplate:remove')")
    @Log(title = "章节模板", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tChapterTemplateService.deleteTChapterTemplateByIds(ids));
    }
}
