<template>
  <el-dialog
    :title="!dataForm.ids ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="设备">
        <el-tag v-model="dataForm.names" v-for="name in names" :key="name" type="info" style="margin-left: 5px;width: 100px;text-align: center">
          {{name}}
        </el-tag>
      </el-form-item>
      <el-form-item label="内容" prop="info">
        <el-input v-model="dataForm.info" placeholder="二维码内容"></el-input>
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
        ids: [],
        names: [],
        modules: [],
        dataForm: {
          info: ''
        },
        dataRule: {
          info: [
              { required: true, message: '二维码不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (ids, names) {
        this.ids = ids
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
              url: this.$http.adornUrl(`/equipment/equip/generateQrcode`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.ids[0],
                'info': this.dataForm.info
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
