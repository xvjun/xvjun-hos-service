package com.xvjun.bigdata.hos.server.service;

import com.xvjun.bigdata.hos.core.usermgr.model.UserInfo;
import com.xvjun.bigdata.hos.server.model.BucketModel;

import java.util.List;

public interface IBucketService {

    public boolean addBucket(UserInfo userInfo, String bucketName,String  detail);

    public boolean deleteBucket(String bucketName);

    public boolean updateBucket(String bucketName,String detail);

    public BucketModel getBucketById(String bucketId);

    public BucketModel getBucketByName(String bucketName);

    public List<BucketModel> getUserBuckets(String token);

}
