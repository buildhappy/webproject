domain:负责定义一些基本的java bean
dao：持久层，负责domain对象的数据库操作。
service：业务层，负责将UserDao和LoginLogDao组织起来完成密码认证，日志记录等操作。