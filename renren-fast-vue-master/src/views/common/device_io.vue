<template>
  <div>
    <el-form>
      <h2>设备IO</h2>
    </el-form>
    <div id="rooms" class="site-doc-a site-doc-anim" style="margin-top: 10px">
      <div class="io_item">
        <h2 class="label-name">测试数据</h2>
        <div class="align_bottom">
          <div class="icons">
            <icon-svg name="bind"></icon-svg>
          </div>
          <div class="icons">
            <icon-svg name="high"></icon-svg>
          </div>
          <div class="icons">
            <icon-svg name="long-press"></icon-svg>
          </div>
          <div class="icons">
            <icon-svg name="close-delay"></icon-svg>
          </div>
          <div class="io-button">继电器99</div>
        </div>
      </div>
      <div class="io_item">
      </div>
      <div class="io_item"></div>
      <div class="io_item"></div>
    </div>
  </div>
</template>

<style>
.site-doc-a {
  font-size: 0;
}


.site-doc-a .io_item {
  position: relative;
  display: inline-block;
  vertical-align: middle;
  width: 12%;
  min-height: 80px;
  margin-left: 1%;
  margin-bottom: 1%;
  padding: 0px 15px;
  color: #fff;
  text-align: center;
  border-radius: 2px;
  line-height: 14px;
  font-size: 14px;
  background-color: #84A9AA;
  /*background-color: rgba(170, 254, 75, 0.98);*/
}

.align_bottom {
  padding: 5% 0;
  width: 100%;
  background: #0A5455;
  display: inline-block;
  position: absolute;
  max-height: 35px;
  height: auto;
  bottom: 0;
  left: 0;
}

.icons {
  float: left;
  height: 100%;
  background: #C1D0D0;
  border: #C1D0D0;
  border-radius: 2px;
  display: inline-block;
  margin: 2px;
}

.icons svg {

  margin: 0 auto;
  float: left;
  left: 0;
  padding: 2px;
}


.label-name {
  font-weight: normal;
  color: #1D110D;
}
.el-button span{

  font-size: 5px;
}

.io-button {
  float: right;
  margin-right: 3%;
  padding: 10px 5px;
  /*flex 布局*/
  display: flex;
  /*实现垂直居中*/
  align-items: center;
  /*实现水平居中*/
  justify-content: center;
  border-radius: 2px;
  min-width: 40px;
  font-size: 6px;
  max-height: 10px;
  color: #fff;
  background-color: #009843;
  border-color: #009843;
  cursor: pointer;
}
.io-button:hover{
  background-color: #52926d;
  border-color: #52926d;
}


</style>
<script>
export default {
  computed: {
    navbarLayoutType: {
      get () {
        return this.$store.state.common.navbarLayoutType
      },
      set (val) {
        this.$store.commit('common/updateNavbarLayoutType', val)
      }
    },
    sidebarLayoutSkin: {
      get () {
        return this.$store.state.common.sidebarLayoutSkin
      },
      set (val) {
        this.$store.commit('common/updateSidebarLayoutSkin', val)
      }
    }
  }
}
</script>

