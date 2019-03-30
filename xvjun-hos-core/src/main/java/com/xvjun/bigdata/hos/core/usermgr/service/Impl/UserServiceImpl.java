package com.xvjun.bigdata.hos.core.usermgr.service.Impl;

import com.google.common.base.Strings;

import com.xvjun.bigdata.hos.common.Utils.CoreUtil;
import com.xvjun.bigdata.hos.core.authmgr.dao.TokenInfoMapper;
import com.xvjun.bigdata.hos.core.authmgr.model.TokenInfo;
import com.xvjun.bigdata.hos.core.usermgr.dao.UserInfoMapper;
import com.xvjun.bigdata.hos.core.usermgr.model.UserInfo;
import com.xvjun.bigdata.hos.core.usermgr.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Transactional
@Service("userServiceImpl")
public class UserServiceImpl implements IUserService {

    /**
     * set expiraTime is better
     */
    private long LONG_REFRESH_TIME = 4670409600000L;
    private int LONG_EXPIRE_TIME = 36500;

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    TokenInfoMapper tokenInfoMapper;

    @Override
    public boolean addUser(UserInfo userInfo) {
        int judge = userInfoMapper.addUser(userInfo);
        //TODO... add token
        Date date = new Date();
        TokenInfo tokenInfo = new TokenInfo(userInfo.getUserId(),
                LONG_EXPIRE_TIME,date,date,true,
                CoreUtil.SYSTEM_USER);
        int judge2 = tokenInfoMapper.addToken(tokenInfo);

        return judge > 0 && judge2 > 0 ? true : false;
    }

    @Override
    public boolean updateUserInfo(String userId, String password, String detail) {
        int judge = userInfoMapper.updateUserInfo(userId,
                Strings.isNullOrEmpty(password) ? null : CoreUtil.getMd5Password(password),detail);

        return judge > 0? true : false;
    }

    @Override
    public boolean deleteUser(String userId) {
        int judge = userInfoMapper.deleteUser(userId);
        //TODO... delete token
        tokenInfoMapper.deleteToken(userId);

        return judge > 0? true : false;
    }

    @Override
    public UserInfo getUserInfo(String userId) {
        return userInfoMapper.getUserInfo(userId);
    }

    @Override
    public UserInfo getUserInfoByName(String userName) {
        return userInfoMapper.getUserInfoByName(userName);
    }

    @Override
    public UserInfo checkPassword(String userName, String password) {
        return userInfoMapper.checkPassword(userName,CoreUtil.getMd5Password(password));
    }
}
