package com.hotel.cloud.modules.hotel.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hotel.cloud.modules.hotel.entity.HotelRoomTypeEntity;
import com.hotel.cloud.modules.hotel.service.HotelRoomTypeService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.R;


/**
 * 酒店房型表
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-09 17:24:56
 */
@RestController
@RequestMapping("hotel/hotelRoomType")
public class HotelRoomTypeController {
    @Autowired
    private HotelRoomTypeService hotelRoomTypeService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("hotel:hotelRoomType:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = hotelRoomTypeService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("hotel:hotelRoomType:info")
    public R info(@PathVariable("id") Long id){
		HotelRoomTypeEntity hotelRoomType = hotelRoomTypeService.getById(id);

        return R.ok().put("hotelRoomType", hotelRoomType);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("hotel:hotelRoomType:save")
    public R save(@RequestBody HotelRoomTypeEntity hotelRoomType){
		hotelRoomTypeService.saveRoomType(hotelRoomType);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("hotel:hotelRoomType:update")
    public R update(@RequestBody HotelRoomTypeEntity hotelRoomType){
		hotelRoomTypeService.updateById(hotelRoomType);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("hotel:hotelRoomType:delete")
    public R delete(@RequestBody Long[] ids){
		hotelRoomTypeService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @GetMapping("/select")
    @RequiresPermissions("hotel:hotelRoomType:select")
    public R select(){
        return R.ok().put("data", hotelRoomTypeService.select());
    }

}
