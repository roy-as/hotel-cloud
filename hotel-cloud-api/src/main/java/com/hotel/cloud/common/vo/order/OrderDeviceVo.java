package com.hotel.cloud.common.vo.order;

import com.hotel.cloud.modules.equipment.entity.DeviceEntity;
import com.hotel.cloud.modules.order.entity.CouponEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeviceVo {

    private CouponEntity coupon;

    private List<DeviceEntity> devices;
}
