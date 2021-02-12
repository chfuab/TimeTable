package com.example.timetablefragment;

import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class Util {
    public static void removeTableRowViews(TableRow row, int nRow, TableLayout tL) {
        for (int i=1; i<nRow; i++){
            row = (TableRow) tL.getChildAt(i);
            row.removeAllViews();
        }
    }
    public static int timeUnit(int hr, int min){
        return (4*hr + min/15);
    }
    public static boolean timeCrashComparison(int startAhr1, int startAmin1, int endAhr2, int endAmin2, int startBhr3, int startBmin3, int endBhr4, int endBmin4){
        int eventStartA = timeUnit(startAhr1, startAmin1);
        int eventStartB = timeUnit(startBhr3, startBmin3);
        int eventEndA = timeUnit(endAhr2, endAmin2);
        int eventEndB = timeUnit(endBhr4, endBmin4);
        boolean crashOrNot = false;
        boolean crashScenarioA = eventStartA > eventStartB && eventStartA < eventEndB;
        boolean crashScenarioB = eventEndA > eventStartB && eventEndA < eventEndB;
        boolean crashScenarioC = eventStartA >= eventStartB && eventEndA <= eventEndB;
        boolean crashScenarioD = eventStartB >= eventStartA && eventEndB <= eventEndA;
        if (crashScenarioA || crashScenarioB || crashScenarioC || crashScenarioD){
            crashOrNot = true;
        }
        return crashOrNot;
    }

    public static boolean queryCriteriaBeforeConfirm(EventViewModel mViewModel, int startHour, int startMin, int endHour, int endMin) {
        if (mViewModel.checkQuery()){
            Events[] events = mViewModel.getCriteriaEvents();
            String crashEvent = null;
            int crashStartHr =999, crashStartMin = 999, crashEndHr = 999, crashEndMin = 999;
            if (events != null){
                for (Events e: events){
                    if (timeCrashComparison(startHour, startMin, endHour, endMin, e.startHour, e.startMin, e.endHour, e.endMin)){
                        crashEvent = e.eventTitle;
                        crashStartHr = e.startHour;
                        crashStartMin = e.startMin;
                        crashEndHr = e.endHour;
                        crashEndMin = e.endMin;
                        break;
                    };
                }
                if (crashEvent != null){
                    Toast.makeText(EditEvent.retrieveContext(), "The event has time crash with " + crashEvent + " held between "
                                    + String.format("%02d : %02d and %02d : %02d on the same day.", crashStartHr, crashStartMin, crashEndHr, crashEndMin),
                            Toast.LENGTH_LONG).show();
                    return false;
                } else{ return true; }
            } else{ return true; }
        } else{
            //Toast.makeText(EditEvent.getContext(), "Press the button again when checking completed.", Toast.LENGTH_LONG).show();
            Log.d("Checking completed", "Not yet");
            return false;
        }
    }
}