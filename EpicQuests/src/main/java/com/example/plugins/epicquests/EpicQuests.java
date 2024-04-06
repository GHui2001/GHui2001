package com.example.plugins.epicquests;

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
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.ChatColor;
import javax.swing.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.bukkit.Location;







public class EpicQuests extends JavaPlugin implements CommandExecutor, Listener {
    private boolean countdownRunning = false; // 用于标记倒计时是否正在进行
    private boolean airBlockDetected = false; // 用于标记是否检测到空气方块
    private boolean autoFallEnabled = true;//用于标记方块下落状态是否开启
    private int intervalTicks = 3;//用于调整生成动物延迟


    private BukkitRunnable countdownTask;

    // 定义变量以存储计算后的值
    private int[] var_a;
    private int[] var_b;
    private int[] var_c;
    private int[] var_d;
    private int[] var_a1;
    private int[] var_b1;
    private int[] var_c1;
    private int[] var_d1;
    private int[] var_a2;
    private int[] var_b2;
    private int[] var_ty1;
    private int[] var_ty2;
    private int[] var_jy1;
    private int[] var_jy2;
    private int[] var_zy1;
    private int[] var_zy2;
    private int[] var_tnt1;
    private int[] var_tnt2;

    private int zy1x;//检测左下角坐标
    private int zy1y;//检测左下角坐标
    private int zy1z;//检测左下角坐标
    private int zy2x;//检测右上角坐标
    private int zy2y;//检测右上角坐标
    private int zy2z;//检测右上角坐标
    private int qk1x;//清空左下角坐标
    private int qk1y;//清空左下角坐标
    private int qk1z;//清空左下角坐标
    private int qk2x;//清空右上角坐标
    private int qk2y;//清空右上角坐标
    private int qk2z;//清空右上角坐标
    private int hsx;//红石坐标
    private int hsy;//红石坐标
    private int hsz;//红石坐标


    private Material fkMaterial = Material.WHITE_STAINED_GLASS;

    private boolean replaceRunning = false; // 用于标记是否正在替换中

    // 标记卡密
    private String card;
    private boolean input = false;


    /**
     * 这个问题是，刚刚在网页上配置了，但是没保存，
     */
    @Override
    public void onEnable() {
        // 这里是验证的入口
        // 初始化加密配置
        NetworkConfig networkConfig = new NetworkConfig();
        NetworkUtils.networkConfig = networkConfig;
        // appId 从应用中心处获得13
        networkConfig.setAppId(13);
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
        networkConfig.setEncryptKey("dYrz5QQfTsnFK3DMxtHrFTESdxQA8tJf");
        // 从应用中心处获取
        networkConfig.setAppKey("6O38OL4XIXY2YMUBFZ6F77OWNZ0DBKYP");
        networkConfig.setTimestamp(200000);

        // 配置信息以上就写好了，开始发送网络请求

        // 第一步随机出来一个随机数
        String randomStr = RandomUtil.randomString(16);
        // 第二部，如果你要用的是登录接口，例如单码登录，则找到单码登录的参数  -> 参数是card
        HashMap<String, Object> map = new HashMap<>();

        // 读取卡密
        File file = new File("D:\\card");
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

            // 第四步 发送请
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
                FileUtil.writeString(card, new File("D:\\card"), StandardCharsets.UTF_8);
                break;
            } else {
                input = true;
                // 登录失败
                System.out.println("登录失败：" + responseJSON.getString("msg"));
            }
        }


        // 加载配置文件
        getConfig().options().copyDefaults(true);
        saveConfig();

        // 注册命令
        this.getCommand("dfk").setExecutor(this);

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(this, this); // 注册当前类为事件监听器
        getLogger().info("AirBlockCheckerPlugin 已启用");

        //自动下落
        getLogger().info("BlockGravityPlugin has been enabled!");

        new BukkitRunnable() {
            @Override
            public void run() {
                checkBlocks();
            }
        }.runTaskTimer(this, 0, 1);
    }


    @Override
    public void onDisable() {
        // 在插件禁用时执行的逻辑（如果有）
        getLogger().info("AirBlockCheckerPlugin 已禁用");
        //自动下落
        getLogger().info("BlockGravityPlugin has been disabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 执行 /dfk 计算
        player.performCommand("dfk 计算");
        player.performCommand("dfk 开始");
        player.performCommand("mb 10");
        // 将所有玩家传送到指定坐标
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "execute as @a run tp @s " +
                hsx + " " + hsy  + " " + hsz);
        //将全部玩家设置成op
        getServer().dispatchCommand(getServer().getConsoleSender(), "op " + event.getPlayer().getName());
        // 在玩家加入时，以控制台身份执行一次/gamemode creative @a
        getServer().dispatchCommand(getServer().getConsoleSender(), "gamemode creative " + event.getPlayer().getName());
        // 在玩家加入时，以控制台身份执行一次设置复活点
        getServer().dispatchCommand(getServer().getConsoleSender(), "execute at @a run spawnpoint " + event.getPlayer().getName());
        // 在玩家加入时，以控制台身份给予所有玩家一个石头
        getServer().dispatchCommand(getServer().getConsoleSender(), "give " + event.getPlayer().getName() + " minecraft:stone 1");

        // 延迟一段时间后执行
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {

                // 发送欢迎消息
                player.sendMessage( "§c[系统]§a欢迎来到我的世界TNT整蛊.");

                // 发送欢迎消息
                player.sendMessage( "§c[系统]§a拿出石头开始你的搭方块之旅吧.");

                // 发送欢迎消息
                player.sendMessage( "§c[系统]§a跟直播间的观众们斗智斗勇.");

                // 发送欢迎消息
                player.sendMessage( "§c[系统]§a努力完成挑战进度吧.");

                // 发送欢迎消息
                player.sendMessage( "§c[系统]§a更多直播整蛊QQ1413903090.");





            }
        }, 40L); // 20 ticks 延迟，可以根据需要调整延迟的时间
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("lw")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("xb")) {
                    return Xb(sender);
                } else if (args[0].equalsIgnoreCase("tntzf1")) {
                    return Tntzf1(sender);
                } else if (args[0].equalsIgnoreCase("tntzf2")) {
                    return Tntzf2(sender);
                } else if (args[0].equalsIgnoreCase("qk")) {
                    return qk(sender);
                } else if (args[0].equalsIgnoreCase("xtslf")) {
                    return xtslf(sender);
                } else if (args[0].equalsIgnoreCase("dl")) {
                    return dl(sender);
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("tnt")) {
                int amount;
                try {
                    amount = Integer.parseInt(args[1]);
                    generateTNT(sender, amount);
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid amount! Please enter a valid number.");
                    return false;
                }

            }
        }
        //头顶生成动物
        if (command.getName().equalsIgnoreCase("lw")) {
            if (args.length >= 2) {
                if (!sender.hasPermission("lw.spawn")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }

                int amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid number: " + args[1]);
                    return true;
                }

                EntityType entityType = EntityType.fromName(args[0].toUpperCase());
                if (entityType == null || !entityType.isAlive()) {
                    sender.sendMessage(ChatColor.RED + "Invalid animal type: " + args[0]);
                    return true;
                }

                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    spawnAnimalsAbovePlayer(player, entityType, amount);
                    sender.sendMessage(ChatColor.GREEN + "从天而降了 " + amount + " 只 " + entityType.name() + ".");
                } else {
                    spawnAnimalsAboveAllPlayers(entityType, amount);
                    sender.sendMessage(ChatColor.GREEN + "从天而降了 " + amount + " 只 " + entityType.name() + ".");
                }
                return true;
            }
        }



        //自动下落
        if (command.getName().equalsIgnoreCase("dfk")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("下落")) {
                // 如果是 /dfk 下落 命令，则切换自动下落的状态
                autoFallEnabled = !autoFallEnabled;
                sender.sendMessage("自动下落已" + (autoFallEnabled ? "开启" : "关闭"));
                return true;
            }
        }

        if (args.length < 1) {
            sender.sendMessage("/dfk <参数名> <数值> 或 /dfk 计算 或 /dfk 开始");
            return true;
        }

        FileConfiguration config = getConfig();
        String action = args[0].toLowerCase();

        if (action.equals("计算")) {
            calculateVars(config);
            sender.sendMessage("框架大小已计算。");
            return true;
        }

        if (action.equals("开始")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("该命令只能由玩家执行！");
                return true;
            }

            Player player = (Player) sender;
            if (replaceRunning) {
                sender.sendMessage("替换任务已经在运行中！");
                return true;
            }

            startReplaceTask(player);
            sender.sendMessage("框架搭建完成，请开始直播。");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("/dfk <参数名> <数值> 或 /dfk 计算 或 /dfk 开始");
            return true;
        }

        String paramName = args[0].toLowerCase();
        String paramValue = args[1];

        // 检查是否为有效参数
        if (!isValidParam(paramName)) {
            sender.sendMessage("参数名必须为 x、y、z、c、g 或 k。");
            return true;
        }

        try {
            int val = Integer.parseInt(paramValue);

            // 验证特定参数的约束
            if (paramName.equals("g") && val % 3 != 0) {
                sender.sendMessage("参数 'g' 必须是能被 3 整除的整数。");
                return true;
            }

            // 更新配置文件中的参数
            String nodePath = getParamNode(paramName);
            config.set(nodePath + paramName, val);
            saveConfig();

            sender.sendMessage("设置的参数 '" + paramName + "' 为: " + val);

            // 计算变量值并存储
            calculateVars(config);

        } catch (NumberFormatException e) {
            sender.sendMessage("数值必须是整数。");
            return true;
        }

        return true;
    }

    // 检查参数名是否有效
    private boolean isValidParam(String paramName) {
        return paramName.equals("x") || paramName.equals("y") || paramName.equals("z") ||
                paramName.equals("c") || paramName.equals("g") || paramName.equals("k");
    }

    // 根据参数名获取参数保存的节点路径
    private String getParamNode(String paramName) {
        if (paramName.equals("x") || paramName.equals("y") || paramName.equals("z")) {
            return "parameters.xyz.";
        } else {
            return "parameters.cgk.";
        }
    }

    // 计算变量值并存储
    private void calculateVars(FileConfiguration config) {
        int x = config.getInt("parameters.xyz.x");
        int y = config.getInt("parameters.xyz.y");
        int z = config.getInt("parameters.xyz.z");
        int c = config.getInt("parameters.cgk.c");
        int g = config.getInt("parameters.cgk.g");
        int k = config.getInt("parameters.cgk.k");


        var_a = new int[]{x, y, z};
        var_b = new int[]{x + c + 1, y, z + k + 1};
        var_c = new int[]{x + c + 1, y + g, z};
        var_d = new int[]{x, y + g, z + k + 1};
        var_a1 = new int[]{x, y + 1, z};
        var_b1 = new int[]{x + c + 1, y + 1, z + k + 1};
        var_c1 = new int[]{x + c + 1, y + g + 1, z};
        var_d1 = new int[]{x, y + g + 1, z + k + 1};
        var_a2 = new int[]{x + 1, y + 1, z + 1};
        var_b2 = new int[]{x + c, y + 1, z + k};
        var_ty1 = new int[]{x + 1, y + 1, z + 1};
        var_ty2 = new int[]{x + c, y + (g / 3) * 1, z + k};
        var_jy1 = new int[]{x + 1, y + 1 + (g / 3) * 1, z + 1};
        var_jy2 = new int[]{x + c, y + (g / 3) * 2, z + k};
        var_tnt1 = new int[]{x + 2, y + g + 1, z + 2};
        var_tnt2 = new int[]{x + c - 1, y + g + 1, z + k - 1};
        zy1x = x + 1;
        zy1y = y + 1 + (g / 3) * 2;
        zy1z = z + 1;
        var_zy1 = new int[]{zy1x, zy1y, zy1z};
        zy2x = x + c;
        zy2y = y + (g / 3) * 3;
        zy2z = z + k;
        var_zy2 = new int[]{zy2x, zy2y, zy2z};
        qk1x = x + 1;
        qk1y = y + 1;
        qk1z = z + 1;
        qk2x = x + c;
        qk2y = y + g;
        qk2z = z + k;
        hsx = x + (int) Math.ceil((double) c / 2.0);
        hsy = y + g + 1;
        hsz = z + (int) Math.ceil((double) k / 2.0);
    }

    // 开始循环替换任务
    private void startReplaceTask(Player player) {
        replaceRunning = true;
        World world = player.getWorld();

        // 使用 Bukkit 调度器启动一个循环任务，每个游戏刻检查一次 replaceRunning 的值
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (replaceRunning) {
                // 替换除了TNT和空气方块以外的所有方块为铁块
                replaceBlocksExcept(world, Material.AIR, Material.TNT, Material.IRON_BLOCK, var_ty1, var_ty2);
                // 替换除了TNT和空气方块以外的所有方块为金块
                replaceBlocksExcept(world, Material.AIR, Material.TNT, Material.GOLD_BLOCK, var_jy1, var_jy2);
                // 替换除了TNT和空气方块以外的所有方块为金块
                replaceBlocksExcept(world, Material.AIR, Material.TNT, Material.DIAMOND_BLOCK, var_zy1, var_zy2);
                // 替换除了TNT和空气方块以外的所有方块为金块
                replaceBlocksExcept(world, Material.AIR, Material.TNT, Material.AIR, var_c1, var_d1);

                //PINK_WOOL
                //STONE
                // 替换指定区域内的方块为玻璃块
                replaceBlocks(player, var_a, var_b);
                replaceBlocks(player, var_a1, var_c);
                replaceBlocks(player, var_c, var_b1);
                replaceBlocks(player, var_b1, var_d);
                replaceBlocks(player, var_d, var_a1);

            }
        }, 0L, 1L); // 每个游戏刻（大约每秒）执行一次

        // 你也可以在这里添加停止任务的逻辑，如果你想要手动停止任务的话
        // 例如：if (!replaceRunning) {Bukkit.getScheduler().cancelTasks(this);}
    }

    // 替换指定区域内的方块为指定的方块类型
    private void replaceBlocksExcept(World world, Material exclude1, Material exclude2, Material to, int[] fromCoords, int[] toCoords) {
        int fromX = fromCoords[0];
        int fromY = fromCoords[1];
        int fromZ = fromCoords[2];
        int toX = toCoords[0];
        int toY = toCoords[1];
        int toZ = toCoords[2];

        int minX = Math.min(fromX, toX);
        int minY = Math.min(fromY, toY);
        int minZ = Math.min(fromZ, toZ);
        int maxX = Math.max(fromX, toX);
        int maxY = Math.max(fromY, toY);
        int maxZ = Math.max(fromZ, toZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    Material type = block.getType();
                    if (type != exclude1 && type != exclude2) {
                        block.setType(to);
                    }
                }
            }
        }
    }

    // 替换指定区域内的方块为玻璃块
    private void replaceBlocks(Player player, int[] start, int[] end) {
        World world = player.getWorld();
        Vector min = new Vector(start[0], start[1], start[2]);
        Vector max = new Vector(end[0], end[1], end[2]);

        for (int x = Math.min(min.getBlockX(), max.getBlockX()); x <= Math.max(min.getBlockX(), max.getBlockX()); x++) {
            for (int y = Math.min(min.getBlockY(), max.getBlockY()); y <= Math.max(min.getBlockY(), max.getBlockY()); y++) {
                for (int z = Math.min(min.getBlockZ(), max.getBlockZ()); z <= Math.max(min.getBlockZ(),
                        max.getBlockZ()); z++) {
                    world.getBlockAt(x, y, z).setType(Material.WHITE_STAINED_GLASS);
                }
            }
        }
    }
    //自动下落
    private void checkBlocks() {
        // 如果自动下落开关关闭，则直接返回，不执行下落逻辑
        if (!autoFallEnabled) {
            return;
        }
        World world = Bukkit.getWorlds().get(0); // 获取第一个加载的世界

        // 确保 var_a2 和 var_b2 包含有效的坐标
        if (var_a2 == null || var_b2 == null || var_a2.length < 3 || var_b2.length < 3) {
            return;
        }

        // 确保世界已加载
        if (world == null) {
            return;
        }

        // 循环遍历指定区域的坐标
        for (int x = Math.min(var_a2[0], var_zy2[0]); x <= Math.max(var_a2[0], var_zy2[0]); x++) {
            for (int y = Math.min(var_a2[1], var_zy2[1]); y <= Math.max(var_a2[1], var_zy2[1]); y++) {
                for (int z = Math.min(var_a2[2], var_zy2[2]); z <= Math.max(var_a2[2], var_zy2[2]); z++) {
                    Location location = new Location(Bukkit.getWorlds().get(0), x, y, z);
                    Block block = location.getBlock();

                    // 检查方块是否为非空气方块
                    if (block.getType() != Material.AIR) {
                        Location downLocation = location.clone().add(0, -1, 0);
                        Block downBlock = downLocation.getBlock();

                        // 如果下方方块为空气，则将当前方块下移一个单位
                        if (downBlock.getType() == Material.AIR) {
                            downBlock.setType(block.getType());
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
        }

    }

    //头顶生成动物

    private void spawnAnimalsAbovePlayer(Player player, EntityType entityType, int amount) {
        // 生成指定数量的动物在玩家头顶，每个动物之间有间隔
        int delay = 0;
        for (int i = 0; i < amount; i++) {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                player.getWorld().spawnEntity(player.getLocation().add(0, 1, 0), entityType);
            }, delay);
            delay += intervalTicks; // 增加间隔时间
        }
    }

    private void spawnAnimalsAboveAllPlayers(EntityType entityType, int amount) {
        // 在所有在线玩家头顶生成动物
        for (Player player : Bukkit.getOnlinePlayers()) {
            spawnAnimalsAbovePlayer(player, entityType, amount);
        }
    }




    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            // 获取指定区域内的所有方块
            World world = Bukkit.getWorlds().get(0); // 假设您在第一个世界中检测
            int minX = zy1x;
            int minY = zy1y;
            int minZ = zy1z;
            int maxX = zy2x;
            int maxY = zy2y;
            int maxZ = zy2z;

            boolean hasAirBlock = false;
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Block block = world.getBlockAt(x, y, z);
                        if (block.getType() == Material.AIR) {
                            hasAirBlock = true;
                            break;
                        }
                    }
                    if (hasAirBlock) {
                        break;
                    }
                }
                if (hasAirBlock) {
                    break;
                }
            }

            // 根据检测结果执行相应操作
            if (hasAirBlock) {
                // 如果区域内含有空气方块且之前未检测到空气方块，则向控制台输出信息并设置标志位
                if (!airBlockDetected) {
                    getLogger().info("There is an air block in the specified region!");
                    airBlockDetected = true;
                    stopCountdown(); // 停止倒计时
                    // 显示消息给所有在线玩家
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendTitle("§c想完成？做梦呢？", "", 10, 70, 20);
                    }
                }
            } else {
                // 如果区域内不含有空气方块，则启动倒计时，并重置标志位
                if (!countdownRunning) {
                    startCountdown();
                    airBlockDetected = false;
                }
            }
        }, 0L, 20L); // 延迟0 tick执行，之后每20 ticks（1秒）执行一次


    }
    // 清空指定区域内的所有方块
    private void clearBlocksInRange(World world, int[] start, int[] end) {
        int minX = Math.min(start[0], end[0]);
        int minY = Math.min(start[1], end[1]);
        int minZ = Math.min(start[2], end[2]);
        int maxX = Math.max(start[0], end[0]);
        int maxY = Math.max(start[1], end[1]);
        int maxZ = Math.max(start[2], end[2]);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    world.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
    }

    private void startCountdown() {
        // 如果倒计时已经在运行，则不再启动新的倒计时
        if (countdownRunning) {
            return;
        }

        countdownRunning = true; // 标记倒计时正在运行

        countdownTask = new BukkitRunnable() {
            int seconds = 15;

            @Override
            public void run() {
                if (seconds > 0) {
                    // 向所有在线玩家显示倒计时消息
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendTitle("§c倒计时:"+ seconds, "", 0, 20, 0);
                    }
                    seconds--;
                    // 每一秒播放音效
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @a run playsound " +
                            "minecraft:block.note_block.pling block @a");
                } else {
                    // 倒计时结束时取消任务
                    cancel();
                    countdownRunning = false; // 标记倒计时结束
                    World world = Bukkit.getWorlds().get(0); // 获取第一个世界
                    clearBlocksInRange(world, new int[]{qk1x, qk1y, qk1z}, new int[]{qk2x, qk2y, qk2z});
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dq 1");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a title [{\"text\":\"恭喜！通关次数加一\",\"color\":\"gold\",\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false}]");
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "execute as @p at @s run summon minecraft:firework_rocket ~ ~ ~ {LifeTime:0,FireworksItem:{id:\"minecraft:firework_rocket\",Count:1b,tag:{Fireworks:{Explosions:[{Type:1,Colors:[I;11743532,14602026,4312372,2437522],FadeColors:[I;11743532,14602026,4312372,2437522]}]}}}}");
                }
            }
        };
        countdownTask.runTaskTimer(this, 0L, 20L);
    }

    private void stopCountdown() {
        // 停止倒计时任务
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownRunning = false;
        }

    }
//修补 完成
private boolean Xb(CommandSender sender) {
    World world = Bukkit.getWorlds().get(0); // 获取第一个加载的世界
    // 定义区域范围
    int startX = qk1x;
    int startY = qk1y;
    int startZ = qk1z;
    int endX = qk2x;
    int endY = qk2y;
    int endZ = qk2z;

    // 从下往上检查每一层
    for (int y = startY; y <= endY; y++) {
        boolean foundAirBlock = false;
        // 检查当前层是否有空气方块
        for (int x = Math.min(startX, endX); x <= Math.max(startX, endX); x++) {
            for (int z = Math.min(startZ, endZ); z <= Math.max(startZ, endZ); z++) {
                Block block = world.getBlockAt(x, y, z);
                if (block.getType() == Material.AIR) {
                    foundAirBlock = true;
                    break; // 找到一个空气方块就停止检查当前层
                }
            }
            if (foundAirBlock) {
                break; // 找到空气方块就停止检查当前层
            }
        }

        // 如果当前层有空气方块，则将该层填充为钻石方块并结束
        if (foundAirBlock) {
            for (int x = Math.min(startX, endX); x <= Math.max(startX, endX); x++) {
                for (int z = Math.min(startZ, endZ); z <= Math.max(startZ, endZ); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    block.setType(Material.STONE); // 填充方块成功
                }
            }


            // 将所有玩家传送到指定坐标
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "execute as @a run tp @s " +
                    hsx + " " + hsy  + " " + hsz);

            return true; // 填充完毕，结束方法
        }
    }

    // 如果没有发现空气方块，则发送消息并结束
    sender.sendMessage("补满啦！");
    return true;
}

//tnt炸飞1
    private boolean Tntzf1(CommandSender sender) {
        // 使用 Bukkit 调度器执行循环生成 TNT 命令
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                // 执行生成 TNT 的命令
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "execute as @p at @s run summon minecraft:tnt ~ ~ ~ {Fuse:2}");

                // 增加计数器
                count++;

                // 如果执行次数达到 1000 次，则取消调度任务
                if (count >= 250) {
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 0L, 1L); // 每执行一次命令延迟 1 tick
        return true;
    }
//tnt炸飞2
    private boolean Tntzf2(CommandSender sender) {
        // 使用 Bukkit 调度器执行循环生成 TNT 命令
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                // 执行生成 TNT 的命令
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "execute as @p at @s run summon minecraft:tnt ~ ~ ~ {Fuse:2}");

                // 增加计数器
                count++;

                // 如果执行次数达到 1000 次，则取消调度任务
                if (count >= 500) {
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 0L, 1L); // 每执行一次命令延迟 1 tick
        return true;
    }
//清空区域 完成
    private boolean qk(CommandSender sender) {

// 使用动态生成的坐标来放置 TNT 方块
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @s run fill " +
            qk1x + " " + qk1y + " " + qk1z + " " + qk2x + " " + qk2y + " " + qk2z + " minecraft:tnt");

    // 使用动态生成的坐标来放置红石块
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @s run fill " +
            hsx + " " + hsy  + " " + hsz + " " + hsx + " " + hsy + " " + hsz + " minecraft:redstone_block");

    // 使用动态生成的坐标来清楚红石块
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @s run fill " +
            hsx + " " + hsy  + " " + hsz + " " + hsx + " " + hsy + " " + hsz + " minecraft:air");

    // 将所有玩家传送到指定坐标
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "execute as @a run tp @s " +
            hsx + " " + hsy  + " " + hsz);

    return true;
    }
//xtslf
private boolean xtslf(CommandSender sender) {
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @a run replaceitem entity @e[distance=..100] container.0 minecraft:water_bucket");
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "effect give @a minecraft:levitation 5 40");
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamemode survival @a");

    return true;
}
//dl
    private boolean dl(CommandSender sender) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @a run summon minecraft:lightning_bolt ~ ~ ~");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @a run summon minecraft:lightning_bolt ~3 ~ ~-3 ");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @a run summon minecraft:lightning_bolt ~3 ~ ~3");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @a run summon minecraft:lightning_bolt ~-3 ~ ~-3");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @a run summon minecraft:lightning_bolt ~-3 ~ ~3");

        return true;
    }
    //tnt方法
    private void generateTNT(CommandSender sender, int amount) {
        World world;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (var_tnt1 == null || var_tnt2 == null) {
                player.sendMessage(ChatColor.RED + "请先使用 /dfk 计算框架大小.");
                return;
            }
            world = player.getWorld();
        } else {
            if (Bukkit.getWorlds().isEmpty()) {
                sender.sendMessage(ChatColor.RED + "未加载任何世界!");
                return;
            }
            // 如果没有玩家实例，使用第一个加载的世界
            world = Bukkit.getWorlds().get(0);
        }

        AtomicInteger count = new AtomicInteger(0);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (count.get() < amount) {
                    int x = getRandomInRange(var_tnt1[0], var_tnt2[0]);
                    int y = getRandomInRange(var_tnt1[1], var_tnt2[1]);
                    int z = getRandomInRange(var_tnt1[2], var_tnt2[2]);

                    Location location = new Location(world, x + 0.5, y, z + 0.5);
                    world.spawn(location, TNTPrimed.class, tnt -> {
                        tnt.setFuseTicks(20); // 设置引爆时间为一秒后（20 ticks）
                        // 设置爆炸范围
                        tnt.setYield(3.0f); // 爆炸范围为4个方块
                        // 设置TNT的初始速度为零，防止爆炸时炸飞其他TNT
                        tnt.setVelocity(new Vector(0, 0, 0));

                    });

                    count.incrementAndGet();
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 0L, 0L); // 20 ticks = 1 second

    }

//tnt方法
    private int getRandomInRange(int min, int max) {
        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }
}
