package com.hotel.cloud.modules.org.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.R;
import com.hotel.cloud.common.vo.DisableVo;
import com.hotel.cloud.common.vo.hotel.HotelInfoVo;
import com.hotel.cloud.modules.hotel.entity.HotelOssMappingEntity;
import com.hotel.cloud.modules.oss.entity.SysOssEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.hotel.cloud.modules.org.entity.HotelInfoEntity;
import com.hotel.cloud.modules.org.service.HotelInfoService;



/**
 * 酒店信息表
 * @date 2020-10-08 09:39:43
 */
@RestController
@RequestMapping("hotel/hotelInfo")
public class HotelInfoController {
    @Autowired
    private HotelInfoService hotelInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("hotel:hotelInfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = hotelInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("hotel:hotelInfo:info")
    public R info(@PathVariable("id") Long id){
		HotelInfoEntity hotelInfo = hotelInfoService.getInfo(id);

        return R.ok().put("hotelInfo", hotelInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("hotel:hotelInfo:save")
    public R save(@Validated HotelInfoVo vo) throws IOException {
		hotelInfoService.saveHotel(vo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("hotel:hotelInfo:update")
    public R update(HotelInfoVo vo) throws IOException {
		hotelInfoService.update(vo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("hotel:hotelInfo:delete")
    public R delete(@RequestBody Long[] ids){
		hotelInfoService.deleteBatch(ids);

        return R.ok();
    }

    /**
     * 启用禁用
     */
    @RequestMapping("/disable")
    @RequiresPermissions("hotel:hotelInfo:update")
    public R disable(@RequestBody DisableVo disableVo){

        hotelInfoService.disable(disableVo);

        return R.ok();
    }

    @GetMapping("/getPicture")
    @RequiresPermissions("hotel:hotelInfo:info")
    public R getPicture(HotelOssMappingEntity entity) {
        List<SysOssEntity> oss = hotelInfoService.getPicture(entity);
        return R.ok().put("data", oss);
    }

    /**
     * 下拉
     * @return
     */
    @GetMapping("select")
    @RequiresPermissions("hotel:hotelInfo:select")
    public R select() {
        List<HotelInfoEntity> hotels = hotelInfoService.select();
        return R.ok().put("data", hotels);
    }

    @PostMapping("/deletePicture")
    @RequiresPermissions("hotel:hotelInfo:update")
    public R deletePicture(@RequestBody HotelInfoVo vo){
        hotelInfoService.deletePicture(vo);
        return R.ok();
    }
}
