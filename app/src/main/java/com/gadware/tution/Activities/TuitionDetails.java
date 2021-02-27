package com.gadware.tution.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gadware.tution.R;
import com.gadware.tution.asset.ImageHelper;
import com.gadware.tution.databinding.ActivityTuitionDetailsBinding;
import com.gadware.tution.databinding.SessionCardBinding;
import com.gadware.tution.models.DaySchedule;
import com.gadware.tution.models.ImageDetails;
import com.gadware.tution.models.SessionInfo;
import com.gadware.tution.viewmodel.ImageViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TuitionDetails extends AppCompatActivity {
    private AlertDialog alertDialog;
    private AlertDialog alertDialogx;
    private AlertDialog alert;

    private ActivityTuitionDetailsBinding binding;


    StorageReference Storageref;

    private static final int PERMISSION_ALL = 222;
    private static final String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private static final int PICK_IMAGE = 100;

    private final Calendar myCalendar = Calendar.getInstance();
    private DatabaseReference tuitionRef;
    private DatabaseReference sessionRef;
    private DatabaseReference tuitionInfoRef;
    private String mUserId, cTuitionId, completedDays, totalDays, mobile;
    private ImageViewModel imageViewModel;
    List<DaySchedule> Schedule = new ArrayList<>();
    List<DaySchedule> NewDaySchedule = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuition_details);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_tuition_details);
        cTuitionId = getIntent().getExtras().get("Tuition_id").toString();
        mUserId = FirebaseAuth.getInstance().getUid();
        binding.sessionRecycler.setLayoutManager(new LinearLayoutManager(this));
        imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);

        ShowLoadingDialog();


        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-7098600576446460/4992449955");

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

        Storageref = FirebaseStorage.getInstance().getReference("Images").child(cTuitionId + ".jpg");
        tuitionInfoRef = FirebaseDatabase.getInstance().getReference("Tuition List").child(mUserId).child(cTuitionId);
        sessionRef = FirebaseDatabase.getInstance().getReference("Session List").child(mUserId).child(cTuitionId);

        RetriveTuitionInfo();
        RetriveScheduleInfo();
        RetriveSessionInfoList();
        RetriveImage();
        Calendar now = Calendar.getInstance();
        int yr = now.get(Calendar.YEAR);
        int mnth = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        String myDateFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myDateFormat, Locale.US);
        binding.tuitionDAdnSsn.setOnClickListener(v -> {
            Intent nSession = new Intent(TuitionDetails.this, AddNewSession.class);
            nSession.putExtra("bt_id", cTuitionId);
            nSession.putExtra("type", "tuition");
            nSession.putExtra("completedDays", completedDays);
            startActivity(nSession);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });

        binding.tuitionDDTD.setOnClickListener(v -> {
            GetValueAndUpdate("completedDays", completedDays, "totalDays", totalDays);
        });

        binding.tuitionDMobile.setOnClickListener(v -> {

            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse("tel:" + mobile));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(TuitionDetails.this,
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(i);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 99);
                }
            }
            startActivity(i);

        });


        binding.tuitionDImg.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(PERMISSIONS, PERMISSION_ALL);
                } else {
                    openGallery();
                }
            } else {
                openGallery();
            }
        });


        binding.deleteTuitionBtn.setOnClickListener(v -> {
            DeleteTuition();
        });


        binding.tuitionDSDate.setOnClickListener(v -> {
            DatePickerDialog nDate = new DatePickerDialog(this, R.style.datepicker, (view, year, month, dayOfMonth) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                binding.tuitionDSDate.setText(sdf.format(myCalendar.getTime()));
                DatabaseReference sDtRef = FirebaseDatabase.getInstance().getReference("Tuition List").child(mUserId).child(cTuitionId).child("sDate");
                sDtRef.setValue(binding.tuitionDSDate.getText().toString());
            }, yr, mnth, day);
            nDate.show();
        });


        binding.tuitionDEDate.setOnClickListener(v -> {
            DatePickerDialog nDate = new DatePickerDialog(this, R.style.datepicker, (view, year, month, dayOfMonth) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                binding.tuitionDEDate.setText(sdf.format(myCalendar.getTime()));
                DatabaseReference sDtRef = FirebaseDatabase.getInstance().getReference("Tuition List").child(mUserId).child(cTuitionId).child("eDate");
                sDtRef.setValue(binding.tuitionDEDate.getText().toString());
            }, yr, mnth, day);
            nDate.show();

        });

        binding.tuitionDDSpin.setOnClickListener(v -> {
            RetriveScheduleInfo();
            getWeeklySchedule();
        });


    }

    private void DeleteTuition() {
        ShowLoadingDialog();
        final StorageReference ref = Storageref.child(cTuitionId + ".jpg");
        ref.delete().addOnSuccessListener(aVoid -> {
            sessionRef.removeValue().addOnSuccessListener(aVoid1 -> {
                tuitionInfoRef.removeValue().addOnSuccessListener(aVoid2 -> {
                    alertDialog.dismiss();
                    Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }).addOnFailureListener(e -> {
                    alertDialog.dismiss();
                    Toast.makeText(this, "Couldn't Delete", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                alertDialog.dismiss();
                Toast.makeText(this, "Couldn't Delete", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            alertDialog.dismiss();
            Toast.makeText(this, "Couldn't Delete", Toast.LENGTH_SHORT).show();
        });

    }

    private void getWeeklySchedule() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TuitionDetails.this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = TuitionDetails.this.getLayoutInflater();
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

        for (DaySchedule daySchedule : Schedule) {
            if (daySchedule.getDayName().equals("SAT")) {
                satCB.setChecked(true);
                satEt.setVisibility(View.VISIBLE);
                satEt.setText(daySchedule.getTime());
                continue;
            }
            if (daySchedule.getDayName().equals("SUN")) {
                sunCB.setChecked(true);
                sunEt.setVisibility(View.VISIBLE);
                sunEt.setText(daySchedule.getTime());
                continue;
            }
            if (daySchedule.getDayName().equals("MON")) {
                monCB.setChecked(true);
                monEt.setVisibility(View.VISIBLE);
                monEt.setText(daySchedule.getTime());
                continue;
            }
            if (daySchedule.getDayName().equals("TUE")) {
                tueCB.setChecked(true);
                tueEt.setVisibility(View.VISIBLE);
                tueEt.setText(daySchedule.getTime());
                continue;
            }
            if (daySchedule.getDayName().equals("WED")) {
                wedCB.setChecked(true);
                wedEt.setVisibility(View.VISIBLE);
                wedEt.setText(daySchedule.getTime());
                continue;
            }
            if (daySchedule.getDayName().equals("THU")) {
                thuCB.setChecked(true);
                thuEt.setVisibility(View.VISIBLE);
                thuEt.setText(daySchedule.getTime());
                continue;
            }
            if (daySchedule.getDayName().equals("FRI")) {
                friCB.setChecked(true);
                friEt.setVisibility(View.VISIBLE);
                friEt.setText(daySchedule.getTime());
            }
        }

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
            NewDaySchedule.clear();
            if (satCB.isChecked()) {
                String time = satEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 5) {
                    NewDaySchedule.add(new DaySchedule(cTuitionId,"SAT", time,"tuition"));
                }
            }
            if (sunCB.isChecked()) {
                String time = sunEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 5) {
                    NewDaySchedule.add(new DaySchedule(cTuitionId,"SUN", time,"tuition"));
                }
            }
            if (monCB.isChecked()) {
                String time = monEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 5) {
                    NewDaySchedule.add(new DaySchedule(cTuitionId,"MON", time,"tuition"));
                }
            }
            if (tueCB.isChecked()) {
                String time = tueEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 5) {
                    NewDaySchedule.add(new DaySchedule(cTuitionId,"TUE", time,"tuition"));
                }
            }
            if (wedCB.isChecked()) {
                String time = wedEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 5) {
                    NewDaySchedule.add(new DaySchedule(cTuitionId,"WED", time,"tuition"));
                }
            }
            if (thuCB.isChecked()) {
                String time = thuEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 5) {
                    NewDaySchedule.add(new DaySchedule(cTuitionId,"THU", time,"tuition"));
                }
            }
            if (friCB.isChecked()) {
                String time = friEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 5) {
                    NewDaySchedule.add(new DaySchedule(cTuitionId,"FRI", time,"tuition"));
                }
            }
            if (NewDaySchedule.size() > 0) {
                AddSchedules(cTuitionId);
            }

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

    private void AddSchedules(String id) {
        binding.tuitionDDSpin.setText("");
        FirebaseDatabase.getInstance().getReference().child("Tuition List").child(mUserId).child(id).child("weeklyDays").setValue(String.valueOf(NewDaySchedule.size()));
        FirebaseDatabase.getInstance().getReference().child("Schedule List").child(mUserId).child(id).removeValue();
        for (DaySchedule daySchedule : NewDaySchedule) {
            binding.tuitionDDSpin.setText(binding.tuitionDDSpin.getText() + " " + daySchedule.getDayName());
            DatabaseReference ScheduleRef = FirebaseDatabase.getInstance().getReference().child("Schedule List").child(mUserId).child(id);
            ScheduleRef.child(daySchedule.getDayName()).setValue(daySchedule.getTime());
        }

    }

    private void RetriveScheduleInfo() {
        DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference("Schedule List").child(mUserId).child(cTuitionId).getRef();
        scheduleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Schedule.clear();
                binding.tuitionDDSpin.setText("");
                if (snapshot.child("SAT").exists()) {
                    String Time = Objects.requireNonNull(snapshot.child("SAT").getValue()).toString();
                    Schedule.add(new DaySchedule(cTuitionId,"SAT", Time,"tuition"));
                    binding.tuitionDDSpin.setText(binding.tuitionDDSpin.getText() + " SAT");
                }

                if (snapshot.child("SUN").exists()) {
                    String Time = Objects.requireNonNull(snapshot.child("SUN").getValue()).toString();
                    Schedule.add(new DaySchedule(cTuitionId,"SUN", Time,"tuition"));
                    binding.tuitionDDSpin.setText(binding.tuitionDDSpin.getText() + " SUN");
                }

                if (snapshot.child("MON").exists()) {
                    String Time = Objects.requireNonNull(snapshot.child("MON").getValue()).toString();
                    Schedule.add(new DaySchedule(cTuitionId,"MON", Time,"tuition"));
                    binding.tuitionDDSpin.setText(binding.tuitionDDSpin.getText() + " MON");
                }

                if (snapshot.child("TUE").exists()) {
                    String Time = Objects.requireNonNull(snapshot.child("TUE").getValue()).toString();
                    Schedule.add(new DaySchedule(cTuitionId,"TUE", Time,"tuition"));
                    binding.tuitionDDSpin.setText(binding.tuitionDDSpin.getText() + " TUE");
                }

                if (snapshot.child("WED").exists()) {
                    String Time = Objects.requireNonNull(snapshot.child("WED").getValue()).toString();
                    Schedule.add(new DaySchedule(cTuitionId,"WED", Time,"tuition"));
                    binding.tuitionDDSpin.setText(binding.tuitionDDSpin.getText() + " WED");
                }

                if (snapshot.child("THU").exists()) {
                    String Time = Objects.requireNonNull(snapshot.child("THU").getValue()).toString();
                    Schedule.add(new DaySchedule(cTuitionId,"THU", Time,"tuition"));
                    binding.tuitionDDSpin.setText(binding.tuitionDDSpin.getText() + " THU");
                }

                if (snapshot.child("FRI").exists()) {
                    String Time = Objects.requireNonNull(snapshot.child("FRI").getValue()).toString();
                    Schedule.add(new DaySchedule(cTuitionId,"FRI", Time ,"tuition"));
                    binding.tuitionDDSpin.setText(binding.tuitionDDSpin.getText() + " FRI");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void RetriveTuitionInfo() {


        tuitionRef = FirebaseDatabase.getInstance().getReference("Tuition List").child(mUserId).child(cTuitionId).getRef();
        tuitionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alertDialog.dismiss();
                binding.tuitionDSName.setText(snapshot.child("studentName").getValue().toString());
                binding.tuitionDLocation.setText(snapshot.child("location").getValue().toString());
                binding.tuitionDMobile.setText(snapshot.child("mobile").getValue().toString());
                mobile = snapshot.child("mobile").getValue().toString();
                binding.tuitionDWD.setText("Weekly " + snapshot.child("weeklyDays").getValue().toString() + " Days");
                binding.tuitionDRemu.setText(snapshot.child("remuneration").getValue().toString() + " BDT");

                totalDays = snapshot.child("totalDays").getValue().toString();
                completedDays = snapshot.child("completedDays").getValue().toString();
                binding.tuitionDDTD.setText("Completed " + completedDays + " of " + totalDays + " days");

                if (snapshot.hasChild("sDate")) {
                    binding.tuitionDSDate.setText(snapshot.child("sDate").getValue().toString());
                }
                if (snapshot.hasChild("eDate")) {
                    binding.tuitionDEDate.setText(snapshot.child("eDate").getValue().toString());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                alertDialog.dismiss();
            }
        });


    }

    public static class SessionsViewHolder extends RecyclerView.ViewHolder {
        SessionCardBinding sBinding;

        public SessionsViewHolder(@NonNull SessionCardBinding sBinding) {
            super(sBinding.getRoot());
            this.sBinding = sBinding;

        }
    }

    private void RetriveSessionInfoList() {
        sessionRef = FirebaseDatabase.getInstance().getReference("Session List").child(mUserId).child(cTuitionId);

        FirebaseRecyclerOptions<SessionInfo> options = new FirebaseRecyclerOptions.Builder<SessionInfo>().setQuery(sessionRef, SessionInfo.class).build();
        final FirebaseRecyclerAdapter<SessionInfo, SessionsViewHolder> adapter = new FirebaseRecyclerAdapter<SessionInfo, SessionsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SessionsViewHolder holder, int position, @NonNull SessionInfo model) {

                final String SessionIDs = getRef(position).getKey();
                assert SessionIDs != null;
                sessionRef.child(SessionIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        alertDialog.dismiss();
                        if (dataSnapshot.hasChildren()) {
                            binding.noDataLayout.setVisibility(View.GONE);
                            String dt = dataSnapshot.child("date").getValue().toString();
                            String dy = dataSnapshot.child("day").getValue().toString();

                            String t = dataSnapshot.child("time").getValue().toString();
                            String et = dataSnapshot.child("eTime").getValue().toString();

                            holder.sBinding.sessionCardDD.setText(dt + "   " + dy);
                            holder.sBinding.sessionCardTT.setText(t + "  to " + et);
                            holder.sBinding.sessionCardCount.setText("Session No. " + dataSnapshot.child("counter").getValue().toString());
                            holder.sBinding.sessionCardTpc.setText("Topic: " + dataSnapshot.child("topic").getValue().toString());


                            holder.itemView.setOnClickListener(v -> {
                                Intent intent = new Intent(TuitionDetails.this, SessionDetails.class);
                                intent.putExtra("sessionId", SessionIDs);
                                intent.putExtra("tuitionId", cTuitionId);
                                startActivity(intent);
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            });

                            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                                @Override
                                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                                    return false;
                                }

                                @Override
                                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                                    ShowDeleteDialog(SessionIDs);
                                }
                            }).attachToRecyclerView(binding.sessionRecycler);

                        }else{
                            binding.noDataLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        alertDialog.dismiss();
                        Toast.makeText(TuitionDetails.this, "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public SessionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                SessionCardBinding sbinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.session_card, parent, false);
                SessionsViewHolder viewHolder = new SessionsViewHolder(sbinding);
                return viewHolder;

            }
        };

        binding.sessionRecycler.setAdapter(adapter);
        adapter.startListening();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "permission Denied...!!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @SuppressLint("IntentReset")
    private void openGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        @SuppressLint("IntentReset") Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            //imageUri = data.getData();
            Bitmap bitmap = ImageHelper.getImageFromResult(this, RESULT_OK, data);

            Bitmap bitmapx = ImageHelper.generateThumb(bitmap, 4500);
            binding.tuitionDImg.setImageBitmap(bitmapx);

            alertDialog.show();
            byte[] bytes = ImageHelper.toByteArray(bitmapx);
            UploadImage(bytes);

        }
    }

    private void RetriveImage() {
        final long ONE_MEGABYTE = 1024 * 1024;
        try {
            Storageref.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                byte[] bytes1 = bytes;
                Bitmap bitmap = ImageHelper.toBitmap(bytes1);
                binding.tuitionDImg.setImageBitmap(bitmap);
            }).addOnFailureListener(exception -> {
                //Toast.makeText(this, "No Image", Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {

        }


    }

    private void UploadImage(byte[] imageUri) {

        final StorageReference ref = Storageref;
        UploadTask uploadTask = ref.putBytes(imageUri);

        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return ref.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UpdateLocalDB(imageUri);
//                Uri downloadUri = task.getResult();
//                assert downloadUri != null;
//                alertDialog.dismiss();
            }
        }).addOnFailureListener(e -> alertDialog.dismiss());
    }

    private void ShowLoadingDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TuitionDetails.this);
        LayoutInflater inflater = TuitionDetails.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.loading_bar_dialog, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            alertDialog.dismiss();
        }, 3000);
    }

    private void ShowDeleteDialog(String SessionIDs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TuitionDetails.this, R.style.AppTheme_Dark_Dialog);
        builder.setMessage("Want to delete this session ?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    sessionRef.child(SessionIDs).removeValue();
                    Toast.makeText(TuitionDetails.this, "Note deleted", Toast.LENGTH_SHORT).show();
                    alert.dismiss();
                })
                .setNegativeButton("No", (dialog, id) -> {
                    alert.dismiss();
                    recreate();
                });

        alert = builder.create();
        alert.setTitle("Delete Session");
        alert.show();
    }


    private void GetValueAndUpdate(String key, String value, String key2, String value2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_two_value, null);
        dialogBuilder.setView(dialogView);

        EditText editText = dialogView.findViewById(R.id.dialog_input);
        EditText editText2 = dialogView.findViewById(R.id.dialog_input2);
        Button cancelBtn = dialogView.findViewById(R.id.dialog_cancel_btn);
        Button updateBtn = dialogView.findViewById(R.id.dialog_update_btn);
        editText.setText(value);
        editText2.setText(value2);

        cancelBtn.setOnClickListener(v ->
                alertDialogx.dismiss()
        );

        updateBtn.setOnClickListener(v -> {
            String newValue = editText.getText().toString();
            String newValue2 = editText2.getText().toString();
            if (!newValue.equals(value)) {
                if (newValue.isEmpty()) {
                    editText.setError("Enter valid number");
                } else {
                    tuitionInfoRef.child(key).setValue(newValue).addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                        alertDialogx.dismiss();
                    }).addOnFailureListener(e -> {
                                Toast.makeText(TuitionDetails.this, "Couldn't update", Toast.LENGTH_SHORT).show();
                                alertDialogx.dismiss();
                            }
                    );
                }
            } else if (!newValue2.equals(value2)) {
                if (newValue2.isEmpty()) {
                    editText.setError("Enter valid number");
                } else {
                    tuitionInfoRef.child(key2).setValue(newValue2).addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                        alertDialogx.dismiss();
                    }).addOnFailureListener(e -> {
                                Toast.makeText(TuitionDetails.this, "Couldn't update", Toast.LENGTH_SHORT).show();
                                alertDialogx.dismiss();
                            }
                    );
                }
            } else {
                alertDialogx.dismiss();
            }


        });
        alertDialogx = dialogBuilder.create();
        alertDialogx.show();
    }

    private void ShowClock(TextView editText) {
        Calendar myCalendar = Calendar.getInstance();
        String myTimeFormat = "hh.mm a";

        SimpleDateFormat stf = new SimpleDateFormat(myTimeFormat, Locale.US);
        Calendar now = Calendar.getInstance();

        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minte = now.get(Calendar.MINUTE);

        TimePickerDialog nTime = new TimePickerDialog(this, R.style.datepicker, (view, hourOfDay, minute) -> {
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);


            editText.setText(stf.format(myCalendar.getTime()));
        }, hour, minte, false);
        nTime.show();
    }
    private void UpdateLocalDB(byte[] imageBytes) {
        Completable.fromAction(() ->
                imageViewModel.insertSingleImage(new ImageDetails(cTuitionId, "tuition", imageBytes))).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        alertDialog.dismiss();
                        Toast.makeText(TuitionDetails.this, "Successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        alert.dismiss();
                        Toast.makeText(TuitionDetails.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}

