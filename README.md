
# InfluxDBReader 插件文档

___


## 1 快速介绍

InfluxDBReader 插件实现了 DataX 从 InfluxDB 读取数据。
原厂大佬的代码犹如神仙打架 ，ლ(ٱ٥ٱლ) ，渣农含泪自研丐版。亲测可用，除非口罩戴的姿势不对！

作者信息： ZYT@2020@NanJing 。有问题联系：wowiscrazy@163.com 。欢迎贡献意见或者源码持续改进。

（源码基于父项目，不包含在本项目中，如果觉得依赖配置麻烦，可直接使用打包好的influxdbreader插件，
位于target\datax\plugin\reader文件夹下，将其中的influxdbreader整个文件夹拷贝出来黏贴到DataX软件的datax\plugin\reader文件夹下即可安装完并使用。客官，下载后麻烦给点个小星星哦。）



## 2 功能说明

### 2.1 配置样例

* 配置一个从 InfluxDB 数据库同步抽取数据到本地的作业(注意：查询语句中，time字段放在第一个)：



```json
{
  "job": {
    "content": [
      {
        "reader": {
          "name": "influxdbreader",
          "parameter": {
            "dbType": "InfluxDB",
            "address": "http://xxx.xxx.xxx.xxx:8086",
            "username": "xxx",
            "password": "xxx",
            "database": "xxx",
            "querySql": "select time,xx,xx from xxxx limit 2020"
          }
        },
        "writer": {
          "name": "streamwriter",
          "parameter": {
            "encoding": "UTF-8",
            "print": true
          }
        }
      }
    ],
    "setting": {
      "speed": {
        "channel": 1
      }
    }
  }
}
```







### 2.2 参数说明

* **name**
  * 描述：本插件的名称
  * 必选：是
  * 默认值：influxdbreader

* **parameter**
  * **dbType**
    * 描述：目标数据库的类型
    * 必选：是
    * 默认值：无
    * 注意：目前仅支持 "InfluxDB"。

  * **address**
    * 描述：InfluxDB 的 HTTP 连接地址
    * 必选：是
    * 格式：http://IP:Port
    * 默认值：无
    
  * **username**
    * 描述：InfluxDB 的 用户名
    * 必选：否
    * 说明：如果有用户名请填写
    
  * **password**
    * 描述：InfluxDB 的 密码
    * 必选：否
    * 说明：如果有密码请填写
    
  * **database**
    * 描述：InfluxDB 的 数据库名称
    * 必选：是
    * 默认值：无
    
  * **querySql**
    * 描述：InfluxDB 的 查询sql语句
    * 必选：是
    * 默认值：无   
 



### 2.3 类型转换

| DataX 内部类型 | InfluxDB 数据类型                                                |
| -------------- | ------------------------------------------------------------ |
| String         | InfluxDB 数据点序列化字符串，包括 time、tags、fields 和 value |

