package com.xvjun.bigdata.hos.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

/**
 * 数据库配置类
 */

@Configuration
@MapperScan(basePackages = HosDataSourceConfig.PACKAGE,
    sqlSessionFactoryRef = "HosSqlSessionFactory")

public class HosDataSourceConfig {

    static final String PACKAGE = "com.xvjun.bigdata.hos.**";

    /**
     * get datasource
     * @return
     * @throws IOException
     */
    @Bean(name = "HosDataSource")
    @Primary
    public DataSource hosDataSource() throws IOException {
        //TODO... 1.获得DataSource相关配置信息
        ResourceLoader loader = new DefaultResourceLoader();
        InputStream inputStream = loader.getResource("classpath:application.properties").getInputStream();
        Properties properties = new Properties();
        properties.load(inputStream);
        Set<Object> keys = properties.keySet();
        Properties dsproperties = new Properties();
        for(Object key : keys){
            if(key.toString().startsWith("datasource")){
                dsproperties.put(key.toString().replace("datasource.",""),properties.get(key));
            }

        }
        //TODO... 2.通过HikariDataSourcefactory 生成datasource
        HikariDataSourceFactory factory = new HikariDataSourceFactory();
        factory.setProperties(dsproperties);
        inputStream.close();
        return factory.getDataSource();

    }

    @Bean(name = "HosSqlSessionFactory")
    @Primary
    public SqlSessionFactory hosSqlSessionFactory(@Qualifier("HosDataSource") DataSource hosDataSource)
            throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(hosDataSource);
        //TODO... 1.读取mybatis的相关配置
        ResourceLoader loader = new DefaultResourceLoader();
        sqlSessionFactoryBean.setConfigLocation(loader.getResource("classpath:mybatis-config.xml"));

        //TODO... 2.获取sqlsessionfactory实例
        sqlSessionFactoryBean.setSqlSessionFactoryBuilder(new SqlSessionFactoryBuilder());
        return sqlSessionFactoryBean.getObject();

    }

}
