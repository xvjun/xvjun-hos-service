package com.xvjun.bigdata.hos.common.ObjectUtil;

import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;

/**
 * 返回给用户的信息
 */
public class HosObject {

    private  ObjectMetaData metaData; //文件基本信息
    private InputStream content; //文件内容
    private Response response; //返回头

    public HosObject(){}

    public void close(){
        try{
            if(content != null){this.content.close();}
            else if(response != null){this.response.close();}
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public HosObject(Response response) {
        this.response = response;
    }

    public ObjectMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(ObjectMetaData metaData) {
        this.metaData = metaData;
    }

    public InputStream getContent() {
        return content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
