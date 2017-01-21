package com.example.kostas.pamakflights;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.R.attr.format;

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
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                long tempArrDay=0;
                long tempDepDay=0;
                for (int j = 1; j <=priceCategoryArray.length(); j++) {
                    if (priceCategoryArray.length()>1) {
                        if (priceCategoryArray.getJSONObject(j-1).getString("index").equals(priceCategoryArray.getJSONObject(j).getString("index"))) {
                            try {
                                Date dStart = format.parse(priceCategoryArray.getJSONObject(j-1).getString("departureTime"));
                                Date dEnd = format.parse(priceCategoryArray.getJSONObject(j).getString("arrivalTime"));
                                tempDepDay = dStart.getDay();
                                tempArrDay = dEnd.getDay();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            flightsArray.add(priceCategoryArray.getJSONObject(j - 1).put("arrivalTime", priceCategoryArray.getJSONObject(j).getString("arrivalTime"))
                                    .put("flightDuration", priceCategoryArray.getJSONObject(j - 1).getString("flightDuration") + " + " + priceCategoryArray.getJSONObject(j).getString("flightDuration")));
                            if (tempDepDay!=tempArrDay)
                            {
                                flightsArray.add(priceCategoryArray.getJSONObject(j-1).put("diffDay", "true"));
                            }
                            j++;
                        } else if (priceCategoryArray.getJSONObject(j - 1).getString("index").equals(priceCategoryArray.getJSONObject(j).getString("index")) &&
                                priceCategoryArray.getJSONObject(j).getString("index").equals(priceCategoryArray.getJSONObject(j + 1).getString("index"))) {
                            try {
                                Date dStart = format.parse(priceCategoryArray.getJSONObject(j-1).getString("departureTime"));
                                Date dEnd = format.parse(priceCategoryArray.getJSONObject(j+1).getString("arrivalTime"));
                                tempDepDay = dStart.getDay();
                                tempArrDay = dEnd.getDay();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            flightsArray.add(priceCategoryArray.getJSONObject(j - 1).put("arrivalTime", priceCategoryArray.getJSONObject(j + 1).getString("arrivalTime"))
                                    .put("flightDuration", priceCategoryArray.getJSONObject(j - 1).getString("flightDuration") + " + " + priceCategoryArray.getJSONObject(j).getString("flightDuration") + " + " + priceCategoryArray.getJSONObject(j + 1).getString("duration")));
                            if (tempDepDay!=tempArrDay)
                            {
                                flightsArray.add(priceCategoryArray.getJSONObject(j-1).put("diffDay", "true"));
                            }
                            j = j + 2;
                        }
                    }
                    else
                    {
                        flightsArray.add(priceCategoryArray.getJSONObject(j-1));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        TextView flightOriginDestination = (TextView) findViewById(R.id.originDestination);
        try {
            flightOriginDestination.setText(flightsArray.get(0).getString("origin") + "-" + flightsArray.get(0).getString("destination"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FlightsAdapter mArrayAdapter = new FlightsAdapter(this, R.layout.list_item, flightsArray);
        ListView list = (ListView) findViewById(R.id.FlightsList);
        list.setAdapter(mArrayAdapter);
    }
}