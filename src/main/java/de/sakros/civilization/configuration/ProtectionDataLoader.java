package de.sakros.civilization.configuration;

import de.sakros.civilization.Civilization;
import de.sakros.civilization.reinforcement.ReinforcedBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProtectionDataLoader {

    static File dataFile;
    static YamlConfiguration config;

    public static void CreateFile() {
        config = GetFile(((Civilization)Civilization.getPlugin(Civilization.class)).getDataFolder() + "/blockdata.yml");
        dataFile = new File(((Civilization)Civilization.getPlugin(Civilization.class)).getDataFolder() + "/blockdata.yml");
        if (!dataFile.exists())
            try {
                dataFile.createNewFile();
            } catch (IOException iOException) {}
    }

    public static void LoadFile(){
        CreateFile();
        config = GetFile(((Civilization)Civilization.getPlugin(Civilization.class)).getDataFolder() + "/blockdata.yml");
        for (String key : config.getConfigurationSection("world").getKeys(false)){
            String[] coords = key.split(",");
            Location location = new Location(Bukkit.getWorld("world"), Double.parseDouble(coords[0]),
                    Double.parseDouble(coords[1]), Double.parseDouble(coords[2]));

            ReinforcedBlock rblock = new ReinforcedBlock(location.getBlock(), config.getInt("world." + key + ".breaks"), UUID.fromString(config.getString("world." + key + ".owner")));
            ReinforcedBlock.list.put(location, rblock);
        }
        /*
        for (String key : config.getConfigurationSection("world_nether").getKeys(false)){
            System.out.println(config.getString(key + ".x"));
        }
        for (String key : config.getConfigurationSection("world_the_end").getKeys(false)){
            System.out.println(config.getString(key + ".x"));
        }
         */
    }

    public static void SaveFile(){
        CreateFile();
        config = GetFile(((Civilization)Civilization.getPlugin(Civilization.class)).getDataFolder() + "/blockdata.yml");
        for (Map.Entry<Location, ReinforcedBlock> entry : ReinforcedBlock.list.entrySet()) {
            Location location = entry.getKey();
            ReinforcedBlock block = entry.getValue();
            String pathToData = block.GetLocation().getWorld().getName() + "." + (int)block.GetLocation().getX() + "," + (int)block.GetLocation().getY() + "," + (int)block.GetLocation().getZ();
            config = GetFile(((Civilization)Civilization.getPlugin(Civilization.class)).getDataFolder() + "/blockdata.yml");
            config.set(pathToData + ".x", block.GetLocation().getX());
            config.set(pathToData + ".y", block.GetLocation().getY());
            config.set(pathToData + ".z", block.GetLocation().getZ());
            config.set(pathToData + ".owner", block.GetOwner());
            config.set(pathToData + ".breaks", block.GetBreaksLeft());
            config.set(pathToData + ".reinforced", true);
            try {
                config.save(dataFile);
            } catch (IOException iOException) {}
        }
    }

    public static void SaveProtection(ReinforcedBlock block){
        CreateFile();
        ReinforcedBlock.list.put(block.GetLocation(), block);
    }

    public static ReinforcedBlock LoadProtection(Location loc){
        return ReinforcedBlock.list.get(loc);
        /*
        String pathToData = loc.getWorld().getName() + "." + (int)loc.getX() + "," + (int)loc.getY() + "," + (int)loc.getZ();
        if(config.get(pathToData) != null){
            return new ReinforcedBlock(loc.getBlock(), config.getInt(pathToData + ".breaks"));
        }
        return null;
         */
    }

    public static boolean isReinforced(Location loc){
        ReinforcedBlock rblock = ReinforcedBlock.list.get(loc);
        if(ReinforcedBlock.list.get(loc) == null || ReinforcedBlock.list.get(loc).GetBreaksLeft() <= 0){
            return false;
        }
        /*
        config = GetFile(((Civilization)Civilization.getPlugin(Civilization.class)).getDataFolder() + "/blockdata.yml");
        String pathToData = loc.getWorld().getName() + "." + (int)loc.getX() + "," + (int)loc.getY() + "," + (int)loc.getZ();
        try{
            if(config.getBoolean(pathToData + ".reinforced")){
                return true;
            }
        } catch (NullPointerException exception) {
            return false;
        }
        */
        return false;
    }

    public static void setProperty(Location loc, String propertyName, Object value){
        config = GetFile(((Civilization)Civilization.getPlugin(Civilization.class)).getDataFolder() + "/blockdata.yml");
        String pathToData = loc.getWorld().getName() + "." + (int)loc.getX() + "," + (int)loc.getY() + "," + (int)loc.getZ();
        config.set(pathToData + "." + propertyName, value);
        try {
            config.save(dataFile);
        } catch (IOException iOException) {}
        if(propertyName.equals("breaks") && value.equals(0)){
            RemoveProtection(loc);
        }
    }

    public static String getProperty(Location loc, String propertyName){
        config = GetFile(((Civilization)Civilization.getPlugin(Civilization.class)).getDataFolder() + "/blockdata.yml");
        String pathToData = loc.getWorld().getName() + "." + (int)loc.getX() + "," + (int)loc.getY() + "," + (int)loc.getZ();
        return (String)config.get(pathToData + "." + propertyName);
    }

    public static void RemoveProtection(Location loc){
        String pathToData = loc.getWorld().getName() + "." + (int)loc.getX() + "," + (int)loc.getY() + "," + (int)loc.getZ();
        if(config.get(pathToData) != null){
            config.set(pathToData + ".reinforced", false);
            config.set(pathToData, null);
        }
        try {
            config.save(dataFile);
        } catch (IOException iOException) {}
    }

    public static YamlConfiguration GetFile(String pathToFile){
        if(new File(pathToFile).exists()){
            config = YamlConfiguration.loadConfiguration(new File(pathToFile));
            return config;
        }
        return null;
    }
}
