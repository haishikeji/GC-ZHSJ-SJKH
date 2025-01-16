package com.px.web.controller.rc;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.px.common.core.domain.entity.SysDept;
import com.px.system.service.ISysDeptService;
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
import com.px.rc.domain.TDeptInfo;
import com.px.rc.service.ITDeptInfoService;
import com.px.common.utils.poi.ExcelUtil;
import com.px.common.core.page.TableDataInfo;
import com.px.common.utils.StringUtils;

/**
 * 装置信息Controller
 * 
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequestMapping("/rc/deptinfo")
public class TDeptInfoController extends BaseController
{
    @Autowired
    private ITDeptInfoService tDeptInfoService;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询装置信息列表
     */
    @PreAuthorize("@ss.hasPermi('rc:deptinfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(TDeptInfo tDeptInfo)
    {
        startPage();
        List<TDeptInfo> list = tDeptInfoService.selectTDeptInfoList(tDeptInfo);
        for (TDeptInfo obj : list) {
            String deptId = obj.getDeptId();
            if (StringUtils.isNotEmpty(deptId)) {
                if (deptId.indexOf(",") != -1) {
                    StringBuffer sb = new StringBuffer();
                    String[] ids = deptId.split(",");
                    for (String id : ids) {
                        SysDept sysDept = deptService.selectDeptById(Long.parseLong(id));
                        sb.append(sysDept.getDeptName()).append(" / ");
                    }
                    obj.setDeptName(sb.toString().substring(0, sb.length() - 3));
                } else {
                    obj.setDeptName(deptService.selectDeptById(Long.parseLong(deptId)).getDeptName());
                }
            }
        }
        return getDataTable(list);
    }

    /**
     * 导出装置信息列表
     */
    @PreAuthorize("@ss.hasPermi('rc:deptinfo:export')")
    @Log(title = "装置信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TDeptInfo tDeptInfo)
    {
        List<TDeptInfo> list = tDeptInfoService.selectTDeptInfoList(tDeptInfo);
        ExcelUtil<TDeptInfo> util = new ExcelUtil<TDeptInfo>(TDeptInfo.class);
        util.exportExcel(response, list, "装置信息数据");
    }

    /**
     * 获取装置信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('rc:deptinfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        TDeptInfo tDeptInfo = tDeptInfoService.selectTDeptInfoById(id);
        String year = tDeptInfo.getYear();
        if (year.length() > 4) {
            tDeptInfo.setYear(year.substring(0, year.indexOf("-")));
        }
        return success(tDeptInfo);
    }

    /**
     * 新增装置信息
     */
    @PreAuthorize("@ss.hasPermi('rc:deptinfo:add')")
    @Log(title = "装置信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TDeptInfo tDeptInfo)
    {
        if (StringUtils.isNull(tDeptInfo.getDeptId()) || "".equals(tDeptInfo.getDeptId())) {
            tDeptInfo.setDeptId(getLoginUser().getDeptId().toString());
        }
        return toAjax(tDeptInfoService.insertTDeptInfo(tDeptInfo));
    }

    /**
     * 修改装置信息
     */
    @PreAuthorize("@ss.hasPermi('rc:deptinfo:edit')")
    @Log(title = "装置信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TDeptInfo tDeptInfo)
    {
        return toAjax(tDeptInfoService.updateTDeptInfo(tDeptInfo));
    }

    /**
     * 删除装置信息
     */
    @PreAuthorize("@ss.hasPermi('rc:deptinfo:remove')")
    @Log(title = "装置信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tDeptInfoService.deleteTDeptInfoByIds(ids));
    }
}
