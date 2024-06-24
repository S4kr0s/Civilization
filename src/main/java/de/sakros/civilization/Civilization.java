package de.sakros.civilization;

import de.sakros.civilization.commands.*;
import de.sakros.civilization.configuration.ChunkDataLoader;
import de.sakros.civilization.configuration.ConfigurationLoader;
import de.sakros.civilization.configuration.DragonEggDataLoader;
import de.sakros.civilization.configuration.ProtectionDataLoader;
import de.sakros.civilization.listeners.*;
import de.sakros.civilization.reinforcement.ReinforcedBlock;
import github.scarsz.discordsrv.DiscordSRV;
import me.angeschossen.lands.api.integration.LandsIntegration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public class Civilization extends JavaPlugin {

    public static LandsIntegration landsAddon;

    @Override
    public void onEnable() {
        ConfigurationLoader.LoadConfigurationFile();
        ChunkDataLoader.LoadDataFile();
        ProtectionDataLoader.LoadFile();
        DragonEggDataLoader.CreateFile();
        this.getCommand("visual-toggle").setExecutor(new CommandToggle());
        this.getCommand("removeprotection").setExecutor(new CommandRemoveProtection());
        this.getCommand("removeprotection").setTabCompleter(new TabCompleterRemoveProtection());
        this.getCommand("trust").setExecutor(new CommandTrust());
        getServer().getPluginManager().registerEvents(new Events(), this);
        getServer().getPluginManager().registerEvents(new RemoveProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new DragonEggListener(), this);
        //getServer().getPluginManager().registerEvents(new ChunkListener(), this);
        //getServer().getPluginManager().registerEvents(new ProtectionListener(), this);
        getLogger().info("CivilizationProtection has been enabled!");

        landsAddon = new LandsIntegration(this, false);
        getServer().getPluginManager().registerEvents(new TeamListener(), this);
        getLogger().info("CivilizationProtection hooked up with Lands! Let's start controlling over the world!");
    }

    @Override
    public void onDisable() {
        for (World w : Bukkit.getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (e instanceof org.bukkit.entity.ArmorStand &&
                        e.hasMetadata("isMarker"))
                    e.remove();
            }
        }
        ProtectionDataLoader.SaveFile();
        getLogger().info("CivilizationProtection has been disabled!");
    }
}
