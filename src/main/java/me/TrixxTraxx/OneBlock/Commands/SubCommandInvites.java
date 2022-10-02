package me.TrixxTraxx.OneBlock.Commands;

import me.TrixxTraxx.Linq.List;
import me.TrixxTraxx.OneBlock.Island.IslandMember;
import me.TrixxTraxx.OneBlock.Island.MemberRole;
import me.TrixxTraxx.OneBlock.Island.OneBlockIsland;
import me.TrixxTraxx.OneBlock.Language;
import me.TrixxTraxx.OneBlock.OneBlockPlayer;
import org.bukkit.entity.Player;

public class SubCommandInvites extends SubCommand
{
    public SubCommandInvites() {
        super("invites");
    }

    @Override
    public void execute(Player player, String[] args)
    {
        String invites = "";
        OneBlockPlayer pl = OneBlockPlayer.get(player);

        for (OneBlockIsland is:pl.getIslands())
        {
            IslandMember member = is.getMember(pl);
            if(member.getRole() == MemberRole.Invited)
            {
                invites += is.getName() + ", ";
            }
        }
        if(invites.isEmpty())
        {
            Language.sendMessage(player, "invites.noInvites");
            return;
        }
        else
        {
            String finalInvites = invites;
            Language.sendMessage(player, "invites.show", x -> x.replace("{invites}", finalInvites));
        }
    }
}
