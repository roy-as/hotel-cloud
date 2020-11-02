<template>
  <div>
    <el-dialog
      :title="!dataForm.id ? '新增' : '修改'"
      :close-on-click-modal="false"
      :visible.sync="visible"
    >
      <el-form
        :model="dataForm"
        :rules="dataRule"
        ref="dataForm"
        @keyup.enter.native="dataFormSubmit()"
        label-width="80px"
      >
        <el-form-item
          label="酒店"
          prop="hotelId"
          :class="{ 'is-required': !dataForm.id }"
        >
          <el-select
            v-model="dataForm.hotelId"
            @change="$forceUpdate()"
            filterable
            clearable
            placeholder="请选择"
            style="width: 100%; position: relative"
            :disabled="!!dataForm.id"
          >
            <el-option
              v-for="hotel in hotels"
              :key="hotel.id"
              :label="hotel.name"
              :value="hotel.id"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          label="安装公司"
          prop="installationId"
          :class="{ 'is-required': !dataForm.id }"
        >
          <el-select
            v-model="dataForm.installationId"
            @change="$forceUpdate()"
            filterable
            clearable
            placeholder="请选择"
            style="width: 100%; position: relative"
            :disabled="!!dataForm.id"
          >
            <el-option
              v-for="installation in installations"
              :key="installation.id"
              :label="installation.name"
              :value="installation.id"
            >
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item
          label="设备类型"
          prop="deviceType"
          :class="{ 'is-required': !dataForm.id }"
        >
          <!-- <el-select
          v-model="dataForm.deviceType"
          @change="deviceTypeChange()"
          placeholder="请选择"
          style="width: 100%; position: relative"
          :disabled="!!dataForm.id"
        >
          <el-option
            v-for="type in deviceType"
            :key="type.id"
            :label="type.name"
            :value="type.id"
          >
          </el-option>
        </el-select> -->

          <div class="flex" style="flex-wrap: wrap;">
            <div v-for="(val,key) in deviceTypeList" class="flex device-box">
            {{ val.name }}*{{ val.shopNumber }}
            <i class="el-icon-circle-close-outline" @click="clearDevice(key)" style="cursor: pointer;"></i>
            </div>
          </div>
          <el-button type="primary" @click="openchild">请选择</el-button>
        </el-form-item>

        <el-form-item
          label="支付方式"
          prop="payType"
          :class="{ 'is-required': !dataForm.id }"
        >
          <el-select
            v-model="dataForm.payType"
            @change="$forceUpdate()"
            filterable
            clearable
            placeholder="请选择"
            style="width: 100%; position: relative"
            :disabled="!!dataForm.id"
          >
            <el-option
              v-for="payType in payTypes"
              :key="payType.value"
              :label="payType.label"
              :value="payType.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="优惠券" prop="couponSn">
          <el-input
            v-model="dataForm.couponSn"
            placeholder="优惠券序列号"
          ></el-input>
        </el-form-item>
        <el-form-item label="支付金额" prop="amount">
          <el-input v-model="dataForm.amount" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="实际金额" prop="realAmount">
          <el-input v-model="dataForm.realAmount" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="dataForm.remark" placeholder="备注"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
      </span>
    </el-dialog>
    <device-choose ref="chooseDevice"></device-choose>
  </div>
</template>

<script>
import DeviceChoose from "./devicechoose";
export default {
  data() {
    return {
      visible: false,
      hotels: [],
      installations: [],

      max: 10,

      payTypes: [
        {
          label: "支付宝",
          value: 1,
        },
        {
          label: "微信",
          value: 2,
        },
        {
          label: "银行卡",
          value: 3,
        },
      ],
      dataForm: {
        id: 0,
        hotelId: "",
        installationId: "",
        payType: "",
        couponSn: "",
        amount: "",
        realAmount: "",
        remark: "",
        deviceType: "", // 0主机1设备
        deviceName: "", // 设备名称ID
      },
      dataRule: {
        hotelId: [{ required: true, message: "酒店不能为空", trigger: "blur" }],
        installationId: [
          { required: true, message: "安装公司不能为空", trigger: "blur" },
        ],
        payType: [
          { required: true, message: "支付方式不能为空", trigger: "blur" },
        ],
        deviceType: [
          { required: true, message: "设备类型不能为空", trigger: "blur" },
        ],
        deviceName: [
          { required: true, message: "设备名称不能为空", trigger: "blur" },
        ],
        deviceNum: [
          { required: true, message: "设备数量不能为空", trigger: "blur" },
        ],
      },
      deviceType: [], // 设备类型select
      deviceList: [], // 设备名称select
      device: [], // 智能设备
      equip: [], // 智能主机
      deviceTypeList:[]
    };
  },
  components: {
    DeviceChoose,
  },
  methods: {
    clearDevice(key){
      let arr = this.deviceTypeList.splice(key,1)[0];
      for(let i=0;i<this.$refs.chooseDevice.tableData.length;i++){
          if(arr.type==this.$refs.chooseDevice.tableData[i].type){
            for(let j=0;j<this.$refs.chooseDevice.tableData[i].data.length;j++){
              console.log()
              if(this.$refs.chooseDevice.tableData[i].data[j].name==arr.name){
                this.$refs.chooseDevice.tableData[i].data[j].shopNumber=0
              }
            }
          }
        }
    },
    openchild() {
      this.$refs.chooseDevice.openModal();
    },
    init(id) {
      this.dataForm.id = id || 0;
      this.visible = true;
      this.$nextTick(() => {
        this.$refs["dataForm"].resetFields();
        this.$http({
          url: this.$http.adornUrl("/org/select"),
          method: "get",
          params: this.$http.adornParams({
            userType: 2,
          }),
        }).then(({ data }) => {
          this.hotels = data && data.code === 0 ? data.data : [];
        });
        this.$http({
          url: this.$http.adornUrl("/org/select"),
          method: "get",
          params: this.$http.adornParams({
            userType: 3,
          }),
        }).then(({ data }) => {
          this.installations = data && data.code === 0 ? data.data : [];
        });

        // 获取设备类型&列表
        this.$http({
          url: this.$http.adornUrl("/order/order/equipList"),
          method: "get",
          params: this.$http.adornParams(),
        }).then(({ data }) => {
          this.equip = data.equip || [];
          this.device = data.device || [];

          let type = [];
          if (data.equip && data.equip.length > 0) {
            type.push({
              id: 0,
              name: "智能主机",
            });
          }
          if (data.device && data.device.length > 0) {
            type.push({
              id: 1,
              name: "智能设备",
            });
          }
          this.deviceType = type;
        });

        if (this.dataForm.id) {
          this.$http({
            url: this.$http.adornUrl(`/order/order/info/${this.dataForm.id}`),
            method: "get",
            params: this.$http.adornParams(),
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.dataForm.hotelId = data.order.hotelId;
              this.dataForm.hotelName = data.order.hotelName;
              this.dataForm.installationId = data.order.installationId;
              this.dataForm.installationName = data.order.installationName;
              this.dataForm.payType = data.order.payType;
              this.dataForm.couponSn = data.order.couponSn;
              this.dataForm.couponId = data.order.couponId;
              this.dataForm.remark = data.order.remark;
            }
          });
        }
      });
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          const hotel = this.hotels.find((item) => {
            return item.id === this.dataForm.hotelId;
          });
          const installation = this.installations.find((item) => {
            return item.id === this.dataForm.installationId;
          });
          this.$http({
            url: this.$http.adornUrl(
              `/order/order/${!this.dataForm.id ? "save" : "update"}`
            ),
            method: "post",
            data: this.$http.adornData({
              id: this.dataForm.id || undefined,
              hotelId: this.dataForm.hotelId,
              hotelName: hotel.name,
              installationId: this.dataForm.installationId,
              installationName: installation.name,
              payType: this.dataForm.payType,
              couponSn: this.dataForm.couponSn,
              remark: this.dataForm.remark,
            }),
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.visible = false;
                  this.$emit("refreshDataList");
                },
              });
            } else {
              this.$message.error(data.msg);
            }
          });
        }
      });
    },
    deviceTypeChange() {
      this.$forceUpdate();
      this.dataForm.deviceName = "";
      if (this.dataForm.deviceType == 0) {
        // 智能主机
        this.deviceList = this.equip;
      } else {
        // 智能设备
        this.deviceList = this.device;
      }
    },
    deviceChange() {
      this.$forceUpdate();
      if (this.dataForm.deviceType == 0) {
        this.max = 1;
      } else {
        const device = this.deviceList.find((item) => {
          return item.id === this.dataForm.deviceName;
        });
        this.max = device.amount;
      }
    },
  },
};
</script>

<style>
.device-box{
  border: 1px solid #eee;
  padding: 0 5px;
  margin-right: 5px;
  margin-bottom: 5px;
}
</style>