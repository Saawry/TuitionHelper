package com.gadware.tution.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.gadware.tution.models.Batch;
import com.gadware.tution.models.DaySchedule;
import com.gadware.tution.storage.AppDatabase;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class ScheduleViewModel extends AndroidViewModel {


    AppDatabase appDatabase;

    public ScheduleViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(application);
    }


    public Flowable<List<DaySchedule>> getDayScheduleList() {
        return appDatabase.appDao().getDayScheduleList();
    }

    public Flowable<List<DaySchedule>> getDayScheduleListToday(String dayName) {
        return appDatabase.appDao().getDayScheduleListToday(dayName);
    }

    public void insertDayScheduleList(List<DaySchedule>daySchedules){
        appDatabase.appDao().insertDayScheduleList(daySchedules);
    }

    public void insertDaySchedule(String dayName,String time){
        appDatabase.appDao().insertDaySchedule(dayName,time );
    }

    public void deleteDaySchedule(DaySchedule daySchedule){
        appDatabase.appDao().deleteDaySchedule(daySchedule);
    }

    public void deleteDayScheduleTable(){
        appDatabase.appDao().deleteDayScheduleTable();
    }
}
