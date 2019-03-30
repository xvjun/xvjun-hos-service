package com.xvjun.bigdata.hos.common.ObjectUtil;

import java.util.List;

/**
 * 返回多个文件的信息
 */
public class ObjectListResult {

    private String bucket;
    private String maxkey; //起始键
    private String minkey; //结尾键
    private String nextMarker; //下一页的标记
    private int maxKeyNumber; //
    private int objectCount; //数量
    private String listId;
    private List<HosObjectSummary> hosObjectSummaryList;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getMaxkey() {
        return maxkey;
    }

    public void setMaxkey(String maxkey) {
        this.maxkey = maxkey;
    }

    public String getMinkey() {
        return minkey;
    }

    public void setMinkey(String minkey) {
        this.minkey = minkey;
    }

    public String getNextMarker() {
        return nextMarker;
    }

    public void setNextMarker(String nextMarker) {
        this.nextMarker = nextMarker;
    }

    public int getMaxKeyNumber() {
        return maxKeyNumber;
    }

    public void setMaxKeyNumber(int maxKeyNumber) {
        this.maxKeyNumber = maxKeyNumber;
    }

    public int getObjectCount() {
        return objectCount;
    }

    public void setObjectCount(int objectCount) {
        this.objectCount = objectCount;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public List<HosObjectSummary> getHosObjectSummaryList() {
        return hosObjectSummaryList;
    }

    public void setHosObjectSummaryList(List<HosObjectSummary> hosObjectSummaryList) {
        this.hosObjectSummaryList = hosObjectSummaryList;
    }
}
