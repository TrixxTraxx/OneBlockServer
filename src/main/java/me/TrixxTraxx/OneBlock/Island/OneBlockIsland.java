package me.TrixxTraxx.OneBlock.Island;

import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import me.TrixxTraxx.Linq.List;
import me.TrixxTraxx.OneBlock.Language;
import me.TrixxTraxx.OneBlock.OneBlock;
import me.TrixxTraxx.OneBlock.OneBlockPlayer;
import me.TrixxTraxx.OneBlock.Placeholder;
import me.TrixxTraxx.OneBlock.sql.SQL;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class OneBlockIsland
{
    private static List<OneBlockIsland> islands = new List<>();
    private int sqlId;
    private String name;
    private IslandWorld isWorld;

    private List<IslandBackup> backups;
    private List<IslandMember> members;

    public OneBlockIsland(String name, List<IslandBackup> backups, List<IslandMember> members, int sqlId)
    {
        this.name = name;
        this.backups = backups;
        this.members = members;
        this.sqlId = sqlId;
        isWorld = new IslandWorld(String.valueOf(sqlId), "0");
    }

    public int getSqlId()
    {
        return sqlId;
    }

    public String getName(){
        return name;
    }

    public IslandWorld getIslandWorld()
    {
        return isWorld;
    }

    public List<IslandBackup> getBackups()
    {
        return backups;
    }

    public IslandMember getMember(OneBlockPlayer pl)
    {
        OneBlock.log(Level.INFO, "Getting member for player " + pl.getPlayer().getName() + "\npolling from " + members.size() + " members");
        return members.find(x -> x.getName().equalsIgnoreCase(pl.getPlayer().getName()));
    }

    public List<IslandMember> getMembers()
    {
        return members;
    }

    public void kickMember(OneBlockPlayer pl)
    {
        IslandMember member = getMember(pl);
        if(member != null)
        {
            members.remove(member);
            SQL.Instance.kickMember(this, pl);
        }
    }

    public void setMemberRole(OneBlockPlayer pl, MemberRole r)
    {
        IslandMember member = getMember(pl);
        if(member != null)
        {
            member.setRole(r);
            SQL.Instance.updateIslandMemberRole(this, pl, r);
        }
    }

    public IslandBackup createBackup(String name)
    {
        IslandBackup backup = SQL.Instance.createBackup(this, name);
        backups.add(backup);
        broadCast("Island.Backup.Created", x -> x.replace("{name}", name));
        return backup;
    }

    public void loadBackup(IslandBackup backup){

    }

    public IslandMember invite(OneBlockPlayer inviter, OneBlockPlayer invited)
    {
        IslandMember member = SQL.Instance.invitePlayer(this, invited);
        members.add(member);
        broadCast("Island.Invited", x -> x.replace("{inviter}", inviter.getPlayer().getName()).replace("{invited}", invited.getPlayer().getName()));
        return member;
    }

    public void addMember(IslandMember member)
    {
        members.add(member);
    }

    public void delete()
    {
        SQL.Instance.deleteIsland(this);
        islands.remove(this);
    }

    public List<World> getLoadedWorlds()
    {
        List<World> worlds = new List<>();
        for (World w : Bukkit.getWorlds())
        {
            if(w.getName().startsWith(String.valueOf(sqlId)))
                worlds.add(w);
        }
        return worlds;
    }

    public void broadCast(String msg)
    {
        Language.sendMessages(getOnlinePlayers().cast(x -> x.getPlayer()), msg);
    }

    public void broadCast(String msg, Placeholder... placeholders)
    {
        Language.sendMessages(getOnlinePlayers().cast(x -> x.getPlayer()), msg, new List<>(placeholders));
    }

    public List<Player> getOnlinePlayers()
    {
        //return all players online in all Loaded worlds that are in survival mode
        List<Player> players = new List<>();
        for (World w : getLoadedWorlds())
        {
            for (Player p : w.getPlayers())
            {
                if(p.getGameMode().equals(org.bukkit.GameMode.SURVIVAL)) players.add(p);
            }
        }
        return players;
    }

    public List<Player> getOnlineSpecs()
    {
        //return all players online in all Loaded worlds that are in spectator mode
        List<Player> players = new List<>();
        for (World w : getLoadedWorlds())
        {
            for (Player p : w.getPlayers())
            {
                if(p.getGameMode().equals(org.bukkit.GameMode.SPECTATOR)) players.add(p);
            }
        }
        return players;
    }

    public SlimeWorld getWorld(World.Environment env)
    {
        String name;
        switch (env){
            case NORMAL:
                name = isWorld.getOverWorldName();
                break;
            case NETHER:
                name = isWorld.getNetherWorldName();
                break;
            case THE_END:
                name = isWorld.getEndWorldName();
                break;
            default:
                return null;
        }
        //check if the world is loaded
        World w = getLoadedWorlds().find(x -> x.getName().equalsIgnoreCase(name));
        SlimeWorld sw = OneBlock.Slime.getWorld(name);;
        if(w == null)
        {
            SlimePropertyMap properties = new SlimePropertyMap();

            properties.setValue(SlimeProperties.DIFFICULTY, Difficulty.HARD.toString());
            properties.setValue(SlimeProperties.SPAWN_X, 64);
            properties.setValue(SlimeProperties.SPAWN_Y, 64);
            properties.setValue(SlimeProperties.SPAWN_Z, 64);
            properties.setValue(SlimeProperties.ENVIRONMENT, env.toString());

            try {
                //if not load it
                sw = OneBlock.Slime.loadWorld(OneBlock.WorldLoader, name, false, properties);

                OneBlock.Slime.generateWorld(sw);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return sw;
    }

    public static OneBlockIsland getIsland(String name)
    {
        for (OneBlockIsland island : islands)
        {
            if (island.name.equals(name)) return island;
        }
        return null;
    }

    public static List<OneBlockIsland> getAll()
    {
        return islands;
    }

    public static void cacheAll()
    {
        islands = SQL.Instance.getIslands();
    }

    public static void create(OneBlockPlayer pl, String name)
    {
        OneBlockIsland island = SQL.Instance.createIsland(name, pl);
        islands.add(island);
    }
}