# 管理平台 - 用户中心

## 前言

管理平台用户中心系统的代码开发。



## 需求

1. 登录 / 注册
2. 用户管理
3. 用户校验



## 技术选型

前端：

- 三件套 + React + 组件库 Ant Design + Umi + Ant Design Pro

后端：

- Java
- Spring 
- SpringMVC 
- Spring Boot
- MyBatis
- MyBatis-Plus
- Junit
- MySQL

部署： 服务器 / 容器



## 快速使用

### Windows 版本

Windows 主机本地快速启动方式：

```
1、下载后端代码
2、在 MySQL 数据库中执行 sql 文件目录下对应初始化语句
3、编辑配置文件 application.yml 进行配置项参数更改
4、IDE 工具执行启动
5、按照前端执行说明进行初始化和启动
```

### Linux 服务器

需要提前准备 JDK、Maven 环境（如果已有对应环境则跳过）：

1、安装 JDK

```shell
$ yum install -y java-1.8.0-openjdk*
```

2、安装 Maven

```shell
$ curl -o apache-maven-3.8.5-bin.tar.gz https://dlcdn.apache.org/maven/maven-3/3.8.5/binaries/apache-maven-3.8.5-bin.tar.gz
$ tar -zxvf apache-maven-3.8.5-bin.tar.gz
```

3、安装 MySQL 数据库并且执行初始化语句

```
sql/platform_db_init.sql
sql/platform_user.sql
```

4、安装 git

```shell
$ yum install -y git
```

6、下载代码

```shell
$ git clone https://github.com/pitt1997/platform-user-backend.git
```

7、进入目录执行 maven 构建

```shell
$ mvn package -DskipTests
```

8、执行 jar 包启动项目

```shell
$ chmod a+x ./target/platform-user-backend-0.0.1-SNAPSHOT.jar
$ java -jar ./target/platform-user-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

9、nohup 方式启动

```shell
$ nohup java -jar ./target/platform-user-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod &
```

### Docker 容器

docker build

```shell
$ sudo docker build -t platform-user-backend:v0.0.1 .
```

docker run

```shell
$ docker run -p 8080:8080 platform-user-backend:v0.0.1
```

