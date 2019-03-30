package com.xvjun.bigdata.hos.server.service.Impl;

import com.xvjun.bigdata.hos.core.authmgr.model.ServiceAuth;
import com.xvjun.bigdata.hos.core.authmgr.service.IAuthService;
import com.xvjun.bigdata.hos.core.usermgr.model.UserInfo;
import com.xvjun.bigdata.hos.server.dao.BucketMapper;
import com.xvjun.bigdata.hos.server.model.BucketModel;
import com.xvjun.bigdata.hos.server.service.IBucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service("bucketServiceImpl")
@Transactional
public class BucketServiceImpl implements IBucketService {

    @Autowired
    BucketMapper bucketMapper;

    @Autowired
    @Qualifier("authServiceImpl")
    IAuthService authService;

    @Override
    public boolean addBucket(UserInfo userInfo, String bucketName, String detail) {
        BucketModel bucketModel = new BucketModel(bucketName,userInfo.getUserName(),detail);
        int judge = bucketMapper.addBucket(bucketModel);
        //TODO... add user in auth
        ServiceAuth serviceAuth = new ServiceAuth(bucketName,userInfo.getUserId(),new Date());
        boolean judge2 = authService.addAuth(serviceAuth);


        return judge > 0 && judge2 ? true : false;
    }

    @Override
    public boolean deleteBucket(String bucketName) {
        int judge = bucketMapper.deleteBucket(bucketName);
        //TODO... delete auth
        boolean judge2 = authService.deleteAuthByBucket(bucketName);
        return judge > 0 && judge2 ? true : false;
    }

    @Override
    public boolean updateBucket(String bucketName, String detail) {
        int judge = bucketMapper.updateBucket(bucketName,detail);
        return judge > 0 ? true : false;
    }

    @Override
    public BucketModel getBucketById(String bucketId) {

        return bucketMapper.getBucket(bucketId);
    }

    @Override
    public BucketModel getBucketByName(String bucketName) {
        return bucketMapper.getBucketByName(bucketName);
    }

    @Override
    public List<BucketModel> getUserBuckets(String token) {
        return bucketMapper.getUserAuthorizedBuckets(token);
    }
}
