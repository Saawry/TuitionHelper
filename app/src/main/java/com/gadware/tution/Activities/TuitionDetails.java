package com.gadware.tution.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gadware.tution.R;
import com.gadware.tution.databinding.ActivityTuitionDetailsBinding;
import com.gadware.tution.databinding.SessionCardBinding;
import com.gadware.tution.models.DaySchedule;
import com.gadware.tution.models.SessionInfo;
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
import java.util.Objects;

public class TuitionDetails extends AppCompatActivity {
    private AlertDialog alertDialog;
    ActivityTuitionDetailsBinding binding;
    private final Calendar myCalendar = Calendar.getInstance();
    private DatabaseReference tuitionRef, sessionRef;
    private String  mUserId, cTuitionId,completedDays;
    List<DaySchedule> NewDaySchedule = new ArrayList<>();
    List<DaySchedule> Schedule= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuition_details);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_tuition_details);
        cTuitionId = getIntent().getExtras().get("Tuition_id").toString();
        mUserId= FirebaseAuth.getInstance().getUid();
        binding.sessionRecycler.setLayoutManager(new LinearLayoutManager(this));
        RetriveTuitionInfo();
        RetriveScheduleInfo();
        RetriveSessionInfoList();

        Calendar now = Calendar.getInstance();
        int yr = now.get(Calendar.YEAR);
        int mnth = now.get(Calendar.MONTH) ; // Note: result may zero based!(+1)
        int day = now.get(Calendar.DAY_OF_MONTH);
        String myDateFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myDateFormat, Locale.US);
        binding.tuitionDAdnSsn.setOnClickListener(v -> {
            Intent nSession = new Intent(TuitionDetails.this, AddNewSession.class);
            nSession.putExtra("Tuition_id", cTuitionId);
            nSession.putExtra("completedDays", completedDays);
            startActivity(nSession);
        });

        binding.tuitionDSDate.setOnClickListener(v -> {
            DatePickerDialog nDate = new DatePickerDialog(this, R.style.datepicker, (DatePickerDialog.OnDateSetListener) (view, year, month, dayOfMonth) -> {
                myCalendar.set(Calendar.YEAR, yr);
                myCalendar.set(Calendar.MONTH, mnth);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);


                binding.tuitionDSDate.setText(sdf.format(myCalendar.getTime()));
                DatabaseReference sDtRef=FirebaseDatabase.getInstance().getReference("Tuition List").child(mUserId).child(cTuitionId).child("sDate");
                sDtRef.setValue(binding.tuitionDSDate.getText().toString());
            }, yr, mnth, day);
            nDate.show();

//            AlertDialog.Builder builder = new AlertDialog.Builder(TuitionDetails.this);
//            builder.setTitle("Input Start Date");
//
//            final EditText input = new EditText(TuitionDetails.this);
//            input.setInputType(InputType.TYPE_CLASS_DATETIME);
//            builder.setView(input);
//
//            final String[] m_Text = new String[1];
//            builder.setPositiveButton("OK", (dialog, which) -> m_Text[0] = input.getText().toString());
//            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
//            builder.show();
        });







        binding.tuitionDEDate.setOnClickListener(v -> {
            DatePickerDialog nDate = new DatePickerDialog(this, R.style.datepicker, (DatePickerDialog.OnDateSetListener) (view, year, month, dayOfMonth) -> {
                myCalendar.set(Calendar.YEAR, yr);
                myCalendar.set(Calendar.MONTH, mnth);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);


                binding.tuitionDEDate.setText(sdf.format(myCalendar.getTime()));
                DatabaseReference sDtRef=FirebaseDatabase.getInstance().getReference("Tuition List").child(mUserId).child(cTuitionId).child("eDate");
                sDtRef.setValue(binding.tuitionDEDate.getText().toString());
            }, yr, mnth, day);
            nDate.show();

//            AlertDialog.Builder builder = new AlertDialog.Builder(TuitionDetails.this);
//            builder.setTitle("Input End Date");
//
//            final EditText input = new EditText(TuitionDetails.this);
//            input.setInputType(InputType.TYPE_CLASS_DATETIME);
//            builder.setView(input);
//
//            final String[] m_Text = new String[1];
//            builder.setPositiveButton("OK", (dialog, which) -> m_Text[0] = input.getText().toString());
//            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
//            builder.show();
        });

        binding.tuitionDDSpin.setOnClickListener(v -> {

            getWeeklySchedule();


//            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TuitionDetails.this);
//
//            LayoutInflater inflater = TuitionDetails.this.getLayoutInflater();
//            View dialogView = inflater.inflate(R.layout.weekly_days_select, null);
//            dialogBuilder.setView(dialogView);
//
//            EditText satEt =  dialogView.findViewById(R.id.satTimeET);
//            CheckBox satCB =  dialogView.findViewById(R.id.satcheckBox);
//
//            AlertDialog alertDialog = dialogBuilder.create();
//            alertDialog.show();
        });


    }
    private void getWeeklySchedule() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TuitionDetails.this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = TuitionDetails.this.getLayoutInflater();
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

        for (DaySchedule daySchedule:Schedule){
            if (daySchedule.getDayName().equals("SAT")){
                satCB.setChecked(true);
                satEt.setVisibility(View.VISIBLE);
                satEt.setText(daySchedule.getTime());
                continue;
            }
            if (daySchedule.getDayName().equals("SUN")){
                sunCB.setChecked(true);
                sunEt.setVisibility(View.VISIBLE);
                sunEt.setText(daySchedule.getTime());
                continue;
            }
            if (daySchedule.getDayName().equals("MON")){
                monCB.setChecked(true);
                monEt.setVisibility(View.VISIBLE);
                monEt.setText(daySchedule.getTime());
                continue;
            }
            if (daySchedule.getDayName().equals("TUE")){
                tueCB.setChecked(true);
                tueEt.setVisibility(View.VISIBLE);
                tueEt.setText(daySchedule.getTime());
                continue;
            }
            if (daySchedule.getDayName().equals("WED")){
                wedCB.setChecked(true);
                wedEt.setVisibility(View.VISIBLE);
                wedEt.setText(daySchedule.getTime());
                continue;
            }
            if (daySchedule.getDayName().equals("THU")){
                thuCB.setChecked(true);
                thuEt.setVisibility(View.VISIBLE);
                thuEt.setText(daySchedule.getTime());
                continue;
            }
            if (daySchedule.getDayName().equals("FRI")){
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
            if (satCB.isChecked()) {
                String time = satEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    NewDaySchedule.add(new DaySchedule("SAT", time));
                }
            }
            if (sunCB.isChecked()) {
                String time = sunEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    NewDaySchedule.add(new DaySchedule("SUN", time));
                }
            }
            if (monCB.isChecked()) {
                String time = monEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    NewDaySchedule.add(new DaySchedule("MON", time));
                }
            }
            if (tueCB.isChecked()) {
                String time = tueEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    NewDaySchedule.add(new DaySchedule("TUE", time));
                }
            }
            if (wedCB.isChecked()) {
                String time = wedEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    NewDaySchedule.add(new DaySchedule("WED", time));
                }
            }
            if (thuCB.isChecked()) {
                String time = thuEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    NewDaySchedule.add(new DaySchedule("THU", time));
                }
            }
            if (friCB.isChecked()) {
                String time = friEt.getText().toString();
                if (!TextUtils.isEmpty(time) && time.length() > 6) {
                    NewDaySchedule.add(new DaySchedule("FRI", time));
                }
            }
            if (NewDaySchedule.size()>0){
                AddSchedules(cTuitionId);
            }

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

    private void AddSchedules(String id) {
        binding.tuitionDDSpin.setText("");
        FirebaseDatabase.getInstance().getReference().child("Tuition List").child(mUserId).child(id).child("weeklyDays").setValue(NewDaySchedule.size());
        for (DaySchedule daySchedule:NewDaySchedule){
            DatabaseReference ScheduleRef = FirebaseDatabase.getInstance().getReference().child("Schedule List").child(id);
            ScheduleRef.child(daySchedule.getDayName()).setValue(daySchedule.getTime());

            binding.tuitionDDSpin.setText(binding.tuitionDDSpin.getText().toString()+" "+daySchedule.getDayName());
        }

    }

    private void RetriveScheduleInfo() {
        DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference("Schedule List").child(cTuitionId).getRef();
        scheduleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                binding.tuitionDDSpin.setText("");
                if (snapshot.child("SAT").exists()){
                    String Time= Objects.requireNonNull(snapshot.child("SAT").getValue()).toString();
                    Schedule.add(new DaySchedule("SAT",Time));
                    binding.tuitionDDSpin.setText(binding.tuitionDDSpin.getText()+" SAT");
                }

                if (snapshot.child("SUN").exists()){
                    String Time= Objects.requireNonNull(snapshot.child("SUN").getValue()).toString();
                    Schedule.add(new DaySchedule("SUN",Time));
                    binding.tuitionDDSpin.setText(binding.tuitionDDSpin.getText()+" SUN");
                }

                if (snapshot.child("MON").exists()){
                    String Time= Objects.requireNonNull(snapshot.child("MON").getValue()).toString();
                    Schedule.add(new DaySchedule("MON",Time));
                    binding.tuitionDDSpin.setText(binding.tuitionDDSpin.getText()+" MON");
                }

                if (snapshot.child("TUE").exists()){
                    String Time= Objects.requireNonNull(snapshot.child("TUE").getValue()).toString();
                    Schedule.add(new DaySchedule("TUE",Time));
                    binding.tuitionDDSpin.setText(binding.tuitionDDSpin.getText()+" TUE");
                }

                if (snapshot.child("WED").exists()){
                    String Time= Objects.requireNonNull(snapshot.child("WED").getValue()).toString();
                    Schedule.add(new DaySchedule("WED",Time));
                    binding.tuitionDDSpin.setText(binding.tuitionDDSpin.getText()+" WED");
                }

                if (snapshot.child("THU").exists()){
                    String Time= Objects.requireNonNull(snapshot.child("THU").getValue()).toString();
                    Schedule.add(new DaySchedule("THU",Time));
                    binding.tuitionDDSpin.setText(binding.tuitionDDSpin.getText()+" THU");
                }

                if (snapshot.child("FRI").exists()){
                    String Time= Objects.requireNonNull(snapshot.child("FRI").getValue()).toString();
                    Schedule.add(new DaySchedule("FRI",Time));
                    binding.tuitionDDSpin.setText(binding.tuitionDDSpin.getText()+" FRI");
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

                binding.tuitionDSName.setText(snapshot.child("studentName").getValue().toString());
                binding.tuitionDLocation.setText(snapshot.child("location").getValue().toString());
                binding.tuitionDMobile.setText(snapshot.child("mobile").getValue().toString());
                binding.tuitionDWD.setText("Weekly "+snapshot.child("weeklyDays").getValue().toString()+" Days");
                binding.tuitionDRemu.setText(snapshot.child("remuneration").getValue().toString());

                String td = snapshot.child("totalDays").getValue().toString();
                completedDays = snapshot.child("completedDays").getValue().toString();
                binding.tuitionDDTD.setText("Completed " + completedDays + " of " + td + " days");

                if (snapshot.hasChild("ImageUri")) {
                    Glide.with(TuitionDetails.this).load(snapshot.child("ImageUri").getValue().toString()).into(binding.tuitionDImg);
                }
                if (snapshot.hasChild("sDate")) {
                    binding.tuitionDSDate.setText(snapshot.child("sDate").getValue().toString());
                }
                if (snapshot.hasChild("eDate")) {
                    binding.tuitionDEDate.setText(snapshot.child("eDate").getValue().toString());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

        sessionRef = FirebaseDatabase.getInstance().getReference().child("Session List").child(cTuitionId);
        FirebaseRecyclerOptions<SessionInfo> options = new FirebaseRecyclerOptions.Builder<SessionInfo>().setQuery(sessionRef, SessionInfo.class).build();
        final FirebaseRecyclerAdapter<SessionInfo, SessionsViewHolder> adapter
                = new FirebaseRecyclerAdapter<SessionInfo, SessionsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SessionsViewHolder holder, int position, @NonNull SessionInfo model) {

                final String SessionIDs = getRef(position).getKey();
                sessionRef.child(SessionIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //SessionInfo sessionInfo= (SessionInfo) dataSnapshot.getValue();
                        String dt = dataSnapshot.child("date").getValue().toString();
                        String dy = dataSnapshot.child("day").getValue().toString();

                        String t = dataSnapshot.child("time").getValue().toString();
                        String et = dataSnapshot.child("eTime").getValue().toString();

                        holder.sBinding.sessionCardDD.setText(dt + "   " + dy);
                        holder.sBinding.sessionCardTT.setText(t + "  to " + et);
                        holder.sBinding.sessionCardCount.setText(dataSnapshot.child("counter").getValue().toString());
                        holder.sBinding.sessionCardTpc.setText(dataSnapshot.child("topic").getValue().toString());


//                        holder.itemView.setOnClickListener(v -> {
//                            Intent nSession = new Intent(TuitionDetails.this, SessionDetails.class);
//                            nSession.putExtra("SessionId", SessionIDs);
//                            startActivity(nSession);
//                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(TuitionDetails.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
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
}

