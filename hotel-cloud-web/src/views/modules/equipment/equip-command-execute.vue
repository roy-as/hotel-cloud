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
      <el-form-item label="数据包" prop="body">
        <el-input  v-model="dataForm.body" placeholder="16进制的数据包,例如:256f"></el-input>
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
        dataRule: {
          body: [
            {required: true, msg: '数据包不能为空', trigger: 'blur'}
          ]
        },
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
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/sys/command/release`),
              method: 'post',
              data: this.$http.adornData({
                'commandId': this.command.id,
                'datas': [this.dataForm.body],
                'macs': this.macs
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                let msg = ''
                let result = data.data
                let time = 1500
                let flag = true
                for (let key in result) {
                  msg += `设备:${key}下发指令${result[key].success === true ? '成功' : '失败'}\n`
                  if (result[key].success === false) {
                    flag = false
                  }
                }
                if (flag) {
                  this.$message({
                    message: '执行失败',
                    type: 'success',
                    duration: time,
                    onClose: () => {
                      this.visible = false
                      this.$emit('refreshDataList')
                    }
                  })
                } else {
                  this.$message.error(msg)
                }
              } else {
                this.$message.error('执行失败')
              }
            })
          }
        })
      },
      cancel () {
        this.visible = false
      }
    }
  }
</script>
