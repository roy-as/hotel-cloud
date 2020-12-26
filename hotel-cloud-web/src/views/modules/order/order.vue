<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.key" placeholder="参数名" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">查询</el-button>
        <el-button v-if="isAuth('order:order:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
        <el-button v-if="isAuth('order:order:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      v-loading="dataListLoading"
      @selection-change="selectionChangeHandle"
      style="width: 100%;">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>
      <el-table-column
        prop="id"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="订单号">
      </el-table-column>
      <el-table-column
        prop="agentName"
        header-align="center"
        align="center"
        label="代理">
      </el-table-column>
      <el-table-column
        prop="hotelName"
        header-align="center"
        align="center"
        label="酒店">
      </el-table-column>
      <el-table-column
        prop="installationName"
        header-align="center"
        align="center"
        label="安装公司">
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        label="状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === -1" size="small" type="danger">已拒绝</el-tag>
          <el-tag v-if="scope.row.status === 0" size="small" type="danger">待审核</el-tag>
          <el-tag v-if="scope.row.status === 1" size="small" type="danger" >待发货</el-tag>
          <el-tag v-if="scope.row.status === 2" size="small" type="warning">待安装</el-tag>
          <el-tag v-if="scope.row.status === 3" size="small" type="warning">酒店确认</el-tag>
          <el-tag v-if="scope.row.status === 4" size="small" type="warning">待付款</el-tag>
          <el-tag v-if="scope.row.status === 5" size="small">完成</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="delivery"
        header-align="center"
        align="center"
        label="快递">
      </el-table-column>
      <el-table-column
        prop="deliveryNo"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="单号">
      </el-table-column>
      <el-table-column
        prop="payType"
        header-align="center"
        align="center"
        label="支付方式">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.payType === 1" size="small" >支付宝</el-tag>
          <el-tag v-if="scope.row.payType === 2" size="small">微信</el-tag>
          <el-tag v-if="scope.row.payType === 3" size="small">银行卡</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="amount"
        header-align="center"
        align="center"
        label="支付金额">
      </el-table-column>
      <el-table-column
        prop="realAmount"
        header-align="center"
        align="center"
        label="实际支付">
      </el-table-column>
      <el-table-column
        prop="couponSn"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="优惠券">
      </el-table-column>
      <el-table-column
        prop="payOrderNo"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="流水号">
      </el-table-column>
      <el-table-column
        prop="createBy"
        header-align="center"
        align="center"
        label="创建人">
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="创建时间">
      </el-table-column>
      <el-table-column
        prop="remark"
        header-align="center"
        align="center"
        label="备注">
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <!--<el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>-->
          <el-button v-if="isAuth('order:order:list')" type="text" size="small" @click="deviceList(scope.row.id)">设备清单</el-button>
          <el-button v-if="isAuth('order:order:audit') && scope.row.status===0" type="text" size="small" @click="audit(scope.row.id, 1)">通过</el-button>
          <el-button v-if="isAuth('order:order:audit') && scope.row.status===0" type="text" size="small" @click="audit(scope.row.id, -1)">拒绝</el-button>
          <el-button v-if="isAuth('order:order:delivery') && scope.row.status===1" type="text" size="small" @click="delivery(scope.row.id)">发货</el-button>
          <el-button v-if="isAuth('order:order:installed') && scope.row.status===2" type="text" size="small" @click="installed(scope.row.id)">确认安装</el-button>
          <el-button v-if="isAuth('order:order:confirmInstall') && scope.row.status===3" type="text" size="small" @click="confirm(scope.row.id)">酒店确认</el-button>
          <el-button v-if="isAuth('order:order:pay') && scope.row.status===4" type="text" size="small" @click="pay(scope.row.id)">付款</el-button>
          <el-button v-if="isAuth('order:order:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
      :current-page="pageIndex"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pageSize"
      :total="totalPage"
      layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
    <order-device-list v-if="orderDeviceListVisible" ref="orderDeviceList"></order-device-list>
    <order-delivery v-if="orderDeliveryVisible" ref="orderDelivery" @refreshDataList="getDataList"></order-delivery>
    <order-pay v-if="orderPayVisible" ref="orderPay" @refreshDataList="getDataList"></order-pay>
  </div>
</template>

<script>
  import AddOrUpdate from './order-add-or-update'
  import OrderDeviceList from './order-device-list'
  import OrderDelivery from './order-delivery'
  import OrderPay from './order-pay'
  export default {
    data () {
      return {
        dataForm: {
          key: ''
        },
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        dataListSelections: [],
        addOrUpdateVisible: false,
        orderDeviceListVisible: false,
        orderDeliveryVisible: false,
        orderPayVisible: false
      }
    },
    components: {
      AddOrUpdate,
      OrderDeviceList,
      OrderDelivery,
      OrderPay
    },
    activated () {
      this.getDataList()
    },
    methods: {
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/order/order/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'key': this.dataForm.key
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.dataList = data.page.list
            this.totalPage = data.page.totalCount
          } else {
            this.dataList = []
            this.totalPage = 0
          }
          this.dataListLoading = false
        })
      },
      // 每页数
      sizeChangeHandle (val) {
        this.pageSize = val
        this.pageIndex = 1
        this.getDataList()
      },
      // 当前页
      currentChangeHandle (val) {
        this.pageIndex = val
        this.getDataList()
      },
      // 多选
      selectionChangeHandle (val) {
        this.dataListSelections = val
      },
      // 新增 / 修改
      addOrUpdateHandle (id) {
        this.addOrUpdateVisible = true
        this.$nextTick(() => {
          this.$refs.addOrUpdate.init(id)
        })
      },
      // 删除
      deleteHandle (id) {
        var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.id
        })
        this.$confirm(`确定对[id=${ids.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/order/order/delete'),
            method: 'post',
            data: this.$http.adornData(ids, false)
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.getDataList()
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        })
      },
      audit (id, handle) {
        this.$confirm(`确认${handle === 1 ? '通过' : '拒绝'}吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/order/order/audit'),
            method: 'post',
            data: this.$http.adornData({
              id: id,
              status: handle
            })
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.getDataList()
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        })
      },
      deviceList (id) {
        this.orderDeviceListVisible = true
        this.$nextTick(() => {
          this.$refs.orderDeviceList.init(id)
        })
      },
      delivery (id) {
        this.orderDeliveryVisible = true
        this.$nextTick(() => {
          this.$refs.orderDelivery.init(id)
        })
      },
      pay (id) {
        this.orderPayVisible = true
        this.$nextTick(() => {
          this.$refs.orderPay.init(id)
        })
      },
      installed (id) {
        this.$confirm(`确认已安装吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/order/order/installed'),
            method: 'post',
            data: this.$http.adornData({
              id: id
            })
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.getDataList()
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        })
      },
      confirm (id) {
        this.$confirm(`确认已安装吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/order/order/confirm'),
            method: 'post',
            data: this.$http.adornData({
              id: id
            })
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.getDataList()
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        })
      }
    }
  }
</script>
