package com.example.kostas.pamakflights;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Calendar;

import static com.example.kostas.pamakflights.BuildConfig.FLIGHTS_API_KEY;
import static com.example.kostas.pamakflights.BuildConfig.IATA_API_KEY;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView originCountry;
    private AutoCompleteTextView originAirport;
    private AutoCompleteTextView destinationCountry;
    private AutoCompleteTextView destinationAirport;
    private String input = null;
    private String apiKey = null;
    private int index = 0;
    private ArrayList<String> countryNames = new ArrayList<>();
    private ArrayList<String> countryCodes = new ArrayList<>();
    private String origin = null;
    private String destination = null;
    private ArrayList<String> label = new ArrayList<>();
    private RelativeLayout layout = null;
    private String formattedDepartureDate = null;
    private String formattedArrivalDate = null;
    private SimpleDateFormat sdf = null;
    private TextView numOfAdults = null;
    private TextView numOfKids = null;
    private TextView numOfBabies = null;
    private CheckBox nonstop = null;
    private ProgressDialog progress;
    private String jsonPath = null;
    private String formYear;
    private String formMonth;
    private String formDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            JSONObject obj2 = new JSONObject(loadJSONFromAsset("Country Codes"));
            JSONArray country = obj2.getJSONArray("names");


            for (int i=0;i<country.length();i++)
            {
                JSONObject codes = country.getJSONObject(i);
                countryNames.add(codes.getString("name"));
                countryCodes.add(codes.getString("alpha-2"));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, countryNames);
            originCountry = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
            originCountry.setThreshold(1);
            originCountry.setAdapter(adapter);
            originAirport = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
            originAirport.setThreshold(1);
            destinationCountry = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView3);
            destinationCountry.setThreshold(1);
            destinationCountry.setAdapter(adapter);
            destinationAirport = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView4);
            destinationAirport.setThreshold(1);

            apiKey = FLIGHTS_API_KEY;

            originAirport.setOnFocusChangeListener(new View.OnFocusChangeListener(){

                @Override
                public void onFocusChange(View v, boolean hasFocus){
                    if(hasFocus && !originCountry.getText().toString().equals(""))
                    {
                        label.clear();
                        input = null;
                        input = originCountry.getText().toString();
                        for (int i=0;i<countryNames.size();i++)
                        {
                            if (input.equals(countryNames.get(i)))
                                index = i;
                        }
                        new jsonAirports().execute("https://api.sandbox.amadeus.com/v1.2/airports/autocomplete?apikey="+ apiKey +"&country="+countryCodes.get(index),
                                "origin");
                    }
                }
            });
            destinationAirport.setOnFocusChangeListener(new View.OnFocusChangeListener(){

                @Override
                public void onFocusChange(View v, boolean hasFocus){
                    if(hasFocus && !destinationCountry.getText().toString().equals(""))
                    {
                        label.clear();
                        input = null;
                        input = destinationCountry.getText().toString();
                        for (int i=0;i<countryNames.size();i++)
                        {
                            if (input.equals(countryNames.get(i)))
                                index = i;
                        }

                        new jsonAirports().execute("https://api.sandbox.amadeus.com/v1.2/airports/autocomplete?apikey="+ apiKey +"&country="+countryCodes.get(index),
                                "destination");
                    }
                }


            });

            layout = (RelativeLayout)findViewById(R.id.hidden);
            sdf = new SimpleDateFormat("yy-MM-DD");

            CalendarView departureDate = (CalendarView) findViewById(R.id.departureDate);
            departureDate.setMinDate(departureDate.getDate());
            Calendar c = Calendar.getInstance();
            formYear = String.valueOf(c.get(Calendar.YEAR));
            formMonth = c.get(Calendar.MONTH) < 9 ? '0' + String.valueOf(c.get(Calendar.MONTH) + 1) : String.valueOf(c.get(Calendar.MONTH) + 1);
            formDay = c.get(Calendar.DAY_OF_MONTH) < 10 ? '0' + String.valueOf(c.get(Calendar.DAY_OF_MONTH)) : String.valueOf(c.get(Calendar.DAY_OF_MONTH));
            formattedDepartureDate = formYear + "-" + formMonth + "-" + formDay;
            departureDate.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
                public void onSelectedDayChange(@NonNull CalendarView departureDate, int year, int month, int day) {
                    formYear = String.valueOf(year);
                    formMonth = month < 9 ? "0" + String.valueOf(month + 1) : String.valueOf(month + 1);
                    formDay = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
                    formattedDepartureDate = formYear + "-" + formMonth + "-" + formDay;
                }
            });

            CalendarView arrivalDate = (CalendarView) findViewById(R.id.arrivalDate);
            arrivalDate.setMinDate(arrivalDate.getDate());
            formattedArrivalDate = formYear + "-" + formMonth + "-" + formDay;
            arrivalDate.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
                public void onSelectedDayChange(@NonNull CalendarView arrivalDate, int year, int month, int day) {
                    formYear = String.valueOf(year);
                    formMonth = month < 9 ? "0" + String.valueOf(month + 1) : String.valueOf(month + 1);
                    formDay = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
                    formattedArrivalDate = formYear + "-" + formMonth + "-" + formDay;
                }
            });

            numOfAdults = (TextView)findViewById(R.id.numOfAdults);
            numOfKids = (TextView)findViewById(R.id.numOfKids);
            numOfBabies = (TextView)findViewById(R.id.numOfBabies);

            nonstop = (CheckBox)findViewById(R.id.nonstopValue);

            progress = new ProgressDialog(this);
            progress.setTitle("Αναζήτηση πτήσεων");
            progress.setMessage("H αναζήτηση ενδέχεται να καθυστερήσει. Παρακαλούμε περιμένετε.");
            progress.setCancelable(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_flight_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.moreOptions:
                item.setVisible(false);
                layout.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Οι επιπλέον επιλογές πτήσης είναι τώρα διαθέσιμες", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.searchFlights:
                String[] temp;
                if (!originAirport.getText().toString().equals("")) {
                    temp = originAirport.getText().toString().split("\\[");
                    origin = temp[1].replace("]", "");
                } else origin = null;
                if (!destinationAirport.getText().toString().equals("")) {
                    temp = destinationAirport.getText().toString().split("\\[");
                    destination = temp[1].replace("]", "");
                } else destination = null;
                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = sdf.parse(formattedDepartureDate);
                    d2 = sdf.parse(formattedArrivalDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (origin == null) {
                    Toast.makeText(MainActivity.this, "Δεν έχετε επιλέξει αφετηρία", Toast.LENGTH_SHORT).show();
                } else if (destination == null) {
                    Toast.makeText(MainActivity.this, "Δεν έχετε επιλέξει προορισμό", Toast.LENGTH_SHORT).show();
                } else if (origin.equals(destination)) {
                    Toast.makeText(MainActivity.this, "Η αφετηρία είναι ίδια με τον προορισμό", Toast.LENGTH_SHORT).show();
                }
                else if (d1!=null && d2!=null && d2.before(d1)) {
                    Toast.makeText(MainActivity.this, "Η ημερομηνία επιστροφής είναι πριν την ημερομηνία αναχώρησης", Toast.LENGTH_SHORT).show();
                }
                else {
                    progress.show();
                    jsonPath = "https://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?apikey=" + apiKey + "&origin=" + origin + "&destination="
                            + destination + "&departure_date=" + formattedDepartureDate + "&currency=EUR" + "&adults=" + numOfAdults.getText() + "&children="
                            + numOfKids.getText() + "&infants=" + numOfBabies.getText() + "&nonstop=" + nonstop.isChecked();
                    if (formattedArrivalDate != null) jsonPath += "&return_date=" + formattedArrivalDate;
                    new isResponseValid().execute(jsonPath);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String loadJSONFromAsset(String name) {
        String json;
        try {
            InputStream is = getAssets().open(name+".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public String httpRequest(String params) {
        try {
            HttpURLConnection urlConnection;
            BufferedReader reader;

            URL url;
            url = new URL(params);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray findFlightsByCategory(JSONArray flights, String flightType) {
        JSONObject flight;
        JSONArray numOfFlights;
        JSONObject flightDetails;
        JSONArray flightsArray;
        JSONArray formattedFlights = new JSONArray();

        try {
            String flightDuration = null;
            String midDestination = null;

            String buffer = httpRequest("http://nano.aviasales.ru/places_en?" + "&term=" + origin);
            String formattedOrigin = new JSONArray(buffer).getJSONObject(0).getString("name");
            String[] temp = formattedOrigin.split(",");
            formattedOrigin = temp[0];
            buffer = httpRequest("http://nano.aviasales.ru/places_en?" + "&term=" + destination);
            String formattedDestination = new JSONArray(buffer).getJSONObject(0).getString("name");
            String[] temp2 = formattedDestination.split(",");
            formattedDestination = temp2[0];
            int a = 0;

            for (int i=0; i<flights.length(); i++) {
                flight = flights.getJSONObject(i);
                numOfFlights = flight.getJSONArray("itineraries");
                for (int j = 0; j < numOfFlights.length(); j++) {
                    formattedFlights.put(new JSONObject().put("flights", new JSONArray()));
                    flightsArray = numOfFlights.getJSONObject(j).getJSONObject(flightType).getJSONArray("flights");
                    for (int k = 0; flightsArray.length() > k; k++) {
                        flightDetails = flightsArray.getJSONObject(k);
                        if (flightsArray.length() > 1) {
                            buffer = httpRequest("http://nano.aviasales.ru/places_en?" + "&term=" + flightsArray.getJSONObject(k).getJSONObject("destination").getString("airport"));
                            midDestination = new JSONArray(buffer).getJSONObject(0).getString("name");
                            String[] temp3 = midDestination.split(",");
                            midDestination = temp3[0];
                        }
                        buffer = httpRequest("https://iatacodes.org/api/v6/airlines?api_key=" + IATA_API_KEY + "&code=" + flightDetails.getString("operating_airline"));
                        String company = new JSONObject(buffer).getJSONArray("response").getJSONObject(0).getString("name");
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                        try {
                            Date dStart = format.parse(flightDetails.getString("departs_at"));
                            Date dEnd = format.parse(flightDetails.getString("arrives_at"));
                            long diffM = (dEnd.getTime() - dStart.getTime()) / 1000 / 60;
                            long diffH = diffM / 60;
                            if (diffH != 0) {
                                diffM = diffM - diffH * 60;
                                if (diffM != 0) flightDuration = diffH + "h" + diffM + "mins";
                                else flightDuration = diffH + "h";
                            } else {
                                flightDuration = diffM + "mins";
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        formattedFlights.getJSONObject(a).getJSONArray("flights").put(new JSONObject());
                        formattedFlights.getJSONObject(a).getJSONArray("flights").getJSONObject(k)
                                .put("origin", flightType.equals("outbound") ? formattedOrigin : formattedDestination)
                                .put("destination", flightType.equals("outbound") ? formattedDestination : formattedOrigin)
                                .put("company", company)
                                .put("departureTime", flightDetails.getString("departs_at"))
                                .put("arrivalTime", flightDetails.getString("arrives_at"))
                                .put("flightDuration", flightDuration)
                                .put("seatsRemaining", flightDetails.getJSONObject("booking_info").getString("seats_remaining"))
                                .put("intermediateStop", midDestination)
                                .put("price", flight.getJSONObject("fare").getString("total_price") + " €")
                                .put("index", Integer.toString(a));
                    }
                    a++;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return formattedFlights;
    }

    public class isResponseValid extends AsyncTask<String,String,Boolean> {

        protected Boolean doInBackground(String... params) {
            try {
                URL obj = new URL(params[0]);
                URLConnection conn = obj.openConnection();
                Map<String, List<String>> map = conn.getHeaderFields();
                for (Map.Entry<String, List<String>> entry : map.entrySet())
                    if (entry.getValue().get(0).equals("HTTP/1.1 400 Bad Request")) {
                        return false;
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        protected void onPostExecute(Boolean result) {
            if (!result) {
                Toast.makeText(MainActivity.this, "Δεν υπάρχουν δρομολόγια για την συγκεκριμένη ημερομηνία και προορισμό", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            } else {
                new jsonResults().execute(jsonPath);
            }
        }
    }

    public class jsonAirports extends AsyncTask<String,String,String> {
        protected String doInBackground(String... params)
        {
            String countryJsonStr;
            try {
                countryJsonStr = httpRequest(params[0]);

                JSONArray result = new JSONArray(countryJsonStr);

                for (int i=0;i<result.length();i++)
                {
                    JSONObject results = result.getJSONObject(i);
                    label.add(results.getString("label"));
                }
                return params[1];
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return params[1];
        }
        protected void onPostExecute(String result) {
            if (result.equals("origin")) {
                originAirport.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, label));
            } else {
                destinationAirport.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, label));
            }
        }
    }

    public class jsonResults extends AsyncTask<String,String,String> {
        protected String doInBackground(String... params)
        {
            try {

                String buffer = httpRequest(params[0]);
                JSONArray results = new JSONObject(buffer).getJSONArray("results");
                JSONArray formattedOutboundFlights = findFlightsByCategory(results, "outbound");
                JSONArray formattedInboundFlights = findFlightsByCategory(results, "inbound");
                progress.dismiss();

                Intent i = new Intent(getApplicationContext(), FlightResults.class);
                i.putExtra("Outbound Flights", formattedOutboundFlights.toString());
                i.putExtra("Inbound Flights", formattedInboundFlights.toString());
                startActivity(i);
                return "Success";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
