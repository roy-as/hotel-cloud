package com.hotel.cloud.common.vo.hotel;

import com.hotel.cloud.modules.hotel.entity.HotelRoomTypeEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Data
public class RoomTypeVo {

    /**
     * ID
     */
    private Long id;
    /**
     * 房型名称
     */
    @NotBlank(message = "名称不能为空")
    private String name;
    /**
     * 面积
     */
    @NotNull(message = "面积不能为空")
    private BigDecimal square;
    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
    private BigDecimal price;
    /**
     * 房型描述
     */
    private String remark;
    /**
     * 酒店ID
     */
    @NotNull(message = "酒店不能为空")
    private Long hotelId;
    /**
     * 酒店名称
     */
    private String hotelName;
    /**
     * 房型图片
     */
    private MultipartFile[] roomPictures;
    /**
     * 图片id
     */
    private Long[] pictureIds;

    public HotelRoomTypeEntity getHotelRoomTypeEntity() {
        HotelRoomTypeEntity entity = new HotelRoomTypeEntity();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }

    public List<Long> getPictureIds () {
        return Arrays.asList(this.pictureIds);
    }
}
