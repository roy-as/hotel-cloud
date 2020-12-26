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
      <el-form-item label="按键" v-if="keyVisible" prop="key">
        <el-input  v-model="dataForm.key"  placeholder="按键"></el-input>
      </el-form-item>
      <el-form-item v-for="body in bodies" :label="body" :prop="body">
        <el-input  v-model="dataForm[body]"  :placeholder="body" :disabled="true"></el-input>
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
        keyVisible: false,
        bodies: [],
        command: {},
        names: [],
        dataForm: {
          key: ''
        },
        dataRule: {
          key: [
            { required: true, message: ' 按键不能为空', trigger: 'blur' }
          ]
        },
        macs: []
      }
    },
    methods: {
      init (command, names, macs) {
        this.dataForm = {key: ''}
        this.command = command
        this.bodies = JSON.parse(command.data)
        this.macs = macs
        this.names = names
        this.visible = true
        if (command.command === '[13, 11, 01]') {
          this.keyVisible = true
        } else {
          this.keyVisible = false
        }
        this.bodies.forEach((body, idx) => {
          this.$set(this.dataForm, body, '')
        })
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (command.command === '[23]') {
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
                    const res = result[key].data
                    this.dataForm.信号 = res.signal
                    this.dataForm.用户名 = res.account
                    this.dataForm.密码 = res.password
                  } else {
                    this.$message.error('查询失败')
                    this.visible = false
                  }
                }
              } else {
                this.$message.error('查询失败')
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        if (this.command.command === '[23]') {
          this.visible = false
        } else {
          this.$refs['dataForm'].validate((valid) => {
            if (valid) {
              let data = [this.dataForm.key]
              this.$http({
                url: this.$http.adornUrl(`/sys/command/release`),
                method: 'post',
                data: this.$http.adornData({
                  'commandId': this.command.id,
                  'datas': data,
                  'macs': this.macs
                })
              }).then(({data}) => {
                if (data && data.code === 0) {
                  let result = data.data
                  for (let key in result) {
                    if (result[key].success === true) {
                      const res = result[key].data
                      this.$set(this.dataForm, '按键开(开)', JSON.stringify(res.openKeyOpenList))
                      this.$set(this.dataForm, '按键开(关)', JSON.stringify(res.openKeyCloseList))
                      this.$set(this.dataForm, '按键关(开)', JSON.stringify(res.closeKeyOpenList))
                      this.$set(this.dataForm, '按键关(关)', JSON.stringify(res.closeKeyCloseList))
                    } else {
                      this.$message.error('查询失败')
                      this.visible = false
                    }
                  }
                } else {
                  this.$message.error('查询失败')
                }
              })
            }
          })
        }
      },
      cancel () {
        this.visible = false
      }
    }
  }
</script>
