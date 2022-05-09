package me.yoursole.smpplugin.discord;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.yoursole.smpplugin.config.ConfigManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.entity.Player;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Bot {
    private static JDABuilder builder;
    private static JDA jda;
    public static String t;

    public static void bringOnline(String token) throws LoginException {
        t = ConfigManager.get().getString("Bot_Channel");

        builder = JDABuilder.createDefault(token);
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.NONE);
        builder.setActivity(Activity.playing("Quacking SMP"));
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.addEventListeners(new ChatListener());
        jda = builder.build();
    }

    public static void sendMessage(String message, String playerName) throws IOException {
        String uuid = getUUID(playerName);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setImage("https://crafatar.com/avatars/"+uuid+"?overlay"); //doesn't work
        embedBuilder.setColor(Color.YELLOW);
        embedBuilder.addField(playerName, message,true);

        TextChannel textChannel = jda.getTextChannelById(t);
        if (textChannel != null) {
            textChannel.sendMessage(embedBuilder.build()).queue();
        }
    }

    public static void sendJoinMessage(Player p){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(p.isOp()?Color.CYAN:Color.YELLOW);
        embedBuilder.addField(p.getDisplayName() + " joined the game!","",true);

        TextChannel textChannel = jda.getTextChannelById(t);
        if (textChannel != null) {
            textChannel.sendMessage(embedBuilder.build()).queue();
        }
    }

    public static void sendLeaveMessage(Player p){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(p.isOp()?Color.CYAN:Color.YELLOW);
        embedBuilder.addField(p.getDisplayName() + " left the game.","",true);

        TextChannel textChannel = jda.getTextChannelById(t);
        if (textChannel != null) {
            textChannel.sendMessage(embedBuilder.build()).queue();
        }
    }

    public static String getUUID(String name) throws IOException {
        String url = "https://api.mojang.com/users/profiles/minecraft/"+name;
        String UUIDJson;
        try{
            UUIDJson = new Scanner(new URL(url).openStream(),"UTF-8").useDelimiter("\\A").next();
            JsonObject JsonFile = (JsonObject) JsonParser.parseString(UUIDJson);
            String uuid = JsonFile.get("id").toString();
            return uuid.replace("\"","");
        }catch(NoSuchElementException e){
            return null;
        }
    }
}
