package com.example.timetablefragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeTable#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeTable extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Calendar calendarNew;
    private int numOfRow = 8;
    private TableRow row;
    private TextView auxText;
    int tableLayoutChildNum;
    TableLayout table;
    TableLayout table_time;
    HashMap<Integer, String> weekDay = new HashMap<Integer, String>();
    private int MAKE_EVENT_REQUEST_CODE = 1;
    private int startYr, startMon, startDay, startHr, startMin, endYr, endMon, endDay, endHr, endMin;
    private String title, organizers, venue, eventDescription;
    public EventViewModel mViewModel;
    private Bundle reply = new Bundle();
    private List<Events> eventLive;
    Button refresh;
    int tabHeight, tabWidth;
    CalendarView cal;

    public Calendar timeStart, timeEnd, timeCursor;
    TableRow.LayoutParams param;

    public TimeTable() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimeTable.
     */
    // TODO: Rename and change types and number of parameters
    public static TimeTable newInstance() {
        TimeTable fragment = new TimeTable();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        weekDay.put(7,"Sat");
        weekDay.put(1,"Sun");
        weekDay.put(2,"Mon");
        weekDay.put(3,"Tue");
        weekDay.put(4,"Wed");
        weekDay.put(5,"Thur");
        weekDay.put(6,"Fri");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_time_table, container, false);
        final Button editEvent = rootView.findViewById(R.id.editEvent);

        table = rootView.findViewById(R.id.table_date);
        table_time = rootView.findViewById(R.id.table_time);
        refresh = rootView.findViewById(R.id.refresh);
        cal = rootView.findViewById(R.id.calendar);
        tableLayoutChildNum = table.getChildCount();

        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action = TimeTableDirections.actionTimeTableToEditEvent();
                Navigation.findNavController(v).navigate(action);
                rootView.setVisibility(View.GONE);
            }
        });
        Calendar calendarInit = Calendar.getInstance();
        populateHrMin(calendarInit);
        populateTimeTableFrame(calendarInit);

        calendarNew = Calendar.getInstance();
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int yr, int mon, int day) {
                calendarNew.set(yr, mon, day);
                populateTimeTableFrame(calendarNew);
                refreshTimeTable(yr, mon, day, 0, 0);
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArguments() != null){
                    title = getArguments().getString("eventTitle");
                    eventDescription = getArguments().getString("eventDescription");
                    organizers = getArguments().getString("organizers");
                    venue = getArguments().getString("venue");
                    startYr = getArguments().getIntegerArrayList("startDateAndTime").get(0);
                    startMon = getArguments().getIntegerArrayList("startDateAndTime").get(1);
                    startDay = getArguments().getIntegerArrayList("startDateAndTime").get(2);
                    startHr = getArguments().getIntegerArrayList("startDateAndTime").get(3);
                    startMin = getArguments().getIntegerArrayList("startDateAndTime").get(4);
                    endYr = getArguments().getIntegerArrayList("endDateAndTime").get(0);
                    endMon = getArguments().getIntegerArrayList("endDateAndTime").get(1);
                    endDay = getArguments().getIntegerArrayList("endDateAndTime").get(2);
                    endHr = getArguments().getIntegerArrayList("endDateAndTime").get(3);
                    endMin = getArguments().getIntegerArrayList("endDateAndTime").get(4);
                }
                refreshTimeTable(startYr, startMon, startDay, startHr, startMin);
            }
        });
        mViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        mViewModel.getAllEvents().observe(getViewLifecycleOwner(), new Observer<List<Events>>() {
            @Override
            public void onChanged(List<Events> events) {
                eventLive = events;
                if (eventLive != null){
                    Toast.makeText(getContext(), Integer.toString(eventLive.size()), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rootView;
    }
    public void populateHrMin(Calendar theDate){

        theDate.set(Calendar.HOUR_OF_DAY,0);
        theDate.set(Calendar.MINUTE,0);
        for (int i=0; i<96; i++){
            int hour = theDate.get(Calendar.HOUR_OF_DAY);
            int min = theDate.get(Calendar.MINUTE);
            String time = String.format("%02d:%02d", hour, min);

            row = (TableRow) table_time.getChildAt(0);
            auxText = new TextView(getContext());
            auxText.setText(time);
            auxText.setWidth(120);
            tabWidth = auxText.getWidth();
            param = new TableRow.LayoutParams();
            param.column = 1;
            row.addView(auxText, param);
            theDate.add(Calendar.MINUTE, 15);
        }
    }
    public void populateTimeTableFrame(Calendar theDate){
        for (int i=1; i<numOfRow; i++) {
            row = (TableRow) table.getChildAt(i);
            TextView textDate = (TextView) row.getChildAt(0);
            int monNew = theDate.get(Calendar.MONTH);
            int dayNew = theDate.get(Calendar.DAY_OF_MONTH);
            int dayWeek = theDate.get(Calendar.DAY_OF_WEEK);
            String date = String.format("%02d/%02d(%s)", monNew + 1, dayNew, weekDay.get(dayWeek));
            textDate.setText(date);
            textDate.setTextColor(Color.WHITE);
            textDate.setBackgroundColor(getResources().getColor(R.color.purple_500));
            textDate.setWidth(200);
            tabHeight = textDate.getHeight();
            theDate.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
    private void displayEventOnTable(Calendar start, Calendar end, String eventTitle, int rowIndex, int itemColor, int itemTextColor){
        int startDay = start.get(Calendar.DAY_OF_MONTH);
        int startHr = start.get(Calendar.HOUR_OF_DAY);
        int startMin = start.get(Calendar.MINUTE);
        int endDay = end.get(Calendar.DAY_OF_MONTH);
        int endHr = end.get(Calendar.HOUR_OF_DAY);
        int endMin = end.get(Calendar.MINUTE);
        int span = 0;
        int col = 0;
        if (endDay == startDay){
            span = (endHr - startHr) * (4) + (endMin - startMin) / 15;
            col = startHr * 4 + startMin / 15 + 1;
        }
        row = (TableRow) table_time.getChildAt(rowIndex);
        auxText = new TextView(getContext());
        auxText.setTextColor(itemTextColor);
        auxText.setText(eventTitle);
        auxText.setBackgroundColor(itemColor);
        auxText.setHeight(tabHeight);
        auxText.setWidth(tabWidth);
        param = new TableRow.LayoutParams();
        param.column = col;
        param.span = span;
        row.addView(auxText, param);
    }
    private void createRow(int rowIndex){
        row = new TableRow(getContext());
        table_time.addView(row, rowIndex);
    }
    public void refreshTimeTable(int sYr, int sMon, int sDay, int sHr, int sMin) {
        timeCursor = Calendar.getInstance();
        timeCursor.set(sYr, sMon, sDay, sHr, sMin);
        populateTimeTableFrame(timeCursor);
        Util.removeTableRowViews(row, numOfRow, table_time);
        timeCursor.add(Calendar.DAY_OF_MONTH,-7);
        for (int i=1; i<8; i++){
            boolean eventExisted = false;
            createRow(i);
            for (Events event: eventLive){
                timeStart = Calendar.getInstance();
                timeStart.set(event.startYear, event.startMonth, event.startDay, event.startHour, event.startMin);
                timeEnd = Calendar.getInstance();
                timeEnd.set(event.endYear, event.endMonth, event.endDay, event.endHour, event.endMin);
                if (timeCursor.get(Calendar.YEAR) == timeStart.get(Calendar.YEAR) &&
                        timeCursor.get(Calendar.MONTH)== timeStart.get(Calendar.MONTH) &&
                        timeCursor.get(Calendar.DAY_OF_MONTH) == timeStart.get(Calendar.DAY_OF_MONTH)){
                    eventExisted = true;
                    displayEventOnTable(timeStart, timeEnd, event.eventTitle, i, getResources().getColor(R.color.purple_500), Color.WHITE);
                }
            }
            if (!eventExisted){
                timeStart.set(Calendar.MONTH, timeCursor.get(Calendar.MONTH));
                timeStart.set(Calendar.DAY_OF_MONTH, timeCursor.get(Calendar.DAY_OF_MONTH));
                timeStart.set(Calendar.HOUR_OF_DAY, 0);
                timeStart.set(Calendar.MINUTE, 0);
                timeEnd.set(Calendar.MONTH, timeCursor.get(Calendar.MONTH));
                timeEnd.set(Calendar.DAY_OF_MONTH, timeCursor.get(Calendar.DAY_OF_MONTH));
                timeEnd.set(Calendar.HOUR_OF_DAY, 23);
                timeEnd.set(Calendar.MINUTE, 45);
                displayEventOnTable(timeStart, timeEnd, "No event today yet", i, getResources().getColor(R.color.grey), Color.WHITE);
            }
            timeCursor.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
}