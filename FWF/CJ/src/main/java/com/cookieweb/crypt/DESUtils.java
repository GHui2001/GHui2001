package com.cookieweb.crypt;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;
import com.cookieweb.NetworkUtils;

public class DESUtils {

    public static String encryptHex(String content) {
        byte[] key = NetworkUtils.networkConfig.getEncryptKey().getBytes();
        DES des = SecureUtil.des(key);
        return des.encryptHex(content);
    }

    public static String encryptBase64(String content) {
        byte[] key = NetworkUtils.networkConfig.getEncryptKey().getBytes();
        DES des = SecureUtil.des(key);
        return des.encryptBase64(content);
    }

    public static String decrypt(String content) {
        byte[] key = NetworkUtils.networkConfig.getEncryptKey().getBytes();
        DES des = SecureUtil.des(key);
        return des.decryptStr(content);
    }

}
