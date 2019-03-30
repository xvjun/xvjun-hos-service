package com.xvjun.bigdata.hos.web.rest;


import com.xvjun.bigdata.hos.common.ErrorCodes;
import com.xvjun.bigdata.hos.core.authmgr.model.ServiceAuth;
import com.xvjun.bigdata.hos.core.authmgr.model.TokenInfo;
import com.xvjun.bigdata.hos.core.authmgr.service.IAuthService;
import com.xvjun.bigdata.hos.core.usermgr.model.SystemRole;
import com.xvjun.bigdata.hos.core.usermgr.model.UserInfo;
import com.xvjun.bigdata.hos.core.usermgr.service.IUserService;
import com.xvjun.bigdata.hos.web.security.ContextUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("hos/v1/sys/")
public class ManageController extends BaseController {

    @Autowired
    @Qualifier("authServiceImpl")
    IAuthService authService;

    @Autowired
    @Qualifier("userServiceImpl")
    IUserService userService;

    /**
     * 创建user，需要判断权限
     * @param userName
     * @param password
     * @param detail
     * @param role
     * @return
     */
    @RequestMapping(value = "user",method = RequestMethod.POST)
    public  Object createUser(@RequestParam("userName") String userName,
                              @RequestParam("password") String password,
                              @RequestParam(name = "detail",required = false,defaultValue = "") String detail,
                              @RequestParam(name = "role",required = false,defaultValue = "USER") String role
                              ){
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if(operationAccessControl.checkSystemRole(currentUser.getSystemRole(),SystemRole.valueOf(role))){
            UserInfo userInfo = new UserInfo(userName,password,detail,SystemRole.valueOf(role));
            userService.addUser(userInfo);
            return getResult("success");
        }
        return getError(ErrorCodes.ERROR_PERMISSION_DENIED,"not admin");

    }

    /**
     * 删除用户，需要判断权限
     * @param userId
     * @return
     */
    @RequestMapping(value = "user",method = RequestMethod.DELETE)
    public Object deleteUser(@RequestParam("userId") String userId){
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if(operationAccessControl.checkSystemRole(currentUser.getSystemRole(),userId)){
            userService.deleteUser(userId);
            return getResult("success");
        }
        return getError(ErrorCodes.ERROR_PERMISSION_DENIED,"permission defind");
    }

    /**
     * 更新user信息，游客不允许
     * @param password
     * @param detail
     * @return
     */
    @RequestMapping(value = "user",method = RequestMethod.PUT)
    public Object updateUserInfo(@RequestParam(name = "password",required = false,defaultValue = "") String password,
                                 @RequestParam(name = "detail",required = false,defaultValue = "") String detail
                                 ){
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if(currentUser.getSystemRole().equals(SystemRole.VISITER)){
            return getError(ErrorCodes.ERROR_PERMISSION_DENIED,"permission defind");
        }
        userService.updateUserInfo(currentUser.getUserId(),password,detail);
        return getResult("success");
    }

    /**
     * 获得当前用户对象
     * @return
     */
    @RequestMapping(value = "user",method = RequestMethod.GET)
    public Object getUserInfo(){
        UserInfo currentUser = ContextUtil.getCurrentUser();
        return getResult(currentUser);
    }

    /**
     * 创建token，游客无法创建
     * @param expireTime
     * @param isActive
     * @return
     */
    @RequestMapping(value = "token",method = RequestMethod.POST)
    public Object createToken(@RequestParam(name = "expireTime",required = false,defaultValue = "7") String expireTime,
                              @RequestParam(name = "isActive",required = false,defaultValue = "true") String isActive
                              ){
        UserInfo currentUser  = ContextUtil.getCurrentUser();
        if(!currentUser.getSystemRole().equals(SystemRole.VISITER)){
            TokenInfo tokenInfo = new TokenInfo(currentUser.getUserName());
            tokenInfo.setExpireTime(Integer.parseInt(expireTime));
            tokenInfo.setActive(Boolean.parseBoolean(isActive));
            authService.addToken(tokenInfo);
            return getResult(tokenInfo);
        }
        return getError(ErrorCodes.ERROR_PERMISSION_DENIED,"not user");
    }

    /**
     * 删除token
     * @param token
     * @return
     */
    @RequestMapping(value = "token", method = RequestMethod.DELETE)
    public Object deleteToken(@RequestParam("token") String token) {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (operationAccessControl.checkTokenOwner(currentUser.getUserName(), token)) {
            authService.deleteToken(token);
            return getResult("success");
        }
        return getError(ErrorCodes.ERROR_PERMISSION_DENIED, "PERMISSION DENIED");
    }

    /**
     * 更新token
     * @param token
     * @param expireTime
     * @param isActive
     * @return
     */
    @RequestMapping(value = "token", method = RequestMethod.PUT)
    public Object updateTokenInfo(
            @RequestParam("token") String token,
            @RequestParam(name = "expireTime", required = false, defaultValue = "7") String expireTime,
            @RequestParam(name = "isActive", required = false, defaultValue = "true") String isActive) {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (operationAccessControl.checkTokenOwner(currentUser.getUserName(), token)) {
            authService.updateToken(token, Integer.parseInt(expireTime), Boolean.parseBoolean(isActive));
            return getResult("success");
        }

        return getError(ErrorCodes.ERROR_PERMISSION_DENIED, "PERMISSION DENIED");
    }

    /**
     * 获得token信息
     * @param token
     * @return
     */
    @RequestMapping(value = "token", method = RequestMethod.GET)
    public Object getTokenInfo(@RequestParam("token") String token) {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (operationAccessControl.checkTokenOwner(currentUser.getUserName(), token)) {
            TokenInfo tokenInfo = authService.getTokenInfo(token);
            return getResult(tokenInfo);
        }

        return getError(ErrorCodes.ERROR_PERMISSION_DENIED, "PERMISSION DENIED");

    }

    /**
     * 获得当前user的所有token
     * @return
     */
    @RequestMapping(value = "token/list", method = RequestMethod.GET)
    public Object getTokenInfoList() {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (!currentUser.getSystemRole().equals(SystemRole.VISITER)) {
            List<TokenInfo> tokenInfos = authService.getTokenInfos(currentUser.getUserName());
            return getResult(tokenInfos);
        }

        return getError(ErrorCodes.ERROR_PERMISSION_DENIED, "PERMISSION DENIED");

    }

    /**
     * 刷新token
     * @param token
     * @return
     */
    @RequestMapping(value = "token/refresh", method = RequestMethod.POST)
    public Object refreshToken(@RequestParam("token") String token) {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (operationAccessControl.checkTokenOwner(currentUser.getUserName(), token)) {
            authService.refreshToken(token);
            return getResult("success");
        }

        return getError(ErrorCodes.ERROR_PERMISSION_DENIED, "PERMISSION DENIED");
    }

    /**
     * 创建auth，判断bucket是否属于user，和token是否属于user
     * @param serviceAuth
     * @return
     */
    @RequestMapping(value = "auth", method = RequestMethod.POST)
    public Object createAuth(@RequestBody ServiceAuth serviceAuth) {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (operationAccessControl
                .checkBucketOwner(currentUser.getUserName(), serviceAuth.getBucketName())
                && operationAccessControl
                .checkTokenOwner(currentUser.getUserName(), serviceAuth.getTargetToken())) {
            authService.addAuth(serviceAuth);
            return getResult("success");
        }
        return getError(ErrorCodes.ERROR_PERMISSION_DENIED, "PERMISSION DENIED");
    }

    /**
     * 删除Auth
     * @param bucket
     * @param token
     * @return
     */
    @RequestMapping(value = "auth", method = RequestMethod.DELETE)
    public Object deleteAuth(@RequestParam("bucket") String bucket,
                             @RequestParam("token") String token) {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (operationAccessControl
                .checkBucketOwner(currentUser.getUserName(), bucket)
                && operationAccessControl
                .checkTokenOwner(currentUser.getUserName(), token)) {
            authService.deleteAuth(bucket, token);
            return getResult("success");
        }
        return getError(ErrorCodes.ERROR_PERMISSION_DENIED, "PERMISSION DENIED");
    }



}
