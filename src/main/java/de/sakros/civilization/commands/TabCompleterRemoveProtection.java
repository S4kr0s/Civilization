package de.sakros.civilization.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TabCompleterRemoveProtection implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equals("removeprotection")) {
            if(commandSender instanceof Player){
                Player player = (Player) commandSender;
                if(strings.length <= 1){
                    List<String> list = new ArrayList<>();
                    list.add("world");
                    return list;
                } else if(strings.length == 2){
                    List<String> list = new ArrayList<>();
                    list.add(String.valueOf(player.getTargetBlock((Set<Material>) null, 10).getLocation().getX()));
                    return list;
                } else if(strings.length == 3){
                    List<String> list = new ArrayList<>();
                    list.add(String.valueOf(player.getTargetBlock((Set<Material>) null, 10).getLocation().getY()));
                    return list;
                } else if(strings.length == 4){
                    List<String> list = new ArrayList<>();
                    list.add(String.valueOf(player.getTargetBlock((Set<Material>) null, 10).getLocation().getZ()));
                    return list;
                } else {
                    List<String> list = new ArrayList<>();
                    return list;
                }
            }
        }
        return null;
    }
}
