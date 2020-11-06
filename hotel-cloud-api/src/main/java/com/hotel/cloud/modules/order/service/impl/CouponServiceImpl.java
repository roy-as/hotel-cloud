package com.hotel.cloud.modules.order.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.cloud.common.enums.CouponStatusEnum;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.CommonUtils;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import com.hotel.cloud.common.utils.ShiroUtils;
import com.hotel.cloud.common.vo.order.CouponValidate;
import com.hotel.cloud.common.vo.order.CouponValidateVo;
import com.hotel.cloud.common.vo.order.CouponVo;
import com.hotel.cloud.common.vo.order.OrderDeviceVo;
import com.hotel.cloud.modules.equipment.entity.DeviceEntity;
import com.hotel.cloud.modules.equipment.entity.EquipEntity;
import com.hotel.cloud.modules.equipment.service.DeviceService;
import com.hotel.cloud.modules.equipment.service.EquipService;
import com.hotel.cloud.modules.order.dao.CouponDao;
import com.hotel.cloud.modules.order.entity.CouponEntity;
import com.hotel.cloud.modules.order.service.CouponService;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import com.sun.org.apache.bcel.internal.generic.BREAKPOINT;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service("couponService")
public class CouponServiceImpl extends ServiceImpl<CouponDao, CouponEntity> implements CouponService {

    @Resource
    private EquipService equipService;

    @Resource
    private DeviceService deviceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String createBy = (String) params.get("createBy");
        String status = (String) params.get("status");
        String sn = (String) params.get("sn");
        String name = (String) params.get("name");
        String type = (String) params.get("type");
        boolean statusCondition = StringUtils.isNotBlank(status);
        boolean expired = statusCondition && CouponStatusEnum.EXPIRED.getCode().equals(Integer.valueOf(status));
        boolean notExpired = statusCondition && !CouponStatusEnum.EXPIRED.getCode().equals(Integer.valueOf(status));
        boolean unused = statusCondition && CouponStatusEnum.NOT_USED.getCode().equals(Integer.valueOf(status));
        IPage<CouponEntity> page = this.page(
                new Query<CouponEntity>().getPage(params),
                new QueryWrapper<CouponEntity>()
                        .like(StringUtils.isNotBlank(name), "name", name)
                        .like(StringUtils.isNotBlank(sn), "sn", sn)
                        .like(StringUtils.isNotBlank(createBy), "create_by", createBy)
                        .and(notExpired, wrapper -> wrapper
                                .eq(notExpired, "status", status)
                                .ge(notExpired, "expired_time", new Date())
                                .or(unused)
                                .isNull(unused, "expired_time")

                        )
                        .eq(StringUtils.isNotBlank(type), "type", type)
                        .lt(expired, "expired_time", new Date())
                        .orderByAsc("status")
                        .orderByDesc("create_time")
        );
        List<CouponEntity> records = page.getRecords();
        for (CouponEntity record : records) {
            if (null != record.getExpiredTime()) {
                if (record.getStatus().equals(CouponStatusEnum.NOT_USED.getCode()) &&
                        record.getExpiredTime().getTime() < System.currentTimeMillis()) {
                    record.setStatus(2);
                }
            }
        }
        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void create(CouponVo vo) {
        if (null != vo.getExpiredTime() && vo.getExpiredTime().getTime() < System.currentTimeMillis()) {
            throw new RRException(ExceptionEnum.NOT_LESS_NOW);
        }
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        Integer count = vo.getCount();
        String[] sns = CommonUtils.subUUID(count);
        List<CouponEntity> entities = IntStream.range(0, count).mapToObj(index -> {
            CouponEntity entity = new CouponEntity();
            entity.setCreateTime(new Date());
            entity.setCreateBy(loginUser.getUsername());
            BeanUtils.copyProperties(vo, entity);
            entity.setSn(sns[index]);
            return entity;
        }).collect(Collectors.toList());
        this.saveBatch(entities);
    }

    private void checkIfSatisfyCoupon(BigDecimal amount, BigDecimal couponAmount) {
        if (amount.compareTo(couponAmount) < 0) {
            throw new RRException(ExceptionEnum.NOT_SATISFY_COUPON);
        }
    }

    @Override
    public OrderDeviceVo check(CouponValidate validate, boolean hasSn) {
        CouponEntity coupon = this.getBaseMapper().selectOne(
                new QueryWrapper<CouponEntity>().eq("sn", validate.getSn())
        );
        if (hasSn) {
            Optional.ofNullable(coupon).orElseThrow(new RRException(ExceptionEnum.COUPON_NOT_EXIST));
            if (null != coupon.getExpiredTime() && System.currentTimeMillis() > coupon.getExpiredTime().getTime()) {
                throw new RRException(ExceptionEnum.COUPON_EXPIRED);
            }else if(null != coupon.getOrderId()) {
                throw new RRException(ExceptionEnum.COUPON_USED);
            }
        } else {
            coupon = new CouponEntity();
        }
        OrderDeviceVo orderDeviceVo = new OrderDeviceVo();
        List<CouponValidateVo> infos = validate.getValidationInfo();
        Map<Integer, List<CouponValidateVo>> validateMap = infos.stream().collect(Collectors.groupingBy(CouponValidateVo::getDeviceType));
        Integer scope = coupon.getScope();
        // 智能主机金额
        BigDecimal equipAmount = new BigDecimal(0);
        // 智能设备优惠
        BigDecimal deviceAmount = new BigDecimal(0);

        List<CouponValidateVo> equipList = validateMap.get(1);
        List<CouponValidateVo> deviceList = validateMap.get(2);
        Map<Long, Integer> shopNumberMap = infos.stream().collect(
                Collectors.toMap(CouponValidateVo::getId, CouponValidateVo::getShopNumber)
        );
        // 智能主机金额
        boolean equipNotEmpty = validateMap.containsKey(1) && CollectionUtil.isNotEmpty(equipList);
        if (equipNotEmpty) {
            List<Long> equipIds = equipList.stream().map(CouponValidateVo::getId).collect(Collectors.toList());
            List<EquipEntity> equips = equipService.get(new QueryWrapper<EquipEntity>().in("id", equipIds));
            for (EquipEntity equip : equips) {
                equipAmount = equipAmount.add(equip.getPrice().multiply(new BigDecimal(shopNumberMap.get(equip.getId()))));
            }
        }
        // 智能设备金额
        boolean deviceNotEmpty = validateMap.containsKey(2) && CollectionUtil.isNotEmpty(deviceList);
        if (deviceNotEmpty) {
            List<Long> deviceIds = deviceList.stream().map(CouponValidateVo::getId).collect(Collectors.toList());
            List<DeviceEntity> devices = deviceService.getBaseMapper().selectBatchIds(deviceIds);
            orderDeviceVo.setDevices(devices);
            for (DeviceEntity device : devices) {
                deviceAmount = deviceAmount.add(device.getPrice().multiply(new BigDecimal(shopNumberMap.get(device.getId()))));
            }
        }
        // 总支付金额
        BigDecimal amount = equipAmount.add(deviceAmount);
        coupon.setTotalAmount(amount);
        if (hasSn) {
            switch (scope) {
                case 0:// 所有商品优惠
                    this.checkIfSatisfyCoupon(amount, coupon.getAmount());
                    this.checkPrice(coupon, amount, null);
                    break;
                case 1:// 智能主机优惠
                    if (!equipNotEmpty) {
                        throw new RRException(ExceptionEnum.NOT_SATISFY_COUPON);
                    }
                    this.checkIfSatisfyCoupon(equipAmount, coupon.getAmount());
                    this.checkPrice(coupon, amount, equipAmount);
                    break;

                case 2:// 智能设备优惠
                    if (!deviceNotEmpty) {
                        throw new RRException(ExceptionEnum.NOT_SATISFY_COUPON);
                    }
                    this.checkIfSatisfyCoupon(deviceAmount, coupon.getAmount());
                    this.checkPrice(coupon, amount, deviceAmount);
                    break;
                default:
                    throw new RRException(ExceptionEnum.ENUM_NOT_EXIST);
            }
        }
        orderDeviceVo.setCoupon(coupon);
        return orderDeviceVo;
    }

    /**
     * 设置优惠后的金额
     *
     * @param coupon
     * @param amount
     */
    private void checkPrice(CouponEntity coupon, BigDecimal amount, BigDecimal couponAmount) {
        BigDecimal realAmount;
        switch (coupon.getType()) {
            case 1:
                realAmount = amount.subtract(coupon.getCouponAmount());
                break;
            case 2:
                int times;
                if (null != couponAmount) {
                    times = couponAmount.divide(coupon.getAmount(), 0, BigDecimal.ROUND_FLOOR).intValue();
                } else {
                    times = amount.divide(coupon.getAmount(), 0, BigDecimal.ROUND_CEILING).intValue();
                }
                realAmount = amount.subtract(coupon.getCouponAmount().multiply(new BigDecimal(times)));
                break;
            case 3:
                realAmount = amount.multiply(coupon.getDiscount()).setScale(2, BigDecimal.ROUND_FLOOR);
                coupon.setCouponAmount(amount.subtract(realAmount));
                break;
            case 4:
                if (null != couponAmount) {
                    realAmount = amount.subtract(couponAmount.multiply(new BigDecimal(1).subtract(coupon.getDiscount()))).setScale(2, BigDecimal.ROUND_FLOOR);
                } else {
                    realAmount = amount.multiply(coupon.getDiscount()).setScale(2, BigDecimal.ROUND_FLOOR);
                }
                coupon.setCouponAmount(amount.subtract(realAmount));
                break;
            case 5:
                realAmount = amount.subtract(coupon.getCouponAmount());
                break;
            default:
                throw new RRException(ExceptionEnum.ENUM_NOT_EXIST);
        }
        coupon.setRealAmount(realAmount);
    }
}