package me.TrixxTraxx.OneBlock.Commands;

import me.TrixxTraxx.OneBlock.Island.IslandBackup;
import me.TrixxTraxx.OneBlock.Island.OneBlockIsland;
import me.TrixxTraxx.OneBlock.OneBlockPlayer;

public class SubCommandBackupLoad extends SubCommandIsland
{


    public SubCommandBackupLoad()
    {
        super("backupLoad");
    }

    @Override
    public void execute(OneBlockIsland island, OneBlockPlayer player, String[] args)
    {
        if(args.length == 0)
        {
            island.broadCast("backup.load.noName");
            return;
        }
        IslandBackup backup = island.getBackups().find(x -> x.getName().equalsIgnoreCase(args[0]));

        if(backup == null)
        {
            island.broadCast("backup.load.notFound");
            return;
        }

        island.loadBackup(backup);
    }
}
