<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="代理商名称"></el-input>
      </el-form-item>
      <el-form-item v-if="agentLevelVisible" label="代理类型"  prop="agentLevel">
        <el-select v-model="dataForm.agentLevel" @change="$forceUpdate()" filterable clearable placeholder="请选择" style="width: 100%; position: relative" :disabled="!!dataForm.id">
          <el-option v-for="agentLevel in agentLevelList" :key="agentLevel.level" :label="agentLevel.name" :value="agentLevel.level">
          </el-option>
        </el-select>
      </el-form-item>
      <!--<el-form-item v-if="parentIdVisible" label="上级代理" prop="parentId" :class="{ 'is-required': !dataForm.id }">-->
        <!--<el-select v-model="dataForm.parentId" @change="$forceUpdate()" filterable clearable placeholder="请选择"-->
                   <!--style="width: 100%; position: relative" :disabled="!!dataForm.id">-->
          <!--<el-option v-for="parent in parentList" :key="parent.id" :label="parent.name" :value="parent.id">-->
            <!--<span style="float: left">{{ parent.name }}</span>-->
            <!--<span style="float: right; color: #8492a6; font-size: 13px">{{ parent.agentLevelName }}</span>-->
          <!--</el-option>-->
        <!--</el-select>-->
      <!--</el-form-item>-->
      <el-form-item label="联系人" prop="contact">
        <el-input v-model="dataForm.contact" placeholder="联系人"></el-input>
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
      <el-button @click="cancel">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  import { isEmail, isMobile } from '@/utils/validate'
  import { getUser } from '@/utils/index'
  const city = require('@/utils/constant/city.json')
  export default {
    data () {
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
      var validateParent = (rule, value, callback) => {
        if (this.agentLevelVisible && value && value.length === 0) {
          callback(new Error('代理类型不能为空'))
        }
        callback()
      }
      var validateAgentName = (rule, value, callback) => {
        if (this.dataForm.id) {
          callback()
        }
        this.$http({
          url: this.$http.adornUrl('/agentUser/list'),
          method: 'get',
          params: this.$http.adornParams({
            'name': value
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
        agentLevelList: [],
        cityOption: city,
        parentIdVisible: true,
        agentLevelVisible: false,
        visible: false,
        dataForm: {
          id: 0,
          name: '',
          email: '',
          mobile: '',
          contact: '',
          province: '',
          address: '',
          remark: '',
          status: 1,
          area: [],
          agentLevel: ''
        },
        dataRule: {
          email: [
            { required: true, message: '邮箱不能为空', trigger: 'blur' },
            { validator: validateEmail, trigger: 'blur' }
          ],
          contact: [
            { required: true, message: '联系人不能为空', trigger: 'blur' }
          ],
          mobile: [
            { required: true, message: '手机号不能为空', trigger: 'blur' },
            { validator: validateMobile, trigger: 'blur' }
          ],
          name: [
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
          agentLevel: [
            {required: true, message: '代理类型不能为空', trigger: 'blur'},
            { validator: validateParent, trigger: 'blur' }
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
        this.dataForm.id = id || 0
        const user = getUser()
        this.agentLevelList = []
        if (user.userType === 0) {
          this.agentLevelVisible = true
          this.agentLevelList.push({level: 1, name: '省级代理'})
          this.agentLevelList.push({level: 4, name: '区域代理'})
          // this.parentIdVisible = false
        } else {
          this.agentLevelVisible = false
        }
        this.$http({
          url: this.$http.adornUrl('/agentUser/select'),
          method: 'get',
          params: this.$http.adornParams()
        }).then(({data}) => {
          this.parentList = data && data.code === 0 ? data.data : []
        }).then(() => {
          this.visible = true
          this.$nextTick(() => {
            this.$refs['dataForm'].resetFields()
            if (this.dataForm.id) {
              this.$http({
                url: this.$http.adornUrl(`/agentUser/info/${this.dataForm.id}`),
                method: 'get',
                params: this.$http.adornParams()
              }).then(({data}) => {
                if (data && data.code === 0) {
                  this.dataForm.name = data.agentUser.name
                  this.dataForm.province = data.agentUser.province
                  this.dataForm.address = data.agentUser.address
                  this.dataForm.remark = data.agentUser.remark
                  this.dataForm.mobile = data.agentUser.mobile
                  this.dataForm.email = data.agentUser.email
                  this.dataForm.status = data.agentUser.status
                  this.dataForm.contact = data.agentUser.contact
                  this.dataForm.parentId = data.agentUser.parentId
                  this.dataForm.area = JSON.parse(data.agentUser.area)
                  this.dataForm.agentLevel = data.agentUser.agentLevel
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
            const parent = this.parentList.find((item) => {
              return item.id === this.dataForm.parentId
            })
            console.log(parent)
            this.$http({
              url: this.$http.adornUrl(`/agentUser/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'name': this.dataForm.name,
                'province': this.dataForm.province,
                'address': this.dataForm.address,
                'remark': this.dataForm.remark,
                'parentId': !this.dataForm.id ? this.dataForm.parentId : null,
                'parentName': !this.dataForm.id && parent ? parent.name : null,
                'email': this.dataForm.email,
                'mobile': this.dataForm.mobile,
                'contact': this.dataForm.contact,
                'status': this.dataForm.status,
                'areas': this.dataForm.area,
                'agentLevel': this.dataForm.agentLevel
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
      },
      cancel () {
        this.visible = false
        this.dataForm.parentId = null
      }
    }
  }
</script>
