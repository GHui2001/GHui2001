package com.cookieweb.crypt;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import com.cookieweb.NetworkUtils;
import com.cookieweb.enums.AESEnum;
import com.cookieweb.utils.NetworkException;

public class AESUtils {

    public static String encrypt2Hex(String content) {
        AES aes = returnAES();
        return aes.encryptHex(content);
    }

    public static String encrypt2Base64(String content) {
        AES aes = returnAES();
        return aes.encryptBase64(content);
    }

    public static AES returnAES() {
        AES aes;
        AESEnum aesEnum;
        try {
            Integer mode = NetworkUtils.networkConfig.getEncryptType();
            if (mode == 0) {
                aesEnum = AESEnum.FUNC1_ECB_PADDING;
            } else if (mode == 5) {
                aesEnum = AESEnum.FUNC2_CTS_PADDING;
            } else if (mode == 6) {
                aesEnum = AESEnum.FUNC3_CBC_PADDING;
            } else {
                throw new RuntimeException("没有匹配的");
            }
        } catch (Exception e) {
            aesEnum = AESEnum.FUNC1_ECB_PADDING;
        }

        if (aesEnum.compareTo(AESEnum.FUNC1_ECB_PADDING) == 0) {
            aes = new AES(Mode.ECB, Padding.PKCS5Padding, NetworkUtils.networkConfig.getEncryptKey().getBytes());
        } else {
            aes = new AES(aesEnum.getAesMode(), aesEnum.getPaddingMode(),  NetworkUtils.networkConfig.getEncryptKey().getBytes(), NetworkUtils.networkConfig.getIv().getBytes());
        }
        return aes;
    }

    public static String decrypt(String content) {
        if (StrUtil.isEmpty(content)) throw new NetworkException("解密内容为空");
        AES aes = returnAES();
        content = content.replaceAll(" ", "+");
        return aes.decryptStr(content, CharsetUtil.CHARSET_UTF_8);
    }

}
