package me.TrixxTraxx.OneBlock.Listener;

import me.TrixxTraxx.OneBlock.OneBlockPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaytimeRunnable extends BukkitRunnable
{
    @Override
    public void run()
    {
        for (OneBlockPlayer player : OneBlockPlayer.getPlayers())
        {
            player.addPlaytime(1);
        }
    }
}
