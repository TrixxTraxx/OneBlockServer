package me.TrixxTraxx.OneBlock.Commands;

import me.TrixxTraxx.OneBlock.Island.IslandBackup;
import me.TrixxTraxx.OneBlock.Island.OneBlockIsland;
import me.TrixxTraxx.OneBlock.OneBlockPlayer;

public class SubCommandBackupDelete extends SubCommandIsland
{
    public SubCommandBackupDelete()
    {
        super("backupDelete");
    }

    @Override
    public void execute(OneBlockIsland island, OneBlockPlayer player, String[] args)
    {
        if(args.length == 0){
            island.broadCast("backup.delete.noName");
            return;
        }
        IslandBackup backup = island.getBackups().find(x -> x.getName().equalsIgnoreCase(args[0]));
        backup.delete();
        island.broadCast("backup.deleted", x -> x.replace("{name}", backup.getName()));
    }
}
