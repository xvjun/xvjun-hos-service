//package com.xvjun.bigdata.hos.core.test.service;
//
//
//import com.xvjun.bigdata.hos.core.authmgr.model.ServiceAuth;
//import com.xvjun.bigdata.hos.core.authmgr.model.TokenInfo;
//import com.xvjun.bigdata.hos.core.authmgr.service.IAuthService;
//import com.xvjun.bigdata.hos.core.usermgr.model.UserInfo;
//import com.xvjun.bigdata.hos.core.usermgr.service.IUserService;
//import com.xvjun.bigdata.hos.mybatis.test.BaseTest;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//
//import java.util.Date;
//import java.util.List;
//
///**
// * Created by jixin on 18-3-8.
// */
//public class AuthServiceTest extends BaseTest {
//
//  @Autowired
//  @Qualifier("authServiceImpl")
//  IAuthService authService;
//
//  @Autowired
//  @Qualifier("userServiceImpl")
//  IUserService userService;
//
//  @Test
//  public void addToken() {
//    UserInfo a = userService.getUserInfoByName("xvjun");
//    TokenInfo tokenInfo = new TokenInfo(a.getUserId(),7,new Date(),new Date(),true,a.getUserName());
//    System.out.println(authService.addToken(tokenInfo));
//  }
//
//  @Test
//  public void refreshToken() {
//    List<TokenInfo> tokenInfos = authService.getTokenInfos("jixin");
//    tokenInfos.forEach(tokenInfo -> {
//      System.out.println(authService.refreshToken(tokenInfo.getToken()));
//    });
//  }
//
//  @Test
//  public void deleteToken() {
//    List<TokenInfo> tokenInfos = authService.getTokenInfos("jixin");
//    if (tokenInfos.size() > 0) {
//      authService.deleteToken(tokenInfos.get(0).getToken());
//    }
//  }
//
//  @Test
//  public void addAuth() {
//    List<TokenInfo> tokenInfos = authService.getTokenInfos("jixin");
//    if (tokenInfos.size() > 0) {
//      ServiceAuth serviceAuth = new ServiceAuth();
//      serviceAuth.setAuthTime(new Date());
//      serviceAuth.setBucketName("testBucket");
//      serviceAuth.setTargetToken(tokenInfos.get(0).getToken());
//      authService.addAuth(serviceAuth);
//    }
//  }
//
//  @Test
//  public void deleteAuth() {
//    List<TokenInfo> tokenInfos = authService.getTokenInfos("jixin");
//    if (tokenInfos.size() > 0) {
//      authService.deleteAuth("testBucket", tokenInfos.get(0).getToken());
//    }
//  }
//}
