<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.name" placeholder="酒店名称" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">查询</el-button>
        <el-button v-if="isAuth('hotel:hotelInfo:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
        <el-button v-if="isAuth('hotel:hotelInfo:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
        <el-button v-if="isAuth('hotel:hotelInfo:update')" type="primary" @click="disable(null, null, 1)" :disabled="dataListSelections.length <= 0">批量启用</el-button>
        <el-button v-if="isAuth('hotel:hotelInfo:update')" type="danger" @click="disable(null, null, 0)" :disabled="dataListSelections.length <= 0">批量禁用</el-button>
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
        label="酒店名称">
      </el-table-column>
      <el-table-column
        prop="logoUrl"
        header-align="center"
        align="center"
        label="Logo">
        <template slot-scope="scope">
          <span v-if="scope.row.logo && scope.row.logo.url">
            <img :src="imgUrl(scope.row)" height="95" width="70"/>
          </span>
        </template>
      </el-table-column>
      <el-table-column
        prop="area"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="地区">
        <template slot-scope="scope">
          {{ JSON.parse(scope.row.area).join('|') || '无' }}
        </template>
      </el-table-column>
      <el-table-column
        prop="address"
        header-align="center"
        align="center"
        label="详细地址">
      </el-table-column>
      <el-table-column
        prop="remark"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="酒店描述">
      </el-table-column>
      <el-table-column
        prop="mobile"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="联系电话">
      </el-table-column>
      <el-table-column
        prop="notice"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="温馨提示">
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
        prop="service"
        header-align="center"
        align="center"
        label="酒店服务">
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        label="状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 0" size="small" type="danger">禁用</el-tag>
          <el-tag v-else size="small">正常</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <el-button v-if="isAuth('hotel:hotelInfo:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
          <el-button v-if="isAuth('hotel:hotelInfo:info')" type="text" size="small" @click="showPictureHandle(scope.row.id, 1)">全景图</el-button>
          <el-button v-if="isAuth('hotel:hotelInfo:info')" type="text" size="small" @click="showPictureHandle(scope.row.id, 2)">酒店照片</el-button>
          <el-button v-if="isAuth('hotel:hotelInfo:delete')" type="text" size="small" @click="deleteHandle(scope.row.id, scope.row.name)">删除</el-button>
          <el-button v-if="scope.row.status === 0 && isAuth('hotel:hotelInfo:update')" type="text" size="small" @click="disable(scope.row.id, scope.row.name, 1)">启用</el-button>
          <el-button v-if="scope.row.status === 1 && isAuth('hotel:hotelInfo:update')" type="text" size="small" @click="disable(scope.row.id, scope.row.name, 0)"><span style="color: lightpink">禁用</span></el-button>
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
    <show-picture v-if="showPictureVisible" ref="showPicture" @refreshDataList="getDataList"></show-picture>
  </div>
</template>

<script>
  import AddOrUpdate from './hotelInfo-add-or-update'
  import ShowPicture from './hotelInfo-show-picture'
  export default {
    data () {
      return {
        modules: [],
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
        showPictureVisible: false
      }
    },
    components: {
      AddOrUpdate,
      ShowPicture
    },
    activated () {
      this.getDataList()
    },
    methods: {
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/hotel/hotelInfo/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'name': this.dataForm.name
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
      showPictureHandle (id, pictureType) {
        this.showPictureVisible = true
        this.$nextTick(() => {
          this.$refs.showPicture.init(id, pictureType)
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
        this.$confirm(`确定对[酒店:${names.join(',')}]进行[${name ? '删除' : '批量删除'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/hotel/hotelInfo/delete'),
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
        this.$confirm(`确定对[酒店:${names.join(',')}]进行[${name ? text : '批量' + text}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/hotel/hotelInfo/disable'),
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
      imgUrl: function (row) {
        if (!row.logo) {
          return ''
        }
        if (!row.logo.url) {
          return ''
        }
        return row.logo.url
      }
    }
  }
</script>
