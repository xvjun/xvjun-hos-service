package com.xvjun.bigdata.hos.web;

import com.xvjun.bigdata.hos.common.Utils.CoreUtil;
import com.xvjun.bigdata.hos.core.usermgr.model.SystemRole;
import com.xvjun.bigdata.hos.core.usermgr.model.UserInfo;
import com.xvjun.bigdata.hos.core.usermgr.service.IUserService;
import com.xvjun.bigdata.hos.server.service.IHosStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationInitialization implements ApplicationRunner {

    @Autowired
    @Qualifier("hosStoreService")
    IHosStoreService hosStoreService;

    @Autowired
    @Qualifier("userServiceImpl")
    IUserService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        UserInfo userInfo = userService.getUserInfoByName(CoreUtil.SYSTEM_USER);
        if (userInfo == null) {
            UserInfo userInfo1 = new UserInfo(CoreUtil.SYSTEM_USER, "superadmin",
                    "this is superadmin", SystemRole.SUPERADMIN);
            userService.addUser(userInfo1);
        }
        hosStoreService.createSqlTable();
    }
}

