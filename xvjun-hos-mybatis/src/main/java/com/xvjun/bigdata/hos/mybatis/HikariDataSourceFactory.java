package com.xvjun.bigdata.hos.mybatis;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

/**
 * 数据库连接池
 */

public class HikariDataSourceFactory extends UnpooledDataSourceFactory {

    public HikariDataSourceFactory(){
        this.dataSource = new HikariDataSource();
    }

}
