package com.hotel.cloud.common.utils;

import cn.hutool.core.io.IoUtil;
import com.google.common.collect.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.exception.RRException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.google.zxing.client.j2se.MatrixToImageWriter.toBufferedImage;

public class CommonUtils {

    public static boolean isEmpty(Object[] arr) {
        return null == arr || arr.length == 0;
    }

    public static boolean isNotEmpty(Object[] arr) {
        return !isEmpty(arr);
    }

    /**
     * 生成二维码
     *
     * @param content 二维码内容
     * @param width   高
     * @param height  宽
     * @return 二维码的base64字符串
     * @throws RRException
     */
    public static byte[] createQrCode(String content, int width, int height, InputStream logoStream) throws RRException {
        // 定义二维码的参数
        Map<EncodeHintType, Object> hints = Maps.newHashMap();
        hints.put(EncodeHintType.CHARACTER_SET, Constants.DEFAULT_CHARSET);    // 指定字符编码
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);  // 指定二维码的纠错等级
        hints.put(EncodeHintType.MARGIN, 2);    // 设置图片的边距

        // 生成二维码
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            BufferedImage image = toBufferedImage(bitMatrix, new MatrixToImageConfig(0xFE000000, 0xFFFFFFFF));
            // 添加中心logo
            if (null != logoStream) {
                image = addLogoToQrCode(image, logoStream);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // 输出二维码图片流
            ImageIO.write(image, Constants.QRCODE_FORMAT, outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException(ExceptionEnum.INTERNAL_SERVER_ERROR);
        }
    }

    public static byte[] createQrCode(String content, InputStream logo) {
        return createQrCode(content, Constants.DEFAULT_QRCODE_WIDTH, Constants.DEFAULT_QRCODE_HEIGHT, logo);
    }

    public static byte[] createQrCode(String content) {
        return createQrCode(content, Constants.DEFAULT_QRCODE_WIDTH, Constants.DEFAULT_QRCODE_HEIGHT, null);
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 为二维码中心添加logo
     *
     * @param image      二维码图像
     * @param logoStream logo input流
     * @return
     */
    private static BufferedImage addLogoToQrCode(BufferedImage image, InputStream logoStream) {
        // 读取logo图片
        BufferedImage logo;
        try {
            logo = ImageIO.read(logoStream);
        } catch (IOException e) {
            e.printStackTrace();
            return image;
        }
        // 获取画笔
        Graphics2D graphics = image.createGraphics();
        // 设置二维码大小，太大，会覆盖二维码，此处20%
        int logoWidth = logo.getWidth(null) > image.getWidth() * 2 / 10 ? (image.getWidth() * 2 / 10)
                : logo.getWidth(null);
        int logoHeight = logo.getHeight(null) > image.getHeight() * 2 / 10 ? (image.getHeight() * 2 / 10)
                : logo.getHeight(null);
        // 设置logo图片放置位置
        // 中心
        int x = (image.getWidth() - logoWidth) / 2;
        int y = (image.getHeight() - logoHeight) / 2;
        // 开始合并绘制图片
        graphics.drawImage(logo, x, y, logoWidth, logoHeight, null);
        graphics.drawRoundRect(x, y, logoWidth, logoHeight, 15, 15);
        // logo边框大小
        graphics.setStroke(new BasicStroke(2));
        // logo边框颜色
        graphics.setColor(Color.WHITE);
        graphics.drawRect(x, y, logoWidth, logoHeight);
        graphics.dispose();
        logo.flush();
        image.flush();
        return image;
    }

    public static String genOrderId() {
        return LocalDateTime.now().format(Constants.FORMATTER) + subUUID();
    }

    public static String subUUID() {
        StringBuilder uuid = new StringBuilder(UUID.randomUUID().toString().toUpperCase().replaceAll("-", "").substring(8, 20));
        for (int i = 0; i < 2; i++) {
            int num = (int) (Math.random() * 10);
            uuid.append(num);
        }
        return uuid.toString();
    }

    public static String[] subUUID(Integer count) {
        return IntStream.range(0, count).mapToObj(
                index -> subUUID()
        ).toArray(String[]::new);
    }

    /**
     * 16进制转化为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] hexStr2bytes(String hexStr) {
        if (StringUtils.isBlank(hexStr)) {
            return null;
        }
        if (hexStr.length() % 2 != 0) {//长度为单数
            hexStr = "0" + hexStr;//前面补0
        }
        char[] chars = hexStr.toCharArray();
        int len = chars.length / 2;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            int x = i * 2;
            bytes[i] = Byte.parseByte(String.valueOf(new char[]{chars[x], chars[x + 1]}), 16);
        }
        return bytes;
    }

    public static byte[] string2Bytes(String... strs) {
        int length = strs.length;
        int totalLength = Arrays.stream(strs).mapToInt(String::length).sum();
        byte[] data = new byte[length + totalLength];
        int index = 0;
        for (String str : strs) {
            data[index] = (byte) str.length();
            System.arraycopy(str.getBytes(), 0, data, index + 1, str.length());
            index = index + str.length() + 1;
        }
        return data;
    }

    public static void zip(Set<String> srcFiles, ZipOutputStream zos) throws IOException {
        List<InputStream> inputs = new ArrayList<>(srcFiles.size());
        for (String path : srcFiles) {
            File file = new File(path);
            if(file.exists()) {
                FileInputStream is = new FileInputStream(file);
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zos.putNextEntry(zipEntry);
                IOUtils.copy(is, zos);
                inputs.add(is);
            }
        }
        if(null != zos) {
            zos.flush();
            zos.closeEntry();
            zos.close();
        }
        if(CollectionUtils.isNotEmpty(inputs)) {
            for (InputStream is : inputs) {
                is.close();
            }
        }
    }

    public static void createFile(String path, InputStream is) throws Exception {
        FileOutputStream os = new FileOutputStream(new File(path));
        IoUtil.copy(is, os);
        os.flush();
        os.close();
        is.close();
    }

    public static void main(String[] args) throws Exception {
        Set<String> list = new LinkedHashSet<>();
        list.add("/Users/aby/Desktop/test1/1.png");
        list.add("/Users/aby/Desktop/test1/2.png");
        list.add("/Users/aby/Desktop/test1/3.png");
        FileOutputStream os = new FileOutputStream("/Users/aby/Desktop/test1/test.zip");
        zip(list, new ZipOutputStream(os));
    }
}

