package com.ahjo_explorer.spartacus.ahjoexplorer.hkanava_data_access;

public class Attendance implements Comparable<Attendance>
{
    public String name;
    public String party;
    public String seat;

    @Override
    public int compareTo(Attendance another)
    {
        return party.compareTo(another.party);
    }
}
