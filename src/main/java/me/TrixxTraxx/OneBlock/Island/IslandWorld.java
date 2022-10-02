package me.TrixxTraxx.OneBlock.Island;

public class IslandWorld
{
    private String prefix;
    private String suffix;

    public IslandWorld(String prefix, String suffix)
    {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getOverWorldName()
    {
        return prefix + "_overworld_" + suffix;
    }

    public String getNetherWorldName()
    {
        return prefix + "_nether_" + suffix;
    }

    public String getEndWorldName()
    {
        return prefix + "_end_" + suffix;
    }
}
