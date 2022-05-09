package me.yoursole.smpplugin;

import jdk.jpackage.internal.Log;
import me.yoursole.smpplugin.config.BotDataManager;
import me.yoursole.smpplugin.config.ConfigManager;
import me.yoursole.smpplugin.config.PluginDataManager;
import me.yoursole.smpplugin.data.BotData;
import me.yoursole.smpplugin.data.DataManager;
import me.yoursole.smpplugin.data.PluginData;
import me.yoursole.smpplugin.discord.Bot;
import me.yoursole.smpplugin.events.BlockBreak;
import me.yoursole.smpplugin.events.ChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public final class SMPPlugin extends JavaPlugin {
    //add transfer item command in discord
    //add link minecraft account in discord
    //add command to view graph of blocks broken
    @Override
    public void onEnable() {
        setupDataManagers();
        setupBot();
        setupPlugin(this);
        try {
            loadData();
        } catch (IOException | ClassNotFoundException e) {
            Bukkit.getLogger().log(new LogRecord(Level.SEVERE, "Bot or Plugin Data Failed to load -- EXITING"));
            System.exit(-1);
        }
    }

    private static void loadData() throws IOException, ClassNotFoundException {
        if(BotDataManager.get().getString("Bot_Data") == null){
            DataManager.botData = new BotData();
            return;
        }

        DataManager.botData = new BotData(BotDataManager.get().getString("Bot_Data"));

        if(PluginDataManager.get().getString("Plugin_Data") == null){
            DataManager.pluginData = new PluginData();
            return;
        }

        DataManager.pluginData = new PluginData(PluginDataManager.get().getString("Plugin_Data"));
    }

    private static void setupDataManagers(){
        BotDataManager.setup();
        BotDataManager.save();

        PluginDataManager.setup();
        PluginDataManager.save();

        ConfigManager.setup();
        ConfigManager.get().addDefault("Bot_Token", 0);
        ConfigManager.get().addDefault("Bot_Channel","");
        ConfigManager.get().options().copyDefaults(true);
        ConfigManager.save();
    }

    private static void setupBot(){
        String token = ConfigManager.get().getString("Bot_Token");
        try {
            Bot.bringOnline(token);
        } catch (LoginException ignored){}
    }

    private static void setupPlugin(JavaPlugin p){
        p.getServer().getPluginManager().registerEvents(new ChatEvent(), p);
        p.getServer().getPluginManager().registerEvents(new BlockBreak(), p);
    }

    @Override
    public void onDisable() {
        String botData = null;
        try {
            botData = DataManager.botData.serialize();
        } catch (IOException ignored){}

        BotDataManager.get().set("Bot_Data",botData);
        BotDataManager.save();

        String pluginData = null;
        try {
            pluginData = DataManager.pluginData.serialize();
        } catch (IOException ignored){}

        PluginDataManager.get().set("Plugin_Data",pluginData);
        PluginDataManager.save();
    }
}
