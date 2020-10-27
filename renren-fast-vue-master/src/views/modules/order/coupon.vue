<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.name" placeholder="名称" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.sn" placeholder="序列号" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.createBy" placeholder="创建人" clearable></el-input>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="dataForm.type" @change="$forceUpdate()" filterable clearable placeholder="请选择" style="width: 100%; position: relative">
          <el-option v-for="type in types" :key="type.type" :label="type.name" :value="type.type">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="dataForm.status" @change="$forceUpdate()" filterable clearable placeholder="请选择" style="width: 100%; position: relative">
          <el-option v-for="status in statusList" :key="status.type" :label="status.name" :value="status.type">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">查询</el-button>
        <el-button v-if="isAuth('order:coupon:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
        <el-button v-if="isAuth('order:coupon:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
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
        v-if="false"
        label="主键">
      </el-table-column>
      <el-table-column
        prop="name"
        header-align="center"
        align="center"
        label="名称">
      </el-table-column>
      <el-table-column
        prop="type"
        header-align="center"
        align="center"
        label="类型">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.type === 1" size="small" >满减</el-tag>
          <el-tag v-if="scope.row.type === 2" size="small">每满减</el-tag>
          <el-tag v-if="scope.row.type === 3" size="small">折扣</el-tag>
          <el-tag v-if="scope.row.type === 4" size="small">满折扣</el-tag>
          <el-tag v-if="scope.row.type === 5" size="small">定额优惠</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="sn"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="序列号">
      </el-table-column>
      <el-table-column
        prop="amount"
        header-align="center"
        align="center"
        label="满减金额">
      </el-table-column>
      <el-table-column
        prop="couponAmount"
        header-align="center"
        align="center"
        label="优惠金额">
      </el-table-column>
      <el-table-column
        prop="discount"
        header-align="center"
        align="center"
        label="折扣">
        <template slot-scope="scope">
          {{ scope.row.discount ? scope.row.discount * 10 + '折' : ''}}
        </template>
      </el-table-column>
      <el-table-column
        prop="expiredTime"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="过期时间">
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        label="状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 0" size="small" >未使用</el-tag>
          <el-tag v-if="scope.row.status === 1" size="small" type="danger" >已使用</el-tag>
          <el-tag v-if="scope.row.status === 2" size="small" type="warning">已过期</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="创建时间">
      </el-table-column>
      <el-table-column
        prop="createBy"
        header-align="center"
        align="center"
        label="创建人">
      </el-table-column>
      <el-table-column
        prop="orderId"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="订单">
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <!--<el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>-->
          <el-button type="text" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>
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
  </div>
</template>

<script>
  import AddOrUpdate from './coupon-add-or-update'
  export default {
    data () {
      return {
        dataForm: {
          key: ''
        },
        types: [
          {type: 1, name: '满减'},
          {type: 2, name: '每满减'},
          {type: 3, name: '折扣'},
          {type: 4, name: '满折扣'},
          {type: 5, name: '定额'}
        ],
        statusList: [
          {type: 0, name: '未使用'},
          {type: 1, name: '已使用'},
          {type: 2, name: '已过期'}
        ],
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        dataListSelections: [],
        addOrUpdateVisible: false
      }
    },
    components: {
      AddOrUpdate
    },
    activated () {
      this.getDataList()
    },
    methods: {
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/order/coupon/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'name': this.dataForm.name,
            'createBy': this.dataForm.createBy,
            'sn': this.dataForm.sn,
            'status': this.dataForm.status,
            'type': this.dataForm.type
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
            url: this.$http.adornUrl('/order/coupon/delete'),
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
      }
    }
  }
</script>
