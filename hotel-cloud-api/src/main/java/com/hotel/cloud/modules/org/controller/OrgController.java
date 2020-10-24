package com.hotel.cloud.modules.org.controller;

import com.hotel.cloud.common.utils.R;
import com.hotel.cloud.modules.org.service.OrgService;
import com.hotel.cloud.modules.org.utils.OrgServiceFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/org")
public class OrgController {

    @Resource
    private OrgServiceFactory factory;


    @GetMapping("select")
    public R select(Integer userType) {
        OrgService<?> service = factory.getInstance(userType);
        List<?> data = service.select();
        return R.ok().put("data", data);
    }
}
