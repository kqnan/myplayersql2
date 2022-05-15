package me.kqn.myplayersql.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Logger {
    public static  void Log(String msg){
        Bukkit.getServer().getLogger().info("[Myplayersql]"+msg);
    }
    public static void sendHelpMSG(CommandSender sender){
        sender.sendMessage("===Myplayersql同步插件===");
        sender.sendMessage("/mps reload重载插件");
        sender.sendMessage("/mps save <玩家名> 保存指定玩家数据");
        sender.sendMessage("/mps saveall保存所有玩家数据");
    }
}
