package com.hotel.cloud.common.vo.equip;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class QrcodeVo {

    @NotNull(message = "设备不能为空")
    private Long id;

    @NotBlank(message = "二维码前缀不能为空")
    private String info;
}
