package com.hotel.cloud.modules.order.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.cloud.common.enums.EquipStatusEnum;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.enums.FlagEnum;
import com.hotel.cloud.common.enums.OrderStatusEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.*;
import com.hotel.cloud.common.vo.order.CouponValidate;
import com.hotel.cloud.common.vo.order.CouponValidateVo;
import com.hotel.cloud.common.vo.order.OrderDeviceVo;
import com.hotel.cloud.common.vo.order.OrderVo;
import com.hotel.cloud.modules.equipment.entity.DeviceEntity;
import com.hotel.cloud.modules.equipment.entity.EquipEntity;
import com.hotel.cloud.modules.equipment.service.DeviceService;
import com.hotel.cloud.modules.equipment.service.EquipService;
import com.hotel.cloud.modules.order.dao.OrderDao;
import com.hotel.cloud.modules.order.entity.CouponEntity;
import com.hotel.cloud.modules.order.entity.OrderDeviceMappingEntity;
import com.hotel.cloud.modules.order.entity.OrderEntity;
import com.hotel.cloud.modules.order.service.CouponService;
import com.hotel.cloud.modules.order.service.OrderDeviceMappingService;
import com.hotel.cloud.modules.order.service.OrderService;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Resource
    private EquipService equipService;

    @Resource
    private DeviceService deviceService;

    @Resource
    private CouponService couponService;

    @Resource
    private OrderDeviceMappingService orderDeviceMappingService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveOrder(OrderVo vo) {
        OrderEntity order = vo.getEntity();
        if (!ShiroUtils.isAgent()) {
            throw new RRException(ExceptionEnum.NOT_AUTHENTICATION);
        }
        CouponValidate couponValidate = new CouponValidate(vo.getCouponSn(), vo.getEquips(), vo.getAmount());
        OrderDeviceVo orderDeviceVo = this.couponService.check(couponValidate, StringUtils.isNotBlank(vo.getCouponSn()));
        CouponEntity coupon = orderDeviceVo.getCoupon();
        if (StringUtils.isNotBlank(vo.getCouponSn())) {
            // 获取优惠金额
            order.setRealAmount(coupon.getRealAmount());
            order.setAmount(coupon.getTotalAmount());
        } else {
            order.setRealAmount(coupon.getTotalAmount());
            order.setAmount(coupon.getTotalAmount());
        }
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        order.setAgentId(loginUser.getOrgId());
        order.setAgentName(loginUser.getOrgName());
        order.setCreateBy(loginUser.getUsername());
        order.setCreateTime(new Date());
        String orderId = CommonUtils.genOrderId();
        order.setId(orderId);
        // 保存订单
        this.save(order);
        List<CouponValidateVo> equips = vo.getEquips();

        // 订单与设备的映射
        List<OrderDeviceMappingEntity> mappings = equips.stream().map(validateVo -> {
            OrderDeviceMappingEntity mappingEntity = new OrderDeviceMappingEntity();
            mappingEntity.setOrderId(order.getId());
            mappingEntity.setDeviceId(validateVo.getId());
            mappingEntity.setDeviceType(validateVo.getDeviceType());
            mappingEntity.setShoppingNumber(validateVo.getShopNumber());
            return mappingEntity;
        }).collect(Collectors.toList());
        // 保存设备订单映射
        this.orderDeviceMappingService.saveBatch(mappings);
        // 当使用优惠券时，订单关联优惠券
        if (null != coupon.getId()) {
            coupon.setOrderId(orderId);
            this.couponService.updateById(coupon);
        }

        // 修改设备剩余库存
        Map<Long, Integer> equipMap = equips.stream().collect(
                Collectors.toMap(CouponValidateVo::getId, CouponValidateVo::getShopNumber)
        );
        List<DeviceEntity> devices = orderDeviceVo.getDevices();
        if (CollectionUtil.isNotEmpty(devices)) {
            for (DeviceEntity device : devices) {
                int remain = device.getAmount() - equipMap.get(device.getId());
                if (remain < 0) {
                    throw new RRException(ExceptionEnum.STOCK_NOT_ENOUGH);
                }
                device.setAmount(remain);
            }
        }
        this.deviceService.updateBatchById(devices);
    }

    @Override
    public R equipList() {
        List<EquipEntity> equips = equipService.get(new QueryWrapper<EquipEntity>()
                .eq("flag", FlagEnum.OK.getCode())
                .eq("status", EquipStatusEnum.PENDING_RELEASE.getCode())
                .orderByDesc("create_time")
        );

        List<DeviceEntity> devices = deviceService.list(new QueryWrapper<DeviceEntity>()
                .eq("flag", FlagEnum.OK.getCode())
                .gt("amount", Constants.ZERO)
                .orderByDesc("create_time")
        );
        return R.ok().put("equip", equips).put("device", devices);
    }

    @Override
    public PageUtils deviceList(Map<String, Object> params) {
        IPage<OrderEntity> page = new Query<OrderEntity>().getPage(params);
        List<DeviceEntity> devices = this.baseMapper.deviceList(page, params);
        return new PageUtils(page, devices);
    }

    @Override
    @Transactional
    public void delivery(OrderVo vo) {
        if (StringUtils.isBlank(vo.getId()) || StringUtils.isBlank(vo.getDelivery())
                || StringUtils.isBlank(vo.getDeliveryNo())) {
            throw new RRException(ExceptionEnum.PARAM_ERROR);
        }
        OrderEntity order = this.getById(vo.getId());
        order.setDelivery(vo.getDelivery());
        order.setDeliveryNo(vo.getDeliveryNo());
        order.setStatus(OrderStatusEnum.DELIVERY.getCode());
        order.setDeliveryBy(ShiroUtils.getLoginUser().getUsername());
        this.updateById(order);
    }

    @Override
    @Transactional
    public void pay(OrderVo vo) {
        if (StringUtils.isBlank(vo.getId()) || StringUtils.isBlank(vo.getPayOrderNo())) {
            throw new RRException(ExceptionEnum.PARAM_ERROR);
        }
        this.update(
                new UpdateWrapper<OrderEntity>()
                        .set("status", OrderStatusEnum.PAID.getCode())
                        .set("pay_order_no", vo.getPayOrderNo())
                        .eq("id", vo.getId()));
    }

}