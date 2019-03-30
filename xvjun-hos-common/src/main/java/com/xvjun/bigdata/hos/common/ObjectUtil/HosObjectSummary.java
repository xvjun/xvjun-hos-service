package com.xvjun.bigdata.hos.common.ObjectUtil;

import java.io.Serializable;
import java.util.Map;

/**
 * 面向用户的文件信息
 */
public class HosObjectSummary implements Comparable<HosObjectSummary>,Serializable {

    private String id;  //
    private String key;  //文件路径
    private String name; //文件名
    private long length; //文件大小
    private String mediaType; //文件类型
    private long lastModifyTime; //修改时间
    private String bucket; //
    private Map<String,String> attrs;
    //acl
    //properties

    public String getContentEncoding(){
        return attrs != null ? attrs.get("content-encoding") : null;
    }

    @Override
    public int compareTo(HosObjectSummary hosObjectSummary){
        return this.getKey().compareTo(hosObjectSummary.getKey());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, String> attrs) {
        this.attrs = attrs;
    }
}
