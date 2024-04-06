package com.cookieweb.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class SignUtils {


    public static String getMachineCode() {
        try {
            InetAddress ipAddress = InetAddress.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(ipAddress);

            byte[] macAddressBytes = networkInterface.getHardwareAddress();
            StringBuilder macAddressBuilder = new StringBuilder();

            for (int i = 0; i < macAddressBytes.length; i++) {
                macAddressBuilder.append(String.format("%02X%s", macAddressBytes[i],
                        (i < macAddressBytes.length - 1) ? "-" : ""));
            }

            return SignUtils.md5Hex(macAddressBuilder.toString());
        } catch (Exception e) {
            System.out.println("get mac error");
            System.exit(1);
            return "";
        }
    }

    private static String readOutput(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(),
                StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line.trim());
        }
        reader.close();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static String map2Params(Map<String, Object> map, String[] excludes, boolean auto) {
        for (String exclude : excludes) {
            map.remove(exclude);
        }
        ArrayList<String> list = new ArrayList<>(map.keySet());
        Collections.sort(list);
        StringBuilder str = new StringBuilder();
        for (String a : list) {
            str.append(a).append("=").append(map.get(a)).append("&");
        }
        return str.substring(0, str.length() - 1);
    }

    public static String md5Hex(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4',
                '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] btInput = s.getBytes();
            //获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            //使用指定的字节更新摘要
            mdInst.update(btInput);
            //获得密文
            byte[] md = mdInst.digest();
            //把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String sha1Hex(String input) {
        try {
            MessageDigest sha1Digest = MessageDigest.getInstance("SHA-1");
            byte[] hashedBytes = sha1Digest.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 not supported", e);
        }
    }

    public static String sha256Hex(String input) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(input.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
