package com.gadware.tution.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.gadware.tution.R;
import com.gadware.tution.databinding.ActivityAddNewSessionBinding;
import com.gadware.tution.models.SessionInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddNewSession extends AppCompatActivity {


    ActivityAddNewSessionBinding binding;
    private final Calendar myCalendar = Calendar.getInstance();
    private String id,  date,  day,  time, etime,  topic,  counter;
    private String userId,tuitionid;
    private DatabaseReference tuitionRef,sessionRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_session);

        //getExtra() as tuitionId
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_session);
        Calendar now = Calendar.getInstance();
        int yr = now.get(Calendar.YEAR);
        int mnth = now.get(Calendar.MONTH) ; // Note: result may zero based!(+1)
        int day = now.get(Calendar.DAY_OF_MONTH);
        int today = now.get(Calendar.DAY_OF_WEEK);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int ehour = now.get(Calendar.HOUR_OF_DAY)+1;
        int minte = now.get(Calendar.MINUTE);
        long integerRepresentation = now.getTimeInMillis();

        Calendar cend=Calendar.getInstance();
        cend.setTimeInMillis(integerRepresentation+3600000);



        String myTimeFormat = "hh.mm a";
        SimpleDateFormat stf = new SimpleDateFormat(myTimeFormat, Locale.US);
        String myDateFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myDateFormat, Locale.US);


        binding.inputDate.setText(sdf.format(now.getTime()));
        binding.inputSTime.setText(stf.format(now.getTime()));
        binding.inputETime.setText(stf.format(cend.getTime()));
        binding.inputDay.setText(GetDAY(today));




        binding.inputDate.setOnClickListener(v -> {

            DatePickerDialog nDate = new DatePickerDialog(this, R.style.datepicker, (DatePickerDialog.OnDateSetListener) (view, year, month, dayOfMonth) -> {
                myCalendar.set(Calendar.YEAR, yr);
                myCalendar.set(Calendar.MONTH, mnth);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);


                binding.inputDate.setText(sdf.format(myCalendar.getTime()));
            }, yr, mnth, day);
            nDate.show();
        });

        binding.inputSTime.setOnClickListener(v -> {

            TimePickerDialog nTime = new TimePickerDialog(this, R.style.datepicker, (view, hourOfDay, minute) -> {
                myCalendar.set(Calendar.HOUR_OF_DAY, hour);
                myCalendar.set(Calendar.MINUTE, minte);


                binding.inputSTime.setText(sdf.format(myCalendar.getTime()));
            }, hour, minte, false);
            nTime.show();
        });

        binding.inputETime.setOnClickListener(v -> {

            TimePickerDialog nTime = new TimePickerDialog(this, R.style.datepicker, (view, hourOfDay, minute) -> {
                myCalendar.set(Calendar.HOUR_OF_DAY, ehour);
                myCalendar.set(Calendar.MINUTE, minte);

                binding.inputETime.setText(sdf.format(myCalendar.getTime()));
            }, ehour, minte, false);
            nTime.show();
        });

        binding.addSessionBtnId.setOnClickListener(v -> {
            if (validate()==1){
                InsertNewSession();
            }
        });

    }

    private void InsertNewSession() {
        tuitionRef= FirebaseDatabase.getInstance().getReference().child("SessionInfo").child(tuitionid).push();
        id=tuitionRef.getKey();
//        tuitionRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


        tuitionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                counter=String.valueOf(snapshot.getChildrenCount()-1);//if new push not added, remove -1
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        SessionInfo sessionInfo=new SessionInfo(id,date,time,etime,day,topic,counter);
        tuitionRef.setValue(sessionInfo).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show();
            Intent intent =new Intent(this,TuitionDetails.class);
            intent.putExtra("tuitionId",tuitionid);
            startActivity(intent);
        }).addOnFailureListener(e -> Toast.makeText(AddNewSession.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private int validate() {
        date=binding.inputDate.getText().toString();
        if (date.isEmpty() || date.length()<10){
            binding.inputDate.setError("enter valid date");
            return 0;
        }

        time=binding.inputSTime.getText().toString();
        if (time.isEmpty() || time.length()<6){
            binding.inputSTime.setError("enter valid time");
            return 0;
        }

        etime=binding.inputETime.getText().toString();
        if (etime.isEmpty() || etime.length()<6){
            binding.inputETime.setError("enter valid time");
            return 0;
        }

        day=binding.inputDay.getText().toString();
        if (date.isEmpty() || date.length()<3){
            binding.inputDay.setError("enter valid day");
            return 0;
        }

        topic=binding.inputTopic.getText().toString();
        if (topic.isEmpty() || topic.length()<3){
            binding.inputDate.setError("enter valid topic");
            return 0;
        }

        return 1;
    }

    private String GetDAY(int today) {
        switch (today) {
            case Calendar.FRIDAY:
                return "FRIDAY";
            case Calendar.SATURDAY:
                return "SATURDAY";
            case Calendar.SUNDAY:
                return "SUNDAY";
            case Calendar.MONDAY:
                return "MONDAY";
            case Calendar.TUESDAY:
                return "TUESDAY";
            case Calendar.WEDNESDAY:
                return "WEDNESDAY";
            case Calendar.THURSDAY:
                return "THURSDAY";
        }
        return "";
    }

}