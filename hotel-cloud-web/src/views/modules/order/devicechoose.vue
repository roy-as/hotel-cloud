<template>
  <el-dialog
    title="设备选择"
    :visible.sync="dialogVisible"
    width="50%"
    :show-close="true"
  >
    <table cellspacing="0" class="device-table">
      <thead>
        <tr>
          <td>设备类型</td>
          <td>设备名称</td>
          <td>单价</td>
          <td>最大可用数量</td>
          <td>购买数量</td>
        </tr>
      </thead>
      <tbody v-for="(val, j) in tableData">
        <tr v-for="(item, key) in val.data">
          <td v-if="key === 0" :rowspan="val.data.length">{{ val.type }}</td>
          <td>{{ item.name }}{{ val.key }}</td>
          <td>{{ item.price }}</td>
          <td>{{ item.maxNumber }}</td>
          <td class="flex end-td">
            <el-button
              :disabled="item.shopNumber <= 0"
              @click="item.shopNumber--"
              ><i class="el-icon-minus"></i
            ></el-button>
            <el-input
              v-model="item.shopNumber"
              min="0"
              @input="onlyNumber(item.shopNumber, j, key)"
              :max="item.maxNumber"
            ></el-input>
            <el-button
              :disabled="item.shopNumber >= item.maxNumber"
              @click="item.shopNumber++"
              ><i class="el-icon-plus"></i
            ></el-button>
          </td>
        </tr>
      </tbody>
    </table>
    <div class="flex modal-footer-button">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button @click="ok" type="primary">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'devicechoose',
  data () {
    return {
      dialogVisible: false,
      tableData: []
    }
  },
  methods: {
    ok () {
      let arr = []
      this.$parent.dataForm.amount = 0
      this.$parent.dataForm.realAmount = 0
      for (let i = 0; i < this.tableData.length; i++) {
        for (let j = 0; j < this.tableData[i].data.length; j++) {
          if (this.tableData[i].data[j].shopNumber > 0) {
            let obj = this.tableData[i].data[j]
            obj.type = this.tableData[i].type
            arr.push(obj)
          }
        }
      }
      this.$parent.deviceTypeList = arr
      arr.forEach((item, index) => {
        this.$parent.dataForm.amount += item.price * item.shopNumber
        this.$parent.dataForm.realAmount += item.price * item.shopNumber
      })
      this.dialogVisible = false
    },
    onlyNumber (val, key, key1) {
      if (!Number(val)) {
        this.tableData[key].data[key1].shopNumber = 0
        return
      }
      if (val <= 0) {
        this.tableData[key].data[key1].shopNumber = 0
        return
      }
      if (val >= this.tableData[key].data[key1].maxNumber) {
        this.tableData[key].data[key1].shopNumber = this.tableData[key].data[key1].maxNumber
      }
    },
    openModal (data) {
      this.dialogVisible = true
      this.tableData = data
      console.log(this.tableData)
    }
  }
}
</script>

<style>
.device-table {
  border: 1px solid #ebebeb;
  width: 100%;
}
.device-table td {
  border-bottom: 1px solid #ebeef5;
  border-right: 1px solid #ebeef5;
  text-align: center;
}
.device-table td input {
  border-bottom: 1px solid #ebeef5;
  border-right: 1px solid #ebeef5;
  text-align: center;
}
.device-table td .el-input {
  width: 100px;
}
.device-table .end-td {
  justify-content: center;
}
.modal-footer-button {
  margin-top: 20px;
  justify-content: flex-end;
}
.flex {
  display: flex;
  align-items: center;
}
</style>
