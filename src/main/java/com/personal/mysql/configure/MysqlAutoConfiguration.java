/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.mysql.configure;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.personal.mysql.aop.DataSourceAop;
import com.personal.mysql.dynamic.DataSourceBean;
import com.personal.mysql.dynamic.DynamicDataSource;
import com.personal.mysql.dynamic.DynamicDataSourceContextHolder;
import com.personal.mysql.properties.MysqlProperties;
import com.personal.mysql.utils.SpringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sunpeikai
 * @version MysqlAutoConfiguration, v0.1 2020/10/12 16:51
 * @description
 */
@AutoConfigureBefore({DataSourceAutoConfiguration.class})
@Configuration
@Import({SpringUtils.class, DataSourceAop.class})
@EnableConfigurationProperties({MysqlProperties.class})
@ConditionalOnProperty(name = {"mysql.enable"}, havingValue = "true")
public class MysqlAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(MysqlAutoConfiguration.class);

    private final MysqlProperties mysqlProperties;

    public MysqlAutoConfiguration(MysqlProperties mysqlProperties) {
        this.mysqlProperties = mysqlProperties;
        DynamicDataSourceContextHolder.elector = mysqlProperties.getElector();
    }

    /**
     * 写库数据源
     * */
    @Bean("writeDataSources")
    @ConditionalOnMissingBean(name = "writeDataSources")
    public DataSourceBean writeDataSources(){
        DataSourceBean dataSourceBean = new DataSourceBean();
        mysqlProperties.getWrite().forEach(write -> dataSourceBean.addDataSource(write.isMaster(), DataSourceBean.DataBaseType.WRITE, DataSourceBuilder.create()
                .type(mysqlProperties.getType())
                .url(write.getUrl())
                .username(write.getUsername())
                .password(write.getPassword())
                .driverClassName(write.getDriverClassName())
                .build()));
        return dataSourceBean;
    }

    /**
     * 读库数据源
     * */
    @Bean("readDataSources")
    @ConditionalOnMissingBean(name = "readDataSources")
    public DataSourceBean readDataSources(){
        DataSourceBean dataSourceBean = new DataSourceBean();
        mysqlProperties.getRead().forEach(read -> dataSourceBean.addDataSource(false, DataSourceBean.DataBaseType.READ, DataSourceBuilder.create()
                .type(mysqlProperties.getType())
                .url(read.getUrl())
                .username(read.getUsername())
                .password(read.getPassword())
                .driverClassName(read.getDriverClassName())
                .build()));
        return dataSourceBean;
    }

    /**
     * 动态数据源
     * */
    @Bean
    @ConditionalOnMissingBean
    public DynamicDataSource dynamicDataSource(DataSourceBean writeDataSources, DataSourceBean readDataSources){
        Map<Object, Object> targetDataSources = new HashMap<>();
        // 所有数据源放入map
        writeDataSources.getDataSources().forEach(dataSource -> {
            targetDataSources.put(dataSource.getName(), dataSource.getDataSource());
            DynamicDataSourceContextHolder.dataSourceKeys.add(dataSource.getName());
            DynamicDataSourceContextHolder.writeDataSourceKeys.add(dataSource.getName());
        });
        readDataSources.getDataSources().forEach(dataSource -> {
            targetDataSources.put(dataSource.getName(), dataSource.getDataSource());
            DynamicDataSourceContextHolder.dataSourceKeys.add(dataSource.getName());
            DynamicDataSourceContextHolder.readDataSourceKeys.add(dataSource.getName());
        });

        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        // 默认DataSource,选举出master库的任一数据源
        dynamicDataSource.setDefaultTargetDataSource(writeDataSources.getMasterDataSource());
        log.info("mysql init ok");
        return dynamicDataSource;
    }

    /**
     * 创建SqlSessionFactory
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DynamicDataSource dynamicDataSource, MybatisPlusInterceptor mybatisPlusInterceptor) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setPlugins(mybatisPlusInterceptor);
        factoryBean.setDataSource(dynamicDataSource);
        // 下边两句仅仅用于*.xml文件，如果整个持久层操作不需要使用到xml文件的话（只用注解就可以搞定），则不加
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mysqlProperties.getMapperLocations()));
        return factoryBean.getObject();
    }

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 开启动态数据源切换切面
     * */
    @Bean
    public AspectJExpressionPointcutAdvisor dataSourceAop() {
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression(mysqlProperties.getAopExpression());
        advisor.setAdvice(new DataSourceAop());
        advisor.setOrder(1);
        return advisor;
    }
}
