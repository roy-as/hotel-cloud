<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="IO名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="IO名称"></el-input>
      </el-form-item>
      <el-form-item label="设备模块" prop="moduleId" :class="{ 'is-required': !dataForm.id }">
        <el-select v-model="dataForm.moduleId" @change="$forceUpdate()" filterable clearable placeholder="请选择"
                   style="width: 100%; position: relative" :disabled="!!dataForm.id">
          <el-option v-for="module in modules" :key="module.id" :label="module.name" :value="module.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="IO类型" prop="ioType" :class="{ 'is-required': !dataForm.id }">
        <el-select v-model="dataForm.ioType" @change="$forceUpdate()" filterable clearable placeholder="请选择"
                   style="width: 100%; position: relative" :disabled="!!dataForm.id">
          <el-option v-for="io in ioTypes" :key="io.value" :label="io.paramKey" :value="io.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="开关类型" prop="switchType">
        <el-select v-model="dataForm.switchType" @change="$forceUpdate()" filterable clearable placeholder="请选择"
                   style="width: 100%; position: relative" :disabled="!!dataForm.id">
          <el-option v-for="switchType in switchTypes" :key="switchType.value" :label="switchType.paramKey" :value="switchType.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="按键类型" prop="keyType">
        <el-select v-model="dataForm.keyType" @change="keyChange" filterable clearable placeholder="请选择"
                   style="width: 100%; position: relative" :disabled="!!dataForm.id">
          <el-option v-for="keyType in keyTypes" :key="keyType.value" :label="keyType.paramKey" :value="keyType.value">
          </el-option>
        </el-select>
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
        ioTypes: [],
        switchTypes: [],
        keyTypes: [],
        dataForm: {
          id: 0,
          name: '',
          moduleId: '',
          ioType: '',
          switchType: '',
          keyType: ''
        },
        dataRule: {
          name: [
            { required: true, message: '设备IO名称不能为空', trigger: 'blur' }
          ],
          moduleId: [
            { required: true, message: '模块模块不能为空', trigger: 'blur' }
          ],
          ioType: [
            { required: true, message: 'IO类型不能为空', trigger: 'blur' }
          ],
          switchType: [
            { required: true, message: '开关类型不能为空', trigger: 'blur' }
          ],
          keyType: [
            { required: true, message: ' 按键类型不能为空', trigger: 'blur' }
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
          this.$http({
            url: this.$http.adornUrl('/sys/config/select?paramTypes=keyType&paramTypes=ioType&paramTypes=switchType'),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            const isRight = data && data.code === 0
            this.keyTypes = isRight && data.data.keyType ? data.data.keyType : []
            this.ioTypes = isRight && data.data.ioType ? data.data.ioType : []
            this.switchTypes = isRight && data.data.switchType ? data.data.switchType : []
          })
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/equipment/equipIo/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.name = data.equipIo.name
                this.dataForm.moduleId = data.equipIo.moduleId
                this.dataForm.moduleName = data.equipIo.moduleName
                this.dataForm.ioType = data.equipIo.ioType
                this.dataForm.switchType = data.equipIo.switchType
                this.dataForm.keyType = data.equipIo.keyType
                this.dataForm.keyName = data.equipIo.keyName
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
              url: this.$http.adornUrl(`/equipment/equipIo/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'name': this.dataForm.name,
                'moduleId': this.dataForm.moduleId,
                'moduleName': this.dataForm.moduleName,
                'ioType': this.dataForm.ioType,
                'switchType': this.dataForm.switchType,
                'keyType': this.dataForm.keyType,
                'keyName': this.dataForm.keyName
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.cancel()
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
        this.dataForm.ioType = null
        this.dataForm.switchType = null
        this.dataForm.keyType = null
      },
      keyChange () {
        this.$forceUpdate()
        console.log(this.dataForm.ioType)
      }
    }
  }
</script>
