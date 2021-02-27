package com.gadware.tution.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.gadware.tution.models.Batch;
import com.gadware.tution.models.SessionInfo;
import com.gadware.tution.storage.AppDatabase;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class BatchViewModel extends AndroidViewModel {


    AppDatabase appDatabase;

    public BatchViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(application);
    }


    public Flowable<List<Batch>> getBatchInfoList() {
        return appDatabase.appDao().getBatchInfoList();
    }

    public Single<Batch> getBatchInfo(String id) {
        return appDatabase.appDao().getBatchInfo(id);
    }

    public void insertBatchInfoList(List<Batch>batchList){
        appDatabase.appDao().insertBatchInfoList(batchList);
    }

    public void insertBatchInfo(Batch batch){
        appDatabase.appDao().insertBatchInfo(batch );
    }

    public void deleteBatchInfo(String id){
        appDatabase.appDao().deleteBatchInfo(id);
    }

    public void deleteBatchInfoTable(){
        appDatabase.appDao().deleteBatchInfoTable();
    }
}
