package com.xvjun.bigdata.hos.server.dao;


import com.xvjun.bigdata.hos.server.model.BucketModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;

import java.util.List;

@Mapper
public interface BucketMapper {

    public int addBucket(@Param("bucket") BucketModel bucketModel);

    public int updateBucket(@Param("bucketName") String bucketName,@Param("detail") String detail);

    public int deleteBucket(@Param("bucketName") String bucketName);

    @ResultMap("BucketResultMap")
    public BucketModel getBucket(@Param("bicketId") String bucketId);

    @ResultMap("BucketResultMap")
    public BucketModel getBucketByName(@Param("bucketName") String bucketName);

    @ResultMap("BucketResultMap")
    public List<BucketModel> getBucketByCreator(@Param("creator") String creator);

    @ResultMap("BucketResultMap")
    public  List<BucketModel> getUserAuthorizedBuckets(@Param("token") String token);

}
