<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="楼栋名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="楼栋名称"></el-input>
      </el-form-item>
      <el-form-item label="楼栋号" prop="buildingNumber">
        <el-input v-model="dataForm.buildingNumber" placeholder="楼栋号"></el-input>
      </el-form-item>
      <el-form-item label="酒店"  prop="parentList" :class="{ 'is-required': !dataForm.id }">
        <el-select v-model="dataForm.hotelId" @change="$forceUpdate()" filterable clearable placeholder="请选择" style="width: 100%; position: relative" :disabled="!!dataForm.id">
          <el-option v-for="hotel in hotels" :key="hotel.id" :label="hotel.name" :value="hotel.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="dataForm.remark" placeholder="备注"></el-input>
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
        hotels: [],
        dataForm: {
          id: 0,
          name: '',
          buildingNumber: '',
          hotelId: '',
          remark: ''
        },
        dataRule: {
          name: [
            { required: true, message: '楼栋名称不能为空', trigger: 'blur' }
          ],
          buildingNumber: [
            { required: true, message: '楼栋号不能为空', trigger: 'blur' }
          ],
          hotelId: [
            { required: true, message: '酒店不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        this.$http({
          url: this.$http.adornUrl('/hotel/hotelInfo/select'),
          method: 'get',
          params: this.$http.adornParams()
        }).then(({data}) => {
          this.hotels = data && data.code === 0 ? data.data : []
        }).then(() => {
          this.$nextTick(() => {
            this.$refs['dataForm'].resetFields()
            if (this.dataForm.id) {
              this.$http({
                url: this.$http.adornUrl(`/hotel/hotelBuilding/info/${this.dataForm.id}`),
                method: 'get',
                params: this.$http.adornParams()
              }).then(({data}) => {
                if (data && data.code === 0) {
                  this.dataForm.name = data.hotelBuilding.name
                  this.dataForm.buildingNumber = data.hotelBuilding.buildingNumber
                  this.dataForm.remark = data.hotelBuilding.remark
                  this.dataForm.hotelId = data.hotelBuilding.hotelId
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
            const chosenHotel = this.hotels.find((item) => {
              return item.id === this.dataForm.hotelId
            })
            this.$http({
              url: this.$http.adornUrl(`/hotel/hotelBuilding/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'name': this.dataForm.name,
                'buildingNumber': this.dataForm.buildingNumber,
                'remark': this.dataForm.remark,
                'hotelId': this.dataForm.hotelId,
                'hotelName': chosenHotel ? chosenHotel.name : null
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.dataForm.hotelId = null
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
        this.dataForm.hotelId = null
      }
    }
  }
</script>
