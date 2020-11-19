package com.hotel.cloud.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.cloud.common.utils.*;
import com.hotel.cloud.config.property.SysProperty;
import com.hotel.cloud.modules.oss.entity.SysOssEntity;
import com.hotel.cloud.modules.oss.service.SysOssService;
import com.hotel.cloud.modules.sys.dao.QrcodeDao;
import com.hotel.cloud.modules.sys.entity.QrcodeEntity;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import com.hotel.cloud.modules.sys.service.QrcodeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.zip.ZipOutputStream;


@Service("qrcodeService")
public class QrcodeServiceImpl extends ServiceImpl<QrcodeDao, QrcodeEntity> implements QrcodeService {

    @Resource
    private SysOssService sysOssService;

    @Resource
    private SysProperty sysProperty;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<QrcodeEntity> page = this.page(
                new Query<QrcodeEntity>().getPage(params),
                new QueryWrapper<QrcodeEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        InputStream is = this.getClass().getResourceAsStream(Constants.QRCODE_IMPORT_TEMPLATE);
        OutputStream os = response.getOutputStream();
        CommonUtils.download(response, Constants.QRCODE_TEMPLATE_FILE_NAME);
        IOUtils.copy(is, os);
        os.flush();
        is.close();
    }

    @Override
    @Transactional
    public void importExcel(MultipartFile file) throws IOException {
        List<QrcodeEntity> entities = ExcelUtils.importExcel(file, 0, 1, QrcodeEntity.class);
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        List<QrcodeEntity> list = new ArrayList<>(entities.size());
        String username = ShiroUtils.getLoginUser().getUsername();
        Date now = new Date();
        for (QrcodeEntity entity : entities) {
            int number = entity.getNumber();
            for (int i = 0; i < number; i++) {
                String info = entity.getInfo() + "-" + CommonUtils.subUUID() + "-" + System.currentTimeMillis() / 1000000;
                byte[] data = CommonUtils.createQrCode(info, info);
                SysOssEntity oss = sysOssService.upload(data, Constants.PNG_SUFFIX);
                QrcodeEntity qrcode = new QrcodeEntity();
                qrcode.setInfo(info);
                qrcode.setUrl(oss.getUrl());
                qrcode.setCreateBy(username);
                qrcode.setCreateTime(now);
                list.add(qrcode);
            }
        }
        if (CollectionUtils.isNotEmpty(list)) {
            this.baseMapper.insertBatch(list);
        }
    }

    @Override
    public void download(Long[] ids, HttpServletResponse response) throws IOException {
        List<QrcodeEntity> equips = this.baseMapper.selectBatchIds(Arrays.asList(ids));
        List<Callable<InputStream>> tasks = new ArrayList<>(equips.size());
        for (QrcodeEntity qrcode : equips) {
            tasks.add(() -> HttpUtils.get(qrcode.getUrl()));
        }
        List<InputStream> isList = ThreadPoolUtils.execute(tasks);
        Set<String> filePathList = new LinkedHashSet<>(isList.size());
        for (InputStream is : isList) {
            String filePath = sysProperty.getTempDir() + CommonUtils.uuid() + Constants.PNG_SUFFIX;
            FileOutputStream os = new FileOutputStream(new File(filePath));
            IOUtils.copy(is, os);
            os.close();
            is.close();
            filePathList.add(filePath);
        }
        String fileName = LocalDateTime.now().format(Constants.FORMATTER_TIME) + Constants.ZIP_SUFFIX;
        CommonUtils.download(response, fileName);
        ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
        CommonUtils.zip(filePathList, zos);
    }

}