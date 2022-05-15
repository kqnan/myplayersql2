package me.kqn.myplayersql.command;

import io.netty.channel.unix.Buffer;
import me.kqn.myplayersql.Myplayersql;
import me.kqn.myplayersql.user.User;
import me.kqn.myplayersql.user.UserManager;
import me.kqn.myplayersql.util.Logger;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import sun.rmi.runtime.Log;

import java.sql.SQLException;

public class Command implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length>=1){
            if(args[0].equalsIgnoreCase("reload")){
                reload(sender);
            }
            if(args[0].equalsIgnoreCase("save")){
                save(sender,args);
            }
            if(args[0].equalsIgnoreCase("saveall")){
                saveall(sender);
            }
            if (args[0].equalsIgnoreCase("load")){
                load(sender,args);
            }
            if (args[0].equalsIgnoreCase("loadall")){
                loadall(sender);
            }

        }
        else {
            Logger.sendHelpMSG(sender);
        }
        return true;
    }
    public void load(CommandSender sender,String [] args){
        if(sender.hasPermission("Myplayersql.op")){
            Validate.notNull(Bukkit.getPlayerExact(args[1]));
            UserManager userManager=Myplayersql.getInstance().userManager;
            User user=new User(Bukkit.getPlayerExact(args[1]));
            Bukkit.getScheduler().runTaskAsynchronously(Myplayersql.getInstance(),()->{
                try {
                    byte[] playerData=Myplayersql.getInstance().mysql.getPlayerData(args[1]);
                    user.setData(playerData);
                    userManager.setModulesData(user);
                    Logger.Log("加载成功");
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }
    }
    public void loadall(CommandSender sender){

    }
    public void save(CommandSender sender,String[] args){
        if(sender.hasPermission("Myplayersql.op")){
            UserManager userManager=Myplayersql.getInstance().userManager;
            User user=userManager.getModulesData(Bukkit.getPlayerExact(args[1]));
            Bukkit.getScheduler().runTaskAsynchronously(Myplayersql.getInstance(),()->{
                try {
                    if(Myplayersql.getInstance().mysql.update(user)){
                        sender.sendMessage("成功");
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
        }
    }
    public void saveall(CommandSender sender){

    }
    public void reload(CommandSender sender){
        if((sender instanceof ConsoleCommandSender)){
            Myplayersql.getInstance().onReload();
            sender.sendMessage("重载成功");
        }
        if(sender instanceof Player){
            Player player=(Player) sender;
            if(player.hasPermission("Myplayersql.op")){
                Myplayersql.getInstance().onReload();
                sender.sendMessage("重载成功");
            }
        }
    }

}
