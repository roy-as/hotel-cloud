package com.hotel.cloud.common.vo.equip;

import com.hotel.cloud.common.annotation.Min;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class QrcodeVo {

    @Min(min = 1, message = "设备不能为空")
    private List<Long> ids;

    @NotBlank(message = "二维码前缀不能为空")
    private String prefix;

    @NotBlank(message = "起始值不能为空")
    private String start;
}
