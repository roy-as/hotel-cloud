<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="模块名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="设备模块名称"></el-input>
      </el-form-item>
      <el-form-item label="价格" prop="price">
        <el-input v-model="dataForm.price" placeholder="价格"></el-input>
      </el-form-item>
      <el-form-item label="协议" prop="protocol">
        <el-input v-model="dataForm.protocol" placeholder="协议"></el-input>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="dataForm.remark" placeholder="备注"></el-input>
      </el-form-item>
      <el-form-item label="设备图片" prop="picture">
        <el-upload
          class="avatar-uploader"
          :action="upload"
          :limit="2"
          :on-preview="true"
          :file-list="pictureList"
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
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  import { isDigit } from '@/utils/validate'
  export default {
    data () {
      var validateNum = (rule, value, callback) => {
        if (!isDigit(value)) {
          callback(new Error('只能输入数字'))
        } else {
          callback()
        }
      }
      return {
        visible: false,
        pictureList: [],
        dataForm: {
          id: 0,
          name: '',
          remark: '',
          protocol: '',
          price: ''
        },
        dataRule: {
          name: [
            { required: true, message: '设备模块名称不能为空', trigger: 'blur' }
          ],
          price: [
            { required: true, message: '价格不能为空', trigger: 'blur' },
            { validator: validateNum, trigger: 'blur' }
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
              url: this.$http.adornUrl(`/equipment/equipModule/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.name = data.equipModule.name
                this.dataForm.protocol = data.equipModule.protocol
                this.dataForm.price = data.equipModule.price
                this.dataForm.remark = data.equipModule.remark
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
            formData.append('price', this.dataForm.price)
            formData.append('protocol', this.dataForm.protocol)
            formData.append('remark', this.dataForm.remark)
            if (this.pictureList && this.pictureList[0]) {
              formData.append('picture', this.pictureList[0].raw)
            }
            this.$http({
              url: this.$http.adornUrl(`/equipment/equipModule/${!this.dataForm.id ? 'save' : 'update'}`),
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
        this.pictureList = fileList
      },
      beforeUpload (file) {
        this.formData.append('file', file)
        return false
      }
    }
  }
</script>
