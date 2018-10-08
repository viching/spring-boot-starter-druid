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