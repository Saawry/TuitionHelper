package com.gadware.tution.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.gadware.tution.models.Batch;
import com.gadware.tution.models.User;
import com.gadware.tution.storage.AppDatabase;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class UserViewModel extends AndroidViewModel {


    AppDatabase appDatabase;

    public UserViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(application);
    }


    public Flowable<List<User>> getUserInfoList() {
        return appDatabase.appDao().getUserList();
    }

    public Single<User> getUserInfo(String id) {
        return appDatabase.appDao().getUserInfo(id);
    }

    public void insertUserInfoList(List<User>userList){
        appDatabase.appDao().insertUserInfoList(userList);
    }

    public void insertUserInfo(User user){
        appDatabase.appDao().insertUserInfo(user);
    }

    public void deleteUserInfo(String id){
        appDatabase.appDao().deleteUserInfo(id);
    }

    public void deleteUserInfoTable(){
        appDatabase.appDao().deleteUserInfoTable();
    }
}
