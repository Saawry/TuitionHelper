package com.gadware.tution.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.gadware.tution.R;
import com.gadware.tution.databinding.ActivityAddNewTuitionBinding;
import com.gadware.tution.models.TuitionInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNewTuition extends AppCompatActivity {

    ActivityAddNewTuitionBinding binding;
    private String id, studentName, location, mobile, totalDays, completedDays, weeklyDays, remuneration;

    private DatabaseReference UserRef,TuitionRef;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_tuition);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_tuition);

        binding.addTuitionBtnId.setOnClickListener(v -> {
            if (validate()==1){
                InitNewTuition();
            }
        });

    }

    private void InitNewTuition() {
        mUserId=FirebaseAuth.getInstance().getUid();
        TuitionRef=FirebaseDatabase.getInstance().getReference().child("Tuition List").child(mUserId).push();
        id=TuitionRef.getKey();
        TuitionInfo tuitionInfo=new TuitionInfo(id,studentName,location,mobile,totalDays,completedDays,weeklyDays,remuneration,"Active");
        TuitionRef.setValue(tuitionInfo).addOnSuccessListener(aVoid -> {
            UserRef =FirebaseDatabase.getInstance().getReference().child("Users").child(mUserId).child("TuitionList").child(id);
            UserRef.setValue("Active");
            startActivity(new Intent(this,MainActivity.class));
        }).addOnFailureListener(e -> Toast.makeText(AddNewTuition.this, e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    public int validate() {

        studentName=binding.inputStudentName.getText().toString();
        if (studentName.isEmpty() || studentName.length()<4) {
            binding.inputStudentName.setError("enter a valid name");
            return 0;
        } else {
            binding.inputStudentName.setError(null);
        }


        location=binding.inputLocation.getText().toString();
        if (location.isEmpty() || location.length()<4) {
            binding.inputLocation.setError("enter a valid location");
            return  0;
        } else {
            binding.inputLocation.setError(null);
        }

        mobile=binding.inputMobile.getText().toString();
        if (mobile.isEmpty() || mobile.length()<10) {
            binding.inputMobile.setError("enter a valid mobile number");
            return 0;
        } else {
            binding.inputMobile.setError(null);
        }

        totalDays=binding.inputTotalDays.getText().toString();
        if (totalDays.isEmpty() || totalDays.length()<1) {
            binding.inputTotalDays.setError("enter days amount");
            return 0;
        } else {
            binding.inputTotalDays.setError(null);
        }

        completedDays=binding.inputCompletedDays.getText().toString();
        if (completedDays.isEmpty() || completedDays.length()<1) {
            binding.inputCompletedDays.setError("enter days amount");
            return 0;
        } else {
            binding.inputCompletedDays.setError(null);
        }


        weeklyDays=binding.inputWeeklyDays.getText().toString();
        if (weeklyDays.isEmpty() || weeklyDays.length()<1) {
            binding.inputWeeklyDays.setError("enter weekly days amount");
            return 0;
        } else {
            binding.inputWeeklyDays.setError(null);
        }


        remuneration=binding.inputRemuneration.getText().toString();
        if (remuneration.isEmpty() || remuneration.length()<3) {
            binding.inputRemuneration.setError("enter min 3 digit amount");
            return 0;
        } else {
            binding.inputRemuneration.setError(null);
        }


        return 1;
    }
}