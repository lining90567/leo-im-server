package org.leo.im.http.util;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Jwt工具类
 * 
 * @author Leo
 * @date 2018/3/28
 */
public class JwtUtils {

    /**
     * 生成加密key
     * 
     * @param secret
     * @return
     */
    private static SecretKey generalKey(String secret) {
        byte[] encodedKey = Base64.getDecoder().decode(secret);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * 创建jwt
     * 
     * @param subject
     * @param secret
     * @param ttlMillis
     * @return
     */
    public static String createJWT(String subject, String secret, long ttlMillis) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        SecretKey key = generalKey(secret);
        JwtBuilder builder = Jwts.builder().setId("jwt").setIssuedAt(now).setSubject(subject)
                .signWith(signatureAlgorithm, key);
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    /**
     * 解密jwt
     * 
     * @param jwt
     * @param secret
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt, String secret) {
        SecretKey key = generalKey(secret);
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();
        return claims;
    }

}
