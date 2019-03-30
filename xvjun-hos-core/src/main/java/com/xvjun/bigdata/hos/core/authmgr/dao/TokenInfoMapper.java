package com.xvjun.bigdata.hos.core.authmgr.dao;


import com.xvjun.bigdata.hos.core.authmgr.model.TokenInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;

import java.util.Date;
import java.util.List;

@Mapper
public interface TokenInfoMapper {

    public int addToken(@Param("token")TokenInfo tokenInfo);

    public int deleteToken(@Param("token") String token);

    public int updateToken(@Param("token") String token,@Param("expireTime") int expireTime,@Param("isActive") int isActive);

    public int refreshToken(@Param("token") String token,@Param("refreshTime") Date refreshTime);

    @ResultMap("TokenInfoResultMap")
    public TokenInfo getTokenInfo(@Param("token") String token);

    @ResultMap("TokenInfoResultMap")
    public List<TokenInfo> getTokenInfoList(@Param("creator") String creator);
}
