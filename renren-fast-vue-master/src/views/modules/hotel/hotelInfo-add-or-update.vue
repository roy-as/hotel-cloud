<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()"
             label-width="80px">
      <el-form-item label="酒店名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="酒店名称"></el-input>
      </el-form-item>
      <el-form-item label="地区" prop="area">
        <el-cascader v-model="dataForm.area" :options="cityOption" style="width: 100%" clearable></el-cascader>
      </el-form-item>
      <el-form-item label="详细地址" prop="address">
        <el-input v-model="dataForm.address" placeholder="详细地址"></el-input>
      </el-form-item>
      <el-form-item label="酒店描述" prop="remark">
        <el-input v-model="dataForm.remark" placeholder="酒店描述"></el-input>
      </el-form-item>
      <el-form-item label="联系电话" prop="mobile">
        <el-input v-model="dataForm.mobile" placeholder="联系电话"></el-input>
      </el-form-item>
      <el-form-item label="温馨提示" prop="notice">
        <el-input v-model="dataForm.notice" placeholder="温馨提示"></el-input>
      </el-form-item>
      <el-form-item label="酒店服务" prop="service">
        <el-input v-model="dataForm.service" placeholder="酒店服务"></el-input>
      </el-form-item>
      <el-form-item label="状态" size="mini" prop="status">
        <el-radio-group v-model="dataForm.status">
          <el-radio :label="0">禁用</el-radio>
          <el-radio :label="1">正常</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="Logo" prop="logoUrl">
        <el-upload
          class="avatar-uploader"
          :action="upload"
          :limit="2"
          :on-preview="true"
          :file-list="logoList"
          :with-credentials="true"
          :auto-upload="false"
          :before-upload="beforeUpload"
          :on-change="logoChangeUpload"
          accept=".jpg, .png, .jpeg, bmp, .tif, .tga"
          list-type="picture">
          <el-button size="small" type="primary">点击上传</el-button>
          <div slot="tip" class="el-upload__tip">只能上传jpg/png/jpeg/tif/tga文件</div>
        </el-upload>
      </el-form-item>
      <el-form-item label="全景图" prop="logoUrl">
        <el-upload
          class="avatar-uploader"
          :action="upload"
          :on-preview="true"
          :file-list="fullViewList"
          :with-credentials="true"
          :auto-upload="false"
          :before-upload="beforeUpload"
          :on-change="fullViewChangeUpload"
          accept=".jpg, .png, .jpeg, bmp, .tif, .tga"
          list-type="picture">
          <el-button size="small" type="primary">点击上传</el-button>
          <div slot="tip" class="el-upload__tip">只能上传jpg/png/jpeg/tif/tga文件</div>
        </el-upload>
      </el-form-item>
      <el-form-item label="酒店图片" prop="logoUrl">
        <el-upload
          class="avatar-uploader"
          :action="upload"
          :on-preview="true"
          :file-list="hotelPictureList"
          :with-credentials="true"
          :auto-upload="false"
          :before-upload="beforeUpload"
          :on-change="hotelPictureChangeUpload"
          accept=".jpg, .png, .jpeg, bmp, .tif, .tga"
          list-type="picture">
          <el-button size="small" type="primary">点击上传</el-button>
          <div slot="tip" class="el-upload__tip">只能上传jpg/png/jpeg/tif/tga文件</div>
        </el-upload>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  const city = require('@/utils/constant/city.json')
  export default {
    data () {
      var validateArea = (rule, value, callback) => {
        if (value.length === 0) {
          callback(new Error('地区不能为空'))
        }
        callback()
      }
      return {
        cityOption: city,
        visible: false,
        logoList: [],
        fullViewList: [],
        hotelPictureList: [],
        dataForm: {
          id: 0,
          name: '',
          logoUrl: '',
          address: '',
          remark: '',
          mobile: '',
          notice: '',
          createTime: '',
          updateTime: '',
          createBy: '',
          updateBy: '',
          service: '',
          area: [],
          status: 1
        },
        dataRule: {
          name: [
            { required: true, message: '酒店名称不能为空', trigger: 'blur' }
          ],
          address: [
            { required: true, message: '门店地址不能为空', trigger: 'blur' }
          ],
          mobile: [
            { required: true, message: '联系电话不能为空', trigger: 'blur' }
          ],
          status: [
            { required: true, message: '状态不能为空', trigger: 'blur' }
          ],
          area: [
            {required: true, message: '地区不能为空', trigger: 'blur'},
            { validator: validateArea, trigger: 'blur' }
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
              url: this.$http.adornUrl(`/hotel/hotelInfo/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.name = data.hotelInfo.name
                this.dataForm.address = data.hotelInfo.address
                this.dataForm.remark = data.hotelInfo.remark
                this.dataForm.mobile = data.hotelInfo.mobile
                this.dataForm.notice = data.hotelInfo.notice
                this.dataForm.service = data.hotelInfo.service
                this.dataForm.area = JSON.parse(data.hotelInfo.area)
                this.dataForm.status = data.hotelInfo.status
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            const formData = new FormData()
            if (this.dataForm.id) {
              formData.append('id', this.dataForm.id)
            }
            formData.append('name', this.dataForm.name)
            const areas = this.dataForm.area
            areas.forEach((area, index) => {
              formData.append('area', area)
            })
            console.log(this.logoList)
            formData.append('address', this.dataForm.address)
            formData.append('mobile', this.dataForm.mobile)
            formData.append('notice', this.dataForm.notice)
            formData.append('service', this.dataForm.service)
            formData.append('status', this.dataForm.status)
            if (this.logoList[0]) {
              formData.append('logo', this.logoList[0].raw)
            }
            if (this.fullViewList && this.fullViewList.length > 0) {
              this.fullViewList.forEach((item, index) => {
                formData.append('fullViews', item.raw)
              })
            }
            if (this.hotelPictureList && this.hotelPictureList.length > 0) {
              this.hotelPictureList.forEach((item, index) => {
                formData.append('hotelPictures', item.raw)
              })
            }
            this.$http({
              url: this.$http.adornUrl(`/hotel/hotelInfo/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              headers: {'content-type': 'multipart/form-data'},
              data: formData
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.logoList = []
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
      logoChangeUpload (file, fileList) {
        this.logoList = fileList
      },
      fullViewChangeUpload (file, fileList) {
        this.fullViewList = fileList
      },
      hotelPictureChangeUpload (file, fileList) {
        this.hotelPictureList = fileList
      },
      beforeUpload (file) {
        this.formData.append('file', file)
        return false
      }
    }
  }
</script>
