<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-button @click="getDataList()">查询</el-button>
        <el-button v-if="isAuth('sys:qrcode:save')" type="primary" @click="downloadTemplate()">模版导出</el-button>
        <el-button  v-if="isAuth('sys:qrcode:save')" type="primary" @click="uploadHandle()">批量导入</el-button>
        <el-button v-if="isAuth('sys:qrcode:list')" type="primary" @click="download()" :disabled="dataListSelections.length <= 0">二维码下载</el-button>
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
        prop="info"
        header-align="center"
        align="center"
        label="二维码信息">
      </el-table-column>

      <el-table-column
        prop="url"
        header-align="center"
        align="center"
        label="二维码">
        <template slot-scope="scope">
          <span v-if="scope.row.url">
            <img :src="imgUrl(scope.row)" :preview="scope.$index" height="70" width="70"/>
          </span>
        </template>
      </el-table-column>
      <el-table-column
        prop="equipName"
        header-align="center"
        align="center"
        label="绑定设备">
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
    <upload v-if="uploadVisible" ref="upload" @refreshDataList="getDataList"></upload>
  </div>
</template>

<script>
  import Upload from './qrcode-upload'
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
        uploadVisible: false
      }
    },
    components: {
      Upload
    },
    activated () {
      this.getDataList()
    },
    methods: {
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/sys/qrcode/list'),
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
      downloadTemplate () {
        this.$http({
          url: this.$http.adornUrl('/sys/qrcode/downloadTemplate'),
          method: 'get',
          responseType: 'blob'
        }).then(({data}) => {
          this.downloadFile(data, '二维码导入模版.xlsx')
        }).catch(err => {
          console.log('err: ', err)
        })
      },
      download () {
        let params = ''
        console.log(this.dataListSelections)
        this.dataListSelections.forEach((item, index) => {
          params += 'ids=' + item.id + '&'
        })
        this.$http({
          url: this.$http.adornUrl('/sys/qrcode/download?' + params + 't=' + new Date().getTime()),
          method: 'get',
          responseType: 'blob'
        }).then(({data}) => {
          this.downloadFile(data, new Date().getTime() + '.zip')
        }).catch(err => {
          console.log('err: ', err)
        })
      },
      downloadFile (data, fileName) {
        if (!data) {
          this.$message.error('下载内容为空')
          return
        }
        let url = window.URL.createObjectURL(new Blob([data]))
        let link = document.createElement('a')
        link.style.display = 'none'
        link.href = url
        link.setAttribute('download', fileName)

        document.body.appendChild(link)
        link.click()
        // 释放URL对象所占资源
        window.URL.revokeObjectURL(url)
        // 用完即删
        document.body.removeChild(link)
      },
      // 上传文件
      uploadHandle () {
        this.uploadVisible = true
        this.$nextTick(() => {
          this.$refs.upload.init()
        })
      },
      imgUrl: function (row) {
        if (!row.url) {
          return ''
        }
        return row.url
      }
    }
  }
</script>
