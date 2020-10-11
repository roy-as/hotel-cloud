<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item label="设备名称">
        <el-input v-model="dataForm.name" placeholder="设备名称" clearable></el-input>
      </el-form-item>
      <el-form-item prop="module" label="设备模块">
        <el-select v-model="dataForm.moduleId" filterable clearable @change="$forceUpdate()">
          <el-option v-for="module in modules" :key="module.id" :label="module.name" :value="module.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="酒店"  prop="hotelId">
        <el-select v-model="dataForm.hotelId" @change="$forceUpdate()" filterable clearable>
          <el-option v-for="hotel in hotels" :key="hotel.id" :label="hotel.name" :value="hotel.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">查询</el-button>
        <el-button v-if="isAuth('equipment:equip:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
        <el-button v-if="isAuth('equipment:equip:generateQrcode')" type="primary" @click="generateQrcode()" :disabled="dataListSelections.length <= 0">生成二维码</el-button>
        <el-button v-if="isAuth('equipment:equip:release')" type="primary" @click="releaseEquip()" :disabled="dataListSelections.length <= 0">设备下发</el-button>
        <el-button v-if="isAuth('equipment:equip:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
        <el-button v-if="isAuth('equipment:equip:recycle')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量回收</el-button>
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
        label="设备名称">
      </el-table-column>
      <el-table-column
        prop="moduleName"
        header-align="center"
        align="center"
        label="设备模块">
      </el-table-column>
      <el-table-column
        prop="qrcodeUrl"
        header-align="center"
        align="center"
        label="Logo">
        <template slot-scope="scope">
          <span v-if="scope.row.qrcodeUrl">
            <img :src="imgUrl(scope.row)" height="70" width="70"/>
          </span>
        </template>
      </el-table-column>
      <el-table-column
        prop="qrcodeInfo"
        header-align="center"
        align="center"
        show-overflow-tooltip
        width="100"
        label="二维码信息">
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
        prop="mac"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="mac地址">
      </el-table-column>
      <el-table-column
        prop="sn"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="序列号">
      </el-table-column>
      <el-table-column
        prop="versionNumber"
        header-align="center"
        align="center"
        label="版本号">
      </el-table-column>
      <el-table-column
        prop="remark"
        header-align="center"
        align="center"
        label="备注">
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        label="状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 0" size="small" type="danger">禁用</el-tag>
          <el-tag v-if="scope.row.status === 1" size="small" type="warning">待下发</el-tag>
          <el-tag v-if="scope.row.status === 2" size="small">已绑定</el-tag>
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
        show-overflow-tooltip
        label="创建人">
      </el-table-column>
      <el-table-column
        prop="updateBy"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="更新人">
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <el-button v-if="isAuth('equipment:equip:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
          <el-button v-if="isAuth('equipment:equip:delete')" type="text" size="small" @click="deleteHandle(scope.row.id, scope.row.name)">删除</el-button>
          <el-button v-if="isAuth('equipment:equip:recycle')" type="text" size="small" @click="deleteHandle(scope.row.id, scope.row.name)"><span style="color: lightpink">回收</span></el-button>
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
    <generate-qrcode v-if="generateQrcodeVisible" ref="generateQrcode" @refreshDataList="getDataList"></generate-qrcode>
    <release v-if="releaseVisible" ref="release"  @refreshDataList="getDataList"></release>
  </div>
</template>

<script>
  import AddOrUpdate from './equip-add-or-update'
  import GenerateQrcode from './equip-generate-qrcode'
  import Release from './equip-release'
  export default {
    data () {
      return {
        dataForm: {
          key: ''
        },
        dataList: [],
        modules: [],
        hotels: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        dataListSelections: [],
        addOrUpdateVisible: false,
        generateQrcodeVisible: false,
        releaseVisible: false
      }
    },
    components: {
      AddOrUpdate,
      GenerateQrcode,
      Release
    },
    activated () {
      this.getDataList()
    },
    methods: {
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/equipment/equip/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'name': this.dataForm.name,
            'moduleId': this.dataForm.moduleId,
            'hotelId': this.dataForm.hotelId
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
          url: this.$http.adornUrl('/equipment/equipModule/select'),
          method: 'get',
          params: this.$http.adornParams()
        }).then(({data}) => {
          this.modules = data && data.code === 0 ? data.data : []
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
      // 删除
      deleteHandle (id, name) {
        var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.id
        })
        var names = name ? [name] : this.dataListSelections.map(item => {
          return item.name
        })
        this.$confirm(`确定对[设备:${names.join(',')}]进行[${name ? '删除' : '批量删除'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/equipment/equip/delete'),
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
      generateQrcode: function (id, name) {
        let valid = true
        let devices = []
        this.dataListSelections.forEach((item, index) => {
          if (item.qrcodeInfo && item.ossId) {
            valid = false
            devices.push(item.name)
          }
        })
        const msg = `设备:  [${devices.join(',')}] 已生成二维码`
        if (!valid) {
          this.$alert(msg, '提示', {
            confirmButtonText: '确定',
            callback: () => {
              this.$message({
                type: 'warning',
                message: msg
              })
            }
          })
          return
        }
        var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.id
        })
        var names = name ? [name] : this.dataListSelections.map(item => {
          return item.name
        })
        this.generateQrcodeVisible = true
        this.$nextTick(() => {
          this.$refs.generateQrcode.init(ids, names)
        })
      },
      releaseEquip (id, name) {
        let valid = true
        let devices = []
        this.dataListSelections.forEach((item, index) => {
          if (item.agentId && item.hotelId) {
            valid = false
            devices.push(item.name)
          }
        })
        const msg = `设备:  [${devices.join(',')}] 已下发`
        if (!valid) {
          this.$alert(msg, '提示', {
            confirmButtonText: '确定',
            callback: () => {
              this.$message({
                type: 'warning',
                message: msg
              })
            }
          })
          return
        }
        var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.id
        })
        var names = name ? [name] : this.dataListSelections.map(item => {
          return item.name
        })
        this.releaseVisible = true
        this.$nextTick(() => {
          this.$refs.release.init(ids, names)
        })
      },
      imgUrl: function (row) {
        if (!row.qrcodeUrl) {
          return ''
        }
        return row.qrcodeUrl
      }
    }
  }
</script>
