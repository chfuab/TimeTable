package com.example.timetablefragment;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventRepo implements MyTaskInformer{
    private EventDao mEventDao;
    private LiveData<List<Events>> mEvents;
    private Events[] mCriteriaEvents;
    private boolean criteriaQueryCompleted = false;

    EventRepo(Application application) {
        EventsDatabase db = EventsDatabase.getDatabase(application);
        mEventDao = db.eventDao();
        mEvents = mEventDao.getAllEvents();

    }

    LiveData<List<Events>> getAllEvents() {
        return mEvents;
    }

    public void makeCriteria(int startYear, int startMonth, int startDay){
        new criteriaAsyncTask(mEventDao, this, startYear, startMonth, startDay).execute();
    }
    public Events[] getCritera(){
        return mCriteriaEvents;
    }
    public boolean isCriteriaQueryCompleted(){
        return criteriaQueryCompleted;
    }
    public void resetCriteriaQuery(){
        criteriaQueryCompleted = false;
    }
    public void insert (Events event) {
        new insertAsyncTask(mEventDao).execute(event);
    }
    public void delete () {
        new deleteAsyncTask(mEventDao).execute();
    }

    private static class criteriaAsyncTask extends AsyncTask<Events, Void, Events[]> {
        private EventDao mAsyncTaskDao;
        private WeakReference<MyTaskInformer> events;
        ///
        private int startYr, startMon, startDay;
        ///

        criteriaAsyncTask(EventDao dao, MyTaskInformer e, int startYear, int startMonth, int startDay) {
            mAsyncTaskDao = dao;
            events = new WeakReference<>(e);
            this.startYr = startYear;
            this.startMon = startMonth;
            this.startDay = startDay;
        }
        @Override
        protected Events[] doInBackground(Events... events) {
            Events[] eventArray = mAsyncTaskDao.getCriteria(startYr, startMon, startDay);
            return eventArray;
        }

        @Override
        protected void onPostExecute(Events[] result) {
            super.onPostExecute(result);
            final MyTaskInformer callBack = events.get();
            if (callBack != null){
                callBack.onTaskDone(result);
            }
        }
    }

    private static class insertAsyncTask extends AsyncTask<Events, Void, Void> {
        private EventDao mAsyncTaskDao;
        insertAsyncTask(EventDao dao) {
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final Events... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Events, Void, Void> {

        private EventDao mAsyncTaskDao;

        deleteAsyncTask(EventDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Events... params) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }
    @Override
    public void onTaskDone(Events[] output) {
        mCriteriaEvents = output;
        criteriaQueryCompleted = true;
    }
}