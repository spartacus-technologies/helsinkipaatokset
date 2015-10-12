package com.ahjo_explorer.spartacus.ahjoexplorer.hkanava_data_access;

import com.ahjo_explorer.spartacus.ahjoexplorer.hkanava_data_access.HelsinkiKanavaDataClasses.Issues;
import com.ahjo_explorer.spartacus.ahjoexplorer.hkanava_data_access.HelsinkiKanavaDataClasses.Video;

import java.util.List;

public class Metadata implements Comparable<Metadata>
{
    public String version;
    public String id;
    public String source;
    public String terms_of_service;
    public String started;
    public String finished;
    public String title;
    public String language;
    public String session_url;
    public Video video;
    public List<Attendance> attendance;
    public List<Issues> issues;
    public String url;

    @Override
    public int compareTo(Metadata another)
    {
        return started.compareTo(another.started);
    }
}
