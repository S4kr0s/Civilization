package de.sakros.civilization.commands;

import java.util.ArrayList;
import java.util.List;

import de.sakros.civilization.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandToggle implements CommandExecutor {
    public static List<Player> playerToggle = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player){
            Player p = (Player)commandSender;
            if (playerToggle.contains(p)) {
                playerToggle.remove(p);
                Utils.SendMessageNormal(p, "Visualisierung erfolgreich deaktiviert.");
            } else {
                playerToggle.add(p);
                Utils.SendMessageNormal(p, "Visualisierung erfolgreich aktiviert.");
            }
            return true;
        }
        return false;
    }
}
