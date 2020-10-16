package com.hotel.cloud.modules.equipment.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.R;
import com.hotel.cloud.common.utils.ShiroUtils;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.hotel.cloud.modules.equipment.entity.EquipIoEntity;
import com.hotel.cloud.modules.equipment.service.EquipIoService;



/**
 * 
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-12 09:08:04
 */
@RestController
@RequestMapping("equipment/equipIo")
public class EquipIoController {
    @Autowired
    private EquipIoService equipIoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("equipment:equipIo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = equipIoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("equipment:equipIo:info")
    public R info(@PathVariable("id") Long id){
		EquipIoEntity equipIo = equipIoService.getById(id);

        return R.ok().put("equipIo", equipIo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("equipment:equipIo:save")
    public R save(@RequestBody EquipIoEntity equipIo){
        equipIo.setCreateTime(new Date());
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        equipIo.setCreateBy(loginUser.getUsername());
        equipIo.setUpdateBy(loginUser.getUsername());
        equipIoService.save(equipIo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("equipment:equipIo:update")
    public R update(@RequestBody EquipIoEntity equipIo){
        equipIo.setUpdateBy(ShiroUtils.getLoginUser().getUsername());
		equipIoService.updateById(equipIo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("equipment:equipIo:delete")
    public R delete(@RequestBody Long[] ids){
		equipIoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
