package com.gadware.tution.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gadware.tution.R;
import com.gadware.tution.databinding.ActivityTuitionDetailsBinding;
import com.gadware.tution.databinding.SessionCardBinding;
import com.gadware.tution.databinding.TuitionCardBinding;
import com.gadware.tution.models.SessionInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TuitionDetails extends AppCompatActivity {

    ActivityTuitionDetailsBinding binding;
    private FirebaseDatabase fireDb;
    private DatabaseReference tuitionRef, sessionRef;
    private String status = "", mUserId, cTuitionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuition_details);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_tuition_details);
        cTuitionId = getIntent().getExtras().get("Tuition_id").toString();

        RetriveTuitionInfo();


        binding.tuitionDSDate.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TuitionDetails.this);
            builder.setTitle("Input Start Date");

            final EditText input = new EditText(TuitionDetails.this);
            input.setInputType(InputType.TYPE_CLASS_DATETIME );
            builder.setView(input);

            final String[] m_Text = new String[1];
            builder.setPositiveButton("OK", (dialog, which) ->  m_Text[0] = input.getText().toString());
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });
        binding.tuitionDEDate.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TuitionDetails.this);
            builder.setTitle("Input End Date");

            final EditText input = new EditText(TuitionDetails.this);
            input.setInputType(InputType.TYPE_CLASS_DATETIME);
            builder.setView(input);

            final String[] m_Text = new String[1];
            builder.setPositiveButton("OK", (dialog, which) ->  m_Text[0] = input.getText().toString());
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        binding.tuitionDDSpin.setOnClickListener((View.OnClickListener) v -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TuitionDetails.this);
// ...Irrelevant code for customizing the buttons and title
            LayoutInflater inflater = TuitionDetails.this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.weekly_days_select, null);
            dialogBuilder.setView(dialogView);

            EditText satEt = (EditText) dialogView.findViewById(R.id.satTimeET);
            CheckBox satCB = (CheckBox) dialogView.findViewById(R.id.satcheckBox);

            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        });

        sessionRef = FirebaseDatabase.getInstance().getReference().child("Session List").child(cTuitionId);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<SessionInfo>().setQuery(sessionRef, SessionInfo.class).build();
        final FirebaseRecyclerAdapter<SessionInfo, SessionsViewHolder> adapter
                = new FirebaseRecyclerAdapter<SessionInfo, SessionsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SessionsViewHolder holder, int position, @NonNull SessionInfo model) {

                final String SessionIDs = getRef(position).getKey();

                sessionRef.child(SessionIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //SessionInfo sessionInfo= (SessionInfo) dataSnapshot.getValue();
                        String dt=dataSnapshot.child("date").getValue().toString();
                        String dy=dataSnapshot.child("day").getValue().toString();

                        String t=dataSnapshot.child("time").getValue().toString();
                        String et=dataSnapshot.child("eTime").getValue().toString();

                        holder.sBinding.sessionCardDD.setText(dt+" "+dy);
                        holder.sBinding.sessionCardTT.setText(t+"  to "+et);
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

    private void RetriveTuitionInfo() {
        tuitionRef = FirebaseDatabase.getInstance().getReference("Tuition List").child(mUserId).child(cTuitionId).getRef();
        tuitionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                binding.tuitionDSName.setText(snapshot.child("studentName").getValue().toString());
                binding.tuitionDLocation.setText(snapshot.child("location").getValue().toString());
                binding.tuitionDMobile.setText(snapshot.child("mobile").getValue().toString());
                binding.tuitionDWD.setText(snapshot.child("weeklyDays").getValue().toString());
                binding.tuitionDRemu.setText(snapshot.child("remuneration").getValue().toString());

                String td=snapshot.child("totalDays").getValue().toString();
                String cd=snapshot.child("completedDays").getValue().toString();
                binding.tuitionDDTD.setText("Completed "+cd+" of "+td+" days");

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
}

