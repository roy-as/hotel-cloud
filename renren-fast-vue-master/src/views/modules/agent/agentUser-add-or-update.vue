<template>
  <el-dialog
    :title="!dataForm.userId ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="账号" prop="username">
      <el-input v-model="dataForm.username" placeholder="登陆账号" :disabled="!!dataForm.userId"></el-input>
    </el-form-item>
    <el-form-item label="密码" prop="password" :class="{ 'is-required': !dataForm.userId }">
      <el-input v-model="dataForm.password" type="password" placeholder="密码"></el-input>
    </el-form-item>
    <el-form-item label="确认密码" prop="comfirmPassword" :class="{ 'is-required': !dataForm.userId }">
      <el-input v-model="dataForm.comfirmPassword" type="password" placeholder="确认密码"></el-input>
    </el-form-item>
    <el-form-item label="代理名称" prop="agentName">
      <el-input v-model="dataForm.agentName" placeholder="代理商名称"></el-input>
    </el-form-item>
    <el-form-item label="上级代理"  prop="parentList" :class="{ 'is-required': !dataForm.id }">
      <el-select v-model="dataForm.parentId" filterable clearable placeholder="请选择" style="width: 100%; position: relative" :disabled="!!dataForm.userId">
        <el-option v-for="parent in parentList" :key="parent.userId" :label="parent.agentName" :value="parent.userId">
          <span style="float: left">{{ parent.agentName }}</span>
          <span style="float: right; color: #8492a6; font-size: 13px">{{ parent.sysUser.agentLevelName }}</span>
        </el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="联系人" prop="contactPerson">
      <el-input v-model="dataForm.contactPerson" placeholder="联系人"></el-input>
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model="dataForm.email" placeholder="邮箱"></el-input>
    </el-form-item>
    <el-form-item label="手机号" prop="mobile">
      <el-input v-model="dataForm.mobile" placeholder="手机号"></el-input>
    </el-form-item>
    <el-form-item label="地区" prop="area">
      <el-cascader v-model="dataForm.area" :options="cityOption" style="width: 100%" clearable></el-cascader>
    </el-form-item>
    <el-form-item label="详细地址" prop="address">
      <el-input v-model="dataForm.address" placeholder="详细地址"></el-input>
    </el-form-item>
    <el-form-item label="备注" prop="remark">
      <el-input v-model="dataForm.remark" placeholder="备注"></el-input>
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
  import { isEmail, isMobile } from '@/utils/validate'
  const city = require('@/utils/constant/city.json')
  export default {
    data () {
      var validatePassword = (rule, value, callback) => {
        if (!this.dataForm.userId && !/\S/.test(value)) {
          callback(new Error('密码不能为空'))
        } else {
          callback()
        }
      }
      var validateComfirmPassword = (rule, value, callback) => {
        if (!this.dataForm.userId && !/\S/.test(value)) {
          callback(new Error('确认密码不能为空'))
        } else if (this.dataForm.password !== value) {
          callback(new Error('确认密码与密码输入不一致'))
        } else {
          callback()
        }
      }
      var validateEmail = (rule, value, callback) => {
        if (!isEmail(value)) {
          callback(new Error('邮箱格式错误'))
        } else {
          callback()
        }
      }
      var validateMobile = (rule, value, callback) => {
        if (!isMobile(value)) {
          callback(new Error('手机号格式错误'))
        } else {
          callback()
        }
      }
      var validateArea = (rule, value, callback) => {
        if (value.length === 0) {
          callback(new Error('地区不能为空'))
        }
        callback()
      }
      var validateUsername = (rule, value, callback) => {
        if (this.dataForm.userId) {
          callback()
        }
        this.$http({
          url: this.$http.adornUrl('/agentUser/list'),
          method: 'get',
          params: this.$http.adornParams({
            'username': value
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            let list = data.page.list
            if (list && list.length > 0) {
              callback(new Error('登录名已存在'))
            } else {
              callback()
            }
          } else {
            callback()
          }
        })
      }
      var validateAgentName = (rule, value, callback) => {
        if (this.dataForm.userId) {
          callback()
        }
        this.$http({
          url: this.$http.adornUrl('/agentUser/list'),
          method: 'get',
          params: this.$http.adornParams({
            'agentName': value
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            let list = data.page.list
            if (list && list.length > 0) {
              callback(new Error('代理商名称已存在'))
            } else {
              callback()
            }
          } else {
            callback()
          }
        })
      }
      return {
        parentList: [],
        cityOption: city,
        visible: false,
        dataForm: {
          userId: 0,
          username: '',
          password: '',
          comfirmPassword: '',
          agentName: '',
          roleIdList: [],
          email: '',
          mobile: '',
          contactPerson: '',
          province: '',
          address: '',
          remark: '',
          status: 1,
          area: []
        },
        dataRule: {
          username: [
            { required: true, message: '账号不能为空', trigger: 'blur' },
            { validator: validateUsername, trigger: 'blur' }
          ],
          password: [
            { validator: validatePassword, trigger: 'blur' }
          ],
          comfirmPassword: [
            { validator: validateComfirmPassword, trigger: 'blur' }
          ],
          email: [
            { required: true, message: '邮箱不能为空', trigger: 'blur' },
            { validator: validateEmail, trigger: 'blur' }
          ],
          contactPerson: [
            { required: true, message: '联系人不能为空', trigger: 'blur' }
          ],
          mobile: [
            { required: true, message: '手机号不能为空', trigger: 'blur' },
            { validator: validateMobile, trigger: 'blur' }
          ],
          agentName: [
            { required: true, message: '代理商名称不能为空', trigger: 'blur' },
            { validator: validateAgentName, trigger: 'blur' }
          ],
          province: [
            { required: true, message: '省份不能为空', trigger: 'blur' }
          ],
          address: [
            { required: true, message: '地址不能为空', trigger: 'blur' }
          ],
          parentId: [
            {required: true, message: '上级代理不能为空', trigger: 'blur'}
          ],
          area: [
            {required: true, message: '地区不能为空', trigger: 'blur'},
            { validator: validateArea, trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.userId = id || 0
        this.$http({
          url: this.$http.adornUrl('/agentUser/select'),
          method: 'get',
          params: this.$http.adornParams()
        }).then(({data}) => {
          this.parentList = data && data.code === 0 ? data.data : []
          console.log(this.parentList)
        }).then(() => {
          this.visible = true
          this.$nextTick(() => {
            this.$refs['dataForm'].resetFields()
            if (this.dataForm.userId) {
              this.$http({
                url: this.$http.adornUrl(`/agentUser/info/${this.dataForm.userId}`),
                method: 'get',
                params: this.$http.adornParams()
              }).then(({data}) => {
                if (data && data.code === 0) {
                  this.dataForm.agentName = data.agentUser.agentName
                  this.dataForm.province = data.agentUser.province
                  this.dataForm.address = data.agentUser.address
                  this.dataForm.remark = data.agentUser.remark
                  this.dataForm.username = data.agentUser.sysUser.username
                  this.dataForm.mobile = data.agentUser.sysUser.mobile
                  this.dataForm.email = data.agentUser.sysUser.email
                  this.dataForm.status = data.agentUser.sysUser.status
                  this.dataForm.contactPerson = data.agentUser.sysUser.contactPerson
                  this.dataForm.parentId = data.agentUser.sysUser.parentId
                  this.dataForm.area = JSON.parse(data.agentUser.area)
                }
              })
            }
          })
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/agentUser/${!this.dataForm.userId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'userId': this.dataForm.userId || undefined,
                'agentName': this.dataForm.agentName,
                'province': this.dataForm.province,
                'address': this.dataForm.address,
                'remark': this.dataForm.remark,
                'username': !this.dataForm.userId ? this.dataForm.username : null,
                'password': this.dataForm.password,
                'parentId': !this.dataForm.userId ? this.dataForm.parentId : null,
                'email': this.dataForm.email,
                'mobile': this.dataForm.mobile,
                'contactPerson': this.dataForm.contactPerson,
                'status': this.dataForm.status,
                'areas': this.dataForm.area
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.dataForm.parentId = null
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
