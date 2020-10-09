package com.hotel.cloud.modules.hotel.controller;

import java.util.Arrays;
import java.util.Map;

import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.R;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.hotel.cloud.modules.hotel.entity.HotelBuildingEntity;
import com.hotel.cloud.modules.hotel.service.HotelBuildingService;




/**
 * 酒店楼栋表
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-09 17:24:56
 */
@RestController
@RequestMapping("hotel/hotelBuilding")
public class HotelBuildingController {
    @Autowired
    private HotelBuildingService hotelBuildingService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("hotel:hotelBuilding:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = hotelBuildingService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("hotel:hotelBuilding:info")
    public R info(@PathVariable("id") Long id){
		HotelBuildingEntity hotelBuilding = hotelBuildingService.getById(id);

        return R.ok().put("hotelBuilding", hotelBuilding);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("hotel:hotelBuilding:save")
    public R save(@RequestBody HotelBuildingEntity hotelBuilding){
		hotelBuildingService.save(hotelBuilding);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("hotel:hotelBuilding:update")
    public R update(@RequestBody HotelBuildingEntity hotelBuilding){
		hotelBuildingService.updateById(hotelBuilding);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("hotel:hotelBuilding:delete")
    public R delete(@RequestBody Long[] ids){
		hotelBuildingService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
