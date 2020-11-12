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
        <el-input  v-model="dynamicFormInfo[body]"  :placeholder="body"></el-input>
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
        dynamicFormInfo:{} //动态表单数据
      }
    },
    methods: {
      init (command, names) {
        this.dataForm.command = command
        this.bodies = JSON.parse(command.data)
        this.bodies.forEach((body,idx)=>{
            this.$set(this.dynamicFormInfo,body,'');
        })
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
            console.log(111,this.dynamicFormInfo)
            this.$http({
              url: this.$http.adornUrl(`/equipment/equip/old`),
              method: 'post',
              data: this.$http.adornData({
                'ids': this.ids,
                'count': this.dataForm.count
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              } else {
                this.$message.error(data.msg)
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
