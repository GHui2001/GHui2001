package com.cookieweb.enums;

public enum CookieEnum {
    // 不加密
    ENCRYPT_TYPE_NONE(0),
    // AES算法 BCE模式
    ENCRYPT_TYPE_AES_BCE(1),
    // DES算法
    ENCRYPT_TYPE_DES(2),
    // RC4算法
    ENCRYPT_TYPE_RC4(3),
    // RSA算法
    ENCRYPT_TYPE_RSA(4),
    // AES算法 CTS模式
    ENCRYPT_TYPE_AES_CTS(5),
    // AES算法 CBC模式
    ENCRYPT_TYPE_AES_CBC(6),
    // 请求不加密
    REQ_TYPE_NONE(0),
    // 请求仅加密参数值
    REQ_TYPE_VALUE(1),
    // 请求全部加密
    REQ_TYPE_ALL(2),
    // 响应不加密
    RES_TYPE_NONE(0),
    // 响应全部加密
    RES_TYPE_ALL(1),
    // 开启随机数防篡改
    SAFE_CODE_OPEN(0),
    // 关闭随机数防篡改
    SAFE_CODE_CLOSE(1),
    // 不编码
    ENCODE_TYPE_NONE(0),
    // 16进制编码
    ENCODE_TYPE_HEX(1),
    // Base64编码
    ENCODE_TYPE_BASE64(2),
    // 不签名
    SIGN_TYPE_NONE(0),
    // MD5签名
    SIGN_TYPE_MD5(1),
    // SHA1签名
    SIGN_TYPE_SHA1(2),
    // SHA256签名
    SIGN_TYPE_SHA256(3),
    // 签名计算方式 一
    SIGN_RULE_FUNC_1(0),
    // 签名计算方式 二
    SIGN_RULE_FUNC_2(1),
    // 时间戳校验 -> 如开启，则传入具体的范围即可，例如 传入1000 即响应时间不超过1秒
    TIMESTAMP_OPEN(1),
    // 关闭时间戳校验
    TIMESTAMP_CLOSE(0);
    private final int key;

    CookieEnum(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
