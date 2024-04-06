package com.cookieweb;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.cookieweb.crypt.AESUtils;
import com.cookieweb.crypt.DESUtils;
import com.cookieweb.crypt.RC4Util;
import com.cookieweb.crypt.RSAUtils;
import com.cookieweb.enums.CookieEnum;
import com.cookieweb.utils.NetworkException;
import com.cookieweb.utils.SignUtils;

import java.util.HashMap;

public class NetworkUtils {

    public static NetworkConfig networkConfig;


    /**
     * 拼接基础参数
     *
     * @return appId+safeCode+timestamp
     */
    public static HashMap<String, Object> getBaseParams(String randomStr) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("appId", networkConfig.getAppId());
        if (networkConfig.getSafeCode() == 0) {
            if (StrUtil.isEmpty(randomStr)) {
                throw new NetworkException("您开启了随机数校验，但并未传递随机数");
            }
            map.put("safeCode", randomStr);
        }
        map.put("timestamp", System.currentTimeMillis());
        return map;
    }

    /**
     * 该方法为加密参数值，传入键值对，将值加密好之后返回键值对
     */
    public static HashMap<String, Object> encryptValue(HashMap<String, Object> map) {
        HashMap<String, Object> hashMap = new HashMap<>();
        if (networkConfig.getEncodeType() == 1) {
            if (networkConfig.getEncryptType() == 1 || networkConfig.getEncryptType() == 5 || networkConfig.getEncryptType() == 6) {
                for (String s : map.keySet()) {
                    hashMap.put(s, AESUtils.encrypt2Hex(String.valueOf(map.get(s))));
                }
            }
            if (networkConfig.getEncryptType() == 2) {
                for (String s : map.keySet()) {
                    hashMap.put(s, DESUtils.encryptHex(String.valueOf(map.get(s))));
                }
            }
            if (networkConfig.getEncryptType() == 3) {
                for (String s : map.keySet()) {
                    hashMap.put(s, RC4Util.encryptHex(String.valueOf(map.get(s))));
                }
            }
        } else if (networkConfig.getEncodeType() == 2) {
            if (networkConfig.getEncryptType() == 1 || networkConfig.getEncryptType() == 5 || networkConfig.getEncryptType() == 6) {
                for (String s : map.keySet()) {
                    hashMap.put(s, AESUtils.encrypt2Base64(String.valueOf(map.get(s))));
                }
            }
            if (networkConfig.getEncryptType() == 2) {
                for (String s : map.keySet()) {
                    hashMap.put(s, DESUtils.encryptBase64(String.valueOf(map.get(s))));
                }
            }
            if (networkConfig.getEncryptType() == 3) {
                for (String s : map.keySet()) {
                    hashMap.put(s, RC4Util.encryptBase64(String.valueOf(map.get(s))));
                }
            }
        }
        return hashMap;
    }

    /**
     * 调用该方法即可完成一键加密
     *
     * @param params 专属参数
     * @param random 随机数
     * @return 已经加密好了的map键值对
     */
    public static HashMap<String, Object> goEncrypt(HashMap<String, Object> params, String random) {
        HashMap<String, Object> baseParams = NetworkUtils.getBaseParams(random);
        baseParams.putAll(params);

        /* 计算签名 */
        String[] arr = {"appId"};
        if (networkConfig.getSignRule() == 0) {
            if (networkConfig.getSignType() == 1) {
                baseParams.put("signature", SignUtils.md5Hex(baseParams.get("timestamp") + networkConfig.getAppKey()));
            } else if (networkConfig.getSignType() == 2) {
                baseParams.put("signature", SignUtils.sha1Hex(baseParams.get("timestamp") + networkConfig.getAppKey()));
            } else if (networkConfig.getSignType() == 3) {
                baseParams.put("signature",
                        SignUtils.sha256Hex(baseParams.get("timestamp") + networkConfig.getAppKey()));
            } else {
                baseParams.put("signature", "");
            }
        }
        if (networkConfig.getSignRule() == 1) {
            String s = SignUtils.map2Params(baseParams, arr, true);
            if (networkConfig.getSignType() == 1) {
                baseParams.put("signature", SignUtils.md5Hex(s + networkConfig.getAppKey()));
            } else if (networkConfig.getSignType() == 2) {
                baseParams.put("signature", SignUtils.sha1Hex(s + networkConfig.getAppKey()));
            } else if (networkConfig.getSignType() == 3) {
                baseParams.put("signature", SignUtils.sha256Hex(s + networkConfig.getAppKey()));
            } else {
                baseParams.put("signature", "");
            }
        }
        /* 加密数据 */
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("appId", networkConfig.getAppId());
        if (networkConfig.getReqType() == 0) {
            map.putAll(baseParams);
        } else if (networkConfig.getReqType() == 1) {
            HashMap<String, Object> encrypted = NetworkUtils.encryptValue(baseParams);
            map.putAll(encrypted);
        } else if (networkConfig.getReqType() == 2) {
            String s = SignUtils.map2Params(baseParams, arr, true);
            if (networkConfig.getEncodeType() == 1) {
                if (networkConfig.getEncryptType() == 1 || networkConfig.getEncryptType() == 5 || networkConfig.getEncryptType() == 6)
                    map.put("params", AESUtils.encrypt2Hex(s));
                if (networkConfig.getEncryptType() == 2) map.put("params", DESUtils.encryptHex(s));
                if (networkConfig.getEncryptType() == 3) map.put("params", RC4Util.encryptHex(s));
                if (networkConfig.getEncryptType() == 4) map.put("params", RSAUtils.pubilcKeyEncrypt2Hex(s));
            } else if (networkConfig.getEncodeType() == 2) {
                if (networkConfig.getEncryptType() == 1 || networkConfig.getEncryptType() == 5 || networkConfig.getEncryptType() == 6)
                    map.put("params", AESUtils.encrypt2Base64(s));
                if (networkConfig.getEncryptType() == 2) map.put("params", DESUtils.encryptBase64(s));
                if (networkConfig.getEncryptType() == 3) map.put("params", RC4Util.encryptBase64(s));
                if (networkConfig.getEncryptType() == 4) map.put("params", RSAUtils.pubilcKeyEncrypt2Base64(s));
            }
        }
        return map;
    }

    /**
     * 该方法传入被加密的字符串，将根据配置的加密信息，自动实现解密
     */
    public static String goDecrypt(String encryptStr) {
        if (networkConfig.getEncryptType() == 1 || networkConfig.getEncryptType() == 5 || networkConfig.getEncryptType() == 6)
            return AESUtils.decrypt(encryptStr);
        if (networkConfig.getEncryptType() == 2)
            return DESUtils.decrypt(encryptStr);
        if (networkConfig.getEncryptType() == 3)
            return RC4Util.decrypt(encryptStr);
        if (networkConfig.getEncryptType() == 4)
            return RSAUtils.pubilcKeyDecrypt(encryptStr);
        throw new NetworkException("解密失败，未配置解密对象");
    }

    /**
     * 该方法将校验返回值是否被篡改
     * 传入解密后的响应的字符串
     * 调用该方法若可能被篡改则抛出异常
     */
    public static void checkResponse(String decryptedResponse, String randomStr) {
        JSONObject parse = JSONObject.parseObject(decryptedResponse);
        // 验证随机数是否正确
        if (networkConfig.getSafeCode() == CookieEnum.SAFE_CODE_OPEN.getKey()) {
            if (StrUtil.isEmpty(randomStr) || !randomStr.equals(parse.getString("safeCode"))) {
                throw new NetworkException("随机数验证失败");
            }
        }
        // 验证签名是否正确
        HashMap<String, Object> map = new HashMap<String, Object>(parse);
        if (map.get("data") != null) {
            map.put("data", JSONObject.parseObject(String.valueOf(map.get("data"))));
        }
        String comSignature = parse.getString("timestamp") + networkConfig.getAppKey();
        if (networkConfig.getSignRule() == CookieEnum.SIGN_RULE_FUNC_1.getKey()) {
            if (networkConfig.getSignType() == CookieEnum.SIGN_TYPE_MD5.getKey()) {
                comSignature = SignUtils.md5Hex(comSignature);
            }
            if (networkConfig.getSignType() == CookieEnum.SIGN_TYPE_SHA1.getKey()) {
                comSignature = SignUtils.sha1Hex(comSignature);
            }
            if (networkConfig.getSignType() == CookieEnum.SIGN_TYPE_SHA256.getKey()) {
                comSignature = SignUtils.sha256Hex(comSignature);
            }
        }
        if (networkConfig.getSignRule() == CookieEnum.SIGN_RULE_FUNC_2.getKey()) {
            String tempStr = SignUtils.map2Params(map, new String[]{"signature"}, true) + networkConfig.getAppKey();
            if (networkConfig.getSignType() == CookieEnum.SIGN_TYPE_MD5.getKey()) {
                comSignature = SignUtils.md5Hex(tempStr);
            }
            if (networkConfig.getSignType() == CookieEnum.SIGN_TYPE_SHA1.getKey()) {
                comSignature = SignUtils.sha1Hex(tempStr);
            }
            if (networkConfig.getSignType() == CookieEnum.SIGN_TYPE_SHA256.getKey()) {
                comSignature = SignUtils.sha256Hex(tempStr);
            }
        }
        if (!parse.getString("signature").equals(comSignature)) {
            throw new NetworkException("数据签名验证失败");
        }
        // 验证时间戳
        if (networkConfig.getTimestamp() != 0) {
            Long timestamp = parse.getLong("timestamp");
            long l = System.currentTimeMillis();
            if (l - timestamp > networkConfig.getTimestamp()) {
                throw new NetworkException("时间戳验证失败");
            }
        }
    }

}
