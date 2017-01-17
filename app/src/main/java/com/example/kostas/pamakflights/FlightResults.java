package com.example.kostas.pamakflights;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FlightResults extends AppCompatActivity {

    private JSONArray flightsJSON = null;
    private ArrayList<JSONObject> flightsArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_results);
        Bundle extras = getIntent().getExtras();
        JSONObject flightData;
        if (extras != null) {
            try {
                flightsJSON = new JSONArray(extras.getString("flights"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONArray priceCategoryArray;
        for(int i=0; i < flightsJSON.length() ; i++) {
            try {
                flightData = flightsJSON.getJSONObject(i);
                priceCategoryArray = flightData.getJSONArray("flights");
                for (int j = 0; j < priceCategoryArray.length(); j++) {
                    flightsArray.add(priceCategoryArray.getJSONObject(j));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        System.out.println(flightsArray);
        FlightsAdapter mArrayAdapter = new FlightsAdapter(this, R.layout.list_item, flightsArray);
        ListView list = (ListView) findViewById(R.id.FlightsList);
        list.setAdapter(mArrayAdapter);
    }
}