package me.TrixxTraxx.OneBlock.Commands;

import me.TrixxTraxx.OneBlock.Island.MemberRole;
import me.TrixxTraxx.OneBlock.Island.OneBlockIsland;
import me.TrixxTraxx.OneBlock.OneBlockPlayer;

public class SubCommandKick extends SubCommandIsland
{
    public SubCommandKick() {
        super("kick");
    }

    @Override
    public void execute(OneBlockIsland island, OneBlockPlayer player, String[] args)
    {
        if(island.getMember(player).getRole() != MemberRole.Admin){
            island.broadCast("kick.noPermission");
            return;
        }
        if(args.length == 0)
        {
            island.broadCast("kick.noPlayer");
            return;
        }
        OneBlockPlayer target = OneBlockPlayer.get(x -> x.getPlayer().getName().equalsIgnoreCase(args[0]));
        if(target == null)
        {
            island.broadCast("kick.notFound");
            return;
        }
        island.kickMember(target);
        island.broadCast("kick.success", x -> target.getPlayer().getName());
    }
}
