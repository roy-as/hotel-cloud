package com.hotel.cloud.common.vo.agent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestVo {

    private String ip;

    private int count;

    private String uri;

    private long lastRequest;
}
