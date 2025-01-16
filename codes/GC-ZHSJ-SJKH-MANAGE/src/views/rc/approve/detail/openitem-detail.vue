<template>
  <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="visible" :append-to-body="true" width="600px">

    <el-descriptions title="详细信息" :column="2" border>
      <el-descriptions-item :span="1" label="问题类型">
        <span
          v-for="dict in dict.type.t_open_item_type"
          v-if="openitem.type==dict.value"
          v-text="dict.label"
        ></span>
      </el-descriptions-item>
      <el-descriptions-item :span="1" label="涉及整改code">{{openitem.questionnaireId}}</el-descriptions-item>
      <el-descriptions-item :span="2" label="存在问题描述">{{openitem.description}}</el-descriptions-item>
      <el-descriptions-item :span="1" label="开项级别">
        <span
          v-for="dict in dict.type.t_open_item_level"
          v-if="openitem.level==dict.value"
          v-text="dict.label"
        ></span>
      </el-descriptions-item>
      <el-descriptions-item :span="1" label="开项时间">{{openitem.openTime}}</el-descriptions-item>
      <el-descriptions-item :span="1" label="闭项时间">{{openitem.closeTime}}</el-descriptions-item>
      <el-descriptions-item :span="1" label="问题处理状态">
        <span
          v-for="dict in dict.type.t_open_item_status"
          v-if="openitem.status==dict.value"
          v-text="dict.label"
        ></span>
      </el-descriptions-item>
      <el-descriptions-item :span="1" label="负责人">{{openitem.personInChargeName}}</el-descriptions-item>
      <el-descriptions-item :span="1" label="审核人">{{openitem.reviewerName}}</el-descriptions-item>
      <el-descriptions-item :span="1" label="责任人确认">
        <span
          v-for="dict in dict.type.t_open_item_result"
          v-if="openitem.result==dict.value"
          v-text="dict.label"
        ></span>
      </el-descriptions-item>
      <el-descriptions-item :span="1" label="截至时间">{{openitem.deadline}}</el-descriptions-item>
      <el-descriptions-item :span="2" label="备注">{{openitem.remarks}}</el-descriptions-item>
    </el-descriptions>

    <el-table :data="historyList" border v-loading="historyLoading" style="width: 100%;">
      <el-table-column width="100" prop="taskName" header-align="center" align="center" label="流程进度"></el-table-column>
      <el-table-column width="80" prop="userName" header-align="center" align="center" label="处理人"></el-table-column>
      <el-table-column prop="comment" header-align="center" align="center" label="备注 / 审批意见"></el-table-column>
      <el-table-column width="100" prop="taskCreateTime" header-align="center" align="center" label="开始时间"></el-table-column>
      <el-table-column width="100" prop="taskEndTime" header-align="center" align="center" label="结束时间"></el-table-column>
    </el-table>

    <div slot="footer" class="dialog-footer">
      <el-button v-if="type == 2" type="success" @click="dataFormSubmit(1)">通 过</el-button>
      <el-button v-if="type == 2" type="info" @click="dataFormSubmit(0)">驳 回</el-button>
      <el-button @click="visible = false">返回</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import { getToken } from "@/utils/auth";
  import {getHistorylist} from "@/api/rc/approvedanger";
  import { handleOpenitem } from "@/api/rc/openitem";
  import { listAllUser } from "@/api/system/user";

export default {
  name: "openitem-detail",
  components: {  },
  dicts: [ 't_open_item_result', 't_open_item_type', 't_open_item_level', 't_open_item_status' ],
  data() {
    return {
      rules: {
      },
      taskForm: {},
      title: null,
      visible: false,
      deptOptions: [],
      //流转列表
      historyList: [],
      // rejectList: [],
      historyLoading: true,
      dialogType: 0,
      openitem: null,
      type: null,
      // 用户列表
      userOptions: [],
    }
  },
  methods: {
    init(openitem, taskId, processId, taskName, type) {
      this.getUserList();
      this.openitem = openitem;
      this.type = type;
      this.taskForm.openItem = openitem;
      this.taskForm.taskId = taskId;
      this.taskForm.processId = processId;
      this.taskForm.taskName = taskName;
      // 流转列表
      getHistorylist({ "processId": processId }).then(response => {
        this.historyList = response.rows;
        this.historyLoading = false
      });
      this.visible = true;
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
    reset() {
      this.taskForm =  {
      };
    },
    dataFormSubmit(condition) {
      this.taskForm.condition = condition;
      handleOpenitem(this.taskForm).then(response => {
        this.visible = false;
        this.$modal.msgSuccess("提交成功");
        // refreshDataList事件：调用父组件getList方法刷新页面
        this.$emit('refreshDataList');
      });
    },
  }
}
</script>
