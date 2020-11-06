package com.hotel.cloud.common.vo.order;

import com.hotel.cloud.common.annotation.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponValidate {

    @NotBlank(message = "优惠群序列号不能为空")
    String sn;

    @Min(min = 1)
    List<CouponValidateVo> validationInfo;

    @NotNull(message = "总价格不能为空")
    private BigDecimal amount;
}
