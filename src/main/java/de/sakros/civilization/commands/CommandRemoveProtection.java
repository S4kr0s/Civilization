package de.sakros.civilization.commands;

import de.sakros.civilization.Static;
import de.sakros.civilization.Utils;
import de.sakros.civilization.configuration.ProtectionDataLoader;
import de.sakros.civilization.reinforcement.ReinforcedBlock;
import javafx.scene.paint.Color;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class CommandRemoveProtection implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            Location loc;
            if(strings.length <= 1){
                loc = player.getTargetBlock((Set<Material>) null, 10).getLocation();
            } else {
                loc = new Location(Bukkit.getWorld(strings[0]), Double.parseDouble(strings[1]), Double.parseDouble(strings[2]), Double.parseDouble(strings[3]));
            }
            if (ReinforcedBlock.list.get(loc) == null){
                Utils.SendMessageActionbar(player, "Keinen Schutz gefunden.");
                return true;
            }

            if(ReinforcedBlock.GetAtLocation(loc).GetOwner().equals(player.getUniqueId())){
                Material dropping = Material.COBBLESTONE;
                int breaks = ReinforcedBlock.GetAtLocation(loc).GetBreaksLeft();

                if(breaks <= Static.BREAKS_TIER1){
                    dropping = Material.COBBLESTONE;
                } else if (breaks <= Static.BREAKS_TIER2) {
                    dropping = Material.IRON_BLOCK;
                } else if (breaks <= Static.BREAKS_TIER3) {
                    dropping = Material.DIAMOND_BLOCK;
                } else if (breaks <= Static.BREAKS_TIER4) {
                    dropping = Material.OBSIDIAN;
                } else if (breaks <= Static.BREAKS_TIER5) {
                    dropping = Material.STICK;
                }

                player.getInventory().addItem(new ItemStack(dropping));
                ReinforcedBlock.GetAtLocation(loc).SetBreaksLeft(0);
                Utils.SendMessageActionbar(player, "Schutz erfolgreich entfernt..");
            } else {
                Utils.SendMessageActionbar(player, "Dieser Schutz wurde nicht von dir erstellt!");
            }
        }
        return true;
    }
}
