package com.gadware.tution.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.gadware.tution.models.Batch;
import com.gadware.tution.models.StudentList;
import com.gadware.tution.storage.AppDatabase;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class StudentListViewModel extends AndroidViewModel {


    AppDatabase appDatabase;

    public StudentListViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(application);
    }


    public Flowable<List<StudentList>> getAllStudentIdList() {
        return appDatabase.appDao().getAllStudentIdList();
    }

    public Flowable<List<StudentList>> getBatchStudentList(String batchId) {
        return appDatabase.appDao().getBatchStudentList(batchId);
    }

    public void insertStudentList(List<StudentList>studentLists){
        appDatabase.appDao().insertStudentList(studentLists);
    }

    public void insertSingleStudent(StudentList studentList){
        appDatabase.appDao().insertSingleStudent(studentList );
    }

    public void deleteStudentFromBatch(String stdId){
        appDatabase.appDao().deleteStudentFromBatch(stdId);
    }

    public void deleteBatchAllStudent(String batchId){
        appDatabase.appDao().deleteBatchAllStudent(batchId);
    }

    public void deleteStudentListTable(){
        appDatabase.appDao().deleteStudentListTable();
    }
}
