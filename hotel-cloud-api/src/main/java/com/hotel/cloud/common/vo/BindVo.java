package com.hotel.cloud.common.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class BindVo {

    @NotNull(message = "设备不能为空")
    private String mac;

    @NotNull(message = "绑定类型不能为空")
    private Integer type;

    @NotNull(message = "序号不能为空")
    private Byte key;

    private List<Integer> openKeyOpen;

    private List<Integer> openKeyClose;

    private List<Integer> closeKeyOpen;

    private List<Integer> closeKeyClose;

    private List<Integer> keys;

    private List<Integer> switches;

    public List<Integer> getOpenKeyOpen() {
        return Optional.ofNullable(openKeyOpen).orElse(new ArrayList<>());
    }

    public List<Integer> getOpenKeyClose() {
        return Optional.ofNullable(openKeyClose).orElse(new ArrayList<>());
    }

    public List<Integer> getCloseKeyOpen() {
        return Optional.ofNullable(closeKeyOpen).orElse(new ArrayList<>());
    }

    public List<Integer> getCloseKeyClose() {
        return Optional.ofNullable(closeKeyClose).orElse(new ArrayList<>());
    }
}
