package de.sakros.civilization.reinforcement;

import java.util.*;

import de.sakros.civilization.Civilization;
import de.sakros.civilization.Utils;
import de.sakros.civilization.configuration.ConfigurationLoader;
import de.sakros.civilization.configuration.ProtectionDataLoader;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

public class ReinforcedBlock {

    public static Material block;
    public static Map<Location, ReinforcedBlock> list = new HashMap<>();
    private Location blockLocation;
    private int breaksLeft = 1;
    private UUID owner;
    private List<ArmorStand> blockStands = new ArrayList<>();
    Vector[] offsets;
    List<ArmorStand> breakIndicators;
    public long lastClearTime;

    public ReinforcedBlock(Block b, int breaksLeft, UUID owner) {
        this.offsets = new Vector[] { new Vector(0.4F, 0.0F, 0.0F), new Vector(-0.4F, 0.0F, 0.0F), new Vector(0.0F, 0.4F, 0.0F), new Vector(0.0F, -0.4F, 0.0F), new Vector(0.0F, 0.0F, 0.4F), new Vector(0.0F, 0.0F, -0.4F) };
        this.breakIndicators = new ArrayList<>();
        this.lastClearTime = 0L;
        if (b.getType() == Material.AIR)
            return;
        World w = b.getLocation().getWorld();
        Vector pos = b.getLocation().toVector();
        Location loc = new Location(w, pos.getX(), pos.getY(), pos.getZ());
        this.blockLocation = loc;
        this.breaksLeft = breaksLeft;
        this.owner = owner;
        list.put(this.blockLocation, this);
        block = Material.COBBLESTONE;
    }

    public UUID GetOwner(){
        return this.owner;
    }

    public int GetBreaksLeft() {
        return this.breaksLeft;
    }

    public void SetBreaksLeft(int breaks) {
        ProtectionDataLoader.setProperty(this.blockLocation, "breaks", breaks);
        this.breaksLeft = breaks;
        if (this.breaksLeft <= 0) {
            GetLocation().getWorld().playSound(GetLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
            Dispose();
        }
    }

    public void Break(Location source, int times) {
        Break(source, times, 14);
    }

    public void Break(Location source, int times, int ticks) {
        SetBreaksLeft(GetBreaksLeft() - times);
        if (GetBreaksLeft() <= 0)
            return;
        clearBreaksLeft(source, true);
        if (!indicatorsVisible()) {
            displayBreaksLeft(source, ticks);
        } else {
            displayBreaksLeft(source, -1L);
        }
    }

    public void displayBreaksLeft(Location source, long ticks) {
        Location standLocation = this.blockLocation.clone();
        standLocation.add(0.5D, 0.10000000149011612D, 0.5D);
        Vector closest = Utils.GetClosestBlockFaceToLocationVisible(source, this.blockLocation.getBlock());
        standLocation.add(closest);
        ArmorStand stand = (ArmorStand)this.blockLocation.getWorld().spawn(standLocation, ArmorStand.class, new Consumer<ArmorStand>() {
            public void accept(ArmorStand t) {
                t.setGravity(false);
                t.setCanPickupItems(false);
                t.setVisible(false);
                t.setSilent(true);
                t.setInvulnerable(true);
                t.setMarker(true);
                t.setMarker(true);
                t.setSilent(true);
                t.setCanPickupItems(false);
                t.setMetadata("isMarker", (MetadataValue)new FixedMetadataValue((Plugin)Civilization.getPlugin(Civilization.class), Boolean.valueOf(true)));
            }
        });
        int maxBreaks = ((Integer)ConfigurationLoader.ConfigValues.get("reinforcement_break_counter")).intValue();
        float pct = this.breaksLeft / maxBreaks * 100.0F;
        ChatColor c = ChatColor.GREEN;
        if (pct < 65.0F && pct >= 35.0F)
            c = ChatColor.YELLOW;
        if (pct < 35.0F)
            c = ChatColor.RED;
        stand.setCustomName(c + "" + this.breaksLeft);
        stand.setCustomNameVisible(true);
        this.breakIndicators.add(stand);
        if (ticks == -1L)
            return;
        final ArmorStand finalStand = stand;
        final Location finalLoc = standLocation.clone();
        final long finalTicks = ticks;
        final Vector finalClosest = closest.clone();
        finalClosest.multiply(0.5F);
        final int repeating = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)Civilization.getPlugin(Civilization.class), new Runnable() {
            public void run() {
                finalLoc.subtract(finalClosest.getX() / finalTicks, finalClosest.getY() / finalTicks, finalClosest.getZ() / finalTicks);
                finalStand.teleport(finalLoc);
            }
        }, 0L, 1L);
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Civilization.getPlugin(Civilization.class), new Runnable() {
            public void run() {
                Bukkit.getScheduler().cancelTask(repeating);
                ReinforcedBlock.this.breakIndicators.remove(finalStand);
                finalStand.remove();
            }
        },  ticks);
    }

    public void clearBreaksLeft(Location src, boolean instant) {
        this.lastClearTime = System.currentTimeMillis();
        for (int i = 0; i < this.breakIndicators.size(); i++) {
            ArmorStand stand = this.breakIndicators.get(i);
            if (instant) {
                this.breakIndicators.remove(stand);
                stand.remove();
            } else {
                final ArmorStand finalStand = stand;
                final Location finalLoc = stand.getLocation().clone();
                final Vector finalClosest = Utils.GetClosestBlockFaceToLocationVisible(src, this.blockLocation.getBlock());
                final int repeating = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)Civilization.getPlugin(Civilization.class), new Runnable() {
                    public void run() {
                        finalLoc.subtract(finalClosest.getX() / 14.0D, finalClosest.getY() / 14.0D, finalClosest.getZ() / 14.0D);
                        finalStand.teleport(finalLoc);
                    }
                }, 10L, 1L);
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) Civilization.getPlugin(Civilization.class), new Runnable() {
                    public void run() {
                        Bukkit.getScheduler().cancelTask(repeating);
                        ReinforcedBlock.this.breakIndicators.remove(finalStand);
                        finalStand.remove();
                    }
                },  16L);
            }
        }
    }

    public void Dispose() {
        removeAllIndicators(true);
        clearBreaksLeft(null, true);
        list.remove(this.blockLocation);
        Random r = new Random();
        int chance = (int)(r.nextFloat() * 100.0F);
        int configChance = ((Integer)ConfigurationLoader.ConfigValues.get("reinforcement_block_drop_chance")).intValue();
        if (chance <= configChance)
            this.blockLocation.getWorld().dropItemNaturally(this.blockLocation, new ItemStack(block, 1));
        this.blockLocation = null;
    }

    public Location GetLocation() {
        return this.blockLocation;
    }

    public static ReinforcedBlock GetAtLocation(Location loc) {
        ReinforcedBlock result;
        result = list.get(loc);
        if(result == null){
            result = ProtectionDataLoader.LoadProtection(loc);
        }
        return result;
    }

    public void createArmorStandIndicators(boolean instant) {
        Location _loc = this.blockLocation.clone();
        final Block b = this.blockLocation.getBlock();
        _loc.add(new Vector(0.5F, -1.2F, 0.5F));
        ItemStack chest = new ItemStack(Material.BEDROCK, 1);
        if (GetBreaksLeft() <= 8){
            chest = new ItemStack(Material.COBBLESTONE, 1);
        } else if (GetBreaksLeft() <= 64) {
            chest = new ItemStack(Material.IRON_BLOCK, 1);
        } else if (GetBreaksLeft() <= 128) {
            chest = new ItemStack(Material.DIAMOND_BLOCK, 1);
        } else if (GetBreaksLeft() <= 256) {
            chest = new ItemStack(Material.OBSIDIAN, 1);
        }
        ArmorStand[] stands = new ArmorStand[6];
        for (int j = 0; j < 6; j++) {
            Location loc = _loc.clone();
            if (instant) {
                loc.add(this.offsets[j]);
                Vector offset = this.offsets[j].clone();
                offset.multiply(2.5F);
                if (Utils.HasNeighbor(b, offset))
                    continue;
            }
            ArmorStand stand = (ArmorStand)b.getLocation().getWorld().spawn(loc, ArmorStand.class, new Consumer<ArmorStand>() {
                public void accept(ArmorStand t) {
                    t.setGravity(false);
                    t.setCanPickupItems(false);
                    t.setVisible(false);
                    t.setSilent(true);
                    t.setInvulnerable(true);
                    t.setMarker(true);
                    t.setMetadata("isMarker", (MetadataValue)new FixedMetadataValue((Plugin)Civilization.getPlugin(Civilization.class), Boolean.valueOf(true)));
                }
            });
            EntityEquipment equip = stand.getEquipment();
            equip.setHelmet(chest);
            stands[j] = stand;
            continue;
        }
        this.blockStands.addAll(Arrays.asList(stands));
        if (instant)
            return;
        final ArmorStand[] finalStands = stands;
        final int timer = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)Civilization.getPlugin(Civilization.class), new Runnable() {
            public void run() {
                for (int i = 0; i < 6; i++) {
                    if (finalStands[i] != null) {
                        ArmorStand stand = finalStands[i];
                        Location loc = stand.getLocation().clone();
                        Vector offset = ReinforcedBlock.this.offsets[i].clone();
                        offset.multiply(0.1F);
                        loc.add(offset);
                        Vector offsetNeighbor = ReinforcedBlock.this.offsets[i].clone();
                        offsetNeighbor.multiply(2.5F);
                        if (!Utils.HasNeighbor(b, offsetNeighbor))
                            stand.teleport(loc);
                    }
                }
            }
        }, 0L, 1L);
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Civilization.getPlugin(Civilization.class), new Runnable() {
            public void run() {
                Bukkit.getScheduler().cancelTask(timer);
            }
        },  10L);
    }

    public void removeAllIndicators(boolean instant) {
        if (this.blockStands.size() <= 0)
            return;
        if (instant) {
            for (ArmorStand stand : this.blockStands) {
                if (stand != null)
                    stand.remove();
            }
            this.blockStands.clear();
            return;
        }
        final List<ArmorStand> stands = new ArrayList<>(this.blockStands);
        this.blockStands.clear();
        final int timer = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)Civilization.getPlugin(Civilization.class), new Runnable() {
            public void run() {
                for (int i = 0; i < 6; i++) {
                    if (stands.get(i) != null) {
                        ArmorStand stand = stands.get(i);
                        Location loc = stand.getLocation().clone();
                        Vector offset = ReinforcedBlock.this.offsets[i].clone();
                        offset.multiply(0.1F);
                        offset.multiply(-1);
                        loc.add(offset);
                        stand.teleport(loc);
                    }
                }
            }
        }, 0L, 1L);
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Civilization.getPlugin(Civilization.class), new Runnable() {
            public void run() {
                Bukkit.getScheduler().cancelTask(timer);
                for (ArmorStand stand : stands) {
                    if (stand != null)
                        stand.remove();
                }
            }
        },  6L);
    }

    public boolean indicatorsVisible() {
        if (this.blockStands.size() > 0)
            return true;
        return false;
    }
}
