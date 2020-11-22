package com.hotel.cloud.modules.sys.controller;

import com.hotel.cloud.common.utils.*;
import com.hotel.cloud.common.vo.CommandVo;
import com.hotel.cloud.modules.sys.entity.CommandEntity;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import com.hotel.cloud.modules.sys.service.CommandService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * @author ${author}
 * @email ${email}
 * @date 2020-11-09 17:38:13
 */
@RestController
@RequestMapping("sys/command")
public class CommandController {
    @Autowired
    private CommandService commandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:command:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = commandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:command:info")
    public R info(@PathVariable("id") Long id) {
        CommandEntity command = commandService.getById(id);

        return R.ok().put("command", command);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:command:save")
    public R save(@RequestBody CommandEntity command) {
        String[] commandStr = command.getCommand().split(",");
        byte[] commands = new byte[commandStr.length];
        for (int i = 0; i < commandStr.length; i++) {
            commands[i] = Objects.requireNonNull(CommonUtils.hexStr2bytes(commandStr[i]))[0];
        }
        command.setCommand(Arrays.toString(commands));
        if(StringUtils.isNotBlank(command.getData()) && !command.getData().contains("[") && !command.getData().contains("]")) {
            String[] datas = command.getData().split(",");
            command.setData(Arrays.toString(datas));
        }
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        command.setCreateBy(loginUser.getUsername());
        command.setUpdateBy(loginUser.getUsername());
        command.setCreateTime(new Date());
        commandService.save(command);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:command:update")
    public R update(@RequestBody CommandEntity command) {
        if(!command.getData().contains("[") && !command.getData().contains("]")) {
            String[] datas = command.getData().split(",");
            command.setData(Arrays.toString(datas));
        }
        commandService.updateById(command);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:command:delete")
    public R delete(@RequestBody Long[] ids) {
        commandService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @GetMapping("/data")
    @RequiresPermissions("sys:command:list")
    public R list() {
        List<CommandEntity> list = this.commandService.list();
        return R.ok().put("data", list);
    }

    @PostMapping("/release")
    public R release(@Validated @RequestBody CommandVo vo) throws Exception {
        return R.ok().put("data", this.commandService.release(vo));
    }
}
