package org.leo.im.service.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码工具类
 * 
 * @author Leo
 * @date 2018/3/20
 */
public final class PasswordUtils {

    /**
     * 盐的长度
     */
    private static final int SALT_LENGTH = 16;

    /**
     * 生成盐
     * 
     * @return
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 得到md5加密口令
     * 
     * @param password
     * @param salt
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String getMd5Password(String password, String salt)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] bs = md5.digest((password + "|" + salt + "|").getBytes());
        return byteToHexString(bs);
    }

    /**
     * 将数组转换成16进制字符串
     * @param salt
     * @return
     */
    private static String byteToHexString(byte[] salt) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < salt.length; i++) {
            String hex = Integer.toHexString(salt[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }

}
