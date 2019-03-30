package com.xvjun.bigdata.hos.web.security;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.xvjun.bigdata.hos.core.authmgr.model.TokenInfo;
import com.xvjun.bigdata.hos.core.authmgr.service.IAuthService;
import com.xvjun.bigdata.hos.core.usermgr.model.SystemRole;
import com.xvjun.bigdata.hos.core.usermgr.model.UserInfo;
import com.xvjun.bigdata.hos.core.usermgr.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

/**
 * 拦截器，拦截用户进行验证
 */
@Component
public class SecurityIntercepter implements HandlerInterceptor {

    @Autowired
    @Qualifier("authServiceImpl")
    private IAuthService authService;

    @Autowired
    @Qualifier("userServiceImpl")
    private IUserService userService;

    private Cache<String,UserInfo> userInfoCache = CacheBuilder.newBuilder().expireAfterWrite(20,TimeUnit.MINUTES).build();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(request.getRequestURI().equals("/loginPost")){
            return true;
        }
        String token = "";
        HttpSession session = request.getSession();
        if(session.getAttribute(ContextUtil.SESSION_KEY) != null){
            token = session.getAttribute(ContextUtil.SESSION_KEY).toString();
        }else{
            token = request.getHeader("X-Auth_Token");
        }
        TokenInfo tokenInfo = authService.getTokenInfo(token);
        if(tokenInfo == null){
            String url = "/loginPost";
            response.sendRedirect(url);
            return false;
        }
        UserInfo userInfo = userInfoCache.getIfPresent(tokenInfo.getToken());
        if(userInfo == null){
            userInfo = userService.getUserInfo(token);
            if(userInfo == null){
                userInfo = new UserInfo();
                userInfo.setUserId(token);
                userInfo.setUserName("NOT_EXIST_USER");
                userInfo.setDetail("a temporary visitor");
                userInfo.setSystemRole(SystemRole.VISITER);
            }
            userInfoCache.put(tokenInfo.getToken(),userInfo);
        }
        ContextUtil.setCurrentUser(userInfo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
