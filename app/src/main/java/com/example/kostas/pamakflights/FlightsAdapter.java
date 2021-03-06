package com.example.kostas.pamakflights;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

class FlightsAdapter extends ArrayAdapter<JSONObject> {

    private int resource;
    //Initialize adapter
    FlightsAdapter(Context context, int resource, ArrayList<JSONObject> items) {
        super(context, resource, items);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        RelativeLayout flightView;
        JSONObject flight = getItem(position);

        //Inflate the view
        if(convertView == null)
        {
            flightView = new RelativeLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vinf;
            vinf = (LayoutInflater)getContext().getSystemService(inflater);
            vinf.inflate(resource, flightView, true);
        }
        else {
            flightView = (RelativeLayout) convertView;
        }
        TextView price =(TextView)flightView.findViewById(R.id.price);
        TextView info =(TextView)flightView.findViewById(R.id.infoField);
        TextView depTime =(TextView)flightView.findViewById(R.id.depTime);
        TextView arrTime =(TextView)flightView.findViewById(R.id.arrTime);
        TextView flightDuration =(TextView)flightView.findViewById(R.id.duration);
        TextView depAirport =(TextView)flightView.findViewById(R.id.depAiport);
        TextView arrAirport =(TextView)flightView.findViewById(R.id.arrAirport);
        TextView directOrNot =(TextView)flightView.findViewById(R.id.directFlightorNot);
        TextView diffDay =(TextView)flightView.findViewById(R.id.diffDay);
        TextView cities = (TextView)flightView.findViewById(R.id.cities);

        try {
            if (flight != null) {
                String tempDep = flight.getString("departureTime");
                String tempArr = flight.getString("arrivalTime");
                price.setText(flight.getString("price"));
                price.setTextColor(Color.RED);
                info.setText(flight.getString("company"));
                depTime.setText(tempDep.substring((tempDep.length() - 5)));
                depTime.setTextColor(Color.BLUE);
                arrTime.setText(tempArr.substring((tempArr.length() - 5)));
                arrTime.setTextColor(Color.BLUE);
                flightDuration.setText(flight.getString("flightDuration"));
                depAirport.setText(flight.getString("origin"));
                arrAirport.setText(flight.getString("destination"));
                if (!flight.has("intermediateStop")) directOrNot.setText("Απ' ευθείας πτήση");
                else directOrNot.setText("Πτήση με ανταπόκριση");
                if (flight.has("diffDay")) diffDay.setText("+1 ημέρα");
                else diffDay.setText("");
                if (flight.has("intermediateStop")) cities.setText("Μέσω: " + flight.getString("intermediateStop"));
                else cities.setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return flightView;
    }
}