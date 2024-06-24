package de.sakros.civilization.utility;

import de.sakros.civilization.Civilization;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.player.LandPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LandsUtility {
    public static boolean canBuildHere(Location location, Player p){
        LandsIntegration landsAddon = Civilization.landsAddon;
        LandPlayer landPlayer = landsAddon.getLandPlayer(p.getUniqueId());
        if(landsAddon.getLand(location) == null) {
            return true;
        }
        if(!landPlayer.getLands().contains(landsAddon.getLand(location))){
            return false;
        }
        return true;
    }
}
