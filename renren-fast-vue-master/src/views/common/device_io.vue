<template>
  <div >
    <el-form>
      <h2>设备IO</h2>
    </el-form>
    <div id="rooms" class="site-doc-a site-doc-anim" style="margin-top: 10px">
      <div class="io_item">
        <div class="align_bottom">
          <icon-svg name="shezhi" class="el-icon-setting"></icon-svg>
          <icon-svg name="shezhi" class="el-icon-setting"></icon-svg>
          <icon-svg name="shezhi" class="el-icon-setting"></icon-svg>
          <icon-svg name="shezhi" class="el-icon-setting"></icon-svg>
        </div>
      </div>
      <div class="io_item"></div>
      <div class="io_item"></div>
      <div class="io_item"></div>
    </div>
  </div>
</template>

<style>
  .site-doc-a{
    font-size: 0;
  }


  .site-doc-a .io_item {
    position: relative;
    display: inline-block;
    vertical-align: middle;
    width: 9%;
    min-height: 80px;
    margin-left: 1%;
    margin-bottom: 1%;
    padding: 0px 15px;
    color: #fff;
    text-align: center;
    border-radius: 2px;
    line-height: 14px;
    font-size: 14px;
    background-color: #7497aa;
    /*background-color: rgba(170, 254, 75, 0.98);*/
  }

  .align_bottom{
    display: inline-block;
    position: absolute;
    height: auto;
    bottom: 5%;
    left: 3%;
  }
  .align_bottom svg{
   margin: 2% 0;
  }
</style>
<script>
  export default {
    computed: {
      navbarLayoutType: {
        get () { return this.$store.state.common.navbarLayoutType },
        set (val) { this.$store.commit('common/updateNavbarLayoutType', val) }
      },
      sidebarLayoutSkin: {
        get () { return this.$store.state.common.sidebarLayoutSkin },
        set (val) { this.$store.commit('common/updateSidebarLayoutSkin', val) }
      }
    }
  }
</script>

