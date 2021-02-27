package com.gadware.tution.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.security.crypto.EncryptedSharedPreferences;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.gadware.tution.R;
import com.gadware.tution.asset.DocHelper;
import com.gadware.tution.databinding.ActivityAddNewBatchBinding;
import com.gadware.tution.models.Batch;
import com.gadware.tution.models.DaySchedule;
import com.gadware.tution.models.TuitionInfo;
import com.gadware.tution.models.User;
import com.gadware.tution.viewmodel.BatchViewModel;
import com.gadware.tution.viewmodel.ScheduleViewModel;
import com.gadware.tution.viewmodel.UserViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV;
import static androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM;

public class AddNewBatch extends AppCompatActivity {

    ActivityAddNewBatchBinding binding;
    private DatabaseReference BatchRef;
    private String mUserId;
    private String id, className, subject, studentAmount, weeklyDays, remuneration;
    private String satT, sunT, monT, tueT, wedT, thuT, friT;
    private AlertDialog alertDialog, alert;
    private long BatchCounter = 0;
    private BatchViewModel batchViewModel;
    private ScheduleViewModel scheduleViewModel;
    List<DaySchedule> daySchedules = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_batch);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_batch);

        batchViewModel = new ViewModelProvider(this).get(BatchViewModel.class);
        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);

        final View activityRootView = findViewById(R.id.activity_root_view);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
            if (heightDiff > dpToPx(AddNewBatch.this, 100)) {
                binding.adView.setVisibility(View.INVISIBLE);
            } else {
                binding.adView.setVisibility(View.VISIBLE);
            }
        });

        mUserId = FirebaseAuth.getInstance().getUid();

        BatchRef = FirebaseDatabase.getInstance().getReference().child("Batch List").child(mUserId);
        id = BatchRef.push().getKey();


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


        GetBatchCounter();

        binding.inputWeeklyDays.setOnClickListener(v -> {
            getWeeklySchedule();
        });

        binding.addBatchBtnId.setOnClickListener(v -> {
            if (BatchCounter >= 4) {
                Toast.makeText(AddNewBatch.this, "Reached Maximum limit(4), delete old or contact us", Toast.LENGTH_LONG).show();
            } else if (validate() == 1) {
                InitNewBatch();
            }
        });
    }


    private void UpdateLocalDB(Batch batch) {
        Completable.fromAction(() ->
                batchViewModel.insertBatchInfo(batch)).observeOn(AndroidSchedulers.mainThread())
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
                        Toast.makeText(AddNewBatch.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void GetBatchCounter() {
        BatchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                BatchCounter = dataSnapshot.getChildrenCount();

                if (BatchCounter >= 4) {
                    Toast.makeText(AddNewBatch.this, "Reached Maximum limit(4), delete old Batch or contact us", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });

    }

    private void getWeeklySchedule() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddNewBatch.this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = AddNewBatch.this.getLayoutInflater();
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

    private void InitNewBatch() {
        ShowLoadingDialog();

        Batch batch = new Batch(id, className, subject, studentAmount, remuneration, weeklyDays);
        assert id != null;
        BatchRef.child(id).setValue(batch).addOnSuccessListener(aVoid -> {
            UpdateLocalDB(batch);
        }).addOnFailureListener(e -> {
            alert.dismiss();
            Toast.makeText(AddNewBatch.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    private void AddSchedules(String id) {
        final boolean[] ok = {true};
        for (DaySchedule daySchedule : daySchedules) {
            DatabaseReference ScheduleRef = FirebaseDatabase.getInstance().getReference().child("Schedule List").child(mUserId).child(id);
            ScheduleRef.child(daySchedule.getDayName()).setValue(daySchedule.getTime()).addOnFailureListener(e -> {
                ok[0] = false;
                alert.dismiss();
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            });
        }
        if (ok[0]) {
            UpdateLocalSchedule(daySchedules);
        } else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(AddNewBatch.this, BatchDetails.class);
                        intent.putExtra("Batch_id", id);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }

                    @Override
                    public void onError(Throwable e) {
                        alert.dismiss();
                        Toast.makeText(AddNewBatch.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public int validate() {

        className = binding.inputClass.getText().toString();
        if (className.isEmpty() || className.length() < 3) {
            binding.inputClass.setError("invalid(ex: nine)");
            return 0;
        } else {
            binding.inputClass.setError(null);
        }


        subject = binding.inputSubject.getText().toString();
        if (subject.isEmpty() || subject.length() < 3) {
            binding.inputSubject.setError("enter a valid subject");
            return 0;
        } else {
            binding.inputSubject.setError(null);
        }

        studentAmount = binding.inputStudentsAmount.getText().toString();
        if (studentAmount.isEmpty()) {
            binding.inputStudentsAmount.setError("enter student Amount");
            return 0;
        } else {
            binding.inputStudentsAmount.setError(null);
        }

        remuneration = binding.inputRemuneration.getText().toString();
        if (remuneration.isEmpty() || remuneration.length() < 3) {
            binding.inputRemuneration.setError("enter monthly salary");
            return 0;
        } else {
            binding.inputRemuneration.setError(null);
        }


        weeklyDays = String.valueOf(daySchedules.size());
        if (weeklyDays.isEmpty() || weeklyDays.equals("0")) {
            binding.inputWeeklyDays.setError("select schedule");
            return 0;
        } else {
            binding.inputWeeklyDays.setError(null);
        }

        return 1;
    }

    private void ShowClock(TextView tv) {
        Calendar myCalendar = Calendar.getInstance();
        String myTimeFormat = "hh.mm a";

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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddNewBatch.this);
        LayoutInflater inflater = AddNewBatch.this.getLayoutInflater();
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