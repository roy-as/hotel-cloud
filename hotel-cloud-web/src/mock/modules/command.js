// 获取文件列表
export function list () {
    return {
      url: '/sys/command/data',
      type: 'get',
      data: {
          "msg":"success",
          "code":0,
          "data":[
              {"id":1,"name":"WIFI设置","command":"[25]","data":"[\"账户\",\"密码\"]","remark":"修改已连接上平台设备，WIFI用户名、密码","createBy":"admin","updateBy":"admin","createTime":"2020-11-11 19:40:45","updateTime":"2020-11-12 09:14:21"},
              {"id":2,"name":"MQTT设置","command":"[24]","data":"[\"账户\",\"密码\"]","remark":"修改已连接上平台设备，MQTT账户、密码","createBy":"admin","updateBy":"admin","createTime":"2020-11-11 19:54:17","updateTime":"2020-11-12 09:16:13"},
              {"id":5,"name":"测试","command":"[111, 4]","data":"[\"楼栋\", \"楼层\", \"房号\"]","remark":"","createBy":"admin","updateBy":"admin","createTime":"2020-11-11 21:20:41","updateTime":"2020-11-11 21:28:00"}
            ]
        }
    }
  }
  