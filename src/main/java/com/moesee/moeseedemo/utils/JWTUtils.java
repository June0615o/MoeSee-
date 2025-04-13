package com.moesee.moeseedemo.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JWTUtils {
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 86400000;
    //生成JWT
    public static String generateToken(String userUid){
        return Jwts.builder()
                .setSubject(userUid)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(KEY)
                .compact();
    }
    //解析JWT
    public static Claims parseToken(String token) throws ExpiredJwtException, JwtException{
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
