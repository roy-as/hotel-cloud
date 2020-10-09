package com.hotel.cloud.common.enums;

public enum HotelPictureTypeEnum {

    HOTEL_LOGO(0, "酒店Logo"),
    HOTEL_FULL_VIEW_PICTURE(1, "酒店360实景图"),
    HOTEL_PICTURE(2, "酒店照片"),
    ROOM_FULL_VIEW_PICTURE(3, "房间360实景图"),
    ROOM_PICTURE(4, "房型照片");

    private Integer type;

    private String desc;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    HotelPictureTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
