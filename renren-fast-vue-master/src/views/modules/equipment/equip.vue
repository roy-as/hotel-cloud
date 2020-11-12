<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item prop="module" label="模块">
        <el-select v-model="dataForm.moduleId" filterable clearable @change="$forceUpdate()">
          <el-option v-for="module in modules" :key="module.id" :label="module.name" :value="module.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="代理">
        <el-input v-model="dataForm.agentName" placeholder="代理" clearable></el-input>
      </el-form-item>
      <el-form-item label="酒店"  prop="hotelId">
        <el-select v-model="dataForm.hotelId" @change="$forceUpdate()" filterable clearable>
          <el-option v-for="hotel in hotels" :key="hotel.id" :label="hotel.name" :value="hotel.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item prop="status" label="过期时间">
        <el-date-picker
          v-model="dataForm.expiredTimeRange"
          type="datetimerange"
          :picker-options="pickerOptions"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          align="right">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="mac地址">
        <el-input v-model="dataForm.mac" placeholder="mac地址" clearable></el-input>
      </el-form-item>
      <el-form-item prop="status" label="状态">
        <el-select v-model="dataForm.status" filterable clearable>
          <el-option v-for="status in statusList" :key="status.code" :label="status.label" :value="status.code"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">查询</el-button>
        <el-button v-if="isAuth('equipment:equip:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
        <el-button v-for="command in commands" type="primary" @click="releaseCommand(command)"  :disabled="dataListSelections.length <= 0">
          {{ command.name }}
        </el-button>
        <el-button v-if="isAuth('equipment:equip:release')" type="primary" @click="releaseEquip()" :disabled="dataListSelections.length <= 0">批量下发</el-button>
        <el-button v-if="isAuth('equipment:equip:old')" type="primary" @click="old()" :disabled="dataListSelections.length <= 0">批量老化</el-button>
        <el-button v-if="isAuth('equipment:equip:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
        <el-button v-if="isAuth('equipment:equip:recycle')" type="danger" @click="recycleHandle()" :disabled="dataListSelections.length <= 0">批量回收</el-button>
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
        show-overflow-tooltip
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
        label="二维码">
        <template slot-scope="scope">
          <span v-if="scope.row.qrcodeUrl">
            <img :src="imgUrl(scope.row)" :preview="scope.$index" height="70" width="70"/>
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
        prop="versionNumber"
        header-align="center"
        align="center"
        label="版本号">
      </el-table-column>
      <el-table-column
        prop="ip"
        header-align="center"
        align="center"
        label="IP">
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
          <el-tag v-if="scope.row.status === 0" size="small" type="danger">待绑定</el-tag>
          <el-tag v-if="scope.row.status === 1" size="small" type="warning">待老化</el-tag>
          <el-tag v-if="scope.row.status === 2" size="small" type="warning">待下发</el-tag>
          <el-tag v-if="scope.row.status === 3" size="small" type="waning">待安装</el-tag>
          <el-tag v-if="scope.row.status === 4" size="small">工作中</el-tag>
          <el-tag v-if="scope.row.status === 5" size="small" type="warning">已回收</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        label="在线状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 0 || !scope.row.ip" size="small" type="danger">离线</el-tag>
          <el-tag v-if="scope.row.status === 1 && scope.row.ip" size="small">在线</el-tag>
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
        show-overflow-tooltip
        label="创建人">
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <el-button v-if="isAuth('equipment:equip:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
          <el-button v-if="isAuth('equipment:equip:generateQrcode') && scope.row.status === 0" type="text" size="small" @click="generateQrcode(scope.row.id, scope.row.name)">绑定二维码</el-button>
          <el-button v-if="isAuth('equipment:equip:old') && scope.row.status === 1" type="text" size="small" @click="old(scope.row.id, scope.row.name)">　老化</el-button>
          <el-button v-if="isAuth('equipment:equip:release') && scope.row.status === 2 && checkIfRelease(scope.row)" type="text" size="small" @click="releaseEquip(scope.row.id, scope.row.name)">下发</el-button>
          <el-button v-if="isAuth('equipment:equip:delete')" type="text" size="small" @click="deleteHandle(scope.row.id, scope.row.name)">删除</el-button>
          <el-button v-if="isAuth('equipment:equip:recycle') && scope.row.status !== 3" type="text" size="small" @click="recycleHandle(scope.row.id, scope.row.name)"><span style="color: lightpink">回收</span></el-button>
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
    <old v-if="oldVisible" ref="old"  @refreshDataList="getDataList"></old>
    <command v-if="commandVisible" ref="command"></command>
  </div>
</template>

<script>
  import AddOrUpdate from './equip-add-or-update'
  import GenerateQrcode from './equip-generate-qrcode'
  import Release from './equip-release'
  import { getUserId } from '@/utils/index'
  import Old from './equip-old'
  import Command from './equip-command'
  export default {
    data () {
      return {
        pickerOptions: {
          shortcuts: [{
            text: '昨天',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24)
              picker.$emit('pick', [start, end])
            }
          }, {
            text: '上周',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
              picker.$emit('pick', [start, end])
            }
          }, {
            text: '前一个月',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
              picker.$emit('pick', [start, end])
            }
          }, {
            text: '前三个月',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
              picker.$emit('pick', [start, end])
            }
          }]
        },
        value1: [new Date(2000, 10, 10, 10, 10), new Date(2000, 10, 11, 10, 10)],
        value2: '',
        dataForm: {
          key: ''
        },
        statusList: [
          {code: 0, label: '待绑定'},
          {code: 1, label: '待老化'},
          {code: 2, label: '待下发'},
          {code: 3, label: '待安装'},
          {code: 4, label: '工作中'},
          {code: 5, label: '已回收'}
        ],
        commands: [],
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
        releaseVisible: false,
        oldVisible: false,
        commandVisible: false
      }
    },
    components: {
      AddOrUpdate,
      GenerateQrcode,
      Release,
      Old,
      Command
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
            'mac': this.dataForm.mac,
            'moduleId': this.dataForm.moduleId,
            'hotelId': this.dataForm.hotelId,
            'agentName': this.dataForm.agentName,
            'status': this.dataForm.status,
            'expiredTimeFrom': this.dataForm.expiredTimeRange ? this.dataForm.expiredTimeRange[0].getTime() : null,
            'expiredTimeEnd': this.dataForm.expiredTimeRange ? this.dataForm.expiredTimeRange[1].getTime() : null
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
        this.$http({
          url: this.$http.adornUrl('/sys/command/data'),
          method: 'get',
          params: this.$http.adornParams()
        }).then(({data}) => {
          this.commands = data && data.code === 0 ? data.data : []
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
      recycleHandle (id, name) {
        var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.id
        })
        var names = name ? [name] : this.dataListSelections.map(item => {
          return item.name
        })
        this.$confirm(`确定对[设备:${names.join(',')}]进行[${name ? '回收' : '批量回收'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/equipment/equip/recycle'),
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
        let qrcode = true
        let devices = []
        let qrcodeDevice = []
        this.dataListSelections.forEach((item, index) => {
          if (this.checkIfRelease(item)) {
            valid = false
            devices.push(item.name)
          }
          if (item.status !== 1) {
            qrcode = false
            qrcodeDevice.push(item.name)
          }
        })
        if (!qrcode) {
          this.$alert(`设备:  [${qrcodeDevice.join(',')}] 状态不能下发`, '提示', {
            confirmButtonText: '确定',
            callback: () => {
              this.$message({
                type: 'warning',
                message: `设备:  [${qrcodeDevice.join(',')}] 状态不能下发`
              })
            }
          })
          return
        }
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
      },
      checkIfRelease (item) {
        const userId = getUserId()
        return !item.agentId && (item.hotelId || item.agentId !== Number(userId))
      },
      old (id, name) {
        let valid = true
        let devices = []
        let qrcode = true
        let qrcodeDevices = []
        this.dataListSelections.forEach((item, index) => {
          if (item.status > 1) {
            valid = false
            devices.push(item.name)
          } else if (item.status < 1) {
            qrcode = false
            qrcodeDevices.push(item.name)
          }
        })
        if (!qrcode) {
          this.$alert(`设备:  [${qrcodeDevices.join(',')}] 未绑定二维码`, '提示', {
            confirmButtonText: '确定',
            callback: () => {
              this.$message({
                type: 'warning',
                message: `设备:  [${qrcodeDevices.join(',')}] 未绑定二维码`
              })
            }
          })
          return
        }
        const msg = `设备:  [${devices.join(',')}] 已老化`
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
        this.oldVisible = true
        this.$nextTick(() => {
          this.$refs.old.init(ids, names)
        })
      },
      releaseCommand (command) {
        console.log(command)
        const names = []
        this.dataListSelections.forEach((item, index) => {
          names.push(item.name)
        })
        this.commandVisible = true
        this.$nextTick(() => {
          this.$refs.command.init(command, names)
        })
      }
    }
  }
</script>
