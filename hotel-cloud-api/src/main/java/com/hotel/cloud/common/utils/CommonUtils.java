package com.hotel.cloud.common.utils;

import com.google.common.collect.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.exception.RRException;
import org.apache.commons.lang.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

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
        for(int i =0; i < 2; i++) {
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
}

