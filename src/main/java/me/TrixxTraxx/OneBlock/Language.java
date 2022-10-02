package me.TrixxTraxx.OneBlock;

import me.TrixxTraxx.Linq.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class Language
{
    private static YamlConfiguration lang;
    private static boolean initialized = false;

    private static void init()
    {
        if(initialized) return;
        File f = new File("./plugins/OneBlock/Language.yml");
        if(!f.exists()) {
            try
            {
                f.createNewFile();
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
        lang = YamlConfiguration.loadConfiguration(f);
    }

    public static void sendMessage(Player pl, String msg)
    {
        pl.sendMessage(getMessage(msg, pl, new List()));
    }

    public static void sendMessages(List<Player> players, String msg)
    {
        for(Player p : players) sendMessage(p, msg);
    }

    public static void sendMessage(Player pl, String msg, List<Placeholder> placeholders)
    {
        pl.sendMessage(getMessage(msg, pl, placeholders));
    }

    public static void sendMessages(List<Player> players, String msg, List<Placeholder> placeholders)
    {
        for(Player p : players) sendMessage(p, msg, placeholders);
    }

    public static void sendMessage(Player pl, String msg, Placeholder... placeholders)
    {
        pl.sendMessage(getMessage(msg, pl, new List(placeholders)));
    }

    public static void sendMessages(List<Player> players, String msg, Placeholder... placeholders)
    {
        for(Player p : players) sendMessage(p, msg, placeholders);
    }

    public static String getMessage(String key, Player pl, List<Placeholder> placeholders)
    {
        init();
        String msg = lang.getString(key);
        if(msg == null) msg = "Couldn't load Message: " + key;
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        for(Placeholder p : placeholders)
        {
            msg = p.apply(msg);
        }
        msg = msg.replace("{receivingPlayer}", pl.getName());
        return msg;
    }
}
