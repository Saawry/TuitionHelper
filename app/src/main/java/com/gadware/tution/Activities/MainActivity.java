package com.gadware.tution.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.gadware.tution.R;
import com.gadware.tution.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;


    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase fireDb;
    private DatabaseReference tuitionRef;
    private String status = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        fireDb = FirebaseDatabase.getInstance();
        VerifyUserExistence();

        binding.ProfileIcon.setOnClickListener(v -> {

                    startActivity(new Intent(MainActivity.this, UserProfile.class));
                }
        );

        binding.favAddNewTuitionBtn.setOnClickListener(v ->

                startActivity(new Intent(MainActivity.this, AddNewTuition.class))
        );

    }


    private void VerifyUserExistence() {
        if (mUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {

            String activeUserId = mUser.getUid();
            try {
                DatabaseReference ref = fireDb.getReference().child("Users").child(activeUserId).child("UserInfo").child("status");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            status = Objects.requireNonNull(snapshot.getValue()).toString();
                            if (status.equals("noImage")) {
                                Toast.makeText(MainActivity.this, "No Profile Image", Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(MainActivity.this, UserProfile.class));
//                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }
    }
}