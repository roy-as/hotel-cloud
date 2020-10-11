package com.hotel.cloud.common.vo.equip;

import com.hotel.cloud.common.annotation.Min;
import lombok.Data;

import java.util.List;

@Data
public class ReleaseEquipVo {

    @Min(min = 1, message = "设备不能为空")
    private List<Long> ids;

    private Long hotelId;

    private Long agentId;

    private String hotelName;

    private String agentName;
}
