<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="文件名" prop="fileName">
        <el-input
          v-model="queryParams.fileName"
          placeholder="请输入文件名"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
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
          icon="el-icon-folder-add"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['file:file:add']"
        >新建文件夹
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          icon="el-icon-upload2"
          size="mini"
          v-hasPermi="['file:file:add']"
          @click="handleAddFile"
        >上传文件
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      ref="fileTable"
      v-loading="loading"
      :data="fileList"
      row-key="id"
      default-expand-all
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
      @row-click="rowClick"
    >
      <el-table-column label="文件名" width="500" align="left" prop="fileName" :show-overflow-tooltip="true">
        <template slot-scope="scope">

          <svg-icon style="font-size: 200%;" v-if="scope.row.type == '1'" iconClass="excel_1"/>
          <svg-icon style="font-size: 200%;" v-else-if="scope.row.type == '2'" iconClass="ppt"/>
          <svg-icon style="font-size: 200%;" v-else-if="scope.row.type == '4'" iconClass="pdf_1"/>
          <svg-icon style="font-size: 200%;" v-else-if="scope.row.type == '3'" iconClass="word"/>
          <svg-icon style="font-size: 200%;" v-else-if="scope.row.type == '5'" iconClass="txt"/>
          <svg-icon style="font-size: 200%;" v-else-if="scope.row.type == '0'" iconClass="folder"/>
          <el-image
            v-else-if="scope.row.type == '6'"
            style="border-radius:5px; padding:5px 5px 0 0;width: 30px; height: 30px"
            :src="getImgUrl(scope.row.fileUrl)"
            :preview-src-list="[getImgUrl(scope.row.fileUrl)]">
          </el-image>
          <svg-icon style="font-size: 200%;" v-else iconClass="unknown"/>
          <span v-if="scope.row.type == 0">{{ scope.row.fileName }}</span>
          <a v-else class="link-type" @click="handleFileView(scope.row)">
            <span>{{ scope.row.fileName }}</span>
          </a>
        </template>
      </el-table-column>
      <!--      <el-table-column label="类型" width="80" align="center" prop="type">
              <template slot-scope="scope">
                <svg-icon style="font-size: 200%;" v-if="scope.row.type == '1'" iconClass="excel_1"/>
                <svg-icon style="font-size: 200%;" v-else-if="scope.row.type == '2'" iconClass="ppt"/>
                <svg-icon style="font-size: 200%;" v-else-if="scope.row.type == '4'" iconClass="pdf_1"/>
                <svg-icon style="font-size: 200%;" v-else-if="scope.row.type == '3'" iconClass="word"/>
                <svg-icon style="font-size: 200%;" v-else-if="scope.row.type == '5'" iconClass="txt"/>
                <svg-icon style="font-size: 200%;" v-else-if="scope.row.type == '0'" iconClass="folder"/>
                <el-image
                  v-else-if="scope.row.type == '6'"
                  style="border-radius:5px; padding:5px 5px 0 0;width: 30px; height: 30px"
                  :src="getImgUrl(scope.row.fileUrl)"
                  :preview-src-list="[getImgUrl(scope.row.fileUrl)]">
                </el-image>
                <svg-icon style="font-size: 200%;" v-else iconClass="unknown"/>
              </template>
            </el-table-column>-->

      <el-table-column label="创建人" align="center" prop="uploader"/>
      <el-table-column label="创建时间" align="center" prop="uploadDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.uploadDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="文件大小(Kb)" width="100" align="center" prop="fileSize"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.type != 0"
            size="mini"
            type="text"
            icon="el-icon-download"
            @click="handleDownload(scope.row)"
          >下载
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            v-hasPermi="['file:file:edit']"
            @click="handleUpdate(scope.row)"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            v-hasPermi="['file:file:remove']"
            @click="handleDelete(scope.row)"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!--    <el-dialog :close-on-click-modal="false" v-dialogDrag :title="pdf.title" :visible.sync="pdf.open" width="1300px"-->
    <!--               append-to-body>-->
    <!--      <div style="margin-top: -60px;float: right;margin-right: 40px;">-->
    <!--        <el-button size="mini" type="text" @click="openPdf">新页面打开PDF</el-button>-->
    <!--      </div>-->
    <!--      <div style="margin-top: -30px">-->
    <!--        <iframe :src="pdf.pdfUrl" frameborder="0" width="100%" height="700px"></iframe>-->
    <!--      </div>-->
    <!--    </el-dialog>-->
    <!-- 添加或修改附件对话框 -->
    <el-dialog :title="doc.title" :visible.sync="doc.open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">

        <el-form-item label="上级" prop="pId">
          <treeselect v-model="form.pId" :options="fileOptions" :normalizer="normalizer"
                      placeholder="请选择文件夹"/>
        </el-form-item>
        <el-upload style="text-align: center;"
                   ref="doc"
                   :limit="50"
                   :headers="doc.headers"
                   :action="doc.url + '?linkId=' + this.$route.query.linkId + '&linkName=' + this.$route.query.linkName + '&pId=' + this.form.pId"
                   :disabled="doc.isUploading"
                   :on-progress="handleFileDocProgress"
                   :on-success="handleFileDocSuccess"
                   :auto-upload="true"
                   multiple
                   drag
        >
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">
            将文件拖到此处，或
            <em>点击上传</em>
          </div>
        </el-upload>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFile">确 定</el-button>
        <el-button @click="doc.open = false">返 回</el-button>
      </div>
    </el-dialog>
    <!-- 添加或修改附件对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item v-if="form.type != 1" label="文件夹" prop="fileName">
          <el-input v-model="form.fileName" placeholder="请输入文件夹名"/>
        </el-form-item>

        <el-form-item label="上级" prop="pId">
          <treeselect v-model="form.pId" :options="fileOptions" :normalizer="normalizer" placeholder="请选择文件夹"/>
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
import {addFile, delFile, getFile, listFile, updateFile} from "@/api/rc/file";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import {getToken} from "@/utils/auth";

export default {
  name: "uploadFile",
  components: {
    Treeselect
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: false,
      // 附件表格数据
      fileList: [],
      // 附件树选项
      fileOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      pId: null,
      // 查询参数
      queryParams: {
        linkId: this.$route.query.linkId,
        linkName: this.$route.query.linkName,
        fileName: null,
        pId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        id: [
          {required: true, message: "id不能为空", trigger: "blur"}
        ],
      },
      pdf: {
        title: '',
        pdfUrl: '',
        numPages: null,
        open: false,
        pageNum: 1,
        pageTotalNum: 1,
        loadedRatio: 0,
      },
      // 报告附件参数
      doc: {
        file: "",
        // 是否显示弹出层（报告附件）
        open: false,
        // 弹出层标题（报告附件）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的用户数据
        updateSupport: 0,
        // 报告附件上传位置编号
        ids: 0,
        // 设置上传的请求头部
        headers: {Authorization: "Bearer " + getToken()},
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/file/file/uploadFile",
        commonfileList: null,
      },
    };
  },
  created() {
    console.log(this.$route.query)
    if (this.$route.query) {
      this.queryParams.linkId = this.$route.query.linkId
      this.queryParams.linkName = this.$route.query.linkName
    }
    this.getList();
  },
  methods: {
    rowClick(row, column, event) {
      this.$refs.fileTable.toggleRowExpansion(row);
    },
    getImgUrl(url) {
      return process.env.VUE_APP_BASE_API + url;
    },
    /** 查询附件列表 */
    getList() {
      this.loading = true;
      listFile(this.queryParams).then(response => {
        this.fileList = this.handleTree(response.data, "id", "pId");
        this.loading = false;
      });
    },
    /** 转换附件数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children;
      }
      return {
        id: node.id,
        label: node.fileName,
        children: node.children
      };
    },
    /** 查询部门下拉树结构 */
    getTreeselect() {
      let param = {
        linkId: this.$route.query.linkId,
        linkName: this.$route.query.linkName,
      }
      param.type = 0
      listFile(param).then(response => {
        this.fileOptions = [];
        const data = {id: 0, fileName: '顶级节点', children: []};
        data.children = this.handleTree(response.data, "id", "pId");
        this.fileOptions.push(data);
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
        pId: null,
        fileName: null,
        fileUrl: null,
        delFlag: null,
        remarks: null,
        fileSize: null,
        type: null,
        linkId: this.$route.query.linkId,
        linkName: this.$route.query.linkName
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.getTreeselect();
      this.open = true;
      this.title = "添加附件";
      this.form.type = '0';
      this.form.pId = 0;
    },
    handleAddFile() {
      this.reset();
      this.getTreeselect();
      this.form.type = '1';
      this.form.pId = 0;
      this.doc.open = true;
      this.$nextTick(() => {
        this.$refs.doc.clearFiles()
      })
      this.doc.title = "添加附件";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.getTreeselect();
      getFile(row.id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改附件";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateFile(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addFile(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    submitFile() {
      this.doc.open = false;
      this.getList();
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
    handleDelete(row) {
      this.$confirm('是否确认删除附件名为"' + row.fileName + '"的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function () {
        return delFile(row.id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      })
    },
    //附件上传中处理
    handleFileDocProgress(event, file, fileList) {
      this.doc.file = file;
      this.doc.isUploading = true;
    },
    //附件上传成功处理
    handleFileDocSuccess(response, file, fileList) {
      this.doc.isUploading = false;
      // this.$alert(response.msg, '导入结果', {dangerouslyUseHTMLString: true});
      this.form.id = this.doc.pId;
      // addFile(this.form)
      this.$refs.upload.clearFiles();
      this.getList()
    },
    handleFileView(row) {
      if (row.type == 1 || row.type == 2 || row.type == 3) {
        this.$router.push({path: '/office/edit', query: {fileId: row.id, ot: 'detail'}});
      } else {
        window.open(process.env.VUE_APP_BASE_API + row.fileUrl);
      }
    }
  }
};
</script>
