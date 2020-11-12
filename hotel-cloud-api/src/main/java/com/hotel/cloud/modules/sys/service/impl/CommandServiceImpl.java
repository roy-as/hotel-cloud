package com.hotel.cloud.modules.sys.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.CommonUtils;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import com.hotel.cloud.common.vo.CommandVo;
import com.hotel.cloud.modules.sys.dao.CommandDao;
import com.hotel.cloud.modules.sys.entity.CommandEntity;
import com.hotel.cloud.modules.sys.service.CommandService;
import com.hotel.cloud.modules.sys.service.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Objects;


@Service("commandService")
public class CommandServiceImpl extends ServiceImpl<CommandDao, CommandEntity> implements CommandService {

    @Autowired
    private MqttService mqttService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CommandEntity> page = this.page(
                new Query<CommandEntity>().getPage(params),
                new QueryWrapper<CommandEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void release(CommandVo vo) {
        CommandEntity commandEntity = this.getById(vo.getCommandId());
        if(null == commandEntity) {
            throw new RRException(ExceptionEnum.COMMAND_NOT_EXIST);
        }
        String command = commandEntity.getCommand();
        JSONArray array = JSON.parseArray(command);
        byte []  commands = new byte[array.size()];
        for(int i = 0; i < array.size(); i++) {
            commands[i] = Objects.requireNonNull(CommonUtils.hexStr2bytes(array.getString(i)))[0];
        }
        String[] macs = vo.getMacs();
        for(String mac : macs) {
            this.mqttService.publish(mac, commands, vo.getDatas());
        }
    }

}