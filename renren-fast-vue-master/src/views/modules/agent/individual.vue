<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item prop="username" label="登录名">
        <el-input v-model="dataForm.username" clearable></el-input>
      </el-form-item>
      <el-form-item prop="agentName" label="代理商名称">
        <el-input v-model="dataForm.agentName" clearable></el-input>
      </el-form-item>
      <el-form-item prop="status" label="状态">
        <el-select v-model="dataForm.status" filterable clearable >
          <el-option v-for="status in statusList" :key="status.code" :label="status.name" :value="status.code"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">查询</el-button>
        <el-button v-if="isAuth('agentUser:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
        <el-button v-if="isAuth('agentUser:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
        <el-button v-if="isAuth('agentUser:update')" type="primary" @click="disable(null, null, 1)" :disabled="dataListSelections.length <= 0">批量启用</el-button>
        <el-button v-if="isAuth('agentUser:update')" type="danger" @click="disable(null, null, 0)" :disabled="dataListSelections.length <= 0">批量禁用</el-button>
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
        prop="userId"
        header-align="center"
        align="center"
        v-if="false"
        label="系统用户id">
      </el-table-column>
      <el-table-column
        prop="sysUser.username"
        header-align="center"
        align="center"
        label="登录名">
      </el-table-column>
      <el-table-column
        prop="agentName"
        header-align="center"
        align="center"
        label="代理商名称">
      </el-table-column>
      <el-table-column
        prop="sysUser.contactPerson"
        header-align="center"
        align="center"
        label="联系人">
      </el-table-column>
      <el-table-column
        prop="sysUser.mobile"
        header-align="center"
        align="center"
        label="手机号">
      </el-table-column>
      <el-table-column
        prop="sysUser.email"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="邮箱">
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
        label="地址">
      </el-table-column>
      <el-table-column
        prop="parentName"
        header-align="center"
        align="center"
        label="上级代理">
        <template slot-scope="scope">
          {{ scope.row.sysUser.parentName || '无' }}
        </template>
      </el-table-column>
      <el-table-column
        prop="remark"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="备注">
        <template slot-scope="{row}">
          {{ row.remark || '-' }}
        </template>
      </el-table-column>
      <el-table-column
        prop="sysUser.createTime"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="创建时间">
      </el-table-column>
      <el-table-column
        prop="sysUser.status"
        header-align="center"
        align="center"
        label="状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.sysUser.status === 0" size="small" type="danger">禁用</el-tag>
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
          <el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.userId)">修改</el-button>
          <el-button type="text" size="small" @click="deleteHandle(scope.row.userId, scope.row.sysUser.username)">删除</el-button>
          <el-button v-if="scope.row.sysUser.status === 0" type="text" size="small" @click="disable(scope.row.userId, scope.row.sysUser.username, 1)">启用</el-button>
          <el-button v-else type="text" size="small" @click="disable(scope.row.userId, scope.row.sysUser.username, 0)"><span style="color: lightpink">禁用</span></el-button>
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
  import AddOrUpdate from './individual-add-or-update'
  export default {
    data () {
      return {
        agentLevelList: [
          {level: null, agentName: '全部'},
          {level: 1, agentName: '省级'},
          {level: 2, agentName: '市级'},
          {level: 3, agentName: '县级'}
        ],
        statusList: [
          {code: null, name: '全部'},
          {code: 0, name: '禁用'},
          {code: 1, name: '启用'}
        ],
        dataForm: {
          key: ''
        },
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
          url: this.$http.adornUrl('/agentUser/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'username': this.dataForm.username,
            'agentName': this.dataForm.agentName,
            'agentLevel': 4,
            'status': this.dataForm.status
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
      deleteHandle (id, username) {
        var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.userId
        })
        var userNames = username ? [username] : this.dataListSelections.map(item => {
          return item.sysUser.username
        })
        this.$confirm(`确定对[登录名:${userNames.join(',')}]进行[${username ? '删除' : '批量删除'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/agentUser/delete'),
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
      disable (userId, username, status) {
        var userIds = userId ? [userId] : this.dataListSelections.map(item => {
          return item.userId
        })
        var userNames = username ? [username] : this.dataListSelections.map(item => {
          return item.sysUser.username
        })
        let text = '启用'
        if (status === 0) {
          text = '禁用'
        }
        this.$confirm(`确定对[登录名:${userNames.join(',')}]进行[${username ? text : '批量' + text}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/agentUser/disable'),
            method: 'post',
            data: this.$http.adornData({
              'id': userIds,
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
      }
    }
  }
</script>
