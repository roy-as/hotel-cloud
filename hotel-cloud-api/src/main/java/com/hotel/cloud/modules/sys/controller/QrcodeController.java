package com.hotel.cloud.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.R;
import com.hotel.cloud.modules.equipment.entity.EquipEntity;
import com.hotel.cloud.modules.sys.entity.QrcodeEntity;
import com.hotel.cloud.modules.sys.service.QrcodeService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-11-18 19:55:16
 */
@RestController
@RequestMapping("sys/qrcode")
public class QrcodeController {
    @Autowired
    private QrcodeService qrcodeService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:qrcode:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = qrcodeService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:qrcode:info")
    public R info(@PathVariable("id") Long id){
		QrcodeEntity qrcode = qrcodeService.getById(id);

        return R.ok().put("qrcode", qrcode);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:qrcode:save")
    public R save(@RequestBody QrcodeEntity qrcode){
		qrcodeService.save(qrcode);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:qrcode:update")
    public R update(@RequestBody QrcodeEntity qrcode){
		qrcodeService.updateById(qrcode);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:qrcode:delete")
    public R delete(@RequestBody Long[] ids){
		qrcodeService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @GetMapping("/downloadTemplate")
    @RequiresPermissions("sys:qrcode:list")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        qrcodeService.downloadTemplate(response);
    }

    @PostMapping("/import")
    @RequiresPermissions("sys:qrcode:save")
    public R importExcel(MultipartFile file) throws Exception {
        qrcodeService.importExcel(file);
        return R.ok();
    }

    @GetMapping("/download")
    @RequiresPermissions("sys:qrcode:list")
    public void download(Long[] ids, HttpServletResponse response) throws IOException {
        qrcodeService.download(ids, response);
    }

    @GetMapping("select")
    @RequiresPermissions("sys:qrcode:list")
    public R select() {
        List<QrcodeEntity> qrcodes = qrcodeService.list(new QueryWrapper<QrcodeEntity>().isNull("equip_id"));
        return R.ok().put("data", qrcodes);
    }

}
