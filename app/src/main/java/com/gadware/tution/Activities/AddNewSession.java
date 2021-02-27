package com.gadware.tution.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.gadware.tution.R;
import com.gadware.tution.databinding.ActivityAddNewSessionBinding;
import com.gadware.tution.models.Batch;
import com.gadware.tution.models.SessionInfo;
import com.gadware.tution.viewmodel.SessionViewModel;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddNewSession extends AppCompatActivity {


    ActivityAddNewSessionBinding binding;
    private AlertDialog alert;
    private DatabaseReference sessionRef;
    private final Calendar myCalendar = Calendar.getInstance();
    private String id, type, date, day, time, etime, topic;
    int counter;
    private String userId, bTId, completedDays;
    private long sessionCounter = 0;
    private SessionViewModel sessionViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_session);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_session);

        sessionViewModel = new ViewModelProvider(this).get(SessionViewModel.class);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final View activityRootView = findViewById(R.id.activity_root_view);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
            if (heightDiff > dpToPx(AddNewSession.this, 100)) {
                binding.adView.setVisibility(View.INVISIBLE);
            } else {
                binding.adView.setVisibility(View.VISIBLE);
            }
        });
        userId = FirebaseAuth.getInstance().getUid();
        bTId = getIntent().getExtras().get("bt_id").toString();
        type = getIntent().getExtras().get("type").toString();
        completedDays = getIntent().getExtras().get("completedDays").toString();
        counter = Integer.parseInt(completedDays) + 1;
        sessionRef = FirebaseDatabase.getInstance().getReference().child("Session List").child(userId).child(bTId);


        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);

        //adView.setAdUnitId("ca-app-pub-7098600576446460/1812333402");
        adView.setAdUnitId("ca-app-pub-7098600576446460/1812333402");

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


        Calendar now = Calendar.getInstance();
        int yr = now.get(Calendar.YEAR);
        int mnth = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        int today = now.get(Calendar.DAY_OF_WEEK);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int ehour = now.get(Calendar.HOUR_OF_DAY) + 1;
        int minte = now.get(Calendar.MINUTE);
        long integerRepresentation = now.getTimeInMillis();

        Calendar cend = Calendar.getInstance();
        cend.setTimeInMillis(integerRepresentation + 3600000);


        String myTimeFormat = "hh:mm";
        SimpleDateFormat stf = new SimpleDateFormat(myTimeFormat, Locale.US);
        String myDateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myDateFormat, Locale.US);


        binding.inputDate.setText(sdf.format(now.getTime()));
        binding.inputSTime.setText(stf.format(now.getTime()));
        binding.inputETime.setText(stf.format(cend.getTime()));
        binding.inputDay.setText(GetDAY(today));


        binding.inputDate.setOnClickListener(v -> {
            DatePickerDialog nDate = new DatePickerDialog(this, R.style.datepicker, (view, year, month, dayOfMonth) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                binding.inputDay.setText(GetDAY(myCalendar.get(Calendar.DAY_OF_WEEK)));
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
        GetSessionCounter();
        binding.addSessionBtnId.setOnClickListener(v -> {

            if (sessionCounter >= 20) {
                Toast.makeText(AddNewSession.this, "Reached Maximum(20), delete old student or contact us", Toast.LENGTH_LONG).show();
            } else if (validate() == 1) {
                InsertNewSession();
            }

        });

    }

    private void InsertNewSession() {
        ShowLoadingDialog();
        id = sessionRef.push().getKey();
        SessionInfo sessionInfo = new SessionInfo(id, bTId, type, date, day, time, etime, topic, userId, String.valueOf(counter));
        sessionRef.setValue(sessionInfo).addOnSuccessListener(aVoid -> {

            UpdateLocalDB(sessionInfo);

        }).addOnFailureListener(e -> {
            alert.dismiss();
            Toast.makeText(AddNewSession.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    private void UpdateLocalDB(SessionInfo sessionInfo) {
        Completable.fromAction(() ->
                sessionViewModel.insertSessionInfo(sessionInfo)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        if (type.equals("tuition")) {
                            SetCompletedCounter();
                        } else {
                            alert.dismiss();
                            Toast.makeText(AddNewSession.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddNewSession.this, BatchDetails.class);
                            intent.putExtra("Batch_id", bTId);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        alert.dismiss();
                        Toast.makeText(AddNewSession.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void SetCompletedCounter() {

        DatabaseReference cntRef = FirebaseDatabase.getInstance().getReference("Tuition List").child(userId).child(bTId).getRef();
        cntRef.child("completedDays").setValue(String.valueOf(counter)).addOnSuccessListener(aVoid -> {
            alert.dismiss();
            Toast.makeText(AddNewSession.this, "Added Successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddNewSession.this, TuitionDetails.class);
            intent.putExtra("Tuition_id", bTId);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }).addOnFailureListener(e -> {
            alert.dismiss();
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    private int validate() {
        date = binding.inputDate.getText().toString();
        if (date.isEmpty() || date.length() < 8) {
            binding.inputDate.setError("enter valid date");
            return 0;
        }

        time = binding.inputSTime.getText().toString();
        if (time.isEmpty() || time.length() < 4) {
            binding.inputSTime.setError("enter valid time");
            return 0;
        }

        etime = binding.inputETime.getText().toString();
        if (etime.isEmpty() || etime.length() < 4) {
            binding.inputETime.setError("enter valid time");
            return 0;
        }

        day = binding.inputDay.getText().toString();
        if (day.isEmpty() || day.length() < 3) {
            binding.inputDay.setError("enter valid day");
            return 0;
        }

        topic = binding.inputTopic.getText().toString();
        if (topic.isEmpty() || topic.length() < 3) {
            binding.inputTopic.setError("min length 3");
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

    private void ShowLoadingDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddNewSession.this);
        LayoutInflater inflater = AddNewSession.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.loading_bar_dialog, null);
        dialogBuilder.setView(dialogView);
        alert = dialogBuilder.create();
        alert.setCancelable(false);
        alert.show();

    }

    private void GetSessionCounter() {
        sessionRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                sessionCounter = dataSnapshot.getChildrenCount();

                if (sessionCounter >= 20) {
                    Toast.makeText(AddNewSession.this, "Reached Maximum limit(20), delete old session or contact us", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
}