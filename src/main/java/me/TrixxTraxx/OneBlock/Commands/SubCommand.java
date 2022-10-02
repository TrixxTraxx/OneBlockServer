package me.TrixxTraxx.OneBlock.Commands;

import org.bukkit.entity.Player;

public abstract class SubCommand
{
    private String name;

    public SubCommand(String name)
    {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void execute(Player player, String[] args);
}
