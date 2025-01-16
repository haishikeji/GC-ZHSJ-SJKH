<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="问题类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择问题类型" clearable>
          <el-option
            v-for="dict in dict.type.t_open_item_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="涉及code" prop="questionnaireId">
        <el-input
          v-model="queryParams.questionnaireId"
          placeholder="请输入涉及整改code"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="开项级别" prop="level">
        <el-select v-model="queryParams.level" placeholder="请选择开项级别" clearable>
          <el-option
            v-for="dict in dict.type.t_open_item_level"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="开项时间" prop="openTime">
        <el-date-picker clearable
          v-model="queryParams.openTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择开项时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="闭项时间" prop="closeTime">
        <el-date-picker clearable
          v-model="queryParams.closeTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择闭项时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="处理状态" prop="result">
        <el-select v-model="queryParams.status" placeholder="请选择开项问题处理状态">
          <el-option
            v-for="dict in dict.type.t_open_item_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="负责人" prop="personInCharge">
        <el-select clearable v-model="queryParams.personInCharge" placeholder="请选择负责人">
          <el-option
            v-for="dict in userOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="审核人" prop="reviewer">
        <el-select clearable v-model="queryParams.reviewer" placeholder="请选择审核人">
          <el-option
            v-for="dict in userOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="责任人" prop="result">
        <el-select v-model="queryParams.result" placeholder="请选择责任人确认" clearable>
          <el-option
            v-for="dict in dict.type.t_open_item_result"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="截至时间" prop="deadline">
        <el-date-picker clearable
          v-model="queryParams.deadline"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择截至时间">
        </el-date-picker>
      </el-form-item>
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
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <!--<el-col :span="1.5">-->
        <!--<el-button-->
          <!--type="primary"-->
          <!--plain-->
          <!--icon="el-icon-plus"-->
          <!--size="mini"-->
          <!--@click="handleAdd"-->
          <!--v-hasPermi="['rc:openitem:add']"-->
        <!--&gt;新增</el-button>-->
      <!--</el-col>-->
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['rc:openitem:edit']"
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
          v-hasPermi="['rc:openitem:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['rc:openitem:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table border v-loading="loading" :data="openitemList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="装置" align="center" prop="deptName" width="120"/>
      <el-table-column label="问题类型" align="center" prop="type">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_open_item_type" :value="scope.row.type"/>
        </template>
      </el-table-column>
      <el-table-column label="涉及整改code" align="center" prop="questionnaireId" />
      <el-table-column label="存在问题描述" align="center" prop="description" width="200" />
      <el-table-column label="开项级别" align="center" prop="level">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_open_item_level" :value="scope.row.level"/>
        </template>
      </el-table-column>
      <el-table-column label="开项时间" align="center" prop="openTime" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.openTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="闭项时间" align="center" prop="closeTime" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.closeTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="问题处理状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_open_item_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="负责人" align="center" prop="personInChargeName" />
      <el-table-column label="审核人" align="center" prop="reviewerName" />
      <el-table-column label="责任人确认" align="center" prop="result">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_open_item_result" :value="scope.row.result"/>
        </template>
      </el-table-column>
      <el-table-column label="截至时间" align="center" prop="deadline" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.deadline, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remarks" width="200"/>
      <el-table-column label="审计文档" align="center" width="100" fixed="right">
        <template slot-scope="scope">
          <el-button icon="el-icon-folder" style="color:#6e96fa;" circle @click="handleDoc(scope.row , 'audit')"></el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="120" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['rc:openitem:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['rc:openitem:remove']"
          >删除</el-button>
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

    <!-- 添加或修改开项对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="问题类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择问题类型">
            <el-option
              v-for="dict in dict.type.t_open_item_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="涉及整改code" prop="questionnaireId">
          <el-input v-model="form.questionnaireId" placeholder="请输入涉及整改code" />
        </el-form-item>
        <el-form-item label="存在问题描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="开项级别" prop="level">
          <el-select v-model="form.level" placeholder="请选择开项级别">
            <el-option
              v-for="dict in dict.type.t_open_item_level"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="开项时间" prop="openTime">
          <el-date-picker clearable
            v-model="form.openTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择开项时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="闭项时间" prop="closeTime">
          <el-date-picker clearable
            v-model="form.closeTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择闭项时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="问题处理状态" prop="result">
          <el-select v-model="form.status" placeholder="请选择问题处理状态">
            <el-option
              v-for="dict in dict.type.t_open_item_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
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
        <el-form-item label="审核人" prop="reviewer">
          <el-select clearable v-model="form.reviewer" placeholder="请选择审核人">
            <el-option
              v-for="dict in userOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="责任人确认" prop="result">
          <el-select v-model="form.result" placeholder="请选择责任人确认">
            <el-option
              v-for="dict in dict.type.t_open_item_result"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="截至时间" prop="deadline">
          <el-date-picker clearable
            v-model="form.deadline"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择截至时间">
          </el-date-picker>
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
  </div>
</template>

<script>
import { listOpenitem, getOpenitem, delOpenitem, addOpenitem, updateOpenitem } from "@/api/rc/openitem";
import { listDept } from "@/api/system/dept";
import { listAllUser } from "@/api/system/user";

export default {
  name: "Openitem",
  dicts: ['t_open_item_result', 't_open_item_type', 't_open_item_level', 't_open_item_status'],
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
      // 开项表格数据
      openitemList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        type: null,
        questionnaireId: null,
        description: null,
        level: null,
        openTime: null,
        closeTime: null,
        status: null,
        personInCharge: null,
        reviewer: null,
        result: null,
        deadline: null,
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
      // 用户列表
      userOptions: [],
    };
  },
  created() {
    this.getList();
    this.getDeptList();
    this.getUserList();
  },
  methods: {
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
    /** 查询开项列表 */
    getList() {
      this.loading = true;
      listOpenitem(this.queryParams).then(response => {
        this.openitemList = response.rows;
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
        type: null,
        questionnaireId: null,
        description: null,
        level: null,
        openTime: null,
        closeTime: null,
        status: null,
        personInCharge: null,
        reviewer: null,
        result: null,
        deadline: null,
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
      this.title = "添加开项";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getOpenitem(id).then(response => {
        // 字符串转数组
        if (response.data.deptId != null && response.data.deptId != "") {
            response.data.deptId = response.data.deptId.split(",").map(Number);
        } else {
            response.data.deptId = [];
        }
        this.form = response.data;
        this.open = true;
        this.title = "修改开项";
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
            updateOpenitem(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addOpenitem(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除开项编号为"' + ids + '"的数据项？').then(function() {
        return delOpenitem(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('rc/openitem/export', {
        ...this.queryParams
      }, `openitem_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
