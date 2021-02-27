package com.gadware.tution.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.gadware.tution.models.SessionInfo;
import com.gadware.tution.models.TuitionInfo;
import com.gadware.tution.storage.AppDatabase;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class SessionViewModel extends AndroidViewModel {


    AppDatabase appDatabase;

    public SessionViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(application);
    }


    public Flowable<List<SessionInfo>> getSessionInfoList() {
        return appDatabase.appDao().getSessionInfoList();
    }

    public Single<SessionInfo> getSessionInfo(String id) {
        return appDatabase.appDao().getSessionInfo(id);
    }

    public void insertSessionInfoList(List<SessionInfo>sessionInfoList){
        appDatabase.appDao().insertSessionInfoList(sessionInfoList);
    }

    public void insertSessionInfo(SessionInfo sessionInfo){
        appDatabase.appDao().insertSessionInfo(sessionInfo );
    }

    public void deleteSessionInfo(String id){
        appDatabase.appDao().deleteSessionInfo(id);
    }

    public void deleteSessionInfoTable(){
        appDatabase.appDao().deleteSessionInfoTable();
    }
}
