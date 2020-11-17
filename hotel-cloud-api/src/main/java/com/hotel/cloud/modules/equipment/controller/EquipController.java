package com.hotel.cloud.modules.equipment.controller;

import java.io.IOException;
import java.util.*;

import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.R;
import com.hotel.cloud.common.utils.ShiroUtils;
import com.hotel.cloud.common.vo.equip.QrcodeVo;
import com.hotel.cloud.common.vo.equip.EquipVo;
import com.hotel.cloud.modules.org.entity.AgentEntity;
import com.hotel.cloud.modules.org.service.AgentService;
import com.hotel.cloud.modules.org.entity.HotelInfoEntity;
import com.hotel.cloud.modules.org.service.HotelInfoService;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.hotel.cloud.modules.equipment.entity.EquipEntity;
import com.hotel.cloud.modules.equipment.service.EquipService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;


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

    @Autowired
    private HotelInfoService hotelInfoService;

    @Autowired
    private AgentService agentService;

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
    public R save(@RequestBody EquipEntity equip) throws MqttException {
        equipService.saveEquip(equip);
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
		equipService.batchDelete(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/generateQrcode")
    @RequiresPermissions("equipment:equip:generateQrcode")
    public R generateQrcode(@RequestBody @Validated QrcodeVo vo) throws IOException {
        this.equipService.generateQrcode(vo);
        return R.ok();
    }

    @GetMapping("selectAgentAndHotel")
    @RequiresPermissions({"agentUser:select", "hotel:hotelInfo:select"})
    public R selectAgentAndHotel() {
        boolean isAgent = ShiroUtils.isAgent();
        List<HotelInfoEntity> hotels;
        if(!isAgent) {
            hotels = new ArrayList<>();
        } else {
            hotels = hotelInfoService.select();
        }
        List<AgentEntity> agents = agentService.select(isAgent);
        return R.ok().put("hotels", hotels).put("agents", agents).put("agentLevel", 1);
    }

    @PostMapping("release")
    @RequiresPermissions("equipment:equip:release")
    public R release(@RequestBody @Validated EquipVo vo) {
        equipService.releaseVo(vo);
        return R.ok();
    }

    @PostMapping("/recycle")
    @RequiresPermissions("equipment:equip:recycle")
    public R recycle(@RequestBody Long[] ids) {
        equipService.recycle(ids);
        return R.ok();
    }

    @PostMapping("/old")
    @RequiresPermissions("equipment:equip:old")
    public R old(@RequestBody @Validated EquipVo vo) {
        equipService.old(vo.getIds(), vo.getCount());
        return R.ok();
    }

    @GetMapping("/download")
    public void download(Long[] ids, HttpServletResponse response) throws IOException {
        equipService.download(ids, response);
    }

    @PostMapping("/import")
    public R importExcel(MultipartFile file) throws IOException {
        equipService.importExcel(file);
        return R.ok();
    }



}
