package com.example.copyright.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * 密钥（从配置文件读取）
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * 过期时间（从配置文件读取，单位：秒）
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * 生成Token
     */
    public String generateToken(String address, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("address", address);
        claims.put("role", role);

        // expiration 配置单位是秒，需要转换为毫秒
        long expirationMs = expiration * 1000;

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(address)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();
    }

    /**
     * 从Token中获取地址
     */
    public String getAddressFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
            return (String) claims.get("address");
        } catch (Exception e) {
            log.error("Failed to get address from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从Token中获取角色
     */
    public String getRoleFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
            return (String) claims.get("role");
        } catch (Exception e) {
            log.error("Failed to get role from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 验证Token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token.replace("Bearer ", ""));
            return true;
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查Token是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("Failed to check token expiration: {}", e.getMessage());
            return true;
        }
    }
}
