package me.TrixxTraxx.OneBlock.Commands;

import me.TrixxTraxx.OneBlock.Island.OneBlockIsland;
import me.TrixxTraxx.OneBlock.Language;
import me.TrixxTraxx.OneBlock.OneBlockPlayer;
import org.apache.commons.codec.language.bm.Lang;
import org.bukkit.entity.Player;

public abstract class SubCommandIsland extends SubCommand
{
    public SubCommandIsland(String name)
    {
        super(name);
    }

    @Override
    public void execute(Player player, String[] args)
    {
        OneBlockPlayer obp = OneBlockPlayer.get(player);
        OneBlockIsland island = obp.getCurrentIsland();
        if(island == null)
        {
            Language.sendMessage(player, "Island.NotOnIsland");
            return;
        }
        execute(island, obp, args);
    }

    public abstract void execute(OneBlockIsland island, OneBlockPlayer player, String[] args);
}
