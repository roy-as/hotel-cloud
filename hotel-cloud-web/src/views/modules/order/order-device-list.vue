<template>
  <el-dialog
    title="设备清单"
    :close-on-click-modal="false"
    :visible.sync="visible"
    width="75%">
    <el-table
      :data="dataList"
      border
      v-loading="dataListLoading"
      height="460"
      style="width: 100%;">
      <el-table-column
        prop="deviceType"
        header-align="center"
        align="center"
        width="80"
        label="类型">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.deviceType === 1" size="small" >智能主机</el-tag>
          <el-tag v-if="scope.row.deviceType === 2" size="small">智能设备</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="name"
        header-align="center"
        align="center"
        width="80"
        label="名称">
      </el-table-column>
      <el-table-column
        prop="pictureUrl"
        header-align="center"
        align="center"
        label="图片">
        <template slot-scope="scope">
          <span v-if="scope.row.pictureUrl">
            <img :src="imgUrl(scope.row)" :preview="scope.$index" height="70" width="70"/>
          </span>
        </template>
      </el-table-column>
      <el-table-column
        prop="price"
        header-align="center"
        align="center"
        label="价格">
      </el-table-column>
      <el-table-column
        prop="shopNumber"
        header-align="center"
        align="center"
        label="数量">
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
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        dataForm: {
          orderId: ''
        },
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false
      }
    },
    methods: {
      init (id) {
        this.visible = true
        this.dataForm.orderId = id
        this.getDataList()
      },
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/order/order/deviceList'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'orderId': this.dataForm.orderId
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            console.log(data)
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
      imgUrl: function (row) {
        if (!row.pictureUrl) {
          return ''
        }
        return row.pictureUrl
      }
    }
  }
</script>
