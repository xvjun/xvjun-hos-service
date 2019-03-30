package com.xvjun.bigdata.hos.sdk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HosClientFactory {

    private static Map<String,HosClient> clientCache = new ConcurrentHashMap<>();

    public static HosClient getOrClient(String endpoints,String token){
        String key = endpoints+"_"+token;
        if(clientCache.containsKey(key)){
            return clientCache.get(key);
        } else{
            HosClient client = new HosClientImpl();
        }
    }

}
