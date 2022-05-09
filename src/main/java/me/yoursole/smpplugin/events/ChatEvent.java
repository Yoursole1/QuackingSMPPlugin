package me.yoursole.smpplugin.events;

import me.yoursole.smpplugin.discord.Bot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.IOException;

public class ChatEvent implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) throws IOException {
        Bot.sendMessage(e.getMessage(),e.getPlayer().getName());
    }
}
