package me.TrixxTraxx.OneBlock.sql;

import me.TrixxTraxx.Linq.List;
import me.TrixxTraxx.OneBlock.Island.MemberRole;
import me.TrixxTraxx.OneBlock.Island.IslandBackup;
import me.TrixxTraxx.OneBlock.Island.IslandMember;
import me.TrixxTraxx.OneBlock.Island.IslandWorld;
import me.TrixxTraxx.OneBlock.Island.OneBlockIsland;
import me.TrixxTraxx.OneBlock.OneBlock;
import me.TrixxTraxx.OneBlock.OneBlockPlayer;
import org.bukkit.entity.Player;

import java.sql.*;
import java.time.Instant;
import java.util.UUID;
import java.util.logging.Level;

public class SQL
{
    public static SQL Instance = new SQL();

    private SQL(){}

    private String host;
    private String port;
    private String database;
    private String username;
    private String password;

    private Connection con;

    private String[] createTable = {
            "CREATE TABLE IF NOT EXIST\n" +
                    "  `Island` (\n" +
                    "    `ID` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "    `created_at` timestamp NOT NULL DEFAULT current_timestamp(),\n" +
                    "    `Stage` varchar(255) NOT NULL DEFAULT 'Start',\n" +
                    "    `Cheats` text DEFAULT NULL,\n" +
                    "    `Playtime` int(11) NOT NULL DEFAULT 0,\n" +
                    "    `Deaths` int(11) NOT NULL DEFAULT 0,\n" +
                    "    `Name` varchar(255) DEFAULT NULL,\n" +
                    "    PRIMARY KEY (`ID`)\n" +
                    "  ) ENGINE = InnoDB AUTO_INCREMENT = 7 DEFAULT CHARSET = utf8mb4",
            "CREATE TABLE IF NOT EXIST\n" +
                    "  `IslandBackup` (\n" +
                    "    `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,\n" +
                    "    `created_at` timestamp NOT NULL DEFAULT current_timestamp(),\n" +
                    "    `Name` varchar(255) DEFAULT NULL,\n" +
                    "    `Island_ID` int(11) DEFAULT NULL,\n" +
                    "    PRIMARY KEY (`ID`),\n" +
                    "    KEY `IslandBackup_relation_1` (`Island_ID`),\n" +
                    "    CONSTRAINT `IslandBackup_relation_1` FOREIGN KEY (`Island_ID`) REFERENCES `Island` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION\n" +
                    "  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4",
            "CREATE TABLE IF NOT EXIST\n" +
                    "  `IslandMember` (\n" +
                    "    `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,\n" +
                    "    `Player_ID` int(11) NOT NULL,\n" +
                    "    `Island_ID` int(11) NOT NULL,\n" +
                    "    `Role` varchar(255) NOT NULL DEFAULT 'Member',\n" +
                    "    `Deaths` int(11) NOT NULL DEFAULT 0,\n" +
                    "    `Playtime` int(11) NOT NULL DEFAULT 0,\n" +
                    "    `x` double NOT NULL DEFAULT 0.5,\n" +
                    "    `y` double NOT NULL DEFAULT -63,\n" +
                    "    `z` double NOT NULL DEFAULT 0.5,\n" +
                    "    `Dimension` int(11) NOT NULL DEFAULT 1,\n" +
                    "    `Inventory` text DEFAULT NULL,\n" +
                    "    `SpawnX` double NOT NULL DEFAULT 0.5,\n" +
                    "    `SpawnY` double NOT NULL DEFAULT -63,\n" +
                    "    `SpawnZ` double NOT NULL DEFAULT 0.5,\n" +
                    "    `SpawnDimension` tinyint(2) NOT NULL DEFAULT 1,\n" +
                    "    PRIMARY KEY (`ID`),\n" +
                    "    KEY `IslandMember_relation_1` (`Island_ID`),\n" +
                    "    KEY `IslandMember_relation_2` (`Player_ID`),\n" +
                    "    CONSTRAINT `IslandMember_relation_1` FOREIGN KEY (`Island_ID`) REFERENCES `Island` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION,\n" +
                    "    CONSTRAINT `IslandMember_relation_2` FOREIGN KEY (`Player_ID`) REFERENCES `Player` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION\n" +
                    "  ) ENGINE = InnoDB AUTO_INCREMENT = 6 DEFAULT CHARSET = utf8mb4",
            "CREATE TABLE IF NOT EXIST\n" +
                    "  `Player` (\n" +
                    "    `ID` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "    `UUID` varchar(255) NOT NULL,\n" +
                    "    `Name` varchar(255) DEFAULT NULL,\n" +
                    "    `Deaths` int(11) NOT NULL DEFAULT 0,\n" +
                    "    `Playtime` int(11) NOT NULL DEFAULT 0,\n" +
                    "    PRIMARY KEY (`ID`)\n" +
                    "  ) ENGINE = InnoDB AUTO_INCREMENT = 2 DEFAULT CHARSET = utf8mb4"
    };

    public void init(String host, String port, String database, String username, String password)
    {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;

        try
        {
            connect();
            createTables();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    public boolean isConnected()
    {
        return con != null;
    }

    public void connect() throws SQLException
    {
        if(!isConnected())
        {
            con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false",
                    username,
                    password
            );
        }
    }

    public void handleNotConnected()
    {
        throw new IllegalStateException("Not connected to the database!");
    }

    public void createTables()
    {
        try
        {
            Statement statement = con.createStatement();
            for(String query : createTable)
            {
                statement.addBatch(query);
            }
            statement.executeBatch();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public List<OneBlockIsland> getIslands()
    {
        List<OneBlockIsland> islands = new List<>();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM `Island`");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                islands.add(getIsland(rs.getString("Name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return islands;
    }

    public OneBlockIsland getIsland(String name)
    {
        try
        {
            if(!isConnected()) handleNotConnected();

            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM `Island` WHERE `Name` = ?"
            );
            ps.setString(1, name);
            ps.executeQuery();
            ResultSet rs = ps.getResultSet();
            if(!rs.next()) return null;
            int id = rs.getInt("id");


            List<IslandBackup> backups = getBackups(id);
            List<IslandMember> members = getIslandMembers(id);

            return new OneBlockIsland(
                    rs.getString("Name"),
                    backups,
                    members,
                    id
            );
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private List<IslandBackup> getBackups(int IslandId)
    {
        try {
            PreparedStatement psBackup = con.prepareStatement(
                    "SELECT * FROM `IslandBackup`" +
                            "INNER JOIN `Island` ON `IslandBackup`.`Island_ID` = `Island`.`ID`" +
                            "WHERE `Island`.`ID` = ?"
            );
            psBackup.setInt(1, IslandId);
            psBackup.executeQuery();
            ResultSet rsBackup = psBackup.getResultSet();

            List<IslandBackup> backups = new List<IslandBackup>();
            while (rsBackup.next())
            {
                int id = rsBackup.getInt("ID");
                backups.add(new IslandBackup(
                        rsBackup.getTime("created_at"),
                        rsBackup.getString("Name"),
                        rsBackup.getInt("ID"),
                        IslandId
                ));
            }

            return backups;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return new List<>();
    }

    private List<IslandMember> getIslandMembers(int islandId)
    {
        try {
            PreparedStatement psMembers = con.prepareStatement(
                    "SELECT * FROM `IslandMember`" +
                    "INNER JOIN `Island` ON `IslandMember`.`Island_ID` = `Island`.`ID`" +
                    "WHERE `Island`.`ID` = ?"
            );
            psMembers.setInt(1, islandId);
            psMembers.executeQuery();
            ResultSet rsMembers = psMembers.getResultSet();

            List<IslandMember> members = new List<IslandMember>();

            PreparedStatement psPlayer = con.prepareStatement(
                    "SELECT * FROM `Player`" +
                    "INNER JOIN `IslandMember` ON `Player`.`ID` = `IslandMember`.`Player_ID`" +
                    "INNER JOIN `Island` ON `IslandMember`.`Island_ID` = `Island`.`ID`" +
                    "WHERE `Island`.`ID` = ?"
            );
            psPlayer.setInt(1, islandId);
            psPlayer.executeQuery();
            ResultSet rsPlayer = psPlayer.getResultSet();
            OneBlock.log(Level.INFO, "IslandId: " + islandId);

            List<PlayerCache> playerCache = new List<PlayerCache>();
            while(rsPlayer.next())
            {
                OneBlock.log(Level.INFO, "Adding player:" + rsPlayer.getString("Name"));
                playerCache.add(new PlayerCache(
                        rsPlayer.getInt("ID"),
                        rsPlayer.getString("Name"),
                        rsPlayer.getString("UUID")
                ));
            }

            while (rsMembers.next())
            {
                int id = rsMembers.getInt("Player_ID");
                PlayerCache player = playerCache.find(x -> x.sqlId == id);
                members.add(new IslandMember
                    (
                            rsMembers.getInt("ID"),
                            MemberRole.valueOf(rsMembers.getString("Role")),
                            player.name,
                            player.uuid
                    )
                );
            }
            return members;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new List();
    }

    private class PlayerCache
    {
        public int sqlId;
        public UUID uuid;
        public String name;

        public PlayerCache(int sqlId, String name, String uuid)
        {
            this.sqlId = sqlId;
            this.name = name;
            this.uuid = UUID.fromString(uuid);
        }
    }

    public OneBlockPlayer getOneBlockPlayer(Player pl)
    {
        try
        {
            if(!isConnected()) handleNotConnected();

            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM `Player` WHERE `UUID` = ?"
            );
            ps.setString(1, pl.getUniqueId().toString());
            ps.executeQuery();
            ResultSet rs = ps.getResultSet();
            if(!rs.next()) {
                //create a Player
                return createPlayer(pl);
            }

            int id = rs.getInt("ID");

            PreparedStatement psIslands = con.prepareStatement(
                    "SELECT * FROM `Island`" +
                    "INNER JOIN `IslandMember` ON `Island`.`ID` = `IslandMember`.`Island_ID`" +
                    "INNER JOIN `Player` ON `IslandMember`.`Player_ID` = `Player`.`ID`" +
                    "WHERE `Player`.`ID` = ?"
            );
            psIslands.setInt(1, id);
            psIslands.executeQuery();
            ResultSet rsIsland = psIslands.getResultSet();

            List<OneBlockIsland> islands = new List<>();
            while(rsIsland.next())
            {
                String Name = rsIsland.getString("Name");
                OneBlockIsland is = OneBlockIsland.getIsland(Name);
                if(is != null) islands.add(is);
            }

            return new OneBlockPlayer(
                pl,
                islands,
                id,
                rs.getInt("Playtime"),
                rs.getInt("Deaths")
            );
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public OneBlockPlayer createPlayer(Player pl)
    {
        //create a player and get the generated ID
        try {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO `Player` (`UUID`, `Name`) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, pl.getUniqueId().toString());
            ps.setString(2, pl.getName());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next())
            {
                int id = rs.getInt(1);
                return new OneBlockPlayer(
                    pl,
                    new List<>(),
                    id,
                    0,
                    0
                );
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public OneBlockIsland createIsland(String name, OneBlockPlayer owner)
    {
        try
        {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO `Island` (`Name`) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, name);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next())
            {
                int id = rs.getInt(1);
                OneBlockIsland is = new OneBlockIsland(
                        name,
                        new List<IslandBackup>(),
                        new List<IslandMember>(),
                        id
                );

                Statement st = con.createStatement();
                st.addBatch(
                        "INSERT INTO `IslandMember` (`Island_ID`, `Player_ID`, `Role`) VALUES ("+id+", "+owner.getSqlId()+", '"+MemberRole.Admin.toString()+"')"
                );
                st.addBatch(
                        "INSERT IGNORE INTO `worlds` (`name`, `world`)" +
                        "select '" + is.getIslandWorld().getOverWorldName() + "', world FROM `worlds` WHERE `name` = '" + OneBlock.Config.getString("defaultOverWorld") + "'"
                );
                st.executeBatch();

                //get the id of the new IslandMember
                PreparedStatement psMember = con.prepareStatement(
                        "SELECT * FROM `IslandMember` WHERE `Island_ID` = ? AND `Player_ID` = ?"
                );
                psMember.setInt(1, id);
                psMember.setInt(2, owner.getSqlId());
                psMember.executeQuery();
                ResultSet rsMember = psMember.getResultSet();
                if(rsMember.next())
                {
                    int memberId = rsMember.getInt("ID");
                    is.addMember(new IslandMember(
                            memberId,
                            MemberRole.Admin,
                            owner.getPlayer().getName(),
                            owner.getPlayer().getUniqueId()
                    ));
                }
                return is;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public IslandBackup createBackup(OneBlockIsland is, String Name)
    {
        try
        {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO `IslandBackup` (`Island_ID`, `Name`) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, is.getSqlId());
            ps.setString(2, Name);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next())
            {
                int id = rs.getInt(1);
                IslandWorld isWorld = is.getIslandWorld();
                IslandBackup backup = new IslandBackup
                (
                    (Time) Time.from(Instant.now()),
                    Name,
                    id,
                    is.getSqlId()
                );
                IslandWorld backupWorld = backup.getIslandWorld();

                //copy the worlds from the island to the backup
                Statement st = con.createStatement();
                st.addBatch(
                        "INSERT IGNORE INTO `worlds` (`name`, `world`)" +
                        "select '" + isWorld.getOverWorldName() + "', world FROM `worlds` WHERE `name` = " + backupWorld.getOverWorldName()
                );
                st.addBatch(
                        "INSERT IGNORE INTO `worlds` (`name`, `world`)" +
                        "select '" + isWorld.getNetherWorldName() + "', world FROM `worlds` WHERE `name` = " + backupWorld.getNetherWorldName()
                );
                st.addBatch(
                        "INSERT IGNORE INTO `worlds` (`name`, `world`)" +
                        "select '" + isWorld.getEndWorldName() + "', world FROM `worlds` WHERE `name` = " + backupWorld.getEndWorldName()
                );
                st.executeBatch();

                return backup;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void addDeath(OneBlockPlayer pl)
    {
        try
        {
            //make a batch to update the Island, IslandMember and Player "Deaths" property
            Statement stmt = con.createStatement();
            stmt.addBatch("UPDATE `Island` SET `Deaths` = `Deaths` + 1 WHERE `ID` = " + pl.getCurrentIsland().getSqlId());
            stmt.addBatch("UPDATE `IslandMember` SET `Deaths` = `Deaths` + 1 WHERE `Island_ID` = " + pl.getCurrentIsland().getMember(pl).getSqlId() + " AND `Player_ID` = " + pl.getSqlId());
            stmt.addBatch("UPDATE `Player` SET `Deaths` = `Deaths` + 1 WHERE `ID` = " + pl.getSqlId());
            stmt.executeBatch();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addPlaytime(OneBlockPlayer pl, int seconds)
    {
        try
        {
            //make a batch to update the Island, IslandMember and Player "Playtime" property
            Statement stmt = con.createStatement();
            stmt.addBatch("UPDATE `Island` SET `Playtime` = `Playtime` + " + seconds + " WHERE `ID` = " + pl.getCurrentIsland().getSqlId());
            stmt.addBatch("UPDATE `IslandMember` SET `Playtime` = `Playtime` + " + seconds + " WHERE `ID` = " + pl.getCurrentIsland().getMember(pl).getSqlId() + " AND `Player_ID` = " + pl.getSqlId());
            stmt.addBatch("UPDATE `Player` SET `Playtime` = `Playtime` + " + seconds + " WHERE `ID` = " + pl.getSqlId());
            stmt.executeBatch();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateCheats(OneBlockIsland is, String val)
    {
        try
        {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE `Island` SET `Cheats` = ? WHERE `ID` = ?"
            );
            ps.setString(1, val);
            ps.setInt(2, is.getSqlId());
            ps.executeUpdate();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateIslandName(OneBlockIsland is, String val)
    {
        try
        {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE `Island` SET `Name` = ? WHERE `ID` = ?"
            );
            ps.setString(1, val);
            ps.setInt(2, is.getSqlId());
            ps.executeUpdate();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateIslandMemberRole(OneBlockIsland is, OneBlockPlayer pl, MemberRole val)
    {
        try
        {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE `IslandMember` SET `Role` = ? WHERE `ID` = ?"
            );
            ps.setString(1, val.toString());
            ps.setInt(2, is.getMember(pl).getSqlId());
            ps.executeUpdate();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public IslandMember invitePlayer(OneBlockIsland is, OneBlockPlayer pl)
    {
        try
        {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO `IslandMember` (`Island_ID`, `Player_ID`, `Role`) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, is.getSqlId());
            ps.setInt(2, pl.getSqlId());
            ps.setString(3, MemberRole.Invited.toString());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next())
            {
                int id = rs.getInt(1);
                return new IslandMember(
                        id,
                        MemberRole.Invited,
                        pl.getPlayer().getName(),
                        pl.getPlayer().getUniqueId()
                );
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteBackup(IslandBackup backup)
    {
        try
        {
            Statement stmt = con.createStatement();
            stmt.addBatch("DELETE FROM `IslandBackup` WHERE `ID` = " + backup.getSqlId());
            stmt.addBatch("DELETE FROM `worlds` WHERE `name` = " + backup.getIslandWorld().getOverWorldName());
            stmt.addBatch("DELETE FROM `worlds` WHERE `name` = " + backup.getIslandWorld().getNetherWorldName());
            stmt.addBatch("DELETE FROM `worlds` WHERE `name` = " + backup.getIslandWorld().getEndWorldName());
            stmt.executeBatch();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void kickMember(OneBlockIsland island, OneBlockPlayer pl)
    {
        try
        {
            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM `IslandMember` WHERE `ID` = ?"
            );
            ps.setInt(1, island.getMember(pl).getSqlId());
            ps.executeUpdate();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteIsland(OneBlockIsland is)
    {
        try
        {
            Statement stmt = con.createStatement();
            IslandWorld world = is.getIslandWorld();

            stmt.addBatch("DELETE FROM `Island` WHERE `ID` = " + is.getSqlId());
            stmt.addBatch("DELETE FROM `worlds` WHERE `name` = " + world.getOverWorldName());
            stmt.addBatch("DELETE FROM `worlds` WHERE `name` = " + world.getNetherWorldName());
            stmt.addBatch("DELETE FROM `worlds` WHERE `name` = " + world.getEndWorldName());
            for (IslandBackup backup : is.getBackups()) {
                IslandWorld backupWorld = backup.getIslandWorld();
                stmt.addBatch("DELETE FROM `worlds` WHERE `name` = " + backupWorld.getOverWorldName());
                stmt.addBatch("DELETE FROM `worlds` WHERE `name` = " + backupWorld.getNetherWorldName());
                stmt.addBatch("DELETE FROM `worlds` WHERE `name` = " + backupWorld.getEndWorldName());
            }
            stmt.executeBatch();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateMemberProfile(IslandMember ism, Player pl, String inv){
        try
        {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE `IslandMember` SET `x` = ? , `y` = ? , `z` = ? , `Dimension` = ?, `Inventory` = ? WHERE `ID` = ?"
            );
            ps.setInt(1, pl.getLocation().getBlockX());
            ps.setInt(2, pl.getLocation().getBlockY());
            ps.setInt(3, pl.getLocation().getBlockZ());
            int env = 0;
            switch (pl.getWorld().getEnvironment()){
                case NORMAL:
                    env = 1;
                    break;
                case NETHER:
                    env = 2;
                    break;
                case THE_END:
                    env = 3;
                    break;
            }
            ps.setInt(4, env);
            ps.setString(4, inv);
            ps.setInt(5, ism.getSqlId());
            ps.executeUpdate();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public PlayerProfile getMemberProfile(IslandMember ism){
        try
        {
            OneBlock.log(Level.INFO, "Getting profile for " + ism.getName() + " id: " + ism.getSqlId());
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM `IslandMember` WHERE `ID` = ?"
            );
            ps.setInt(1, ism.getSqlId());
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                return new PlayerProfile(
                        rs.getInt("x"),
                        rs.getInt("y"),
                        rs.getInt("z"),
                        rs.getInt("Dimension"),
                        rs.getString("Inventory")
                );
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public class PlayerProfile
    {
        public double x;
        public double y;
        public double z;
        public int world;
        public String inventory;

        public PlayerProfile(double x, double y, double z, int dim,  String inventory)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.world = dim;
            this.inventory = inventory;
        }
    }
}