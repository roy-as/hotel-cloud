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
      <el-form-item label="楼层" prop="floor">
        <el-input v-model="dataForm.floor" placeholder="楼层"></el-input>
      </el-form-item>
      <el-form-item v-if="false" prop="hotelId">
        <el-input v-model="dataForm.hotelId"></el-input>
      </el-form-item>
      <el-form-item label="房型"  prop="roomTypeId">
        <el-select v-model="dataForm.roomTypeId" @change="roomTypeChange" filterable clearable style="width: 100%; position: relative">
          <el-option v-for="roomType in roomTypes" :key="roomType.id" :label="roomType.name" :value="roomType.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="酒店" prop="hotelName">
        <el-input v-model="dataForm.hotelName" :disabled="true"></el-input>
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
  export default {
    data () {
      return {
        visible: false,
        roomTypes: [],
        dataForm: {
          id: 0,
          name: '',
          number: '',
          remark: '',
          hotelId: '',
          roomTypeId: '',
          status: 1,
          floor: '',
          roomTypeName: ''
        },
        dataRule: {
          name: [
            { required: true, message: '房间名称不能为空', trigger: 'blur' }
          ],
          number: [
            { required: true, message: '房号不能为空', trigger: 'blur' }
          ],
          floor: [
            { required: true, message: '楼层不能为空', trigger: 'blur' }
          ],
          hotelId: [
            { required: true, message: '酒店不能为空', trigger: 'blur' }
          ],
          roomTypeId: [
            { required: true, message: '房型不能为空', trigger: 'blur' }
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
            url: this.$http.adornUrl('/hotel/hotelRoomType/select'),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            this.roomTypes = data && data.code === 0 ? data.data : []
          })
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
                this.dataForm.hotelName = data.hotelRoom.hotelName
                this.dataForm.roomTypeId = data.hotelRoom.roomTypeId
                this.dataForm.roomTypeName = data.hotelRoom.roomTypeName
                this.dataForm.status = data.hotelRoom.status
                this.dataForm.floor = data.hotelRoom.floor
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
                'hotelName': this.dataForm.hotelName,
                'roomTypeName': this.dataForm.roomTypeName,
                'floor': this.dataForm.floor
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
      roomTypeChange () {
        this.$forceUpdate()
        const roomType = this.roomTypes.find((item) => {
          return item.id === this.dataForm.roomTypeId
        })
        if (roomType) {
          this.dataForm.hotelId = roomType.hotelId
          this.dataForm.hotelName = roomType.hotelName
          this.dataForm.roomTypeName = roomType.name
        }
      }
    }
  }
</script>
