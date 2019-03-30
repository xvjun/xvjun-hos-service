package com.xvjun.bigdata.hos.mybatis.test;


import com.xvjun.bigdata.hos.mybatis.HosDataSourceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@Import(HosDataSourceConfig.class)
@PropertySource("classpath:application.properties")
@ComponentScan("com.xvjun.bigdata.hos.*")
@MapperScan(("com.xvjun.bigdata.hos.*"))
public class BaseTest {

    @Test
    public void test(){}

}
