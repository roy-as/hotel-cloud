package com.hotel.cloud.modules.org.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.vo.DisableVo;
import com.hotel.cloud.modules.org.entity.AgentEntity;
import com.hotel.cloud.common.vo.agent.AgentUserVo;
import java.util.List;
import java.util.Map;


public interface AgentService extends IService<AgentEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveVo(AgentUserVo vo);

    void updateAgentUser(AgentUserVo vo);

    void delete(Long[] userIds);

    void disable(DisableVo vo);

    List<AgentEntity> selectParentAgent();

    List<AgentEntity> select(boolean isAgent);
}

