package com.jfb;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.command.CommandExecutor;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public class Main extends JavaPlugin implements CommandExecutor, Listener {
    private BossBar progressBar;
    private BossBar likesBar;
    private int targetLikes = 50;
    private int currentLikes = 0;
    private int targetRounds = 0; // 默认目标数量
    private int currentRounds = 0;
    private static final long DEFAULT_DELAY_TICKS = 1L;

    @Override
    public void onEnable() {
        getLogger().info("YxPlugin has been enabled!");
        getServer().getPluginManager().registerEvents(this, this);

        // Create progress bar BossBar
        progressBar = Bukkit.createBossBar(ChatColor.YELLOW + "§6§l下播目标", BarColor.BLUE, BarStyle.SOLID);
        progressBar.setVisible(true);

        // Create likes bar BossBar
        likesBar = Bukkit.createBossBar("§6掉炸弹:§d " + currentLikes + "§r/§c" + targetLikes, BarColor.RED, BarStyle.SOLID);
        likesBar.setVisible(true);

        getCommand("yx").setExecutor(this);
        getCommand("mb").setExecutor(this);
        getCommand("dq").setExecutor(this);
        getCommand("dz").setExecutor(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("YxPlugin has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("yx")) {
            if (!sender.hasPermission("yx.use")) {
                sender.sendMessage("You don't have permission to use this command.");
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage("Usage: /yx [entity]");
                return true;
            }

            String entity = args[0];
            String name = "菜"; // 默认名字
            long delayTicks = DEFAULT_DELAY_TICKS; // 默认延迟时间

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Location loc = player.getLocation().subtract(0, 10, 0);
                        if (entity.equalsIgnoreCase("mf")) {
                            Bee bee = (Bee) loc.getWorld().spawnEntity(loc, EntityType.BEE);
                            bee.setCustomName(name);
                            bee.setCustomNameVisible(true);
                            bee.setAnger(400);
                            bee.setTarget(player);

                            // 一秒后杀死蜜蜂
                            Bukkit.getScheduler().runTaskLater(Main.this, () -> {
                                if (!bee.isDead()) {
                                    bee.damage(bee.getHealth());
                                }
                            }, 1L);
                        } else if (entity.equalsIgnoreCase("hg")) {
                            Turtle turtle = (Turtle) loc.getWorld().spawnEntity(loc, EntityType.TURTLE);
                            turtle.setCustomName(name);
                            turtle.setCustomNameVisible(true);

                            // 一秒后杀死海龟
                            Bukkit.getScheduler().runTaskLater(Main.this, () -> {
                                if (!turtle.isDead()) {
                                    turtle.damage(turtle.getHealth());
                                }
                            }, 1L);
                        } else if (entity.equalsIgnoreCase("xm")) {
                            Panda panda = (Panda) loc.getWorld().spawnEntity(loc, EntityType.PANDA);
                            panda.setCustomName(name);
                            panda.setCustomNameVisible(true);

                            // 一秒后杀死熊猫
                            Bukkit.getScheduler().runTaskLater(Main.this, () -> {
                                if (!panda.isDead()) {
                                    panda.damage(panda.getHealth());
                                }
                            }, 1L);
                        } else if (entity.equalsIgnoreCase("ht")) {
                            Dolphin dolphin = (Dolphin) loc.getWorld().spawnEntity(loc, EntityType.DOLPHIN);
                            dolphin.setCustomName(name);
                            dolphin.setCustomNameVisible(true);

                            // 一秒后杀死海豚
                            Bukkit.getScheduler().runTaskLater(Main.this, () -> {
                                if (!dolphin.isDead()) {
                                    dolphin.damage(dolphin.getHealth());
                                }
                            }, 1L);
                        }
                    }
                }
            }.runTaskLater(this, delayTicks);




        } else if (command.getName().equalsIgnoreCase("mb")) {
            // Handle /mb command to set target rounds
            if (args.length == 1) {
                try {
                    int value = Integer.parseInt(args[0]);
                    targetRounds += value; // 加减操作
                    sender.sendMessage(ChatColor.GREEN + "目标数量已更新为：" + targetRounds);
                    updateProgressBar();
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "请输入有效的数字！");
                    return false;
                }
            }
        } else if (command.getName().equalsIgnoreCase("dq")) {
            // Handle /dq command to increase or decrease progress
            if (args.length == 1) {
                try {
                    int value = Integer.parseInt(args[0]);
                    currentRounds += value;
                    updateProgressBar();
                    sender.sendMessage(ChatColor.GREEN + "进度已更新。");
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "请输入有效的数字！");
                    return false;
                }
            }
        } else if (command.getName().equalsIgnoreCase("dz")) {
            // Handle /dz command for likes
            if (args.length < 1) {
                sender.sendMessage("用法: /dz dzsl <数值> 或者 /dz dz");
                return false;
            }

            // Handle /dz dzsl command
            if (args[0].equalsIgnoreCase("dzsl")) {
                if (args.length < 2) {
                    sender.sendMessage("请提供目标点赞数量！");
                    return false;
                }

                try {
                    targetLikes = Integer.parseInt(args[1]);
                    sender.sendMessage("目标点赞数量已设置为 " + targetLikes);
                    updateLikesBar();
                } catch (NumberFormatException e) {
                    sender.sendMessage("请输入一个有效的数字！");
                }
            }

            // Handle /dz dz command
            else if (args[0].equalsIgnoreCase("dz")) {
                currentLikes++;
                sender.sendMessage("+1");
                updateLikesBar();

                if (currentLikes >= targetLikes) {
                    // Execute /kill command
                    getServer().dispatchCommand(getServer().getConsoleSender(), "lw tnt 5");
                    // Reset likes count
                    currentLikes = 0;
                    updateLikesBar();
                }
            }
        }
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        progressBar.addPlayer(player);
        likesBar.addPlayer(player);
    }

    private void updateProgressBar() {
        double progress = (double) currentRounds / targetRounds;
        progressBar.setTitle("§6§l下播目标:§c§l " + currentRounds + "§r/§6§l" + targetRounds);
        progressBar.setProgress(progress);
    }

    private void updateLikesBar() {
        likesBar.setTitle("§6掉炸弹:§d " + currentLikes + "§r/§c" + targetLikes);
    likesBar.setProgress((double) currentLikes / targetLikes);
}

private void updateScoreboard() {
    // This method is left for your implementation
    // You can update scoreboard in this method if needed
}
}
