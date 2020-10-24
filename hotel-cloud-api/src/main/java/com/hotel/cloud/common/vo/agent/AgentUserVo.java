package com.hotel.cloud.common.vo.agent;

import com.alibaba.fastjson.JSON;
import com.hotel.cloud.common.annotation.Length;
import com.hotel.cloud.common.utils.Constants;
import com.hotel.cloud.modules.org.entity.AgentEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class AgentUserVo {

    private Long id;

    private Long parentId = 0L;

    private String parentName = Constants.NONE;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = Constants.MOBILE_PATTERN, message = "手机格式错误")
    private String mobile;

    @NotBlank(message = "邮箱不能为空")
    @Pattern(regexp = Constants.EMAIL_PATTERN, message = "邮箱格式错误")
    private String email;

    @NotBlank(message = "联系人不能为空")
    private String contact;

    @NotBlank(message = "代理名称不能为空")
    private String name;


    @Length(size = 3, message = "地区格式错误")
    private String[] areas;

    @NotBlank(message = "地址不能为空")
    private String address;

    private Integer status = 1;

    private Integer agentLevel;

    private String remark;

    public AgentEntity getEntity() {
        AgentEntity entity = new AgentEntity();
        BeanUtils.copyProperties(this, entity);
        entity.setArea(JSON.toJSONString(this.getAreas()));
        return entity;
    }
}
