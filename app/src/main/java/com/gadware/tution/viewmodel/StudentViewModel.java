package com.gadware.tution.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.gadware.tution.models.SessionInfo;
import com.gadware.tution.models.StudentInfo;
import com.gadware.tution.storage.AppDatabase;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class StudentViewModel extends AndroidViewModel {


    AppDatabase appDatabase;

    public StudentViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(application);
    }


    public Flowable<List<StudentInfo>> getStudentInfoList() {
        return appDatabase.appDao().getStudentInfoList();
    }

    public Single<StudentInfo> getStudentInfo(String id) {
        return appDatabase.appDao().getStudentInfo(id);
    }

    public void insertStudentInfoList(List<StudentInfo>studentInfoList){
        appDatabase.appDao().insertStudentInfoList(studentInfoList);
    }

    public void insertStudentInfo(StudentInfo studentInfo){
        appDatabase.appDao().insertStudentInfo(studentInfo );
    }

    public void deleteStudentInfo(String id){
        appDatabase.appDao().deleteStudentInfo(id);
    }

    public void deleteStudentInfoTable(){
        appDatabase.appDao().deleteStudentInfoTable();
    }
}
