package me.TrixxTraxx.OneBlock.Commands;

import me.TrixxTraxx.Linq.List;
import me.TrixxTraxx.OneBlock.Island.IslandMember;
import me.TrixxTraxx.OneBlock.Island.MemberRole;
import me.TrixxTraxx.OneBlock.Island.OneBlockIsland;
import me.TrixxTraxx.OneBlock.Language;
import me.TrixxTraxx.OneBlock.OneBlockPlayer;
import org.bukkit.entity.Player;

public class SubCommandJoin extends SubCommand
{
    public SubCommandJoin()
    {
        super("join");
    }

    @Override
    public void execute(Player player, String[] args)
    {
        OneBlockPlayer obp = OneBlockPlayer.get(player);
        String name = "";
        if(args.length == 0)
        {
            List<OneBlockIsland> islands = obp.getIslands();
            if(islands.size() == 0)
            {
                Language.sendMessage(player, "Island.NoIslands");
                return;
            }
            else if(islands.size() == 1)
            {
                name = islands.get(0).getName();
            }
            else
            {
                Language.sendMessage(player, "Island.MultipleIslands");
                return;
            }
        }

        String finalName = name;
        OneBlockIsland island = obp.getIslands().find(x -> x.getName().equalsIgnoreCase(finalName));

        if(island == null)
        {
            Language.sendMessage(player, "join.notFound");
            return;
        }

        IslandMember member = island.getMember(obp);
        if(member.getRole() == MemberRole.Invited)
        {
            island.setMemberRole(obp, MemberRole.Member);
            Language.sendMessage(player, "join.success");
            island.broadCast("join.broadcast", x -> x.replace("{player}", player.getName()));
        }
        else
        {
            island.broadCast("island.playerJoined");
        }

        obp.join(island);
    }
}
