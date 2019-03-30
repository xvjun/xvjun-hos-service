package com.xvjun.bigdata.hos.web.security;

import com.xvjun.bigdata.hos.core.usermgr.model.SystemRole;
import com.xvjun.bigdata.hos.core.usermgr.model.UserInfo;

/**
 * 权限验证
 */
public interface IOperationAccessControl {
    /**
     * 登录验证，判断账号密码
     * @param userName
     * @param password
     * @return
     */
    public UserInfo checkLogin(String userName,String password);

    /**
     * 判断systemRole1的权限是否大于systemRole2
     * @param systemRole1
     * @param systemRole2
     * @return
     */
    public boolean checkSystemRole(SystemRole systemRole1,SystemRole systemRole2);

    /**
     * 判断systemRole1对用户是否有操作权限
     * @param systemRole1
     * @param userId
     * @return
     */
    public boolean checkSystemRole(SystemRole systemRole1,String userId);


    public boolean checkTokenOwner(String userName,String token);

    public boolean checkBucketOwner(String userName,String bucketName);

    /**
     * 判断token对bucket是否有操作权限
     * @param token
     * @param bucket
     * @return
     */
    public boolean checkPermission(String token,String bucket);

}
