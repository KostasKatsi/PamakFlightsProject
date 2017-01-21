package com.example.kostas.pamakflights;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.kostas.pamakflights.BuildConfig.FLIGHTS_API_KEY;
import static com.example.kostas.pamakflights.BuildConfig.IATA_API_KEY;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView autoComplete;
    private AutoCompleteTextView autoComplete2;
    private AutoCompleteTextView autoComplete3;
    private AutoCompleteTextView autoComplete4;
    private ArrayAdapter<String> adapter1;
    private ArrayAdapter<String> adapter2;
    private String input = null;
    private String apiKey = null;
    private int index = 0;
    private ArrayList<String> countryNames = new ArrayList<>();
    private ArrayList<String> countryCodes = new ArrayList<>();
    private String origin = null;
    private String destination = null;
    private ArrayList<String> label = new ArrayList<>();
    private Button moreButton = null;
    private RelativeLayout layout = null;
    private String formattedDepartureDate = null;
    private String formattedArrivalDate = null;
    private SimpleDateFormat sdf = null;
    private TextView numOfAdults = null;
    private TextView numOfKids = null;
    private TextView numOfBabies = null;
    private CheckBox nonstop = null;
    private ProgressDialog progress;


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
            autoComplete = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
            autoComplete.setAdapter(adapter);
            autoComplete.setThreshold(1);
            autoComplete3 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView3);
            autoComplete3.setAdapter(adapter);
            autoComplete3.setThreshold(1);

            apiKey = FLIGHTS_API_KEY;

            autoComplete2 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
            autoComplete2.setOnFocusChangeListener(new View.OnFocusChangeListener(){

                @Override
                public void onFocusChange(View v, boolean hasFocus){
                    if(hasFocus && !autoComplete.getText().toString().equals(""))
                    {
                        label.clear();
                        input = null;
                        input = autoComplete.getText().toString();
                        for (int i=0;i<countryNames.size();i++)
                        {
                            if (input.equals(countryNames.get(i)))
                                index = i;

                        }
                        new jsonAirports().execute("https://api.sandbox.amadeus.com/v1.2/airports/autocomplete?apikey="+ apiKey +"&country="+countryCodes.get(index));
                    }
                    adapter1 = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, label);
                    autoComplete2.setAdapter(adapter1);
//                    autoComplete2.setThreshold(1);
                }
            });

            autoComplete4 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView4);
            autoComplete4.setOnFocusChangeListener(new View.OnFocusChangeListener(){

                @Override
                public void onFocusChange(View v, boolean hasFocus){

                    if(hasFocus && !autoComplete3.getText().toString().equals(""))
                    {
                        label.clear();
                        input = null;
                        input = autoComplete3.getText().toString();
                        for (int i=0;i<countryNames.size();i++)
                        {
                            if (input.equals(countryNames.get(i)))
                                index = i;
                        }

                        new jsonAirports().execute("https://api.sandbox.amadeus.com/v1.2/airports/autocomplete?apikey="+ apiKey +"&country="+countryCodes.get(index));
                    }
                    adapter2 = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, label);
                    autoComplete4.setAdapter(adapter2);
//                    autoComplete4.setThreshold(1);
                }


            });

            moreButton = (Button)findViewById(R.id.more);
            layout = (RelativeLayout)findViewById(R.id.hidden);
            moreButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(layout.getVisibility() != View.VISIBLE) {
                        layout.setVisibility(View.VISIBLE);
                        moreButton.setText("Λιγοτερα...");
                    }
                    else
                    {
                        layout.setVisibility(View.GONE);
                        moreButton.setText("Περισσοτερα...");
                    }
                }
            });

            sdf = new SimpleDateFormat("yy-MM-DD");

            CalendarView departureDate = (CalendarView) findViewById(R.id.departureDate);
            departureDate.setMinDate(departureDate.getDate());
            formattedDepartureDate = "20" + sdf.format(departureDate.getDate());
            departureDate.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
                public void onSelectedDayChange(@NonNull CalendarView departureDate, int year, int month, int day) {
                    formattedDepartureDate = "20" + sdf.format(new Date(year, month, day));
                }
            });

            CalendarView arrivalDate = (CalendarView) findViewById(R.id.arrivalDate);
            arrivalDate.setMinDate(arrivalDate.getDate());
            formattedArrivalDate = "20" + sdf.format(arrivalDate.getDate());
            arrivalDate.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
                public void onSelectedDayChange(@NonNull CalendarView arrivalDate, int year, int month, int day) {
                    formattedArrivalDate = "20" + sdf.format(new Date(year, month, day));
                }
            });

            numOfAdults = (TextView)findViewById(R.id.numOfAdults);
            numOfKids = (TextView)findViewById(R.id.numOfKids);
            numOfBabies = (TextView)findViewById(R.id.numOfBabies);

            nonstop = (CheckBox)findViewById(R.id.nonstopValue);

            progress = new ProgressDialog(this);
            progress.setTitle("Αναζήτηση πτήσεων");
            progress.setMessage("Παρακαλώ περιμένε...");
            progress.setCancelable(false);

            Button findButton = (Button)findViewById(R.id.find);
            findButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progress.show();

                    String[] temp;
                    if (!autoComplete2.getText().toString().equals("")) {
                        temp = autoComplete2.getText().toString().split("\\[");
                        origin = temp[1].replace("]", "");
                    } else origin = null;
                    if (!autoComplete4.getText().toString().equals("")) {
                        temp = autoComplete4.getText().toString().split("\\[");
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
                        Toast.makeText(MainActivity.this, "Η αφετηρία δεν μπορεί να είναι ίδια με τον προορισμό", Toast.LENGTH_SHORT).show();
                    } else if (d1!=null && d2!=null && d2.before(d1)) {
                        Toast.makeText(MainActivity.this, "Η ημερομηνία επιστροφής είναι πριν την ημερομηνία αναχώρησης", Toast.LENGTH_SHORT).show();
                    } else {
                        String jsonPath = "https://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?apikey=" + apiKey + "&origin=" + origin + "&destination="
                                + destination + "&departure_date=" + formattedDepartureDate + "&currency=EUR" + "&adults=" + numOfAdults.getText() + "&children="
                                + numOfKids.getText() + "&infants=" + numOfBabies.getText() + "&nonstop=" + nonstop.isChecked();
                        if (formattedArrivalDate != null) jsonPath += "&arrival_date=" + formattedArrivalDate;
                        String tester = "http://validate.jsontest.com/?json=" + jsonPath;
                        new jsonResults().execute(jsonPath);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
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

    public class jsonAirports extends AsyncTask<String,String,String>
    {
        protected String doInBackground(String... params)
        {
            String countryJsonStr = null;
            try {
                countryJsonStr = httpRequest(params[0]);

                JSONArray result = new JSONArray(countryJsonStr);

                for (int i=0;i<result.length();i++)
                {
                    JSONObject results = result.getJSONObject(i);
                    label.add(results.getString("label"));
                }
                return countryJsonStr;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return countryJsonStr;
        }
    }

    public class jsonTest extends AsyncTask<String,String,String>
    {
        protected String doInBackground(String... params)
        {
            String test = null;
            try {
                test = httpRequest(params[0]);

                JSONObject result = new JSONObject(test);

                if (result.getString("validate").equals("false"))
                return test;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return test;
        }
    }

    public class jsonResults extends AsyncTask<String,String,String>
    {
        protected String doInBackground(String... params)
        {
            try {
                JSONArray formattedFlights = new JSONArray();
                String buffer = httpRequest(params[0]);

                JSONArray results = new JSONObject(buffer).getJSONArray("results");

                JSONObject flight;
                JSONArray numOfFlights;
                JSONObject flightDetails;
                JSONArray outboundFlights;

                String flightDuration = null;
                String midDestination = null;

                buffer = httpRequest("http://nano.aviasales.ru/places_en?" + "&term=" + origin);
                String formattedOrigin = new JSONArray(buffer).getJSONObject(0).getString("name");
                String[] temp = formattedOrigin.split(",");
                formattedOrigin = temp[0];
                buffer = httpRequest("http://nano.aviasales.ru/places_en?" + "&term=" + destination);
                String formattedDestination = new JSONArray(buffer).getJSONObject(0).getString("name");
                String[] temp2 = formattedDestination.split(",");
                formattedDestination = temp2[0];
                System.out.println(formattedOrigin+formattedDestination);

                for (int i=0; i<results.length(); i++) {
                    flight = results.getJSONObject(i);
                    formattedFlights.put(new JSONObject().put("flights", new JSONArray()));
                    numOfFlights = flight.getJSONArray("itineraries");
                    for (int j = 0; j < numOfFlights.length(); j++) {
                        outboundFlights = numOfFlights.getJSONObject(j).getJSONObject("outbound").getJSONArray("flights");
                        for (int k = 0; outboundFlights.length() > k; k++)
                        {
                            flightDetails = outboundFlights.getJSONObject(k);
                            if (outboundFlights.length() > 1) {
                                buffer = httpRequest("http://nano.aviasales.ru/places_en?" + "&term=" + outboundFlights.getJSONObject(k).getJSONObject("destination").getString("airport"));
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

                            formattedFlights.getJSONObject(i).getJSONArray("flights").put(new JSONObject()
                                .put("origin", formattedOrigin)
                                .put("destination", formattedDestination)
                                .put("company", company)
                                .put("departureTime", flightDetails.getString("departs_at"))
                                .put("arrivalTime", flightDetails.getString("arrives_at"))
                                .put("flightDuration", flightDuration)
                                .put("seatsRemaining", flightDetails.getJSONObject("booking_info").getString("seats_remaining"))
                                .put("intermediateStop", midDestination)
                                .put("price", flight.getJSONObject("fare").getString("total_price") + " €")
                                .put("index", Integer.toString(j)));
                        }

                    }
                }
                progress.dismiss();

                Intent i = new Intent(getApplicationContext(), FlightResults.class);
                i.putExtra("flights", formattedFlights.toString());
                startActivity(i);
                return "Success";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
