package com.xvjun.bigdata.hos.common.ObjectUtil;

import java.util.Map;

/**
 * 文件源信息
 */
public class ObjectMetaData {

    private String bucket;  //所在的bucket
    private String key;  //文件路径
    private String mediaType;  //文件类型
    private long length;  //文件大小
    private long lastModiafyTime;  //最后修改时间
    private Map<String,String> attrs;  //文件的相关属性

    public String getContentEcoding(){
        return attrs != null ? attrs.get("content-encoding") : null;
    }

    @Override
    public String toString() {
        return "ObjectMetaData{" +
                "bucket='" + bucket + '\'' +
                ", key='" + key + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", length=" + length +
                ", lastModiafyTime=" + lastModiafyTime +
                ", attrs=" + attrs +
                '}';
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getLastModiafyTime() {
        return lastModiafyTime;
    }

    public void setLastModiafyTime(long lastModiafyTime) {
        this.lastModiafyTime = lastModiafyTime;
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, String> attrs) {
        this.attrs = attrs;
    }
}
