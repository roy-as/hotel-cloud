<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item label="酒店"  prop="hotelId">
        <el-select v-model="dataForm.hotelId" @change="$forceUpdate()" filterable clearable>
          <el-option v-for="hotel in hotels" :key="hotel.id" :label="hotel.name" :value="hotel.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="房型"  prop="roomTypeId">
        <el-select v-model="dataForm.roomTypeId" @change="roomTypeChange" filterable clearable style="width: 100%; position: relative">
          <el-option v-for="roomType in roomTypes" :key="roomType.id" :label="roomType.name" :value="roomType.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">查询</el-button>
        <el-button v-if="isAuth('hotel:hotelRoom:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
        <el-button v-if="isAuth('hotel:hotelRoom:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
        <el-button v-if="isAuth('hotel:hotelRoom:update')" type="primary" @click="disable(null, null, 1)" :disabled="dataListSelections.length <= 0">批量启用</el-button>
        <el-button v-if="isAuth('hotel:hotelRoom:update')" type="danger" @click="disable(null, null, 0)" :disabled="dataListSelections.length <= 0">批量禁用</el-button>
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
        label="ID">
      </el-table-column>
      <el-table-column
        prop="name"
        header-align="center"
        align="center"
        label="房间名称">
      </el-table-column>
      <el-table-column
        prop="number"
        header-align="center"
        align="center"
        label="房号">
      </el-table-column>
      <el-table-column
        prop="floor"
        header-align="center"
        align="center"
        label="楼层">
      </el-table-column>
      <el-table-column
        prop="hotelName"
        header-align="center"
        align="center"
        label="酒店">
      </el-table-column>
      <el-table-column
        prop="roomTypeName"
        header-align="center"
        align="center"
        label="房型">
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        label="状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 0" size="small" type="danger">禁用</el-tag>
          <el-tag v-if="scope.row.status === 1" size="small">正常</el-tag>
          <el-tag v-if="scope.row.status === 2" size="small" type="warning">已入住</el-tag>
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
        prop="updateTime"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="更新时间">
      </el-table-column>
      <el-table-column
        prop="createBy"
        header-align="center"
        align="center"
        label="创建人">
      </el-table-column>
      <el-table-column
        prop="updateBy"
        header-align="center"
        align="center"
        label="更新人">
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <el-button v-if="isAuth('hotel:hotelRoom:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
          <el-button v-if="isAuth('hotel:hotelRoom:delete')" type="text" size="small" @click="deleteHandle(scope.row.id, scope.row.name)">删除</el-button>
          <el-button v-if="scope.row.status === 0 && isAuth('hotel:hotelRoom:update')" type="text" size="small" @click="disable(scope.row.id, scope.row.name, 1)">启用</el-button>
          <el-button v-if="scope.row.status === 1 && isAuth('hotel:hotelRoom:update')" type="text" size="small" @click="disable(scope.row.id, scope.row.name, 0)"><span style="color: lightpink">禁用</span></el-button>
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
  import AddOrUpdate from './hotelRoom-add-or-update'
  export default {
    data () {
      return {
        dataForm: {
          key: ''
        },
        roomTypes: [],
        hotels: [],
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
          url: this.$http.adornUrl('/hotel/hotelRoom/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'hotelId': this.dataForm.hotelId,
            'roomTypeId': this.dataForm.roomTypeId
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
        this.$http({
          url: this.$http.adornUrl('/hotel/hotelRoomType/select'),
          method: 'get',
          params: this.$http.adornParams()
        }).then(({data}) => {
          this.roomTypes = data && data.code === 0 ? data.data : []
        })
        this.$http({
          url: this.$http.adornUrl('/hotel/hotelInfo/select'),
          method: 'get',
          params: this.$http.adornParams()
        }).then(({data}) => {
          this.hotels = data && data.code === 0 ? data.data : []
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
      disable (id, name, status) {
        var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.id
        })
        var names = name ? [name] : this.dataListSelections.map(item => {
          return item.name
        })
        let text = '启用'
        if (status === 0) {
          text = '禁用'
        }
        this.$confirm(`确定对[房间:${names.join(',')}]进行[${name ? text : '批量' + text}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/hotel/hotelRoom/disable'),
            method: 'post',
            data: this.$http.adornData({
              'id': ids,
              'status': status
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
      // 删除
      deleteHandle (id, name) {
        var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.id
        })
        var names = name ? [name] : this.dataListSelections.map(item => {
          return item.name
        })
        this.$confirm(`确定对[房间:${names.join(',')}]进行[${name ? '删除' : '批量删除'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/hotel/hotelRoom/delete'),
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
