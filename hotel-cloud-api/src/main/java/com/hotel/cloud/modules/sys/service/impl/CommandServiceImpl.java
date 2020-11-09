package com.hotel.cloud.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import com.hotel.cloud.modules.sys.dao.CommandDao;
import com.hotel.cloud.modules.sys.entity.CommandEntity;
import com.hotel.cloud.modules.sys.service.CommandService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("commandService")
public class CommandServiceImpl extends ServiceImpl<CommandDao, CommandEntity> implements CommandService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CommandEntity> page = this.page(
                new Query<CommandEntity>().getPage(params),
                new QueryWrapper<CommandEntity>()
        );

        return new PageUtils(page);
    }

}