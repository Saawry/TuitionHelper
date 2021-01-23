package com.gadware.tution.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


                        holder.itemView.setOnClickListener(v -> {
                            Intent nSession = new Intent(TuitionDetails.this, SessionDetails.class);
                            nSession.putExtra("SessionId", SessionIDs);
                            startActivity(nSession);
                        });


                        }

                        @Override
                        public void onCancelled (@NonNull DatabaseError databaseError){

                        }
                    });
                }

                @NonNull
                @Override
                public SessionsViewHolder onCreateViewHolder (@NonNull ViewGroup parent,int i){
                    SessionCardBinding sbinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.session_card, parent, false);
                    SessionsViewHolder viewHolder = new SessionsViewHolder(sbinding);
                    return viewHolder;

                }
            }

            ;

        binding.sessionRecycler.setAdapter(adapter);
        adapter.startListening();
        }

        private void RetriveTuitionInfo () {
            tuitionRef = FirebaseDatabase.getInstance().getReference("Users").child(mUserId).child("TuitionList").child(cTuitionId).getRef();
            tuitionRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //update fields

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

