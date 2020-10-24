package com.hotel.cloud.common.enums;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * 代理层级
 */
public enum AgentTypeEnum {

    SYSTEM_USER(0, "系统用户"), PROVINCE_AGENT(1, "省级代理"),
    CITY_AGENT(2, "市级代理"), COUNTRY_AGENT(3, "县级代理"),
    INDIVIDUAL_AGENT(4, "个人代理"), UNKNOWN(5, "未知代理类型");

    public final static List<Integer> ALL_AGENT_LEVEL;

    static {
        ALL_AGENT_LEVEL = ImmutableList.of(1, 2 ,3);
    }

    private Integer level;

    private String name;

    AgentTypeEnum(Integer level, String name) {
        this.level = level;
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getAgentLevelName(Integer level) {
        AgentTypeEnum[] enums = values();
        for(AgentTypeEnum agentLevel : enums) {
            if(agentLevel.getLevel().equals(level)) {
                return agentLevel.name;
            }
        }
        return UNKNOWN.name;
    }
}

