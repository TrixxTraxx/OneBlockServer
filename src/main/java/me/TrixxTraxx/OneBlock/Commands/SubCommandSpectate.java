package me.TrixxTraxx.OneBlock.Commands;

import me.TrixxTraxx.Linq.List;
import me.TrixxTraxx.OneBlock.Island.OneBlockIsland;
import me.TrixxTraxx.OneBlock.Language;
import me.TrixxTraxx.OneBlock.OneBlockPlayer;
import org.bukkit.entity.Player;

public class SubCommandSpectate extends SubCommand
{
    public SubCommandSpectate()
    {
        super("spectate");
    }

    @Override
    public void execute(Player player, String[] args)
    {
        if(args.length == 0) {
            Language.sendMessage(player, "spectate.noName");
            return;
        }
        String name = args[0];
        OneBlockPlayer obp = OneBlockPlayer.get(player);

        OneBlockIsland island = null;

        for (OneBlockIsland is:OneBlockIsland.getAll())
        {
            if(is.getMembers().any(x -> x.getName().equalsIgnoreCase(player.getName())))
            {
                island = is;
                break;
            }
        }

        if(island == null)
        {
            Language.sendMessage(player, "spectate.noIslandFound");
            return;
        }

        obp.spectate(island);
    }
}
