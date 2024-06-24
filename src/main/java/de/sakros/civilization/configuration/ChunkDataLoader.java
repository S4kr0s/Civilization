package de.sakros.civilization.configuration;

import de.sakros.civilization.Civilization;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class ChunkDataLoader {
    static File dataFile;

    static YamlConfiguration config;

    public static void CreateFile(String fileName) {
        dataFile = new File((Civilization.getPlugin(Civilization.class)).getDataFolder() + "/chunks/" + fileName + ".yml");
        if (!dataFile.exists())
            try {
                dataFile.createNewFile();
            } catch (IOException iOException) {}
    }

    public static void LoadDataFile() {
    }

    public static void SaveDataToFile(Player player, Chunk chunk) {
        String fileName = chunk.getX() + ";" + chunk.getZ();
        CreateFile(fileName);
        dataFile = new File((Civilization.getPlugin(Civilization.class)).getDataFolder() + "/chunks/" + fileName + ".yml");
        config = YamlConfiguration.loadConfiguration(dataFile);
        config.set("chunk.world", chunk.getWorld().getName());
        config.set("chunk.x", chunk.getX());
        config.set("chunk.z", chunk.getZ());
        config.set("chunk.owner", player.getUniqueId().toString());
        config.set("chunk.trusted." + player.getUniqueId().toString(), true);
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

    public static String CoordinatesToPath(Chunk chunk){
        String fileName = chunk.getX() + ";" + chunk.getZ();
        return (Civilization.getPlugin(Civilization.class)).getDataFolder() + "/chunks/" + fileName + ".yml";
    }

    public static boolean TrustPlayer(Player player, String pathToFile){
        config = GetFile(pathToFile);
        player.sendMessage(config.get("chunk.trusted." + player.getUniqueId().toString()) + "");
        if(config.get("chunk.trusted." + player.getUniqueId().toString()) == null){
            player.sendMessage("Checkpoint reached.");
            config.set("chunk.trusted." + player.getUniqueId().toString(), true);
        } else {
            if((boolean)config.get("chunk.trusted." + player.getUniqueId().toString())) {
                config.set("chunk.trusted." + player.getUniqueId().toString(), false);
            } else if(!(boolean)config.get("chunk.trusted." + player.getUniqueId().toString())){
                config.set("chunk.trusted." + player.getUniqueId().toString(), true);
            }
        }
        try {
            config.save(new File(pathToFile));
        } catch (IOException iOException) {}
        return (boolean)config.get("chunk.trusted." + player.getUniqueId().toString());
    }

    public static boolean TrustPlayer(OfflinePlayer player, String pathToFile){
        if(new File(pathToFile).exists()){
            config = GetFile(pathToFile);
            if(config.get("chunk.trusted." + player.getUniqueId().toString()) == null){
                config.set("chunk.trusted." + player.getUniqueId().toString(), true);
            } else {
                if((boolean)config.get("chunk.trusted." + player.getUniqueId().toString())) {
                    config.set("chunk.trusted." + player.getUniqueId().toString(), false);
                } else if(!(boolean)config.get("chunk.trusted." + player.getUniqueId().toString())){
                    config.set("chunk.trusted." + player.getUniqueId().toString(), true);
                }
            }
            try {
                config.save(new File(pathToFile));
            } catch (IOException iOException) {}
            return (boolean)config.get("chunk.trusted." + player.getUniqueId().toString());
        } else {

            return false;
        }
    }

    public static boolean GetPlayerTrust(Player player, Chunk chunk){
        if (new File(CoordinatesToPath(chunk)).exists()){
            config = GetFile(CoordinatesToPath(chunk));
            if(config.get("chunk.trusted." + player.getUniqueId().toString()) != null){
                return (boolean) config.get("chunk.trusted." + player.getUniqueId().toString());
            }
            return false;
        }
        return true;
    }
}
