package com.gadware.tution.Activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.gadware.tution.R;
import com.gadware.tution.databinding.ActivityAddNewTuitionBinding;
import com.gadware.tution.models.DaySchedule;
import com.gadware.tution.models.TuitionInfo;
import com.gadware.tution.viewmodel.ScheduleViewModel;
import com.gadware.tution.viewmodel.SessionViewModel;
import com.gadware.tution.viewmodel.TuitionViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class AddNewTuition extends AppCompatActivity {

    ActivityAddNewTuitionBinding binding;
    private String id, studentName, location, mobile, totalDays, completedDays, weeklyDays, remuneration;
    private AlertDialog alertDialog, alert;
    private DatabaseReference TuitionRef;
    private String mUserId;
    private long tuitionCounter = 0;
    private TuitionViewModel tuitionViewModel;
    private ScheduleViewModel scheduleViewModel;
    private String satT, sunT, monT, tueT, wedT, thuT, friT;
    List<DaySchedule> daySchedules = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_tuition);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_tuition);

        tuitionViewModel = new ViewModelProvider(this).get(TuitionViewModel.class);
        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final View activityRootView = findViewById(R.id.activity_root_view);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
            if (heightDiff > dpToPx(AddNewTuition.this, 100)) {
                binding.adView.setVisibility(View.INVISIBLE);
            } else {
                binding.adView.setVisibility(View.VISIBLE);
            }
        });


        mUserId = FirebaseAuth.getInstance().getUid();

        TuitionRef = FirebaseDatabase.getInstance().getReference().child("Tuition List").child(mUserId);
        id = TuitionRef.push().getKey();


        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        //adView.setAdUnitId("ca-app-pub-7098600576446460/2003905092");
        adView.setAdUnitId("ca-app-pub-7098600576446460/2003905092");

        MobileAds.initialize(this, initializationStatus -> {

        });

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);

        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });


        GetTuitionCounter();

        binding.inputWeeklyDays.setOnClickListener(v -> {
            getWeeklySchedule();
        });

        binding.addTuitionBtnId.setOnClickListener(v -> {
            if (tuitionCounter >= 10) {
                Toast.makeText(AddNewTuition.this, "Reached Maximum(10), delete old tuition or contact us", Toast.LENGTH_LONG).show();
            } else if (validate() == 1) {
                InitNewTuition();
            }
        });

    }

    private void GetTuitionCounter() {
        TuitionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tuitionCounter = dataSnapshot.getChildrenCount();

                if (tuitionCounter >= 10) {
                    Toast.makeText(AddNewTuition.this, "Reached Maximum(10), delete old tuition or contact us", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getWeeklySchedule() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddNewTuition.this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = AddNewTuition.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.weekly_days_select, null);
        dialogBuilder.setView(dialogView);

        TextView satEt = dialogView.findViewById(R.id.satTimeET);
        TextView sunEt = dialogView.findViewById(R.id.sunTimeET);
        TextView monEt = dialogView.findViewById(R.id.monTimeET);
        TextView tueEt = dialogView.findViewById(R.id.tueTimeET);
        TextView wedEt = dialogView.findViewById(R.id.wedTimeET);
        TextView thuEt = dialogView.findViewById(R.id.thuTimeET);
        TextView friEt = dialogView.findViewById(R.id.friTimeET);

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
                    daySchedules.add(new DaySchedule(id, "SAT", satT, time));
                }
            }
            if (sunCB.isChecked()) {
                String time = sunEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    daySchedules.add(new DaySchedule(id, "SUN", sunT, time));
                }
            }
            if (monCB.isChecked()) {
                String time = monEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    daySchedules.add(new DaySchedule(id, "MON", monT, time));
                }
            }
            if (tueCB.isChecked()) {
                String time = tueEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    daySchedules.add(new DaySchedule(id, "TUE", tueT, time));
                }
            }
            if (wedCB.isChecked()) {
                String time = wedEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    daySchedules.add(new DaySchedule(id, "WED", wedT, time));
                }
            }
            if (thuCB.isChecked()) {
                String time = thuEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    daySchedules.add(new DaySchedule(id, "THU", thuT, time));
                }
            }
            if (friCB.isChecked()) {
                String time = friEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    daySchedules.add(new DaySchedule(id, "FRI", friT, time));
                }
            }
            binding.inputWeeklyDays.setText(String.valueOf(daySchedules.size()));
            alertDialog.dismiss();
        });

        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void HideViewEditText(TextView Et, boolean isChecked) {
        if (isChecked) {
            Et.setVisibility(View.VISIBLE);
            ShowClock(Et);
        } else {
            Et.setText("");
            Et.setVisibility(View.GONE);
        }
    }

    private void InitNewTuition() {
        ShowLoadingDialog();
        TuitionInfo tuitionInfo = new TuitionInfo(id, studentName, location, mobile, totalDays, completedDays, weeklyDays, remuneration,"0000-00-00","0000-00-00");
        assert id != null;
        TuitionRef.child(id).setValue(tuitionInfo).addOnSuccessListener(aVoid -> {
            UpdateLocalTuitionDB(tuitionInfo);

        }).addOnFailureListener(e -> {
            alert.dismiss();
            Toast.makeText(AddNewTuition.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    private void UpdateLocalTuitionDB(TuitionInfo tuitionInfo) {
        Completable.fromAction(() -> tuitionViewModel.insertTuitionInfo(tuitionInfo)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {

                        AddSchedules(id);
                    }

                    @Override
                    public void onError(Throwable e) {
                        alert.dismiss();
                        Toast.makeText(AddNewTuition.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void AddSchedules(String id) {
        boolean flag[] = {true};
        for (DaySchedule daySchedule : daySchedules) {
            DatabaseReference ScheduleRef = FirebaseDatabase.getInstance().getReference().child("Schedule List").child(mUserId).child(id);
            ScheduleRef.child(daySchedule.getDayName()).setValue(daySchedule.getTime()).addOnFailureListener(e -> {
                alert.dismiss();
                flag[0] = false;
            });
        }
        if (flag[0]) {
            UpdateLocalSchedule(daySchedules);
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

    }

    private void UpdateLocalSchedule(List<DaySchedule> daySchedules) {
        Completable.fromAction(() ->
                scheduleViewModel.insertDayScheduleList(daySchedules)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        alert.dismiss();
                        Intent intent = new Intent(AddNewTuition.this, BatchDetails.class);
                        intent.putExtra("Tuition_id", id);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }

                    @Override
                    public void onError(Throwable e) {
                        alert.dismiss();
                        Toast.makeText(AddNewTuition.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
        if (totalDays.isEmpty()) {
            binding.inputTotalDays.setError("enter days amount");
            return 0;
        } else {
            binding.inputTotalDays.setError(null);
        }

        completedDays = binding.inputCompletedDays.getText().toString();
        if (completedDays.isEmpty()) {
            binding.inputCompletedDays.setError("enter days amount");
            return 0;
        } else {
            binding.inputCompletedDays.setError(null);
        }


        weeklyDays = String.valueOf(daySchedules.size());
        if (weeklyDays.isEmpty() || weeklyDays.equals("0")) {
            binding.inputWeeklyDays.setError("enter weekly days");
            return 0;
        } else {
            binding.inputWeeklyDays.setError(null);
        }


        remuneration = binding.inputRemuneration.getText().toString();
        if (remuneration.isEmpty() || remuneration.length() < 2) {
            binding.inputRemuneration.setError("min 2 digit amount");
            return 0;
        } else {
            binding.inputRemuneration.setError(null);
        }

        return 1;
    }

    private void ShowClock(TextView tv) {
        Calendar myCalendar = Calendar.getInstance();
        String myTimeFormat = "hh:mm";

        SimpleDateFormat stf = new SimpleDateFormat(myTimeFormat, Locale.US);
        Calendar now = Calendar.getInstance();

        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minte = now.get(Calendar.MINUTE);

        TimePickerDialog nTime = new TimePickerDialog(this, R.style.datepicker, (view, hourOfDay, minute) -> {
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);

            tv.setText(stf.format(myCalendar.getTime()));
            int hh = myCalendar.get(Calendar.HOUR_OF_DAY) - 1;
            int mm = myCalendar.get(Calendar.MINUTE);

            String t = stf.format(hh + mm);

            SetAtime(tv, t);
        }, hour, minte, false);
        nTime.show();
    }

    private void SetAtime(TextView tv, String t) {
        int tvid = tv.getId();

        if (tvid == R.id.satTimeET) {
            satT = t;
            return;
        }
        if (tvid == R.id.sunTimeET) {
            sunT = t;
            return;
        }
        if (tvid == R.id.monTimeET) {
            monT = t;
            return;
        }
        if (tvid == R.id.tueTimeET) {
            tueT = t;
            return;
        }
        if (tvid == R.id.wedTimeET) {
            wedT = t;
            return;
        }
        if (tvid == R.id.thuTimeET) {
            thuT = t;
            return;
        }
        if (tvid == R.id.friTimeET) {
            friT = t;
        }


    }

    private void ShowLoadingDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddNewTuition.this);
        LayoutInflater inflater = AddNewTuition.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.loading_bar_dialog, null);
        dialogBuilder.setView(dialogView);
        alert = dialogBuilder.create();
        alert.setCancelable(false);
        alert.show();

    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
}