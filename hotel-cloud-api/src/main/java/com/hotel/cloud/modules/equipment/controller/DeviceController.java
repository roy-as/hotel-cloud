package com.hotel.cloud.modules.equipment.controller;

import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.R;
import com.hotel.cloud.common.vo.equip.DeviceVo;
import com.hotel.cloud.modules.equipment.entity.DeviceEntity;
import com.hotel.cloud.modules.equipment.service.DeviceService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;



/**
 * 智能设备
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-21 09:38:59
 */
@RestController
@RequestMapping("equipment/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("equipment:device:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = deviceService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("equipment:device:info")
    public R info(@PathVariable("id") Long id){
		DeviceEntity device = deviceService.getById(id);

        return R.ok().put("device", device);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("equipment:device:save")
    public R save(DeviceVo vo) throws IOException {
        deviceService.save(vo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("equipment:device:update")
    public R update(DeviceVo vo) throws IOException {
		deviceService.update(vo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("equipment:device:delete")
    public R delete(@RequestBody Long[] ids){
		deviceService.delete(Arrays.asList(ids));

        return R.ok();
    }

}
