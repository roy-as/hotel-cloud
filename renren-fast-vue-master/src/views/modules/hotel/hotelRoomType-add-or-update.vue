<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="房型名称" prop="name">
      <el-input v-model="dataForm.name" placeholder="房型名称"></el-input>
    </el-form-item>
    <el-form-item label="面积" prop="square">
      <el-input v-model="dataForm.square" placeholder="面积"></el-input>
    </el-form-item>
    <el-form-item label="价格" prop="price">
      <el-input v-model="dataForm.price" placeholder="价格"></el-input>
    </el-form-item>
    <el-form-item label="房型描述" prop="remark">
      <el-input v-model="dataForm.remark" placeholder="房型描述"></el-input>
    </el-form-item>
    <el-form-item label="酒店ID" prop="hotelId">
      <el-input v-model="dataForm.hotelId" placeholder="酒店ID"></el-input>
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
          square: '',
          price: '',
          remark: '',
          hotelId: '',
          createTime: '',
          updateTime: '',
          createBy: '',
          updateBy: ''
        },
        dataRule: {
          name: [
            { required: true, message: '房型名称不能为空', trigger: 'blur' }
          ],
          square: [
            { required: true, message: '面积不能为空', trigger: 'blur' }
          ],
          price: [
            { required: true, message: '价格不能为空', trigger: 'blur' }
          ],
          remark: [
            { required: true, message: '房型描述不能为空', trigger: 'blur' }
          ],
          hotelId: [
            { required: true, message: '酒店ID不能为空', trigger: 'blur' }
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
              url: this.$http.adornUrl(`/hotel/hotelRoomType/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.name = data.hotelRoomType.name
                this.dataForm.square = data.hotelRoomType.square
                this.dataForm.price = data.hotelRoomType.price
                this.dataForm.remark = data.hotelRoomType.remark
                this.dataForm.hotelId = data.hotelRoomType.hotelId
                this.dataForm.createTime = data.hotelRoomType.createTime
                this.dataForm.updateTime = data.hotelRoomType.updateTime
                this.dataForm.createBy = data.hotelRoomType.createBy
                this.dataForm.updateBy = data.hotelRoomType.updateBy
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
              url: this.$http.adornUrl(`/hotel/hotelRoomType/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'name': this.dataForm.name,
                'square': this.dataForm.square,
                'price': this.dataForm.price,
                'remark': this.dataForm.remark,
                'hotelId': this.dataForm.hotelId,
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
