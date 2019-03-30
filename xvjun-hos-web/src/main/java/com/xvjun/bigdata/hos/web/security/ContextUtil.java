package com.xvjun.bigdata.hos.web.security;

import com.xvjun.bigdata.hos.core.usermgr.model.UserInfo;

/**
 * 获取当前用户信息
 */
public class ContextUtil {

    public final static String SESSION_KEY = "user_token";

    private static ThreadLocal<UserInfo> userInfoThreadLocal = new ThreadLocal<UserInfo>();

    public static  UserInfo getCurrentUser(){return userInfoThreadLocal.get();}

    public static void setCurrentUser(UserInfo userInfo){
        userInfoThreadLocal.set(userInfo);
    }

    public static void clear(){userInfoThreadLocal.remove();}
}
