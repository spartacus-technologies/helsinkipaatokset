package com.ahjo_explorer.spartacus.ahjoexplorer.APIObjects;

/**
 * Created by Eetu on 3.10.2015.
 */
public class Meeting {

    Meeting(){

    }

    //API JSON format:
    /*

        {
            "date": "2015-10-07",
            "id": 8702,
            "minutes": false,
            "number": 15,
            "policymaker": "/paatokset/v1/policymaker/5/",
            "policymaker_name": "Kaupunginvaltuusto",
            "resource_uri": "/paatokset/v1/meeting/8702/",
            "year": 2015
        }

     */

    private String date;
    private int id;
    private boolean minutes;
    private int number;
    private String policymaker;
    private String policymaker_name;
    private String resource_uri;
    private int year;

}
