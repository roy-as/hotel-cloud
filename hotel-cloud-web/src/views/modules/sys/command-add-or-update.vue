<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="名称" prop="name">
      <el-input v-model="dataForm.name" placeholder="名称"></el-input>
    </el-form-item>
      <el-form-item label="指令类型" prop="commandType" :class="{ 'is-required': !dataForm.id }">
        <el-select v-model="dataForm.commandType" @change="$forceUpdate()" filterable clearable placeholder="请选择"
                   style="width: 100%; position: relative">
          <el-option v-for="tyoe in commandTypes" :key="tyoe.value" :label="tyoe.label" :value="tyoe.value">
          </el-option>
        </el-select>
      </el-form-item>
    <el-form-item label="指令" prop="command">
      <el-input v-model="dataForm.command" placeholder="16进制指令，不能带有0x，多个数字用英文逗号隔开"></el-input>
    </el-form-item>
    <el-form-item label="数据包" prop="data">
      <el-input v-model="dataForm.data" placeholder='数据名称，例如:用户名,密码；多个数据名称用英文逗号隔开'></el-input>
    </el-form-item>
    <el-form-item label="备注" prop="remark">
      <el-input v-model="dataForm.remark" placeholder="备注"></el-input>
    </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        commandTypes: [
          {label: '设置类', value: 1},
          {label: '直发指令类', value: 2},
          {label: '查询类', value: 3}
        ],
        visible: false,
        dataForm: {
          id: 0,
          name: '',
          command: '',
          data: '',
          remark: '',
          commandType: ''
        },
        dataRule: {
          name: [
            { required: true, message: '名称不能为空', trigger: 'blur' }
          ],
          command: [
            { required: true, message: '指令不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/sys/command/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.name = data.command.name
                this.dataForm.command = data.command.command
                this.dataForm.data = data.command.data
                this.dataForm.remark = data.command.remark
                this.dataForm.commandType = data.command.commandType
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/sys/command/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'name': this.dataForm.name,
                'command': this.dataForm.command,
                'data': this.dataForm.data,
                'remark': this.dataForm.remark,
                'commandType': this.dataForm.commandType
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
      }
    }
  }
</script>
