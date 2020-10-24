package com.hotel.cloud.common.enums;

import com.google.common.collect.ImmutableList;
import com.hotel.cloud.common.exception.RRException;

import java.util.List;

/**
 * 代理层级
 */
public enum UserTypeEnum {

    SYSTEM_USER(0, "系统用户"), AGENT(1, "代理商用户", "agent"),
    HOTEL(2, "酒店用户", "hotel"), INSTALLATION(3, "安装公司用户", "installation"),
    CHILD_USER(4, "子用户"),
    UNKNOWN(100, "未知代理类型");

    public final static List<Integer> ALL_AGENT_LEVEL;

    static {
        ALL_AGENT_LEVEL = ImmutableList.of(1, 2 ,3);
    }

    private Integer level;

    private String name;

    private String desc;

    UserTypeEnum(Integer level, String name) {
        this.level = level;
        this.name = name;
    }

    UserTypeEnum(Integer level, String name, String desc) {
        this.level = level;
        this.name = name;
        this.desc = desc;
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


    public static String getDesc(Integer level) {
        UserTypeEnum[] enums = values();
        for(UserTypeEnum agentLevel : enums) {
            if(agentLevel.getLevel().equals(level)) {
                return agentLevel.desc;
            }
        }
        throw new RRException(ExceptionEnum.ENUM_NOT_EXIST);
    }

    public static String getName(Integer level) {
        UserTypeEnum[] enums = values();
        for(UserTypeEnum agentLevel : enums) {
            if(agentLevel.getLevel().equals(level)) {
                return agentLevel.name;
            }
        }
        throw new RRException(ExceptionEnum.ENUM_NOT_EXIST);
    }
}

