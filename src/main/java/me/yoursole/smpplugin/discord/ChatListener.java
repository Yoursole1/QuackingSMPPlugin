package me.yoursole.smpplugin.discord;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ChatListener extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent e){
        if(!e.getChannel().getId().equalsIgnoreCase(Bot.t))
            return;
        if(e.getAuthor().isBot())
            return;

        Bukkit.broadcastMessage(ChatColor.BLUE+e.getAuthor().getName()+": "+e.getMessage().getContentRaw());
    }
}
