package com.example.diazapps.startandend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<MyNotes> {

    public CustomAdapter(Context context, ArrayList<MyNotes> note) {
        super(context, R.layout.customrow, note);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // default -  return super.getView(position, convertView, parent);

        LayoutInflater myCustomInflater = LayoutInflater.from(getContext());
        View customView = myCustomInflater.inflate(R.layout.customrow, parent, false);


        TextView noteText = (TextView) customView.findViewById(R.id.noteText);
        TextView dateStart = (TextView) customView.findViewById(R.id.dateStart);
        TextView dateReturn = (TextView) customView.findViewById(R.id.dateReturn);
        TextView daysLeft = (TextView) customView.findViewById(R.id.daysLeft);

        String textofNote = getItem(position).getNoteText();
        String startdateofNote = "Start date: " + getItem(position).getDate();
        String returndateofNote = "End date: " + getItem(position).getReturndate();
        String daysLeftText = "";


        noteText.setText(textofNote);
        dateStart.setText(startdateofNote);
        dateReturn.setText(returndateofNote);
        daysLeft.setText(String.valueOf(getItem(position).getDaysLeft()));
        if(getItem(position).getisSelected() == true)
        {
            customView.setBackgroundResource(R.color.bar_color);
        }
        else if(getItem(position).getisSelected() == false)
        {
            customView.setBackgroundResource(android.R.drawable.list_selector_background);
        }

        return customView;

    }
}
