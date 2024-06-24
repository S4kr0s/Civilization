package de.sakros.civilization.listeners;

import de.sakros.civilization.configuration.ChunkDataLoader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ProtectionListener implements Listener {

    @EventHandler
    public static void onBlockBreak (BlockBreakEvent event){
        if(!ChunkDataLoader.GetPlayerTrust(event.getPlayer(), event.getBlock().getChunk())){
            event.setCancelled(true);
        }
    }
}
