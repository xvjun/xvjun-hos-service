package com.xvjun.bigdata.hos.core.authmgr.dao;


import com.xvjun.bigdata.hos.core.authmgr.model.ServiceAuth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;

@Mapper
public interface ServiceAuthMapper {

    public int addAuth(@Param("auth")ServiceAuth auth);

    public int deleteAuth(@Param("bucket") String bucketName,@Param("token") String token);

    public int deleteAuthByToken(@Param("token") String token);

    public int deleteAuthByBucket(@Param("bucket") String bucketName);

    @ResultMap("ServiceAuthResultMap")
    public ServiceAuth getAuth(@Param("bucket") String bucketName,@Param("token") String token);


}
