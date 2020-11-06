package com.hotel.cloud.modules.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.R;
import com.hotel.cloud.common.vo.order.OrderVo;
import com.hotel.cloud.modules.order.entity.OrderEntity;

import java.util.Map;

/**
 * 
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-27 09:22:31
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveOrder(OrderVo vo);

    R equipList();

    PageUtils deviceList(Map<String, Object> params);

    void delivery(OrderVo vo);

    void pay(OrderVo vo);
}

