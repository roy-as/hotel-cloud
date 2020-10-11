package com.hotel.cloud.modules.agent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.vo.DisableVo;
import com.hotel.cloud.modules.agent.entity.AgentUserEntity;
import com.hotel.cloud.common.vo.agent.AgentUserVo;
import java.util.List;
import java.util.Map;


public interface AgentUserService extends IService<AgentUserEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveVo(AgentUserVo vo);

    AgentUserEntity getAgentUser(Long userId);

    void updateAgentUser(AgentUserVo vo);

    void delete(Long[] userIds);

    void disable(DisableVo vo);

    List<AgentUserEntity> selectParentAgent();

    List<AgentUserEntity> select(boolean isAgent);
}

