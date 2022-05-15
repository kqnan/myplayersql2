package me.kqn.myplayersql.user;

import me.kqn.myplayersql.api.IModule;
import me.kqn.myplayersql.api.MPSAPI;
import me.kqn.myplayersql.util.Logger;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import sun.rmi.runtime.Log;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {
    public User getModulesData(Player player){
        Validate.notNull(player);
        User user=new User(player);
        Map dataMap=user.getDataMap();
        for(IModule module: MPSAPI.getEnableModel()){
            try {
                dataMap.put(module.getModelId(),module.getData(player));
            } catch (Exception e) {
                Logger.Log("序列化模块:"+module.getModelId()+"时出错");
                e.printStackTrace();
            }
        }
        return user;
    }
    public void setModulesData(User user){
        Validate.notNull(user);
        for (IModule module:MPSAPI.getEnableModel()){
            if(user.getDataMap().containsKey(module.getModelId().toLowerCase())){
                try {
                    module.restore(user.getPlayer(),user.getDataMap().get(module.getModelId().toLowerCase()));
                } catch (Exception e) {
                    Logger.Log("为玩家:"+user.getPlayer().getName()+"载入模块:"+module.getModelId()+"时出错");
                    e.printStackTrace();
                }
            }
        }
    }
}
