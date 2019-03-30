//package com.xvjun.bigdata.hos.server;
//
//
//import com.xvjun.bigdata.hos.core.authmgr.model.ServiceAuth;
//import com.xvjun.bigdata.hos.core.authmgr.service.IAuthService;
//import com.xvjun.bigdata.hos.core.usermgr.model.UserInfo;
//import com.xvjun.bigdata.hos.core.usermgr.service.IUserService;
//import com.xvjun.bigdata.hos.mybatis.test.BaseTest;
//import com.xvjun.bigdata.hos.server.model.BucketModel;
//import com.xvjun.bigdata.hos.server.service.IBucketService;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//
//import java.util.List;
//
///**
// * Created by jixin on 18-3-8.
// */
//public class BucketMapperTest extends BaseTest {
//
//  @Autowired
//  @Qualifier("bucketServiceImpl")
//  IBucketService bucketService;
//  @Autowired
//  @Qualifier("authServiceImpl")
//  IAuthService authService;
//  @Autowired
//  @Qualifier("userServiceImpl")
//  IUserService userService;
//
//  @Test
//  public void addBucket() {
//    UserInfo userInfo = userService.getUserInfoByName("xvjun");
//    System.out.println(bucketService.addBucket(userInfo,"bucket2","frist"));
////      BucketModel bucketModel = new BucketModel("test1", "jixin", "");
//////      bucketMapper.addBucket(bucketModel);
//////      UserInfo userInfo = new UserInfo("jixin", "123456", SystemRole.ADMIN, "");
//////      userService.addUser(userInfo);
////      ServiceAuth serviceAuth = new ServiceAuth();
////      serviceAuth.setTargetToken(userInfo.getUserId());
////      serviceAuth.setBucketName(bucketModel.getBucketName());
////      authService.addAuth(serviceAuth);
////      BucketModel bucketModel2 = new BucketModel("test2", "jixin", "");
////      bucketMapper.addBucket(bucketModel2);
//  }
//
//  @Test
//  public void getBucket() {
//    BucketModel bucketModel = bucketService.getBucketByName("bucket1");
//    System.out.println(bucketModel.toString());
//
//  }
//
//  @Test
//  public void getUserAuthorizedBuckets() {
//    UserInfo userInfo = userService.getUserInfoByName("xvjun");
//    List<BucketModel> bucketModels = bucketService.getUserBuckets(userInfo.getUserId());
//    bucketModels.forEach(bucketModel -> {
//      System.out.println(bucketModel.toString());
//    });
//  }
//
//  @Test
//  public void deleteBucket() {
////    UserInfo userInfo = userService.getUserInfoByName("jixin");
////    List<BucketModel> bucketModels = bucketMapper.getUserAuthorizedBuckets(userInfo.getUserId());
////    bucketModels.forEach(bucketModel -> {
////      bucketMapper.deleteBucket(bucketModel.getBucketId());
////    });
//  }
//}
