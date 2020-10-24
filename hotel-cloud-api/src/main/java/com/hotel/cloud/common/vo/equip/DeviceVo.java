package com.hotel.cloud.common.vo.equip;

import com.hotel.cloud.modules.equipment.entity.DeviceEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
@Data
public class DeviceVo {

    /**
     * 主键
     */
    private Long id;
    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    private String name;
    /**
     * 文件id
     */
    private Long ossId;
    /**
     * 图片url
     */
    private String pictureUrl;
    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
    private BigDecimal price;
    /**
     * 描述
     */
    private String remark;
    /**
     * 当前数量
     */
    @NotNull(message = "数量不能为空")
    private Integer amount;

    private MultipartFile picture;

    public DeviceEntity getEntity() {
        DeviceEntity entity = new DeviceEntity();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }
}
