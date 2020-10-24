<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="设备名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="设备名称"></el-input>
      </el-form-item>
      <el-form-item label="设备模块" prop="moduleId" :class="{ 'is-required': !dataForm.id }">
        <el-select v-model="dataForm.moduleId" @change="$forceUpdate()" filterable clearable placeholder="请选择"
                   style="width: 100%; position: relative" :disabled="!!dataForm.id">
          <el-option v-for="module in modules" :key="module.id" :label="module.name" :value="module.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="版本号" prop="versionNumber">
        <el-input v-model="dataForm.versionNumber" placeholder="版本号"></el-input>
      </el-form-item>
      <el-form-item label="mac地址" prop="mac">
        <el-input v-model="dataForm.mac" placeholder="设备mac地址" :disabled="!!dataForm.id"></el-input>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="dataForm.remark" placeholder="备注"></el-input>
      </el-form-item>
      <el-form-item label="过期时间" prop="expiredTime"  v-if="dataForm.id">
        <el-date-picker
          v-model="dataForm.expiredTime"
          type="date"
          style="width: 100%"
          placeholder="选择日期">
        </el-date-picker>
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
        modules: [],
        dataForm: {
          id: 0,
          name: '',
          moduleId: '',
          mac: '',
          remark: '',
          sn: '',
          status: '',
          expiredTime: '',
          versionNumber: ''
        },
        dataRule: {
          name: [
            { required: true, message: '设备名称不能为空', trigger: 'blur' }
          ],
          moduleId: [
            { required: true, message: '设备模块不能为空', trigger: 'blur' }
          ],
          mac: [
            { required: true, message: 'mac地址不能为空', trigger: 'blur' }
          ],
          status: [
            { required: true, message: '状态不能为空', trigger: 'blur' }
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
          this.$http({
            url: this.$http.adornUrl('/equipment/equipModule/select'),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            this.modules = data && data.code === 0 ? data.data : []
          })
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/equipment/equip/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.name = data.equip.name
                this.dataForm.moduleId = data.equip.moduleId
                this.dataForm.mac = data.equip.mac
                this.dataForm.remark = data.equip.remark
                this.dataForm.expiredTime = data.equip.expiredTime
                this.dataForm.versionNumber = data.equip.versionNumber
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            const chosenModule = this.modules.find((item) => {
              return item.id === this.dataForm.moduleId
            })
            this.$http({
              url: this.$http.adornUrl(`/equipment/equip/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'name': this.dataForm.name,
                'moduleId': this.dataForm.moduleId,
                'moduleName': chosenModule.name,
                'mac': this.dataForm.mac,
                'remark': this.dataForm.remark,
                'versionNumber': this.dataForm.versionNumber,
                'expiredTime': this.dataForm.expiredTime ? this.dataForm.expiredTime.getTime() : null
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
        this.dataForm.moduleId = null
      }
    }
  }
</script>
