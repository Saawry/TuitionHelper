package com.gadware.tution.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
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
import com.google.firebase.auth.FirebaseAuth;
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
    private String id,  date,  day,  time, etime,  topic;
    int counter;
    private String userId,tuitionid,completedDays;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_session);

        tuitionid=getIntent().getExtras().get("Tuition_id").toString();
        completedDays=getIntent().getExtras().get("completedDays").toString();
        counter = Integer.parseInt(completedDays)+1;

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

        userId= FirebaseAuth.getInstance().getUid();

        String myTimeFormat = "hh.mm a";
        SimpleDateFormat stf = new SimpleDateFormat(myTimeFormat, Locale.US);
        String myDateFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myDateFormat, Locale.US);


        binding.inputDate.setText(sdf.format(now.getTime()));
        binding.inputSTime.setText(stf.format(now.getTime()));
        binding.inputETime.setText(stf.format(cend.getTime()));
        binding.inputDay.setText(GetDAY(today));


        binding.inputDate.setOnClickListener(v -> {
            //date.show();
            DatePickerDialog nDate = new DatePickerDialog(this, R.style.datepicker, (DatePickerDialog.OnDateSetListener) (view, year, month, dayOfMonth) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                binding.inputDate.setText(sdf.format(myCalendar.getTime()));
            }, yr, mnth, day);
            nDate.show();
        });

        binding.inputSTime.setOnClickListener(v -> {

            TimePickerDialog nTime = new TimePickerDialog(this, R.style.datepicker, (view, hourOfDay, minute) -> {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);


                binding.inputSTime.setText(stf.format(myCalendar.getTime()));
            }, hour, minte, false);
            nTime.show();
        });

        binding.inputETime.setOnClickListener(v -> {

            TimePickerDialog nTime = new TimePickerDialog(this, R.style.datepicker, (view, hourOfDay, minute) -> {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);

                binding.inputETime.setText(stf.format(myCalendar.getTime()));
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
//        tuitionRef= FirebaseDatabase.getInstance().getReference().child("Session List").child(tuitionid);
//        tuitionRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                long cc=snapshot.getChildrenCount();
//                cc++;
//                counter=String.valueOf(cc);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        DatabaseReference sessnRef= FirebaseDatabase.getInstance().getReference().child("Session List").child(tuitionid).push();
        id=sessnRef.getKey();
        SessionInfo sessionInfo=new SessionInfo(id,date,day,time,etime,topic,String.valueOf(counter));
        sessnRef.setValue(sessionInfo).addOnSuccessListener(aVoid -> {
            DatabaseReference cntRef = FirebaseDatabase.getInstance().getReference("Tuition List").child(userId).child(tuitionid).getRef();
            cntRef.child("completedDays").setValue(String.valueOf(counter));
            Toast.makeText(AddNewSession.this, "Added Successfully", Toast.LENGTH_SHORT).show();
            Intent intent =new Intent(AddNewSession.this,TuitionDetails.class);
            intent.putExtra("Tuition_id",tuitionid);
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