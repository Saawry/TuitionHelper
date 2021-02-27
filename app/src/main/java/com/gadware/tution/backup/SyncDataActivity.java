package com.gadware.tution.backup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;

import com.gadware.tution.R;
import com.gadware.tution.models.Batch;
import com.gadware.tution.models.DaySchedule;
import com.gadware.tution.models.ImageDetails;
import com.gadware.tution.models.SessionInfo;
import com.gadware.tution.models.StudentInfo;
import com.gadware.tution.models.TuitionInfo;
import com.gadware.tution.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SyncDataActivity extends AppCompatActivity {
    private AlertDialog alertDialog;
    StorageReference Storageref, tuitionImages;
    DatabaseReference userInfoRef, tuitionRef, scheduleRef, sessionRef;
    String muserId;
    List<TuitionInfo> tuitionList = new ArrayList<>();
    List<StudentInfo> studentInfoList = new ArrayList<>();
    List<Batch> batchList = new ArrayList<>();
    List<SessionInfo> sessionInfoList = new ArrayList<>();
    List<DaySchedule> dayScheduleList = new ArrayList<>();
    List<ImageDetails> imageDetailList = new ArrayList<>();
    List<User> userList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);

        Storageref = FirebaseStorage.getInstance().getReference("Images").child(muserId + ".jpg");
        tuitionImages = FirebaseStorage.getInstance().getReference("Images");
        userInfoRef = FirebaseDatabase.getInstance().getReference("Users").child(muserId);
        sessionRef = FirebaseDatabase.getInstance().getReference("Session List").child(muserId);
        tuitionRef = FirebaseDatabase.getInstance().getReference("Tuition List").child(muserId);
        scheduleRef = FirebaseDatabase.getInstance().getReference("Schedule List").child(muserId);

    }
}