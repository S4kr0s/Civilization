package de.sakros.civilization.listeners;

import de.sakros.civilization.Utils;
import de.sakros.civilization.configuration.DragonEggDataLoader;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.EmbedType;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class DragonEggListener implements Listener {

    @EventHandler
    public static void onBlockFromTo(BlockFromToEvent event){
        if(event.getBlock().getType().equals(Material.DRAGON_EGG)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public static void onPlayerInteract (PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        PlayerInventory inventory = event.getPlayer().getInventory();

        if((event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) && block != null && block.getType().equals(Material.DRAGON_EGG)){
            Utils.SendBroadcast("Das &5DRACHENEI&d wurde von " + player.getDisplayName() + " aufgesammelt!");
            for (Player onlinePlayer: player.getServer().getOnlinePlayers()) {
                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 1.0f, 1.0f);
            }
            block.setType(Material.AIR);
            if(inventory.firstEmpty() == -1){
                player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.DRAGON_EGG));
            } else {
                inventory.addItem(new ItemStack(Material.DRAGON_EGG));
            }
            if(DragonEggDataLoader.EggExists()){
                DragonEggDataLoader.removeData();
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public static void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();

        if(block.getType().equals(Material.DRAGON_EGG)){
            Utils.SendBroadcast("Das &5DRACHENEI&d wurde von " + player.getDisplayName() + " platziert!");
            for (Player onlinePlayer: player.getServer().getOnlinePlayers()) {
                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 1.0f, 1.0f);
            }
            DragonEggDataLoader.SaveData(block, player);
        }
    }
}
