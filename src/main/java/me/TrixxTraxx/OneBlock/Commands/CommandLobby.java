package me.TrixxTraxx.OneBlock.Commands;

import me.TrixxTraxx.OneBlock.OneBlockPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLobby extends Command
{
    public CommandLobby()
    {
        super("lobby");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(sender instanceof Player)
        {
            OneBlockPlayer.get((Player) sender).toLobby();
            return true;
        }
        return false;
    }
}
