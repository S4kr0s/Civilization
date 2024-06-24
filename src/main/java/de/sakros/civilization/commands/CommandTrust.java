package de.sakros.civilization.commands;

import de.sakros.civilization.Utils;
import de.sakros.civilization.configuration.ChunkDataLoader;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class CommandTrust implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player player = (Player)commandSender;
            if(new File(ChunkDataLoader.CoordinatesToPath((player.getLocation().getChunk()))).exists()){
                if(player.getServer().getOfflinePlayer(strings[0]).hasPlayedBefore()){
                    if(ChunkDataLoader.TrustPlayer(player.getServer().getOfflinePlayer(strings[0]),
                            ChunkDataLoader.CoordinatesToPath((player.getLocation().getChunk())))){
                        Utils.SendMessageSuccess(player, "Der Spieler " + strings[0] + " besitzt nun Protection-Rechte.");
                    } else {
                        Utils.SendMessageFailed(player,"Der Spieler " + strings[0] + " besitzt keine Protection-Rechte mehr.");
                    }
                } else {
                    Utils.SendMessageFailed(player, "Der Spieler existiert nicht.");
                }
            } else {
                Utils.SendMessageFailed(player, "Der Chunk ist nicht gesichert.");
            }
            return true;
        }
        return false;
    }
}
