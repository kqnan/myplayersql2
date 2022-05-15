package me.kqn.myplayersql.util;

import me.kqn.myplayersql.user.User;
import net.minecraft.server.v1_16_R3.Tag;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import sun.rmi.runtime.Log;

import javax.xml.transform.Result;
import java.sql.*;

import static javafx.scene.input.DataFormat.URL;

public class Mysql {
    private Connection conn=null;
    public static final String dbName="MyPlayerSqlDataBase";
    public static final String tbName="PlayerDataTable";
    private static Mysql mysql=new Mysql();
    /**出错则返回null或报错
     * */
    @Nullable
    public static Mysql getInstance(String url,String username,String pw)throws Exception{
        if(mysql.conn!=null){
            mysql.conn.close();
        }
        //1.加载驱动程序
        Class.forName("com.mysql.jdbc.Driver");
        //2. 获得数据库连接
        mysql.conn = DriverManager.getConnection("jdbc:mysql://"+url, username, pw);
        //3.操作数据库
        Statement stmt = mysql.conn.createStatement();
        //4.检查表是否存在,不存在就创建
        try {
            stmt.execute("CREATE DATABASE IF NOT EXISTS `"+dbName+"`");
            stmt.execute("USE `"+dbName+"`");
            stmt.execute("CREATE TABLE IF NOT EXISTS `"+tbName+"`(name CHAR(50) PRIMARY KEY,locked BOOLEAN,datas BLOB)");
        }catch (Exception e){
            Logger.Log("获取mysql对象时出错");
            e.printStackTrace();
        }
        stmt.close();
        return mysql;
    }
    public void onDisable(){
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public synchronized boolean isLocked(String playername)throws  Exception{
        Player player= Bukkit.getPlayerExact(playername);

        return isLocked(player);
    }
    public synchronized boolean isLocked(Player player)throws  Exception{
        Validate.notNull(player);
        Statement stmt=conn.createStatement();
        String name=player.getName();
        stmt.execute("USE `"+dbName+"`");
        ResultSet rs=stmt.executeQuery("SELECT locked FROM `"+tbName+"` WHERE name='"+name+"'");
        Validate.isTrue(rs.next());
        boolean boolRes=rs.getBoolean("locked");
        rs.close();
        stmt.close();
        return boolRes;
    }
    public synchronized byte[] getPlayerData(String playername) throws  Exception{
        Player player=Bukkit.getPlayerExact(playername);
        return getPlayerData(player);
    }
    public synchronized byte[] getPlayerData(Player player)throws Exception{
        Validate.notNull(player);
        Statement stmt=conn.createStatement();
        String name=player.getName();
        stmt.execute("USE `"+dbName+"`");
        ResultSet rs=stmt.executeQuery("SELECT datas FROM `"+tbName+"` WHERE name='"+name+"'");
        Validate.isTrue(rs.next());
        Blob blob =rs.getBlob("datas");
        byte[] res=blob.getBytes(1, Math.toIntExact(blob.length()));
        rs.close();
        stmt.close();
        return res;
    }
    public boolean update(User pUser) throws SQLException {
        //this.mLock.lock();

            PreparedStatement tStatement = conn.prepareStatement("REPLACE INTO "
                    + tbName + " (" + User.COL_NAME + "," + User.COL_LOCK + "," + User.COL_DATA + ") "
                    + "VALUES (?,?,?)");
            tStatement.setString(1, pUser.getPlayer().getName());
            tStatement.setBoolean(2, pUser.isLocked());
            tStatement.setBytes(3, pUser.getData());
            return tStatement.executeUpdate() != 0;

    }
    private Mysql(){

    }

}
