package com.xvjun.bigdata.hos.common.Utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.InputStream;
import java.util.Properties;

public class HbaseUtil {

    private static Configuration configuration = null;
    private static Connection connection = null;

    private static HbaseUtil instance = null;
    public static synchronized HbaseUtil getInstance(){
        if(instance == null){
            instance = new HbaseUtil();
        }
        return instance;
    }


    public Connection getConnection(){
        try {
            if(configuration == null){
                Properties prop = new Properties();
                InputStream file = HbaseUtil.class.getClassLoader().getResourceAsStream("hbase_login_initial.properties");
                prop.load(file);
                String ZK_CONNECT_KEY = prop.getProperty("ZK_CONNECT_KEY");
                String ZK_CONNECT_VALUE = prop.getProperty("ZK_CONNECT_VALUE");
                configuration = HBaseConfiguration.create();
                configuration.set(ZK_CONNECT_KEY,ZK_CONNECT_VALUE);
            }
            if(connection == null){
                connection = ConnectionFactory.createConnection(configuration);
            } else return connection;
        } catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }

}
