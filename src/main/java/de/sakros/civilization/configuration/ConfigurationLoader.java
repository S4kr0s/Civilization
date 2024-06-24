package de.sakros.civilization.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import de.sakros.civilization.Civilization;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigurationLoader {
    public static Map<String, Object> ConfigValues = new HashMap<>();

    static File configFile;

    public static void CreateFile() {
        configFile = new File((Civilization.getPlugin(Civilization.class)).getDataFolder(), "config.yml");
        if (!configFile.exists())
            try {
                (Civilization.getPlugin(Civilization.class)).getDataFolder().mkdirs();
                configFile.createNewFile();
                InputStream def = (Civilization.getPlugin(Civilization.class)).getResource("config.yml");
                byte[] buffer = new byte[def.available()];
                def.read(buffer);
                FileOutputStream stream = new FileOutputStream(configFile);
                stream.write(buffer);
                stream.close();
            } catch (IOException iOException) {}
    }

    public static void LoadConfigurationFile() {
        if (configFile == null) {
            configFile = new File((Civilization.getPlugin(Civilization.class)).getDataFolder() + "/config.yml");
            if (!configFile.exists())
                CreateFile();
        }
        FileConfiguration config = (Civilization.getPlugin(Civilization.class)).getConfig();
        for (String item : config.getKeys(false)) {
            if (config.getString(item) != null)
                ConfigValues.put(item, config.get(item));
        }
    }
}
