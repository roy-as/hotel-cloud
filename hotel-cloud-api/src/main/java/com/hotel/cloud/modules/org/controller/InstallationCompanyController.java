package com.hotel.cloud.modules.org.controller;

import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.R;
import com.hotel.cloud.common.utils.ShiroUtils;
import com.hotel.cloud.modules.org.entity.InstallationCompanyEntity;
import com.hotel.cloud.modules.org.service.InstallationCompanyService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;



/**
 * 安装公司表
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-24 09:07:56
 */
@RestController
@RequestMapping("org/installation")
public class InstallationCompanyController {
    @Autowired
    private InstallationCompanyService installationCompanyService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("org:installation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = installationCompanyService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("org:installation:info")
    public R info(@PathVariable("id") Long id){
		InstallationCompanyEntity installationCompany = installationCompanyService.getById(id);

        return R.ok().put("installationCompany", installationCompany);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("org:installation:save")
    public R save(@RequestBody InstallationCompanyEntity installationCompany){
        installationCompany.setCreateTime(new Date());
        installationCompany.setCreateBy(ShiroUtils.getLoginUser().getUsername());
        installationCompany.setUpdateBy(ShiroUtils.getLoginUser().getUsername());
		installationCompanyService.save(installationCompany);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("org:installation:update")
    public R update(@RequestBody InstallationCompanyEntity installationCompany){
		installationCompanyService.updateById(installationCompany);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("org:installation:delete")
    public R delete(@RequestBody Long[] ids){
		installationCompanyService.deleteBatch(Arrays.asList(ids));

        return R.ok();
    }

}
