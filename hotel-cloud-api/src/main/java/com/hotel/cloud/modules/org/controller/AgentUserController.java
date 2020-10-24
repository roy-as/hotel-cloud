package com.hotel.cloud.modules.org.controller;

import java.util.Map;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.R;
import com.hotel.cloud.common.vo.agent.AgentUserVo;
import com.hotel.cloud.common.vo.DisableVo;
import com.hotel.cloud.modules.org.entity.AgentEntity;
import com.hotel.cloud.modules.org.service.AgentService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;




/**
 * 
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-10-05 10:01:42
 */
@RestController
@RequestMapping("/agentUser")
public class AgentUserController {
    @Autowired
    private AgentService agentService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("agentUser:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = agentService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("agentUser:info")
    public R info(@PathVariable("id") Long id){
        AgentEntity agent = agentService.getById(id);
        return R.ok().put("agentUser", agent);
    }

    /**
     * 下拉
     */
    @RequestMapping("/select")
    @RequiresPermissions("agentUser:select")
    public R select(){
        return R.ok().put("data", agentService.selectParentAgent());
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("agentUser:save")
    public R save(@RequestBody @Validated AgentUserVo vo){

        agentService.saveVo(vo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("agentUser:update")
    public R update(@RequestBody AgentUserVo vo){
        agentService.updateAgentUser(vo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("agentUser:delete")
    public R delete(@RequestBody Long[] userIds){

        agentService.delete(userIds);

        return R.ok();
    }

    /**
     * 启用禁用
     */
    @RequestMapping("/disable")
    @RequiresPermissions("agentUser:update")
    public R disable(@RequestBody DisableVo disableVo){

        agentService.disable(disableVo);

        return R.ok();
    }
}
