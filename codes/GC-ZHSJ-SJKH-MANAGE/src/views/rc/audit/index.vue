<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="装置" prop="deptId">
        <el-select clearable v-model="queryParams.deptId" placeholder="请选择装置">
          <el-option
            v-for="dict in deptOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="年份" prop="year">
        <el-date-picker clearable
                        v-model="queryParams.year"
                        type="year"
                        value-format="yyyy"
                        placeholder="请选择年份">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="预审时间" prop="preAuditTime">
        <el-date-picker clearable
          v-model="queryParams.preAuditTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择预审时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="审计时间" prop="auditTime">
        <el-date-picker clearable
          v-model="queryParams.auditTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择审计时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['rc:audit:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['rc:audit:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['rc:audit:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['rc:audit:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table border v-loading="loading" :data="auditList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="装置" align="center" prop="deptName" width="120"/>
      <el-table-column label="年份" align="center" prop="year" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.year, '{y}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="预审时间" align="center" prop="preAuditTime" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.preAuditTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审计时间" align="center" prop="auditTime" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.auditTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审计结果" align="center" prop="auditResult" width="120"/>
      <el-table-column label="备注" align="center" prop="remarks"/>
      <el-table-column label="审计结果报告" align="center" width="120" fixed="right">
        <template slot-scope="scope">
          <el-button icon="el-icon-folder" style="color:#6e96fa;" circle @click="handleDoc(scope.row , 'audit-result')"></el-button>
        </template>
      </el-table-column>
      <el-table-column label="审计开项整改情况介绍" align="center" width="120" fixed="right">
        <template slot-scope="scope">
          <el-button icon="el-icon-folder" style="color:#6e96fa;" circle @click="handleDoc(scope.row , 'audit-openitem')"></el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['rc:audit:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['rc:audit:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleInfo(scope.row)"
          >问卷详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-time"
            @click="handleProgress(scope.row)"
          >进度跟踪</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改审计记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="年份" prop="year">
          <el-date-picker clearable
                          v-model="form.year"
                          type="year"
                          value-format="yyyy"
                          placeholder="请选择年份">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="预审时间" prop="preAuditTime">
          <el-date-picker clearable
            v-model="form.preAuditTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择预审时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="审计时间" prop="auditTime">
          <el-date-picker clearable
            v-model="form.auditTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择审计时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="审计结果" prop="auditResult">
          <el-input v-model="form.auditResult" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="form.remarks" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="装置" prop="deptId">
          <el-select clearable multiple v-model="form.deptId" placeholder="请选择装置">
            <el-option
              v-for="dict in deptOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 附件对话框 -->
    <el-dialog  :close-on-click-modal="false" v-dialogDrag :title="doc.title" :visible.sync="doc.open" width="800px" append-to-body >
      <el-upload ref="doc"
                 :limit="50"
                 :headers="doc.headers"
                 :action="doc.url + '?pType=' + doc.pType + '&pId=' + doc.pId"
                 :disabled="doc.isUploading"
                 :on-progress="handleFileDocProgress"
                 :on-success="handleFileDocSuccess"
                 :auto-upload="true"
                 drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">
          将文件拖到此处，或
          <em>点击上传</em>
        </div>
      </el-upload>
      <el-table :data="doc.commonfileList" border>
        <el-table-column label="文件名" align="center" prop="fileName" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <a  class="link-type"  @click="handleDownload(scope.row)">
              <span>{{ scope.row.fileName }}</span>
            </a>
          </template>
        </el-table-column>
        <el-table-column label="大小(Kb)" align="center" prop="fileSize" :show-overflow-tooltip="true" width="80" />
        <el-table-column label="上传人" align="center" prop="creator" :show-overflow-tooltip="true" width="120"/>
        <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-download"
              @click="handleDownload(scope.row)"
            >下载</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDeleteDoc(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { listAudit, getAudit, delAudit, addAudit, updateAudit } from "@/api/rc/audit";
import { listDept } from "@/api/system/dept";
import { getToken } from "@/utils/auth";
import { addCommonfile, allFileList, delCommonfile, updateCommonfile } from "@/api/rc/commonfile";

export default {
  name: "Audit",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 审计记录表格数据
      auditList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        year: null,
        preAuditTime: null,
        auditTime: null,
        auditResult: null,
        remarks: null,
        deptId: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      // 装置列表
      deptOptions: [],
      // 附件参数
      doc: {
        file: "",
        // 是否显示弹出层（报告附件）
        open: false,
        // 弹出层标题（报告附件）
        title: "附件",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的用户数据
        updateSupport: 0,
        // 报告附件上传位置编号
        ids: 0,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/rc/commonfile/uploadFile",
        commonfileList: null,
        queryParams: {
          pId: null,
          pType: ''
        },
        pType: '',
        pId: null,
        form: {}
      },
    };
  },
  created() {
    this.getList();
    this.getDeptList();
  },
  methods: {
    /** 附件按钮操作 */
    handleDoc(row , type) {
      var typeName = "";
      if (type === "audit-result"){
        typeName = "审计结果报告";
      } else if (type === "audit-openitem"){
        typeName = "审计开项整改情况介绍";
      }
      this.doc.pType = type
      this.doc.queryParams.pType = type
      this.doc.id = row.id;
      this.doc.title = row.deptName + typeName + "（" + new Date(row.year).getFullYear() + "）";
      this.doc.open = true;
      this.doc.queryParams.pId = row.id
      this.doc.pId = row.id
      this.getFileList();
    },
    getFileList(){
      allFileList(this.doc.queryParams).then(response => {
        this.doc.commonfileList = response;
      });
    },
    //附件上传中处理
    handleFileDocProgress(event, file, fileList) {
      this.doc.file = file;
      this.doc.isUploading = true;
    },
    //附件上传成功处理
    handleFileDocSuccess(response, file, fileList) {
      this.doc.isUploading = false;
      this.$alert(response.msg, '导入结果', { dangerouslyUseHTMLString: true });
      this.getFileList()
    },
    // 文件下载处理
    handleDownload(row) {
      var name = row.fileName;
      var url = row.fileUrl;
      var suffix = url.substring(url.lastIndexOf("."), url.length);
      const a = document.createElement('a')
      a.setAttribute('download', name)
      a.setAttribute('target', '_blank')
      a.setAttribute('href', process.env.VUE_APP_BASE_API + url)
      a.click()
    },
    /** 删除按钮操作 */
    handleDeleteDoc(row) {
      const ids = row.id || this.ids;
      this.$confirm('是否确认删除?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: "warning"
      }).then(function() {
        return delCommonfile(ids);
      }).then(() => {
        this.getFileList()
        this.$message({
          message: '删除成功',
          type: 'success'
        });
      })
    },
    /** 问卷详情 */
    handleInfo(row) {
      this.$router.push({
        path: "/rc/auditinfo",
        query: {
          auditId: row.id
        }
      });
    },
    /** 进度跟踪 */
    handleProgress(row) {
      this.$router.push({
        path: "/rc/progress",
        query: {
          auditId: row.id
        }
      });
    },
    /** 查询审计记录列表 */
    getList() {
      this.loading = true;
      listAudit(this.queryParams).then(response => {
        this.auditList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 查询装置列表 */
    getDeptList() {
      listDept().then(response => {
        let data = response.data;
        for (let i = 0; i < data.length; i++) {
          // 非顶级节点
          if (data[i].parentId !== 0) {
            // 插入装置列表
            this.deptOptions.push({"dictLabel": data[i].deptName, "dictValue": data[i].deptId});
          }
        }
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        year: null,
        preAuditTime: null,
        auditTime: null,
        auditResult: null,
        remarks: null,
        deptId: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加审计记录";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAudit(id).then(response => {
        // 字符串转数组
        if (response.data.deptId != null && response.data.deptId != "") {
          response.data.deptId = response.data.deptId.split(",").map(Number);
        } else {
          response.data.deptId = [];
        }
        this.form = response.data;
        this.open = true;
        this.title = "修改审计记录";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 数组转字符串
          if (this.form.deptId != null) {
            this.form.deptId = this.form.deptId.toString();
          }
          if (this.form.id != null) {
            updateAudit(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAudit(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除审计记录编号为"' + ids + '"的数据项？').then(function() {
        return delAudit(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('rc/audit/export', {
        ...this.queryParams
      }, `audit_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
