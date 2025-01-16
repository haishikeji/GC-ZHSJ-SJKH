<template>
  <div class="app-container" style="padding: 0px;">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="问卷类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择问卷类型" clearable>
          <el-option
            v-for="dict in dict.type.t_sec_sub_chap_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="YES/NO/NA" prop="yesNoNa">
        <el-select v-model="queryParams.yesNoNa" placeholder="请选择YES/NO/NA" clearable>
          <el-option
            v-for="dict in dict.type.t_sec_sub_chap_yes_no_na"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="标准" prop="standard">
        <el-select v-model="queryParams.standard" placeholder="请选择标准" clearable>
          <el-option
            v-for="dict in dict.type.t_sec_sub_chap_standard"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="完成情况" prop="completionStatus">
        <el-select v-model="queryParams.completionStatus" placeholder="请选择完成情况" clearable>
          <el-option
            v-for="dict in dict.type.t_sec_sub_chap_completion_status"
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
          plain
          size="mini"
          @click="handleEditChapter"
        >编辑左侧章节</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
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

    <el-table border v-loading="loading" :data="questionnaireList" @selection-change="handleSelectionChange" style="font-size: 12px;">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="年份" align="center" prop="year">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.year, '{y}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="问卷类型" align="center" prop="type">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_sec_sub_chap_type" :value="scope.row.type"/>
        </template>
      </el-table-column>
      <el-table-column label="目录" align="center" prop="directory" width="200" />
      <el-table-column label="序号" align="center" prop="code" />
      <el-table-column label="名称" align="center" prop="name" width="350" />
      <el-table-column label="YES" align="center" prop="yesNoNa">
        <template slot-scope="scope">
          <span>{{scope.row.yesNoNa == 1 ? "√" : ""}}</span>
        </template>
      </el-table-column>
      <el-table-column label="NO" align="center" prop="yesNoNa">
        <template slot-scope="scope">
          <span>{{scope.row.yesNoNa == 2 ? "√" : ""}}</span>
        </template>
      </el-table-column>
      <el-table-column label="NA" align="center" prop="yesNoNa">
        <template slot-scope="scope">
          <span>{{scope.row.yesNoNa == 3 ? "√" : ""}}</span>
        </template>
      </el-table-column>
      <el-table-column label="MinimumStandard" align="center" prop="minimumStandard" width="150" />
      <el-table-column label="Good Practices" align="center" prop="goodPractices" width="150" />
      <el-table-column label="标准" align="center" prop="standard">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_sec_sub_chap_standard" :value="scope.row.standard"/>
        </template>
      </el-table-column>
      <el-table-column label="完成情况" align="center" prop="completionStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_sec_sub_chap_completion_status" :value="scope.row.completionStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="负责人" align="center" prop="personInChargeName" />
      <el-table-column label="审核人" align="center" prop="reviewerName" />
      <el-table-column label="备注" align="center" prop="remarks" width="150" />
      <el-table-column label="标准附件" align="center" width="100" fixed="right">
        <template slot-scope="scope">
          <el-button icon="el-icon-folder" style="color:#6e96fa;" circle @click="handleDoc(scope.row , 'questionnaire-standard')"></el-button>
        </template>
      </el-table-column>
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
            @click="handleOpenitem(scope.row)"
          >开项</el-button>
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

    <!-- 添加或修改问卷对话框 -->
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
        <el-form-item label="问卷类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择问卷类型">
            <el-option
              v-for="dict in dict.type.t_sec_sub_chap_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="目录" prop="directory">
          <el-input v-model="form.directory" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="序号" prop="code">
          <el-input v-model="form.code" placeholder="请输入序号" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="" prop="yesNoNa">
          <el-radio-group v-model="form.yesNoNa">
            <el-radio
              v-for="dict in dict.type.t_sec_sub_chap_yes_no_na"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="Minimum Standard" prop="minimumStandard">
          <el-input v-model="form.minimumStandard" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="Good Practices" prop="goodPractices">
          <el-input v-model="form.goodPractices" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="标准" prop="standard">
          <el-select v-model="form.standard" placeholder="请选择标准">
            <el-option
              v-for="dict in dict.type.t_sec_sub_chap_standard"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="完成情况" prop="completionStatus">
          <el-select v-model="form.completionStatus" placeholder="请选择完成情况">
            <el-option
              v-for="dict in dict.type.t_sec_sub_chap_completion_status"
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
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="form.remarks" type="textarea" placeholder="请输入内容" />
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

    <!-- 添加或修改开项对话框 -->
    <el-dialog :title="openitemTitle" :visible.sync="openitemOpen" width="500px" append-to-body>
      <el-form ref="openitemForm" :model="openitemForm" :rules="openitemRules" label-width="120px">
        <el-form-item label="问题类型" prop="type">
          <el-select v-model="openitemForm.type" placeholder="请选择问题类型">
            <el-option
              v-for="dict in dict.type.t_open_item_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="存在问题描述" prop="description">
          <el-input v-model="openitemForm.description" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="开项级别" prop="level">
          <el-select v-model="openitemForm.level" placeholder="请选择开项级别">
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
                          v-model="openitemForm.openTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择开项时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="闭项时间" prop="closeTime">
          <el-date-picker clearable
                          v-model="openitemForm.closeTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择闭项时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="问题处理状态" prop="result">
          <el-select v-model="openitemForm.status" placeholder="请选择问题处理状态">
            <el-option
              v-for="dict in dict.type.t_open_item_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="负责人" prop="personInCharge">
          <el-select clearable v-model="openitemForm.personInCharge" placeholder="请选择负责人">
            <el-option
              v-for="dict in userOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="审核人" prop="reviewer">
          <el-select clearable v-model="openitemForm.reviewer" placeholder="请选择审核人">
            <el-option
              v-for="dict in userOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="责任人确认" prop="result">
          <el-select v-model="openitemForm.result" placeholder="请选择责任人确认">
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
                          v-model="openitemForm.deadline"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择截至时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="openitemForm.remarks" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitOpenitemForm">确 定</el-button>
        <el-button @click="cancelOpenitem">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listQuestionnaire, getQuestionnaire, delQuestionnaire, addQuestionnaire, updateQuestionnaire } from "@/api/rc/questionnaire";
import { listDept } from "@/api/system/dept";
import { listAllUser } from "@/api/system/user";
import { getToken } from "@/utils/auth";
import { addCommonfile, allFileList, delCommonfile, updateCommonfile } from "@/api/rc/commonfile";
import { listOpenitem, getOpenitem, delOpenitem, addOpenitem, updateOpenitem } from "@/api/rc/openitem";

export default {
  name: "Questionnaire",
  dicts: ['t_open_item_result', 't_open_item_type', 't_open_item_level', 't_open_item_status','t_sec_sub_chap_completion_status', 't_sec_sub_chap_yes_no_na', 't_sec_sub_chap_type', 't_sec_sub_chap_standard'],
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
      // 问卷表格数据
      questionnaireList: [],
      // 弹出层标题
      title: "",
      openitemTitle: "",
      // 是否显示弹出层
      open: false,
      openitemOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        auditId: null,
        chapterId: null,
        year: null,
        type: null,
        directory: null,
        code: null,
        name: null,
        yesNoNa: null,
        minimumStandard: null,
        goodPractices: null,
        standard: null,
        completionStatus: null,
        personInCharge: null,
        reviewer: null,
        remarks: null,
        deptId: null
      },
      // 表单参数
      form: {},
      openitemForm: {},
      // 表单校验
      rules: {
      },
      openitemRules: {
      },
      // 用户列表
      userOptions: [],
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
    // this.getList();
    this.getUserList();
  },
  methods: {
    /** 开项按钮操作 */
    handleOpenitem(row) {
      this.resetOpenitem();
      this.openitemOpen = true;
      this.openitemTitle = "添加开项";
      this.openitemForm.deptId = row.deptId;
      this.openitemForm.auditId = row.auditId;
      this.openitemForm.questionnaireId = row.id;
      this.openitemForm.code = row.code;
    },
    /** 开项提交按钮 */
    submitOpenitemForm() {
      this.$refs["openitemForm"].validate(valid => {
        if (valid) {
          addOpenitem(this.openitemForm).then(response => {
            this.$modal.msgSuccess("新增开项成功");
            this.openitemOpen = false;
          });
        }
      });
    },
    // 取消按钮
    cancelOpenitem() {
      this.openitemOpen = false;
      this.resetOpenitem();
    },
    // 开项表单重置
    resetOpenitem() {
      this.openitemForm = {
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
      this.resetForm("openitemForm");
    },
    /** 附件按钮操作 */
    handleDoc(row , type) {
      var typeName = "";
      if (type === "questionnaire-standard"){
        typeName = "标准附件";
        this.doc.pType = type
        this.doc.queryParams.pType = type
        this.doc.id = row.id;
        this.doc.title = "标准附件（CODE " + row.code + "）";
        this.doc.open = true;
        this.doc.queryParams.pId = row.id
        this.doc.pId = row.id
        this.getFileList();
      } else if (type === 'audit') {
        this.$router.push({path: '/rc/file', query: {linkId: row.id, linkName: 'questionnaire'}})
      }
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
    /** 查询用户列表 */
    getUserList() {
      listAllUser().then(response => {
        let data = response.data;
        for (let i = 0; i < data.length; i++) {
          // 非顶级节点
          if (data[i].parentId !== 0) {
            // 插入用户列表
            this.userOptions.push({"dictLabel": data[i].nickName, "dictValue": data[i].userId});
          }
        }
      });
    },
    /** 编辑左侧章节 */
    handleEditChapter() {
      this.$router.push({
        path: "/rc/editchapter",
        query: {
          auditId: this.queryParams.auditId
        }
      });
    },
    /** 查询问卷列表 */
    getList(auditId, chapterId) {
      this.loading = true;
      if (chapterId != null) {
        this.queryParams.chapterId = chapterId;
      }
      if (auditId != null) {
        this.queryParams.auditId = auditId;
      }
      listQuestionnaire(this.queryParams).then(response => {
        this.questionnaireList = response.rows;
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
        chapterId: null,
        year: null,
        type: null,
        directory: null,
        code: null,
        name: null,
        yesNoNa: null,
        minimumStandard: null,
        goodPractices: null,
        standard: null,
        completionStatus: null,
        personInCharge: null,
        reviewer: null,
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
      this.title = "添加问卷";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getQuestionnaire(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改问卷";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateQuestionnaire(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addQuestionnaire(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除问卷编号为"' + ids + '"的数据项？').then(function() {
        return delQuestionnaire(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('rc/questionnaire/export', {
        ...this.queryParams
      }, `questionnaire_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
