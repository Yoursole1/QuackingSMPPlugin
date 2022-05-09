package me.yoursole.smpplugin.data;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;

public class PluginData implements Serializable {
    private final HashMap<String, Integer> blocksBroken;
    private final HashMap<String, Integer> playtimeSec;
    public PluginData(){
        this.blocksBroken = new HashMap<>();
        this.playtimeSec = new HashMap<>();
    }

    public PluginData(String serialized) throws IOException, ClassNotFoundException {
        this.blocksBroken = ((PluginData) deSerialize(serialized)).getBlocksBroken();
        this.playtimeSec = ((PluginData) deSerialize(serialized)).getPlaytimeSec();
    }

    public HashMap<String, Integer> getBlocksBroken(){
        return this.blocksBroken;
    }
    public HashMap<String, Integer> getPlaytimeSec(){
        return this.playtimeSec;
    }

    public void addBlockCount(String key, int amount){
        if(this.blocksBroken.containsKey(key))
            this.blocksBroken.put(key, this.blocksBroken.get(key)+amount);
        else this.blocksBroken.put(key, amount);
    }

    public void addTime(String playerUUID, int amount){
        if(this.playtimeSec.containsKey(playerUUID))
            this.playtimeSec.put(playerUUID, this.playtimeSec.get(playerUUID)+amount);
        else this.playtimeSec.put(playerUUID, amount);
    }

    public int getBlockCount(String key){
        return this.blocksBroken.get(key);
    }
    public int getPlaytime(String playerUUID){
        return this.playtimeSec.get(playerUUID);
    }

    public String serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
    private Object deSerialize(String s) throws IOException, ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o  = ois.readObject();
        ois.close();
        return o;
    }
}
