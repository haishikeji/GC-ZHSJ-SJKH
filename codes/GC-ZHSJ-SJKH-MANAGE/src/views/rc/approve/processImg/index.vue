<template>
  <el-dialog  :close-on-click-modal="false"
    title="流程图"
    :visible.sync="visible">
  <div style="padding-top: 0px;">
    <img :src=getUrl() style="width: 100%"/>
  </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">返回</el-button>
    </span>
  </el-dialog>
</template>

<script>

  export default {
    data () {
      return {
        visible: false,
        dataListLoading: false,
        dataList: [],
        processId: ''
      }
    },
    methods: {
      init (processId) {
        this.processId = processId
        this.visible = true
      },
      getUrl () {
        return process.env.VUE_APP_BASE_API + '/ehs/approvedanger/processImg/' + this.processId
      }
    }
  }
</script>
