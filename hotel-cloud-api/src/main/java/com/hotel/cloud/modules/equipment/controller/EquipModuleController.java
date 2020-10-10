package com.hotel.cloud.modules.equipment.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotel.cloud.common.utils.ShiroUtils;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.hotel.cloud.modules.equipment.entity.EquipModuleEntity;
import com.hotel.cloud.modules.equipment.service.EquipModuleService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.R;


/**
 * 设备模块表
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-10 11:22:30
 */
@RestController
@RequestMapping("equipment/equipModule")
public class EquipModuleController {
    @Autowired
    private EquipModuleService equipModuleService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("equipment:equipModule:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = equipModuleService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("equipment:equipModule:info")
    public R info(@PathVariable("id") Long id){
		EquipModuleEntity equipModule = equipModuleService.getById(id);

        return R.ok().put("equipModule", equipModule);
    }

    /**
     * 信息
     */
    @RequestMapping("/select")
    @RequiresPermissions("equipment:equipModule:select")
    public R select(){
        List<EquipModuleEntity> list = this.equipModuleService.list(
                new QueryWrapper<EquipModuleEntity>().orderByDesc("create_time")
        );
        return R.ok().put("data", list);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("equipment:equipModule:save")
    public R save(@RequestBody @Validated EquipModuleEntity equipModule){
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        equipModule.setCreateTime(new Date());
        equipModule.setCreateBy(loginUser.getUsername());
        equipModule.setUpdateBy(loginUser.getUsername());
        equipModuleService.save(equipModule);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("equipment:equipModule:update")
    public R update(@RequestBody EquipModuleEntity equipModule){
		equipModuleService.updateById(equipModule);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("equipment:equipModule:delete")
    public R delete(@RequestBody Long[] ids){
		equipModuleService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }
}
