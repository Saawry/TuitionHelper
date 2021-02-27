package com.gadware.tution.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.gadware.tution.R;
import com.gadware.tution.databinding.ActivitySessionDetailsBinding;
import com.gadware.tution.models.SessionInfo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SessionDetails extends AppCompatActivity {
    ActivitySessionDetailsBinding binding;
    private String date, day, time, eTime, topic;
    private String tuitionId, sessionId;
    DatabaseReference sessionRef;
    SessionInfo sessionInfo;
    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_details);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final View activityRootView = findViewById(R.id.activity_root_view);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
            if (heightDiff > dpToPx(SessionDetails.this, 100)) {
                binding.adView.setVisibility(View.INVISIBLE);
            } else {
                binding.adView.setVisibility(View.VISIBLE);
            }
        });


        sessionId = getIntent().getStringExtra("sessionId");
        tuitionId = getIntent().getStringExtra("tuitionId");
        sessionRef = FirebaseDatabase.getInstance().getReference().child("Session List").child(tuitionId).child(sessionId);


        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        //adView.setAdUnitId("ca-app-pub-7098600576446460/5139643827");
        adView.setAdUnitId("ca-app-pub-7098600576446460/5139643827");

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


        sessionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sessionInfo = new SessionInfo(snapshot.child("id").getValue().toString(), snapshot.child("date").getValue().toString(), snapshot.child("day").getValue().toString(),
                        snapshot.child("aTime").getValue().toString(), snapshot.child("sTime").getValue().toString(), snapshot.child("eTime").getValue().toString(),
                        snapshot.child("topic").getValue().toString(), snapshot.child("tutor").getValue().toString(), snapshot.child("counter").getValue().toString());

                binding.inputDate.setText(sessionInfo.getDate());
                binding.inputDay.setText(sessionInfo.getDay());
                binding.inputSTime.setText(sessionInfo.getsTime());
                binding.inputETime.setText(sessionInfo.geteTime());
                binding.inputTopic.setText(sessionInfo.getTopic());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Calendar now = Calendar.getInstance();
        int yr = now.get(Calendar.YEAR);
        int mnth = now.get(Calendar.MONTH);
        int dy = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int ehour = now.get(Calendar.HOUR_OF_DAY) + 1;
        int minte = now.get(Calendar.MINUTE);

        String myTimeFormat = "hh.mm a";
        SimpleDateFormat stf = new SimpleDateFormat(myTimeFormat, Locale.US);
        String myDateFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myDateFormat, Locale.US);
        binding.inputDate.setOnClickListener(v -> {
            DatePickerDialog nDate = new DatePickerDialog(this, R.style.datepicker, (DatePickerDialog.OnDateSetListener) (view, year, month, dayOfMonth) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                binding.inputDay.setText(GetDAY(myCalendar.get(Calendar.DAY_OF_WEEK)));
                binding.inputDate.setText(sdf.format(myCalendar.getTime()));
            }, yr, mnth, dy);
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

            date = binding.inputDate.getText().toString();
            if (!date.equals(sessionInfo.getDate())) {
                UpdateValue(date, "date");
            }
            day = binding.inputDay.getText().toString();
            if (!day.equals(sessionInfo.getDay())) {
                UpdateValue(day, "day");
            }
            time = binding.inputSTime.getText().toString();
            if (!time.equals(sessionInfo.getsTime())) {
                UpdateValue(time, "time");
            }
            eTime = binding.inputETime.getText().toString();
            if (!eTime.equals(sessionInfo.geteTime())) {
                UpdateValue(eTime, "eTime");
            }
            topic = binding.inputTopic.getText().toString();
            if (!topic.equals(sessionInfo.getTopic())) {
                UpdateValue(topic, "topic");
            }
        });

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

    private void UpdateValue(String value, String key) {
        sessionRef.child(key).setValue(value).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Updated " + key, Toast.LENGTH_SHORT).show();

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Couldn't update " + key, Toast.LENGTH_SHORT).show();
        });
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
}