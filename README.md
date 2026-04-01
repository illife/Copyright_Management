版权管理平台后端项目

基于 FISCO BCOS 联盟链的版权管理系统后端服务。

📋 项目概述

本系统实现了完整的版权管理功能，包括：

- ✅ 用户注册与管理
- ✅ 版权注册与查询
- ✅ 版权授权管理（申请、审批、激活、撤销）
- ✅ 版税记录与查询
- ✅ 区块链事件监听
- ✅ 审计日志

🛠 技术栈

  技术            	版本    	说明   
  Spring Boot   	2.5.6 	基础框架 
  MyBatis-Plus  	3.5.3 	数据持久化
  FISCO BCOS SDK	2.9.1 	区块链集成
  MySQL         	8.0+  	数据库  
  Druid         	1.2.11	连接池  
  Lombok        	-     	简化开发 
  JWT           	0.11.5	身份认证 

📦 项目结构

    src/main/java/com/example/copyright/
    ├── config/              # 配置类
    │   ├── FiscoBcosConfig.java      # FISCO BCOS配置
    │   ├── ContractConfig.java       # 合约地址配置
    │   ├── IpfsConfig.java           # IPFS配置
    │   └── CorsConfig.java           # 跨域配置
    ├── controller/          # API控制器
    │   ├── UserController.java
    │   ├── CopyrightController.java
    │   ├── LicenseController.java
    │   ├── RoyaltyController.java
    │   └── HealthController.java     # 健康检查
    ├── dto/                 # 数据传输对象
    ├── entity/              # 实体类
    ├── exception/           # 异常处理
    ├── listener/            # 事件监听器
    ├── mapper/              # MyBatis数据访问层
    ├── service/             # 业务服务层
    │   └── FiscoContractService.java # 区块链服务
    ├── utils/               # 工具类
    └── vo/                  # 视图对象

🚀 快速开始

1. 环境准备

- JDK 15+
- Maven 3.6+
- MySQL 8.0+
- FISCO BCOS 节点（已部署）

2. 数据库初始化

执行 SQL 文件：

    mysql -u root -p < sql/copyright_management.sql

3. 配置修改

修改 src/main/resources/application.yml：

    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/copyright_management
        username: root
        password: your_password
    
    fisco:
      bcos:
        node-ip: 192.168.107.131  # 你的节点IP
        node-port: 20200
        group-id: 1
    
    contract:
      rbac-address: 0x93598b9ec73b3cb9521baafa55a8eb31c05a2cdd
      copyright-address: 0x4dbbefbcb00626544edcf6dfeaf4ae75b7d0a580

4. 启动项目

    mvn clean install
    mvn spring-boot:run

或直接运行主类：

    CopyrightManagementApplication.java

5. 测试接口

    # 健康检查
    curl http://localhost:8080/api/health
    
    # API测试
    curl http://localhost:8080/api/test

📡 API 接口

用户管理

  方法  	路径                 	说明  
  POST	/api/users/register	用户注册
  GET 	/api/users/{id}    	查询用户
  GET 	/api/users/list    	用户列表

版权管理

  方法  	路径                             	说明    
  POST	/api/copyrights/register       	注册版权  
  GET 	/api/copyrights/{id}           	查询版权  
  GET 	/api/copyrights/owner/{address}	按所有者查询
  GET 	/api/copyrights/search         	搜索版权  

授权管理

  方法  	路径                         	说明  
  POST	/api/licenses/apply        	申请授权
  PUT 	/api/licenses/{id}/approve 	批准授权
  PUT 	/api/licenses/{id}/reject  	拒绝授权
  PUT 	/api/licenses/{id}/activate	激活授权
  PUT 	/api/licenses/{id}/revoke  	撤销授权

版税管理

  方法  	路径                           	说明    
  POST	/api/royalties/record        	记录版税  
  GET 	/api/royalties/copyright/{id}	查询版税记录

📝 请求示例

用户注册

    POST /api/users/register
    {
      "address": "0x1234567890abcdef1234567890abcdef12345678",
      "username": "alice",
      "email": "alice@example.com"
    }

版权注册

    POST /api/copyrights/register
    {
      "owner": "0x1234567890abcdef1234567890abcdef12345678",
      "title": "My Copyright",
      "author": "Alice",
      "fileHash": "QmXxx... (IPFS hash)"
    }

申请授权

    POST /api/licenses/apply
    {
      "copyrightId": "1",
      "licensee": "0x9876543210987654321098765432109876543210",
      "startTime": 1704067200,
      "endTime": 1735689600,
      "isExclusive": false
    }

🔧 FISCO BCOS 合约集成

项目已配置以下合约地址：

- RBAC 合约: 0x93598b9ec73b3cb9521baafa55a8eb31c05a2cdd
- 版权合约: 0x4dbbefbcb00626544edcf6dfeaf4ae75b7d0a580

合约方法映射

  业务操作	合约方法                                    
  用户注册	registerUser(address, string)           
  版权注册	registerCopyright(address, string, string, string)
  申请授权	applyForLicense(uint256, address, uint256, uint256, bool)
  批准授权	approveLicense(uint256)                 
  激活授权	activateLicense(uint256)                
  撤销授权	revokeLicense(uint256)                  
  记录版税	recordRoyaltyPayment(uint256, address, address, uint256, string)

⚠️ 注意事项

1. 合约集成: 当前代码为模拟实现，实际使用时需：
   - 使用 sol2java 工具生成合约Java类
   - 取消注释 FiscoContractService.java 中的合约调用代码
2. 证书配置: 如需使用证书认证：
   - 将证书文件放到 src/main/resources/cert/ 目录
   - 配置 application.yml 中的证书路径
3. IPFS集成: IPFS配置为可选，主要用于文件存储

🐛 常见问题

Q: 编译错误 - Cannot resolve symbol

A: 执行 mvn clean install 重新下载依赖

Q: 数据库连接失败

A: 检查 MySQL 是否启动，用户名密码是否正确

Q: FISCO BCOS 连接失败

A: 确认节点IP、端口配置正确，节点是否正常运行

Q: 合约调用失败

A: 确认合约地址正确，合约已部署到指定群组

📄 许可证

MIT License

👥 联系方式

- 项目名称：Copyright Management Platform
- 技术支持：FISCO BCOS
