package com.gadware.tution.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.gadware.tution.R;
import com.gadware.tution.databinding.ActivityAddNewTuitionBinding;
import com.gadware.tution.models.DaySchedule;
import com.gadware.tution.models.TuitionInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddNewTuition extends AppCompatActivity {

    ActivityAddNewTuitionBinding binding;
    private String id, studentName, location, mobile, totalDays, completedDays, weeklyDays, remuneration;
    private AlertDialog alertDialog;
    private DatabaseReference  TuitionRef;
    private String mUserId;

    List<DaySchedule> daySchedules = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_tuition);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_tuition);

        binding.inputWeeklyDays.setOnClickListener(v -> {
            getWeeklySchedule();
        });

        binding.addTuitionBtnId.setOnClickListener(v -> {
            if (validate() == 1) {
                InitNewTuition();
            }
        });

    }

    private void getWeeklySchedule() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddNewTuition.this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = AddNewTuition.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.weekly_days_select, null);
        dialogBuilder.setView(dialogView);

        EditText satEt = dialogView.findViewById(R.id.satTimeET);
        EditText sunEt = dialogView.findViewById(R.id.sunTimeET);
        EditText monEt = dialogView.findViewById(R.id.monTimeET);
        EditText tueEt = dialogView.findViewById(R.id.tueTimeET);
        EditText wedEt = dialogView.findViewById(R.id.wedTimeET);
        EditText thuEt = dialogView.findViewById(R.id.thuTimeET);
        EditText friEt = dialogView.findViewById(R.id.friTimeET);

        CheckBox satCB = dialogView.findViewById(R.id.satcheckBox);
        CheckBox sunCB = dialogView.findViewById(R.id.suncheckBox);
        CheckBox monCB = dialogView.findViewById(R.id.moncheckBox);
        CheckBox tueCB = dialogView.findViewById(R.id.tuecheckBox);
        CheckBox wedCB = dialogView.findViewById(R.id.wedcheckBox);
        CheckBox thuCB = dialogView.findViewById(R.id.thucheckBox);
        CheckBox friCB = dialogView.findViewById(R.id.fricheckBox);

        Button confirmBtn = dialogView.findViewById(R.id.confirm_schedule_btn);

        satCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            HideViewEditText(satEt, isChecked);
        });
        sunCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            HideViewEditText(sunEt, isChecked);
        });
        monCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            HideViewEditText(monEt, isChecked);
        });
        tueCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            HideViewEditText(tueEt, isChecked);
        });
        wedCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            HideViewEditText(wedEt, isChecked);
        });
        thuCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            HideViewEditText(thuEt, isChecked);
        });
        friCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            HideViewEditText(friEt, isChecked);
        });

        confirmBtn.setOnClickListener(v -> {
            if (satCB.isChecked()) {
                String time = satEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    daySchedules.add(new DaySchedule("SAT", time));
                }
            }
            if (sunCB.isChecked()) {
                String time = sunEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    daySchedules.add(new DaySchedule("SUN", time));
                }
            }
            if (monCB.isChecked()) {
                String time = monEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    daySchedules.add(new DaySchedule("MON", time));
                }
            }
            if (tueCB.isChecked()) {
                String time = tueEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    daySchedules.add(new DaySchedule("TUE", time));
                }
            }
            if (wedCB.isChecked()) {
                String time = wedEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    daySchedules.add(new DaySchedule("WED", time));
                }
            }
            if (thuCB.isChecked()) {
                String time = thuEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    daySchedules.add(new DaySchedule("THU", time));
                }
            }
            if (friCB.isChecked()) {
                String time = friEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    daySchedules.add(new DaySchedule("FRI", time));
                }
            }
            binding.inputWeeklyDays.setText(String.valueOf(daySchedules.size()));
            alertDialog.dismiss();
        });

        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void HideViewEditText(EditText Et, boolean isChecked) {
        if (isChecked) {
            Et.setVisibility(View.VISIBLE);
        } else {
            Et.setText("");
            Et.setVisibility(View.GONE);
        }
    }

    private void InitNewTuition() {
        mUserId = FirebaseAuth.getInstance().getUid();
        TuitionRef = FirebaseDatabase.getInstance().getReference().child("Tuition List").child(mUserId).push();
        id = TuitionRef.getKey();
        TuitionInfo tuitionInfo = new TuitionInfo(id, studentName, location, mobile, totalDays, completedDays, weeklyDays, remuneration, "Active");
        TuitionRef.setValue(tuitionInfo).addOnSuccessListener(aVoid -> {
//            UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUserId).child("TuitionList").child(id);
//            UserRef.setValue("Active");
            AddSchedules(id);

        }).addOnFailureListener(e -> Toast.makeText(AddNewTuition.this, e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    private void AddSchedules(String id) {
        for (DaySchedule daySchedule:daySchedules){
          DatabaseReference ScheduleRef = FirebaseDatabase.getInstance().getReference().child("Schedule List").child(id);
          ScheduleRef.child(daySchedule.getDayName()).setValue(daySchedule.getTime());
        }
        startActivity(new Intent(this, MainActivity.class));
    }

    public int validate() {

        studentName = binding.inputStudentName.getText().toString();
        if (studentName.isEmpty() || studentName.length() < 4) {
            binding.inputStudentName.setError("enter a valid name");
            return 0;
        } else {
            binding.inputStudentName.setError(null);
        }


        location = binding.inputLocation.getText().toString();
        if (location.isEmpty() || location.length() < 4) {
            binding.inputLocation.setError("enter a valid location");
            return 0;
        } else {
            binding.inputLocation.setError(null);
        }

        mobile = binding.inputMobile.getText().toString();
        if (mobile.isEmpty() || mobile.length() < 10) {
            binding.inputMobile.setError("enter a valid mobile number");
            return 0;
        } else {
            binding.inputMobile.setError(null);
        }

        totalDays = binding.inputTotalDays.getText().toString();
        if (totalDays.isEmpty() || totalDays.length() < 1) {
            binding.inputTotalDays.setError("enter days amount");
            return 0;
        } else {
            binding.inputTotalDays.setError(null);
        }

        completedDays = binding.inputCompletedDays.getText().toString();
        if (completedDays.isEmpty() || completedDays.length() < 1) {
            binding.inputCompletedDays.setError("enter days amount");
            return 0;
        } else {
            binding.inputCompletedDays.setError(null);
        }


        weeklyDays = String.valueOf(daySchedules.size());
        if (weeklyDays.isEmpty() || weeklyDays.length() < 1) {
            binding.inputWeeklyDays.setError("enter weekly days");
            return 0;
        } else {
            binding.inputWeeklyDays.setError(null);
        }


        remuneration = binding.inputRemuneration.getText().toString();
        if (remuneration.isEmpty() || remuneration.length() < 3) {
            binding.inputRemuneration.setError("enter min 3 digit amount");
            return 0;
        } else {
            binding.inputRemuneration.setError(null);
        }


        return 1;
    }
}