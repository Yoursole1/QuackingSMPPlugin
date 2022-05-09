package me.yoursole.smpplugin.discord;

import io.quickchart.QuickChart;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ChatListener extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent e){
        if(e.getMessage().getContentRaw().toCharArray()[0] == '?'){
            processCommand(e.getMessage().getContentRaw(), e.getAuthor());
            return;
        }
        if(!e.getChannel().getId().equalsIgnoreCase(Bot.t))
            return;
        if(e.getAuthor().isBot())
            return;

        Bukkit.broadcastMessage(ChatColor.BLUE+e.getAuthor().getName()+": "+e.getMessage().getContentRaw());
    }

    private void processCommand(String msg, User user){

    }
}
