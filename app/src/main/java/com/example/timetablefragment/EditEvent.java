package com.example.timetablefragment;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditEvent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditEvent extends Fragment {
    public Bundle bundle = new Bundle();
    Bundle datePickerArg = new Bundle();
    Bundle timePickerArg = new Bundle();
    Bundle replyBundle = new Bundle();
    private int startYear, startMonth, startDay, startHour, startMin,
            endYear, endMonth, endDay, endHour, endMin;
    private EditText titleText, venueText, eventDescrpText, organizersText;
    private TextView startDate, endDate, startTime, endTime;
    CheckBox setAlarm;
    RadioGroup alarmTime;
    private Button confirm;
    private Intent confirmedDataIntent;
    public EventViewModel mViewModel;
    public static final String CONFIRMED_DATA = "com.example.timetable.extra.CONFIRMED_DATA";
    private String title, eventDescription, organizers, venue;
    private boolean queryChecking = false;
    private String DateAndTimePickerResultKey = "DateAndTimePickerResultKey";
    private String dateTimeText;
    private static Context context;
    private int timeAlarmInMins = 0;
    private RadioButton fifteenMins, thirtyMins, oneHr;
    private NotificationManager mNotificationManager;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final int NOTIFICATION_ID = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditEvent() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditEvent.
     */
    // TODO: Rename and change types and number of parameters
    public static EditEvent newInstance(String param1, String param2) {
        EditEvent fragment = new EditEvent();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getParentFragmentManager().setFragmentResultListener(DateAndTimePickerResultKey, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if (result.getString("resultType").equals("hourAndMin")) {
                    int hr = result.getInt("resultHour");
                    int min = result.getInt("resultMin");
                    int viewId = result.getInt("viewId");
                    processTime(hr, min, viewId);
                } else if (result.getString("resultType").equals("date")){
                    int year = result.getInt("resultYear");
                    int month = result.getInt("resultMonth");
                    int dayOfMonth = result.getInt("resultDayOfMonth");
                    int viewId = result.getInt("viewId");
                    processDate(year, month, dayOfMonth, viewId);
                }
            }
        });
        context = getContext();
        createNotificationChannel();
    }
    public void createNotificationChannel(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mNotificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "Alarm notification", NotificationManager.IMPORTANCE_HIGH);
            mNotificationChannel.enableLights(true);
            mNotificationChannel.setLightColor(Color.RED);
            mNotificationChannel.enableVibration(true);
            mNotificationChannel.setDescription("Set alarm for the events");
            mNotificationManager.createNotificationChannel(mNotificationChannel);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_edit_event, container, false);
        confirm = rootView.findViewById(R.id.confirmButton);
        titleText = rootView.findViewById(R.id.eventTitle);
        eventDescrpText = rootView.findViewById(R.id.eventDescription);
        venueText = rootView.findViewById(R.id.venueText);
        organizersText = rootView.findViewById(R.id.organizers);
        startDate = rootView.findViewById(R.id.startDateText);
        startTime = rootView.findViewById(R.id.startTimeText);
        endDate = rootView.findViewById(R.id.endDateText);
        endTime = rootView.findViewById(R.id.endTimeText);
        setAlarm = rootView.findViewById(R.id.setAlarm);
        setAlarm.setChecked(false);
        alarmTime = rootView.findViewById(R.id.alarmTime);
        alarmTime.setVisibility(View.GONE);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAndConfirmContent(v);
            }
        });
        setAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                alarmTime.setVisibility(View.VISIBLE);
                alarmTime.setOnCheckedChangeListener((group, checkedId) -> {
                    switch (checkedId){
                        case R.id.fifteenMins:
                            timeAlarmInMins = 15;
                            break;
                        case R.id.thirtyMins:
                            timeAlarmInMins = 30;
                            break;
                        case R.id.oneHr:
                            timeAlarmInMins = 60;
                            break;
                    }
                });
            } else {
                alarmTime.setVisibility(View.GONE);
            }
        });
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
        mViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        mViewModel.getAllEvents().observe(getViewLifecycleOwner(), new Observer<List<Events>>() {
            @Override
            public void onChanged(List<Events> events) {
                Toast.makeText(getContext(), Integer.toString(events.size()), Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    public void showDatePickerDialog(View view){
        int viewId = view.getId();
        DialogFragment datePickerDialog = new DatePickerFragment();
        switch(viewId){
            case R.id.startDateText:
                datePickerArg.putInt("datePickerType", R.id.startDateText);
                break;
            case R.id.endDateText:
                datePickerArg.putInt("datePickerType", R.id.endDateText);
                break;
            default:
                Log.d("datePickerDialog", "Unknown source for the dialog");
        }
        datePickerDialog.setArguments(datePickerArg);
        datePickerDialog.show(getParentFragmentManager(), "datePicker");
    }
    public void processDate(int yr, int mon, int day, int viewId){
        dateTimeText = String.format("%04d/%02d/%02d", yr, mon, day);
        switch (viewId){
            case R.id.startDateText:
                startDate.setText(dateTimeText);
                break;
            case R.id.endDateText:
                endDate.setText(dateTimeText);
                break;
        }
        if (viewId == R.id.startDateText){
            startYear = yr;
            startMonth = mon;
            startDay = day;
        } else {
            endYear = yr;
            endMonth = mon;
            endDay = day;
        }
    }
    public void showTimePickerDialog(View view){
        int viewId = view.getId();
        DialogFragment timePickerDialog = new TimePickerFragment();
        switch(viewId) {
            case R.id.startTimeText:
                timePickerArg.putInt("timePickerType", R.id.startTimeText);
                break;
            case R.id.endTimeText:
                timePickerArg.putInt("timePickerType", R.id.endTimeText);
                break;
            default:
                Log.d("timePickerDialog", "Unknown source for the dialog");
        }
        timePickerDialog.setArguments(timePickerArg);
        timePickerDialog.show(getParentFragmentManager(), "timePicker");
    }
    public void processTime(int hr, int min, int viewId) {
        switch (viewId) {
            case R.id.startTimeText:
                if (min < 15 && min > 0) {
                    min = 0;
                } else if (min < 30 && min > 15) {
                    min = 15;
                } else if (min < 45 && min > 30) {
                    min = 30;
                } else if (min > 45) {
                    min = 45;
                }
                dateTimeText = String.format("%02d:%02d", hr, min);
                startTime.setText(dateTimeText);
                break;
            case R.id.endTimeText:
                if (min < 15 && min > 0) {
                    min = 15;
                } else if (min < 30 && min > 15) {
                    min = 30;
                } else if (min < 45 && min > 30) {
                    min = 45;
                } else if (min > 45) {
                    min = 0;
                    hr = hr + 1;
                }
                dateTimeText = String.format("%02d:%02d", hr, min);
                endTime.setText(dateTimeText);
                break;
        }
        if (viewId == R.id.startTimeText) {
            startHour = hr;
            startMin = min;
        } else {
            endHour = hr;
            endMin = min;
        }
        Toast.makeText(getContext(), "Notes: Resolution of minutes is 15 mins", Toast.LENGTH_SHORT).show();
    }

    public void verifyAndConfirmContent(View v){
        title = titleText.getText().toString();
        eventDescription = eventDescrpText.getText().toString();
        organizers = organizersText.getText().toString();
        venue = venueText.getText().toString();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(eventDescription)||TextUtils.isEmpty(organizers)
                ||TextUtils.isEmpty(venue)){
            Toast.makeText(getContext(), "All text fields should be filled.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!queryChecking){
            mViewModel.makeCriteriaEvents(startYear,startMonth,startDay);
            queryChecking = true;
        }
        if (!Util.queryCriteriaBeforeConfirm(mViewModel, startHour, startMin, endHour, endMin)){
            return;
        }
        insertValue();
        mViewModel.resetCheckQuery();
        ArrayList<Integer> startDateTime = new ArrayList<Integer>();
        startDateTime.add(0, startYear);
        startDateTime.add(1, startMonth);
        startDateTime.add(2, startDay);
        startDateTime.add(3, startHour);
        startDateTime.add(4, startMin);
        ArrayList<Integer> endDateTime = new ArrayList<Integer>();
        endDateTime.add(0, endYear);
        endDateTime.add(1, endMonth);
        endDateTime.add(2, endDay);
        endDateTime.add(3, endHour);
        endDateTime.add(4, endMin);
        replyBundle.putIntegerArrayList("startDateAndTime", startDateTime);
        replyBundle.putIntegerArrayList("endDateAndTime", endDateTime);
        replyBundle.putString("eventTitle", title);
        replyBundle.putString("organizers", organizers);
        replyBundle.putString("venue", venue);
        replyBundle.putString("eventDescription", eventDescription);
        setAlarmForEvent(setAlarm.isChecked());
        Navigation.findNavController(v).navigate(R.id.timeTable, replyBundle);
    }
    public void setAlarmForEvent(Boolean enableAlarm) {
        if (enableAlarm){
            final AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            Intent notifyIntent = new Intent(getContext(), AlarmReceiver.class);
            Bundle notificationInfo = new Bundle();
            notificationInfo.putString("title", title);
            notificationInfo.putString("eventDescription",eventDescription);
            notificationInfo.putString("organizers",organizers);
            notificationInfo.putString("venue",venue);
            notificationInfo.putInt("startHour", startHour);
            notificationInfo.putInt("startMin", startMin);
            notificationInfo.putInt("alarmTime", timeAlarmInMins);
            notifyIntent.putExtras(notificationInfo);
            final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(getContext(), NOTIFICATION_ID, notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Calendar setTime = Calendar.getInstance();
            //setTime.set(startYear, startMonth, startDay, startHour, startMin);
            //setTime.add(Calendar.MINUTE, -1 * timeAlarmInMins);
            long triggerTime = setTime.getTimeInMillis();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                AlarmManager.AlarmClockInfo alarmInfo = new AlarmManager.AlarmClockInfo(triggerTime, null);
                alarmManager.setAlarmClock(alarmInfo, notifyPendingIntent);
            }
        }
    }
    public void insertValue() {
        Events e = new Events(title, venue, organizers, eventDescription, startYear, startMonth, startDay, startHour, startMin,
                endYear, endMonth, endDay, endHour, endMin);
        mViewModel.insert(e);
    }
    public static Context retrieveContext(){
        return context;
    }
}