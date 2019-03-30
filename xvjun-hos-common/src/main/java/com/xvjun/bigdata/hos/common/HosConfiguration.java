package com.xvjun.bigdata.hos.common;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HosConfiguration {

    private static HosConfiguration configuration;
    private static Properties properties;

    static {
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        configuration = new HosConfiguration();
        try{
            configuration.properties = new Properties();
            Resource[] resources = resourcePatternResolver.getResources("classpath:*.properties");
            for(Resource resource : resources){
                Properties properties1 = new Properties();
                InputStream inputStream = resource.getInputStream();
                properties1.load(inputStream);
                inputStream.close();
                configuration.properties.putAll(properties1);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private HosConfiguration(){}

    public static HosConfiguration getConfiguration(){
        return configuration;
    }

    public String getString(String key){
        return properties.get(key).toString();
    }

    public int getInt(String key){
        return Integer.parseInt(properties.getProperty(key));
    }

    public Boolean getBoolean(String key){
        return Boolean.valueOf(properties.getProperty(key));
    }

    public Long getLong(String key){
        return Long.parseLong(properties.getProperty(key));
    }

}
