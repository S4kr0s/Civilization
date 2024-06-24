package de.sakros.civilization.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class RemoveProtectionListener implements Listener {

    @EventHandler
    public static void onClick(PlayerInteractEvent event){
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        if(block == null){
            return;
        }

        if(event.getItem() == null){
            return;
        }

        if(event.getItem() != null && !event.getItem().getType().equals(Material.STICK)){
            return;
        }

        if(event.getAction() != Action.LEFT_CLICK_BLOCK){
            return;
        }

        player.performCommand("removeprotection");
        event.setCancelled(true);
    }
}
