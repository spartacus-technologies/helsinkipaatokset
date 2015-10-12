package com.ahjo_explorer.spartacus.ahjoexplorer.hkanava_data_access;

/**
 * Created by vesa on 12.7.2014.
 */
public interface IJsonListener
{
	public void YearsAvailable();
    public void DataAvailable(String year);
    public void ImageAvailable(int id);
}
