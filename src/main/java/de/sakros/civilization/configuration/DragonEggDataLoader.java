package de.sakros.civilization.configuration;

import de.sakros.civilization.Civilization;
import de.sakros.civilization.reinforcement.ReinforcedBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DragonEggDataLoader {

    static File dataFile;
    static YamlConfiguration config;
    static String pathToData = "dragonegg";

    public static void CreateFile() {
        config = GetFile(((Civilization)Civilization.getPlugin(Civilization.class)).getDataFolder() + "/dragonegg.yml");
        dataFile = new File(((Civilization)Civilization.getPlugin(Civilization.class)).getDataFolder() + "/dragonegg.yml");
        if (!dataFile.exists())
            try {
                dataFile.createNewFile();
                removeData();
            } catch (IOException iOException) {}
    }

    public static void SaveData(Block block, Player player){
        CreateFile();
        config = GetFile(((Civilization)Civilization.getPlugin(Civilization.class)).getDataFolder() + "/dragonegg.yml");
        config.set(pathToData + ".world", block.getLocation().getWorld().getName());
        config.set(pathToData + ".x", block.getLocation().getX());
        config.set(pathToData + ".y", block.getLocation().getY());
        config.set(pathToData + ".z", block.getLocation().getZ());
        config.set(pathToData + ".owner", player.getUniqueId().toString());
        try {
            config.save(dataFile);
        } catch (IOException iOException) {}
    }

    public static boolean EggExists(){
        if(getProperty("world").equals("NULL")){
            return false;
        }
        return true;
    }

    public static void setProperty(String propertyName, Object value){
        config = GetFile(((Civilization)Civilization.getPlugin(Civilization.class)).getDataFolder() + "/dragonegg.yml");
        config.set(pathToData + "." + propertyName, value);
        try {
            config.save(dataFile);
        } catch (IOException iOException) {}
    }

    public static String getProperty(String propertyName){
        config = GetFile(((Civilization)Civilization.getPlugin(Civilization.class)).getDataFolder() + "/dragonegg.yml");
        return (String)config.get(pathToData + "." + propertyName);
    }

    public static Location getLocation(){
        config = GetFile(((Civilization)Civilization.getPlugin(Civilization.class)).getDataFolder() + "/dragonegg.yml");
        return new Location(getWorld(), Double.parseDouble(getProperty("z")),
        Double.parseDouble(getProperty("z")), Double.parseDouble(getProperty("z")));
    }

    public static World getWorld(){
        config = GetFile(((Civilization)Civilization.getPlugin(Civilization.class)).getDataFolder() + "/dragonegg.yml");
        return Bukkit.getWorld((String) config.get(pathToData + ".world"));
    }

    public static String getOwner(){
        config = GetFile(((Civilization)Civilization.getPlugin(Civilization.class)).getDataFolder() + "/dragonegg.yml");
        return (String)config.get(pathToData + ".owner");
    }

    public static void removeData(){
        CreateFile();
        config = GetFile(((Civilization)Civilization.getPlugin(Civilization.class)).getDataFolder() + "/dragonegg.yml");
        config.set(pathToData + ".world", "NULL");
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
