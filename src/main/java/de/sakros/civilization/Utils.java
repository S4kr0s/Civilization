package de.sakros.civilization;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class Utils {

    public static void SendMessageNormal(Player player, String message){
        String prefix = "&6[&eCivilization&6] &e";
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }

    public static void SendMessageSuccess(Player player, String message){
        String prefix = "&2[&aCivilization&2] &a";
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }

    public static void SendMessageFailed(Player player, String message){
        String prefix = "&4[&cCivilization&4] &c";
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }

    public static void SendMessageActionbar(Player player, String message){
        String prefix = "&2[&aCivilization&2] &a";
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', prefix + message)));
    }

    public static void SendBroadcast(String message){
        String prefix = "&5[&dCivilization&5] &d";
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }

    public static List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<>();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++)
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
            }
        }
        return blocks;
    }

    public static void RemoveFromHand(PlayerInventory i, Material item, int quantity) {
        ItemStack hand = i.getItemInMainHand();
        if (hand.getType().equals(item)) {
            hand.setAmount(hand.getAmount() - quantity);
            return;
        }
        byte b;
        int j;
        ItemStack[] arrayOfItemStack;
        for (j = (arrayOfItemStack = i.getContents()).length, b = 0; b < j; ) {
            ItemStack stack = arrayOfItemStack[b];
            if (stack != null && stack.getType() == item) {
                stack.setAmount(stack.getAmount() - quantity);
                break;
            }
            b++;
        }
    }

    static Material[] NeighborBlacklist = new Material[] { Material.WATER,
            Material.LAVA,
            Material.CAVE_AIR,
            Material.AIR,
            Material.FIRE };

    public static boolean HasNeighbor(Block b, Vector offset) {
        Location l = b.getLocation().clone();
        l.add(offset.getX(), offset.getY(), offset.getZ());
        Block neighbor = b.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        byte b1;
        int i;
        Material[] arrayOfMaterial;
        for (i = (arrayOfMaterial = NeighborBlacklist).length, b1 = 0; b1 < i; ) {
            Material m = arrayOfMaterial[b1];
            if (m.equals(neighbor.getType()))
                return false;
            b1++;
        }
        return true;
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    static Vector[] faceOffsets = new Vector[] { new Vector(0.8F, 0.0F, 0.0F),
            new Vector(-0.8F, 0.0F, 0.0F),
            new Vector(0.0F, 0.8F, 0.0F),
            new Vector(0.0F, -0.8F, 0.0F),
            new Vector(0.0F, 0.0F, 0.8F),
            new Vector(0.0F, 0.0F, -0.8F) };

    public static Vector GetClosestBlockFaceToLocationVisible(Location loc, Block b) {
        Location bCenter = b.getLocation();
        bCenter.add(0.5D, 0.5D, 0.5D);
        Vector closestFace = faceOffsets[0];
        double closestDist = Double.MAX_VALUE;
        for (int i = 0; i < 6; i++) {
            Vector offset = faceOffsets[i].clone();
            offset.multiply(1.25F);
            if (!HasNeighbor(b, offset)) {
                Location face = bCenter.clone();
                face.add(faceOffsets[i]);
                double dist = loc.distance(face);
                if (dist < closestDist) {
                    closestDist = dist;
                    closestFace = faceOffsets[i];
                }
            }
        }
        return closestFace;
    }
}