<template>
  <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="visible" :append-to-body="true" width="600px">

    <el-descriptions title="详细信息" :column="2" border>
      <el-descriptions-item :span="2" label="章节">{{progress.chapName}}</el-descriptions-item>
      <el-descriptions-item :span="2" label="细分章节">{{progress.subChapName}}</el-descriptions-item>
      <el-descriptions-item :span="2" label="二级细分章节">{{progress.secSubChapName}}</el-descriptions-item>
      <el-descriptions-item :span="2" label="内容">{{progress.content}}</el-descriptions-item>
      <el-descriptions-item :span="1" label="负责人">
        <span
          v-for="dict in userOptions"
          v-if="progress.personInCharge==dict.dictValue"
          v-text="dict.dictLabel"
        ></span>
      </el-descriptions-item>
      <el-descriptions-item :span="1" label="支持人">
        <span
          v-for="dict in userOptions"
          v-if="progress.supporter==dict.dictValue"
          v-text="dict.dictLabel"
        ></span>
      </el-descriptions-item>
      <el-descriptions-item :span="1" label="开始日期">{{progress.startDate}}</el-descriptions-item>
      <el-descriptions-item :span="1" label="目标日期">{{progress.targetDate}}</el-descriptions-item>
      <el-descriptions-item :span="1" label="实际完成日期">{{progress.finishDate}}</el-descriptions-item>
      <el-descriptions-item :span="1" label="准备情况">
        <span
          v-for="dict in dict.type.t_progress_preparation"
          v-if="progress.preparation==dict.value"
          v-text="dict.label"
        ></span>
      </el-descriptions-item>
      <el-descriptions-item :span="1" label="进度描述">
        <span
          v-for="dict in dict.type.t_progress_description"
          v-if="progress.progress==dict.value"
          v-text="dict.label"
        ></span>
      </el-descriptions-item>
      <el-descriptions-item :span="1" label=""></el-descriptions-item>
      <el-descriptions-item :span="2" label="备注">{{progress.remarks}}</el-descriptions-item>
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
  import { handleQuestionnaire } from "@/api/rc/questionnaire";
  import { listAllUser } from "@/api/system/user";

export default {
  name: "questionnaire-detail",
  components: {  },
  dicts: ['t_progress_preparation', 't_progress_description' ],
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
      progress: null,
      type: 0,
      // 用户列表
      userOptions: [],
    }
  },
  methods: {
    init(progress, taskId, processId, taskName, type) {
      this.getUserList();
      this.progress = progress;
      this.type = type;
      this.taskForm.progress = progress;
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
      handleQuestionnaire(this.taskForm).then(response => {
        this.visible = false;
        this.$modal.msgSuccess("提交成功");
        // refreshDataList事件：调用父组件getList方法刷新页面
        this.$emit('refreshDataList');
      });
    },
  }
}
</script>
