package com.hotel.cloud.common.vo.equip;

import com.hotel.cloud.modules.equipment.entity.EquipModuleEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class EquipModuleVo {

    /**
     * ID
     */
    private Long id;
    /**
     * 设备模块名称
     */
    @NotBlank(message = "模块名称不能为空")
    private String name;
    /**
     * 房间名称
     */
    private String remark;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 更新人
     */
    private String updateBy;
    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
    private BigDecimal price;
    /**
     * 文件id
     */
    private Long ossId;
    /**
     * 图片地址
     */
    private String pictureUrl;
    /**
     * 协议
     */
    private String protocol;

    private MultipartFile picture;

    public EquipModuleEntity getEntity() {
        EquipModuleEntity entity = new EquipModuleEntity();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }
}
