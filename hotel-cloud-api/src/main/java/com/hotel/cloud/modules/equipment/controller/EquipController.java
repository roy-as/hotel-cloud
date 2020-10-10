package com.hotel.cloud.modules.equipment.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.R;
import com.hotel.cloud.common.utils.ShiroUtils;
import com.hotel.cloud.common.vo.QrcodeVo;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.hotel.cloud.modules.equipment.entity.EquipEntity;
import com.hotel.cloud.modules.equipment.service.EquipService;



/**
 * 设备表
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-10 11:22:30
 */
@RestController
@RequestMapping("equipment/equip")
public class EquipController {
    @Autowired
    private EquipService equipService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("equipment:equip:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = equipService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("equipment:equip:info")
    public R info(@PathVariable("id") Long id){
		EquipEntity equip = equipService.getById(id);

        return R.ok().put("equip", equip);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("equipment:equip:save")
    public R save(@RequestBody EquipEntity equip){
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        equip.setCreateTime(new Date());
        equip.setCreateBy(loginUser.getUsername());
        equip.setUpdateBy(loginUser.getUsername());
        equipService.save(equip);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("equipment:equip:update")
    public R update(@RequestBody EquipEntity equip){
		equipService.updateById(equip);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("equipment:equip:delete")
    public R delete(@RequestBody Long[] ids){
		equipService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/generateQrcode")
    @RequiresPermissions("equipment:equip:generateQrcode")
    public R generateQrcode(@RequestBody @Validated QrcodeVo vo) throws IOException {
        this.equipService.generateQrcode(vo);
        return R.ok();
    }


}
