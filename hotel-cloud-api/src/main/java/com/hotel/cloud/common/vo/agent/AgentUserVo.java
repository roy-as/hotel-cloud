package com.hotel.cloud.common.vo.agent;

import com.alibaba.fastjson.JSON;
import com.hotel.cloud.common.annotation.Length;
import com.hotel.cloud.common.utils.Constants;
import com.hotel.cloud.modules.agent.entity.AgentUserEntity;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class AgentUserVo {

    private Long userId;

    @NotNull(message = "上级代理不能为空")
    private Long parentId;

    private String parentName;

    @NotBlank(message = "账号不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = Constants.MOBILE_PATTERN, message = "手机格式错误")
    private String mobile;

    @NotBlank(message = "邮箱不能为空")
    @Pattern(regexp = Constants.EMAIL_PATTERN, message = "邮箱格式错误")
    private String email;

    @NotBlank(message = "联系人不能为空")
    private String contactPerson;

    @NotBlank(message = "代理名称不能为空")
    private String agentName;

    private String province;

    @Length(size = 3, message = "地区格式错误")
    private String[] areas;

    @NotBlank(message = "地址不能为空")
    private String address;

    private Integer status = 1;

    private Integer agentLevel;

    private String remark;

    public void setPassword(String password) {
        if(StringUtils.isNotBlank(password)) {
            this.password = password;
        }
    }

    public SysUserEntity getSysUserEntity() {
        SysUserEntity entity = new SysUserEntity();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }

    public AgentUserEntity getAgentUserEntity() {
        AgentUserEntity entity = new AgentUserEntity();
        BeanUtils.copyProperties(this, entity);
        entity.setArea(JSON.toJSONString(this.getAreas()));
        return entity;
    }
}
