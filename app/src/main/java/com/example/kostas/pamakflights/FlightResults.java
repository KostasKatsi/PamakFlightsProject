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
        JSONObject allItineraries;
        if (extras != null) {
            try {
                flightsJSON = new JSONArray(extras.getString("flights"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONArray flights;
        for(int i=0; i < flightsJSON.length() ; i++) {
            try {
                allItineraries = flightsJSON.getJSONObject(i);
                flights = allItineraries.getJSONArray("flights");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                long tempArrDay=0;
                long tempDepDay=0;
                int j = 1;
                    if (flights.length()==2) {
                            try {
                                Date dStart = format.parse(flights.getJSONObject(j - 1).getString("departureTime"));
                                Date dEnd = format.parse(flights.getJSONObject(j).getString("arrivalTime"));
                                tempDepDay = dStart.getDay();
                                tempArrDay = dEnd.getDay();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (tempDepDay != tempArrDay)
                                flightsArray.add(flights.getJSONObject(j - 1).put("arrivalTime", flights.getJSONObject(j).getString("arrivalTime"))
                                        .put("flightDuration", flights.getJSONObject(j - 1).getString("flightDuration") + " + " + flights.getJSONObject(j).getString("flightDuration"))
                                        .put("intermediateStop", flights.getJSONObject(j - 1).getString("intermediateStop"))
                                        .put("diffDay", "true"));
                            else
                                flightsArray.add(flights.getJSONObject(j - 1).put("arrivalTime", flights.getJSONObject(j).getString("arrivalTime"))
                                        .put("flightDuration", flights.getJSONObject(j - 1).getString("flightDuration") + " + " + flights.getJSONObject(j).getString("flightDuration"))
                                        .put("intermediateStop", flights.getJSONObject(j - 1).getString("intermediateStop")));
                            j++;
                    }
                    else if (flights.length()==3)
                    {
                            try {
                                Date dStart = format.parse(flights.getJSONObject(j-1).getString("departureTime"));
                                Date dEnd = format.parse(flights.getJSONObject(j+1).getString("arrivalTime"));
                                tempDepDay = dStart.getDay();
                                tempArrDay = dEnd.getDay();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (tempDepDay!=tempArrDay)
                                flightsArray.add(flights.getJSONObject(j - 1).put("arrivalTime", flights.getJSONObject(j + 1).getString("arrivalTime"))
                                        .put("flightDuration", flights.getJSONObject(j - 1).getString("flightDuration") + " + " + flights.getJSONObject(j).getString("flightDuration") + " + "
                                                + flights.getJSONObject(j + 1).getString("flightDuration"))
                                        .put("intermediateStop", flights.getJSONObject(j - 1).getString("intermediateStop") + " , " + flights.getJSONObject(j).getString("intermediateStop"))
                                        .put("diffDay", "true"));
                            else
                                flightsArray.add(flights.getJSONObject(j - 1).put("arrivalTime", flights.getJSONObject(j + 1).getString("arrivalTime"))
                                                .put("flightDuration", flights.getJSONObject(j - 1).getString("flightDuration") + " + " + flights.getJSONObject(j).getString("flightDuration") + " + "
                                                        + flights.getJSONObject(j + 1).getString("duration"))
                                                .put("intermediateStop", flights.getJSONObject(j - 1).getString("intermediateStop") + " , " + flights.getJSONObject(j).getString("intermediateStop")));
                            j = j + 2;
                    }
                    else if (flights.length()==4)
                    {
                            try {
                                Date dStart = format.parse(flights.getJSONObject(j-1).getString("departureTime"));
                                Date dEnd = format.parse(flights.getJSONObject(j+2).getString("arrivalTime"));
                                tempDepDay = dStart.getDay();
                                tempArrDay = dEnd.getDay();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (tempDepDay!=tempArrDay)
                                flightsArray.add(flights.getJSONObject(j - 1).put("arrivalTime", flights.getJSONObject(j + 2).getString("arrivalTime"))
                                        .put("flightDuration", flights.getJSONObject(j - 1).getString("flightDuration") + " + " + flights.getJSONObject(j).getString("flightDuration") + " + "
                                                + flights.getJSONObject(j + 1).getString("flightDuration") + " + " + flights.getJSONObject(j + 2).getString("flightDuration"))
                                        .put("intermediateStop", flights.getJSONObject(j - 1).getString("intermediateStop") + " , " + flights.getJSONObject(j).getString("intermediateStop") +
                                         " , " + flights.getJSONObject(j+1).getString("intermediateStop"))
                                        .put("diffDay", "true"));
                            else
                                flightsArray.add(flights.getJSONObject(j - 1).put("arrivalTime", flights.getJSONObject(j + 2).getString("arrivalTime"))
                                        .put("flightDuration", flights.getJSONObject(j - 1).getString("flightDuration") + " + " + flights.getJSONObject(j).getString("flightDuration") + " + "
                                                + flights.getJSONObject(j + 1).getString("duration") + " + " + flights.getJSONObject(j + 2).getString("duration"))
                                        .put("intermediateStop", flights.getJSONObject(j - 1).getString("intermediateStop") + " , " + flights.getJSONObject(j).getString("intermediateStop") +
                                                " , " + flights.getJSONObject(j+1).getString("intermediateStop")));
                            j = j + 3;
                    }
                    else
                    {
                        flightsArray.add(flights.getJSONObject(j-1));
                    }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        TextView flightOriginDestination = (TextView) findViewById(R.id.originDestination);
        TextView flightDate = (TextView) findViewById(R.id.flightDate);
        try {
            flightOriginDestination.setText(flightsArray.get(0).getString("origin") + "-" + flightsArray.get(0).getString("destination"));
            String tempDep = flightsArray.get(0).getString("departureTime");
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = originalFormat.parse(tempDep.substring(0, tempDep.length() - 6));
            String formattedDate = targetFormat.format(date);
            flightDate.setText(formattedDate);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        FlightsAdapter mArrayAdapter = new FlightsAdapter(this, R.layout.list_item, flightsArray);
        ListView list = (ListView) findViewById(R.id.FlightsList);
        list.setAdapter(mArrayAdapter);
    }
}