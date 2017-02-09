package com.example.kostas.pamakflights;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FlightResults extends AppCompatActivity {

    private JSONArray outboundFlightsJSON = null;
    private ArrayList<JSONObject> outboundFlightsArray = new ArrayList<>();
    private JSONArray inboundFlightsJSON = null;
    private ArrayList<JSONObject> inboundFlightsArray = new ArrayList<>();
    private ListView list;
    private FlightsAdapter flightAdapter;
    private String mode;
    private TextView flightOriginDestinationLabel;
    private TextView flightDateLabel;
    private SimpleDateFormat originalFormat;
    private SimpleDateFormat targetFormat;
    private ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_results);
        ab = getSupportActionBar();
        ab.setTitle("Αναχωρήσεις");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                outboundFlightsJSON = new JSONArray(extras.getString("Outbound Flights"));
                inboundFlightsJSON = new JSONArray(extras.getString("Inbound Flights"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        createFlightArray(outboundFlightsJSON, outboundFlightsArray);
        createFlightArray(inboundFlightsJSON, inboundFlightsArray);

        try {
            flightOriginDestinationLabel = (TextView) findViewById(R.id.originDestination);
            flightOriginDestinationLabel.setText(outboundFlightsArray.get(0).getString("origin") + "-" + outboundFlightsArray.get(0).getString("destination"));

            flightDateLabel = (TextView) findViewById(R.id.flightDate);
            String tempDep = outboundFlightsArray.get(0).getString("departureTime");
            originalFormat = new SimpleDateFormat("yyyy-MM-dd");
            targetFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = originalFormat.parse(tempDep.substring(0, tempDep.length() - 6));
            flightDateLabel.setText(targetFormat.format(date));
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        mode = "departures";
        flightAdapter = new FlightsAdapter(this, R.layout.list_item, outboundFlightsArray);
        list = (ListView) findViewById(R.id.FlightsList);
        list.setAdapter(flightAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_arrivals_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.showArrivals:
                if (mode.equals("departures")) {
                    item.setIcon(getResources().getDrawable(R.drawable.ic_flight_takeoff_white_48dp));
                    flightAdapter = new FlightsAdapter(FlightResults.this, R.layout.list_item, inboundFlightsArray);
                    mode = "arrivals";
                    ab.setTitle("Επιστροφές");
                    try {
                        flightOriginDestinationLabel.setText(outboundFlightsArray.get(0).getString("destination") + "-" + outboundFlightsArray.get(0).getString("origin"));
                        String tempDep = outboundFlightsArray.get(0).getString("arrivalTime");
                        Date date = originalFormat.parse(tempDep.substring(0, tempDep.length() - 6));
                        flightDateLabel.setText(targetFormat.format(date));
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    item.setIcon(getResources().getDrawable(R.drawable.ic_flight_land_white_48dp));
                    flightAdapter = new FlightsAdapter(FlightResults.this, R.layout.list_item, outboundFlightsArray);
                    mode = "departures";
                    ab.setTitle("Αναχωρήσεις");
                    try {
                        flightOriginDestinationLabel.setText(outboundFlightsArray.get(0).getString("origin") + "-" + outboundFlightsArray.get(0).getString("destination"));
                        String tempDep = outboundFlightsArray.get(0).getString("departureTime");
                        Date date = originalFormat.parse(tempDep.substring(0, tempDep.length() - 6));
                        flightDateLabel.setText(targetFormat.format(date));
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
                list.setAdapter(flightAdapter);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createFlightArray(JSONArray targetArray, ArrayList<JSONObject> targetJson) {
        JSONObject itineraries;
        JSONArray flights;
        for(int i=0; i < targetArray.length() ; i++) {
            try {
                itineraries = targetArray.getJSONObject(i);
                flights = itineraries.getJSONArray("flights");
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
                        targetJson.add(flights.getJSONObject(j - 1).put("arrivalTime", flights.getJSONObject(j).getString("arrivalTime"))
                                .put("flightDuration", flights.getJSONObject(j - 1).getString("flightDuration") + " + " + flights.getJSONObject(j).getString("flightDuration"))
                                .put("intermediateStop", flights.getJSONObject(j - 1).getString("intermediateStop"))
                                .put("diffDay", "true"));
                    else
                        targetJson.add(flights.getJSONObject(j - 1).put("arrivalTime", flights.getJSONObject(j).getString("arrivalTime"))
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
                        targetJson.add(flights.getJSONObject(j - 1).put("arrivalTime", flights.getJSONObject(j + 1).getString("arrivalTime"))
                                .put("flightDuration", flights.getJSONObject(j - 1).getString("flightDuration") + " + " + flights.getJSONObject(j).getString("flightDuration") + " + "
                                        + flights.getJSONObject(j + 1).getString("flightDuration"))
                                .put("intermediateStop", flights.getJSONObject(j - 1).getString("intermediateStop") + " , " + flights.getJSONObject(j).getString("intermediateStop"))
                                .put("diffDay", "true"));
                    else
                        targetJson.add(flights.getJSONObject(j - 1).put("arrivalTime", flights.getJSONObject(j + 1).getString("arrivalTime"))
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
                        targetJson.add(flights.getJSONObject(j - 1).put("arrivalTime", flights.getJSONObject(j + 2).getString("arrivalTime"))
                                .put("flightDuration", flights.getJSONObject(j - 1).getString("flightDuration") + " + " + flights.getJSONObject(j).getString("flightDuration") + " + "
                                        + flights.getJSONObject(j + 1).getString("flightDuration") + " + " + flights.getJSONObject(j + 2).getString("flightDuration"))
                                .put("intermediateStop", flights.getJSONObject(j - 1).getString("intermediateStop") + " , " + flights.getJSONObject(j).getString("intermediateStop") +
                                        " , " + flights.getJSONObject(j+1).getString("intermediateStop"))
                                .put("diffDay", "true"));
                    else
                        targetJson.add(flights.getJSONObject(j - 1).put("arrivalTime", flights.getJSONObject(j + 2).getString("arrivalTime"))
                                .put("flightDuration", flights.getJSONObject(j - 1).getString("flightDuration") + " + " + flights.getJSONObject(j).getString("flightDuration") + " + "
                                        + flights.getJSONObject(j + 1).getString("duration") + " + " + flights.getJSONObject(j + 2).getString("duration"))
                                .put("intermediateStop", flights.getJSONObject(j - 1).getString("intermediateStop") + " , " + flights.getJSONObject(j).getString("intermediateStop") +
                                        " , " + flights.getJSONObject(j+1).getString("intermediateStop")));
                    j = j + 3;
                }
                else
                {
                    targetJson.add(flights.getJSONObject(j-1));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}