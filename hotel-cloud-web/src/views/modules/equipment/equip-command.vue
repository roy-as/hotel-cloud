<template>
  <el-dialog
    :title="this.dataForm.command.name"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="设备">
        <el-tag v-model="dataForm.names" v-for="name in names" :key="name" type="info" style="margin-left: 5px;width: 100px;text-align: center">
          {{name}}
        </el-tag>
      </el-form-item>
      <el-form-item v-for="body in bodies" :label="body" :prop="body">
        <el-input  v-model="dataForm[body]"  :placeholder="body"></el-input>
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
        bodies: [],
        modules: [],
        dataForm: {
          command: {}
        },
        dataRule: {},
        macs: [],
        dynamicFormInfo: {
        }
      }
    },
    methods: {
      init (command, names, macs) {
        this.dataForm = {}
        this.dataForm.command = command
        this.macs = macs
        this.bodies = JSON.parse(command.data)
        this.bodies.forEach((body, idx) => {
          this.$set(this.dataForm, body, '')
          this.$set(this.dataRule, body, [{ required: true, message: body + '不能为空', trigger: 'blur' }])
        })
        console.log(this.dataRule)
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
            const data = []
            for (let key in this.dataForm) {
              if (key !== 'command') {
                data.push(this.dataForm[key])
              }
            }
            this.$http({
              url: this.$http.adornUrl(`/sys/command/release`),
              method: 'post',
              data: this.$http.adornData({
                'commandId': this.dataForm.command.id,
                'datas': data,
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
                    message: '执行成功',
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
                this.$message.error('执行成功')
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
