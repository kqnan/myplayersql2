package me.kqn.myplayersql.config;

import me.kqn.myplayersql.Myplayersql;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Config {
    public static final String URL="URL";
    public static final String USERNAME="USERNAME";
    public static final String PASSWORD="PASSWORD";
    public static final ArrayList<String> keys=new ArrayList<>(Arrays.asList(URL,USERNAME,PASSWORD));
    public static final HashMap<String,String> config=new HashMap<>();
    public static void onEnable(){
        config.clear();
        File configFile=new File("plugins\\Myplayersql\\config.yml");
        if(!configFile.exists()){
            Myplayersql.getInstance().saveDefaultConfig();
        }
        YamlConfiguration configYaml=YamlConfiguration.loadConfiguration(configFile);
        //检查每一个键是否存在,不存在就创建一个新的,存在就读入哈希表
        for(String key:keys){
            if(!configYaml.contains(key)){
                configYaml.set(key,"");
            }
            config.put(key,(String)configYaml.get(key));
        }
        try {
            configYaml.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
