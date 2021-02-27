package com.gadware.tution.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.gadware.tution.models.TuitionInfo;
import com.gadware.tution.storage.AppDatabase;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class TuitionViewModel extends AndroidViewModel {


    AppDatabase appDatabase;

    public TuitionViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(application);
    }


    public Flowable<List<TuitionInfo>> getTuitionInfoList() {
        return appDatabase.appDao().getTuitionInfoList();
    }

    public Single<TuitionInfo> getTuitionInfo(String id) {
        return appDatabase.appDao().getTuitionInfo(id);
    }

    public void insertTuitionInfoList(List<TuitionInfo>tuitionInfoList){
        appDatabase.appDao().insertTuitionInfoList(tuitionInfoList);
    }

    public void insertTuitionInfo(TuitionInfo tuitionInfo){
        appDatabase.appDao().insertTuitionInfo(tuitionInfo);
    }

    public void insertTuitionInfo(String id, String studentName, String location, String mobile, String totalDays, String completedDays, String weeklyDays, String remuneration, String sDate, String eDate){
        appDatabase.appDao().insertTuitionInfoDetails( id,  studentName,  location,  mobile,  totalDays,  completedDays,  weeklyDays,  remuneration,  sDate,  eDate);
    }

    public void deleteTuitionInfo(String id){
        appDatabase.appDao().deleteTuitionInfo(id);
    }

    public void deleteTuitionInfoTable(){
        appDatabase.appDao().deleteTuitionInfoTable();
    }
}
