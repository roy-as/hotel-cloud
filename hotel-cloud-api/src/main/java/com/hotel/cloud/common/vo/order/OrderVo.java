package com.hotel.cloud.common.vo.order;

import com.hotel.cloud.modules.order.entity.OrderEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author ${author}
 * @email ${email}
 * @date 2020-10-27 09:22:31
 */
@Data
public class OrderVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String id;
    /**
     * 代理id
     */
    private Long agentId;
    /**
     * 代理名称
     */
    private String agentName;
    /**
     * 酒店id
     */
    private Long hotelId;
    /**
     * 酒店名称
     */
    private String hotelName;
    /**
     * 状态，0待审核，1待发货，2完成
     */
    private Integer status;
    /**
     * 安装公司id
     */
    private Long installationId;
    /**
     * 安装公司名称
     */
    private String installationName;
    /**
     * 物流单号
     */
    private String deliveryNo;
    /**
     * 物流公司
     */
    private String delivery;
    /**
     * 支付方式，1支付宝，2微信，3银行转账
     */
    private Integer payType;
    /**
     * 支付金额
     */
    private BigDecimal amount;
    /**
     * 实际支付金额
     */
    private BigDecimal realAmount;
    /**
     * 优惠券序列号
     */
    private String couponSn;
    /**
     * 支付流水号
     */
    private String payOrderNo;
    /**
     * 备注
     */
    private String remark;

    private List<CouponValidateVo> equips;

    public OrderEntity getEntity() {
        OrderEntity entity = new OrderEntity();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }
}
