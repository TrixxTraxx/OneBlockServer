package me.TrixxTraxx.OneBlock.Listener;

import com.grinderwolf.swm.api.SlimePlugin;
import me.TrixxTraxx.OneBlock.Island.OneBlockIsland;
import me.TrixxTraxx.OneBlock.OneBlock;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldUnloadRunnable extends BukkitRunnable
{
    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            if(
                    world.getPlayers().size() == 0 &&
                    OneBlock.Slime.getWorld(world.getName()) != null
            ) Bukkit.unloadWorld(world, true);
        }
    }
}
