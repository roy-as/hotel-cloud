package com.hotel.cloud.modules.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ImmutableList;
import com.hotel.cloud.common.enums.AgentTypeEnum;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.enums.FlagEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.Constants;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import com.hotel.cloud.common.utils.ShiroUtils;
import com.hotel.cloud.common.vo.DisableVo;
import com.hotel.cloud.common.vo.agent.AgentUserVo;
import com.hotel.cloud.modules.org.dao.AgentUserDao;
import com.hotel.cloud.modules.org.entity.AgentEntity;
import com.hotel.cloud.modules.org.service.AgentService;
import com.hotel.cloud.modules.org.service.OrgService;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service("agent")
public class AgentServiceImpl extends ServiceImpl<AgentUserDao, AgentEntity> implements AgentService, OrgService<AgentEntity> {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Long orgId = ShiroUtils.isAgent() ? ShiroUtils.getLoginUser().getOrgId() : null;
        String name = (String) params.get("name");
        String contact = (String) params.get("contact");
        String parentName = (String) params.get("parentName");
        String agentLevel = (String) params.get("agentLevel");
        String status = (String) params.get("status");
        IPage<AgentEntity> page = this.page(new Query<AgentEntity>().getPage(params), new QueryWrapper<AgentEntity>()
                .like(StringUtils.isNotBlank(name), "name", MessageFormat.format("%{0}%", name))
                .eq(null != orgId, "parent_id", orgId)
                .like(StringUtils.isNotBlank(contact), "contact", MessageFormat.format("%{0}%", contact))
                .like(StringUtils.isNotBlank(parentName), "parent_name", MessageFormat.format("%{0}%", parentName))
                .eq(StringUtils.isNotBlank(agentLevel), "agent_level", agentLevel)
                .eq(StringUtils.isNotBlank(status), "status", status)
                .eq("flag", FlagEnum.OK.getCode())
                .orderByDesc("create_time")
        );
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveVo(AgentUserVo vo) {
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        AgentEntity entity = vo.getEntity();
        entity.setCreateBy(loginUser.getUsername());
        entity.setUpdateBy(loginUser.getUsername());
        entity.setCreateTime(new Date());

        if (ShiroUtils.isAgent()) {
            AgentEntity agent = this.getById(loginUser.getOrgId());
            if (agent.getAgentLevel() >= AgentTypeEnum.COUNTRY_AGENT.getLevel()) {// 代理类型为县级代理和个人代理不能创建子代理
                throw new RRException(ExceptionEnum.NOT_ALLOW_CREATE_CHILD_AGENT);
            }
            entity.setAgentLevel(agent.getAgentLevel() + 1);
            entity.setParentId(loginUser.getOrgId());
            entity.setParentName(loginUser.getOrgName());
        } else {
            entity.setParentId((long) Constants.ZERO);
        }
        this.save(entity);
    }


    @Override
    @Transactional
    public void updateAgentUser(AgentUserVo vo) {
        AgentEntity entity = vo.getEntity();
        this.updateById(entity);
    }


    @Override
    @Transactional
    public void delete(Long[] ids) {
        List<AgentEntity> entities = Arrays.stream(ids).map(id -> {
            AgentEntity user = new AgentEntity();
            user.setId(id);
            user.setFlag(FlagEnum.DELETE.getCode());
            return user;
        }).collect(Collectors.toList());
        this.updateBatchById(entities);
    }

    @Override
    @Transactional
    public void disable(DisableVo vo) {
        Integer status = vo.getStatus();
        List<AgentEntity> entities = Arrays.stream(vo.getId()).map(id -> {
            AgentEntity user = new AgentEntity();
            user.setId(id);
            user.setStatus(status);
            return user;
        }).collect(Collectors.toList());
        this.updateBatchById(entities);
    }

    @Override
    public List<AgentEntity> selectParentAgent() {
        List<AgentEntity> list;
        if (!ShiroUtils.isAgent()) {
            list = ImmutableList.of(AgentEntity.getEmptyEntity());
        } else {
            list = this.list(
                    new QueryWrapper<AgentEntity>()
                            .ge("agent_level", AgentTypeEnum.PROVINCE_AGENT.getLevel())
                            .le("agent_level", AgentTypeEnum.COUNTRY_AGENT.getLevel())
            );
        }
        return list;
    }


    public List<AgentEntity> select(boolean isAgent) {
        Map<String, Object> params = new HashMap<>();
        if (isAgent) {
            SysUserEntity loginUser = ShiroUtils.getLoginUser();
            params.put("parentId", loginUser.getUserId());
        } else {
            List<Integer> agentLevels = ImmutableList.of(AgentTypeEnum.PROVINCE_AGENT.getLevel(),
                    AgentTypeEnum.INDIVIDUAL_AGENT.getLevel());
            params.put("agentLevels", agentLevels);
        }
        return null;
    }

    @Override
    public List<AgentEntity> select() {
        boolean sysUser = ShiroUtils.isSysUser();
        // 当前用户为系统用户时，查询省级代理和区域代理
        Long orgId = sysUser ? null : ShiroUtils.getLoginUser().getOrgId();
        return this.list(new QueryWrapper<AgentEntity>()
                .in(sysUser, "agent_level", ImmutableList.of(AgentTypeEnum.PROVINCE_AGENT.getLevel(), AgentTypeEnum.INDIVIDUAL_AGENT.getLevel()))
                .eq(null != orgId, "parent_id", orgId)
        );
    }
}