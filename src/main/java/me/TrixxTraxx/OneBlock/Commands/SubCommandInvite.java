package me.TrixxTraxx.OneBlock.Commands;

import me.TrixxTraxx.OneBlock.Island.OneBlockIsland;
import me.TrixxTraxx.OneBlock.Language;
import me.TrixxTraxx.OneBlock.OneBlockPlayer;

public class SubCommandInvite extends SubCommandIsland
{
    public SubCommandInvite() {
        super("invite");
    }

    @Override
    public void execute(OneBlockIsland island, OneBlockPlayer player, String[] args)
    {
        if(args.length == 0)
        {
            Language.sendMessage(player.getPlayer(), "invite.noPlayer");
            return;
        }

        OneBlockPlayer target = OneBlockPlayer.get(x -> x.getPlayer().getName().equalsIgnoreCase(args[0]));
        if(target == null)
        {
            Language.sendMessage(player.getPlayer(), "invite.notFound");
            return;
        }
        island.invite(player, target);
    }
}
