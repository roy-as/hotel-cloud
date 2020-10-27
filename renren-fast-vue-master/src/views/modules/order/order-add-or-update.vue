<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="酒店" prop="hotelId" :class="{ 'is-required': !dataForm.id }">
        <el-select v-model="dataForm.hotelId" @change="$forceUpdate()" filterable clearable placeholder="请选择"
                   style="width: 100%; position: relative" :disabled="!!dataForm.id">
          <el-option v-for="hotel in hotels" :key="hotel.id" :label="hotel.name" :value="hotel.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="安装公司" prop="installationId" :class="{ 'is-required': !dataForm.id }">
        <el-select v-model="dataForm.installationId" @change="$forceUpdate()" filterable clearable placeholder="请选择"
                   style="width: 100%; position: relative" :disabled="!!dataForm.id">
          <el-option v-for="installation in installations" :key="installation.id" :label="installation.name" :value="installation.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="支付方式" prop="payType" :class="{ 'is-required': !dataForm.id }">
        <el-select v-model="dataForm.payType" @change="$forceUpdate()" filterable clearable placeholder="请选择"
                   style="width: 100%; position: relative" :disabled="!!dataForm.id">
          <el-option v-for="payType in payTypes" :key="payType.value" :label="payType.label" :value="payType.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="优惠券" prop="couponSn">
        <el-input v-model="dataForm.couponSn" placeholder="优惠券序列号"></el-input>
      </el-form-item>
      <el-form-item label="支付金额" prop="amount">
        <el-input v-model="dataForm.amount" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item label="实际金额" prop="realAmount">
        <el-input v-model="dataForm.realAmount" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="dataForm.remark" placeholder="备注"></el-input>
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
        hotels: [],
        installations: [],
        payTypes: [{
          label: '支付宝', value: 1
        }, {
          label: '微信', value: 2
        }, {
          label: '银行卡', value: 3
        }],
        dataForm: {
          id: 0,
          hotelId: '',
          installationId: '',
          payType: '',
          couponSn: '',
          amount: '',
          realAmount: '',
          remark: ''
        },
        dataRule: {
          hotelId: [
            { required: true, message: '酒店不能为空', trigger: 'blur' }
          ],
          installationId: [
            { required: true, message: '安装公司不能为空', trigger: 'blur' }
          ],
          payType: [
            { required: true, message: '支付方式不能为空', trigger: 'blur' }
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
            url: this.$http.adornUrl('/org/select'),
            method: 'get',
            params: this.$http.adornParams({
              userType: 2
            })
          }).then(({data}) => {
            this.hotels = data && data.code === 0 ? data.data : []
          })
          this.$http({
            url: this.$http.adornUrl('/org/select'),
            method: 'get',
            params: this.$http.adornParams({
              userType: 3
            })
          }).then(({data}) => {
            this.installations = data && data.code === 0 ? data.data : []
          })
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/order/order/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.hotelId = data.order.hotelId
                this.dataForm.hotelName = data.order.hotelName
                this.dataForm.installationId = data.order.installationId
                this.dataForm.installationName = data.order.installationName
                this.dataForm.payType = data.order.payType
                this.dataForm.couponSn = data.order.couponSn
                this.dataForm.couponId = data.order.couponId
                this.dataForm.remark = data.order.remark
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            const hotel = this.hotels.find(item => {
              return item.id === this.dataForm.hotelId
            })
            const installation = this.installations.find(item => {
              return item.id === this.dataForm.installationId
            })
            this.$http({
              url: this.$http.adornUrl(`/order/order/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'hotelId': this.dataForm.hotelId,
                'hotelName': hotel.name,
                'installationId': this.dataForm.installationId,
                'installationName': installation.name,
                'payType': this.dataForm.payType,
                'couponSn': this.dataForm.couponSn,
                'remark': this.dataForm.remark
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
