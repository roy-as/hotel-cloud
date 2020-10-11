package com.hotel.cloud.modules.agent.controller;

import java.util.Map;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.R;
import com.hotel.cloud.common.vo.agent.AgentUserVo;
import com.hotel.cloud.common.vo.DisableVo;
import com.hotel.cloud.modules.agent.entity.AgentUserEntity;
import com.hotel.cloud.modules.agent.service.AgentUserService;
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
    private AgentUserService agentUserService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("agentUser:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = agentUserService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{userId}")
    @RequiresPermissions("agentUser:info")
    public R info(@PathVariable("userId") Long userId){
		AgentUserEntity agentUser = agentUserService.getAgentUser(userId);

        return R.ok().put("agentUser", agentUser);
    }

    /**
     * 下拉
     */
    @RequestMapping("/select")
    @RequiresPermissions("agentUser:select")
    public R select(){
        return R.ok().put("data", agentUserService.selectParentAgent());
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("agentUser:save")
    public R save(@RequestBody @Validated AgentUserVo vo){

        agentUserService.saveVo(vo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("agentUser:update")
    public R update(@RequestBody AgentUserVo vo){
        agentUserService.updateAgentUser(vo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("agentUser:delete")
    public R delete(@RequestBody Long[] userIds){

        agentUserService.delete(userIds);

        return R.ok();
    }

    /**
     * 启用禁用
     */
    @RequestMapping("/disable")
    @RequiresPermissions("agentUser:update")
    public R disable(@RequestBody DisableVo disableVo){

        agentUserService.disable(disableVo);

        return R.ok();
    }
}
