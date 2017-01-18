package com.example.kostas.pamakflights;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class FlightsAdapter extends ArrayAdapter<JSONObject> {

    private int resource;
    //Initialize adapter
    public FlightsAdapter(Context context, int resource, ArrayList<JSONObject> items) {
        super(context, resource, items);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout flightView;
        JSONObject flight = getItem(position);

        //Inflate the view
        if(convertView == null)
        {
            flightView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vinf;
            vinf = (LayoutInflater)getContext().getSystemService(inflater);
            vinf.inflate(resource, flightView, true);
        }
        else flightView = (LinearLayout) convertView;
        //Get the text boxes from the list_item.xml file
        TextView alertText =(TextView)flightView.findViewById(R.id.departureDateText);
        TextView alertDate =(TextView)flightView.findViewById(R.id.arrivalDateText);

        //Assign the appropriate data from our alert object above
        try {
            if (flight != null) {
                alertText.setText(flight.getString("departureTime"));
                alertDate.setText(flight.getString("arrivalTime"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return flightView;
    }
}