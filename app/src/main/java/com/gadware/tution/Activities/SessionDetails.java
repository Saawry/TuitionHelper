package com.gadware.tution.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.widget.Toast;

import com.gadware.tution.R;
import com.gadware.tution.databinding.ActivitySessionDetailsBinding;
import com.gadware.tution.models.SessionInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SessionDetails extends AppCompatActivity {
    ActivitySessionDetailsBinding binding;
    private String id, date, day, time, eTime, topic, counter;
    private String tuitionId,sessionId;
    DatabaseReference sessionRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_session_details);
        sessionId=getIntent().getStringExtra("sessionId");
        tuitionId=getIntent().getStringExtra("tuitionId");
        sessionRef = FirebaseDatabase.getInstance().getReference().child("Session List").child(tuitionId).child(sessionId);

        sessionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    SessionInfo sessionInfo= (SessionInfo) snapshot.getValue();
                    Toast.makeText(SessionDetails.this, sessionInfo.getTopic(), Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(SessionDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}