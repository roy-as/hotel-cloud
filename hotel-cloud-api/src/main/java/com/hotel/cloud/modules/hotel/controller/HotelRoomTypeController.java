package com.hotel.cloud.modules.hotel.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.hotel.cloud.common.enums.HotelPictureTypeEnum;
import com.hotel.cloud.common.vo.hotel.RoomTypeVo;
import com.hotel.cloud.modules.hotel.entity.HotelOssMappingEntity;
import com.hotel.cloud.modules.org.service.HotelInfoService;
import com.hotel.cloud.modules.oss.entity.SysOssEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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

    @Autowired
    private HotelInfoService hotelInfoService;

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
    public R save(@RequestBody @Validated RoomTypeVo vo){
		hotelRoomTypeService.saveRoomType(vo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("hotel:hotelRoomType:update")
    public R update(RoomTypeVo vo){
		hotelRoomTypeService.update(vo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("hotel:hotelRoomType:delete")
    public R delete(@RequestBody Long[] ids){
		hotelRoomTypeService.delete(Arrays.asList(ids));

        return R.ok();
    }

    @GetMapping("/select")
    @RequiresPermissions("hotel:hotelRoomType:select")
    public R select(){
        return R.ok().put("data", hotelRoomTypeService.select());
    }

    @GetMapping("/getPicture")
    @RequiresPermissions("hotel:hotelInfo:info")
    public R getPicture(Long id) {
        HotelOssMappingEntity entity = new HotelOssMappingEntity();
        entity.setRoomTypeId(id);
        entity.setPictureType(HotelPictureTypeEnum.ROOM_PICTURE.getType());
        List<SysOssEntity> oss = hotelInfoService.getPicture(entity);
        return R.ok().put("data", oss);
    }

    @PostMapping("/deletePicture")
    @RequiresPermissions("hotel:hotelRoomType:update")
    public R deletePicture(@RequestBody RoomTypeVo vo){
        hotelRoomTypeService.deletePicture(vo);
        return R.ok();
    }

}
