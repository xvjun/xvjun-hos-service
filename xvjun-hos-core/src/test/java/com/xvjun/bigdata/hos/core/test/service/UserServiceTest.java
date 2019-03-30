//package com.xvjun.bigdata.hos.core.test.service;
//
//import com.xvjun.bigdata.hos.core.usermgr.model.SystemRole;
//import com.xvjun.bigdata.hos.core.usermgr.model.UserInfo;
//import com.xvjun.bigdata.hos.core.usermgr.service.IUserService;
//import com.xvjun.bigdata.hos.mybatis.test.BaseTest;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//
//public class UserServiceTest extends BaseTest {
//
//    @Autowired
//    @Qualifier("userServiceImpl")
//    IUserService userService;
//
//    @Test
//    public void addUserTest(){
//        UserInfo userInfo = new UserInfo("xvjun","123456","this is a test user",SystemRole.ADMIN);
//        userService.addUser(userInfo);
//    }
//
//    @Test
//    public void getUserTest(){
//
//
//        UserInfo userInfo = userService.getUserInfoByName("Tom");
//        System.out.println(userInfo.toString());
//    }
//
//    @Test
//    public void deleteUser() {
//        UserInfo userInfo = userService.getUserInfoByName("jixin");
//        userService.deleteUser(userInfo.getUserId());
//    }
//
//    @Test
//    public void getuserInfo(){
//        System.out.println(userService.getUserInfoByName("Tom"));
//    }
//
//    @Test
//    public void getcheckPassword(){
//        System.out.println(userService.checkPassword("Tom","123456"));
//    }
//
//}
