package com.hotel.cloud.common.vo.equip;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
public class EquipVo {

    @Size(min = 1, message = "设备不能为空")
    private List<Long> ids;

    private Long hotelId;

    private Long agentId;

    private String hotelName;

    private String agentName;

    private Date expiredTime;

    private Long count;
}
