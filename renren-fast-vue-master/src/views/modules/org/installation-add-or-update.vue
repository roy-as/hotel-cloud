<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="公司名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="安装公司名称"></el-input>
      </el-form-item>
      <el-form-item label="  联系人" prop="contact">
        <el-input v-model="dataForm.contact" placeholder="  联系人"></el-input>
      </el-form-item>
      <el-form-item label="　手机" prop="mobile">
        <el-input v-model="dataForm.mobile" placeholder="　手机"></el-input>
      </el-form-item>
      <el-form-item label="地区" prop="area">
        <el-cascader v-model="dataForm.area" :options="cityOption" style="width: 100%" clearable></el-cascader>
      </el-form-item>
      <el-form-item label="详细地址" prop="address">
        <el-input v-model="dataForm.address" placeholder="详细地址"></el-input>
      </el-form-item>
      <el-form-item label="状态" size="mini" prop="status">
        <el-radio-group v-model="dataForm.status">
          <el-radio :label="0">禁用</el-radio>
          <el-radio :label="1">正常</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  const city = require('@/utils/constant/city.json')
  export default {
    data () {
      var validateArea = (rule, value, callback) => {
        if (value.length === 0) {
          callback(new Error('地区不能为空'))
        }
        callback()
      }
      return {
        visible: false,
        cityOption: city,
        dataForm: {
          id: 0,
          name: '',
          area: '',
          address: '',
          mobile: '',
          contact: '',
          status: 1
        },
        dataRule: {
          name: [
            { required: true, message: '安装公司名称不能为空', trigger: 'blur' }
          ],
          area: [
            { required: true, message: '地区不能为空', trigger: 'blur' },
            { validator: validateArea, trigger: 'blur' }
          ],
          address: [
            { required: true, message: '详细地址不能为空', trigger: 'blur' }
          ],
          mobile: [
            { required: true, message: '　手机不能为空', trigger: 'blur' }
          ],
          contact: [
            { required: true, message: '  联系人不能为空', trigger: 'blur' }
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
              url: this.$http.adornUrl(`/org/installation/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.name = data.installationCompany.name
                this.dataForm.area = JSON.parse(data.installationCompany.area)
                this.dataForm.address = data.installationCompany.address
                this.dataForm.mobile = data.installationCompany.mobile
                this.dataForm.contact = data.installationCompany.contact
                this.dataForm.status = data.installationCompany.status
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
              url: this.$http.adornUrl(`/org/installation/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'name': this.dataForm.name,
                'area': JSON.stringify(this.dataForm.area),
                'address': this.dataForm.address,
                'mobile': this.dataForm.mobile,
                'contact': this.dataForm.contact,
                'status': this.dataForm.status
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
