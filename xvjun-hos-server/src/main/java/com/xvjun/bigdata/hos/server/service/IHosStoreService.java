package com.xvjun.bigdata.hos.server.service;

import com.xvjun.bigdata.hos.common.ObjectUtil.HosObject;
import com.xvjun.bigdata.hos.common.ObjectUtil.HosObjectSummary;
import com.xvjun.bigdata.hos.common.ObjectUtil.ObjectListResult;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public interface IHosStoreService {

    public void createSqlTable() throws IOException;

    public void put(String bucket, String key, ByteBuffer content, long length, String mediaType,
                    Map<String,String> properties) throws Exception;

    public HosObjectSummary getSummary(String bucket,String key) throws IOException;

    public List<HosObjectSummary> list(String bucket,String startkey,String endkey) throws IOException;

    public ObjectListResult listDir(String bucket,String dir,String start,int maxCount) throws IOException;

    public ObjectListResult listByPrefix(String bucket,String dir,String keyPrefix,String start,int maxCount) throws IOException;

    public HosObject getObject(String bucket,String key) throws IOException;

    public void deleteObject(String bucket,String key) throws Exception;

    public void deleteBucketStore(String bucket) throws IOException;

    public void createBucketStore(String bucket) throws IOException;

}
