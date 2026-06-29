package com.NagarSetu.Backend.CompleteSecurity.Security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@Getter
public class CookieService {

    @Value("${jwt.refresh_token_cookie_name}")
    private  String refreshTokenCookieName;

    @Value("${jwt.cookie_secure}")
    private  boolean cookieSecure;
    @Value("${jwt.cookie_http_only}")
    private boolean cookieHttpOnly;

    @Value("${jwt.cookie_same_site}")
    private String cookieSameSite;

    @Value("${jwt.cookie.Domain:}")
    private String cookieDomain;

    public void attachRefreshTokenToCookie(HttpServletResponse response, String value, long maxAge){
        var responseCookieBuilder =  ResponseCookie.from(refreshTokenCookieName,value)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path("/")
                .maxAge(maxAge/1000)
                .sameSite(cookieSameSite);
        if(cookieDomain!=null&&!cookieDomain.isBlank()){
            responseCookieBuilder.domain(cookieDomain);
        }
        ResponseCookie responseCookie = responseCookieBuilder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

    }

    public void clearRefreshTokenCookie(HttpServletResponse response){
        var builder = ResponseCookie.from(refreshTokenCookieName,"")
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSameSite);

        if(cookieDomain!=null&&!cookieDomain.isBlank()){
            builder.domain(cookieDomain);
        }
        ResponseCookie responseCookie = builder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }


    public void addNoStoreHeader(HttpServletResponse response){
        response.setHeader(HttpHeaders.CACHE_CONTROL,"no-store");
        response.setHeader("Pragma","no-cache");
    }


}
