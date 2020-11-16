<template>
  <el-dialog
    :title="this.command.name"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="设备">
        <el-tag v-model="dataForm.names" v-for="name in names" :key="name" type="info" style="margin-left: 5px;width: 100px;text-align: center">
          {{name}}
        </el-tag>
      </el-form-item>
      <el-form-item label="回包" prop="body">
        <el-input  v-model="dataForm.body" disabled="true"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="cancel">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        command: {},
        names: [],
        dataForm: {
          body: ''
        },
        dataRule: {},
        macs: []
      }
    },
    methods: {
      init (command, names, macs) {
        this.command = command
        this.macs = macs
        this.names = names
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          this.$http({
            url: this.$http.adornUrl(`/sys/command/release`),
            method: 'post',
            data: this.$http.adornData({
              'commandId': this.command.id,
              'datas': null,
              'macs': this.macs
            })
          }).then(({data}) => {
            if (data && data.code === 0) {
              let result = data.data
              for (let key in result) {
                if (result[key].success === true) {
                  this.dataForm.body = result[key].upBody
                } else {
                  this.$message.error('查询失败')
                  this.visible = false
                }
              }
            } else {
              this.$message.error('查询失败')
            }
          })
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.visible = false
      },
      cancel () {
        this.visible = false
      }
    }
  }
</script>
