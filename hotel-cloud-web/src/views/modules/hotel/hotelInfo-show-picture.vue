<template>
  <el-dialog
    :title="dataForm.pictureType == 1 ? '全景图' : '酒店图片'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    class="imgDialog"
    @close="dialogClose">
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="80px">
      <el-form-item>
        <div>
          <span
            class="imgWrap"
            v-for="(picture, index) in pictureList"
            :key="picture.id">
            <span
              :class="[
                selectedPicList.includes(picture.id) ? 'checked' : '',
                'imgCheck',
              ]"
              @click="selectPic(picture.id)">
              <i class="el-icon-check"></i>
            </span>
            <img :src="picture.url" :preview="index"/>
          </span>
          <span class="imgWrap icon" v-if="pictureList.length !== 0">
            <el-popover
              ref="popover"
              placement="top"
              width="180"
              v-model="popoverVisible">
              <p>确认删除选中的图片？</p>
              <div style="text-align: right; margin: 0">
                <el-button
                  size="mini"
                  type="text"
                  @click="popoverVisible = false">取消</el-button>
                <el-button type="primary" size="mini" @click="delPicSubmit">确定</el-button>
              </div>
            </el-popover>
            <i class="el-icon-delete"
              v-if="selectedPicList.length === 0"
              @click="delPic"></i>
            <i class="el-icon-delete"
              v-if="selectedPicList.length !== 0"
              @click="delPic" v-popover:popover></i>
          </span>
          <span class="imgWrap icon">
            <el-upload
              action="#"
              :http-request="imgUpload"
              :show-file-list="false"
              :on-success="handleImgUploadSuccess"
              :before-upload="beforeImgUpload">
              <i class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </span>
        </div>
        <div v-if="pictureList.length == 0">
          <span class="imgWrap icon">
            <el-upload
              action="#"
              :http-request="imgUpload"
              :show-file-list="false"
              :on-success="handleImgUploadSuccess"
              :before-upload="beforeImgUpload">
              <i class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </span>
        </div>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        pictureList: [],
        selectedPicList: [],
        dataForm: {
          id: 0
        },
        dataRule: {},
        popoverVisible: false
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
                hotelId: this.dataForm.id,
                pictureType: pictureType
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.pictureList = data.data
                this.$previewRefresh()
              }
            })
          }
        })
      },
      // 模态框关闭
      dialogClose () {
        this.selectedPicList = []
      },
      // 选中图片
      selectPic (id) {
        let idx = this.selectedPicList.indexOf(id)
        idx === -1
          ? this.selectedPicList.push(id)
          : this.selectedPicList.splice(idx, 1)
      },
      // 删除图片
      delPic () {
        if (this.selectedPicList.length === 0) {
          this.popoverVisible = false
          this.$message({
            message: '请先选择图片',
            type: 'warning'
          })
        } else {
          this.popoverVisible = true
        }
      },
      delPicSubmit () {
        this.popoverVisible = false
        this.$http({
          url: this.$http.adornUrl(`/hotel/hotelInfo/deletePicture`),
          method: 'post',
          data: {
            id: this.dataForm.id,
            pictureIds: this.selectedPicList
          }
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.$message({
              message: '操作成功',
              type: 'success',
              duration: 1500
            })
            this.selectedPicList = []
            this.init(this.dataForm.id, this.dataForm.pictureType)
          } else {
            this.$message.error(data.msg)
          }
        })
      },
      imgUpload (data) {
        const formData = new FormData()
        formData.append('id', this.dataForm.id)
        if (this.dataForm.pictureType === 1) {
          formData.append('fullViews', data.file)
        }
        if (this.dataForm.pictureType === 2) {
          formData.append('hotelPictures', data.file)
        }
        this.$http({
          url: this.$http.adornUrl(`/hotel/hotelInfo/update`),
          method: 'post',
          headers: {'content-type': 'multipart/form-data'},
          data: formData
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.$message({
              message: '操作成功',
              type: 'success',
              duration: 1500
            })
            this.init(this.dataForm.id, this.dataForm.pictureType)
            this.$previewRefresh()
          } else {
            this.$message.error(data.msg)
          }
        })
      },
      handleImgUploadSuccess (res, file) {
        this.imageUrl = URL.createObjectURL(file.raw)
      },
      beforeImgUpload (file) {
        const isJPGorPNG =
          file.type === 'image/jpeg' || file.type === 'image/jpg' || file.type === 'image/png' || file.type === 'image/bmp' || file.type === 'image/tif' || file.type === 'image/tga'
        const isLt2M = file.size / 1024 / 1024 < 2

        if (!isJPGorPNG) {
          this.$message.error('图片格式只能是JPG、png、jpeg、bmp、tif、tga格式!')
        }
        if (!isLt2M) {
          this.$message.error('图片大小不能超过2MB!')
        }
        return isJPGorPNG && isLt2M
      }
    }
  }
</script>
<style lang="scss">
  .imgDialog {
    .imgWrap {
      position: relative;
      display: inline-block;
      width: 160px;
      height: 90px;
      margin: 10px;
      background: rgba(0, 0, 0, 0.2);
      text-align: center;
      vertical-align: top;
      overflow: hidden;

      &.icon {
        i {
          font-size: 36px;
          line-height: 90px;
          cursor: pointer;
        }
      }

      img {
        object-fit: cover;
        height: 100%;
        width: 100%;
      }

      .imgCheck {
        width: 50px;
        height: 50px;
        transform: rotate(45deg);
        position: absolute;
        right: -25px;
        top: -25px;
        background-color: rgba(0, 0, 0, 0.4);
        cursor: pointer;

        i {
          position: relative;
          font-size: 20px;
          font-weight: bold;
          top: 26px;
          transform: rotate(-45deg);
          color: #fff;
        }

        &.checked {
          background-color:rgba(23,179,163,.9);
          i {
            color: #fff;
          }
        }
      }
    }
  }

  .pswp {
    z-index: 9999 !important;
  }
</style>
