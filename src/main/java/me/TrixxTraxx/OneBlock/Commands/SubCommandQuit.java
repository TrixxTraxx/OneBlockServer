package me.TrixxTraxx.OneBlock.Commands;

import me.TrixxTraxx.OneBlock.Island.OneBlockIsland;
import me.TrixxTraxx.OneBlock.Language;
import me.TrixxTraxx.OneBlock.OneBlockPlayer;

public class SubCommandQuit extends SubCommandIsland
{
    public SubCommandQuit()
    {
        super("quit");
    }

    @Override
    public void execute(OneBlockIsland island, OneBlockPlayer player, String[] args)
    {
        island.kickMember(player);
        island.broadCast("quit.success.broadcast", x -> player.getPlayer().getName());
        Language.sendMessage(player.getPlayer(), "quit.success.player", x -> player.getPlayer().getName());
    }
}
