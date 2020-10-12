<template>
  <el-dialog
    :title="!dataForm.ids ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="设备">
        <el-tag v-model="dataForm.names" v-for="name in names" :key="name" type="info" style="margin-left: 5px;width: 100px;text-align: center">
          {{name}}
        </el-tag>
      </el-form-item>
      <el-form-item label="下发类型">
        <el-select v-model="dataForm.releaseType"  @change="select" filterable clearable placeholder="请选择" style="width: 100%; position: relative">
          <el-option v-for="agentType in agentTypes" :key="agentType.value" :label="agentType.label" :value="agentType.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="代理" v-if="agentVisible">
        <el-select v-model="dataForm.agentId"  @change="$forceUpdate()" filterable clearable placeholder="请选择" style="width: 100%; position: relative">
          <el-option v-for="agent in agents" :key="agent.userId" :label="agent.agentName" :value="agent.userId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="酒店" v-if="hotelVisible">
        <el-select v-model="dataForm.hotelId"  @change="$forceUpdate()" filterable clearable placeholder="请选择" style="width: 100%; position: relative">
          <el-option v-for="hotel in hotels" :key="hotel.id" :label="hotel.name" :value="hotel.id"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="cancel">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        agentVisible: false,
        hotelVisible: false,
        agentTypes: [],
        ids: [],
        names: [],
        modules: [],
        agents: [],
        hotels: [],
        dataForm: {
          releaseType: null,
          agentId: null,
          hotelId: null
        },
        dataRule: {}
      }
    },
    methods: {
      init (ids, names) {
        this.ids = ids
        this.names = names
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          this.$http({
            url: this.$http.adornUrl('/equipment/equip/selectAgentAndHotel'),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            this.agentTypes = []
            let releaseAgent = {value: 1, label: '下发代理商'}
            let releaseHotel = {value: 2, label: '下发酒店'}
            if (!(data.agentLevel === 0)) {
              this.hotels = data && data.code === 0 ? data.hotels : []
              this.agentTypes.push(releaseHotel)
            }
            if (data.agentLevel !== 3 && data.agentLevel !== 4) {
              this.agentTypes.push(releaseAgent)
            }
            this.agents = data && data.code === 0 ? data.agents : []
          })
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            const chosenHotel = this.hotels.find((item) => {
              return item.id === this.dataForm.hotelId
            })
            const chosenAgent = this.agents.find((item) => {
              return item.userId === this.dataForm.agentId
            })
            console.log(chosenHotel)
            console.log(chosenAgent)
            this.$http({
              url: this.$http.adornUrl(`/equipment/equip/release`),
              method: 'post',
              data: this.$http.adornData({
                'ids': this.ids,
                'hotelId': this.dataForm.hotelId,
                'agentId': this.dataForm.agentId,
                'agentName': chosenAgent ? chosenAgent.agentName : null,
                'hotelName': chosenHotel ? chosenHotel.name : null
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.cancel()
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
      cancel () {
        this.visible = false
        this.dataForm.releaseType = null
        this.dataForm.hotelId = null
        this.dataForm.agentId = null
        this.agentVisible = false
        this.hotelVisible = false
      },
      select () {
        this.$forceUpdate()
        const releaseType = this.dataForm.releaseType
        if (releaseType === 1) {
          this.agentVisible = true
          this.hotelVisible = false
        } else {
          this.hotelVisible = true
          this.agentVisible = false
        }
      }
    }
  }
</script>
