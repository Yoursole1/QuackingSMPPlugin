package me.yoursole.smpplugin.events;

import me.yoursole.smpplugin.discord.Bot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        Player p = e.getPlayer();
        Bot.sendLeaveMessage(p);

        ChatColor c = p.isOp()?ChatColor.AQUA:ChatColor.YELLOW;
        e.setQuitMessage(c + p.getDisplayName() + " left the game.");
    }
}
