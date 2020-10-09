package com.hotel.cloud.modules.hotel.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.cloud.modules.hotel.entity.HotelRoomEntity;
import com.hotel.cloud.modules.hotel.service.HotelRoomService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.R;



/**
 * 酒店房间表
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-09 17:24:56
 */
@RestController
@RequestMapping("hotel/hotelRoom")
public class HotelRoomController {
    @Autowired
    private HotelRoomService hotelRoomService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("hotel:hotelRoom:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = hotelRoomService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("hotel:hotelRoom:info")
    public R info(@PathVariable("id") Long id){
		HotelRoomEntity hotelRoom = hotelRoomService.getById(id);

        return R.ok().put("hotelRoom", hotelRoom);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("hotel:hotelRoom:save")
    public R save(@RequestBody HotelRoomEntity hotelRoom){
		hotelRoomService.save(hotelRoom);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("hotel:hotelRoom:update")
    public R update(@RequestBody HotelRoomEntity hotelRoom){
		hotelRoomService.updateById(hotelRoom);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("hotel:hotelRoom:delete")
    public R delete(@RequestBody Long[] ids){
		hotelRoomService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
