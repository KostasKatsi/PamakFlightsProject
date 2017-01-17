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
        if(convertView==null)
        {
            flightView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater)getContext().getSystemService(inflater);
            vi.inflate(resource, flightView, true);
        }
        else
        {
            flightView = (LinearLayout) convertView;
        }
        //Get the text boxes from the listitem.xml file
        TextView alertText =(TextView)flightView.findViewById(R.id.txtAlertText);
        TextView alertDate =(TextView)flightView.findViewById(R.id.txtAlertDate);

        //Assign the appropriate data from our alert object above
        try {
            if (flight != null) {
                alertText.setText(flight.getString("origin"));
                alertDate.setText(flight.getString("destination"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return flightView;
    }
}