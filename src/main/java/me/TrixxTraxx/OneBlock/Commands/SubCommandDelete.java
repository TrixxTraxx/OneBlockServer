package me.TrixxTraxx.OneBlock.Commands;

import me.TrixxTraxx.Linq.List;
import me.TrixxTraxx.OneBlock.Island.OneBlockIsland;
import me.TrixxTraxx.OneBlock.Language;
import me.TrixxTraxx.OneBlock.OneBlock;
import me.TrixxTraxx.OneBlock.OneBlockPlayer;
import org.apache.commons.codec.language.bm.Lang;
import org.bukkit.scheduler.BukkitRunnable;

public class SubCommandDelete extends SubCommandIsland
{
    private List<OneBlockPlayer> doubleCommand = new List<>();
    public SubCommandDelete() {
        super("delete");
    }

    @Override
    public void execute(OneBlockIsland island, OneBlockPlayer player, String[] args)
    {
        if(doubleCommand.contains(player))
        {
            island.delete();
            Language.sendMessage(player.getPlayer(), "island.deleted");
            doubleCommand.remove(player);
            return;
        }
        doubleCommand.add(player);
        Language.sendMessage(player.getPlayer(), "island.delete.confirm");
        new BukkitRunnable(){

            @Override
            public void run()
            {
                doubleCommand.remove(player);
            }
        }.runTaskLater(OneBlock.Instance, 20*10);
    }
}
