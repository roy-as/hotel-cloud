package com.hotel.cloud.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.vo.CommandStatus;
import com.hotel.cloud.common.vo.CommandVo;
import com.hotel.cloud.modules.sys.entity.CommandEntity;

import java.util.Map;

/**
 * 
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-11-09 17:38:13
 */
public interface CommandService extends IService<CommandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Map<String, CommandStatus> release(CommandVo vo) throws Exception;
}

