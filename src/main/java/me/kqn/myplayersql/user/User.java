package me.kqn.myplayersql.user;

import com.destroystokyo.paper.io.IOUtil;
import me.kqn.myplayersql.util.Logger;
import org.bukkit.entity.Player;
import sun.rmi.runtime.Log;


import java.io.*;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class User {
    public static final String COL_NAME = "name";
    public static final String COL_LOCK = "locked";
    public static final String COL_DATA = "datas";


    private Player mPlayer;
    /** 数据是否被锁定 */
    public boolean mLocked;
    /** 数据,key请保证小写 */
    private ConcurrentHashMap<String, byte[]> mData = new ConcurrentHashMap<>();
    /** {@link #mData}的数据序列化缓存 */
    private transient byte[] mDataCache = null;
    /***
     *序列化每一个模块的数据
     * 格式: 总共模块数(Int 4字节) - 模块1名字(字符串UTF8) - 模块1数据长度(Int) - 模块1数据(字节数组) - 模块2名字 - 模块2长度 - 模块2数据 .....
     */
    public User(Player player){
        this.mPlayer=player;
    }
    public byte[] getData(){
        if(this.mDataCache==null){
            ByteArrayOutputStream BAOStream=new ByteArrayOutputStream();
            DataOutputStream DOStream=null;
            try {
                DOStream=new DataOutputStream(new GZIPOutputStream(BAOStream));
                DOStream.writeInt(mData.size());
                for(Map.Entry<String,byte[]> entry:mData.entrySet()){
                    if(entry.getValue().length>0){
                        DOStream.writeUTF(entry.getKey().toLowerCase());
                        DOStream.writeInt(entry.getValue().length);
                        DOStream.write(entry.getValue());
                    }
                }

            }catch (Exception e){
                Logger.Log("序列化时出错");
                e.printStackTrace();
            }
            finally {
                try {
                    DOStream.close();
                    BAOStream.close();
                } catch (IOException e) {

                }
            }
            this.mDataCache=BAOStream.toByteArray();
        }
        return this.mDataCache;
    }
    /**反序列胡每一个模块
     * */
    public void setData(byte[] pData){
        this.mDataCache=pData;
        this.mData.clear();
        ByteArrayInputStream BAIStream=new ByteArrayInputStream(pData);
        DataInputStream DIStream=null;
        try {
            DIStream =new DataInputStream(new GZIPInputStream(BAIStream));
            int totalCount=DIStream.readInt();
            for(int i=0;i<totalCount;i++){
                String moduleName=DIStream.readUTF();
                int moduleSize=DIStream.readInt();
                byte[] moduleData=new byte[moduleSize];
                DIStream.readFully(moduleData);
                this.mData.put(moduleName,moduleData);
            }
        }catch (Exception e){
            Logger.Log("从SQL反序列化时出错");
        }finally {
            try {
                DIStream.close();
                BAIStream.close();
            } catch (IOException e) {

            }
        }

    }
    public boolean isLocked(){
        return mLocked;
    }
    /**
     * 获取模块序列化的数据,可以一编辑
     *
     * @return 模块序列化数据
     */
    public Map<String, byte[]> getDataMap() {

        return this.mData;
    }
    public Player getPlayer(){
        return mPlayer;

    }
}
