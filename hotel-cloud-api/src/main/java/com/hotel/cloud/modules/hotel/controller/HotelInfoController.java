package com.hotel.cloud.modules.hotel.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.R;
import com.hotel.cloud.common.vo.DisableVo;
import com.hotel.cloud.common.vo.HotelInfoVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.hotel.cloud.modules.hotel.entity.HotelInfoEntity;
import com.hotel.cloud.modules.hotel.service.HotelInfoService;



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
		hotelInfoService.deleteBatch(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 启用禁用
     */
    @RequestMapping("/disable")
    @RequiresPermissions("agentUser:update")
    public R disable(@RequestBody DisableVo disableVo){

        hotelInfoService.disable(disableVo);

        return R.ok();
    }
}
