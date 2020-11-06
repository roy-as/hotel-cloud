package com.hotel.cloud.modules.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotel.cloud.common.annotation.Delay;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.R;
import com.hotel.cloud.common.vo.order.CouponValidate;
import com.hotel.cloud.common.vo.order.CouponVo;
import com.hotel.cloud.common.vo.order.CouponValidateVo;
import com.hotel.cloud.modules.order.entity.CouponEntity;
import com.hotel.cloud.modules.order.service.CouponService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * 
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-25 11:06:20
 */
@RestController
@RequestMapping("order/coupon")
public class CouponController {
    @Autowired
    private CouponService couponService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("order:coupon:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = couponService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("order:coupon:info")
    public R info(@PathVariable("id") Long id){
		CouponEntity coupon = couponService.getById(id);

        return R.ok().put("coupon", coupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("order:coupon:save")
    public R save(@RequestBody @Validated CouponVo vo){
        couponService.create(vo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("order:coupon:delete")
    public R delete(@RequestBody Long[] ids){
        List<Long> idList = Arrays.asList(ids);
        List<CouponEntity> coupons = couponService.getBaseMapper().selectBatchIds(idList);
        for (CouponEntity coupon : coupons) {
            if(null != coupon.getOrderId()) {
                throw new RRException(ExceptionEnum.COUPON_USED);
            }
        }
        couponService.removeByIds(idList);

        return R.ok();
    }

    @PostMapping("check")
    @Delay(time = 1000 * 60)
    public R check(@RequestBody @Validated CouponValidate validate) {
        return R.ok().put("data", this.couponService.check(validate, true).getCoupon());
    }

}
