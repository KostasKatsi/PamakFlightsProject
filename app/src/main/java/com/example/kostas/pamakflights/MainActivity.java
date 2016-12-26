package com.example.kostas.pamakflights;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView autoComplete;
    private ArrayAdapter<String> adapter;
    private AutoCompleteTextView autoComplete2;
    private AutoCompleteTextView autoComplete3;
    private AutoCompleteTextView autoComplete4;
    private ArrayAdapter<String> adapter1;
    private ArrayAdapter<String> adapter2;
    private HttpURLConnection urlConnection = null;
    private BufferedReader reader = null;
    private String countryJsonStr = null;
    private String input = null;
    private String apikey = null;
    int index = 0;
    private ArrayList<String> countryNames = new ArrayList<String>();
    private ArrayList<String> countryCodes = new ArrayList<String>();
    private String code = null;
    private String origin =null;
    private String destination = null;
    private ArrayList<String> label = new ArrayList<String>();
    private Context mContext;
    private InputStream inp= null;
    private Button btn = null;
    private Button btn2 = null;
    private RelativeLayout layout = null;


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

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countryNames);
            autoComplete = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
            autoComplete.setAdapter(adapter);
            autoComplete.setThreshold(1);
            autoComplete3 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView3);
            autoComplete3.setAdapter(adapter);
            autoComplete3.setThreshold(1);

            apikey = readKeyFromFile();


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

                        new JSONairports().execute("https://api.sandbox.amadeus.com/v1.2/airports/autocomplete?"+apikey+"&country="+countryCodes.get(index));
                    }
                    origin = code;
                    adapter1 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, label);
                    autoComplete2.setAdapter(adapter1);
                    autoComplete2.setThreshold(1);

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

                        new JSONairports().execute("https://api.sandbox.amadeus.com/v1.2/airports/autocomplete?"+apikey+"&country="+countryCodes.get(index));
                    }
                    destination = code;
                    adapter2 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, label);
                    autoComplete4.setAdapter(adapter2);
                    autoComplete4.setThreshold(1);

                }


            });

            btn = (Button)findViewById(R.id.more);
            layout = (RelativeLayout)findViewById(R.id.hidden);
            btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(layout.getVisibility() != View.VISIBLE) {
                        layout.setVisibility(View.VISIBLE);
                        btn.setText("Λιγοτερα...");
                    }
                    else
                    {
                        layout.setVisibility(View.GONE);
                        btn.setText("Περισσοτερα...");
                    }
                }
            });
//
//            btn2 = (Button)findViewById(R.id.button);
//            btn2.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v){
//
//                }
//            });



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public String loadJSONFromAsset(String name) {
        String json = null;
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

    public String readKeyFromFile(){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("Config.txt"), "UTF-8"));

            // do reading, usually loop until end of file reading
            String mLine;
            StringBuilder sb = new StringBuilder();
            while ((mLine = reader.readLine()) != null) {
                sb.append(mLine);
            }
            return sb.toString();
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return null;
    }

    public class JSONairports extends AsyncTask<String,String,String>
    {
        protected String doInBackground(String... params)
        {
            try {

                URL url = new URL(params[0]);

                // Create the request, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                countryJsonStr = buffer.toString();



                JSONArray result = new JSONArray(countryJsonStr);


                for (int i=0;i<result.length();i++)
                {
                    JSONObject apotelesmata = result.getJSONObject(i);
                    code = apotelesmata.getString("value");
                    label.add(apotelesmata.getString("label"));
                }




                return countryJsonStr;
            } catch (IOException e) {
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivity", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

    }

    public class JSONresults extends AsyncTask<String,String,String>
    {
        protected String doInBackground(String... params)
        {
            try {

                URL url = new URL(params[0]);

                // Create the request , and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                countryJsonStr = buffer.toString();



                JSONArray result = new JSONArray(countryJsonStr);


                for (int i=0;i<result.length();i++)
                {
                    JSONObject apotelesmata = result.getJSONObject(i);
                    code = apotelesmata.getString("value");
                    label.add(apotelesmata.getString("label"));
                }




                return countryJsonStr;
            } catch (IOException e) {
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivity", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

    }
}
