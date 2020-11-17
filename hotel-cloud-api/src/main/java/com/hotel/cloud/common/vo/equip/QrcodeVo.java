package com.hotel.cloud.common.vo.equip;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrcodeVo {

    @NotNull(message = "设备不能为空")
    private Long id;

    @NotBlank(message = "二维码前缀不能为空")
    private String info;
}
