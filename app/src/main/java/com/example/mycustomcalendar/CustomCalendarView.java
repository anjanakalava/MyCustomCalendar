package com.example.mycustomcalendar;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class CustomCalendarView extends LinearLayout {
    ImageButton PreviousButton, NextButton;
    TextView CurrentDate;
    GridView gridView;
    private static final int MAX_CALENDAR_Days = 42;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;
    DBOpenHandler dbOpenHelper;
    AlertDialog alertDialog;
    JsonTask jt=new JsonTask();
    String trash_pickup="", recycle_pickup="", compost_pickup="";
    MyGridAdapter myGridAdapter;
    List<Events> eventsList = new ArrayList<>();
    List<Date> dateList = new ArrayList<>();


    public CustomCalendarView(Context context) {
        super(context);
    }

    public CustomCalendarView(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        try {
            IntializeUILayout();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SetupCalendar();
        PreviousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH,-1);
                SetupCalendar();
            }
        });

        NextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH,1);
                SetupCalendar();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String date = dateFormat.format(dateList.get(position));
                AlertDialog.Builder builder= new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View showview=LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_layout, null);
                RecyclerView recyclerView=showview.findViewById(R.id.EventsRV);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(showview.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                EventRecyclerAdapter eventRecyclerAdapter= new EventRecyclerAdapter(showview.getContext(), CollectEventsbyDate(date));
                recyclerView.setAdapter(eventRecyclerAdapter);
                eventRecyclerAdapter.notifyDataSetChanged();

                builder.setView(showview);
                alertDialog=builder.create();
                alertDialog.show();
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder =new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View eventView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_event_layout,null);
                final EditText EventName = eventView.findViewById(R.id.eventName);
                final TextView EventTime = eventView.findViewById(R.id.eventtime);
                ImageButton SelectTime = eventView.findViewById(R.id.setEventTime);
                Button AddEvent = eventView.findViewById(R.id.addevent);
                SelectTime.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();

                        final int hours =calendar.get(Calendar.HOUR_OF_DAY);
                        final int mints = calendar.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog;
                        timePickerDialog= new TimePickerDialog(getContext(), R.style.Theme_AppCompat_Dialog, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                c.set(Calendar.MINUTE,minute);
                                c.setTimeZone(TimeZone.getDefault());
                                SimpleDateFormat format = new SimpleDateFormat("K:mm a", Locale.ENGLISH);
                                String PlannedTime = format.format(c.getTime());
                                EventTime.setText(PlannedTime);
                            }
                        }, hours, mints, false);
                        timePickerDialog.show();
                    }
                });

                final String date= dateFormat.format(dateList.get(position));
                final String month=monthFormat.format(dateList.get(position));
                final String year=yearFormat.format(dateList.get(position));

                AddEvent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SaveEvent(EventName.getText().toString(),EventTime.getText().toString(),date, month, year);
                        SetupCalendar();
                        alertDialog.dismiss();
                    }
                });

                builder.setView(eventView);
                alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });

    }

    private ArrayList<Events> CollectEventsbyDate(String date){
        ArrayList<Events> arrayList = new ArrayList<>();
        dbOpenHelper = new DBOpenHandler(context);
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadEvents(date,sqLiteDatabase);
        if(cursor!=null) {
            while (cursor.moveToNext()) {
                String event = cursor.getString(cursor.getColumnIndex(DataBaseStructure.EVENT));
                String Time = cursor.getString(cursor.getColumnIndex(DataBaseStructure.TIME));
                String Date = cursor.getString(cursor.getColumnIndex(DataBaseStructure.DATE));
                String month = cursor.getString(cursor.getColumnIndex(DataBaseStructure.MONTH));
                String year = cursor.getString(cursor.getColumnIndex(DataBaseStructure.YEAR));
                Events events = new Events(event, Time, Date, month, year);
                arrayList.add(events);
            }
        }
        cursor.close();

        return arrayList;
    }
    private int setEvents(String dbday, String EventName){
        String start=dateFormat.format(calendar.getInstance().getTime());
        System.out.println("start date"+start);
        int day=calendar.getActualMaximum(calendar.DAY_OF_MONTH);
        SimpleDateFormat monthFormat1 = new SimpleDateFormat("MM", Locale.ENGLISH);
        String end = yearFormat.format(calendar.getTime())+"-"+monthFormat1.format(calendar.getTime())+"-"+Integer.toString(day);
        System.out.println("end date"+end);
        DateTimeFormatter pattern = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime startDate = pattern.parseDateTime(start);
        DateTime endDate = pattern.parseDateTime(end);
        //DateTimeConstants[] dateTimeConstants= DateTimeConstants.values();
        List<DateTime> eventdays = new ArrayList<>();
        boolean reachedAFriday = false;
        dbday=dbday.toLowerCase();
        System.out.println("dbday "+dbday );
        while (startDate.isBefore(endDate) || startDate.isEqual(endDate)){
            System.out.println("Inside while loop");
            switch(dbday)
            {

                case "sunday": {
                    if(dbday.equals("sunday"))
                    {
                        System.out.println("It is equal"+ startDate.getDayOfWeek() +  DateTimeConstants.SUNDAY);
                    }
                    if (startDate.getDayOfWeek() == DateTimeConstants.SUNDAY) {
                        System.out.println("Inside sunday");
                        eventdays.add(startDate);
                        reachedAFriday = true;
                    }
                    break;
                }
                case "monday":{
                    if ( startDate.getDayOfWeek() == DateTimeConstants.MONDAY ){
                        eventdays.add(startDate);
                        reachedAFriday = true;
                    }
                    break;}
                case "tuesday": {
                    if (startDate.getDayOfWeek() == DateTimeConstants.TUESDAY) {
                        eventdays.add(startDate);
                        reachedAFriday = true;
                    }
                    break;
                }
                case "wednesday": {
                    if(dbday.equals("wednesday"))
                    {
                        System.out.println("It is equal"+ startDate.getDayOfWeek() +  DateTimeConstants.WEDNESDAY);
                    }
                    if (startDate.getDayOfWeek() == DateTimeConstants.WEDNESDAY) {
                        eventdays.add(startDate);
                        reachedAFriday = true;
                    }
                    break;
                }
                case "thursday": {
                    if (startDate.getDayOfWeek() == DateTimeConstants.THURSDAY) {
                        eventdays.add(startDate);
                        reachedAFriday = true;
                    }
                    break;
                }
                case "friday": {
                    if (startDate.getDayOfWeek() == DateTimeConstants.FRIDAY) {
                        eventdays.add(startDate);
                        reachedAFriday = true;
                    }
                    break;
                }
                case "saturday": {
                    if(dbday.equals("wednesday"))
                    {
                        System.out.println("It is equal"+ startDate.getDayOfWeek() +  DateTimeConstants.WEDNESDAY);
                    }
                    if (startDate.getDayOfWeek() == DateTimeConstants.SATURDAY) {
                        eventdays.add(startDate);
                        reachedAFriday = true;
                    }
                    break;
                }
                default:
                    System.out.println("Inside default block");
                    break;
            }
           /* if ( startDate.getDayOfWeek() == DateTimeConstants.FRIDAY ){
                fridays.add(startDate);
                reachedAFriday = true;
            }*/

            if ( reachedAFriday ){
                System.out.println("Inside true case");
                startDate = startDate.plusWeeks(1);
            } else {
                startDate = startDate.plusDays(1);
            }
        }
       // final String EventName="Trash Pickup";
        final String EventTime="09:00";
        System.out.println("size"+eventdays.size());
    for(int i=0;i<eventdays.size();i++){
            final String date=dateFormat.format(eventdays.get(i).toDate());
            final String month=monthFormat.format(eventdays.get(i).toDate());
            final String year=yearFormat.format(eventdays.get(i).toDate());
            SaveEvent(EventName,EventTime,date, month, year);
        }
    return eventdays.size();
    }
    private void delEvent(){
        dbOpenHelper=new DBOpenHandler(context);
        SQLiteDatabase database=dbOpenHelper.getReadableDatabase();
        dbOpenHelper.deleteEvent(database);
    }
    private void SaveEvent(String event,String time,String date,String Month,String Year){
        dbOpenHelper = new DBOpenHandler(context);
        SQLiteDatabase database= dbOpenHelper.getWritableDatabase();
        //dbOpenHelper.onCreate(database);
        dbOpenHelper.SaveEvent(event, time, date, Month, Year, database);
        dbOpenHelper.close();
        System.out.println("Events Saved");
       // Toast.makeText(context, "Event Saved", Toast.LENGTH_SHORT).show();
    }
    private void IntializeUILayout() throws InterruptedException {

        LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout,this);
        PreviousButton = view.findViewById(R.id.previousB);
        NextButton = view.findViewById(R.id.nextB);
        CurrentDate = view.findViewById(R.id.current_Date);
        gridView = view.findViewById(R.id.gridview);
        HTTPHandler sh = new HTTPHandler();
        // Making a request to url and getting response
        String url = "https://www-student.cse.buffalo.edu/~sindhuso/pickup_days/Zones/getZone.php?zipcode=14214";
        try {
            jt.execute(url);
            Thread.sleep(4000);
        }catch(Exception e){

        }
        //String jsonstr= String.valueOf(new JsonTask().execute(url));

        System.out.println("json string"+ jt.jsonStr);
    /*    String jsonStr=sh.makeServiceCall(url);
        //Log.e(TAG, "Response from url: " + jsonStr);*/
        if (jt.jsonStr!= null) {
            StringTokenizer st=new StringTokenizer(jt.jsonStr, ",");
            while(st.hasMoreTokens()){
                String str=st.nextToken();
                if(str.contains("trash_pickup")){
                    int pos=str.lastIndexOf(':');
                    trash_pickup=str.substring(pos+2,str.length()-1);
                }
                if(str.contains("recycle_pickup")){
                    int pos=str.lastIndexOf(':');
                    recycle_pickup=str.substring(pos+2,str.length()-1);
                }
                if(str.contains("compost_pickup")){
                    int pos=str.lastIndexOf(':');
                    compost_pickup=str.substring(pos+2,str.length()-1);
                }

            }
        }
    }

    private void SetupCalendar() {
        String StartDate = simpleDateFormat.format(calendar.getTime());
        CurrentDate.setText(StartDate);
        dateList.clear();
        int size=0,size1=0,s=0;
        Calendar monthCalendar = (Calendar)calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH,1);
        int FirstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)-1;
        monthCalendar.add(Calendar.DAY_OF_MONTH,-FirstDayOfMonth);
        //System.out.println("json string"+ jt.jsonStr);
        delEvent();
        if(!trash_pickup.equals("")) {
            size = setEvents(trash_pickup, "Trash Pickup");
        }
        if(!recycle_pickup.equals("")) {
            size1 = setEvents(recycle_pickup, "Recycle Pickup");
        }
        if(!compost_pickup.equals("")) {
            s = setEvents(compost_pickup, "Compost Pickup");
        }
        if(size+size1+s>0)
            CollectEventsPerMonth(monthFormat.format(calendar.getTime()), yearFormat.format(calendar.getTime()));

        while (dateList.size() < MAX_CALENDAR_Days){
            dateList.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH,1);

        }
        myGridAdapter = new MyGridAdapter(context,dateList,calendar,eventsList);
        gridView.setAdapter(myGridAdapter);
    }

    private void CollectEventsPerMonth(String month, String year){
        eventsList.clear();
        dbOpenHelper=new DBOpenHandler(context);
        SQLiteDatabase database=dbOpenHelper.getReadableDatabase();
        Cursor cursor=dbOpenHelper.ReadEventsPerMonth(month,year,database);
        if(cursor!=null) {
            while (cursor.moveToNext()) {
                String event = cursor.getString(cursor.getColumnIndex(DataBaseStructure.EVENT));
                String Time = cursor.getString(cursor.getColumnIndex(DataBaseStructure.TIME));
                String Date = cursor.getString(cursor.getColumnIndex(DataBaseStructure.DATE));
                String Month1 = cursor.getString(cursor.getColumnIndex(DataBaseStructure.MONTH));
                String Year1 = cursor.getString(cursor.getColumnIndex(DataBaseStructure.YEAR));
                Events events1 = new Events(event, Time, Date, Month1, Year1);
                eventsList.add(events1);
            }
        }
        cursor.close();
        dbOpenHelper.close();
    }

}