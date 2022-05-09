package me.yoursole.smpplugin.data;

import me.yoursole.smpplugin.discord.Bot;
import net.dv8tion.jda.api.entities.User;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;

public class BotData implements Serializable {
    private HashMap<String,String> discordIDtoMinecraftUUID;


    public BotData(){
        this.discordIDtoMinecraftUUID = new HashMap<>();
    }

    public BotData(String serialized) throws IOException, ClassNotFoundException {
        this.discordIDtoMinecraftUUID = ((BotData) deSerialize(serialized)).getDiscordIDtoMinecraftUUID();
    }

    public void addEntry(String discord, String minecraftUUID){
        this.discordIDtoMinecraftUUID.put(discord, minecraftUUID);
    }

    public String getMinecraft(String discord){
        return this.discordIDtoMinecraftUUID.get(discord);
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
