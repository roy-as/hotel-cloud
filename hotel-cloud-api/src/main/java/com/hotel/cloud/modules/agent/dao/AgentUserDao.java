package com.hotel.cloud.modules.agent.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hotel.cloud.modules.agent.entity.AgentUserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AgentUserDao extends BaseMapper<AgentUserEntity> {

    List<AgentUserEntity> getAgentUserList(IPage<AgentUserEntity> page, Map<String, Object> params);

    AgentUserEntity getAgentUser(Long userId);
	
}
