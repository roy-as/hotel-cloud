package com.hotel.cloud;

import com.hotel.cloud.common.mqtt.Mqtt;
import com.hotel.cloud.common.utils.RedisUtils;
import com.hotel.cloud.modules.equipment.service.EquipService;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import com.hotel.cloud.modules.sys.service.MqttService;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
	@Autowired
	private RedisUtils redisUtils;
	@Autowired
	private EquipService equipService;
	@Autowired
	private MqttService mqttService;

	@Autowired
	private Mqtt mqtt;

	@Test
	public void contextLoads() throws Exception {
		// mqtt.subscribe("40f52022b40a");
		//byte[] message = {0x13, 0x01, 0x07, 0x00, (byte) 0xff, 0x00, 0x00, 0x00, 0x00, 0x07, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xff, 0x00, 0x00, 0x00, 0x00, 0x03, 0x00, 0x00};
		byte[] message = {0x13, 0x11, 0x01, 0x07};
		mqtt.publish("84cca883577b", message, 1);
		mqttService.subscribe("84cca883577b");
		Thread.sleep(1000000);
	}


}
