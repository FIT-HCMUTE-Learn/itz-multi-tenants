package com.multi.tenants.api.controller;

import com.multi.tenants.api.constant.ITzBaseConstant;
import com.multi.tenants.api.jwt.ITzBaseJwt;
import com.multi.tenants.api.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.Objects;

public class ABasicController {
    @Autowired
    private UserServiceImpl userService;

    public long getCurrentUser(){
        ITzBaseJwt mgrJwt = userService.getAddInfoFromToken();
        return mgrJwt.getAccountId();
    }

    public long getCurrentDevice(){
        ITzBaseJwt mgrJwt = userService.getAddInfoFromToken();
        return mgrJwt.getDeviceId();
    }

    public long getTokenId(){
        ITzBaseJwt mgrJwt = userService.getAddInfoFromToken();
        return mgrJwt.getTokenId();
    }

    public ITzBaseJwt getSessionFromToken(){
        return userService.getAddInfoFromToken();
    }

    public boolean isSuperAdmin(){
        ITzBaseJwt mgrJwt = userService.getAddInfoFromToken();
        if(mgrJwt !=null){
            return mgrJwt.getIsSuperAdmin();
        }
        return false;
    }

    public boolean isShop(){
        ITzBaseJwt mgrJwt = userService.getAddInfoFromToken();
        if(mgrJwt !=null){
            return Objects.equals(mgrJwt.getUserKind(), ITzBaseConstant.USER_KIND_MANAGER);
        }
        return false;
    }

    public String getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            OAuth2AuthenticationDetails oauthDetails =
                    (OAuth2AuthenticationDetails) authentication.getDetails();
            if (oauthDetails != null) {
                return oauthDetails.getTokenValue();
            }
        }
        return null;
    }
}
