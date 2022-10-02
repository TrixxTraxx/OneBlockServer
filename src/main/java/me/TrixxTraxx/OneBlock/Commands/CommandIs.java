package me.TrixxTraxx.OneBlock.Commands;

import me.TrixxTraxx.Linq.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandIs implements CommandExecutor {
    private List<SubCommand> subCommands = new List<>();

    public void registerSubCommand(SubCommand subCommand)
    {
        subCommands.add(subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
        if(args.length > 0)
        {
            for (SubCommand subCommand : subCommands)
            {
                if (subCommand.getName().equalsIgnoreCase(args[0]))
                {
                    //make a new array without the first element
                    String[] newArgs = new String[args.length - 1];
                    System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                    subCommand.execute((Player) sender, newArgs);
                    return true;
                }
            }
        }

        for(SubCommand subCommand : subCommands)
        {
            sender.sendMessage("/is" + subCommand.getName());
        }

        return true;
    }
}
