spring-boot-starter-druid

配置示例
```
#DRUID
spring:  
  druid:
    driver-class: com.mysql.cj.jdbc.Driver
    list: 
      - url: jdbc:mysql://localhost:3306/sys?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf8&useSSL=false
        username: root
        password: root
      - url: jdbc:mysql://localhost:3306/sys?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf8&useSSL=false
        username: root
        password: root
    initial-size: 1
    min-idle: 1
    max-active: 20
    test-on-borrow: true
    max-wait: 60000
    time-between-eviction-runs-millis: 60000
    min-evictable-idle-time-millis: 300000
    validation-query: SELECT 1 FROM DUAL
    test-While-Idle: true
    test-on-return: false
    pool-prepared-statements: false
    max-pool-prepared-statement-per-connection-size: 20
    #filters: stat,wall,log4j,config
    connection-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000;config.decrypt=true    
    monitor:
          enabled: enabled # 配置此属性Monitor才生效
          druid-stat-view: /druid/*
          druid-web-stat-filter: /*
          allow: 127.0.0.1
          #deny: 
          login-username: admin
          login-password: 123456
          exclusions: '*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*'
          reset-enable: false
```

查看[Druid配置](https://github.com/alibaba/druid/wiki/DruidDataSource配置属性列表)

`druid.monitor.enabled=enabled` 时Monitor才生效

本例开发支持一主多从配置，通过切面实现读写分离，基于mybatis3.0的annotation化mapper，以下是测试环境的mybatis配置，供参考：

@Configuration
@EnableAutoConfiguration
@AutoConfigureAfter({DruidAutoConfiguration.class})
@ConditionalOnBean(DataSource.class)
@MapperScan(basePackages = "com.viching.test.mapper")
@EnableTransactionManagement

public class MybatisConfig implements TransactionManagementConfigurer {
	
    @Resource(name="dynamicDataSource")
    private DataSource dataSource;

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "sqlSessionFactory")
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactoryBean() {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setVfs(SpringBootVFS.class);
        
        Properties sqlSessionFactoryProperties = new Properties();
        sqlSessionFactoryProperties.setProperty("dialect", "mysql");
        bean.setConfigurationProperties(sqlSessionFactoryProperties);
        
        try {
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}


mysql5.7 安装教程

1.下载源码
[python] view plain copy 
wget http://cdn.mysql.com/Downloads/MySQL-5.7/mysql-5.7.10.tar.gz  

2.解压
[python] view plain copy 
tar zxvf mysql-5.7.10.tar.gz    

3.安装必要的包
[python] view plain copy 
sudo yum install cmake gcc-c++ ncurses-devel perl-Data-Dumper libicu-devel libquadmath-devel python-devel bzip2-devel

sudo install -y unzip zip

4.安装Boost
[python] view plain copy 
wget http://120.52.72.42/netix.dl.sourceforge.net/c3pr90ntcsf0/project/boost/boost/1.60.0/boost_1_60_0.zip  
unzip boost_1_59_0.zip
cd boost_1_59_0. 
./bootstrap.sh   
./b2
sudo ./b2 install  

5.生成makefile
[python] view plain copy 
cmake . -DWITH_SYSTEMD=1  
加上-DWITH_SYSTEMD=1可以使用systemd控制mysql服务，默认是不开启systemd的。
/usr/bin/cmake . -DCMAKE_INSTALL_PREFIX=/opt/mysql -DMYSQL_DATADIR=/opt/mysql/data -DSYSCONFDIR=/opt/mysql -DMYSQL_UNIX_ADDR=/opt/mysql/mysql.sock -DDEFAULT_CHARSET=utf8 -DDEFAULT_COLLATION=utf8_general_ci -DWITH_EXTRA_CHARSETS:STRING=utf8,gbk -DWITH_MYISAM_STORAGE_ENGINE=1 -DWITH_INNOBASE_STORAGE_ENGINE=1 -DWITH_READLINE=1 -DENABLED_LOCAL_INFILE=1 -DDOWNLOAD_BOOST=1 -DWITH_BOOST=/usr/local/include/

-DWITH_BOOST=boost的父目录
6.编译
[python] view plain copy 
make  

7.安装
[python] view plain copy 
sudo make install   

mysql将会安装到/usr/local/mysql路径。

8.添加mysql用户和组
[python] view plain copy 
sudo groupadd mysql    
sudo useradd -r -g mysql mysql 

9.修改目录和文件权限，安装默认数据库
[python] view plain copy 
cd /opt/mysql    
sudo chown -R mysql .
sudo chgrp -R mysql .
sudo bin/mysqld --initialize-insecure --user=mysql

mkdir logs
chown mysql:mysql logs

[python] view plain copy 
sudo bin/mysql_ssl_rsa_setup
sudo chown -R root .
sudo chown -R mysql data
至此，mysql就可以启动运行了。

10.启动mysql
CentOS7自带MariaDB的支持，/etc下默认存在my.cnf文件干扰mysql运行，需要先删掉
[python] view plain copy 
cd /etc    
sudo rm -fr my.cnf my.cnf.d

然后再/opt/mysql下重建my.cnf文件，内容如下
# For advice on how to change settings please see
# http://dev.mysql.com/doc/refman/5.7/en/server-configuration-defaults.html
# *** DO NOT EDIT THIS FILE. It's a template which will be copied to the
# *** default location during install, and will be replaced if you
# *** upgrade to a newer version of MySQL.

[mysqld]

lower_case_table_names = 1

# Remove leading # and set to the amount of RAM for the most important data
# cache in MySQL. Start at 70% of total RAM for dedicated server, else 10%.
# innodb_buffer_pool_size = 128M

# Remove leading # to turn on a very important data integrity option: logging
# changes to the binary log between backups.
# log_bin

# These are commonly set, remove the # and set as required.
basedir = /opt/mysql
datadir = /opt/mysql/data
port = 3306
server_id = 1
socket = /opt/mysql/mysql.sock

# Remove leading # to set options mainly useful for reporting servers.
# The server defaults are faster for transactions and fast SELECTs.
# Adjust sizes as needed, experiment to find the optimal values.
# join_buffer_size = 128M
# sort_buffer_size = 2M
# read_rnd_buffer_size = 2M

sql_mode=NO_ENGINE_SUBSTITUTION

#binary log
log-bin = mysql-bin
binlog_format = mixed
#log_bin_trust_routine_creators = 1
log_bin_trust_function_creators = 1

#slow query log
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 3
log-queries-not-using-indexes
log-slow-admin-statements
 
现在可以使用systemd启动mysql了

,STRICT_TRANS_TABLES,NO_ZERO_DATE, NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER

[python] view plain copy 
/opt/mysql/bin/mysqld --initialize --user=mysql --basedir=/opt/mysql --datadir=/opt/mysql/data 

注：在最新的实验中，发现初始化完成后会给root分配一个密码(如：A temporary password is generated for root@localhost: CulLalicr3!i)，但是并未启动mysql.

杀掉进程后启动则不需要--initialize参数了
/opt/mysql/bin/mysqld --user=mysql --basedir=/opt/mysql --datadir=/opt/mysql/data

11.修改root密码
[python] view plain copy 
   
use mysql;    
UPDATE user SET authentication_string = PASSWORD('root') WHERE user = 'root';
GRANT ALL PRIVILEGES ON *.* TO root@'%' IDENTIFIED BY 'rootpasswd';    
FLUSH PRIVILEGES;
至此，安装基本完成了，一个mysql就能用了。


设置Linux mysql 允许远程连接 
/opt/mysql/bin/mysql -uroot -p'CulLalicr3!i' -S /opt/mysql/mysql.sock  (进入mysql)  

mysql> use mysql; 
ERROR 1820 (HY000): You must SET PASSWORD before executing this statement 
mysql> SET PASSWORD = PASSWORD('123456'); 
Query OK, 0 rows affected (0.03 sec) 

mysql> use mysql;  
mysql> SELECT `Host`,`User` FROM user;
mysql> UPDATE user SET `Host` = '%' WHERE `User` = 'root' LIMIT 1;
mysql> flush privileges;
注意在mysql 命令行形式下一定要输入";". 

+-----------+-----------+
| Host      | User      |
+-----------+-----------+
| %         | root      |
| localhost | mysql.sys |
| localhost | root      |
+-----------+-----------+
注意，这里可能会报错:重复主键，因为Host里面root用户对应了两行记录，分别是%和localhost,所以另起一个用户

mysql>GRANT ALL PRIVILEGES ON *.* TO admin@localhost IDENTIFIED BY 
'123456' WITH GRANT OPTION; flush privileges;
mysql>GRANT ALL PRIVILEGES ON *.* TO admin@"%" IDENTIFIED BY 
'123456' WITH GRANT OPTION;flush privileges;

第一句增加了一个admin用户授权通过本地机（localhost)访问，
密码“123456”。
第二句则是授与admin用户从任何其它主机发起的访问（通配符％）(经测试，第一条设置会导致第二条设置无效，故只设置第二条便可)。

mysql>delete from user where 'Host' = 'localhost' and 'User' = 'admin';commit;
mysql>flush privileges;
//删除用户的数据库
mysql>drop database adminDB;

REVOKE privilege ON mysql.user FROM 'admin'@'localhost';
DROP USER 'admin'@'localhost';
flush privileges;

虽然开启了外网访问权限，但是依旧无法登录3306端口，结果是因为阿里云控制台安全策略限制了。增加访问端口：tcp  3306/3306
