package me.kqn.myplayersql;

import me.kqn.myplayersql.api.MPSAPI;
import me.kqn.myplayersql.command.Command;
import me.kqn.myplayersql.config.Config;
import me.kqn.myplayersql.modules.M_Minecraft;
import me.kqn.myplayersql.user.UserManager;
import me.kqn.myplayersql.util.Logger;
import me.kqn.myplayersql.util.Mysql;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.MojangsonParser;
import net.minecraft.server.v1_16_R3.NBTTagCompound;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import sun.util.resources.cldr.uk.CurrencyNames_uk;

public final class Myplayersql extends JavaPlugin {

    private static Myplayersql instance;
    public static Myplayersql getInstance(){
        return instance;
    }
    private void registerDefaultModules(){
        MPSAPI.registerModel(new M_Minecraft());
    }
    public   Mysql mysql;
    public  UserManager userManager=null;
    @Override
    public void onEnable() {
        instance=this;
        Config.onEnable();
        userManager=new UserManager();
        Logger.Log("尝试连接数据库...");
        try {
            mysql=Mysql.getInstance(Config.config.get(Config.URL),Config.config.get(Config.USERNAME),Config.config.get(Config.PASSWORD));
        }catch (Exception e){
            Logger.Log("连接数据库失败...");
            Logger.Log("即将关闭插件...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Logger.Log("连接数据库成功");
        registerDefaultModules();
        Bukkit.getPluginCommand("Myplayersql").setExecutor(new Command());

        //registerDefaultModules();//注册默认模块 例如:Minecraft原版
    }
    public void onReload(){
        Logger.Log("重载插件中...");
        Config.onEnable();
        Logger.Log("尝试重新连接数据库...");
        try {
            mysql=Mysql.getInstance(Config.config.get(Config.URL),Config.config.get(Config.USERNAME),Config.config.get(Config.PASSWORD));
        }catch (Exception e){
            Logger.Log("连接数据库失败...");
        }
        Logger.Log("重载完毕");
    }
    @Override
    public void onDisable() {
        if(mysql!=null){
            mysql.onDisable();
        }
    }
}
