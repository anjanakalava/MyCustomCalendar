package com.example.mycustomcalendar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyGridAdapter extends ArrayAdapter {
    Calendar currentDate;
    List<Date> dates;
    List<Events> events;
    LayoutInflater inflater;

    public MyGridAdapter(Context context, List<Date> dates2, Calendar currentDate2, List<Events> events2) {
        super(context, R.layout.single_cell_layout);
        this.dates = dates2;
        this.currentDate = currentDate2;
        this.inflater = LayoutInflater.from(context);
        this.events = events2;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ArrayList arrayList;
        MyGridAdapter myGridAdapter = this;
        View view = convertView;
        Date monthDate = (Date) myGridAdapter.dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);
        int DayNo = dateCalendar.get(5);
        int displayMonth = dateCalendar.get(2) + 1;
        int displayYear = dateCalendar.get(1);
        int currentYear = myGridAdapter.currentDate.get(1);
        int currentMonth = myGridAdapter.currentDate.get(2) + 1;
        if (view == null) {
            view = myGridAdapter.inflater.inflate(R.layout.single_cell_layout, parent, false);
        } else {
            ViewGroup viewGroup = parent;
        }
        if (displayMonth == currentMonth && displayYear == currentYear) {
            view.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        } else {
            view.setBackgroundColor(Color.parseColor("#cccccc"));
        }
        TextView eventNumber = (TextView) view.findViewById(R.id.events_id);
        ((TextView) view.findViewById(R.id.calendar_day)).setText(String.valueOf(DayNo));
        Calendar eventCalendar = Calendar.getInstance();
        ArrayList arrayList2 = new ArrayList();
        int i = 0;
        while (i < myGridAdapter.events.size()) {
            eventCalendar.setTime(myGridAdapter.convertStringToDate(((Events) myGridAdapter.events.get(i)).getDate()));
            Date monthDate2 = monthDate;
            if (DayNo != eventCalendar.get(5) || displayMonth != eventCalendar.get(2) + 1) {
                arrayList = arrayList2;
            } else if (displayYear == eventCalendar.get(1)) {
                arrayList = arrayList2;
                arrayList.add(((Events) myGridAdapter.events.get(i)).getEvent());
                StringBuilder sb = new StringBuilder();
                sb.append(arrayList.size());
                sb.append(" events");
                eventNumber.setText(sb.toString());
            } else {
                arrayList = arrayList2;
            }
            i++;
            myGridAdapter = this;
            arrayList2 = arrayList;
            monthDate = monthDate2;
        }
        return view;
    }

    private Date convertStringToDate(String dateInString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getCount() {
        return this.dates.size();
    }

    public Object getItem(int position) {
        return this.dates.get(position);
    }

    public int getPosition(Object item) {
        return this.dates.indexOf(item);
    }
}

