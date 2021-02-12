package com.example.timetablefragment;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Calendar;

@Database(entities = {Events.class}, version = 1,  exportSchema = false)
public abstract class EventsDatabase extends RoomDatabase {
    public abstract EventDao eventDao();
    private static EventsDatabase INSTANCE;

    static EventsDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EventsDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            EventsDatabase.class, "event_database")
                            // Wipes and rebuilds instead of migrating if no Migration object.
                            // Migration is not part of this codelab.
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){

        @Override
        public void onOpen (@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            // If you want to keep the data through app restarts,
            // comment out the following line.
            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final EventDao mDao;
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        int startYear = start.get(Calendar.YEAR);
        int startMonth = start.get(Calendar.MONTH);
        int startDay = start.get(Calendar.DAY_OF_MONTH);
        int startHour = start.get(Calendar.HOUR);
        int startMin = start.get(Calendar.MINUTE);
        int endYear = end.get(Calendar.YEAR);
        int endMonth = end.get(Calendar.MONTH);
        int endDay = end.get(Calendar.DAY_OF_MONTH);
        int endHour = end.get(Calendar.HOUR);
        int endMin = end.get(Calendar.MINUTE);
        String title = "Default title";
        String venue = "Default venue";
        String organizers = "Default organizers";
        String descrip = "Default description";

        PopulateDbAsync(EventsDatabase db) {
            mDao = db.eventDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            mDao.deleteAll();
            Events event = new Events(title, venue, organizers, descrip, startYear, startMonth, startDay, startHour, startMin,
                    endYear, endMonth, endDay, endHour, endMin);
            mDao.insert(event);
            return null;
        }
    }

}