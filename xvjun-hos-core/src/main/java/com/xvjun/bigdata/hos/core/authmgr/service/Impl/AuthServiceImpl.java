package com.xvjun.bigdata.hos.core.authmgr.service.Impl;

import com.xvjun.bigdata.hos.core.authmgr.dao.ServiceAuthMapper;
import com.xvjun.bigdata.hos.core.authmgr.dao.TokenInfoMapper;
import com.xvjun.bigdata.hos.core.authmgr.model.ServiceAuth;
import com.xvjun.bigdata.hos.core.authmgr.model.TokenInfo;
import com.xvjun.bigdata.hos.core.authmgr.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Transactional
@Service("authServiceImpl")
public class AuthServiceImpl implements IAuthService {

    @Autowired
    TokenInfoMapper tokenInfoMapper;

    @Autowired
    ServiceAuthMapper serviceAuthMapper;

    @Override
    public boolean addAuth(ServiceAuth auth) {
        int judge = serviceAuthMapper.addAuth(auth);
        return judge > 0 ? true : false;
    }

    @Override
    public boolean deleteAuthByBucket(String bucketName) {
        int judge = serviceAuthMapper.deleteAuthByBucket(bucketName);
        return judge > 0 ? true : false;
    }

    @Override
    public boolean deleteAuth(String bucketName, String token) {
        int judge = serviceAuthMapper.deleteAuth(bucketName,token);
        return judge > 0 ? true : false;
    }

    @Override
    public boolean deleteAuthByToken(String token) {
        int judge = serviceAuthMapper.deleteAuthByToken(token);
        return judge > 0 ? true : false;
    }

    @Override
    public ServiceAuth getServiceAuth(String bucketName, String token) {
        return serviceAuthMapper.getAuth(bucketName,token);
    }

    @Override
    public boolean addToken(TokenInfo tokenInfo) {
        int judge = tokenInfoMapper.addToken(tokenInfo);
        return judge > 0 ? true : false;
    }

    @Override
    public boolean updateToken(String token, int expireTime, boolean isActive) {
        int judge = tokenInfoMapper.updateToken(token,expireTime,isActive ? 1:0);
        return judge > 0 ? true : false;
    }

    @Override
    public boolean refreshToken(String token) {
        int judge = tokenInfoMapper.refreshToken(token,new Date());
        return judge > 0 ? true : false;
    }

    @Override
    public boolean deleteToken(String token) {
        int judge = tokenInfoMapper.deleteToken(token);
        //TODO... delete auth
        int judge2 = serviceAuthMapper.deleteAuthByToken(token);
        return judge > 0 && judge2 > 0 ? true : false;
    }

    @Override
    public boolean checkToken(String token) {
        TokenInfo tokenInfo = tokenInfoMapper.getTokenInfo(token);
        if(tokenInfo == null){
            return false;
        }else if(!tokenInfo.isActive()){
            return false;
        }
        Date newDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(tokenInfo.getRefreshTime());
        cal.add(Calendar.DATE,tokenInfo.getExpireTime());
        return newDate.before(cal.getTime());
    }

    @Override
    public TokenInfo getTokenInfo(String token) {
        return tokenInfoMapper.getTokenInfo(token);
    }

    @Override
    public List<TokenInfo> getTokenInfos(String creator) {
        return tokenInfoMapper.getTokenInfoList(creator);
    }
}
