package me.TrixxTraxx.OneBlock.Commands;

import me.TrixxTraxx.OneBlock.Island.OneBlockIsland;
import me.TrixxTraxx.OneBlock.Language;
import me.TrixxTraxx.OneBlock.OneBlockPlayer;
import org.apache.commons.codec.language.bm.Lang;
import org.bukkit.entity.Player;

public class SubCommandCreate extends SubCommand
{
    public SubCommandCreate() {
        super("create");
    }

    @Override
    public void execute(Player player, String[] args)
    {
        String name = "New Island";
        if(args.length != 0)
        {
            name = args[0];
        }
        OneBlockPlayer pl = OneBlockPlayer.generate(player);
        OneBlockIsland.create(pl, name);
        Language.sendMessage(player, "island.created");
    }
}
