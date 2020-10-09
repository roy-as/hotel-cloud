<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="房间名称" prop="name">
      <el-input v-model="dataForm.name" placeholder="房间名称"></el-input>
    </el-form-item>
    <el-form-item label="房号" prop="number">
      <el-input v-model="dataForm.number" placeholder="房号"></el-input>
    </el-form-item>
    <el-form-item label="房间名称" prop="remark">
      <el-input v-model="dataForm.remark" placeholder="房间名称"></el-input>
    </el-form-item>
    <el-form-item label="酒店ID" prop="hotelId">
      <el-input v-model="dataForm.hotelId" placeholder="酒店ID"></el-input>
    </el-form-item>
    <el-form-item label="酒店ID" prop="roomTypeId">
      <el-input v-model="dataForm.roomTypeId" placeholder="酒店ID"></el-input>
    </el-form-item>
    <el-form-item label="状态" prop="status">
      <el-input v-model="dataForm.status" placeholder="状态"></el-input>
    </el-form-item>
    <el-form-item label="删除标志位" prop="flag">
      <el-input v-model="dataForm.flag" placeholder="删除标志位"></el-input>
    </el-form-item>
    <el-form-item label="创建时间" prop="createTime">
      <el-input v-model="dataForm.createTime" placeholder="创建时间"></el-input>
    </el-form-item>
    <el-form-item label="更新时间" prop="updateTime">
      <el-input v-model="dataForm.updateTime" placeholder="更新时间"></el-input>
    </el-form-item>
    <el-form-item label="创建人" prop="createBy">
      <el-input v-model="dataForm.createBy" placeholder="创建人"></el-input>
    </el-form-item>
    <el-form-item label="更新人" prop="updateBy">
      <el-input v-model="dataForm.updateBy" placeholder="更新人"></el-input>
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
        visible: false,
        dataForm: {
          id: 0,
          name: '',
          number: '',
          remark: '',
          hotelId: '',
          roomTypeId: '',
          status: '',
          flag: '',
          createTime: '',
          updateTime: '',
          createBy: '',
          updateBy: ''
        },
        dataRule: {
          name: [
            { required: true, message: '房间名称不能为空', trigger: 'blur' }
          ],
          number: [
            { required: true, message: '房号不能为空', trigger: 'blur' }
          ],
          remark: [
            { required: true, message: '房间名称不能为空', trigger: 'blur' }
          ],
          hotelId: [
            { required: true, message: '酒店ID不能为空', trigger: 'blur' }
          ],
          roomTypeId: [
            { required: true, message: '酒店ID不能为空', trigger: 'blur' }
          ],
          status: [
            { required: true, message: '状态不能为空', trigger: 'blur' }
          ],
          flag: [
            { required: true, message: '删除标志位不能为空', trigger: 'blur' }
          ],
          createTime: [
            { required: true, message: '创建时间不能为空', trigger: 'blur' }
          ],
          updateTime: [
            { required: true, message: '更新时间不能为空', trigger: 'blur' }
          ],
          createBy: [
            { required: true, message: '创建人不能为空', trigger: 'blur' }
          ],
          updateBy: [
            { required: true, message: '更新人不能为空', trigger: 'blur' }
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
              url: this.$http.adornUrl(`/hotel/hotelRoom/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.name = data.hotelRoom.name
                this.dataForm.number = data.hotelRoom.number
                this.dataForm.remark = data.hotelRoom.remark
                this.dataForm.hotelId = data.hotelRoom.hotelId
                this.dataForm.roomTypeId = data.hotelRoom.roomTypeId
                this.dataForm.status = data.hotelRoom.status
                this.dataForm.flag = data.hotelRoom.flag
                this.dataForm.createTime = data.hotelRoom.createTime
                this.dataForm.updateTime = data.hotelRoom.updateTime
                this.dataForm.createBy = data.hotelRoom.createBy
                this.dataForm.updateBy = data.hotelRoom.updateBy
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
              url: this.$http.adornUrl(`/hotel/hotelRoom/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'name': this.dataForm.name,
                'number': this.dataForm.number,
                'remark': this.dataForm.remark,
                'hotelId': this.dataForm.hotelId,
                'roomTypeId': this.dataForm.roomTypeId,
                'status': this.dataForm.status,
                'flag': this.dataForm.flag,
                'createTime': this.dataForm.createTime,
                'updateTime': this.dataForm.updateTime,
                'createBy': this.dataForm.createBy,
                'updateBy': this.dataForm.updateBy
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
