package me.yoursole.smpplugin.events;

import me.yoursole.smpplugin.data.DataManager;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if(e.getPlayer().getGameMode() != GameMode.SURVIVAL)
            return;
        
        DataManager.pluginData.addBlockCount(e.getBlock().getType().name(), 1);
    }

}
