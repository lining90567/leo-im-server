# ![Leo-IM](https://raw.githubusercontent.com/wiki/lining90567/leo-im-server/leo-im.png)

Leo-IM，开源好用的IM。

Leo-IM是基于Java语言、Netty框架、Vue+Element-UI开发的轻量级IM，服务端可独立运行（无需部署到web容器），HTTP服务和Socket服务可分开部署，使用基于Netty扩展的[netty-rest-server](https://github.com/lining90567/netty-rest-server)RESTful框架提供Web服务，简单易用，方便扩展。

## 在线演示

演示地址：<a href="http://123.207.147.138:8000" target="_blank">http://123.207.147.138:8000</a>

建议使用Chrome浏览器

- 演示用户1：用户名 test1，口令 123456
- 演示用户2：用户名 test2，口令 123456
- 演示用户3：用户名 test3，口令 123456

## 运行环境要求

- 服务端：Java8、MySQL5.5+
- 客户端：Chrome、IE10+

## 主要功能

- 私聊
- 群聊
- 文字、表情、图片、文件

## 构建与部署

- 安装netty-rest-server到本地仓库

	mvn install:install-file -Dfile=netty-rest-server-1.0.jar -DgroupId=org.leo -DartifactId=netty-rest-server -Dversion=1.0 -Dpackaging=jar

- 创建数据库，并设置字符集（my.cnf或my.ini）

	[client]
	default-character-set=utf8mb4

	[mysqld]
	character-set-client-handshake = FALSE

	character-set-server = utf8mb4

	collation-server = utf8mb4_unicode_ci

	init_connect=’SET NAMES utf8mb4'

	[mysql]
	default-character-set=utf8mb4

- 构建

	mvn package

- 部署

	解压leo-im-1.0.zip，修改conf/app.conf的相关配置

- 启动

	nohup bin/run.sh >/dev/null 2>&1 &
	
## 联系方式
- **邮箱** - lining90567@sina.com
- **QQ** - 328616209