package com.example.plugins.nc;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.cookieweb.NetworkConfig;
import com.cookieweb.NetworkUtils;
import com.cookieweb.enums.CookieEnum;
import com.cookieweb.utils.SignUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import javax.swing.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;


import org.bukkit.plugin.java.JavaPlugin;

public final class NC extends JavaPlugin implements CommandExecutor, Listener {
    // 标记卡密
    private String card;
    private boolean input = false;


    /**
     * 这个问题是，刚刚在网页上配置了，但是没保存，
     */
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        // 这里是验证的入口
        // 初始化加密配置
        NetworkConfig networkConfig = new NetworkConfig();
        NetworkUtils.networkConfig = networkConfig;
        // appId 从应用中心处获得13
        networkConfig.setAppId(16);
        // 加密算法 可以去选，需要与接口安全的配置相同
        networkConfig.setEncryptType(CookieEnum.ENCRYPT_TYPE_AES_BCE);
        // 请求加密方式
        networkConfig.setReqType(CookieEnum.REQ_TYPE_ALL);
        // 响应加密方式
        networkConfig.setResType(CookieEnum.RES_TYPE_ALL);
        // 随机数防劫持
        networkConfig.setSafeCode(CookieEnum.SAFE_CODE_OPEN);
        // 加密编码方式
        networkConfig.setEncodeType(CookieEnum.ENCODE_TYPE_HEX);
        // 签名算法
        networkConfig.setSignType(CookieEnum.SIGN_TYPE_SHA256);
        // 签名计算方式
        networkConfig.setSignRule(CookieEnum.SIGN_RULE_FUNC_2);
        // 加密密钥
        networkConfig.setEncryptKey("QxbDQxN3JYWJjmi8s7maYrEHres6wyG5");
        // 从应用中心处获取
        networkConfig.setAppKey("V1HKSIYZRGLWZ8VZZQCVGFJ3R6EQWH9O");
        networkConfig.setTimestamp(200000);

        // 配置信息以上就写好了，开始发送网络请求

        // 第一步随机出来一个随机数
        String randomStr = RandomUtil.randomString(16);
        // 第二部，如果你要用的是登录接口，例如单码登录，则找到单码登录的参数  -> 参数是card
        HashMap<String, Object> map = new HashMap<>();

        // 读取卡密
        File file = new File("D:\\ztcard");
        if (file.exists()) {
            card = FileUtil.readString(file, StandardCharsets.UTF_8);
            if (!StrUtil.isEmpty(card)) {
                input = false;
            } else {
                input = true;
            }
        }

        // 控制是否让用户输入卡密


        // 此处修改一下代码，变成循环
        while (true) {
            if (input) {
                System.out.println("请输入激活码/许可证(请先复制激活码后直接回车即可)");
                card = JOptionPane.showInputDialog("请输入密钥","");
                if (StrUtil.isEmpty(card) || card.equals("-1")){
                    card = ClipboardUtil.getStr();
                }
            }

            map.put("card", card);
            // 使用md5给机器做编码
            map.put("mac", SignUtils.md5Hex(SignUtils.getMachineCode()));

            // 第三步，加密该数据
            HashMap<String, Object> params = NetworkUtils.goEncrypt(map, randomStr);

            // 第四步 发送请求
            String response = HttpUtil.post("http://plus.cookieweb.cn/api/single/login", params);

            // 第五步 解密数据
            String decryptStr = NetworkUtils.goDecrypt(response);


            // 解密完成后你可以选择验证数据 -> 如果验证失败会抛出异常
            try {
                NetworkUtils.checkResponse(decryptStr, randomStr);
            } catch (Exception e) {
                input = true;
                System.out.println("抱歉，验证失败");
            }

            JSONObject responseJSON = JSONObject.parseObject(decryptStr);
            if (responseJSON.getInteger("code") == 1) {
                // 登录成功
                System.out.println("登录成功");
                // 保存卡密
                FileUtil.writeString(card, new File("D:\\ztcard"), StandardCharsets.UTF_8);
                break;
            } else {
                input = true;
                // 登录失败
                System.out.println("登录失败：" + responseJSON.getString("msg"));
            }
        }


    }

    @Override
    public void onDisable() {
        // 插件关闭逻辑
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 发送欢迎消息
        player.sendMessage( "§c[系统]§a欢迎来到我的世界种麦子整蛊.");

        // 发送欢迎消息
        player.sendMessage( "§c[系统]§a拿出种子开始你的种田之旅吧.");

        // 发送欢迎消息
        player.sendMessage( "§c[系统]§a跟直播间的观众们斗智斗勇.");

        // 发送欢迎消息
        player.sendMessage( "§c[系统]§a努力完成挑战进度吧.");

        // 发送欢迎消息
        player.sendMessage( "§c[系统]§a更多直播整蛊QQ1413903090.");

        player.performCommand("mclive link 321550926058");

    }


}
