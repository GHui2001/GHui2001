package com.cookieweb;

import com.cookieweb.enums.CookieEnum;

/**
 * @author Anc苏长思
 */
public class NetworkConfig {
    /**
     * appId
     */
    private int appId;
    /**
     * 加密方式 0-不加密 1-AES加密-BCE 2-DES加密 3-RC4加密 4-RSA 5-AES-CTS 6-AES-CBC
     */
    private int encryptType;
    private String iv;
    /**
     * 请求加密方式 0-不加密 1-仅加密参数值 2-全部加密
     */
    private int reqType;
    /**
     * 响应加密方式 0-不加密 1-全部加密
     */
    private int resType;
    /**
     * 随机数防劫持 0-关 1-开
     */
    private int safeCode;
    /**
     * 编码方式1-16进制 2-Base64
     */
    private int encodeType;
    /**
     * 签名方式0-不签名 1-MD5 2-SHA1 3-SHA256
     */
    private int signType;
    /**
     * 签名规则
     */
    private int signRule;
    /**
     * 加密密钥
     */
    private String encryptKey;
    /**
     * app密钥
     */
    private String appKey;
    /**
     * 是否开启服务器的时间戳验证
     */
    private int timestamp;


    public NetworkConfig() {
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(CookieEnum cookieEnum) {
        this.timestamp = cookieEnum.getKey();
    }


    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public int getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(CookieEnum cookieEnum) {
        this.encryptType = cookieEnum.getKey();
    }

    public int getReqType() {
        return reqType;
    }

    public void setReqType(CookieEnum cookieEnum) {
        this.reqType = cookieEnum.getKey();
    }

    public int getResType() {
        return resType;
    }

    public void setResType(CookieEnum cookieEnum) {
        this.resType = cookieEnum.getKey();
    }

    public int getSafeCode() {
        return safeCode;
    }

    public void setSafeCode(CookieEnum cookieEnum) {
        this.safeCode = cookieEnum.getKey();
    }

    public int getEncodeType() {
        return encodeType;
    }

    public void setEncodeType(CookieEnum cookieEnum) {
        this.encodeType = cookieEnum.getKey();
    }

    public int getSignType() {
        return signType;
    }

    public void setSignType(CookieEnum cookieEnum) {
        this.signType = cookieEnum.getKey();
    }

    public int getSignRule() {
        return signRule;
    }

    public void setSignRule(CookieEnum cookieEnum) {
        this.signRule = cookieEnum.getKey();
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }


}
