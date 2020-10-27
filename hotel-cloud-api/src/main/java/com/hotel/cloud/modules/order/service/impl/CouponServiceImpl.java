package com.hotel.cloud.modules.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.cloud.common.enums.CouponStatusEnum;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import com.hotel.cloud.common.utils.ShiroUtils;
import com.hotel.cloud.common.vo.order.CouponVo;
import com.hotel.cloud.modules.order.dao.CouponDao;
import com.hotel.cloud.modules.order.entity.CouponEntity;
import com.hotel.cloud.modules.order.service.CouponService;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service("couponService")
public class CouponServiceImpl extends ServiceImpl<CouponDao, CouponEntity> implements CouponService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String createBy = (String) params.get("createBy");
        String status = (String) params.get("status");
        String sn = (String) params.get("sn");
        String name = (String) params.get("name");
        String type = (String) params.get("type");
        boolean statusCondition =  StringUtils.isNotBlank(status);
        boolean expired = statusCondition && CouponStatusEnum.EXPIRED.getCode().equals(Integer.valueOf(status));
        boolean notExpired = statusCondition && !CouponStatusEnum.EXPIRED.getCode().equals(Integer.valueOf(status));
        boolean unused = statusCondition && CouponStatusEnum.NOT_USED.getCode().equals(Integer.valueOf(status));
        IPage<CouponEntity> page = this.page(
                new Query<CouponEntity>().getPage(params),
                new QueryWrapper<CouponEntity>()
                        .like(StringUtils.isNotBlank(name), "name", name)
                        .like(StringUtils.isNotBlank(sn), "sn", sn)
                        .like(StringUtils.isNotBlank(createBy), "create_by", createBy)
                        .and(notExpired, wrapper -> wrapper
                                .eq(notExpired, "status", status)
                                .ge(notExpired, "expired_time", new Date())
                                .or(unused)
                                .isNull(unused, "expired_time")

                        )
                        .eq(StringUtils.isNotBlank(type), "type", type)
                        .lt(expired, "expired_time", new Date())
                .orderByAsc("status")
        );
        List<CouponEntity> records = page.getRecords();
        for (CouponEntity record : records) {
            if (null != record.getExpiredTime()) {
                if (record.getStatus().equals(CouponStatusEnum.NOT_USED.getCode()) &&
                        record.getExpiredTime().getTime() < System.currentTimeMillis()) {
                    record.setStatus(2);
                }
            }
        }
        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void create(CouponVo vo) {
        if (null != vo.getExpiredTime() && vo.getExpiredTime().getTime() < System.currentTimeMillis()) {
            throw new RRException(ExceptionEnum.NOT_LESS_NOW);
        }
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        Integer count = vo.getCount();
        String[] sns = generateSn(count);
        List<CouponEntity> entities = IntStream.range(0, count).mapToObj(index -> {
            CouponEntity entity = new CouponEntity();
            entity.setCreateTime(new Date());
            entity.setCreateBy(loginUser.getUsername());
            BeanUtils.copyProperties(vo, entity);
            entity.setSn(sns[index]);
            return entity;
        }).collect(Collectors.toList());
        this.saveBatch(entities);
    }

    private String[] generateSn(Integer count) {
        return IntStream.range(0, count).mapToObj(
                index -> UUID.randomUUID().toString().toUpperCase().replaceAll("-", "").substring(8, 20)
        ).toArray(String[]::new);
    }
}