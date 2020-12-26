// import Mock from 'mockjs'

// 获取文件列表
export function list () {
  return {
    url: '/order/order/equipList',
    type: 'get',
    data: {
      'msg': 'success',
      'code': 0,
      'equip': [// 智能主机
        {
          'id': 15,
          'name': '空调',
          'moduleId': 2,
          'ossId': 155,
          'agentId': null,
          'hotelId': null,
          'mac': 'AA-AA-AA-AA-AA-AA',
          'versionNumber': null,
          'remark': '',
          'flag': 1,
          'status': 2,
          'createTime': '2020-10-19 15:11:56',
          'updateTime': '2020-10-19 15:45:37',
          'createBy': 'admin',
          'updateBy': 'admin',
          'qrcodeInfo': '我是云住科技哟123123',
          'moduleName': '空调',
          'hotelName': null,
          'agentName': null,
          'qrcodeUrl': 'http://qhrwtqjy8.hn-bkt.clouddn.com/hotel-cloud/upload/img/20201019/35615e8618e0495aa8e6459819d620d2.png',
          'ip': null,
          'expiredTime': '2020-10-31 00:00:00',
          'online': 0,
          'price': 500.00 // 价格
        },
        {
          'id': 13,
          'name': '空调',
          'moduleId': 2,
          'ossId': 153,
          'agentId': 17,
          'hotelId': null,
          'mac': 'AE-AE-AE-AE-AE-AE',
          'versionNumber': null,
          'remark': '',
          'flag': 1,
          'status': 2,
          'createTime': '2020-10-19 11:46:59',
          'updateTime': '2020-10-19 14:59:21',
          'createBy': 'admin',
          'updateBy': 'admin',
          'qrcodeInfo': '测试挺好的啊，美滋滋',
          'moduleName': '空调',
          'hotelName': null,
          'agentName': '天成',
          'qrcodeUrl': 'http://qhrwtqjy8.hn-bkt.clouddn.com/hotel-cloud/upload/img/20201019/80b171156b4c44f59f2147146bc909ee.png',
          'ip': null,
          'expiredTime': null,
          'online': 0,
          'price': 500.00
        }
      ],
      'device': [// 智能设备
        {
          'id': 2,
          'name': '测试',
          'ossId': 162,
          'pictureUrl': 'http://qhrwtqjy8.hn-bkt.clouddn.com/hotel-cloud/upload/img/20201021/d737ac1462c94db58e397e455c775ec2.jpeg',
          'price': 123.20, // 价格
          'remark': '',
          'amount': 100, // 数量
          'saleAmount': 0,
          'createBy': 'admin',
          'createTime': '2020-10-21 11:28:59',
          'updateTime': '2020-10-21 11:41:24',
          'updateBy': 'admin',
          'flag': 1
        }
      ]
    }
  }
}
