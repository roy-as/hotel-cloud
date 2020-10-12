package com.hotel.cloud.common.vo.hotel;

import com.alibaba.fastjson.JSON;
import com.hotel.cloud.common.annotation.Length;
import com.hotel.cloud.common.utils.Constants;
import com.hotel.cloud.modules.hotel.entity.HotelInfoEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Arrays;
import java.util.List;

@Data
public class HotelInfoVo {

    /**
     * 唯一主键
     */
    private Long id;
    /**
     * 酒店名称
     */
    @NotBlank(message = "酒店名称不能为空")
    private String name;
    /**
     * 地区
     */
    @Length(size = 3, message = "地区格式错误")
    private String[] area;
    /**
     * 门店地址
     */
    @NotBlank(message = "详细地址不能为空")
    private String address;
    /**
     * 温馨提示
     */
    private String notice;
    /**
     * 酒店描述
     */
    private String remark;
    /**
     * 联系电话
     */
    @NotBlank(message = "手机不能为空")
    @Pattern(regexp = Constants.MOBILE_PATTERN, message = "手机格式错误")
    private String mobile;
    /**
     * 酒店服务
     */
    private String service;
    /**
     * logo文件
     */
    private MultipartFile logo;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 全景图
     */
    private MultipartFile[] fullViews;
    /**
     * 酒店图片
     */
    private MultipartFile[] hotelPictures;
    /**
     *  图片id
     */
    private Long[] pictureIds;

    public HotelInfoEntity getHotelInfoEntity() {
        HotelInfoEntity hotelInfo = new HotelInfoEntity();
        BeanUtils.copyProperties(this, hotelInfo);
        hotelInfo.setArea(JSON.toJSONString(this.area));
        return hotelInfo;
    }

    public List<Long> getPictureIds () {
        return Arrays.asList(this.pictureIds);
    }
}
