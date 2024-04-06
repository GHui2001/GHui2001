package com.cookieweb.crypt;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.cookieweb.NetworkUtils;

public class RSAUtils {


    public static String pubilcKeyEncrypt2Hex(String content) {
        RSA rsa = new RSA(null, NetworkUtils.networkConfig.getEncryptKey());
        return rsa.encryptHex(content, KeyType.PublicKey);
    }

    public static String pubilcKeyEncrypt2Base64(String content) {
        RSA rsa = new RSA(null, NetworkUtils.networkConfig.getEncryptKey());
        return rsa.encryptBase64(content, KeyType.PublicKey);
    }

    public static String pubilcKeyDecrypt(String content) {
        RSA rsa = new RSA(null, NetworkUtils.networkConfig.getEncryptKey());
        return rsa.decryptStr(content, KeyType.PublicKey);
    }

}
