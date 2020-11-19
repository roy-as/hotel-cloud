package com.hotel.cloud.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.modules.sys.entity.QrcodeEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * 
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-11-18 19:55:16
 */
public interface QrcodeService extends IService<QrcodeEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void downloadTemplate(HttpServletResponse response) throws IOException;

    void importExcel(MultipartFile file) throws IOException;

    void download(Long[] ids, HttpServletResponse response) throws IOException;
}

