package me.TrixxTraxx.OneBlock;

import me.TrixxTraxx.Linq.List;
import me.TrixxTraxx.OneBlock.Island.IslandMember;
import me.TrixxTraxx.OneBlock.Island.MemberRole;
import me.TrixxTraxx.OneBlock.Island.OneBlockIsland;
import me.TrixxTraxx.OneBlock.sql.SQL;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class OneBlockPlayer
{
    private static List<OneBlockPlayer> players = new List<OneBlockPlayer>();

    private Player player;
    private List<OneBlockIsland> islands;
    private int sqlId;
    private int sessionTime = 0;
    private int playTime;
    private int deaths;

    private OneBlockIsland currentIsland;

    public OneBlockPlayer(Player player, List<OneBlockIsland> islands, int sqlId, int playTime, int deaths)
    {
        this.player = player;
        this.islands = islands;
        players.add(this);
        this.sqlId = sqlId;
        this.playTime = playTime;
        this.deaths = deaths;
    }

    public void dispose()
    {
        players.remove(this);
    }


    public List<OneBlockIsland> getInvites()
    {
        return islands.findAll(x -> x.getMember(this).getRole() == MemberRole.Invited);
    }

    public void addPlaytime(int time)
    {
        playTime += time;
        sessionTime += time;
    }

    public Player getPlayer()
    {
        return player;
    }

    public int getSqlId()
    {
        return sqlId;
    }

    public void toLobby()
    {
        player.teleport(OneBlock.Config.getLocation("Lobby"));
        if(currentIsland != null) {
            currentIsland.getMember(this).saveProfile();
            currentIsland = null;
        }
        player.setGameMode(GameMode.SURVIVAL);
        Language.sendMessage(player, "join.toLobby");
    }

    public void join(OneBlockIsland island)
    {
        if(!islands.contains(island))
        {
            player.sendMessage("You tried to join an island you are not a member of!");
            return;
        }
        if(currentIsland != null) currentIsland.getMember(this).saveProfile();


        Language.sendMessage(player, "join.toIsland");
        player.setGameMode(GameMode.SURVIVAL);

        IslandMember member = island.getMember(this);
        member.loadProfile(island);
        currentIsland = island;
    }

    public void spectate(OneBlockIsland island)
    {
        Language.sendMessage(player, "join.toSpectate");
        player.setGameMode(GameMode.SPECTATOR);
        currentIsland = null;

        var world = island.getWorld(World.Environment.NORMAL);
        player.teleport(new Location(Bukkit.getWorld(world.getName()), 0, 0, 0));
    }

    public OneBlockIsland getCurrentIsland()
    {
        return currentIsland;
    }

    public List<OneBlockIsland> getIslands(){
        return islands;
    }

    public static OneBlockPlayer get(Player player)
    {
        for(OneBlockPlayer p : players)
        {
            if(p.player == player)
                return p;
        }
        return null;
    }

    public static OneBlockPlayer get(Lambda func)
    {
        for(OneBlockPlayer p : players)
        {
            if(func.match(p)) return p;
        }
        return null;
    }

    public static interface Lambda
    {
        public boolean match(OneBlockPlayer player);
    }

    public static OneBlockPlayer generate(Player player)
    {
        OneBlockPlayer pl =  SQL.Instance.getOneBlockPlayer(player);
        if(pl == null)
        {
            pl = new OneBlockPlayer(player, new List<>(), -1, 0, 0);
        }
        players.add(pl);
        return pl;
    }

    public static List<OneBlockPlayer> getPlayers()
    {
        return players;
    }
}
