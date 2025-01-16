<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="负责人" prop="personInCharge">
        <el-select clearable v-model="queryParams.personInCharge" placeholder="请选择负责人"
                   @keyup.enter.native="handleQuery">
          <el-option
            v-for="dict in userOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="支持人" prop="supporter"
                    @keyup.enter.native="handleQuery">
        <el-select clearable v-model="queryParams.supporter" placeholder="请选择支持人">
          <el-option
            v-for="dict in userOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="开始日期" prop="startDate">
        <el-date-picker clearable
          v-model="queryParams.startDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择开始日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="目标日期" prop="targetDate">
        <el-date-picker clearable
          v-model="queryParams.targetDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择目标日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="实际完成日期" prop="finishDate">
        <el-date-picker clearable
          v-model="queryParams.finishDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择实际完成日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="准备情况" prop="preparation">
        <el-select v-model="queryParams.preparation" placeholder="请选择准备情况" clearable>
          <el-option
            v-for="dict in dict.type.t_progress_preparation"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="进度描述" prop="progress">
        <el-select v-model="queryParams.progress" placeholder="请选择进度描述" clearable>
          <el-option
            v-for="dict in dict.type.t_progress_description"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="审批状态" prop="applyStatus">
        <el-select v-model="queryParams.applyStatus" placeholder="请选择审批状态" clearable>
          <el-option
            v-for="dict in dict.type.t_progress_apply_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
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
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table border v-loading="loading" :data="progressList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="章节" align="center" prop="chapName" width="200" />
      <el-table-column label="细分章节" align="center" prop="subChapName" width="200" />
      <el-table-column label="二级细分章节" align="center" prop="secSubChapName" width="200" />
      <el-table-column label="序号" align="center" prop="code" width="80" />
      <el-table-column label="名称" align="center" prop="name" width="350" />
      <el-table-column label="内容" align="center" prop="content" width="200" />
      <el-table-column label="负责人" align="center" prop="personInChargeName" width="100" />
      <el-table-column label="支持人" align="center" prop="supporterName" width="100" />
      <el-table-column label="开始日期" align="center" prop="startDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>Search
      <el-table-column label="目标日期" align="center" prop="targetDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.targetDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="实际完成日期" align="center" prop="finishDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.finishDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="准备情况" align="center" prop="preparation" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_progress_preparation" :value="scope.row.preparation"/>
        </template>
      </el-table-column>
      <el-table-column label="进度描述" align="center" prop="progress" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_progress_description" :value="scope.row.progress"/>
        </template>
      </el-table-column>
      <el-table-column label="审批状态" align="center" prop="applyStatus" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_progress_apply_status" :value="scope.row.applyStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remarks" width="200" />
      <el-table-column label="审计文档" align="center" width="100" fixed="right">
        <template slot-scope="scope">
          <el-button icon="el-icon-folder" style="color:#6e96fa;" circle @click="handleDoc(scope.row , 'audit')"></el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            @click="handleApply(scope.row)"
            v-if="(scope.row.applyStatus == 1 || scope.row.applyStatus == 3) && scope.row.preparation == 6"
          >提交申请</el-button>
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

    <!-- 添加或修改进度对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="章节" prop="chapName">
          <el-input v-model="form.chapName" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="细分章节" prop="subChapName">
          <el-input v-model="form.subChapName" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="二级细分章节" prop="secSubChapName">
          <el-input v-model="form.secSubChapName" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="负责人" prop="personInCharge">
          <el-select clearable v-model="form.personInCharge" placeholder="请选择负责人">
            <el-option
              v-for="dict in userOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="支持人" prop="supporter">
          <el-select clearable v-model="form.supporter" placeholder="请选择支持人">
            <el-option
              v-for="dict in userOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker clearable
            v-model="form.startDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择开始日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="目标日期" prop="targetDate">
          <el-date-picker clearable
            v-model="form.targetDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择目标日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="实际完成日期" prop="finishDate">
          <el-date-picker clearable
            v-model="form.finishDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择实际完成日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="准备情况" prop="preparation">
          <el-select v-model="form.preparation" placeholder="请选择准备情况">
            <el-option
              v-for="dict in dict.type.t_progress_preparation"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="进度描述" prop="progress">
          <el-select v-model="form.progress" placeholder="请选择进度描述">
            <el-option
              v-for="dict in dict.type.t_progress_description"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="form.remarks" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listProgress, getProgress, delProgress, addProgress, updateProgress } from "@/api/rc/progress";
import { applyQuestionnaire } from "@/api/rc/questionnaire";
import { listAllUser } from "@/api/system/user";

export default {
  name: "Progress",
  dicts: ['t_progress_preparation', 't_progress_description', 't_progress_apply_status'],
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
      showSearch: false,
      // 总条数
      total: 0,
      // 进度表格数据
      progressList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        chapName: null,
        subChapName: null,
        secSubChapName: null,
        content: null,
        personInCharge: null,
        supporter: null,
        startDate: null,
        targetDate: null,
        finishDate: null,
        preparation: null,
        description: null,
        applyStatus: null,
        remarks: null,
        deptId: null,
        auditId: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      // 用户列表
      userOptions: [],
    };
  },
  created() {
    this.getList();
    this.getUserList();
  },
  methods: {
    /** 提交申请按钮操作 */
    handleApply(row) {
      this.$confirm('是否确认提交?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: "warning"
      }).then(function() {
        return applyQuestionnaire(row);
      }).then(() => {
        this.getList()
        this.$message({
          message: '提交成功',
          type: 'success'
        });
      })
    },
    handleDoc(row , type) {
      this.$router.push({path: '/rc/file', query: {linkId: row.questionnaireId, linkName: 'questionnaire'}})
    },
    /** 查询用户列表 */
    getUserList() {
      listAllUser().then(response => {
        let data = response.data;
        for (let i = 0; i < data.length; i++) {
          // 非顶级节点
          if (data[i].parentId !== 0) {
            // 插入装置列表
            this.userOptions.push({"dictLabel": data[i].nickName, "dictValue": data[i].userId});
          }
        }
      });
    },
    /** 查询进度列表 */
    getList() {
      this.loading = true;
      this.queryParams.auditId = this.$route.query.auditId;
      listProgress(this.queryParams).then(response => {
        this.progressList = response.rows;
        this.total = response.total;
        this.loading = false;
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
        chapName: null,
        subChapName: null,
        secSubChapName: null,
        content: null,
        personInCharge: null,
        supporter: null,
        startDate: null,
        targetDate: null,
        finishDate: null,
        preparation: null,
        description: null,
        applyStatus: null,
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
      this.title = "添加进度";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getProgress(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改进度";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateProgress(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addProgress(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除进度编号为"' + ids + '"的数据项？').then(function() {
        return delProgress(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('rc/progress/export', {
        ...this.queryParams
      }, `progress_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
