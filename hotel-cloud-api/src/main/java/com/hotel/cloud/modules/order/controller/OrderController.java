package com.hotel.cloud.modules.order.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hotel.cloud.common.enums.OrderStatusEnum;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.R;
import com.hotel.cloud.common.vo.order.OrderVo;
import com.hotel.cloud.modules.order.entity.OrderEntity;
import com.hotel.cloud.modules.order.service.OrderService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.Map;



/**
 * 
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-27 09:22:31
 */
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("order:order:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("order:order:info")
    public R info(@PathVariable("id") String id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("order:order:save")
    public R save(@RequestBody OrderVo vo){
        orderService.saveOrder(vo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("order:order:update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("order:order:delete")
    public R delete(@RequestBody String[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @GetMapping("/equipList")
    public R equipList() {
        return orderService.equipList();
    }

    @PostMapping("/audit")
    @RequiresPermissions("order:order:audit")
    public R audit(@RequestBody OrderEntity entity) {
        this.orderService.update(
                new UpdateWrapper<OrderEntity>().set("status", entity.getStatus()).eq("id", entity.getId()));
        return R.ok();
    }

    @GetMapping("/deviceList")
    @RequiresPermissions("order:order:info")
    public R deviceList(@RequestParam Map<String, Object> params) {
        PageUtils page = orderService.deviceList(params);
        return R.ok().put("page", page);
    }

    @PostMapping("/delivery")
    @RequiresPermissions("order:order:delivery")
    public R delivery(@RequestBody OrderVo vo) {
        orderService.delivery(vo);
        return R.ok();
    }

    @PostMapping("/installed")
    @RequiresPermissions("order:order:installed")
    public R installed (@RequestBody OrderEntity entity) {
        this.orderService.update(
                new UpdateWrapper<OrderEntity>().set("status", OrderStatusEnum.INSTALLED.getCode()).eq("id", entity.getId()));
        return R.ok();
    }

    @PostMapping("/confirm")
    @RequiresPermissions("order:order:confirmInstall")
    public R confirmInstall (@RequestBody OrderEntity entity) {
        this.orderService.update(
                new UpdateWrapper<OrderEntity>().set("status", OrderStatusEnum.CONFIRM_INSTALL.getCode()).eq("id", entity.getId()));
        return R.ok();
    }

    @PostMapping("/pay")
    @RequiresPermissions("order:order:pay")
    public R pay (@RequestBody OrderVo vo) {
        this.orderService.pay(vo);
        return R.ok();
    }

}
