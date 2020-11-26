package com.hotel.cloud.modules.sys.service.impl;

import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.mqtt.Mqtt;
import com.hotel.cloud.common.utils.CommonUtils;
import com.hotel.cloud.common.utils.Constants;
import com.hotel.cloud.common.utils.RedisUtils;
import com.hotel.cloud.common.vo.CommandStatus;
import com.hotel.cloud.config.property.MqttProperty;
import com.hotel.cloud.modules.sys.service.MqttService;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

@Service
public class MqttServiceImpl implements MqttService {

    @Autowired
    private Mqtt mqtt;

    @Autowired
    private MqttProperty mqttProperty;

    @Autowired
    private RedisUtils redisUtils;

    private static Map<String, byte[]> COMMAND;

    static {
        COMMAND = new HashMap<>();
        COMMAND.put("wifi", new byte[]{0x23});
        COMMAND.put("switch", new byte[]{0x13, 0x11, 0x01});
        COMMAND.put("switchQuery", new byte[]{0x13, 0x01});
    }

    @Override
    public CommandStatus publish(String target, byte[] commands, Integer commandType, String... data) throws Exception {
        byte[] body = commands;
        if (null != data) {
            byte[] bytes = null;
            switch (commandType) {
                case 1:
                    bytes = CommonUtils.string2Bytes(data);
                    break;
                case 2:
                    bytes = CommonUtils.hexStr2bytes(data[0].replaceAll(",", ""));
                    break;
                case 3:
                    for (String str : data) {
                        if (null == bytes) {
                            bytes = CommonUtils.hexStr2bytes(str);
                        } else {
                            bytes = ArrayUtils.addAll(bytes, CommonUtils.hexStr2bytes(str));
                        }
                    }
                    break;
                default:
                    throw new RRException(ExceptionEnum.PARAM_ERROR);
            }

            body = ArrayUtils.addAll(commands, bytes);
        }
        long start = System.currentTimeMillis();
        this.mqtt.publish(target, body, 1);
        String redisKey = MessageFormat.format(Constants.REDIS_COMMAND_KEY, target);
        CommandStatus commandStatus = null;
        while (System.currentTimeMillis() - start <= 5000) {
            commandStatus = redisUtils.get(redisKey, CommandStatus.class);
            if (null != commandStatus && null != commandStatus.getCommand()) {
                if (ArrayUtils.isEquals(commands, COMMAND.get("switch"))) {
                    commandStatus.setSuccess(ArrayUtils.isEquals(body, commandStatus.getDownBody()) && ArrayUtils.isEquals(ArrayUtils.subarray(commandStatus.getCommand(), 0, 2), COMMAND.get("switchQuery")));
                } else {
                    commandStatus.setSuccess(ArrayUtils.isEquals(body, commandStatus.getDownBody()) && ArrayUtils.isEquals(commands, ArrayUtils.subarray(commandStatus.getCommand(), 0, commands.length)));
                }
                if (commandStatus.isSuccess()) {
                    commandStatus.setData(this.resolve(commands, commandStatus.getUpBody()));
                }
                return commandStatus;
            }
        }
        if (null == commandStatus) {
            commandStatus = new CommandStatus();
            commandStatus.setMac(target);
        }
        commandStatus.setSuccess(false);
        return commandStatus;

    }

    @Override
    public void subscribe(String target) throws MqttException {
        mqtt.subscribe(MessageFormat.format(mqttProperty.getSubscribeTopic(), target));
        mqtt.subscribe(MessageFormat.format(mqttProperty.getPublishTopic(), target));
    }

    private Map<String, Object> resolve(byte[] command, byte[] body) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (ArrayUtils.isEquals(command, COMMAND.get("wifi"))) {
            byte[] data = ArrayUtils.subarray(body, command.length, body.length);
            byte signal = data[0];
            int accountLength = data[1];
            byte[] account = ArrayUtils.subarray(data, 2, 2 + accountLength);
            byte[] password = ArrayUtils.subarray(data, 3 + accountLength, data.length);
            result.put("signal", signal);
            result.put("account", new String(account));
            result.put("password", new String(password));
        } else if (ArrayUtils.isEquals(command, COMMAND.get("switch"))) {
            byte[] data = ArrayUtils.subarray(body, 2, body.length);
            byte key = data[0];
            byte[] openKey = ArrayUtils.subarray(data, 1, 6);
            byte[] openKeyStatus = ArrayUtils.subarray(data, 6, 11);
            byte[] closeKey = ArrayUtils.subarray(data, 11, 16);
            byte[] closeKeyStatus = ArrayUtils.subarray(data, 16, 21);
            String openKeyBinary = CommonUtils.byte2BinaryString(openKey);
            String openKeyStatusBinary = CommonUtils.byte2BinaryString(openKeyStatus);
            String closeKeyBinary = CommonUtils.byte2BinaryString(closeKey);
            String closeKeyStatusBinary = CommonUtils.byte2BinaryString(closeKeyStatus);
            List<Integer> openKeyList = new ArrayList<>();
            List<Integer> openKeyOpenList = new ArrayList<>();
            List<Integer> openKeyCloseList = new ArrayList<>();
            List<Integer> closeKeyList = new ArrayList<>();
            List<Integer> closeKeyOpenList = new ArrayList<>();
            List<Integer> closeKeyCloseList = new ArrayList<>();
            result.put("key", key);
            result.put("openKeyList", openKeyList);
            result.put("openKeyOpenList", openKeyOpenList);
            result.put("openKeyCloseList", openKeyCloseList);
            result.put("closeKeyList", closeKeyList);
            result.put("closeKeyOpenList", closeKeyOpenList);
            result.put("closeKeyCloseList", closeKeyCloseList);

            for (int i = 1; i <= 5; i++) {
                int start = (i - 1) * 8;
                int end = i * 8;
                String open = openKeyBinary.substring(start, end);
                String openStatus = openKeyStatusBinary.substring(start, end);
                String close = closeKeyBinary.substring(start, end);
                String closeStatus = closeKeyStatusBinary.substring(start, end);
                int index = i * 8;
                for (int j = 0; j < 8; j++) {
                    char openChar = open.charAt(j);
                    char openStatusChar = openStatus.charAt(j);
                    if (openChar == '1') {
                        openKeyList.add(index);
                        if (openStatusChar == '1') {
                            openKeyOpenList.add(index);
                        } else {
                            openKeyCloseList.add(index);
                        }
                    }
                    char closeChar = close.charAt(j);
                    char closeStatusChar = closeStatus.charAt(j);
                    if (closeChar == '1') {
                        closeKeyList.add(index);
                        if (closeStatusChar == '1') {
                            closeKeyOpenList.add(index);
                        } else {
                            closeKeyCloseList.add(index);
                        }
                    }
                    index--;
                }

            }

        } else {
            result.put("data", Arrays.toString(ArrayUtils.subarray(body, command.length, body.length)));
        }
        return result;
    }
}
