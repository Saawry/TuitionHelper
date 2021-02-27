package com.gadware.tution.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.gadware.tution.models.Batch;
import com.gadware.tution.models.ImageDetails;
import com.gadware.tution.storage.AppDatabase;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class ImageViewModel extends AndroidViewModel {


    AppDatabase appDatabase;

    public ImageViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(application);
    }


    public Flowable<List<ImageDetails>> getAllImageDetails() {
        return appDatabase.appDao().getAllImageDetails();
    }

    public Flowable<List<ImageDetails>> getBTTypeImages(String type) {
        return appDatabase.appDao().getBTTypeImages(type);
    }

    public Single<ImageDetails> getSingleImage(String id,String type) {
        return appDatabase.appDao().getSingleImage(id,type);
    }

    public void insertImageList(List<ImageDetails>imageDetailsList){
        appDatabase.appDao().insertImageList(imageDetailsList);
    }

    public void insertSingleImage(ImageDetails imageDetails){
        appDatabase.appDao().insertSingleImage(imageDetails);
    }

    public void deleteSingleImage(String id,String type){
        appDatabase.appDao().deleteSingleImage(id,type);
    }

    public void deleteImageDetailsTable(){
        appDatabase.appDao().deleteImageDetailsTable();
    }
}
