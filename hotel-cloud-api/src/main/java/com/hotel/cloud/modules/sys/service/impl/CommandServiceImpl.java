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
import com.hotel.cloud.common.utils.ThreadPoolUtils;
import com.hotel.cloud.common.vo.CommandStatus;
import com.hotel.cloud.common.vo.CommandVo;
import com.hotel.cloud.modules.sys.dao.CommandDao;
import com.hotel.cloud.modules.sys.entity.CommandEntity;
import com.hotel.cloud.modules.sys.service.CommandService;
import com.hotel.cloud.modules.sys.service.MqttService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@Service("commandService")
@Slf4j
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
    public Map<String, CommandStatus> release(CommandVo vo) throws Exception {
        CommandEntity commandEntity = this.getById(vo.getCommandId());
        if (null == commandEntity) {
            throw new RRException(ExceptionEnum.COMMAND_NOT_EXIST);
        }
        String command = commandEntity.getCommand();
        Integer commandType = commandEntity.getCommandType();
        JSONArray array = JSON.parseArray(command);
        byte[] commands = new byte[array.size()];
        for (int i = 0; i < array.size(); i++) {
            commands[i] = Objects.requireNonNull(CommonUtils.hexStr2bytes(array.getString(i)))[0];
        }
        String[] macs = vo.getMacs();
        Map<String, CommandStatus> resultMap = new HashMap<>(macs.length, 0.75f);
        CountDownLatch countDownLatch = new CountDownLatch(macs.length);
        for (String mac : macs) {
            ThreadPoolUtils.execute(() -> {
                try {
                    resultMap.put(mac, this.mqttService.publish(mac, commands, commandType, vo.getDatas()));
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("下发命令失败，mac:{},command:{},body:{},error", mac, commands, vo.getDatas(), e);
                    CommandStatus commandStatus = new CommandStatus();
                    commandStatus.setMac(mac);
                    resultMap.put(mac, commandStatus);
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await(10, TimeUnit.SECONDS);
        return resultMap;
    }
}