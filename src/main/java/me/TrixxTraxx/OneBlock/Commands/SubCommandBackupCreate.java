package me.TrixxTraxx.OneBlock.Commands;

import me.TrixxTraxx.OneBlock.Island.OneBlockIsland;
import me.TrixxTraxx.OneBlock.OneBlockPlayer;

public class SubCommandBackupCreate extends SubCommandIsland
{
    public SubCommandBackupCreate() {
        super("backupCreate");
    }

    @Override
    public void execute(OneBlockIsland island, OneBlockPlayer player, String[] args)
    {
        String name = "New Backup " + System.currentTimeMillis();
        if(args.length > 0)
        {
            name = args[0];
        }
        island.createBackup(name);
        String finalName = name;
        island.broadCast("backup.created", x -> x.replace("{name}", finalName));
    }
}
