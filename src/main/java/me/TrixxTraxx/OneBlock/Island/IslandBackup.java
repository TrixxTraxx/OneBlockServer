package me.TrixxTraxx.OneBlock.Island;

import me.TrixxTraxx.OneBlock.sql.SQL;

import java.sql.Time;

public class IslandBackup
{
    private Time creationTime;
    private String name;
    private int sqlId;
    private IslandWorld isWorld;

    public IslandBackup(Time creationTime, String name, int sqlId, int isId)
    {
        this.creationTime = creationTime;
        this.name = name;
        this.sqlId = sqlId;
        isWorld = new IslandWorld(String.valueOf(isId), String.valueOf(sqlId));
    }

    public IslandWorld getIslandWorld()
    {
        return isWorld;
    }

    public Time getCreationTime() {
        return creationTime;
    }

    public String getName() {
        return name;
    }

    public int getSqlId() {
        return sqlId;
    }

    public void delete(){
        SQL.Instance.deleteBackup(this);
    }
}
