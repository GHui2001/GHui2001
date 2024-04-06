package com.cookieweb.crypt;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import com.cookieweb.NetworkConfig;
import com.cookieweb.NetworkUtils;

import java.nio.charset.StandardCharsets;

public class RC4Util {

    /**
     * 对文本内容进行加密.
     * @param plainText 待加密明文内容.
     * @return 加密的密文.
     */
    public static String encryptBase64(String plainText) {
        byte[] plainBytes;
        byte[] cipherBytes;

        try {
            plainBytes = plainText.getBytes(StandardCharsets.UTF_8);
            cipherBytes = rc4EnOrDecode(plainBytes, NetworkUtils.networkConfig.getEncryptKey());
            return Base64.encode((cipherBytes));
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 对文本内容进行加密.
     * @param plainText 待加密明文内容.
     * @return 加密的密文.
     */
    public static String encryptHex(String plainText) {
        byte[] plainBytes;
        byte[] cipherBytes ;
        try {
            plainBytes = plainText.getBytes(StandardCharsets.UTF_8);
            cipherBytes = rc4EnOrDecode(plainBytes, NetworkUtils.networkConfig.getEncryptKey());
            return HexUtil.encodeHexStr(cipherBytes);
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 对文本密文进行解密.
     * @param cipherText 待解密密文.
     * @return 解密的明文.
     */
    public static String decrypt(String cipherText) {
        byte[] cipherBytes ;
        byte[] plainBytes ;
        try {
            cipherBytes = HexUtil.decodeHex(cipherText);
            plainBytes = rc4EnOrDecode(cipherBytes, NetworkUtils.networkConfig.getEncryptKey());
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            cipherBytes = Base64.decode(cipherText);
            plainBytes = rc4EnOrDecode(cipherBytes, NetworkUtils.networkConfig.getEncryptKey());
            return new String(plainBytes, StandardCharsets.UTF_8);
        }
    }

    /**
     * 初始化RC4密钥.
     * @param rc4Key RC4密钥.
     * @return 初始化后的密钥.
     */
    private static byte[] rc4InitKey(String rc4Key) {
        byte[] keyBytes ;
        byte[] keyState ;
        int indexFirst = 0;
        int indexSecond = 0;
        // 变量初始化.
        keyBytes = rc4Key.getBytes(StandardCharsets.UTF_8);
        keyState = new byte[256];
        for (int i = 0; i < 256; i++) {
            keyState[i] = (byte) i;
        }
        // 进行初始化.
        if (keyBytes.length == 0) {
            return null;
        }
        for (int i = 0; i < 256; i++) {
            indexSecond = ((keyBytes[indexFirst] & 0xff) + (keyState[i] & 0xff) + indexSecond) & 0xff;
            byte tmp = keyState[i];
            keyState[i] = keyState[indexSecond];
            keyState[indexSecond] = tmp;
            indexFirst = (indexFirst + 1) % keyBytes.length;
        }
        return keyState;
    }

    /**
     * RC4算法进行加解密.
     * @param bytes 待处理内容.
     * @param rc4Key RC4密钥.
     * @return 处理后结果内容.
     */
    private static byte[] rc4EnOrDecode(byte[] bytes, String rc4Key) {
        int x = 0;
        int y = 0;
        byte[] key = rc4InitKey(rc4Key);
        int xorIndex;
        byte[] result = new byte[bytes.length];
        // 数据加密.
        for (int i = 0; i < bytes.length; i++) {
            x = (x + 1) & 0xff;
            y = ((key[x] & 0xff) + y) & 0xff;
            byte tmp = key[x];
            key[x] = key[y];
            key[y] = tmp;
            xorIndex = ((key[x] & 0xff) + (key[y] & 0xff)) & 0xff;
            result[i] = (byte) (bytes[i] ^ key[xorIndex]);
        }
        return result;
    }

}
