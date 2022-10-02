package me.TrixxTraxx.OneBlock.Listener;

import me.TrixxTraxx.OneBlock.OneBlockPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener
{
    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        OneBlockPlayer.generate(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerJoinEvent e)
    {
        OneBlockPlayer player = OneBlockPlayer.get(e.getPlayer());
        if(player != null) player.dispose();
    }
}