package com.example.mycustomcalendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;

public class EventRecyclerAdapter extends Adapter<EventRecyclerAdapter.MyViewHolder> {
    ArrayList<Events> arrayList;
    Context context;

    public class MyViewHolder extends ViewHolder {
        TextView Date;
        TextView Event;
        TextView Time;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.Date = (TextView) itemView.findViewById(R.id.eventdate);
            this.Time = (TextView) itemView.findViewById(R.id.eventime);
            this.Event = (TextView) itemView.findViewById(R.id.eventname);
        }
    }

    public EventRecyclerAdapter(Context context2, ArrayList<Events> arrayList2) {
        this.context = context2;
        this.arrayList = arrayList2;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_rowlayout, parent, false));
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        Events events = (Events) this.arrayList.get(position);
        holder.Event.setText(events.getEvent());
        holder.Time.setText(events.getTime());
        holder.Date.setText(events.getDate());
    }

    public int getItemCount() {
        return this.arrayList.size();
    }
}
