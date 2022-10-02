package me.TrixxTraxx.OneBlock.Listener;

import me.TrixxTraxx.OneBlock.OneBlockPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class IslandListener implements Listener
{
    @EventHandler
    public void onRespawn(PlayerSpawnLocationEvent event){
        OneBlockPlayer obp = OneBlockPlayer.get(event.getPlayer());
        if(obp != null)
        {
            if(obp.getCurrentIsland() != null)
            {
                //set the spawn location to the players personal spawn or island spawn...
                //event.setSpawnLocation(obp.getCurrentIsland());
            }
        }
    }
}
