<template>
  <el-dialog
    :title="dataForm.pictureType == 1 ? '全景图' : '酒店图片'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @open='openImgDialog'>
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()"
             label-width="80px">
      <el-form-item>
        <div v-for="pictures in pictureList">
          <span v-for="(picture ,index) in pictures">
            <img :src="picture.url" :preview="index" width="140" height="160" style="margin: 10px">
            <i class="el-icon-delete"></i>
          </span>
        </div>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        pictureList: [],
        dataForm: {
          id: 0
        },
        dataRule: {}
      }
    },
    methods: {
      init: function (id, pictureType) {
        this.pictureList = []
        this.dataForm.id = id || 0
        this.dataForm.pictureType = pictureType
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/hotel/hotelInfo/getPicture`),
              method: 'get',
              params: this.$http.adornParams({
                id: this.dataForm.id,
                pictureType: pictureType

              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                const result = data.data
                let count = Math.ceil(result.length / 4)
                for (let i = 1; i <= count; i++) {
                  let index = i - 1
                  if (i !== count) {
                    this.pictureList.push(result.slice(index * 4, index * 4 + 4))
                  } else {
                    this.pictureList.push(result.slice(index * 4, result.length))
                  }
                }
                console.log(this.pictureList)
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
      openImgDialog () {
        this.$previewRefresh()
      }
    }
  }
</script>
<style lang="scss">
  .pswp{
    z-index: 9999 !important;
  }
</style>
