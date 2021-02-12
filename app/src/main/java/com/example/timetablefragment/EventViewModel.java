package com.example.timetablefragment;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.List;

public class EventViewModel extends AndroidViewModel {
    private EventRepo mRepo;
    private LiveData<List<Events>> mEvents;
    private Events[] mCriteriaEvents;
    private LiveData<Events[]> mLiveCriteria;
    private boolean queryStatus;

    public EventViewModel(@NonNull Application application) {
        super(application);
        mRepo = new EventRepo(application);
        mEvents = mRepo.getAllEvents();
    }
    LiveData<List<Events>> getAllEvents(){return mEvents; }

    public void makeCriteriaEvents(int startYear, int startMonth, int startDay){
        mRepo.makeCriteria(startYear, startMonth, startDay);
    }
    public Events[] getCriteriaEvents(){
        mCriteriaEvents = mRepo.getCritera();
        return mCriteriaEvents;
    }
    public boolean checkQuery(){
        queryStatus = mRepo.isCriteriaQueryCompleted();
        return queryStatus;
    }
    public void resetCheckQuery(){
        mRepo.resetCriteriaQuery();
        queryStatus = mRepo.isCriteriaQueryCompleted();
    }
    public void insert(Events event){mRepo.insert(event);}
    public void delete(){mRepo.delete();}
}