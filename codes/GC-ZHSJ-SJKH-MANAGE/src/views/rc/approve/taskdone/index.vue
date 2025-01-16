<template >
  <div class="app-container">
    <el-table v-loading="loading" :data="approvedangerList" @selection-change="handleSelectionChange" :height="clientHeight" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="流程ID" align="center" prop="processId" :show-overflow-tooltip="true"/>
      <el-table-column label="流程名称" align="center" prop="processName" :show-overflow-tooltip="true"/>
      <el-table-column label="流程创建时间" align="center" prop="processCreateTime" :show-overflow-tooltip="true"/>
      <el-table-column label="申请人姓名" align="center" prop="apName" :show-overflow-tooltip="true"/>
      <el-table-column label="申请编号" align="center" prop="apNo" :show-overflow-tooltip="true"/>
      <el-table-column
        prop="isEnd"
        header-align="center"
        align="center"
        label="是否结束">
        <template slot-scope="scope">
          <el-tag
            :type="scope.row.end? 'success' : 'danger'"
            disable-transitions>{{scope.row.end? '是' : '否'}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="120" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            @click="processImg(scope.row.processId)"
          >流程图</el-button>
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
    <process-img v-if="processImgVisible" ref="processImg" @refreshDataList="getList"></process-img>
    <questionnaire-detail v-if="questionnaireVisible" ref="questionnaireDetail" @refreshDataList="getList"/>
    <openitem-detail v-if="openitemVisible" ref="openitemDetail" @refreshDataList="getList"/>
  </div>
</template>

<script>
  import { getTaskdonelist } from "@/api/rc/approvedanger";
  import QuestionnaireDetail from "@/views/rc/approve/detail/questionnaire-detail";
  import ProcessImg from "../processImg/index";
  import OpenitemDetail from "@/views/rc/approve/detail/openitem-detail";

  export default {
    name: "Taskdone",
    components: {
      ProcessImg,
      QuestionnaireDetail,
      OpenitemDetail
    },
    data() {
      return {
        // 遮罩层
        loading: true,
        processImgVisible: false,
        questionnaireVisible: false,
        openitemVisible: false,
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
        // 我的申请表格数据
        approvedangerList: [],
        // 弹出层标题
        title: "",
        // 部门树选项
        deptOptions: undefined,
        clientHeight:300,
        // 是否显示弹出层
        open: false,
        // 装置名称字典
        plantCodeOptions: [],
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 20,
          plantCode: null,
          recorderId: null,
          hiddendangerLevel: null,
          hiddendangerContent: null,
          measures: null,
          completeTime: null,
          status: null,
          creattime: null,
          endtime: null,
          processId: null,
          approveNo: null,
          deptId: null,
        },
        // 表单参数
        form: {},
        //处理数据传输
        approveInfo: null,
        //弹窗界面是否开启
        addOrUpdateVisible: false,
      };
    },
    watch: {
      // 根据名称筛选部门树
      deptName(val) {
        this.$refs.tree.filter(val);
      }
    },
    created() {
      //设置表格高度对应屏幕高度
      this.$nextTick(() => {
        this.clientHeight = document.body.clientHeight -200
      })
      this.getList();
    },
    methods: {
      /** 查询隐患申请列表 */
      getList() {
        this.loading = true;
        getTaskdonelist(this.queryParams).then(response => {
          this.approvedangerList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      /** 查看操作 */
      handleView (row) {
        if (row.processName == "问卷审批流程") {
          this.title = '查看问卷审批流程';
          this.questionnaireVisible = true
          this.$nextTick(() => {
            this.$refs.questionnaireDetail.init(row.progress, row.taskId, row.processId, row.taskName, 3)
          })
        } else if (row.processName == "开项审批流程") {
          this.title = '查看开项审批流程';
          this.openitemVisible = true
          this.$nextTick(() => {
            this.$refs.openitemDetail.init(row.openItem, row.taskId, row.processId, row.taskName, 3)
          })
        }
      },
      // 装置名称字典翻译
      plantCodeFormat(row, column) {
        return this.selectDictLabel(this.plantCodeOptions, row.plantCode);
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
          plantCode: null,
          recorderId: null,
          hiddendangerLevel: null,
          hiddendangerContent: null,
          measures: null,
          completeTime: null,
          status: 0,
          creattime: null,
          endtime: null,
          processId: null,
          approveNo: null,
          deptId: null,
          delFlag: null
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
      processImg (processId) {
        this.processImgVisible = true
        this.$nextTick(() => {
          this.$refs.processImg.init(processId)
        })
      },
    }
  };
</script>
