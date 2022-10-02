package me.TrixxTraxx.OneBlock;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import me.TrixxTraxx.OneBlock.Commands.*;
import me.TrixxTraxx.OneBlock.Island.OneBlockIsland;
import me.TrixxTraxx.OneBlock.Listener.PlayerListener;
import me.TrixxTraxx.OneBlock.Listener.PlaytimeRunnable;
import me.TrixxTraxx.OneBlock.Listener.WorldUnloadRunnable;
import me.TrixxTraxx.OneBlock.sql.SQL;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public final class OneBlock extends JavaPlugin
{
    public static OneBlock Instance;
    public static FileConfiguration Config;
    public static SlimeLoader WorldLoader;
    public static SlimePlugin Slime;
    @Override
    public void onEnable()
    {
        Instance = this;
        Config = getConfig();
        Slime = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        WorldLoader = Slime.getLoader("mysql");

        saveDefaultConfig();

        // Plugin startup logic
        SQL.Instance.init(
                Config.getString("sql.host"),
                Config.getString("sql.port"),
                Config.getString("sql.database"),
                Config.getString("sql.username"),
                Config.getString("sql.password")
        );

        getLogger().setLevel(Level.ALL);

        OneBlockIsland.cacheAll();
        new PlaytimeRunnable().runTaskTimerAsynchronously(this, 0, 20);
        new WorldUnloadRunnable().runTaskTimer(this, 0, 20*30);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        var cmd = new CommandIs();
        getCommand("is").setExecutor(cmd);
        cmd.registerSubCommand(new SubCommandBackupCreate());
        cmd.registerSubCommand(new SubCommandBackupDelete());
        cmd.registerSubCommand(new SubCommandBackupLoad());
        cmd.registerSubCommand(new SubCommandCreate());
        cmd.registerSubCommand(new SubCommandDelete());
        cmd.registerSubCommand(new SubCommandInvite());
        cmd.registerSubCommand(new SubCommandInvites());
        cmd.registerSubCommand(new SubCommandJoin());
        cmd.registerSubCommand(new SubCommandKick());
        cmd.registerSubCommand(new SubCommandQuit());
        cmd.registerSubCommand(new SubCommandSpectate());
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
    }

    public static void log(Level lvl, String msg)
    {
        Instance.getLogger().log(lvl, msg);
    }
}
