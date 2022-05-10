package me.yoursole.smpplugin.events;

import me.yoursole.smpplugin.discord.Bot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        Bot.sendJoinMessage(p);

        ChatColor c = p.isOp()?ChatColor.AQUA:ChatColor.YELLOW;
        e.setJoinMessage(c + p.getDisplayName() + " joined the game.");
    }
}
