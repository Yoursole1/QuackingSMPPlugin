package me.yoursole.smpplugin.data;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class BotData implements Serializable {
    private final HashMap<String,String> discordIDtoMinecraftUUID;


    public BotData(){
        this.discordIDtoMinecraftUUID = new HashMap<>();
    }

    public BotData(String serialized) throws IOException, ClassNotFoundException {
        this.discordIDtoMinecraftUUID = ((BotData) deSerialize(serialized)).getDiscordIDtoMinecraftUUID();
    }

    public void addEntry(String discord, String minecraftUUID){
        this.discordIDtoMinecraftUUID.put(discord, minecraftUUID);
    }

    public void removeEntry(String discord){
        this.discordIDtoMinecraftUUID.remove(discord);
    }

    public String getMinecraft(String discord){
        return this.discordIDtoMinecraftUUID.get(discord);
    }
    public String getDiscord(String uuid){
        for(Map.Entry<String, String> entry : this.discordIDtoMinecraftUUID.entrySet()){
            if(entry.getValue().equalsIgnoreCase(uuid))
                return entry.getKey();
        }
        return null;
    }

    public HashMap<String, String> getDiscordIDtoMinecraftUUID(){
        return this.discordIDtoMinecraftUUID;
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
