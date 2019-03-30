package com.xvjun.bigdata.hos.web;


import com.xvjun.bigdata.hos.mybatis.HosDataSourceConfig;
import com.xvjun.bigdata.hos.web.security.SecurityIntercepter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.*;

@EnableWebMvc
@SuppressWarnings("deprecation")
@EnableAutoConfiguration(exclude = MongoAutoConfiguration.class)
@Configuration
@ComponentScan({"com.xvjun.bigdata.*"})
@SpringBootApplication
@Import({HosDataSourceConfig.class,HosServerBeanConfiguration.class})
@MapperScan("com.xvjun.bigdata")
public class HosServerApp {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private SecurityIntercepter securityIntercepter;

    public static void main(String[] args){
        SpringApplication.run(HosServerApp.class);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurerAdapter() {

            /**
             * 设置跨域请求
             * @param registry
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") .allowedHeaders("*")
                        .allowedMethods("POST","GET")
                        .allowedOrigins("*");
            }

            /**
             * 将自定义的拦截器添加进去
             * @param registry
             */
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(securityIntercepter);
            }
        };
    }

}
