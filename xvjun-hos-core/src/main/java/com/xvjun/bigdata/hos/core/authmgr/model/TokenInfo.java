package com.xvjun.bigdata.hos.core.authmgr.model;


import com.xvjun.bigdata.hos.common.Utils.CoreUtil;

import java.util.Date;

public class TokenInfo {

    private String token;
    private int expireTime;
    private Date refreshTime;
    private Date createTime;
    private boolean isActive;
    private String creator;

    public TokenInfo(){}

    public TokenInfo(String creator){
        this.token=CoreUtil.getUUID();
        this.expireTime = 7;
        this.creator=creator;
        Date now = new Date();
        this.refreshTime = now;
        this.createTime = now;
        this.isActive = true;
    }

    public TokenInfo(String token, int expireTime, Date refreshTime, Date createTime, boolean isActive, String creator) {
        this.token = token;
        this.expireTime = expireTime;
        this.refreshTime = refreshTime;
        this.createTime = createTime;
        this.isActive = isActive;
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "TokenInfo{" +
                "taken='" + token + '\'' +
                ", expireTime=" + expireTime +
                ", refreshTime=" + refreshTime +
                ", createTime=" + createTime +
                ", isActive=" + isActive +
                ", creator='" + creator + '\'' +
                '}';
    }

    public String getToken() {
        return token;
    }

    public void setToken(String taken) {
        this.token = taken;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public Date getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(Date refreshTime) {
        this.refreshTime = refreshTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
