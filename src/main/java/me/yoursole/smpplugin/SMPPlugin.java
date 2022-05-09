package me.yoursole.smpplugin;

import jdk.jpackage.internal.Log;
import me.yoursole.smpplugin.config.BotDataManager;
import me.yoursole.smpplugin.config.ConfigManager;
import me.yoursole.smpplugin.config.PluginDataManager;
import me.yoursole.smpplugin.data.PluginData;
import me.yoursole.smpplugin.discord.Bot;
import me.yoursole.smpplugin.events.ChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.util.Objects;

public final class SMPPlugin extends JavaPlugin {
    //add transfer item command in discord
    //add link minecraft account in discord
    //add command to view graph of blocks broken
    @Override
    public void onEnable() {
        setupDataManagers();
        setupBot();
        setupPlugin(this);
    }

    private static void setupDataManagers(){
        BotDataManager.setup();
        BotDataManager.get().addDefault("test", 0);
        BotDataManager.get().options().copyDefaults(true);
        BotDataManager.save();

        PluginDataManager.setup();
        PluginDataManager.get().addDefault("test", 1);
        PluginDataManager.get().options().copyDefaults(true);
        PluginDataManager.save();

        ConfigManager.setup();
        ConfigManager.get().addDefault("Bot_Token", 0);
        ConfigManager.get().options().copyDefaults(true);
        ConfigManager.save();
    }

    private static void setupBot(){
        String token = ConfigManager.get().getString("Bot_Token");
        System.out.println(token);
        try {
            Bot.bringOnline(token);
        } catch (LoginException ignored){}
    }

    private static void setupPlugin(JavaPlugin p){
        p.getServer().getPluginManager().registerEvents(new ChatEvent(), p);
    }

    @Override
    public void onDisable() {

    }
}
