package com.gadware.tution.storage;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.gadware.tution.dao.AppDao;
import com.gadware.tution.models.Batch;
import com.gadware.tution.models.DaySchedule;
import com.gadware.tution.models.ImageDetails;
import com.gadware.tution.models.SessionInfo;
import com.gadware.tution.models.StudentInfo;
import com.gadware.tution.models.StudentList;
import com.gadware.tution.models.TuitionInfo;
import com.gadware.tution.models.User;


@Database(entities = {User.class, TuitionInfo.class, StudentInfo.class, SessionInfo.class, DaySchedule.class, Batch.class, ImageDetails.class, StudentList.class}, version = 1, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME="tuition_log";

    private static AppDatabase instance;

    public static AppDatabase getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).build();
        }
        return instance;
    }
//.fallbackToDestructiveMigration()

    public abstract AppDao appDao();

}
