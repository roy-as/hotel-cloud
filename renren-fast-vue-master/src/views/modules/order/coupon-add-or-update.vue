<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="名称"></el-input>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="dataForm.type" @change="typeChange" filterable clearable placeholder="请选择" style="width: 100%; position: relative">
          <el-option v-for="type in types" :key="type.type" :label="type.name" :value="type.type">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="满减金额" prop="amount" v-if="amountVisible">
        <el-input v-model="dataForm.amount" placeholder="满减金额"></el-input>
      </el-form-item>
      <el-form-item label="优惠金额" prop="couponAmount" v-if="couponAmountVisible">
        <el-input v-model="dataForm.couponAmount" placeholder="优惠金额"></el-input>
      </el-form-item>
      <el-form-item label="折扣" prop="discount" v-if="discountVisible">
        <el-input v-model="dataForm.discount" placeholder="折扣，例如:0.8代表8折"></el-input>
      </el-form-item>
      <el-form-item label="数量" prop="count">
        <el-input-number v-model="dataForm.count" controls-position="right" :min="1" label="数量"></el-input-number>
      </el-form-item>
      <el-form-item label="过期时间" prop="expiredTime">
        <el-date-picker
          v-model="dataForm.expiredTime"
          type="date"
          style="width: 100%"
          placeholder="选择日期">
        </el-date-picker>
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
        amountVisible: true,
        couponAmountVisible: true,
        discountVisible: true,
        types: [
          {type: 1, name: '满减'},
          {type: 2, name: '每满减'},
          {type: 3, name: '折扣'},
          {type: 4, name: '满折扣'},
          {type: 5, name: '定额'}
        ],
        dataForm: {
          id: 0,
          name: '',
          type: '',
          sn: '',
          expiredTime: '',
          count: 1,
          amount: '',
          couponAmount: '',
          discount: ''
        },
        dataRule: {
          name: [
            { required: true, message: '名称不能为空', trigger: 'blur' }
          ],
          type: [
            { required: true, message: '优惠券类型不能为空', trigger: 'blur' }
          ],
          count: [
            { required: true, message: '数量不能为空', trigger: 'blur' }
          ],
          amount: [
            { required: true, message: '满减金额不能为空', trigger: 'blur' },
            { validator: validateNum, trigger: 'blur' }
          ],
          couponAmount: [
            { required: true, message: '优惠金额不能为空', trigger: 'blur' },
            { validator: validateNum, trigger: 'blur' }
          ],
          discount: [
            { required: true, message: '折扣不能为空', trigger: 'blur' },
            { validator: validateNum, trigger: 'blur' }
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
              url: this.$http.adornUrl(`/order/coupon/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.name = data.coupon.name
                this.dataForm.type = data.coupon.type
                this.dataForm.count = data.coupon.count
                this.dataForm.amount = data.coupon.amount
                this.dataForm.couponAmount = data.coupon.couponAmount
                this.dataForm.discount = data.coupon.discount
                this.dataForm.expiredTime = data.coupon.expiredTime
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
              url: this.$http.adornUrl(`/order/coupon/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'name': this.dataForm.name,
                'type': this.dataForm.type,
                'count': this.dataForm.count,
                'amount': this.dataForm.amount,
                'expiredTime': this.dataForm.expiredTime.getTime(),
                'couponAmount': this.dataForm.couponAmount,
                'discount': this.dataForm.discount
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
      typeChange () {
        this.$forceUpdate()
        switch (this.dataForm.type) {
          case 1:
            this.amountVisible = true
            this.couponAmountVisible = true
            this.discountVisible = false
            break
          case 2:
            this.amountVisible = true
            this.couponAmountVisible = true
            this.discountVisible = false
            break
          case 3:
            this.amountVisible = false
            this.couponAmountVisible = false
            this.discountVisible = true
            break
          case 4:
            this.amountVisible = true
            this.couponAmountVisible = false
            this.discountVisible = true
            break
          default:
            this.amountVisible = false
            this.couponAmountVisible = true
            this.discountVisible = false
        }
      }
    }
  }
</script>
