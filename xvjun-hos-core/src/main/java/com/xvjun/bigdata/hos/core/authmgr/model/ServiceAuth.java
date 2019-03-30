package com.xvjun.bigdata.hos.core.authmgr.model;

import java.util.Date;

public class ServiceAuth {

    private String bucketName;
    private String targetToken;
    private Date authTime;

    public ServiceAuth(){}

    public ServiceAuth(String bucketName, String targetToken, Date authTime) {
        this.bucketName = bucketName;
        this.targetToken = targetToken;
        this.authTime = authTime;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getTargetToken() {
        return targetToken;
    }

    public void setTargetToken(String targetToken) {
        this.targetToken = targetToken;
    }

    public Date getAuthTime() {
        return authTime;
    }

    public void setAuthTime(Date authTime) {
        this.authTime = authTime;
    }

    @Override
    public String toString() {
        return "ServiceAuth{" +
                "bucketName='" + bucketName + '\'' +
                ", targetToken='" + targetToken + '\'' +
                ", authTime=" + authTime +
                '}';
    }
}
