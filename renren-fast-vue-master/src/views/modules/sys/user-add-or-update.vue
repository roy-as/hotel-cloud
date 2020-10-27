<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="用户类型"  prop="userType" :class="{ 'is-required': !dataForm.id }">
        <el-select v-model="dataForm.userType" @change="userTypeChange" filterable clearable placeholder="请选择" style="width: 100%; position: relative" :disabled="!!dataForm.id">
          <el-option v-for="userType in userTypeList" :key="userType.type" :label="userType.name" :value="userType.type">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="组织"  prop="orgId" :class="{ 'is-required': !dataForm.id }" v-if="orgVisible">
        <el-select v-model="dataForm.orgId" @change="userTypeChange" filterable clearable placeholder="请选择" style="width: 100%; position: relative" :disabled="!!dataForm.id">
          <el-option v-for="org in orgs" :key="org.id" :label="org.name" :value="org.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input v-model="dataForm.name" placeholder="姓名"></el-input>
      </el-form-item>
      <el-form-item label="用户名" prop="userName">
        <el-input v-model="dataForm.userName" placeholder="登录帐号"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password" :class="{ 'is-required': !dataForm.id }">
        <el-input v-model="dataForm.password" type="password" placeholder="密码"></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="comfirmPassword" :class="{ 'is-required': !dataForm.id }">
        <el-input v-model="dataForm.comfirmPassword" type="password" placeholder="确认密码"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="dataForm.email" placeholder="邮箱"></el-input>
      </el-form-item>
      <el-form-item label="手机号" prop="mobile">
        <el-input v-model="dataForm.mobile" placeholder="手机号"></el-input>
      </el-form-item>
      <el-form-item label="角色" size="mini" prop="roleIdList" v-if="roleVisible">
        <el-checkbox-group v-model="dataForm.roleIdList">
          <el-checkbox v-for="role in roleList" :key="role.roleId" :label="role.roleId">{{ role.roleName }}</el-checkbox>
        </el-checkbox-group>
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
  import { getUser } from '@/utils/index'
  export default {
    data () {
      var validatePassword = (rule, value, callback) => {
        if (!this.dataForm.id && !/\S/.test(value)) {
          callback(new Error('密码不能为空'))
        } else {
          callback()
        }
      }
      var validateComfirmPassword = (rule, value, callback) => {
        if (!this.dataForm.id && !/\S/.test(value)) {
          callback(new Error('确认密码不能为空'))
        } else if (this.dataForm.password !== value) {
          callback(new Error('确认密码与密码输入不一致'))
        } else {
          callback()
        }
      }
      var validateEmail = (rule, value, callback) => {
        if (value && !isEmail(value)) {
          callback(new Error('邮箱格式错误'))
        } else {
          callback()
        }
      }
      var validateMobile = (rule, value, callback) => {
        if (value && !isMobile(value)) {
          callback(new Error('手机号格式错误'))
        } else {
          callback()
        }
      }
      return {
        visible: false,
        roleList: [],
        userTypeList: [],
        orgs: [],
        orgVisible: false,
        roleVisible: false,
        dataForm: {
          id: 0,
          userName: '',
          password: '',
          comfirmPassword: '',
          salt: '',
          email: '',
          mobile: '',
          roleIdList: [],
          status: 1,
          name: ''
        },
        dataRule: {
          name: [
            { required: true, message: '姓名不能为空', trigger: 'blur' }
          ],
          userName: [
            { required: true, message: '用户名不能为空', trigger: 'blur' }
          ],
          password: [
            { validator: validatePassword, trigger: 'blur' }
          ],
          comfirmPassword: [
            { validator: validateComfirmPassword, trigger: 'blur' }
          ],
          email: [
            { validator: validateEmail, trigger: 'blur' }
          ],
          mobile: [
            { validator: validateMobile, trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.id = id || 0
        const user = getUser()
        this.userTypeList = []
        if (user.userType === 0) {
          this.userTypeList.push({type: 0, name: '系统用户'})
          this.userTypeList.push({type: 1, name: '代理商用户'})
          this.userTypeList.push({type: 2, name: '酒店用户'})
          this.userTypeList.push({type: 3, name: '安装公司用户'})
        } else if (user.userType === 1) {
          this.userTypeList.push({type: 1, name: '代理商用户'})
          this.userTypeList.push({type: 4, name: '子用户'})
        } else if (user.userType === 2) {
          this.userTypeList.push({type: 4, name: '子用户'})
        } else if (user.userType === 3) {
          this.userTypeList.push({type: 4, name: '子用户'})
        }
        this.$http({
          url: this.$http.adornUrl('/sys/role/select'),
          method: 'get',
          params: this.$http.adornParams()
        }).then(({data}) => {
          this.roleList = data && data.code === 0 ? data.list : []
        }).then(() => {
          this.visible = true
          this.$nextTick(() => {
            this.$refs['dataForm'].resetFields()
          })
        }).then(() => {
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/sys/user/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.userName = data.user.username
                this.dataForm.salt = data.user.salt
                this.dataForm.email = data.user.email
                this.dataForm.mobile = data.user.mobile
                this.dataForm.roleIdList = data.user.roleIdList
                this.dataForm.status = data.user.status
                this.dataForm.name = data.user.name
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            const org = this.orgs.find(item => {
              return item.id === this.dataForm.orgId
            })
            this.$http({
              url: this.$http.adornUrl(`/sys/user/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'userId': this.dataForm.id || undefined,
                'username': this.dataForm.userName,
                'password': this.dataForm.password,
                'salt': this.dataForm.salt,
                'email': this.dataForm.email,
                'mobile': this.dataForm.mobile,
                'status': this.dataForm.status,
                'roleIdList': this.dataForm.roleIdList,
                'userType': this.dataForm.userType,
                'orgId': org ? org.id : null,
                'orgName': org ? org.name : null,
                'name': this.dataForm.name
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
      userTypeChange () {
        this.$forceUpdate()
        if (this.dataForm.userType !== 0 && this.dataForm.userType !== 4) {
          this.roleVisible = false
          this.orgVisible = true
          this.$http({
            url: this.$http.adornUrl('/org/select'),
            method: 'get',
            params: this.$http.adornParams({
              userType: this.dataForm.userType
            })
          }).then(({data}) => {
            this.orgs = data && data.code === 0 ? data.data : []
          })
        } else {
          this.orgVisible = false
          this.roleVisible = true
        }
      }
    }
  }
</script>
