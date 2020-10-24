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
        <el-input v-model="dataForm.square" placeholder="面积,单位:平米"></el-input>
      </el-form-item>
      <el-form-item label="价格" prop="price">
        <el-input v-model="dataForm.price" placeholder="价格,单位:元"></el-input>
      </el-form-item>
      <el-form-item label="酒店" prop="hotelId" :class="{ 'is-required': !dataForm.id }">
        <el-select v-model="dataForm.hotelId" @change="$forceUpdate()" filterable clearable placeholder="请选择"
                   style="width: 100%; position: relative" :disabled="!!dataForm.id">
          <el-option v-for="hotel in hotels" :key="hotel.id" :label="hotel.name" :value="hotel.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="房型描述" prop="remark">
        <el-input v-model="dataForm.remark" placeholder="房型描述"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  import { isDigit } from '@/utils/validate'
  export default {
    data () {
      var validateNum = (rule, value, callback) => {
        if (!isDigit(value)) {
          callback(new Error('只能输入数字'))
        } else {
          callback()
        }
      }
      return {
        visible: false,
        hotels: [],
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
            { required: true, message: '面积不能为空', trigger: 'blur' },
            { validator: validateNum, trigger: 'blur' }
          ],
          price: [
            { required: true, message: '价格不能为空', trigger: 'blur' },
            { validator: validateNum, trigger: 'blur' }
          ],
          hotelId: [
            { required: true, message: '酒店ID不能为空', trigger: 'blur' }
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
            const hotel = this.hotels.find((item) => {
              return item.id === this.dataForm.hotelId
            })
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
                'hotelName': hotel.name
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
