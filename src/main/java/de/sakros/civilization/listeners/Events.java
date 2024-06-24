package de.sakros.civilization.listeners;


import java.util.*;

import de.sakros.civilization.Civilization;
import de.sakros.civilization.Static;
import de.sakros.civilization.Utils;
import de.sakros.civilization.commands.CommandToggle;
import de.sakros.civilization.configuration.ProtectionDataLoader;
import de.sakros.civilization.reinforcement.ReinforcedBlock;
import de.sakros.civilization.configuration.ConfigurationLoader;
import de.sakros.civilization.utility.LandsUtility;
import me.angeschossen.lands.api.player.LandPlayer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class Events implements Listener {

    @EventHandler
    public static void onClick(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        PlayerInventory i = p.getInventory();
        Material hand = i.getItemInMainHand().getType();
        Block b = event.getClickedBlock();

        if (b != null && b.getType().equals(Material.OBSIDIAN)) {
            return;
        }

        if (event.getAction() != Action.LEFT_CLICK_BLOCK || ReinforcedBlock.GetAtLocation(event.getClickedBlock().getLocation()) != null)
            return;

        if (!hand.equals(Material.COBBLESTONE) &&
                !hand.equals(Material.IRON_BLOCK) &&
                !hand.equals(Material.DIAMOND_BLOCK) &&
                !hand.equals(Material.OBSIDIAN) &&
                !hand.equals(Material.BEDROCK)) {
            return;
        }

        if(hand.equals(Material.IRON_BLOCK) && i.getItemInMainHand().getItemMeta().getDisplayName().contains("Slime")){
            Utils.SendMessageFailed(p, "Der SlimeChunkDetector kann nicht als Reinforcement-Material genutzt werden.");
            return;
        }

        if(!LandsUtility.canBuildHere(b.getLocation(), p)) {
            Utils.SendMessageFailed(p, "Du darfst hier leider nicht bauen.");
            return;
        }

        if(!ProtectionDataLoader.isReinforced(event.getClickedBlock().getLocation())){

            int breakProtectionCount;

            switch (hand){
                case COBBLESTONE:
                    breakProtectionCount = Static.BREAKS_TIER1;
                    break;
                case IRON_BLOCK:
                    breakProtectionCount = Static.BREAKS_TIER2;
                    break;
                case DIAMOND_BLOCK:
                    breakProtectionCount = Static.BREAKS_TIER3;
                    break;
                case OBSIDIAN:
                    breakProtectionCount = Static.BREAKS_TIER4;
                    break;
                case BEDROCK:
                    breakProtectionCount = Static.BREAKS_TIER5;
                    break;
                default:
                    return;
            }

        /*
        String materialName = event.getClickedBlock().getType().toString();

        for (String s : ConfigurationLoader.ConfigValues.get("reinforcement_not_reinforceable")) {
            if (materialName.contains(s) && !materialName.contains("BLOCK"))
                return;
        }
        */

            Utils.RemoveFromHand(i, hand, 1);
            Random r = new Random();
            Particle.DustOptions particle = new Particle.DustOptions(Color.GRAY, 0.5F);
            for (int j = 0; j < r.nextInt(10) + 30; j++) {
                Location l = b.getLocation().clone();
                l.add(r.nextDouble() * 2.0D - 0.5D, r.nextDouble() * 2.0D - 0.5D, r.nextDouble() * 2.0D - 0.5D);
                p.getWorld().spawnParticle(Particle.REDSTONE, l, 5, particle);
            }

            p.getWorld().playSound(b.getLocation(), Sound.BLOCK_GLASS_PLACE, 0.8F, 2.0F);
            ReinforcedBlock reinforced = new ReinforcedBlock(b, breakProtectionCount, event.getPlayer().getUniqueId());
            reinforced.createArmorStandIndicators(true);
            reinforced.displayBreaksLeft(p.getEyeLocation(), -1L);
            ProtectionDataLoader.SaveProtection(reinforced);
            Utils.SendMessageActionbar(p, "Block verstärkt!");
        } else {
            Utils.SendMessageActionbar(p, "Dieser Block ist bereits verstärkt!");
        }
        event.setCancelled(true);
    }

    static Map<Player, List<Block>> playerVisibleBlocks = new HashMap<>();

    @EventHandler
    public static void playerMove(PlayerMoveEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        Player p = event.getPlayer();
        Location loc = p.getLocation();
        List<Block> away = Utils.getNearbyBlocks(loc, 4);
        for (Block _b : away) {
            ReinforcedBlock b = ReinforcedBlock.GetAtLocation(_b.getLocation());
            if (b == null || !b.indicatorsVisible())
                continue;
            boolean otherPlayerSees = false;
            for (Map.Entry<Player, List<Block>> entry : playerVisibleBlocks.entrySet()) {
                for (Block bl : entry.getValue()) {
                    if (bl.equals(_b))
                        otherPlayerSees = true;
                }
            }
            if (otherPlayerSees)
                continue;
            b.removeAllIndicators(false);
            b.clearBreaksLeft(p.getEyeLocation(), false);
        }
        boolean playerInfoMode = false;
        for (Player pl : CommandToggle.playerToggle) {
            if (p.equals(pl)) {
                playerInfoMode = true;
                break;
            }
        }
        if (!event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STICK) && !playerInfoMode) {
            playerVisibleBlocks.remove(event.getPlayer());
            return;
        }
        List<Block> nearby = Utils.getNearbyBlocks(loc, 2);
        for (Block _b : nearby) {
            ReinforcedBlock b = ReinforcedBlock.GetAtLocation(_b.getLocation());
            if (b != null) {
                if (!b.indicatorsVisible())
                    b.createArmorStandIndicators(false);
                if (b.lastClearTime + 300L < System.currentTimeMillis()) {
                    b.clearBreaksLeft(p.getEyeLocation(), true);
                    b.displayBreaksLeft(p.getEyeLocation(), -1L);
                }
            }
            away.remove(_b);
        }
        playerVisibleBlocks.put(p, nearby);
    }

    @EventHandler
    public static void switchItem(PlayerItemHeldEvent event) {
        Player p = event.getPlayer();
        PlayerMoveEvent e = new PlayerMoveEvent(p, p.getLocation(), p.getLocation());
        playerMove(e);
    }

    @EventHandler
    public static void blockBreak(BlockBreakEvent event) {
        if(ProtectionDataLoader.isReinforced(event.getBlock().getLocation())){
            Material hand = event.getPlayer().getInventory().getItemInMainHand().getType();
            if (hand.equals(Material.COBBLESTONE) || hand.equals(Material.IRON_BLOCK) || hand.equals(Material.OBSIDIAN) || hand.equals(Material.BEDROCK)) {
                Utils.SendMessageFailed(event.getPlayer(), "Dieser Block ist bereits verstärkt!");
                event.setCancelled(true);
                return;
            }
        }
        Player p = event.getPlayer();
        ReinforcedBlock b = ReinforcedBlock.GetAtLocation(event.getBlock().getLocation());
        if (event.getBlock().getType() == Material.OBSIDIAN) {
            ProtectionDataLoader.RemoveProtection(event.getBlock().getLocation());
        }
        if (event.getBlock().getType() == Material.AIR) {
            ProtectionDataLoader.RemoveProtection(event.getBlock().getLocation());
        }
        if (b != null) {
            b.Break(p.getEyeLocation(), 1);
            if (b.GetBreaksLeft() > 0) {
                ItemStack hand = p.getInventory().getItemInMainHand();
                if (hand.getItemMeta() instanceof Damageable) {
                    Damageable meta = (Damageable)hand.getItemMeta();
                    meta.setDamage(meta.getDamage() + 1);
                    hand.setItemMeta((ItemMeta)meta);
                }
                p.updateInventory();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public static void onBlockExplode(BlockExplodeEvent event) {
        List<Block> affected = event.blockList();
        Location center = event.getBlock().getLocation();
        int maxDmg = ((Integer)ConfigurationLoader.ConfigValues.get("reinforcement_explosion_strength")).intValue();
        Iterator<Block> iter = affected.iterator();
        while (iter.hasNext()) {
            Block b = iter.next();
            if (b.getType() == Material.AIR || b.getType() == Material.CAVE_AIR)
                continue;
            ReinforcedBlock rb = ReinforcedBlock.GetAtLocation(b.getLocation());
            if (rb == null)
                continue;
            iter.remove();
            int calculatedDmg = (int)Math.floor(rb.GetLocation().distance(center));
            int damage = (int)Utils.clamp((maxDmg - calculatedDmg), 0.0F, maxDmg);
            if (rb.GetBreaksLeft() - damage <= 0) {
                rb.GetLocation().getWorld().dropItemNaturally(rb.GetLocation(), new ItemStack(rb.GetLocation().getBlock().getType(), 1));
                rb.GetLocation().getBlock().setType(Material.AIR);
            }
            rb.Break(center, damage, 40);
        }
    }

    @EventHandler
    public static void onEntityExplode(EntityExplodeEvent event) {
        List<Block> affected = event.blockList();
        Location center = event.getLocation();
        int maxDmg = ((Integer)ConfigurationLoader.ConfigValues.get("reinforcement_explosion_strength")).intValue();
        Iterator<Block> iter = affected.iterator();
        while (iter.hasNext()) {
            Block b = iter.next();
            if (b.getType() == Material.AIR || b.getType() == Material.CAVE_AIR)
                continue;
            ReinforcedBlock rb = ReinforcedBlock.GetAtLocation(b.getLocation());
            if (rb == null)
                continue;
            iter.remove();
            int calculatedDmg = (int)Math.floor(rb.GetLocation().distance(center));
            int damage = (int)Utils.clamp((maxDmg - calculatedDmg), 0.0F, maxDmg);
            if (rb.GetBreaksLeft() - damage <= 0) {
                rb.GetLocation().getWorld().dropItemNaturally(rb.GetLocation(), new ItemStack(rb.GetLocation().getBlock().getType(), 1));
                rb.GetLocation().getBlock().setType(Material.AIR);
            }
            rb.Break(center, damage, 40);
        }
    }

    @EventHandler
    public static void fireSpread(BlockIgniteEvent event) {
        Block to = event.getBlock();
        ReinforcedBlock rb = ReinforcedBlock.GetAtLocation(to.getLocation());
        boolean doFireSpread = ((Boolean)ConfigurationLoader.ConfigValues.get("reinforcement_blocks_can_burn")).booleanValue();
        if (rb != null &&
                !doFireSpread) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public static void fireDamage(BlockBurnEvent event) {
        Block to = event.getBlock();
        ReinforcedBlock rb = ReinforcedBlock.GetAtLocation(to.getLocation());
        boolean doFireSpread = ((Boolean)ConfigurationLoader.ConfigValues.get("reinforcement_blocks_can_burn")).booleanValue();
        if (rb != null) {
            event.setCancelled(true);
            if (!doFireSpread)
                return;
            rb.Break(rb.GetLocation(), 1, 40);
        }
    }
}
